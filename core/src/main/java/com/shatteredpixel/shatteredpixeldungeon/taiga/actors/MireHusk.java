package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.MireRoot;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.SwampSprites;
import com.watabou.utils.Random;

/** A body held together by peat and drowned roots. */
public class MireHusk extends Mob {
	{
		spriteClass = SwampSprites.MireHuskSprite.class;
		HP = HT = 32;
		defenseSkill = 9;
		EXP = 7;
		maxLvl = 15;
		loot = MireRoot.class;
		lootChance = 0.20f;
		properties.add(Property.UNDEAD);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(4, 9); }
	@Override public int attackSkill(Char target) { return 14; }
	@Override public int drRoll() { return super.drRoll() + Random.IntRange(0, 3); }
	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(4) == 0 && enemy.isAlive()) Buff.prolong(enemy, Cripple.class, 2f);
		return super.attackProc(enemy, damage);
	}
}
