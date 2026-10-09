package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.FuneralLantern;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.SwampSprites;
import com.watabou.utils.Random;

/** A funeral bell's last echo given a body in the marsh. */
public class BellWraith extends Mob {
	{
		spriteClass = SwampSprites.BellWraithSprite.class;
		HP = HT = 25;
		defenseSkill = 12;
		EXP = 8;
		maxLvl = 16;
		loot = FuneralLantern.class;
		lootChance = 0.08f;
		properties.add(Property.UNDEAD);
		flying = true;
	}
	@Override public int damageRoll() { return Random.NormalIntRange(4, 8); }
	@Override public int attackSkill(Char target) { return 16; }
	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(3) == 0 && enemy.isAlive()) Buff.affect(enemy, Chill.class, 3f);
		return super.attackProc(enemy, damage);
	}
}
