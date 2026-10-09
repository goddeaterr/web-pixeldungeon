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
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaTownLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.WolfPelt;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.watabou.utils.Random;

//MOD (taiga town): prowls the outskirts of the village, never passes the palisade
public class Wolf extends Mob implements TaigaBeast {

	{
		spriteClass = TaigaSprites.WolfSprite.class;

		HP = HT = 12;
		defenseSkill = 4;

		EXP = 3;
		maxLvl = 6;

		loot = WolfPelt.class;
		lootChance = 0.6f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 5 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 10;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( 0, 1 );
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
