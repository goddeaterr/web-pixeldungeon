package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.AshglassShard;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.OrchardSprites;
import com.watabou.utils.Random;

/** Translucent wings scatter a blinding reflection when they strike. */
public class GlassMoth extends Mob {
	{
		spriteClass = OrchardSprites.GlassMothSprite.class;
		HP = HT = 25; defenseSkill = 19; EXP = 8; maxLvl = 21;
		loot = AshglassShard.class; lootChance = 0.25f; flying = true;
	}
	@Override public int damageRoll(){ return Random.NormalIntRange(4, 8); }
	@Override public int attackSkill(Char target){ return 21; }
	@Override public int attackProc(Char enemy, int damage){
		if (Random.Int(5) == 0 && enemy.isAlive()) Buff.prolong(enemy, Blindness.class, 2f);
		return super.attackProc(enemy, damage);
	}
}
