package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.GraveSalt;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.SwampSprites;
import com.watabou.utils.Random;

/** Pale moths that gather where grave lanterns went out. */
public class GraveMoth extends Mob {
	{
		spriteClass = SwampSprites.GraveMothSprite.class;
		HP = HT = 20;
		defenseSkill = 16;
		EXP = 6;
		maxLvl = 15;
		loot = GraveSalt.class;
		lootChance = 0.3f;
		flying = true;
	}
	@Override public int damageRoll() { return Random.NormalIntRange(3, 7); }
	@Override public int attackSkill(Char target) { return 17; }
	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(5) == 0 && enemy.isAlive()) Buff.prolong(enemy, Blindness.class, 2f);
		return super.attackProc(enemy, damage);
	}
}
