# MOD (taiga town): renders mod/town_map.txt with the taiga textures, following the same tile
# stitching rules as the game (tiles/DungeonTerrainTilemap, DungeonWallsTilemap, TerrainFeaturesTilemap),
# so the art can be checked without building the game.
#
#   python mod/town_preview.py out.png [scale]
import os
import sys
import random
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TAIGA = os.path.join(ROOT, 'core', 'src', 'main', 'assets', 'taiga')

# legend shared with TaigaTownLevel.java
LEGEND = {
	'T': 'WALL', 't': 'WALL_DECO', '.': 'EMPTY', ',': 'EMPTY_DECO', '"': 'GRASS', '%': 'HIGH_GRASS',
	'P': 'STATUE', 'S': 'STATUE_SP', 'L': 'BOOKSHELF', '_': 'EMPTY_SP', '=': 'EMPTY_SP', '+': 'DOOR',
	'#': 'BARRICADE', '~': 'WATER', 'W': 'REGION_DECO', 'F': 'REGION_DECO_ALT', 'A': 'ALCHEMY', 'X': 'EXIT',
	'g': 'EMPTY_SP', '$': 'EMPTY_SP', 'f': 'EMPTY_SP', 'h': 'EMPTY_SP', 'u': 'EMPTY_SP',
	'v': 'EMPTY', 'd': 'EMPTY', 'r': 'EMPTY', 'w': 'EMPTY', 'b': 'EMPTY',
}
MARKERS = {'f': (200, 120, 40), 'h': (60, 160, 60), 'u': (40, 90, 50), 'v': (180, 60, 60), 'd': (120, 120, 140),
		   'r': (140, 90, 50), 'w': (90, 90, 100), 'b': (240, 240, 240), '$': (255, 220, 0)}

WALLISH = {'WALL', 'WALL_DECO', 'BOOKSHELF', None}
DOORS = {'DOOR', 'OPEN_DOOR'}
# DungeonTileSheet.waterStitcheable, for the terrains used here
WATER_STITCH = {'EMPTY', 'GRASS', 'EXIT', 'HIGH_GRASS', 'EMPTY_DECO', 'STATUE', 'REGION_DECO', 'ALCHEMY',
				'DOOR', 'OPEN_DOOR', 'BARRICADE'}


def load_map(path):
	with open(path) as f:
		rows = [l.rstrip('\n') for l in f if l.strip()]
	return rows


