# MOD (taiga town): creatures and items of the taiga floors (beasts.png, spirits.png, leshy.png, more item
# icons). Same sheet layout as taiga_sprites.py: 16x16 frames, rows of 12 (idle 0-1, run 2-5, attack 6-8,
# death 9-11). The Leshy uses 24x24 frames and 3 more for his spell (12-14).

from PIL import Image
import taiga_sprites as ts
from taiga_sprites import rgb, grid, shift, squash, sheet, frames_row

# ------------------------------------------------------------------ beasts.png

def wolf_variant(pal):
	ts.PAL_CUR = pal
	return frames_row([''] + ts.WOLF, ts.QUAD_LEGS, [''] + ts.WOLF_BLINK, [''] + ts.WOLF_ATTACK)


FROST_WOLF = {'G': rgb(0xc3cfdb), 'g': rgb(0x7f92a6), 'L': rgb(0xf2f6fa), 'w': rgb(0xd4dee8),
			  'y': rgb(0x6ad0ff), 'n': rgb(0x1e2430), 'k': rgb(0x5e6e80), 'r': rgb(0xc04a4a), 'l': rgb(0x9fb0c2)}
ALPHA = {'G': rgb(0x4a4f57), 'g': rgb(0x26292e), 'L': rgb(0x7c838c), 'w': rgb(0x5a6068),
		 'y': rgb(0xff3a3a), 'n': rgb(0x101114), 'k': rgb(0x1a1c20), 'r': rgb(0xe04a4a), 'l': rgb(0x3a3e45)}

BOAR = [
	"................",
	"................",
	"................",
	"....b.b.b.......",
	"...bBbBbBbb.....",
	"..bBBBBBBBBBb...",
	".bBBBBBBBBBBBb..",
	"bBBBBBBBBBBBeBb.",
	"bBBBBBBBBBBBBBpp",
	"bBBBBBBBBBBBBBpp",
	".bBBBBBBBBBBBtB.",
	"..bBBBBBBBBBBb..",
	"...bbbbbbbbbb...",
]
BOAR_BLINK = list(BOAR)
BOAR_BLINK[7] = "bBBBBBBBBBBBbBb."
BOAR_ATTACK = list(BOAR)
BOAR_ATTACK[9] = "bBBBBBBBBBBBBBBpp"[:16]
BOAR_ATTACK[10] = ".bBBBBBBBBBBBBtt"
BOAR_LEGS = {
	'stand': ["...ll......ll...", "...kk......kk..."],
	'a':     ["..ll......ll....", ".kk......kk....."],
	'pass':  ["....ll....ll....", "....kk....kk...."],
	'b':     ["....ll......ll..", ".....kk......kk."],
}


def boar():
	ts.PAL_CUR = {'B': rgb(0x6b4a32), 'b': rgb(0x3e2a1c), 'e': rgb(0x1e1410), 'p': rgb(0xb07a60),
				  't': rgb(0xf0e6d2), 'l': rgb(0x3e2a1c), 'k': rgb(0x1e1410)}
	return frames_row([''] + BOAR, BOAR_LEGS, [''] + BOAR_BLINK, [''] + BOAR_ATTACK)


BEAR = [
	"................",
	"...........bb...",
	"..........bBBb..",
	"..bbbbbbbbBBBBb.",
	".bBBBBBBBBBBeBBb",
	"bBBBBBBBBBBBBBnn",
	"bBBBBBBBBBBBBBb.",
	"bBBBBBBBBBBBBb..",
	"bBBBBBBBBBBBBb..",
	".bBBBBBBBBBBBb..",
	".bBBbbbbbbbBBb..",
]
BEAR_BLINK = list(BEAR)
BEAR_BLINK[4] = ".bBBBBBBBBBBbBBb"
BEAR_ATTACK = list(BEAR)
BEAR_ATTACK[4] = ".bBBBBBBBBBBeBBbr"[:16]
BEAR_ATTACK[5] = "bBBBBBBBBBBBBBrr"
BEAR_LEGS = {
	'stand': ["..BB.BB...BB.BB.", "..BB.BB...BB.BB.", "..kk.kk...kk.kk."],
	'a':     [".BB..BB..BB..BB.", "BB...BB.BB...BB.", "kk...kk.kk...kk."],
	'pass':  ["...BBBB...BBBB..", "...BBBB...BBBB..", "...kkkk...kkkk.."],
	'b':     ["..BB..BB..BB.BB.", "..BB...BB.BB..BB", "..kk...kk.kk..kk"],
}


