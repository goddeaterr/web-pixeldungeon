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

//MOD (taiga town): a shy white hare, runs from the hero and only bites when cornered
public class SnowHare extends Mob {

	{
		spriteClass = TaigaSprites.SnowHareSprite.class;

		HP = HT = 5;
		defenseSkill = 8;

		EXP = 1;
		maxLvl = 5;

		loot = MysteryMeat.class;
		lootChance = 0.8f;
	}

	@Override
	protected boolean act() {
		if (state == HUNTING && enemy != null && !Dungeon.level.adjacent( pos, enemy.pos )){
			state = FLEEING;
		}
		return super.act();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 2 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 6;
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
