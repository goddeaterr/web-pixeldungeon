package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;

/** The grave warden's heavy iron digging tool; especially effective against the dead. */
public class WardenSpade extends BattleAxe {
	{
		image = TaigaItemSprites.WARDEN_SPADE;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Char.hasProp(defender, Char.Property.UNDEAD)) damage += 4;
		return super.proc(attacker, defender, damage);
	}
}