def bear():
	ts.PAL_CUR = {'B': rgb(0x7a5232), 'b': rgb(0x4a301e), 'e': rgb(0x120c08), 'n': rgb(0x2a1a12),
				  'k': rgb(0x2a1a12), 'r': rgb(0xd05050)}
	return frames_row([''] * 2 + BEAR, BEAR_LEGS, [''] * 2 + BEAR_BLINK, [''] * 2 + BEAR_ATTACK)


def white_stag():
	ts.PAL_CUR = {'B': rgb(0xeef2f5), 'b': rgb(0xaab6c2), 'W': rgb(0xffffff), 't': rgb(0xc8dcef),
				  'e': rgb(0x2a3a50), 'n': rgb(0x8a7a80), 'l': rgb(0xd0d8e0), 'h': rgb(0x5a6470)}
	return frames_row(ts.REINDEER + [''], ts.DEER_LEGS, ts.REINDEER_IDLE2 + [''])[:12]


SABLE = [
	"................",
	"................",
	"................",
	"................",
	"................",
	"................",
	"..........g.g...",
	".........gGGGg..",
	"..gg.....GGGeGn.",
	".gGGg...gGGGGG..",
	".gGGGgggGGGGG...",
	"..ggGGGGGGGGg...",
	"....gGGGGGGGg...",
	".....ggggggg....",
]
SABLE_IDLE2 = list(SABLE)
SABLE_IDLE2[8] = "..gg.....GGGgGn."
SABLE_LEGS = {
	'stand': ["....gg...gg.....", "....kk...kk....."],
	'a':     ["...gg.....gg....", "...kk......kk..."],
	'pass':  [".....gg.gg......", ".....kk.kk......"],
	'b':     ["....gg....gg....", "....kk....kk...."],
}


def sable():
	ts.PAL_CUR = {'G': rgb(0xe8b84a), 'g': rgb(0xa87a24), 'e': rgb(0x2a1a10), 'n': rgb(0x3a2a1a),
				  'k': rgb(0x6a4a20)}
	return frames_row(SABLE, SABLE_LEGS, SABLE_IDLE2)


def beasts_sheet():
	return sheet([wolf_variant(FROST_WOLF), wolf_variant(ALPHA), boar(), bear(), white_stag(), sable()])


# ------------------------------------------------------------------ spirits.png

WISP = [
	"................",
	"................",
	"......wwww......",
	".....wWWWWw.....",
	"....wWHHWWWw....",
	"....wWHWeWeWw...",
	"....wWWWWWWWw...",
	".....wWWWWWw....",
	"......wWWWw.....",
	".......wWw......",
]
WISP_IDLE2 = list(WISP)
WISP_IDLE2[4] = "....wWWHHWWw...."
WISP_ATTACK = list(WISP)
WISP_ATTACK[3] = ".....wHHHHw....."
WISP_ATTACK[6] = "....wWHHHHHWw..."
WISP_TAIL = {
	'stand': ["......w.w.w.....", ".....w...w......"],
	'a':     [".....w.w.w......", "......w...w....."],
	'pass':  ["......w.w.w.....", "......w.w......."],
	'b':     [".......w.w.w....", "......w...w....."],
}


def wisp():
	ts.PAL_CUR = {'W': rgb(0xbfe8ff), 'w': rgb(0x6fb7e8), 'H': rgb(0xffffff), 'e': rgb(0x1e4a7a)}
	rows = frames_row([''] * 2 + WISP, WISP_TAIL, [''] * 2 + WISP_IDLE2, [''] * 2 + WISP_ATTACK)
	# it hovers: the run frames bob up and down
	rows[3] = shift(rows[3], 0, -1)
	rows[5] = shift(rows[5], 0, -1)
	return rows


