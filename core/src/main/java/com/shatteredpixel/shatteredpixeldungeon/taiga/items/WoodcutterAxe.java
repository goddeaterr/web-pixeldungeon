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

package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Leshy;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.SpruceTreant;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;

//MOD (taiga town): a heavy woodcutter's axe, rarely found on the frostbitten. Bites deep into living wood.
public class WoodcutterAxe extends BattleAxe {

	{
		image = TaigaItemSprites.WOODCUTTER_AXE;
	}

	@Override
	public int proc( Char attacker, Char defender, int damage ) {
		if (defender instanceof SpruceTreant || defender instanceof Leshy){
			damage = Math.round( damage * 1.5f );
		}
		return super.proc( attacker, defender, damage );
	}
}
