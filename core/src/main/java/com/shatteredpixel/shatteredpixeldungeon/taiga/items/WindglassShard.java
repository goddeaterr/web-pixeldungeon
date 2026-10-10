package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;
import com.watabou.utils.Random;

/** A one-use shard of trapped wind that cuts and unbalances its target. */
public class WindglassShard extends Item {
	{ image=TaigaItemSprites.WINDGLASS_SHARD; stackable=true; defaultAction=AC_THROW; }
	@Override protected void onThrow(int cell){
		Char target=Actor.findChar(cell);
		if(target!=null && target!=Dungeon.hero){
			target.damage(Random.IntRange(7,11),this);
			if(target.isAlive())Buff.prolong(target,Cripple.class,3f);
		}else super.onThrow(cell);
	}
	@Override public boolean isUpgradable(){return false;}
	@Override public boolean isIdentified(){return true;}
	@Override public int value(){return 24*quantity;}
}
