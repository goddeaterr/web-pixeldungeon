/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.BearClaw;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.BearPelt;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.BearskinCoat;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.watabou.utils.Random;

//MOD (taiga town): a huge brown bear, woken from its winter sleep. Best left asleep.
public class BrownBear extends Mob implements TaigaBeast {

	{
		spriteClass = TaigaSprites.BearSprite.class;

		HP = HT = 42;
		defenseSkill = 4;

		EXP = 9;
		maxLvl = 13;

		loot = BearPelt.class;
		lootChance = 0.6f;

		state = SLEEPING;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 4, 10 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 14;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( 1, 4 );
	}

	@Override
	public Item createLoot() {
		int roll = Random.Int(100);
		if (roll < 6)  return new BearClaw().identify(false);
		if (roll < 10) return new BearskinCoat().identify(false);
		return new BearPelt();
	}
}
