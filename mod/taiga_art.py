# MOD (taiga town): generates every texture of the floor 0 taiga town into core/src/main/assets/taiga/.
#
#   python mod/taiga_art.py            (needs Pillow)
#
# Tiles follow the layouts of the original sheets (see tiles/DungeonTileSheet.java,
# TerrainFeaturesTilemap.java, RaisedTerrainTilemap.java), starting from copies of the sewer sheets,
# so that every slot the game can ask for exists. Characters and items are drawn below as pixel grids.

import os
import random
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = os.path.join(ROOT, 'core', 'src', 'main', 'assets')
OUT = os.path.join(ASSETS, 'taiga')

T = (0, 0, 0, 0)


def rgb(h, a=255):
	return ((h >> 16) & 255, (h >> 8) & 255, h & 255, a)


# ---------------------------------------------------------------- palette

SNOW = rgb(0xdbe5ee)
SNOW_HI = rgb(0xeef4f9)
SNOW_SPARK = rgb(0xffffff)
SNOW_SH = rgb(0xc3d2df)
SNOW_SH2 = rgb(0xa9bccd)
SNOW_DEEP = rgb(0x8ea5b9)

NEEDLE_O = rgb(0x10241f)
NEEDLE_D = rgb(0x1b3a31)
NEEDLE_M = rgb(0x2a5546)
NEEDLE_L = rgb(0x3d7259)
NEEDLE_H = rgb(0x568c6c)

WOOD = [rgb(0x34211a), rgb(0x4e3222), rgb(0x6b4630), rgb(0x8a5d3c), rgb(0xa8784c), rgb(0xc6996a)]
LOG_END = [rgb(0x7a5434), rgb(0xb48a5a), rgb(0xd2ad7c), rgb(0xe6c99c)]

STONE = [rgb(0x3c434d), rgb(0x5a636e), rgb(0x7b8590), rgb(0x9da6b0), rgb(0xbcc4cc)]
ICE_EDGE = rgb(0xa9cfe3)
WATER_D = rgb(0x3f6f93)
WATER_M = rgb(0x5a8db3)
WATER_L = rgb(0x84b5d6)
WATER_H = rgb(0xb4d8ec)

FIRE = [rgb(0xb8321a), rgb(0xe8641e), rgb(0xffa733), rgb(0xffd66b), rgb(0xfff3c4)]
GRASS_D = rgb(0x7d6a3a)
GRASS_M = rgb(0xa88f4f)
GRASS_L = rgb(0xcdb574)

SHADOW = rgb(0x1a2a2c)


# ---------------------------------------------------------------- helpers

def new(w=16, h=16):
	return Image.new('RGBA', (w, h), T)


def px(img, x, y, c):
	if 0 <= x < img.width and 0 <= y < img.height and c is not None:
		img.putpixel((x, y), c)


def get(img, x, y):
	if 0 <= x < img.width and 0 <= y < img.height:
		return img.getpixel((x, y))
	return T


def paste(dst, src, x=0, y=0):
	dst.alpha_composite(src, (x, y))


def outline(img, color, diag=False):
	"""Adds an outline around the opaque pixels of img (in place)."""
	src = img.copy()
	n = [(1, 0), (-1, 0), (0, 1), (0, -1)]
	if diag:
		n += [(1, 1), (-1, -1), (1, -1), (-1, 1)]
	for y in range(img.height):
		for x in range(img.width):
			if src.getpixel((x, y))[3] == 0:
				for dx, dy in n:
					if get(src, x + dx, y + dy)[3] > 0:
						img.putpixel((x, y), color)
						break
	return img


def lum(c):
	return (c[0] * 299 + c[1] * 587 + c[2] * 114) / 1000


def sat(c):
	return max(c[:3]) - min(c[:3])


def ramp(colors, t):
	t = max(0.0, min(0.999, t))
	return colors[int(t * len(colors))]


def recolor_greys(img, colors, max_sat=40, lo=20, hi=230):
	"""Maps low-saturation (stone) pixels onto a colour ramp by brightness; keeps coloured pixels."""
	out = img.copy()
	for y in range(img.height):
		for x in range(img.width):
			c = img.getpixel((x, y))
			if c[3] > 0 and sat(c) <= max_sat:
				t = (lum(c) - lo) / (hi - lo)
				out.putpixel((x, y), ramp(colors, t))
	return out


