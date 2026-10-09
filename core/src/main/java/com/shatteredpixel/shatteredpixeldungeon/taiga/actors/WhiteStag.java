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
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.ElixirOfTheStag;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.watabou.utils.Random;

//MOD (taiga town): rare. A stag with a white coat and silver antlers, gone as soon as it sees you.
public class WhiteStag extends ShyBeast {

	{
		spriteClass = TaigaSprites.WhiteStagSprite.class;

		HP = HT = 30;
		defenseSkill = 16;

		EXP = 10;
		maxLvl = 25;

		baseSpeed = 2f;

		loot = ElixirOfTheStag.class;
		lootChance = 1f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 6 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 14;
	}
}
