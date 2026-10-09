"""Generate the drowned graveyard atlas, water, monster sprites and item icons.

Run after taiga_art.py. The tile sheet keeps DungeonTileSheet's slot layout, but
its ground, masonry, drowned wood, objects and palette are drawn for the swamp.
"""

import os
import random
from PIL import Image, ImageDraw

from taiga_art import ASSETS, OUT, Sheet
from taiga_sprites import shift, squash, sheet


def darken_sheet(image):
	out = image.copy().convert('RGBA')
	p = out.load()
	for y in range(out.height):
		for x in range(out.width):
			r, g, b, a = p[x, y]
			if a:
				v = int(0.27*r + 0.50*g + 0.23*b)
				p[x, y] = (int(0.42*v + 14), int(0.48*v + 18), int(0.53*v + 23), a)
	return out


def ground(seed, kind=0):
	r = random.Random(seed)
	base = (58, 64, 68) if kind != 4 else (66, 69, 73)
	im = Image.new('RGBA', (16, 16), base + (255,))
	d = ImageDraw.Draw(im)
	for _ in range(72):
		x, y = r.randrange(16), r.randrange(16)
		shade = r.choice([(70, 75, 79), (48, 53, 59), (83, 88, 90), (39, 46, 53)])
		d.point((x, y), fill=shade)
	for _ in range(3):
		x, y = r.randrange(1, 13), r.randrange(1, 14)
		d.line((x, y, x+r.randrange(2, 5), y-1), fill=(112, 121, 126))
		if kind in (1, 2):
			d.point((x+1, y+1), fill=(29, 41, 43))
	if kind in (2, 8):
		for _ in range(3):
			x, y = r.randrange(2, 14), r.randrange(6, 15)
			d.line((x, y, x-1, y-4), fill=(105, 111, 96))
	if kind in (4, 10):
		for y in (4, 9, 14):
			d.line((0, y, 15, y), fill=(37, 41, 45))
			d.line((0, y-1, 15, y-1), fill=(86, 90, 88))
	return im


def wall(seed, wood=False):
	r = random.Random(seed)
	im = Image.new('RGBA', (16, 16), (32, 38, 44, 255))
	d = ImageDraw.Draw(im)
	if wood:
		for x in range(0, 16, 4):
			d.rectangle((x, 0, x+2, 15), fill=r.choice([(50, 47, 45), (61, 57, 54), (74, 68, 62)]))
			d.line((x+3, 0, x+3, 15), fill=(23, 28, 33))
		for y in (5, 11): d.line((0, y, 15, y), fill=(25, 29, 34))
	else:
		for _ in range(8):
			x, y = r.randrange(16), r.randrange(16)
			d.rectangle((x, y, min(x+r.randrange(2, 7), 15), min(y+2, 15)),
				fill=r.choice([(47, 54, 60), (61, 67, 72), (79, 84, 85)]))
		for x in (3, 12):
			d.line((x, 2, x+1, 8, x-1, 15), fill=(16, 24, 29))
	# old ice clinging to the top, with very little clean snow
	for x in range(16):
		if r.randrange(3) != 0: d.point((x, 0), fill=(123, 136, 141))
	return im


def stump(top=False):
	im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
	d = ImageDraw.Draw(im)
	if top:
		d.line((8, 15, 6, 8, 3, 4), fill=(31, 34, 37), width=2)
		d.line((7, 10, 11, 7, 13, 2), fill=(43, 43, 42), width=2)
		d.line((6, 8, 4, 6, 2, 6), fill=(90, 99, 102))
	else:
		d.rectangle((6, 1, 9, 13), fill=(41, 38, 39))
		d.line((7, 2, 7, 12), fill=(88, 85, 78))
		d.line((7, 11, 3, 15), fill=(36, 40, 41), width=2)
		d.line((9, 11, 13, 15), fill=(34, 37, 40), width=2)
	return im


def reeds(seed):
	r = random.Random(seed)
	im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
	d = ImageDraw.Draw(im)
	for _ in range(8):
		x, y = r.randrange(16), r.randrange(9, 16)
		d.line((x, y, x+r.choice([-1, 0, 1]), y-r.randrange(3, 8)), fill=(95, 101, 91))
		d.point((x, y-6), fill=(153, 157, 143))
	return im