FROSTBITTEN = [
	"................",
	".....hh.hhh.....",
	"....hHHhHHHh....",
	"...hHHHHHHHHh...",
	"...FFfFFFfFFF...",
	"...sSSSSSSeSS...",
	"...sSSSSSSSSn...",
	"....sSiSiSiS....",
	"...dCCiCCiCCd...",
	"..dCCCCCCCCCCd..",
	"..SdCcCCCCcCdS..",
	"...dCCCCcCCCd...",
	"...dCcCCCCCcd...",
	"....dCCcCCCd....",
]
FROSTBITTEN_BLINK = list(FROSTBITTEN)
FROSTBITTEN_BLINK[5] = "...sSSSSSSsSS..."
FROSTBITTEN_ATTACK = list(FROSTBITTEN)
FROSTBITTEN_ATTACK[9] = "..dCCCCCCCCCCdSS"
FROSTBITTEN_ATTACK[10] = "..SdCcCCCCcCd..."


def frostbitten():
	ts.PAL_CUR = {'h': rgb(0x4a4642), 'H': rgb(0x6e6860), 'F': rgb(0xc8d4dc), 'f': rgb(0x9aa8b2),
				  'S': rgb(0xaecbe0), 's': rgb(0x7e9db4), 'e': rgb(0x9fe8ff), 'n': rgb(0x8fb0c8),
				  'i': rgb(0xe6f6ff), 'C': rgb(0x5d6b78), 'c': rgb(0x46525e), 'd': rgb(0x343d46),
				  'P': rgb(0x3a404a), 'q': rgb(0x2a3038), 'K': rgb(0x2a2a30)}
	legs = {k: [r.replace('d', 'q') for r in v] for k, v in ts.HUMAN_LEGS.items()}
	return frames_row(FROSTBITTEN, legs, FROSTBITTEN_BLINK, FROSTBITTEN_ATTACK)


TREANT = [
	".......ss.......",
	"......dssd......",
	".....dmmmsd.....",
	"....dmmmmmmd....",
	"...dmsmmmmssd...",
	"....dmeemeed....",
	"...dmmmmmmmmd...",
	"..dmsmmmmmmmssd.",
	"...dmmmooommd...",
	"..dmmmmmmmmmmmd.",
	".dmsmmmmmmmmmssd",
	"...ddmmTTmmdd...",
	"......TTTT......",
	"......TTTT......",
]
TREANT_BLINK = list(TREANT)
TREANT_BLINK[5] = "....dmmmmmmd...."
TREANT_ATTACK = list(TREANT)
TREANT_ATTACK[7] = "..dmsmmmmmmmssdT"
TREANT_ATTACK[8] = "...dmmmooommdTT."
TREANT_ROOTS = {
	'stand': [".....T.TT.T.....", "....T..TT..T...."],
	'a':     ["....T..TT.T.....", "...T...TT..T...."],
	'pass':  [".....TTTTTT.....", "......T..T......"],
	'b':     [".....T.TT..T....", "....T..TT...T..."],
}
TREE_STILL = {
	'stand': ["......TTTT......", "....ssssssss...."],
	'a':     ["......TTTT......", "....ssssssss...."],
	'pass':  ["......TTTT......", "....ssssssss...."],
	'b':     ["......TTTT......", "....ssssssss...."],
}


def treant():
	ts.PAL_CUR = {'d': rgb(0x10241f), 'm': rgb(0x2a5546), 's': rgb(0xe6eef4), 'e': rgb(0xffc844),
				  'o': rgb(0x1a0f0a), 'T': rgb(0x6b4630)}
	awake = frames_row(TREANT, TREANT_ROOTS, TREANT_BLINK, TREANT_ATTACK)
	still_rows = [r.replace('e', 'm').replace('o', 'm') for r in TREANT]
	disguise = frames_row(still_rows, TREE_STILL, still_rows)
	return awake, disguise


TOTEM = [
	"....k......k....",
	"...kWk....kWk...",
	"...kWWkkkkWWk...",
	"....kAAAAAAk....",
	"....kAeAAeAk....",
	"....kAAAAAAk....",
	"....kAAmmAAk....",
	"....kAAAAAAk....",
	"....kBBBBBBk....",
	"....kBeBBeBk....",
	"....kBBBBBBk....",
	"....kBBmmBBk....",
	"....kBBBBBBk....",
	"...kkCCCCCCkk...",
]
TOTEM_GLOW = [r.replace('e', 'E') for r in TOTEM]
TOTEM_BASE = {k: ["..kCCCCCCCCCCk..", "..kkkkkkkkkkkk.."] for k in ('stand', 'a', 'pass', 'b')}


