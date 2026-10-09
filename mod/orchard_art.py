"""Draw the Grey Orchard terrain, overlays, enemies and item icons.

The sheet keeps the engine's tile indices but replaces the swamp's peat, reeds,
stones and flooded timber with ash soil, broken glass, dead trees and cisterns.
"""
import os
import random
from PIL import Image, ImageDraw
from taiga_art import OUT, Sheet
from taiga_sprites import shift, squash, sheet


def tint(im):
	out = im.copy().convert('RGBA')
	p = out.load()
	for y in range(out.height):
		for x in range(out.width):
			r,g,b,a = p[x,y]
			if a:
				v = int(.25*r + .52*g + .23*b)
				p[x,y] = (int(.58*v+12),int(.56*v+12),int(.63*v+17),a)
	return out


def soil(seed, path=False):
	r = random.Random(seed)
	base = (67,64,70) if not path else (76,74,81)
	im = Image.new('RGBA',(16,16),base+(255,))
	d = ImageDraw.Draw(im)
	for _ in range(65):
		x,y = r.randrange(16),r.randrange(16)
		d.point((x,y),fill=r.choice([(49,48,54),(94,91,95),(117,114,118),(35,34,40)]))
	if path:
		for y in (4,10):
			d.line((0,y,15,y),fill=(42,42,48))
			d.line((0,y-1,15,y-1),fill=(104,100,106))
		for x in (5,12): d.line((x,0,x,4),fill=(43,42,47))
	else:
		for _ in range(3):
			x,y = r.randrange(2,14),r.randrange(2,14)
			d.line((x,y,x+r.randrange(1,4),y-1),fill=(125,124,128))
	return im


def glass(seed):
	r = random.Random(seed)
	im = Image.new('RGBA',(16,16),(20,24,32,255))
	d = ImageDraw.Draw(im)
	for x in (1,8,15): d.line((x,0,x,15),fill=(88,92,103),width=2)
	for y in (2,10): d.line((0,y,15,y),fill=(88,92,103),width=2)
	for _ in range(4):
		x,y=r.randrange(2,13),r.randrange(2,14)
		d.polygon(((x,y),(min(15,x+3),y+1),(x+1,min(15,y+4))),fill=r.choice([(44,60,71),(58,71,80),(75,81,91)]))
	d.line((2,0,8,8,4,15),fill=(15,17,23))
	return im


def tree(seed):
	r=random.Random(seed)
	im=Image.new('RGBA',(16,16),(0,0,0,0)); d=ImageDraw.Draw(im)
	d.line((7,15,8,7,7,3),fill=(37,34,38),width=3)
	d.line((7,11,3,6,2,3),fill=(49,45,48),width=2)
	d.line((8,9,12,5,13,2),fill=(44,42,45),width=2)
	d.line((7,12,3,14),fill=(42,38,41),width=2)
	for _ in range(3):
		x,y=r.randrange(2,13),r.randrange(1,8)
		d.point((x,y),fill=(137,130,139))
	return im


def build_tiles():
	s=Sheet(os.path.join(OUT,'tiles_wild.png')); s.img=tint(s.img)
	for i in (0,1,2,4,6,7,8,10,12): s.set(i,soil(700+i,i in (4,10)))
	for i in list(range(80,112))+list(range(144,204)): s.set(i,glass(1000+i))
	for i in (48,49,50,52,53,54): s.set(i,glass(2000+i))
	for i in (72,73,128,129):
		im=soil(3000+i); im.alpha_composite(tree(i)); s.set(i,im)
	for i in (240,241): s.set(i,tree(i))
	s.save(os.path.join(OUT,'orchard_tiles.png'))
	r=random.Random(1188); water=Image.new('RGBA',(32,32),(27,31,39,255)); d=ImageDraw.Draw(water)
	for _ in range(85):
		x,y=r.randrange(32),r.randrange(32)
		d.line((x,y,min(31,x+r.randrange(2,6)),y),fill=r.choice([(54,57,67),(76,78,87),(95,91,97)]))
	water.save(os.path.join(OUT,'orchard_water.png'))
	f=Sheet(os.path.join(OUT,'terrain_features.png')); f.img=tint(f.img)
	for i in (128,129,130,131,132,133): f.set(i,tree(i))
	f.save(os.path.join(OUT,'orchard_terrain_features.png'))
	r=Sheet(os.path.join(OUT,'raised_terrain.png')); r.img=tint(r.img)
	for i in (0,2): r.set(i,tree(i+300))
	r.save(os.path.join(OUT,'orchard_raised_terrain.png'))