def build_tiles():
	src = Image.open(os.path.join(OUT, 'tiles_wild.png'))
	s = Sheet(os.path.join(OUT, 'tiles_wild.png'))
	s.img = darken_sheet(src)
	for i in (0, 1, 2, 4, 6, 7, 8, 10, 12): s.set(i, ground(200+i, i))
	for i in list(range(80, 112)) + list(range(144, 204)):
		s.set(i, wall(1000+i, wood=i in list(range(88, 96))+list(range(104, 112))+list(range(176, 204))))
	for i in (48, 49, 52, 53): s.set(i, wall(2000+i))
	for i in (50, 54): s.set(i, wall(2000+i, wood=True))
	for i in (128, 72):
		im = ground(3000+i)
		im.alpha_composite(stump())
		s.set(i, im)
	s.set(240, stump(True))
	for i in (129, 73):
		im = ground(3000+i)
		im.alpha_composite(stump())
		s.set(i, im)
	s.set(241, stump(True))
	s.save(os.path.join(OUT, 'swamp_tiles.png'))


def build_water():
	r = random.Random(8709)
	im = Image.new('RGBA', (32, 32), (23, 39, 44, 255))
	d = ImageDraw.Draw(im)
	for _ in range(90):
		x, y = r.randrange(32), r.randrange(32)
		d.line((x, y, min(31, x+r.randrange(1, 5)), y),
			fill=r.choice([(33, 54, 59), (45, 65, 69), (71, 83, 85)]))
	im.save(os.path.join(OUT, 'swamp_water.png'))


def build_overlays():
	f = Sheet(os.path.join(OUT, 'terrain_features.png'))
	f.img = darken_sheet(f.img)
	for i in (128, 129, 130, 131, 132, 133): f.set(i, reeds(i))
	f.save(os.path.join(OUT, 'swamp_terrain_features.png'))
	r = Sheet(os.path.join(OUT, 'raised_terrain.png'))
	r.img = darken_sheet(r.img)
	for i in (0, 2): r.set(i, reeds(500+i))
	r.save(os.path.join(OUT, 'swamp_raised_terrain.png'))


def mob(kind):
	im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
	d = ImageDraw.Draw(im)
	if kind == 0:  # mire husk: sunken face and roots
		d.rectangle((4, 5, 11, 13), fill=(38, 46, 42))
		d.polygon([(4, 5), (3, 8), (2, 12), (5, 10), (7, 13), (11, 10), (13, 13), (12, 6)], fill=(59, 69, 61))
		d.rectangle((5, 2, 10, 7), fill=(86, 94, 83))
		d.line((4, 3, 10, 2), fill=(143, 151, 138), width=2)
		d.point((6, 5), fill=(203, 219, 178)); d.point((9, 5), fill=(203, 219, 178))
		d.line((4, 13, 2, 15), fill=(45, 56, 48), width=2)
		d.line((11, 13, 13, 15), fill=(45, 56, 48), width=2)
	elif kind == 1:  # broad, ragged grave moth
		d.polygon([(7, 7), (3, 3), (1, 6), (3, 11), (7, 9)], fill=(119, 132, 136))
		d.polygon([(8, 7), (12, 3), (15, 6), (12, 11), (8, 9)], fill=(142, 153, 155))
		d.rectangle((6, 5, 9, 12), fill=(49, 58, 64))
		d.point((5, 6), fill=(193, 209, 202)); d.point((11, 6), fill=(193, 209, 202))
		d.point((7, 7), fill=(211, 229, 188)); d.point((8, 7), fill=(211, 229, 188))
	elif kind == 2:  # hollow bell-shaped wraith
		d.polygon([(7, 1), (10, 3), (12, 7), (11, 12), (13, 15), (9, 13), (7, 15), (5, 12), (3, 14), (4, 7), (5, 3)], fill=(74, 99, 107))
		d.polygon([(7, 2), (10, 5), (10, 10), (6, 10), (5, 7)], fill=(129, 155, 156))
		d.rectangle((6, 6, 9, 8), fill=(24, 37, 43))
		d.point((7, 7), fill=(224, 230, 205)); d.point((9, 7), fill=(224, 230, 205))
	else:  # warden: long mourning mantle, iron bell and crooked spade
		d.polygon([(7, 1), (11, 3), (13, 13), (11, 15), (3, 15), (4, 5)], fill=(28, 34, 41))
		d.polygon([(7, 2), (10, 4), (10, 8), (5, 8), (5, 4)], fill=(88, 97, 101))
		d.rectangle((6, 5, 9, 7), fill=(22, 28, 33))
		d.point((7, 6), fill=(213, 228, 203)); d.point((9, 6), fill=(213, 228, 203))
		d.line((12, 4, 14, 14), fill=(120, 117, 103), width=2)
		d.rectangle((11, 2, 15, 5), fill=(151, 150, 139))
		d.rectangle((4, 11, 11, 12), fill=(70, 75, 77))
	return im


