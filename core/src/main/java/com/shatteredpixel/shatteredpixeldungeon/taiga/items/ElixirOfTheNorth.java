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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

//MOD (taiga town): the shaman's brew: heals over time and keeps the cold away for a long while
public class ElixirOfTheNorth extends TaigaDrink {

	{
		image = TaigaItemSprites.ELIXIR_NORTH;
	}

	@Override
	protected void drink( Hero hero ) {
		Buff.detach( hero, Chill.class );
		Buff.detach( hero, Frost.class );
		Buff.affect( hero, FrostWard.class, FrostWard.DURATION );
		Buff.affect( hero, Healing.class ).setHeal( Math.round(0.5f * hero.HT), 0.2f, 0 );
	}

	@Override
	public int value() {
		return 50 * quantity;
	}

	//immunity to chill and freezing
	public static class FrostWard extends FlavourBuff {

		public static final float DURATION = 200f;

		{
			type = buffType.POSITIVE;
			immunities.add( Chill.class );
			immunities.add( Frost.class );
		}

		@Override
		public int icon() {
			return BuffIndicator.FROST;
		}

		@Override
		public void tintIcon( com.watabou.noosa.Image icon ) {
			icon.hardlight( 1f, 0.8f, 0.3f );
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}
	}
}
