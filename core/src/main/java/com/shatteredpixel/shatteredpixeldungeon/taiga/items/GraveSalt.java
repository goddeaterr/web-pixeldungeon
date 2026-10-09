package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;
import com.watabou.utils.Random;

/** Salt swept from old graves. Thrown salt burns undead and blinds other creatures. */
public class GraveSalt extends Item {
	{
		image = TaigaItemSprites.GRAVE_SALT;
		stackable = true;
		defaultAction = AC_THROW;
	}

	@Override
	protected void onThrow(int cell) {
		Char target = Actor.findChar(cell);
		if (target != null && target != Dungeon.hero) {
			if (Char.hasProp(target, Char.Property.UNDEAD)) {
				target.damage(Random.IntRange(8, 14), this);
			} else {
				Buff.prolong(target, Blindness.class, 3f);
			}
		} else {
			super.onThrow(cell);
		}
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 25 * quantity; }
}
