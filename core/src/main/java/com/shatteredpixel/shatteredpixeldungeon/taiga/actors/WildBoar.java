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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaTownLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.watabou.utils.Random;

//MOD (taiga town): a bristly boar. Charges at twice its speed while it has room to run.
public class WildBoar extends Mob implements TaigaBeast {

	{
		spriteClass = TaigaSprites.BoarSprite.class;

		HP = HT = 20;
		defenseSkill = 3;

		EXP = 4;
		maxLvl = 9;

		loot = MysteryMeat.class;
		lootChance = 0.5f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 6 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 10;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( 0, 2 );
	}

	@Override
	public float speed() {
		float speed = super.speed();
		if (state == HUNTING && enemy != null && Dungeon.level.distance( pos, enemy.pos ) >= 2){
			speed *= 2f;
		}
		return speed;
	}

	@Override
	protected boolean getCloser( final int target ) {
		return TaigaTownLevel.restrictedStep(this, () -> super.getCloser(target));
	}

	@Override
	protected boolean getFurther( final int target ) {
		return TaigaTownLevel.restrictedStep(this, () -> super.getFurther(target));
	}
}
