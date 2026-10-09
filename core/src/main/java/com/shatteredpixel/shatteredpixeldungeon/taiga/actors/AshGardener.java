package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.BlackFruit;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.OrchardSprites;
import com.watabou.utils.Random;

/** An orchard keeper whose pruning shears have become part of its hands. */
public class AshGardener extends Mob {
	{
		spriteClass = OrchardSprites.GardenerSprite.class;
		HP = HT = 42; defenseSkill = 12; EXP = 9; maxLvl = 21;
		loot = BlackFruit.class; lootChance = 0.23f;
		properties.add(Property.UNDEAD);
	}
	@Override public int damageRoll(){ return Random.NormalIntRange(5, 10); }
	@Override public int attackSkill(Char target){ return 19; }
	@Override public int drRoll(){ return super.drRoll() + Random.IntRange(1, 3); }
	@Override public int attackProc(Char enemy, int damage){
		if (Random.Int(4) == 0 && enemy.isAlive()) Buff.prolong(enemy, Cripple.class, 2f);
		return super.attackProc(enemy, damage);
	}
}