def totem():
	ts.PAL_CUR = {'A': rgb(0x8a5d3c), 'B': rgb(0x6b4630), 'C': rgb(0x4e3222), 'W': rgb(0xc6996a),
				  'k': rgb(0x2a1a12), 'e': rgb(0x7fd8ff), 'E': rgb(0xe6fbff), 'm': rgb(0x1a0f0a)}
	return frames_row(TOTEM, TOTEM_BASE, TOTEM_GLOW, TOTEM_GLOW, lean=0)


TRAPPER = [
	"................",
	".....hhhhhh.....",
	"....hHHHHHHh....",
	"...hHHHHHHHHh...",
	"..hHFFFFFFFFHh..",
	"..hHsSSSSSeSHh..",
	"..hhsSSSSSSSnh..",
	"....gggggggg....",
	"...dCgggggggCd..",
	"..dCCCggggCCCCd.",
	"..MdCCCCBCCCCdM.",
	"...dCCCCBCCCCd..",
	"...dCCCrrrCCCd..",
	"....dCCCCCCCd...",
]
TRAPPER_BLINK = list(TRAPPER)
TRAPPER_BLINK[5] = "..hHsSSSSSsSHh.."


def trapper():
	ts.PAL_CUR = dict(ts.SKIN)
	ts.PAL_CUR.update({'h': rgb(0x5a3a22), 'H': rgb(0x7a5232), 'F': rgb(0xd8ccb8), 'g': rgb(0xb8b4ac),
					   'C': rgb(0x8a6a44), 'd': rgb(0x5a4430), 'M': rgb(0x5a3a22), 'B': rgb(0x2a1c10),
					   'r': rgb(0xc8b07a), 'P': rgb(0x4a4238), 'q': rgb(0x3a3228), 'K': rgb(0x2b1e16)})
	legs = {k: [r.replace('d', 'q') for r in v] for k, v in ts.HUMAN_LEGS.items()}
	return frames_row(TRAPPER, legs, TRAPPER_BLINK)


SHAMAN = [
	"..t..........t..",
	"..tt........tt..",
	"...t.kkkkkk.t...",
	"....kKbKKbKk....",
	"...wwSSSSSSww...",
	"...wSSSSSSeSw...",
	"...wsSSSSSSSn...",
	"...wwsSSSSsww...",
	"...rRRbRRbRRr...",
	"..rRRRRRRRRRRr..",
	"..SrRRRbRRRRrS..",
	"...rRRRRRRRRr...",
	"...rRRbRRbRRr...",
	"...rRRRRRRRRr...",
]
SHAMAN_BLINK = list(SHAMAN)
SHAMAN_BLINK[5] = "...wSSSSSSsSw..."


def shaman():
	ts.PAL_CUR = dict(ts.SKIN)
	ts.PAL_CUR.update({'t': rgb(0xd8c49a), 'k': rgb(0x2a1a12), 'K': rgb(0x8a2a2a), 'b': rgb(0x4ac8c8),
					   'w': rgb(0xe8e8ec), 'r': rgb(0x4a2020), 'R': rgb(0x7a2e2e), 'B': rgb(0x3b2a20)})
	legs = {k: [r.replace('K', 'B') for r in v] for k, v in ts.HERBALIST_LEGS.items()}
	legs = {k: [r.replace('r', 'r').replace('R', 'R') for r in v] for k, v in legs.items()}
	return frames_row(SHAMAN, legs, SHAMAN_BLINK)


def spirits_sheet():
	awake, disguise = treant()
	return sheet([wisp(), frostbitten(), awake, disguise, totem(), trapper(), shaman()])


# ------------------------------------------------------------------ leshy.png (24x24)

