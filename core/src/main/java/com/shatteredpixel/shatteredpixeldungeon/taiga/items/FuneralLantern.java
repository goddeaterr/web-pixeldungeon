package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;

/** A grave keeper's lantern that burns long and shakes off the marsh's cold. */
public class FuneralLantern extends Torch {
	{ image = TaigaItemSprites.FUNERAL_LANTERN; }
	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_LIGHT.equals(action)) {
			Buff.prolong(hero, Light.class, Light.DURATION * 1.5f);
			Buff.detach(hero, Chill.class);
		}
	}
	@Override public int value() { return 35 * quantity; }
}
