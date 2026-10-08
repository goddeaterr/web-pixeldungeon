# MOD (taiga town): characters and items of the taiga village, drawn as pixel grids.
# Used by mod/taiga_art.py. Sheet layout (TaigaMobSprite): 16x16 frames, one row per character,
# frames 0-1 idle, 2-5 run, 6-8 attack, 9-11 death.

from PIL import Image


def rgb(h, a=255):
	return ((h >> 16) & 255, (h >> 8) & 255, h & 255, a)


def grid(rows, pal, w=16, h=16, ox=0, oy=0):
	img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
	for y, row in enumerate(rows):
		for x, ch in enumerate(row):
			if ch in ('.', ' '):
				continue
			if ch not in pal:
				raise KeyError('no colour for %r' % ch)
			xx, yy = x + ox, y + oy
			if 0 <= xx < w and 0 <= yy < h:
				img.putpixel((xx, yy), pal[ch])
	return img


def shift(img, dx, dy):
	out = Image.new('RGBA', img.size, (0, 0, 0, 0))
	out.paste(img, (dx, dy), img)
	return out


def squash(img, fy, fx=1.0, darken=0.0):
	box = img.getbbox()
	if not box:
		return img
	part = img.crop(box)
	w = max(1, int(round(part.width * fx)))
	h = max(1, int(round(part.height * fy)))
	part = part.resize((w, h), Image.NEAREST)
	if darken:
		px = part.load()
		for y in range(h):
			for x in range(w):
				r, g, b, a = px[x, y]
				px[x, y] = (int(r * (1 - darken)), int(g * (1 - darken)), int(b * (1 - darken)), a)
	out = Image.new('RGBA', img.size, (0, 0, 0, 0))
	cx = (box[0] + box[2]) // 2
	out.paste(part, (max(0, min(16 - w, cx - w // 2)), box[3] - h), part)
	return out


def frames_row(base, legs=None, idle2=None, attack=None, lean=1):
	"""base: rows of the body without legs; legs: dict of leg rows (stand, a, pass, b) added under it."""
	def body(legrows, bob=0, extra=None):
		rows = list(base) + list(legrows)
		img = grid(rows, PAL_CUR)
		if bob:
			top = grid(base, PAL_CUR)
			lower = grid([''] * len(base) + list(legrows), PAL_CUR)
			img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
			img.alpha_composite(lower)
			img.alpha_composite(shift(top, 0, bob))
		if extra is not None:
			img.alpha_composite(extra)
		return img

	stand = legs['stand']
	f = []
	f.append(body(stand))
	f.append(grid(list(idle2) + list(stand), PAL_CUR) if idle2 else shift(body(stand), 0, 0))
	f.append(body(legs['a']))
	f.append(body(legs['pass'], bob=-1))
	f.append(body(legs['b']))
	f.append(body(legs['pass'], bob=-1))
	atk = grid(list(attack) + list(stand), PAL_CUR) if attack else body(stand)
	f.append(shift(body(stand), lean, 0))
	f.append(shift(atk, lean + 1, 0))
	f.append(shift(body(stand), lean, 0))
	d0 = body(stand)
	f.append(shift(squash(d0, 0.85), 0, 0))
	f.append(squash(d0, 0.55, 1.15, 0.1))
	f.append(squash(d0, 0.3, 1.25, 0.25))
	return f


PAL_CUR = {}


def sheet(rows_of_frames):
	img = Image.new('RGBA', (16 * 12, 16 * len(rows_of_frames)), (0, 0, 0, 0))
	for r, frames in enumerate(rows_of_frames):
		for i, fr in enumerate(frames):
			img.alpha_composite(fr, (i * 16, r * 16))
	return img


# ------------------------------------------------------------------ people

SKIN = {'S': rgb(0xf2c9a0), 's': rgb(0xcf9b74), 'e': rgb(0x2a1e1e), 'n': rgb(0xe0a882)}

HUMAN_LEGS = {
	'stand': ["....dPPddPPd....",
			  "....KKK..KKK...."],
	'a':     ["...dPPd..dPPd...",
			  "..KKK......KKK.."],
	'pass':  [".....dPPPPd.....",
			  ".....KKKKKK....."],
	'b':     ["....dPPd.dPPd...",
			  "...KKK....KKK..."],
}

VILLAGER_BASE = [
	"................",
	".....hhhhhh.....",
	"....hHHHHHHh....",
	"...hHHHHHHHHh...",
	"...FFFFFFFFFF...",
	"...sSSSSSSeSS...",
	"...sSSSSSSSSn...",
	"....sSSSSSSs....",
	"...dCCFFFFCCd...",
	"..dCCCCCCCCCCd..",
	"..MdCCCCBCCCdM..",
	"...dCCCCBCCCd...",
	"...dCCCCCCCCd...",
	"....dCCCCCCd....",
]
VILLAGER_BLINK = list(VILLAGER_BASE)
VILLAGER_BLINK[5] = "...sSSSSSSsSS..."
VILLAGER_ATTACK = list(VILLAGER_BASE)
VILLAGER_ATTACK[9] = "..dCCCCCCCCCCdMM"
VILLAGER_ATTACK[10] = "..MdCCCCBCCCd..."

VILLAGER_LOOKS = [
	# hat, hat dark, coat, coat dark, mittens, trousers
	(0x7a5230, 0x523520, 0x9c3b32, 0x6a2620, 0xd9b04a, 0x3d4250),
	(0x5e5a66, 0x3e3b45, 0x3f6e8c, 0x2a4a60, 0xc94a3c, 0x4a3b30),
	(0xc9c3b8, 0x9a948a, 0x5c7a3a, 0x3d5226, 0x7a4fa0, 0x3d4250),
	(0x8a4a2a, 0x5e301a, 0xb08a4a, 0x7a5e30, 0x3f6e8c, 0x2e2a2a),
]


def villager(look):
	global PAL_CUR
	hat, hatd, coat, coatd, mit, trou = VILLAGER_LOOKS[look]
	PAL_CUR = dict(SKIN)
	PAL_CUR.update({'H': rgb(hat), 'h': rgb(hatd), 'F': rgb(0xece6dc), 'C': rgb(coat), 'd': rgb(coatd),
					'B': rgb(0x3a2a1a), 'M': rgb(mit), 'P': rgb(trou), 'K': rgb(0x3b2a20)})
	legs = {k: [r.replace('d', 'q') for r in v] for k, v in HUMAN_LEGS.items()}
	PAL_CUR['q'] = rgb(max(0, trou - 0x101010))
	return frames_row(VILLAGER_BASE, legs, VILLAGER_BLINK, VILLAGER_ATTACK)


FUR_TRADER = [
	"....hhhhhhh.....",
	"...hHHHHHHHh....",
	"..hHHHHHHHHHh...",
	"..hHHHHHHHHHh...",
	"...FFFFFFFFF....",
	"...sSSSSSSeS....",
	"...bbSSSSSSnb...",
	"...bbbbbbbbbb...",
	"..FbbbbbbbbbbF..",
	".dCFbbbbbbbbFCd.",
	".dCCFFFFFFFFCCd.",
	".MCCCCCBCCCCCCM.",
	".dCCCCCBCCCCCd..",
	"..dCCCCCCCCCCd..",
]
FUR_TRADER_BLINK = list(FUR_TRADER)
FUR_TRADER_BLINK[5] = "...sSSSSSSsS...."


def fur_trader():
	global PAL_CUR
	PAL_CUR = dict(SKIN)
	PAL_CUR.update({'H': rgb(0x4a3020), 'h': rgb(0x30200f), 'F': rgb(0xe8e0d0), 'b': rgb(0x6b4a2a),
					'C': rgb(0x8a6040), 'd': rgb(0x5a3e28), 'B': rgb(0x2a1c10), 'M': rgb(0x4a3020),
					'P': rgb(0x3d3a40), 'K': rgb(0x2b1e16), 'q': rgb(0x2d2a30)})
	legs = {k: [r.replace('d', 'q') for r in v] for k, v in HUMAN_LEGS.items()}
	return frames_row(FUR_TRADER, legs, FUR_TRADER_BLINK)


HERBALIST = [
	"................",
	".....kkkkkk.....",
	"....kKKKKKKk....",
	"...kKKKKKKKKk...",
	"...kKgSSSSSKk...",
	"...kgSSSSSeSk...",
	"...kgSSSSSSSn...",
	"....kSSsSSSk....",
	"...wwWWWWWWww...",
	"..wWWWWWWWWWWw..",
	"..SwWWWWWWWWwG..",
	"...rRRRRRRRRrG..",
	"...rRRRRRRRRr...",
	"...rRRRRRRRRr...",
]
HERBALIST_BLINK = list(HERBALIST)
HERBALIST_BLINK[5] = "...kgSSSSSsSk..."
HERBALIST_LEGS = {
	'stand': ["...rRRRRRRRRr...",
			  "....KKK..KKK...."],
	'a':     ["...rRRRRRRRRr...",
			  "...KKK....KKK..."],
	'pass':  ["...rRRRRRRRRr...",
			  ".....KKKKKK....."],
	'b':     ["...rRRRRRRRRr...",
			  "....KKK..KKK...."],
}


def herbalist():
	global PAL_CUR
	PAL_CUR = dict(SKIN)
	PAL_CUR.update({'K': rgb(0x4f7a3a), 'k': rgb(0x35552a), 'g': rgb(0xb8b8b8), 'W': rgb(0x6a8f4a),
					'w': rgb(0x4a6a32), 'R': rgb(0x8a3a3a), 'r': rgb(0x632828), 'G': rgb(0x8fd06a)})
	legs = {k: [r.replace('K', 'B') for r in v] for k, v in HERBALIST_LEGS.items()}
	PAL_CUR['B'] = rgb(0x3b2a20)
	return frames_row(HERBALIST, legs, HERBALIST_BLINK)


HUNTER = [
	"......aa........",
	".....aAAa.......",
	"....aAAAAa..t...",
	"...aAAAAAAa.tt..",
	"...aAAAAAAAa.t..",
	"...aASSSSeSa.Q..",
	"...aASSSSSSn.Q..",
	"...aAsSSSSsa.Q..",
	"...aaGGGGGGaaQ..",
	"..aGGGGGGGGGGa..",
	"..SaGGGBGGGGaS..",
	"...aGGGBGGGGa...",
	"...aGGGGGGGGa...",
	"....aGGGGGGa....",
]
HUNTER_BLINK = list(HUNTER)
HUNTER_BLINK[5] = "...aASSSSsSa.Q.."
HUNTER_ATTACK = list(HUNTER)
HUNTER_ATTACK[9] = "..aGGGGGGGGGGaSS"
HUNTER_ATTACK[10] = "..SaGGGBGGGGa..."


def hunter():
	global PAL_CUR
	PAL_CUR = dict(SKIN)
	PAL_CUR.update({'A': rgb(0x6e7378), 'a': rgb(0x4a4e52), 'G': rgb(0x4e5a40), 'B': rgb(0x3a2a1a),
					'Q': rgb(0x7a5230), 't': rgb(0xe8e0d0), 'P': rgb(0x3a3830), 'K': rgb(0x3b2a20),
					'q': rgb(0x2a2822)})
	legs = {k: [r.replace('d', 'q') for r in v] for k, v in HUMAN_LEGS.items()}
	return frames_row(HUNTER, legs, HUNTER_BLINK, HUNTER_ATTACK)


# ------------------------------------------------------------------ animals (side view, facing right)

HUSKY = [
	"................",
	"................",
	"................",
	"..........g..g..",
	"..W......gGggG..",
	".WWw.....GWWbWg.",
	".wWw....gGWWWWWn",
	"..wGgggggGWWWWw.",
	"..gGGGGGGGGWWw..",
	"..gGGGGGGGGGWw..",
	"...WWWWWWWWWW...",
	"...wWWWWWWWWw...",
]
HUSKY_BLINK = list(HUSKY)
HUSKY_BLINK[4] = ".W.......gGggG.."
HUSKY_BLINK[5] = "WWw......GWWbWg."
QUAD_LEGS = {
	'stand': ["...l.l....l.l...",
			  "...l.l....l.l...",
			  "...k.k....k.k..."],
	'a':     ["..l..l...l..l...",
			  ".l...l..l....l..",
			  ".k...k..k....k.."],
	'pass':  ["....ll....ll....",
			  "....ll....ll....",
			  "....kk....kk...."],
	'b':     ["...l..l...l..l..",
			  "...l...l..l...l.",
			  "...k...k..k...k."],
}
HUSKY_ATTACK = list(HUSKY)
HUSKY_ATTACK[6] = ".wWw....gGWWWWWW"
HUSKY_ATTACK[7] = "..wGgggggGWWWrr."


def husky():
	global PAL_CUR
	PAL_CUR = {'G': rgb(0x6e7682), 'g': rgb(0x4a515c), 'W': rgb(0xeef0f2), 'w': rgb(0xbfc6cc),
			   'b': rgb(0x6ad0ff), 'n': rgb(0x1e1e22), 'k': rgb(0x8a929a), 'r': rgb(0xd05a5a),
			   'l': rgb(0xd8dde2)}
	return frames_row([''] + HUSKY, QUAD_LEGS, [''] + HUSKY_BLINK, [''] + HUSKY_ATTACK)


REINDEER = [
	"..........t.t...",
	"..........tt.t..",
	"...........ttt..",
	"............t...",
	"...........BBB..",
	"...........BeBBn",
	"..b........BBBB.",
	".bBb......WBB...",
	".bBBbbbbbbWWB...",
	"..BBBBBBBBBWW...",
	"..BBBBBBBBBB....",
	"..bBBBBBBBBb....",
]
REINDEER_IDLE2 = [
	"................",
	"..........t.t...",
	"..........tt.t..",
	"...........ttt..",
	"............t...",
	"..b.........BB..",
	".bBb.......BBeB.",
	".bBBbbbbbbWBBBBn",
	"..BBBBBBBBBWWB..",
	"..BBBBBBBBBBW...",
	"..BBBBBBBBBB....",
	"..bBBBBBBBBb....",
]
DEER_LEGS = {
	'stand': ["...l.l....l.l...",
			  "...l.l....l.l...",
			  "...h.h....h.h..."],
	'a':     ["..l..l...l..l...",
			  ".l...l..l....l..",
			  ".h...h..h....h.."],
	'pass':  ["....ll....ll....",
			  "....ll....ll....",
			  "....hh....hh...."],
	'b':     ["...l..l...l..l..",
			  "...l...l..l...l.",
			  "...h...h..h...h."],
}


def reindeer():
	global PAL_CUR
	PAL_CUR = {'B': rgb(0x8a6040), 'b': rgb(0x5e3e28), 'W': rgb(0xe8e2d8), 't': rgb(0xd8c49a),
			   'e': rgb(0x1e1410), 'n': rgb(0x2a1a14), 'l': rgb(0x6b4a30), 'h': rgb(0x2a1e16)}
	base = REINDEER + [''] * 1
	return frames_row(REINDEER + [''], DEER_LEGS, REINDEER_IDLE2 + [''])[:12]


WOLF = [
	"................",
	"................",
	"................",
	"...........g.g..",
	"..........gGgGg.",
	"..........GGGyGg",
	"........ggGLLLLn",
	".ggggggggGGGLLw.",
	"gGGGGGGGGGGGLw..",
	"gGg.GGGGGGGGGg..",
	"gg..GLLLLLLLGg..",
	"....gLLLLLLLg...",
]
WOLF_BLINK = list(WOLF)
WOLF_BLINK[5] = "..........GGGgGg"
WOLF_ATTACK = list(WOLF)
WOLF_ATTACK[5] = "..........GGGyGgg"[:16]
WOLF_ATTACK[6] = "........ggGLLLLLn"[:16]
WOLF_ATTACK[7] = ".ggggggggGGGLrrr"


def wolf():
	global PAL_CUR
	PAL_CUR = {'G': rgb(0x7d838c), 'g': rgb(0x50555e), 'L': rgb(0xb8bec6), 'w': rgb(0x9aa0a8),
			   'y': rgb(0xf2c84a), 'n': rgb(0x1e1e22), 'k': rgb(0x3a3e45), 'r': rgb(0xc04a4a),
			   'l': rgb(0x6a7079)}
	return frames_row([''] + WOLF, QUAD_LEGS, [''] + WOLF_BLINK, [''] + WOLF_ATTACK)


HARE = [
	"................",
	"................",
	"................",
	"................",
	"..........kk....",
	"..........Wk.k..",
	"..........WwWk..",
	"..........WWWw..",
	"..........WeWWn.",
	"....wWWWWWWWWW..",
	"...WWWWWWWWWWw..",
	"..wWWWWWWWWWw...",
	"..wWWWWWWWWw....",
	"...wwwwwwww.....",
]
HARE_IDLE2 = list(HARE)
HARE_IDLE2[8] = "..........WeWWWn"
HARE_LEGS = {
	'stand': ["...ww....ww....."],
	'a':     ["..ww......ww...."],
	'pass':  ["....wwwwww......"],
	'b':     [".www......www..."],
}


def hare():
	global PAL_CUR
	PAL_CUR = {'W': rgb(0xf4f6f8), 'w': rgb(0xc4ccd4), 'k': rgb(0x2a2a30), 'e': rgb(0x2a1e1e),
			   'n': rgb(0xe8a0a8)}
	return frames_row([''] + HARE, HARE_LEGS, [''] + HARE_IDLE2)


# ------------------------------------------------------------------ items (16x16 slots)

ITEMS = {
	'smoked_fish': ([
		"................",
		"................",
		"................",
		"..........oo....",
		".oo......oGGo...",
		"oGGo...ooGGGGoo.",
		"oGGGoooGGGgGGGeo",
		".oGGGGGGgGGgGGGo",
		".oGgGGgGGGGGGGo.",
		"oGGGoooGGgGGoo..",
		"oGGo...ooooo....",
		".oo.............",
	], {'o': rgb(0x3a2412), 'G': rgb(0xc98a3a), 'g': rgb(0x9a5e22), 'e': rgb(0x1e1410)}),
	'pine_bread': ([
		"................",
		"................",
		"....oooooooo....",
		"..ooBBbBBbBBoo..",
		".oBBnBBBnBBBnBo.",
		"oBBBBBnBBBBBBBBo",
		"oBnBBBBBBnBBBnBo",
		"oBBBBnBBBBBBBBBo",
		"oDBBBBBBBBnBBBDo",
		".oDDDDDDDDDDDDo.",
		"..oooooooooooo..",
	], {'o': rgb(0x3a2412), 'B': rgb(0xa86a38), 'b': rgb(0xc88a50), 'n': rgb(0xf0dca0), 'D': rgb(0x6e4220)}),
	'herbal_tea': ([
		"................",
		"....w..w........",
		".....w..w.......",
		"....w..w........",
		"..oooooooo......",
		"..oGgGGgGoooo...",
		"..oMMMMMMMo.Mo..",
		"..oMMMMMMMo..o..",
		"..oMmMMMMMo.Mo..",
		"..oMmMMMMMoMo...",
		"..oMmmMMMMoo....",
		"...oooooooo.....",
	], {'o': rgb(0x3a2412), 'G': rgb(0xb0582a), 'g': rgb(0xd07a3a), 'M': rgb(0x9a6a40), 'm': rgb(0x7a4e2c),
		'w': rgb(0xe6eef2)}),
	'wolf_pelt': ([
		"................",
		"..oo.......oo...",
		".oGGo.....oGGo..",
		".oGGGoooooGGGo..",
		"..oGGGGGGGGGo...",
		".oGGLLLLLLLGGo..",
		"oGGLLLLLLLLLGGo.",
		"oGGLLLLLLLLLGGo.",
		".oGLLLLLLLLLGo..",
		".oGGLLLLLLLGGo..",
		"oGGoGGGGGGGoGGo.",
		"oGo.oGGGGGo.oGo.",
		".o...oGGGo...o..",
	], {'o': rgb(0x34383e), 'G': rgb(0x7d838c), 'L': rgb(0xb8bec6)}),
	'pine_resin': ([
		"................",
		"................",
		"................",
		"...oooo.........",
		"..oYYyYo........",
		".oYwYYYYo.......",
		".oYYYYyYo.......",
		".oYyYYYYo.......",
		"..oYYYyo........",
		"...oooo.........",
	], {'o': rgb(0x5a2e0a), 'Y': rgb(0xe08a1e), 'y': rgb(0xb0600e), 'w': rgb(0xffe0a0)}),
	'fur_cloak': ([
		".....oooooo.....",
		"....oCCCCCCo....",
		"...oCCcoocCCo...",
		"...oCCo..oCCo...",
		"..oFFFFFFFFFFo..",
		"..oFFFFFFFFFFo..",
		"..oCCCCcCCCCCo..",
		".oCCCCCcCCCCCCo.",
		".oCCCCCcCCCCCCo.",
		"oCCCCCCcCCCCCCCo",
		"oCCCCCCcCCCCCCCo",
		"oFFFFFFFFFFFFFFo",
		".oooooooooooooo.",
	], {'o': rgb(0x2e1e12), 'F': rgb(0xe8e0d0), 'C': rgb(0x7d838c), 'c': rgb(0x5a606a)}),
	'mittens': ([
		"................",
		"..oo......oo....",
		".oMMo....oMMo...",
		".oMMoo..ooMMo...",
		".oMMMMooMMMMo...",
		"oMMMMMooMMMMMo..",
		"oMMMMMooMMMMMo..",
		"oMMMMMooMMMMMo..",
		"oMMMMMooMMMMMo..",
		".oFFFo..oFFFo...",
		".oFFFo..oFFFo...",
		"..ooo....ooo....",
	], {'o': rgb(0x3a2412), 'M': rgb(0x9c3b32), 'F': rgb(0xe8e0d0)}),
}
ITEM_ORDER = ['smoked_fish', 'pine_bread', 'herbal_tea', 'wolf_pelt', 'pine_resin', 'fur_cloak', 'mittens']


def items_sheet():
	img = Image.new('RGBA', (256, 512), (0, 0, 0, 0))
	for i, name in enumerate(ITEM_ORDER):
		rows, pal = ITEMS[name]
		img.alpha_composite(grid(rows, pal), ((i % 16) * 16, (i // 16) * 16))
	return img


def villagers_sheet():
	return sheet([villager(i) for i in range(4)])


def traders_sheet():
	return sheet([fur_trader(), herbalist(), hunter()])


def animals_sheet():
	return sheet([husky(), reindeer(), wolf(), hare()])
