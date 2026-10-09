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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

//MOD (taiga town): a known, identified drink of the taiga (not a potion, so it stays out of the potion colours)
public abstract class TaigaDrink extends Item {

	public static final String AC_DRINK = "DRINK";

	{
		stackable = true;
		defaultAction = AC_DRINK;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_DRINK );
		return actions;
	}

	@Override
	public String actionName( String action, Hero hero ) {
		if (action.equals(AC_DRINK)) return Messages.get(HerbalTea.class, "ac_drink");
		return super.actionName( action, hero );
	}

	protected abstract void drink( Hero hero );

	@Override
	public void execute( Hero hero, String action ) {
		super.execute( hero, action );
		if (action.equals( AC_DRINK )){
			detach( hero.belongings.backpack );
			drink( hero );
			GLog.p( Messages.get(this, "drink_msg") );
			hero.sprite.operate( hero.pos );
			hero.busy();
			hero.spend( 1f );
			Sample.INSTANCE.play( Assets.Sounds.DRINK );
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
}
