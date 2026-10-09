package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;
import com.watabou.utils.Random;

/** A one-use shard that cuts and flashes when thrown. */
public class AshglassShard extends Item {
	{
		image = TaigaItemSprites.ASHGLASS_SHARD;
		stackable = true;
		defaultAction = AC_THROW;
	}
	@Override protected void onThrow(int cell){
		Char target = Actor.findChar(cell);
		if (target != null && target != Dungeon.hero){
			target.damage(Random.IntRange(5, 9), this);
			if (target.isAlive()) Buff.prolong(target, Blindness.class, 2f);
		} else super.onThrow(cell);
	}
	@Override public boolean isUpgradable(){ return false; }
	@Override public boolean isIdentified(){ return true; }
	@Override public int value(){ return 18 * quantity; }
}