def mob_frames(kind):
	b = mob(kind)
	return [b, shift(b, 0, -1), shift(b, -1, 0), shift(b, 0, -1),
		shift(b, 1, 0), shift(b, 0, -1), shift(b, 1, 0), shift(b, 2, 0),
		shift(b, 1, 0), squash(b, .8), squash(b, .5), squash(b, .25)]


def build_mobs():
	sheet([mob_frames(i) for i in range(4)]).save(os.path.join(OUT, 'swamp_mobs.png'))
	warden = Image.new('RGBA', (24, 24), (0, 0, 0, 0))
	d = ImageDraw.Draw(warden)
	d.polygon([(10,2),(15,3),(19,9),(21,22),(16,23),(7,23),(3,21),(5,9)], fill=(25,31,38))
	d.polygon([(10,3),(15,4),(17,10),(6,10),(7,6)], fill=(77,86,91))
	d.polygon([(8,7),(16,7),(15,14),(9,13)], fill=(16,23,29))
	d.rectangle((9,9,15,11), fill=(7,13,18))
	d.point((10,10), fill=(206,226,195)); d.point((14,10), fill=(206,226,195))
	d.line((7,15,18,15), fill=(91,94,92), width=2)
	d.line((19,5,22,20), fill=(112,99,80), width=2)
	d.polygon([(18,2),(23,3),(22,10),(18,9)], fill=(161,164,154))
	d.line((19,3,22,4), fill=(222,221,204))
	d.rectangle((3,16,5,21), fill=(100,105,105))
	frames = [warden, shift(warden,0,-1), shift(warden,-1,0), shift(warden,0,-1),
		shift(warden,1,0), shift(warden,0,-1), shift(warden,1,0), shift(warden,2,0),
		shift(warden,1,0), warden, shift(warden,0,2), shift(warden,0,5)]
	boss = Image.new('RGBA', (24*12, 24), (0,0,0,0))
	for i, frame in enumerate(frames): boss.alpha_composite(frame, (24*i,0))
	boss.save(os.path.join(OUT, 'swamp_boss.png'))


def build_items():
	im = Image.open(os.path.join(OUT, 'items.png')).convert('RGBA')
	d = ImageDraw.Draw(im)
	def box(slot): return (slot % 16 * 16, slot // 16 * 16)
	x, y = box(20)
	d.polygon([(x+3,y+12),(x+6,y+5),(x+9,y+3),(x+12,y+7),(x+11,y+13)], fill=(69,77,57))
	d.line((x+6,y+5,x+8,y+2,x+11,y+1), fill=(152,161,124), width=2)
	x, y = box(21)
	d.polygon([(x+3,y+11),(x+6,y+4),(x+10,y+4),(x+13,y+11)], fill=(110,112,106))
	d.line((x+5,y+8,x+11,y+8), fill=(229,228,207), width=2)
	d.point((x+7,y+10), fill=(240,238,216)); d.point((x+11,y+10), fill=(240,238,216))
	x, y = box(22)
	d.line((x+4,y+14,x+11,y+2), fill=(83,73,62), width=3)
	d.polygon([(x+9,y+1),(x+15,y+3),(x+12,y+9),(x+8,y+7)], fill=(150,156,150))
	d.line((x+9,y+2,x+14,y+3), fill=(220,222,204))
	x, y = box(23)
	d.rectangle((x+4,y+5,x+12,y+13), fill=(52,59,61))
	d.rectangle((x+6,y+7,x+10,y+11), fill=(212,196,126))
	d.line((x+5,y+5,x+5,y+12), fill=(134,140,135), width=2)
	d.line((x+11,y+5,x+11,y+12), fill=(134,140,135), width=2)
	d.arc((x+5,y+1,x+11,y+7), 180, 360, fill=(164,168,160), width=2)
	im.save(os.path.join(OUT, 'items.png'))


def build_all():
	build_tiles()
	build_water()
	build_overlays()
	build_mobs()
	build_items()


if __name__ == '__main__':
	build_all()
