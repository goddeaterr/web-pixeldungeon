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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

//MOD (taiga town): the Leshy's heart of amber and green fire. Taking it in grants lasting vitality.
public class HeartOfTheTaiga extends Item {

	public static final String AC_ABSORB = "ABSORB";
	public static final int HP_BONUS = 10;

	{
		image = TaigaItemSprites.HEART_OF_TAIGA;
		defaultAction = AC_ABSORB;
		unique = true;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_ABSORB );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute( hero, action );
		if (action.equals( AC_ABSORB )){
			detach( hero.belongings.backpack );
			hero.HTBoost += HP_BONUS;
			hero.updateHT( true );
			hero.HP = hero.HT;
			hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 12 );
			hero.sprite.showStatus( 0x44FF44, Messages.get(this, "status", HP_BONUS) );
			GLog.p( Messages.get(this, "absorb_msg") );
			Sample.INSTANCE.play( Assets.Sounds.LEVELUP );
			hero.sprite.operate( hero.pos );
			hero.busy();
			hero.spend( 1f );
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return new ItemSprite.Glowing( 0x66FF44, 1.5f );
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
