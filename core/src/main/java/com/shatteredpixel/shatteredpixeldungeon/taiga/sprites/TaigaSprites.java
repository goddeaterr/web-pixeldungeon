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

package com.shatteredpixel.shatteredpixeldungeon.taiga.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Villager;

//MOD (taiga town): sprites of the village characters, rows of the sheets drawn by mod/taiga_art.py
public class TaigaSprites {

	public static class VillagerSprite extends TaigaMobSprite {
		public VillagerSprite(){
			super();
			setup( TaigaAssets.VILLAGERS, 0, 2 );
		}

		@Override
		public void linkVisuals( Char ch ) {
			super.linkVisuals( ch );
			if (ch instanceof Villager){
				setup( TaigaAssets.VILLAGERS, ((Villager) ch).look, 2 );
			}
		}
	}

	public static class FurTraderSprite extends TaigaMobSprite {
		public FurTraderSprite(){
			super();
			setup( TaigaAssets.TRADERS, 0, 2 );
		}
	}

	public static class HerbalistSprite extends TaigaMobSprite {
		public HerbalistSprite(){
			super();
			setup( TaigaAssets.TRADERS, 1, 2 );
		}
	}

	public static class HunterSprite extends TaigaMobSprite {
		public HunterSprite(){
			super();
			setup( TaigaAssets.TRADERS, 2, 2 );
		}
	}

	public static class HuskySprite extends TaigaMobSprite {
		public HuskySprite(){
			super();
			setup( TaigaAssets.ANIMALS, 0, 4 );
		}
	}

	public static class ReindeerSprite extends TaigaMobSprite {
		public ReindeerSprite(){
			super();
			setup( TaigaAssets.ANIMALS, 1, 2 );
		}
	}

	public static class WolfSprite extends TaigaMobSprite {
		public WolfSprite(){
			super();
			setup( TaigaAssets.ANIMALS, 2, 3 );
		}
	}

	public static class SnowHareSprite extends TaigaMobSprite {
		public SnowHareSprite(){
			super();
			setup( TaigaAssets.ANIMALS, 3, 3 );
		}
	}
}
