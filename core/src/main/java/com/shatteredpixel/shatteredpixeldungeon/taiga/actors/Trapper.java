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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaQuests;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.AlphaFang;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.BearskinCoat;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.FrostJavelin;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.TrapperKnife;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.shatteredpixel.shatteredpixeldungeon.taiga.windows.WndTaigaReward;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

//MOD (taiga town): an old trapper camping on taiga floor 2. Wants the One-Eyed Alpha (floor 3) dead.
public class Trapper extends TaigaQuestGiver {

	{
		spriteClass = TaigaSprites.TrapperSprite.class;
	}

	@Override
	protected Window dialog() {
		AlphaFang fang = Dungeon.hero.belongings.getItem( AlphaFang.class );
		if (TaigaQuests.trapperDone){
			return say( Messages.get(this, "farewell") );
		} else if (fang != null){
			return new WndTaigaReward( this, fang, Messages.get(this, "reward"),
					() -> {
						TaigaQuests.trapperDone = true;
						yell( Messages.get(this, "thanks") );
					},
					new FrostJavelin().quantity(3).identify(false), new BearskinCoat().identify(false),
					new TrapperKnife().identify(false) );
		} else if (!TaigaQuests.trapperGiven){
			TaigaQuests.trapperGiven = true;
			return say( Messages.get(this, "intro") );
		} else {
			return say( Messages.get(this, "reminder") );
		}
	}
}
