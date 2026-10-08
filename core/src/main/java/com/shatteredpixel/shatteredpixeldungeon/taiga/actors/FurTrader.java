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
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.FurCloak;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.Mittens;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.PineNutBread;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;

import java.util.ArrayList;

//MOD (taiga town): sells warm clothes and some gear brought up from the dungeon
public class FurTrader extends TownTrader {

	{
		spriteClass = TaigaSprites.FurTraderSprite.class;
	}

	@Override
	public ArrayList<Item> stock( int floorSet ) {
		ArrayList<Item> items = new ArrayList<>();
		items.add( clean(new FurCloak()) );
		items.add( clean(new Mittens()) );
		//these never use the item decks of the dungeon, so the floors stay the same for a seed
		items.add( clean(Generator.randomArmor(floorSet)) );
		items.add( clean(Generator.randomWeapon(floorSet, true)) );
		items.add( clean(Generator.randomWeapon(floorSet, true)) );
		items.add( new PineNutBread() );
		return items;
	}
}
