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

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.PineResin;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.SmokedFish;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;

import java.util.ArrayList;

//MOD (taiga town): sells thrown weapons, resin and smoked fish, and pays well for wolf pelts
public class Hunter extends TownTrader {

	{
		spriteClass = TaigaSprites.HunterSprite.class;
	}

	@Override
	public ArrayList<Item> stock( int floorSet ) {
		ArrayList<Item> items = new ArrayList<>();
		items.add( clean(Generator.randomMissile(floorSet, true)) );
		items.add( clean(Generator.randomMissile(floorSet, true)) );
		items.add( TippedDart.randomTipped(2) );
		items.add( new PineResin().quantity(3) );
		items.add( new SmokedFish() );
		items.add( new SmokedFish() );
		return items;
	}
}
