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

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaQuests;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.ElixirOfTheNorth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.SpiritStaff;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.shatteredpixel.shatteredpixeldungeon.taiga.windows.WndTaigaReward;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

//MOD (taiga town): an old shaman at her fire on taiga floor 4. Wants the three corrupted totems broken.
public class Shaman extends TaigaQuestGiver {

	{
		spriteClass = TaigaSprites.ShamanSprite.class;
	}

	@Override
	protected Window dialog() {
		if (TaigaQuests.shamanDone){
			return say( Messages.get(this, "farewell") );
		} else if (TaigaQuests.totemsBroken >= TaigaQuests.TOTEMS){
			TaigaQuests.shamanGiven = true;
			return new WndTaigaReward( this, null, Messages.get(this, "reward"),
					() -> {
						TaigaQuests.shamanDone = true;
						yell( Messages.get(this, "thanks") );
					},
					new SpiritStaff().identify(false), new ElixirOfTheNorth().quantity(3),
					new ScrollOfUpgrade().identify(false) );
		} else if (!TaigaQuests.shamanGiven){
			TaigaQuests.shamanGiven = true;
			return say( Messages.get(this, "intro") );
		} else {
			return say( Messages.get(this, "reminder", TaigaQuests.totemsBroken, TaigaQuests.TOTEMS) );
		}
	}
}