class Sheet:
	def __init__(self, path_or_size, tile=16):
		if isinstance(path_or_size, str):
			self.img = Image.open(path_or_size).convert('RGBA')
		else:
			self.img = Image.new('RGBA', path_or_size, T)
		self.tile = tile
		self.cols = self.img.width // tile

	def box(self, i):
		x, y = (i % self.cols) * self.tile, (i // self.cols) * self.tile
		return (x, y, x + self.tile, y + self.tile)

	def get(self, i):
		return self.img.crop(self.box(i))

	def set(self, i, tile):
		b = self.box(i)
		self.img.paste(Image.new('RGBA', (self.tile, self.tile), T), b)
		self.img.paste(tile, b[:2])

	def save(self, path):
		self.img.save(path)


# ---------------------------------------------------------------- ground

def snow(seed, drifts=2):
	r = random.Random(seed)
	img = Image.new('RGBA', (16, 16), SNOW)
	for y in range(16):
		for x in range(16):
			v = r.random()
			if v < 0.10:
				px(img, x, y, SNOW_HI)
			elif v < 0.19:
				px(img, x, y, SNOW_SH)
			elif v < 0.205:
				px(img, x, y, SNOW_SPARK)
	for _ in range(drifts):
		x0, y0, ln = r.randint(-2, 12), r.randint(2, 14), r.randint(4, 8)
		for i in range(ln):
			px(img, x0 + i, y0, SNOW_SH)
			px(img, x0 + i, y0 - 1, SNOW_HI)
		px(img, x0 + ln, y0, SNOW_SH2)
	return img


def tracks(img, seed):
	r = random.Random(seed)
	x, y = r.randint(1, 4), 14
	while y > 0:
		px(img, x, y, SNOW_SH2)
		px(img, x + 2, y - 1, SNOW_SH2)
		x += 2
		y -= 3
		x %= 14
	return img


def rock(img, cx, cy, w=4):
	for x in range(cx, cx + w):
		px(img, x, cy, STONE[2])
		px(img, x, cy + 1, STONE[1])
	px(img, cx, cy + 1, STONE[0])
	px(img, cx + w - 1, cy + 1, STONE[0])
	for x in range(cx + 1, cx + w - 1):
		px(img, x, cy - 1, SNOW_HI)
	px(img, cx + 1, cy, STONE[3])
	for x in range(cx, cx + w):
		px(img, x, cy + 2, SNOW_SH2)
	return img


def twigs(img, seed):
	r = random.Random(seed)
	for _ in range(2):
		x, y = r.randint(1, 11), r.randint(3, 13)
		dx = r.choice([1, -1])
		for i in range(4):
			px(img, x + i, y + (i * dx) // 2, WOOD[2])
		px(img, x + 1, y + dx - 1, WOOD[3])
	# pine cone
	x, y = r.randint(3, 11), r.randint(4, 12)
	px(img, x, y, WOOD[3]); px(img, x + 1, y, WOOD[2])
	px(img, x, y + 1, WOOD[2]); px(img, x + 1, y + 1, WOOD[1])
	px(img, x, y + 2, SNOW_SH2); px(img, x + 1, y + 2, SNOW_SH2)
	return img


def dry_grass_base(seed):
	r = random.Random(seed)
	img = snow(seed, 1)
	for _ in range(5):
		x, y = r.randint(0, 14), r.randint(1, 14)
		px(img, x, y, GRASS_M)
		px(img, x + 1, y, GRASS_D)
		px(img, x, y - 1, GRASS_L)
	return img


def grass_blades(seed):
	r = random.Random(seed)
	img = new()
	for _ in range(7):
		x, y = r.randint(0, 15), r.randint(5, 15)
		h = r.randint(2, 4)
		lean = r.choice([0, 1, -1])
		for i in range(h):
			c = GRASS_L if i == h - 1 else (GRASS_M if i > 0 else GRASS_D)
			px(img, x + (lean if i >= h - 1 else 0), y - i, c)
		if r.random() < 0.4:
			px(img, x + lean, y - h, SNOW_HI)
	return img


def planks(seed, rug=False):
	r = random.Random(seed)
	img = new()
	rows = [WOOD[4], WOOD[3], WOOD[3], WOOD[1]]
	for y in range(16):
		for x in range(16):
			c = rows[y % 4]
			if y % 4 in (1, 2) and r.random() < 0.08:
				c = WOOD[2]
			img.putpixel((x, y), c)
	# staggered butt joints
	for band in range(4):
		jx = (band * 7 + 3) % 16
		for y in range(band * 4, band * 4 + 3):
			px(img, jx, y, WOOD[1])
	for _ in range(2):
		x, y = r.randint(1, 14), r.randint(0, 3) * 4 + 1
		px(img, x, y, WOOD[1]); px(img, x + 1, y, WOOD[2])
	if rug:
		for y in range(4, 13):
			for x in range(3, 13):
				c = rgb(0x8c2f2f) if (x + y) % 4 else rgb(0xc9a14a)
				if x in (3, 12) or y in (4, 12):
					c = rgb(0x5e1f22)
				px(img, x, y, c)
	return img


def mine_exit():
	img = snow(17, 1)
	# dark shaft
	for y in range(3, 14):
		for x in range(3, 13):
			d = min(x - 3, 12 - x, y - 3, 13 - y)
			px(img, x, y, rgb(0x0b0e14) if d > 1 else rgb(0x1b2129))
	# timber frame
	for x in range(2, 14):
		px(img, x, 2, WOOD[4]); px(img, x, 3, WOOD[2])
		px(img, x, 13, WOOD[3]); px(img, x, 14, WOOD[1])
	for y in range(2, 15):
		px(img, 2, y, WOOD[3]); px(img, 13, y, WOOD[2])
	for x in range(2, 14, 3):
		px(img, x, 1, SNOW_HI)
	px(img, 2, 2, LOG_END[2]); px(img, 13, 2, LOG_END[2]); px(img, 2, 14, LOG_END[1]); px(img, 13, 14, LOG_END[1])
	# ladder going down
	for y in range(4, 13):
		px(img, 6, y, WOOD[3]); px(img, 9, y, WOOD[3])
	for y in range(5, 13, 2):
		for x in range(6, 10):
			px(img, x, y, WOOD[4] if y < 9 else WOOD[2])
	# The mountain's mine is barred until the summit key is recovered.
	for x in (4, 7, 10, 12):
		for y in range(4, 14): px(img, x, y, rgb(0x7d8990) if x % 2 else rgb(0x4d5962))
	for y in (6, 11):
		for x in range(3, 13): px(img, x, y, rgb(0x9ca8ad) if x % 3 else rgb(0x5d6971))
	for y in range(7, 10):
		for x in range(7, 10): px(img, x, y, rgb(0x8b8072))
	# snow lip
	for x in range(3, 13, 2):
		px(img, x, 4, SNOW_SH2)
	return img


def water_edge(mask, seed):
	"""+1 ground above, +2 right, +4 below, +8 left: snowy banks over the stream."""
	r = random.Random(seed)
	jit = [[r.randint(0, 1) for _ in range(16)] for _ in range(4)]
	img = new()
	for y in range(16):
		for x in range(16):
			best = None
			for side, bit, d, along in ((0, 1, y, x), (1, 2, 15 - x, y), (2, 4, 15 - y, x), (3, 8, x, y)):
				if mask & bit:
					t = 3 + jit[side][along] - d
					if best is None or t > best:
						best = t
			if best is None or best < 0:
				continue
			if best == 0:
				c = ICE_EDGE
			elif best == 1:
				c = SNOW_SH2
			elif best == 2:
				c = SNOW_SH
			else:
				c = SNOW if r.random() > 0.15 else SNOW_HI
			px(img, x, y, c)
	return img


def water_texture():
	r = random.Random(77)
	img = Image.new('RGBA', (32, 32), WATER_M)
	for y in range(32):
		for x in range(32):
			v = r.random()
			if v < 0.12:
				px(img, x, y, WATER_D)
			elif v < 0.2:
				px(img, x, y, rgb(0x6a9cc0))
	# ripples
	for _ in range(14):
		x, y, ln = r.randint(0, 31), r.randint(0, 31), r.randint(2, 5)
		for i in range(ln):
			px(img, (x + i) % 32, y, WATER_L)
		px(img, (x + ln) % 32, y, WATER_H)
	# small ice floes
	for _ in range(4):
		x, y = r.randint(0, 31), r.randint(0, 31)
		w, h = r.randint(3, 5), r.randint(2, 3)
		for j in range(h):
			for i in range(w):
				if (i, j) in ((0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)):
					continue
				px(img, (x + i) % 32, (y + j) % 32, SNOW_HI if j == 0 else SNOW_SH)
		for i in range(1, w - 1):
			px(img, (x + i) % 32, (y + h) % 32, WATER_D)
	return img


# ---------------------------------------------------------------- forest (walls)

def canopy_texture(seed):
	"""Tops of snowy spruces seen from above, wraps around so that tiles join seamlessly."""
	r = random.Random(seed)
	img = Image.new('RGBA', (16, 16), NEEDLE_O)
	crowns = [(4, 4, 4), (12, 3, 3), (0, 11, 4), (8, 12, 4), (13, 9, 2)]
	for cx, cy, rad in sorted(crowns, key=lambda c: c[1]):
		for dy in range(-rad, rad + 1):
			for dx in range(-rad, rad + 1):
				d2 = dx * dx + dy * dy
				if d2 > rad * rad + 1:
					continue
				x, y = (cx + dx) % 16, (cy + dy) % 16
				# star shaped crown: needles reach further along the axes
				if d2 > rad * rad - 1 and dx != 0 and dy != 0:
					continue
				if dy <= -rad + 1 and abs(dx) <= 1:
					c = SNOW_HI
				elif dy < 0 and abs(dx) <= -dy - 1:
					c = SNOW_SH if r.random() < 0.6 else SNOW_HI
				elif dy < 1:
					c = NEEDLE_L if dx < 0 else NEEDLE_M
				elif dy < rad - 1:
					c = NEEDLE_M if dx < 0 else NEEDLE_D
				else:
					c = NEEDLE_D
				img.putpixel((x, y), c)
	return img


CANOPY = canopy_texture(5)
ROOF_SNOW = None


def roof_texture():
	"""Snow lying on a plank roof: bluish snow with the boards showing through in lines."""
	r = random.Random(9)
	img = Image.new('RGBA', (16, 16), SNOW_SH)
	for y in range(16):
		for x in range(16):
			if y % 4 == 3:
				px(img, x, y, WOOD[2] if r.random() < 0.55 else SNOW_SH2)
			elif y % 4 == 0:
				px(img, x, y, SNOW if r.random() < 0.7 else SNOW_HI)
			elif r.random() < 0.08:
				px(img, x, y, SNOW)
	return img


ROOF_SNOW = roof_texture()


def top_from_mask(src, interior, rim_colors, outline_color, inner_color=None):
	"""Wall tops: black source pixels become 'interior', the other opaque pixels become a rim
	(rim_colors None: the rim is drawn with the interior texture too)."""
	img = new()
	for y in range(16):
		for x in range(16):
			c = src.getpixel((x, y))
			if c[3] == 0:
				continue
			if lum(c) < 12 or rim_colors is None:
				img.putpixel((x, y), interior.getpixel((x, y)))
			else:
				img.putpixel((x, y), ramp(rim_colors, (lum(c) - 40) / 190))
	# dark line where the mass ends (and optionally a second, inner line)
	for color in (outline_color, inner_color):
		if color is None:
			continue
		src2 = img.copy()
		edge = outline_color if color is inner_color else None
		for y in range(16):
			for x in range(16):
				cur = src2.getpixel((x, y))
				if cur[3] > 0 and cur != edge:
					for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
						nx, ny = x + dx, y + dy
						if 0 <= nx < 16 and 0 <= ny < 16:
							n = src2.getpixel((nx, ny))
							if n[3] == 0 or (edge is not None and n == edge):
								img.putpixel((x, y), color)
								break
	return img


def forest_face(seed, open_right, open_left, big=False):
	r = random.Random(seed)
	img = Image.new('RGBA', (16, 16), SHADOW)
	# distant trunks in the gloom
	for x in range(r.randint(0, 2), 16, 5):
		for y in range(3, 13):
			px(img, x, y, rgb(0x253435))
	# trunks
	xs = [3, 11] if not big else [6]
	for tx in xs:
		tx += r.randint(-1, 1)
		w = 2 if not big else 4
		for y in range(2, 14):
			for i in range(w):
				c = WOOD[3] if i == 0 else (WOOD[2] if i < w - 1 else WOOD[1])
				if r.random() < 0.08:
					c = WOOD[1]
				px(img, tx + i, y, c)
		px(img, tx - 1, 13, WOOD[1]); px(img, tx + w, 13, WOOD[1])
	# canopy underside with hanging branch tips
	for x in range(16):
		ln = r.randint(3, 6)
		for y in range(ln):
			c = NEEDLE_D if y < ln - 1 else NEEDLE_M
			if y == ln - 1 and r.random() < 0.35:
				c = SNOW_SH
			px(img, x, y, c)
	# snow at the foot of the trees
	for x in range(16):
		top = 13 + r.randint(0, 1)
		for y in range(top, 16):
			px(img, x, y, SNOW_SH if y == top else SNOW_SH2)
	if open_right:
		for y in range(16):
			px(img, 15, y, NEEDLE_O if y < 12 else SNOW_DEEP)
	if open_left:
		for y in range(16):
			px(img, 0, y, NEEDLE_O if y < 12 else SNOW_DEEP)
	return img


# ---------------------------------------------------------------- cabins (bookshelf walls)

def log_face(seed, open_right, open_left, window=False, lintel=False):
	r = random.Random(seed)
	img = new()
	shades = [WOOD[4], WOOD[3], WOOD[2], WOOD[0]]
	for y in range(16):
		for x in range(16):
			c = shades[y % 4]
			if y % 4 in (1, 2) and r.random() < 0.07:
				c = WOOD[1]
			img.putpixel((x, y), c)
	# log ends where the wall stops
	for side, on in ((15, open_right), (0, open_left)):
		if not on:
			continue
		for band in range(4):
			y0 = band * 4
			inner = side - 1 if side == 15 else side + 1
			for y in range(y0, y0 + 3):
				px(img, side, y, LOG_END[2])
				px(img, inner, y, LOG_END[1])
			px(img, side, y0 + 1, LOG_END[3])
			px(img, side, y0 + 3, WOOD[0])
	if window:
		for y in range(4, 12):
			for x in range(4, 12):
				edge = x in (4, 11) or y in (4, 11)
				px(img, x, y, WOOD[1] if edge else rgb(0xffc864))
		for x in range(5, 11):
			px(img, x, 7, WOOD[1])
		for y in range(5, 11):
			px(img, 7, y, WOOD[1])
		for x, y in ((5, 5), (6, 5), (5, 6), (10, 10), (9, 10), (10, 9)):
			px(img, x, y, rgb(0xdcecf6))
		px(img, 9, 5, rgb(0xfff1c0))
		for x in range(3, 13):
			px(img, x, 12, WOOD[4])
			px(img, x, 13, SNOW)
	if lintel:
		for x in range(16):
			px(img, x, 12, WOOD[1])
			px(img, x, 13, WOOD[4])
	# icicles under the roof
	for x in range(1, 15, 3):
		x2 = x + r.randint(0, 1)
		for y in range(r.randint(1, 3)):
			px(img, x2, y, rgb(0xcfe8f5) if y == 0 else rgb(0xa9d3ea))
	return img


# ---------------------------------------------------------------- objects

def pine(seed=0, deco=False, w=13, h=27):
	"""Returns a 16x32 image: rows 0-15 go into the overhang tile above, 16-31 into the object tile."""
	r = random.Random(seed)
	img = new(16, 32)
	base = 29
	top = base - h
	cx = 7.5
	# trunk
	for y in range(base - 4, base + 1):
		px(img, 7, y, WOOD[3]); px(img, 8, y, WOOD[1])
	tiers = 4
	tier_tops = [top + i * (h - 6) // tiers for i in range(tiers)]
	for t in reversed(range(tiers)):
		y0 = tier_tops[t]
		y1 = (tier_tops[t + 1] + 3) if t + 1 < tiers else base - 3
		maxw = 2 + (w / 2 - 1.5) * (t + 1) / tiers
		for y in range(y0, y1 + 1):
			hw = min(maxw, 0.6 + (y - y0) * 0.9)
			for x in range(16):
				dx = x + 0.5 - cx - 0.5
				if abs(dx) <= hw:
					if y == y1 and int(abs(dx)) % 2 == 1 and abs(dx) > hw - 2:
						continue
					c = NEEDLE_M if dx < 0 else NEEDLE_D
					if dx < -hw + 1.5 and y < y1:
						c = NEEDLE_L
					if abs(dx) > hw - 1.2 and y < y1 - 1:
						c = SNOW_HI if dx < 0 else SNOW_SH
					if y == y1:
						c = NEEDLE_D
					px(img, x, y, c)
		# snow lying on top of the tier
		for x in range(16):
			dx = x - cx
			if abs(dx) < maxw - 1 and r.random() < 0.55:
				yy = int(y0 + (abs(dx) - 0.6) / 0.9) if abs(dx) > 0.6 else y0
				px(img, x, max(y0, yy), SNOW_HI)
	if deco:
		colors = [rgb(0xff5050), rgb(0xffd94a), rgb(0x5ab4ff), rgb(0xff8ef0)]
		for i in range(12):
			y = top + 5 + i * 2
			if y > base - 4:
				break
			x = int(cx + r.uniform(-1, 1) * min(5, 1 + (y - top) * 0.3))
			if get(img, x, y)[3] > 0:
				px(img, x, y, colors[i % 4])
		# star
		sy = top - 2
		for x, y in ((7, sy), (8, sy), (6, sy + 1), (7, sy + 1), (8, sy + 1), (9, sy + 1), (7, sy + 2), (8, sy + 2)):
			px(img, x, y, rgb(0xffe66b))
	outline(img, NEEDLE_O)
	# soft shadow on the snow
	for x in range(3, 13):
		if get(img, x, base + 1)[3] == 0:
			px(img, x, base + 1, SNOW_SH2)
	return img


def split(tall):
	return tall.crop((0, 0, 16, 16)), tall.crop((0, 16, 16, 32))


def on_snow(obj, seed):
	img = snow(seed, 1)
	paste(img, obj)
	return img


def bush(seed, trampled=False):
	"""16x24: rows 0-7 overhang, 8-23 object tile."""
	r = random.Random(seed)
	img = new(16, 24)
	if trampled:
		for _ in range(9):
			x, y = r.randint(1, 13), r.randint(17, 22)
			for i in range(3):
				px(img, x + i, y, NEEDLE_M if i else NEEDLE_D)
			px(img, x + 1, y - 1, SNOW_HI)
		for _ in range(4):
			x, y = r.randint(1, 13), r.randint(16, 22)
			px(img, x, y, WOOD[2]); px(img, x + 1, y + 1, WOOD[2])
		return img
	blobs = [(4, 15, 4), (11, 14, 4), (7, 11, 5), (3, 19, 3), (12, 19, 3), (8, 18, 4)]
	for cx, cy, rad in blobs:
		cx += r.randint(-1, 1)
		for dy in range(-rad, rad + 1):
			for dx in range(-rad, rad + 1):
				if dx * dx + dy * dy <= rad * rad:
					c = NEEDLE_L if dx + dy < -rad * 0.4 else (NEEDLE_M if dx + dy < rad * 0.5 else NEEDLE_D)
					px(img, cx + dx, cy + dy, c)
	# snow caps
	for x in range(16):
		for y in range(24):
			if get(img, x, y)[3] > 0:
				if get(img, x, y - 1)[3] == 0 and r.random() < 0.8:
					px(img, x, y, SNOW_HI)
					if r.random() < 0.5:
						px(img, x, y + 1, SNOW_SH)
				break
	for _ in range(6):
		x, y = r.randint(2, 13), r.randint(9, 20)
		if get(img, x, y)[3] > 0:
			px(img, x, y, SNOW_SH)
	outline(img, NEEDLE_O)
	return img


def palisade():
	"""16x24: rows 0-7 overhang (pointed tops), 8-23 object tile."""
	img = new(16, 24)
	for i in range(4):
		x0 = i * 4
		top = 3 if i % 2 == 0 else 4
		for y in range(top, 24):
			for dx in range(4):
				if y - top < 2 and dx in ((0, 3) if y == top + 1 else (0, 1, 3) if y == top else ()):
					continue
				c = [WOOD[4], WOOD[3], WOOD[2], WOOD[0]][dx]
				px(img, x0 + dx, y, c)
		px(img, x0 + 1, top - 1, SNOW_HI)
		px(img, x0 + 2, top - 1, SNOW_HI)
		px(img, x0 + 1, top, SNOW_HI); px(img, x0 + 2, top, SNOW_SH)
	# binding ropes
	for y in (10, 18):
		for x in range(16):
			px(img, x, y, rgb(0x8f7a55) if x % 2 else rgb(0x6d5a3c))
	for x in range(16):
		px(img, x, 22, SNOW_SH)
		px(img, x, 23, SNOW_SH2)
	return img


def woodpile():
	"""16x24: rows 0-7 overhang, 8-23 object tile. Logs seen from the end."""
	img = new(16, 24)

	def log_end(cx, cy):
		for dy in range(-2, 2):
			for dx in range(-2, 2):
				if (dx, dy) in ((-2, -2), (1, -2), (-2, 1), (1, 1)):
					continue
				ring = max(abs(dx + 0.5), abs(dy + 0.5))
				px(img, cx + dx, cy + dy, LOG_END[3] if ring < 1 else LOG_END[1])
		px(img, cx - 1, cy - 1, LOG_END[2])

	for cy, xs in ((20, (3, 7, 11, 15)), (16, (5, 9, 13)), (12, (7, 11))):
		for cx in xs:
			log_end(cx - 1, cy)
	outline(img, WOOD[0])
	for x in range(5, 12):
		px(img, x, 9, SNOW_HI)
		px(img, x, 10, SNOW_HI if x % 3 else SNOW_SH)
	for x in range(1, 15):
		px(img, x, 23, SNOW_SH2)
	return img


def campfire():
	"""16x24: rows 0-7 overhang (flames), 8-23 object tile."""
	img = new(16, 24)
	# stone ring
	for cx, cy in ((2, 20), (5, 22), (10, 22), (13, 20), (3, 17), (12, 17)):
		for dx in range(2):
			for dy in range(2):
				px(img, cx + dx, cy + dy, STONE[3] if dy == 0 else STONE[1])
	# crossed logs
	for i in range(9):
		px(img, 4 + i, 20 - i // 3, WOOD[3])
		px(img, 12 - i, 20 - i // 3, WOOD[2])
	# flames
	flame = [
		"......4.......",
		".....43.......",
		".....433..4...",
		"....4332..3...",
		"...43221.32...",
		"...322110221..",
		"..42211002114.",
		"..32110000123.",
		"...211000112..",
		"....2111112...",
	]
	for j, row in enumerate(flame):
		for i, ch in enumerate(row):
			if ch != '.':
				px(img, 1 + i, 8 + j, FIRE[4 - int(ch)])
	for x in range(6, 11):
		px(img, x, 19, FIRE[0])
	return img


def cauldron():
	"""16x24: rows 0-7 overhang (steam), 8-23 object tile. The herbalist's iron pot over embers."""
	img = new(16, 24)
	pot = [
		"..oooooooooo..",
		".oGgggggggggo.",
		".ogGGGGGGGGgo.",
		"oIIIIIIIIIIIIo",
		"oIiiiiiiiiiiIo",
		"oIiiiiiiiiiIIo",
		".oIiiiiiiiIIo.",
		".oIIiiiiiIIIo.",
		"..oIIIIIIIIo..",
		"...oooooooo...",
	]
	cols = {'o': rgb(0x15181c), 'I': rgb(0x2c3138), 'i': rgb(0x434b55), 'G': rgb(0x5fbf4a), 'g': rgb(0x93e06a)}
	for j, row in enumerate(pot):
		for i, ch in enumerate(row):
			if ch != '.':
				px(img, 1 + i, 11 + j, cols[ch])
	px(img, 4, 15, rgb(0x6b7480)); px(img, 5, 15, rgb(0x6b7480))
	# legs and embers
	for x in (3, 12):
		px(img, x, 21, rgb(0x15181c)); px(img, x, 22, rgb(0x15181c))
	for x in range(5, 11):
		px(img, x, 22, FIRE[1] if x % 2 else FIRE[2])
		px(img, x, 21, FIRE[0] if x % 3 == 0 else T)
	# bubbles and steam
	for x, y in ((5, 11), (9, 10), (7, 9)):
		px(img, x, y, rgb(0xb6f08e))
	for x, y in ((6, 6), (7, 5), (8, 3), (7, 2), (9, 6), (10, 4)):
		px(img, x, y, rgb(0xe9f2f6))
	for x in range(2, 14):
		px(img, x, 23, SNOW_SH2)
	return img


def trail_up():
	"""Stone steps cut into a snowy slope, leading up (the way up the mountain)."""
	img = snow(16, 1)
	for i, y in enumerate((11, 7, 3)):
		w0, w1 = 3 + i, 12 - i
		for x in range(w0, w1 + 1):
			px(img, x, y, STONE[3])
			px(img, x, y + 1, STONE[2])
			px(img, x, y + 2, STONE[1])
			px(img, x, y - 1, SNOW_HI)
		px(img, w0, y + 1, STONE[0]); px(img, w1, y + 1, STONE[0])
	# a trail marker: a post with a red ribbon
	for y in range(2, 13):
		px(img, 14, y, WOOD[3] if y % 3 else WOOD[2])
	px(img, 13, 3, rgb(0xd83a3a)); px(img, 12, 4, rgb(0xb02828)); px(img, 13, 4, rgb(0xd83a3a))
	px(img, 14, 1, SNOW_HI)
	return img


def trail_down():
	"""The trail going down the slope, into the shade of the trees (the way back down)."""
	img = snow(18, 1)
	for y in range(16):
		for x in range(16):
			d = abs(x - 7.5)
			if d < 3 + y * 0.2:
				shade = [SNOW_SH, SNOW_SH, SNOW_SH2, SNOW_SH2, SNOW_DEEP][min(4, y // 4)]
				px(img, x, y, shade)
	for i, y in enumerate((3, 7, 11)):
		for x in range(5 - i, 11 + i):
			px(img, x, y, STONE[2] if y < 10 else STONE[1])
			px(img, x, y + 1, STONE[0])
	for x, y in ((6, 1), (9, 2), (6, 5), (9, 9), (6, 13)):
		px(img, x, y, SNOW_DEEP)
	return img


# ---------------------------------------------------------------- the town tile sheet

def build_tiles():
	src = Sheet(os.path.join(ASSETS, 'environment', 'tiles_sewers.png'))
	s = Sheet(os.path.join(ASSETS, 'environment', 'tiles_sewers.png'))

	# ground
	s.set(0, snow(0))
	s.set(6, snow(6, 3))
	s.set(12, rock(snow(12, 1), 5, 7))
	s.set(1, twigs(snow(1), 1))
	s.set(7, tracks(snow(7, 1), 7))
	g = dry_grass_base(2)
	s.set(2, g)
	s.set(8, dry_grass_base(8))
	s.set(4, planks(4))
	s.set(10, planks(10))
	s.set(17, mine_exit())
	s.set(16, trail_up())

	# stream banks
	for m in range(16):
		s.set(32 + m, water_edge(m, 300 + m))

	# forest
	for i, (r_, l_) in enumerate(((0, 0), (1, 0), (0, 1), (1, 1))):
		s.set(80 + i, forest_face(80 + i, r_, l_))
		s.set(96 + i, forest_face(96 + i, r_, l_))
		s.set(84 + i, forest_face(84 + i, r_, l_, big=True))
		s.set(100 + i, forest_face(100 + i, r_, l_, big=True))
		# cabins
		s.set(92 + i, log_face(92 + i, r_, l_))
		s.set(108 + i, log_face(108 + i, r_, l_, window=True))
		s.set(88 + i, log_face(88 + i, r_, l_, lintel=True))
		s.set(104 + i, log_face(104 + i, r_, l_, lintel=True))

	for i in list(range(144, 176)) + list(range(192, 200)):
		s.set(i, top_from_mask(src.get(i), CANOPY, None, rgb(0x0a1714), NEEDLE_D))
	for i in list(range(176, 192)) + list(range(200, 204)):
		s.set(i, top_from_mask(src.get(i), ROOF_SNOW, WOOD[2:], WOOD[0]))

	# doors and their overhangs: stone frames become timber
	for i in [56, 57, 58, 59, 60, 61, 112, 113, 114, 115, 116] + list(range(204, 232)):
		s.set(i, recolor_greys(src.get(i), WOOD))

	flat_canopy = top_from_mask(Image.new('RGBA', (16, 16), (0, 0, 0, 255)), CANOPY, None, None)
	for i in (48, 49, 52, 53):
		s.set(i, flat_canopy)
	s.set(50, log_face(50, 1, 1))
	s.set(54, log_face(54, 1, 1, window=True))

	# objects: raised tile (lower part, on snow), overhang (upper part), flat icon
	tree_top, tree_low = split(pine(1))
	s.set(128, on_snow(tree_low, 128))
	s.set(240, tree_top)
	s.set(72, on_snow(tree_low, 72))

	sp_top, sp_low = split(pine(2, deco=True))
	s.set(129, on_snow(sp_low, 129))
	s.set(241, sp_top)
	s.set(73, on_snow(sp_low, 73))

	def tall24(img, raised, over, flat, seed):
		over_t = new(); paste(over_t, img.crop((0, 0, 16, 8)), 0, 8)
		low = img.crop((0, 8, 16, 24))
		s.set(raised, on_snow(low, seed))
		s.set(over, over_t)
		if flat is not None:
			s.set(flat, on_snow(low, seed + 1))
		return low

	tall24(woodpile(), 130, 242, 74, 130)
	tall24(campfire(), 131, 243, 75, 131)
	tall24(palisade(), 121, 233, 65, 121)
	b1 = bush(122)
	b2 = bush(125)
	tall24(b1, 122, 234, 66, 122)
	tall24(b2, 125, 237, 69, 125)
	tall24(bush(123, True), 123, 235, 67, 123)
	tall24(bush(126, True), 126, 238, 70, 126)

	tall24(cauldron(), 120, 232, 64, 120)

	return s, (b1, b2)


def build_features(bushes):
	s = Sheet(os.path.join(ASSETS, 'environment', 'terrain_features.png'))
	b1, b2 = bushes
	s.set(128, b1.crop((0, 8, 16, 24)))
	s.set(129, b2.crop((0, 8, 16, 24)))
	s.set(130, bush(123, True).crop((0, 8, 16, 24)))
	s.set(131, bush(126, True).crop((0, 8, 16, 24)))
	s.set(132, grass_blades(132))
	s.set(133, grass_blades(133))
	# palisade, cauldron, trees, woodpile and campfire are fully drawn by the tile sheet
	for i in range(134, 139):
		s.set(i, new())
	return s


def build_raised(bushes):
	s = Sheet(os.path.join(ASSETS, 'environment', 'raised_terrain.png'))
	b1, b2 = bushes
	# front leaves that cover the lower half of someone standing in a bush
	for slot, b in ((0, b1), (2, b2)):
		t = new()
		paste(t, b.crop((0, 16, 16, 24)), 0, 8)
		s.set(slot, t)
	for slot in (1, 3):
		s.set(slot, new())
	return s


def splash():
	"""Loading screen picture: the village itself, like the other regions show their own floors."""
	import town_preview
	town = town_preview.render(1, markers=False)
	# the square, the cabins and the palisade
	crop = town.crop((16 * 16, 2 * 16, 16 * 16 + 480, 2 * 16 + 270))
	img = crop.resize((960, 540), Image.NEAREST).convert('RGBA')
	shade = Image.new('RGBA', img.size, (0, 0, 0, 0))
	px_ = shade.load()
	for y in range(img.height):
		for x in range(img.width):
			dx = (x - img.width / 2) / (img.width / 2)
			dy = (y - img.height / 2) / (img.height / 2)
			d = min(1.0, (dx * dx + dy * dy) ** 0.5)
			px_[x, y] = (8, 14, 20, int(200 * max(0.0, d - 0.45) / 0.55))
	img.alpha_composite(shade)
	return img


if __name__ == '__main__':
	os.makedirs(OUT, exist_ok=True)
	tiles, bushes = build_tiles()
	tiles.save(os.path.join(OUT, 'tiles.png'))
	# the taiga floors: their way down is a trail, not the mine shaft
	tiles.set(17, trail_down())
	tiles.save(os.path.join(OUT, 'tiles_wild.png'))
	build_features(bushes).save(os.path.join(OUT, 'terrain_features.png'))
	build_raised(bushes).save(os.path.join(OUT, 'raised_terrain.png'))
	water_texture().save(os.path.join(OUT, 'water.png'))

	import taiga_sprites
	import taiga_sprites2
	taiga_sprites2.add_items(taiga_sprites.items_sheet()).save(os.path.join(OUT, 'items.png'))
	taiga_sprites2.beasts_sheet().save(os.path.join(OUT, 'beasts.png'))
	taiga_sprites2.spirits_sheet().save(os.path.join(OUT, 'spirits.png'))
	taiga_sprites2.leshy_sheet().save(os.path.join(OUT, 'leshy.png'))
	taiga_sprites.villagers_sheet().save(os.path.join(OUT, 'villagers.png'))
	taiga_sprites.traders_sheet().save(os.path.join(OUT, 'traders.png'))
	taiga_sprites.animals_sheet().save(os.path.join(OUT, 'animals.png'))

	import swamp_art
	swamp_art.build_all()
	import orchard_art
	orchard_art.build_all()

	splash().convert('RGB').save(os.path.join(OUT, 'splash.png'))
	print('taiga art written to', OUT)
