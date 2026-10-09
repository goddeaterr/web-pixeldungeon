package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;
import com.watabou.utils.Random;

/** The conservator's paired blades occasionally throw a blinding glass flash. */
public class ConservatorShears extends Scimitar {
	{ image = TaigaItemSprites.CONSERVATOR_SHEARS; tier = 4; unique = true; }
	@Override public int proc(Char attacker, Char defender, int damage){
		if (defender.isAlive() && Random.Int(5) == 0) Buff.prolong(defender, Blindness.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