def render(scale=2, markers=True):
	rows = load_map(os.path.join(ROOT, 'mod', 'town_map.txt'))
	H, W = len(rows), len(rows[0])
	terr = [[LEGEND[c] for c in row] for row in rows]

	def at(x, y):
		if 0 <= x < W and 0 <= y < H:
			return terr[y][x]
		return None

	tiles = Image.open(os.path.join(TAIGA, 'tiles.png')).convert('RGBA')
	feats = Image.open(os.path.join(TAIGA, 'terrain_features.png')).convert('RGBA')
	raised = Image.open(os.path.join(TAIGA, 'raised_terrain.png')).convert('RGBA')
	water = Image.open(os.path.join(TAIGA, 'water.png')).convert('RGBA')

	def tile(sheet, i, cols=16):
		x, y = (i % cols) * 16, (i // cols) * 16
		return sheet.crop((x, y, x + 16, y + 16))

	rnd = random.Random(3)
	var = [[rnd.randint(0, 99) for _ in range(W)] for _ in range(H)]

	def alt(v, base, *pairs):
		res = base
		for ch, a in pairs:
			if v < ch:
				res = a
		return res

	img = Image.new('RGBA', (W * 16, H * 16), (0, 0, 0, 255))
	for y in range(0, H * 16, 32):
		for x in range(0, W * 16, 32):
			img.paste(water, (x, y))

	def put(sheet, i, x, y, cols=16):
		if i is None or i < 0:
			return
		img.alpha_composite(tile(sheet, i, cols), (x * 16, y * 16))

	# terrain layer
	for y in range(H):
		for x in range(W):
			t = terr[y][x]
			v = var[y][x]
			vis = None
			if t == 'EMPTY':
				vis = alt(v, 0, (52.5, 6), (5, 12))
			elif t == 'EMPTY_DECO':
				vis = alt(v, 1, (50, 7))
			elif t == 'GRASS':
				vis = alt(v, 2, (50, 8))
			elif t == 'EMPTY_SP':
				vis = alt(v, 4, (50, 10))
			elif t == 'EXIT':
				vis = 17
			elif t == 'WATER':
				m = 0
				for bit, (dx, dy) in ((1, (0, -1)), (2, (1, 0)), (4, (0, 1)), (8, (-1, 0))):
					n = at(x + dx, y + dy)
					if n in WATER_STITCH:
						m += bit
				vis = 32 + m if m else None
			elif t in DOORS:
				vis = 116 if at(x, y - 1) in WALLISH else (112 if t == 'DOOR' else 113)
			elif t in WALLISH:
				below = at(x, y + 1)
				if below in WALLISH:
					vis = None
				else:
					if below in DOORS:
						vis = 88
					elif t == 'WALL':
						vis = 80
					elif t == 'WALL_DECO':
						vis = 84
					else:
						vis = 92
					if v < 50:
						vis += 16
					if at(x + 1, y) not in WALLISH:
						vis += 1
					if at(x - 1, y) not in WALLISH:
						vis += 2
			else:
				vis = {'STATUE': 128, 'STATUE_SP': 129, 'REGION_DECO': 130, 'REGION_DECO_ALT': 131,
					   'ALCHEMY': 120, 'BARRICADE': 121, 'HIGH_GRASS': alt(v, 122, (50, 125))}.get(t)
			put(tiles, vis, x, y)

	# features layer
	for y in range(H):
		for x in range(W):
			t = terr[y][x]
			v = var[y][x]
			f = None
			if t == 'GRASS':
				f = alt(v, 132, (50, 133))
			elif t == 'HIGH_GRASS':
				f = alt(v, 128, (50, 129))
			put(feats, f, x, y)

	# markers for characters
	for y in range(H if markers else 0):
		for x in range(W):
			c = rows[y][x]
			if c in MARKERS:
				for dy in range(5, 12):
					for dx in range(5, 11):
						img.putpixel((x * 16 + dx, y * 16 + dy), MARKERS[c] + (255,))

	# raised terrain (over characters)
	for y in range(H):
		for x in range(W):
			if terr[y][x] == 'HIGH_GRASS':
				put(raised, alt(var[y][x], 0, (50, 2)), x, y, 4)

	# walls layer
	for y in range(H):
		for x in range(W):
			t = terr[y][x]
			below = at(x, y + 1) if y + 1 < H else 'WALL'
			vis = None
			if t in WALLISH:
				if below not in WALLISH:
					if below == 'DOOR':
						vis = 227
				else:
					base = 176 if (t == 'BOOKSHELF' or below == 'BOOKSHELF') else 144
					if at(x + 1, y) not in WALLISH: base += 1
					if at(x + 1, y + 1) not in WALLISH: base += 2
					if at(x - 1, y + 1) not in WALLISH: base += 4
					if at(x - 1, y) not in WALLISH: base += 8
					vis = base
			else:
				if below in WALLISH:
					if t == 'DOOR':
						vis = 212
					elif below == 'BOOKSHELF':
						vis = 200
					else:
						vis = 192
					if at(x + 1, y + 1) not in WALLISH: vis += 1
					if at(x - 1, y + 1) not in WALLISH: vis += 2
				elif below == 'DOOR':
					vis = 224
				else:
					vis = {'STATUE': 240, 'STATUE_SP': 241, 'REGION_DECO': 242, 'REGION_DECO_ALT': 243,
						   'ALCHEMY': 232, 'BARRICADE': 233}.get(below)
					if below == 'HIGH_GRASS':
						vis = alt(var[y + 1][x], 234, (50, 237))
			put(tiles, vis, x, y)

	return img.resize((img.width * scale, img.height * scale), Image.NEAREST)


def main(out, scale=2):
	render(scale).save(out)


if __name__ == '__main__':
	main(sys.argv[1], int(sys.argv[2]) if len(sys.argv) > 2 else 2)
