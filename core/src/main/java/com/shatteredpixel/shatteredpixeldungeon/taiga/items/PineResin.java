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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

//MOD (taiga town): a sticky lump of resin. Thrown at someone, it glues them to the ground for a few turns.
public class PineResin extends Item {

	public static final float ROOT_TIME = 4f;

	{
		image = TaigaItemSprites.PINE_RESIN;
		stackable = true;
		defaultAction = AC_THROW;
	}

	@Override
	protected void onThrow( int cell ) {
		Char ch = Actor.findChar( cell );
		if (ch != null && ch != Dungeon.hero){
			Buff.prolong( ch, Roots.class, ROOT_TIME );
			Splash.at( cell, 0xFFC8862A, 6 );
			if (Dungeon.level.heroFOV[cell]){
				GLog.i( Messages.get(this, "stuck", ch.name()) );
			}
		} else {
			super.onThrow( cell );
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 5 * quantity;
	}
}