def mob(kind):
	im=Image.new('RGBA',(16,16),(0,0,0,0)); d=ImageDraw.Draw(im)
	if kind==0: # ash gardener: long coat and shears
		d.polygon([(5,4),(10,4),(13,14),(3,14)],fill=(46,45,51))
		d.rectangle((5,2,10,6),fill=(104,100,105)); d.rectangle((6,5,9,7),fill=(28,30,36))
		d.point((6,5),fill=(190,178,166)); d.point((9,5),fill=(190,178,166))
		d.line((2,8,1,14),fill=(132,132,139),width=2); d.line((12,8,15,12),fill=(135,139,143),width=2)
	elif kind==1: # glass moth
		d.polygon([(7,8),(2,2),(1,10),(7,11)],fill=(112,130,142))
		d.polygon([(8,8),(14,2),(15,10),(8,11)],fill=(158,165,172))
		d.line((7,4,8,12),fill=(35,41,49),width=2)
		d.point((5,7),fill=(220,224,218)); d.point((11,7),fill=(220,224,218))
	elif kind==2: # hollow scarecrow
		d.line((8,1,8,15),fill=(84,72,65),width=2); d.line((2,6,14,6),fill=(89,77,68),width=2)
		d.polygon([(5,3),(11,3),(13,11),(8,13),(3,11)],fill=(66,61,64))
		d.polygon([(4,2),(8,0),(12,2)],fill=(31,29,34))
		d.point((6,5),fill=(201,178,143)); d.point((10,5),fill=(201,178,143))
	else: # porter, bandaged and cloaked
		d.polygon([(4,3),(11,3),(13,14),(3,14)],fill=(76,68,72))
		d.rectangle((5,2,10,7),fill=(159,147,144)); d.rectangle((6,5,9,6),fill=(36,37,44))
		d.line((2,9,2,14),fill=(90,77,68),width=2)
	return im


def frames(base):
	return [base,shift(base,0,-1),shift(base,-1,0),shift(base,0,-1),shift(base,1,0),
		shift(base,0,-1),shift(base,1,0),shift(base,2,0),shift(base,1,0),
		squash(base,.8),squash(base,.5),squash(base,.25)]


def build_mobs():
	sheet([frames(mob(i)) for i in range(4)]).save(os.path.join(OUT,'orchard_mobs.png'))
	b=Image.new('RGBA',(24,24),(0,0,0,0)); d=ImageDraw.Draw(b)
	d.polygon([(8,2),(16,2),(20,11),(21,23),(3,23),(4,11)],fill=(28,28,36))
	d.polygon([(7,3),(17,3),(19,10),(5,10)],fill=(89,84,92))
	d.rectangle((8,7,16,14),fill=(13,19,26)); d.line((8,10,16,10),fill=(143,163,175))
	d.point((10,9),fill=(219,228,222)); d.point((14,9),fill=(219,228,222))
	d.line((4,15,20,15),fill=(102,115,127),width=2)
	d.line((19,3,23,21),fill=(91,78,70),width=2)
	d.polygon([(17,1),(23,3),(22,9),(18,8)],fill=(171,181,187))
	row=Image.new('RGBA',(24*12,24),(0,0,0,0))
	for i,f in enumerate(frames(b)): row.alpha_composite(f,(24*i,0))
	row.save(os.path.join(OUT,'orchard_boss.png'))


def build_items():
	im=Image.open(os.path.join(OUT,'items.png')).convert('RGBA'); d=ImageDraw.Draw(im)
	def xy(slot): return slot%16*16,slot//16*16
	x,y=xy(24); d.polygon([(x+7,y+1),(x+13,y+5),(x+9,y+15),(x+3,y+10)],fill=(92,115,130)); d.line((x+7,y+2,x+11,y+5,x+5,y+10),fill=(210,219,216))
	x,y=xy(25); d.ellipse((x+3,y+5,x+13,y+14),fill=(45,36,46)); d.arc((x+3,y+5,x+13,y+14),190,340,fill=(120,102,121)); d.line((x+8,y+6,x+9,y+2),fill=(107,99,84),width=2)
	x,y=xy(26); d.line((x+5,y+15,x+10,y+4),fill=(96,75,68),width=3); d.arc((x+3,y+1,x+15,y+11),180,355,fill=(186,194,201),width=3)
	x,y=xy(27); d.line((x+3,y+14,x+11,y+2),fill=(130,137,145),width=2); d.line((x+12,y+14,x+5,y+2),fill=(177,189,195),width=2); d.ellipse((x+2,y+12,x+6,y+15),outline=(112,98,85)); d.ellipse((x+10,y+12,x+14,y+15),outline=(112,98,85))
	im.save(os.path.join(OUT,'items.png'))


def build_all():
	build_tiles(); build_mobs(); build_items()


if __name__=='__main__': build_all()