LESHY = [
	"........................",
	"...a................a...",
	"...aa..............aa...",
	"....aa.a........a.aa....",
	".....aaa........aaa.....",
	"......aa.wwwwww.aa......",
	".......awWWWWWWwa.......",
	"......wWWWWWWWWWWw......",
	"......wWgeWWWWegWw......",
	"......wWWWWWWWWWWw......",
	".......mMMmWWmMMm.......",
	"......mMMMMMMMMMMm......",
	".....mMMMMmMMmMMMMm.....",
	"....bBmMMMMMMMMMMmBb..c.",
	"...bBBBmMMMMMMMMmBBBbcc.",
	"...bBBbBmMMMMMMmBbBBBc..",
	"...bB.bBBBBBBBBBBb.BBc..",
	"..bBb.bBBBBbBBBBBb.bBc..",
	"..bb..bBBBBbBBBBBb..bc..",
	"......bBBBBbBBBBBb...c..",
	"......bBBBb.bBBBBb...c..",
	".....bBBBb...bBBBBb..c..",
	".....bbbb.....bbbb...c..",
	"........................",
]
LESHY_PAL = {'a': rgb(0xd8c49a), 'w': rgb(0x4e3222), 'W': rgb(0x6b4630), 'g': rgb(0x2a1a12),
			 'e': rgb(0x8dff6a), 'E': rgb(0xe8ffd8), 'm': rgb(0x2f5a2c), 'M': rgb(0x4f8a3a),
			 'b': rgb(0x3a2618), 'B': rgb(0x5a3e26), 'c': rgb(0x8a6a3a)}


def leshy_variant(rows, legs=None, eyes=None, crook=None):
	r = list(rows)
	if eyes:
		r = [row.replace('e', eyes) for row in r]
	if legs:
		for i, row in legs.items():
			r[i] = row
	if crook is not None:
		# move the crook: strip it and draw it in the given column / rows
		r = [row.replace('c', '.') for row in r]
		for (x, y) in crook:
			row = list(r[y])
			row[x] = 'c'
			r[y] = ''.join(row)
	return grid(r, LESHY_PAL, 24, 24)


