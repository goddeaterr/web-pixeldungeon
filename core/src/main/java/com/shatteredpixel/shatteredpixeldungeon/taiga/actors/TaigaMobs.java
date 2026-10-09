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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

//MOD (taiga town): which creatures live on which taiga floor
public class TaigaMobs {

	private static final Class<?>[] TYPES = {
			FrostWolf.class, WildBoar.class, SnowHare.class, IceWisp.class,
			Frostbitten.class, BrownBear.class, SpruceTreant.class
	};

	public static Mob random( int depth ){
		//rarely, something special wanders by
		if (Random.Int(40) == 0){
			return Random.Int(2) == 0 ? new WhiteStag() : new GoldenSable();
		}
		float[] chances;
		switch (depth){
			case 1: default: chances = new float[]{ 4, 4, 2, 0, 0, 0, 0 }; break;
			case 2:          chances = new float[]{ 3, 3, 1, 3, 0, 0, 0 }; break;
			case 3:          chances = new float[]{ 3, 0, 0, 3, 3, 1, 0 }; break;
			case 4:          chances = new float[]{ 0, 0, 0, 2, 3, 2, 3 }; break;
		}
		return (Mob) Reflection.newInstance( TYPES[Random.chances(chances)] );
	}
}
