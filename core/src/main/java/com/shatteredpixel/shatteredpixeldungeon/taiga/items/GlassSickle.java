package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;

/** Pruning blade that shears through the orchard's hollow constructs. */
public class GlassSickle extends Sickle {
	{ image = TaigaItemSprites.GLASS_SICKLE; }
	@Override public int proc(Char attacker, Char defender, int damage){
		if (Char.hasProp(defender, Char.Property.INORGANIC)) damage += 4;
		return super.proc(attacker, defender, damage);
	}
}
