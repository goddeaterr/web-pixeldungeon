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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

/**
 * MOD (taiga town): a trader of the village. Works like the dungeon shopkeeper (buys anything, sells what lies
 * on their stalls, buyback), but never flees: the stalls of the other traders must not burn down because the
 * hero hit one of them. TaigaTownLevel puts new goods on the stalls whenever the hero has reached a new floor.
 */
public abstract class TownTrader extends Shopkeeper {

	//cells of this trader's stall, filled by TaigaTownLevel
	public int[] stalls = new int[0];

	private int warnCooldown = 0;

	//the goods for a restock, floorSet is 0-4 like Generator's (sewers .. halls)
	public abstract ArrayList<Item> stock( int floorSet );

	//shop goods are never cursed or enchanted and always identified, like in the dungeon shops
	protected static Item clean( Item item ){
		if (item instanceof Weapon){
			((Weapon) item).enchant(null);
		}
		if (item instanceof Armor){
			((Armor) item).inscribe(null);
		}
		item.cursed = false;
		item.level(0);
		return item.identify(false);
	}

	@Override
	protected boolean act() {
		if (warnCooldown > 0) warnCooldown--;
		return super.act();
	}

	@Override
	public void processHarm() {
		if (Dungeon.level.heroFOV[pos] && warnCooldown == 0){
			warnCooldown = 10;
			yell( Messages.get(TownTrader.class, "warn") );
		}
	}

	@Override
	public void flee() {
		//they live here
	}

	@Override
	public Notes.Landmark landmark() {
		return null;
	}

	@Override
	public String chatText() {
		return Messages.get(this, "talk");
	}

	private static final String STALLS = "stalls";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STALLS, stalls );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if (bundle.contains( STALLS )) stalls = bundle.getIntArray( STALLS );
	}
}