def leshy_sheet():
	base = LESHY
	frames = []
	frames.append(leshy_variant(base))
	frames.append(leshy_variant(base, eyes='E'))
	walk = [
		{20: "......bBBBb..bBBBBb..c..", 21: ".....bBBBb.....bBBBb.c..", 22: "....bbbb.......bbbb.c..."},
		{20: "......bBBBBbBBBBBb..c...", 21: "......bBBBb.bBBBBb..c...", 22: "......bbbb..bbbb....c..."},
		{20: ".......bBBBbbBBBb...c...", 21: "......bBBBb..bBBBb..c...", 22: "......bbbb...bbbbb.c...."},
		{20: "......bBBBBbBBBBBb..c...", 21: "......bBBBb.bBBBBb..c...", 22: "......bbbb..bbbb....c..."},
	]
	for w in walk:
		frames.append(shift(leshy_variant(base, legs=w), 0, 0))
	# swinging the crook down at the enemy
	swing = [
		[(21, 9), (21, 10), (22, 11), (22, 12), (22, 13), (21, 14), (21, 15), (21, 16)],
		[(19, 13), (20, 13), (21, 14), (22, 15), (23, 16), (23, 17)],
		[(20, 15), (21, 16), (22, 17), (23, 18), (23, 19), (23, 20)],
	]
	for c in swing:
		frames.append(leshy_variant(base, crook=c))
	# falling apart into leaves and wood
	d0 = leshy_variant(base)
	for fy, fx, dk in ((0.8, 1.0, 0.1), (0.5, 1.2, 0.25), (0.25, 1.35, 0.45)):
		box = d0.getbbox()
		part = d0.crop(box)
		w, h = int(part.width * fx), max(1, int(part.height * fy))
		part = part.resize((w, h), Image.NEAREST)
		px = part.load()
		for yy in range(h):
			for xx in range(w):
				r_, g_, b_, a_ = px[xx, yy]
				px[xx, yy] = (int(r_ * (1 - dk)), int(g_ * (1 - dk)), int(b_ * (1 - dk)), a_)
		out = Image.new('RGBA', (24, 24), (0, 0, 0, 0))
		out.paste(part, (max(0, 12 - w // 2), 23 - h), part)
		frames.append(out)
	# the spell: the crook raised high, eyes blazing
	raise_ = [
		[(21, 5), (21, 6), (21, 7), (21, 8), (21, 9), (21, 10), (21, 11), (21, 12), (21, 13)],
		[(21, 2), (21, 3), (21, 4), (21, 5), (21, 6), (21, 7), (21, 8), (21, 9), (21, 10), (20, 1)],
		[(21, 1), (21, 2), (21, 3), (21, 4), (21, 5), (21, 6), (21, 7), (21, 8), (22, 0), (20, 0)],
	]
	for i, c in enumerate(raise_):
		frames.append(leshy_variant(base, crook=c, eyes='E' if i else None))

	img = Image.new('RGBA', (24 * 12, 24 * 2), (0, 0, 0, 0))
	for i, f in enumerate(frames):
		img.alpha_composite(f, ((i % 12) * 24, (i // 12) * 24))
	return img


# ------------------------------------------------------------------ more items (slots 7-19 of taiga/items.png)

ITEMS2 = [
	('frost_javelin', [
		"................",
		"..............wW",
		".............wWw",
		"............bWw.",
		"...........bb...",
		"..........bb....",
		".........bb.....",
		"........bb......",
		".......bb.......",
		"......bb........",
		".....bb.........",
		"....bb..........",
		"...rr...........",
		"..rr............",
		".rr.............",
	], {'w': rgb(0x9fd8ff), 'W': rgb(0xe8f8ff), 'b': rgb(0x8a6a44), 'r': rgb(0xc8b07a)}),
	('trapper_knife', [
		"................",
		"................",
		"..........oo....",
		".........oSSo...",
		"........oSSSo...",
		".......oSSSo....",
		"......oSSSo.....",
		".....oSSSo......",
		"....oSSso.......",
		"...ooooo........",
		"..oHHo..........",
		".oHHo...........",
		"oHHo............",
		".oo.............",
	], {'o': rgb(0x2a2420), 'S': rgb(0xd6dde4), 's': rgb(0xa0aab4), 'H': rgb(0x7a5232)}),
	('bearskin_coat', [
		"...oooooooooo...",
		"..oFFFFFFFFFFo..",
		".oBBFFFooFFFBBo.",
		"oBBBBFo..oFBBBBo",
		"oBBBBBo..oBBBBBo",
		"oBBbBBBooBBBbBBo",
		".oBbBBBBBBBBbBo.",
		"..oBBBBBBBBBBo..",
		"..oBbBBBBBBbBo..",
		"..oBBBBBBBBBBo..",
		"..oBBBBBBBBBBo..",
		"..oFFFFFFFFFFo..",
		"...oooooooooo...",
	], {'o': rgb(0x2a1a12), 'F': rgb(0x9a7a56), 'B': rgb(0x7a5232), 'b': rgb(0x5a3a22)}),
	('bear_claw', [
		"................",
		"..W...W...W.....",
		"..Ww..Ww..Ww....",
		"...Ww..Ww..Ww...",
		"...oBBBBBBBBo...",
		"..oBBBBBBBBBBo..",
		"..oBbBBbBBbBBo..",
		"..oBBBBBBBBBBo..",
		"...oLLLLLLLLo...",
		"...oLLLLLLLLo...",
		"....oooooooo....",
	], {'W': rgb(0xf0e6d2), 'w': rgb(0xb8ac94), 'o': rgb(0x2a1a12), 'B': rgb(0x7a5232), 'b': rgb(0x5a3a22),
		'L': rgb(0x6b4a32)}),
	('woodcutter_axe', [
		"........ooo.....",
		".......oSSSo....",
		"......oSSSSSo...",
		"......oSSSSSSo..",
		".......oSSSSoH..",
		"........oSSoHH..",
		"..........HH....",
		".........HH.....",
		"........HH......",
		".......HH.......",
		"......HH........",
		".....HH.........",
		"....HH..........",
		"...hh...........",
	], {'o': rgb(0x2a2420), 'S': rgb(0xc4ccd4), 'H': rgb(0x8a5d3c), 'h': rgb(0x5a3a22)}),
	('spirit_staff', [
		"..........b.bb..",
		"...........bbb..",
		"..........tWt...",
		"..........WkW...",
		".........sWWs...",
		"........SS......",
		".......SS.......",
		"......SS........",
		".....SS.........",
		"....SS..........",
		"...SS...........",
		"..SS............",
		".ss.............",
	], {'b': rgb(0x4ac8c8), 't': rgb(0xd8c49a), 'W': rgb(0xe8e2d4), 'k': rgb(0x2a1a12), 's': rgb(0x8a2a2a),
		'S': rgb(0x6b4630)}),
	('leshy_crook', [
		"........mmm.....",
		".......mCCCm....",
		"......mC...Cm...",
		"......C.....C...",
		"..........eC....",
		"..........CC....",
		".........CC.....",
		"........CC......",
		".......CC.......",
		"......CC........",
		".....CC.........",
		"....CC..........",
		"...CC...........",
		"..cc............",
	], {'m': rgb(0x4f8a3a), 'C': rgb(0x6b4630), 'c': rgb(0x4e3222), 'e': rgb(0x8dff6a)}),
	('heart_of_taiga', [
		"................",
		"....oo...oo.....",
		"...oGGo.oGGo....",
		"..oGgGGoGGGGo...",
		"..oGgGGGGGGGo...",
		"..oGGGGGGGGGo...",
		"...oGGGGGGGo....",
		"....oGGGGGo.....",
		".....oGGGo......",
		"......oGo.......",
		".......o........",
	], {'o': rgb(0x5a3a12), 'G': rgb(0x7ad84a), 'g': rgb(0xe8ffb8)}),
	('elixir_stag', [
		"................",
		"......t..t......",
		".......tt.......",
		"......oooo......",
		".......oo.......",
		"......oWWo......",
		".....oWWWWo.....",
		"....oWWHWWWo....",
		"....oWWWWWWo....",
		"....oWWWWWWo....",
		".....oWWWWo.....",
		"......oooo......",
	], {'t': rgb(0xc8dcef), 'o': rgb(0x3a4a5a), 'W': rgb(0xd8ecff), 'H': rgb(0xffffff)}),
	('elixir_north', [
		"................",
		"................",
		"......oooo......",
		".......oo.......",
		"......oBBo......",
		".....oBBBBo.....",
		"....oBBHBBBo....",
		"....oBBBBBBo....",
		"....oGGGGGGo....",
		"....oGGGGGGo....",
		".....oGGGGo.....",
		"......oooo......",
	], {'o': rgb(0x2a3a4a), 'B': rgb(0x6fb7e8), 'H': rgb(0xe8f8ff), 'G': rgb(0x4a8adf)}),
	('alpha_fang', [
		"................",
		"................",
		"......oooo......",
		".....oWWWWo.....",
		".....oWWWWo.....",
		"......oWWo......",
		"......oWWo......",
		"......oWWo......",
		".......oWo......",
		".......oWo......",
		"........o.......",
		"....rr.....rr...",
		"...r..rrrrr..r..",
	], {'o': rgb(0x5a5040), 'W': rgb(0xf0e6d2), 'r': rgb(0x8a6a44)}),
	('bear_pelt', [
		"................",
		"..oo.......oo...",
		".oBBo.....oBBo..",
		".oBBBoooooBBBo..",
		"..oBBBBBBBBBo...",
		".oBBbbbbbbbBBo..",
		"oBBbbbbbbbbbBBo.",
		"oBBbbbbbbbbbBBo.",
		".oBbbbbbbbbbBo..",
		".oBBbbbbbbbBBo..",
		"oBBoBBBBBBBoBBo.",
		"oBo.oBBBBBo.oBo.",
		".o...oBBBo...o..",
	], {'o': rgb(0x2a1a12), 'B': rgb(0x7a5232), 'b': rgb(0x9a7046)}),
	('sable_pelt', [
		"................",
		"................",
		"..........oo....",
		".........oGGo...",
		"..ooooooooGGGo..",
		".oGGGGGGGGGGGo..",
		"oGGgGGGgGGGGo...",
		".oGGGGGGGGGo....",
		"..ooGGGGGGo.....",
		"....oGGGGo......",
		".....oooo.......",
	], {'o': rgb(0x6a4a14), 'G': rgb(0xe8b84a), 'g': rgb(0xfff0b0)}),
]


def add_items(img):
	for i, (name, rows, pal) in enumerate(ITEMS2):
		slot = 7 + i
		img.alpha_composite(grid(rows, pal), ((slot % 16) * 16, (slot // 16) * 16))
	return img
