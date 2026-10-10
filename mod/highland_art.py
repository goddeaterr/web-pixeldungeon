"""Authored Windward Barrens tiles, overlays, sprites, and inventory icons.

Tile indices follow the game's terrain atlas; every dominant surface is redrawn
as slate, wind-scoured snow, black ice, or iron survey hardware.
"""
import os
import random
from PIL import Image, ImageDraw
from taiga_art import OUT, Sheet
from taiga_sprites import shift, squash, sheet


def cold(im):
    out=im.copy().convert('RGBA'); p=out.load()
    for y in range(out.height):
        for x in range(out.width):
            r,g,b,a=p[x,y]
            if a:
                v=int(.30*r+.48*g+.22*b)
                p[x,y]=(int(.57*v+9),int(.65*v+15),int(.75*v+23),a)
    return out


def scree(seed,road=False):
    rng=random.Random(seed)
    im=Image.new('RGBA',(16,16),(42,52,62,255) if road else (37,46,56,255)); d=ImageDraw.Draw(im)
    for _ in range(24):
        x,y=rng.randrange(16),rng.randrange(16)
        color=rng.choice([(67,78,88),(85,96,104),(21,28,39),(112,122,128)])
        d.line((x,y,min(15,x+rng.randrange(1,5)),y),fill=color)
    if road:
        for y in (4,10):
            d.line((0,y,15,y),fill=(17,26,35)); d.line((0,y-1,15,y-1),fill=(91,107,119))
        for x in (5,12): d.line((x,0,x,4),fill=(17,26,35))
    else:
        for _ in range(3):
            x,y=rng.randrange(15),rng.randrange(15)
            d.line((x,y,min(15,x+3),max(0,y-1)),fill=(153,165,171))
    return im


def cliff(seed):
    rng=random.Random(seed); im=Image.new('RGBA',(16,16),(18,25,35,255)); d=ImageDraw.Draw(im)
    for x in range(0,16,4):
        top=rng.randrange(0,4)
        d.polygon([(x,top),(min(15,x+4),top+2),(min(15,x+3),15),(x,13)],fill=rng.choice([(34,44,56),(48,60,72),(56,69,80)]))
        d.line((x,top,x,14),fill=(9,15,24))
        d.line((x+1,top+1,min(15,x+2),top+1),fill=(130,145,153))
    return im


def black_ice(seed):
    rng=random.Random(seed); im=Image.new('RGBA',(16,16),(12,22,33,255)); d=ImageDraw.Draw(im)
    for _ in range(7):
        x,y=rng.randrange(16),rng.randrange(16)
        d.line((x,y,min(15,x+rng.randrange(2,7)),max(0,y-rng.randrange(0,3))),fill=rng.choice([(58,83,101),(78,105,121),(34,58,78)]))
    d.line((0,11,5,7,10,10,15,4),fill=(137,158,164))
    return im


def marker(seed):
    im=scree(seed)
    im.alpha_composite(marker_overlay(seed))
    return im


def marker_overlay(seed):
    rng=random.Random(seed); im=Image.new('RGBA',(16,16),(0,0,0,0)); d=ImageDraw.Draw(im)
    d.polygon([(5,14),(6,4),(10,4),(11,14)],fill=(18,25,32))
    d.rectangle((5,2,11,5),fill=(105,112,114)); d.rectangle((7,0,9,3),fill=(141,150,154))
    d.line((6,7,10,7),fill=(172,158,139)); d.line((8,4,8,14),fill=(73,79,84))
    for _ in range(2): d.point((rng.randrange(1,15),rng.randrange(12,16)),fill=(177,188,190))
    return im


def windbreak(seed):
    im=scree(seed,True)
    im.alpha_composite(windbreak_overlay())
    return im


def windbreak_overlay():
    im=Image.new('RGBA',(16,16),(0,0,0,0)); d=ImageDraw.Draw(im)
    d.polygon([(2,14),(5,2),(12,2),(14,14)],fill=(13,21,29))
    d.polygon([(5,12),(7,4),(10,4),(12,12)],fill=(68,78,87))
    d.line((2,14,14,14),fill=(153,166,172),width=2)
    d.line((5,3,11,3),fill=(153,166,172))
    return im


def ridge_stair(up):
    im=scree(7001 if up else 7002,True); d=ImageDraw.Draw(im)
    for y,left,right in ((4,5,10),(8,3,12),(12,1,14)):
        d.rectangle((left,y,right,y+2),fill=(18,27,36))
        d.line((left,y,right,y),fill=(136,151,158))
    d.line((2,1,2,15),fill=(97,107,111),width=2)
    d.line((13,1,13,15),fill=(97,107,111),width=2)
    d.polygon([(8,2),(5,6),(7,6),(7,10),(9,10),(9,6),(11,6)] if up
              else [(7,3),(9,3),(9,7),(11,7),(8,11),(5,7),(7,7)],fill=(196,208,207))
    return im


def build_tiles():
    s=Sheet(os.path.join(OUT,'tiles_wild.png')); s.img=cold(s.img)
    for i in (0,1,2,4,6,7,8,10,12): s.set(i,scree(6100+i,i in (4,10)))
    s.set(16,ridge_stair(True));s.set(17,ridge_stair(False))
    for i in list(range(80,112))+list(range(144,204)): s.set(i,cliff(6200+i))
    for i in (48,49,50,52,53,54): s.set(i,black_ice(6300+i))
    for i in (72,73,128,129,240,241): s.set(i,marker(6400+i))
    for i in (56,57,58,59,60,61): s.set(i,windbreak(6500+i))
    s.save(os.path.join(OUT,'highland_tiles.png'))
    w=Image.new('RGBA',(32,32),(13,23,34,255)); d=ImageDraw.Draw(w); rng=random.Random(5901)
    for _ in range(80):
        x,y=rng.randrange(32),rng.randrange(32)
        d.line((x,y,min(31,x+rng.randrange(2,9)),max(0,y-rng.randrange(0,3))),fill=rng.choice([(34,57,72),(56,80,97),(92,112,123)]))
    w.save(os.path.join(OUT,'highland_water.png'))
    f=Sheet(os.path.join(OUT,'terrain_features.png'));f.img=cold(f.img)
    for i in (128,129,130,131,132,133):f.set(i,marker_overlay(i+6600))
    f.save(os.path.join(OUT,'highland_terrain_features.png'))
    r=Sheet(os.path.join(OUT,'raised_terrain.png'));r.img=cold(r.img)
    for i in (0,2):r.set(i,windbreak_overlay())
    r.save(os.path.join(OUT,'highland_raised_terrain.png'))


def mob(kind):
    im=Image.new('RGBA',(16,16),(0,0,0,0));d=ImageDraw.Draw(im)
    if kind==0: # slate crawler: six angular legs and a shale carapace
        for x in (2,5,10,13):d.line((x,9,x-1 if x<8 else x+1,14),fill=(44,52,58),width=2)
        d.polygon([(2,9),(5,5),(12,5),(14,10),(9,13),(4,12)],fill=(56,65,73))
        d.line((4,7,11,7),fill=(131,145,150));d.point((12,9),fill=(177,204,215))
    elif kind==1: # frost surveyor, hood and long measuring rod
        d.polygon([(5,4),(11,4),(13,15),(3,15)],fill=(43,52,61))
        d.rectangle((5,2,11,6),fill=(106,120,128));d.rectangle((6,5,10,8),fill=(12,20,28))
        d.point((7,6),fill=(179,202,212));d.point((10,6),fill=(179,202,212))
        d.line((14,1,13,15),fill=(139,145,140),width=2);d.line((12,4,15,4),fill=(167,171,161))
    elif kind==2: # ambulatory iron vane
        d.line((8,3,8,15),fill=(77,87,94),width=3)
        d.polygon([(2,5),(8,2),(14,5),(8,8)],fill=(114,128,134))
        d.line((2,5,14,5),fill=(28,39,46),width=2)
        d.point((7,10),fill=(156,199,215));d.point((9,10),fill=(156,199,215))
    else: # living merchant, layered coat and lantern
        d.polygon([(5,3),(11,3),(14,15),(2,15)],fill=(69,74,78))
        d.rectangle((5,2,10,7),fill=(141,150,149));d.rectangle((6,5,9,7),fill=(38,42,46))
        d.line((3,8,2,14),fill=(174,120,70),width=2);d.ellipse((1,11,4,14),fill=(218,158,80))
    return im


def frames(base):
    return [base,shift(base,0,-1),shift(base,-1,0),shift(base,0,-1),shift(base,1,0),
            shift(base,0,-1),shift(base,1,0),shift(base,2,0),shift(base,1,0),
            squash(base,.8),squash(base,.5),squash(base,.25)]


def build_mobs():
    sheet([frames(mob(i)) for i in range(4)]).save(os.path.join(OUT,'highland_mobs.png'))
    b=Image.new('RGBA',(24,24),(0,0,0,0));d=ImageDraw.Draw(b)
    d.polygon([(8,2),(16,2),(21,22),(3,22)],fill=(27,35,43))
    d.polygon([(6,4),(18,4),(16,15),(8,15)],fill=(85,97,104))
    d.rectangle((9,6,15,11),fill=(9,17,25));d.point((10,8),fill=(177,211,223));d.point((14,8),fill=(177,211,223))
    d.line((2,14,22,14),fill=(153,161,164),width=2);d.line((12,1,12,23),fill=(134,144,146),width=2)
    d.polygon([(1,5),(12,1),(23,5),(12,8)],fill=(122,140,150))
    row=Image.new('RGBA',(24*12,24),(0,0,0,0))
    for i,f in enumerate(frames(b)):row.alpha_composite(f,(24*i,0))
    row.save(os.path.join(OUT,'highland_boss.png'))


def build_items():
    im=Image.open(os.path.join(OUT,'items.png')).convert('RGBA');d=ImageDraw.Draw(im)
    def xy(slot):return slot%16*16,slot//16*16
    x,y=xy(28);d.polygon([(x+8,y+1),(x+14,y+8),(x+7,y+15),(x+2,y+9)],fill=(89,137,158));d.line((x+8,y+2,x+12,y+8,x+6,y+12),fill=(205,230,230))
    x,y=xy(29);d.rectangle((x+2,y+5,x+14,y+13),fill=(85,75,65));d.rectangle((x+4,y+6,x+12,y+11),fill=(159,151,130));d.line((x+3,y+8,x+13,y+8),fill=(203,190,155))
    x,y=xy(30);d.line((x+5,y+15,x+11,y+2),fill=(104,113,119),width=3);d.polygon([(x+9,y+1),(x+14,y+4),(x+10,y+9),(x+6,y+5)],fill=(165,186,192));d.line((x+4,y+11,x+10,y+13),fill=(180,195,197))
    im.save(os.path.join(OUT,'items.png'))


def build_all():build_tiles();build_mobs();build_items()


if __name__=='__main__':build_all()
