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

package com.shatteredpixel.shatteredpixeldungeon.taiga;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;

//MOD (taiga town): textures and texts of the village, all drawn by mod/taiga_art.py
public class TaigaAssets {

	public static final String TILES            = "taiga/tiles.png";
	public static final String TERRAIN_FEATURES = "taiga/terrain_features.png";
	public static final String RAISED_TERRAIN   = "taiga/raised_terrain.png";
	public static final String WATER            = "taiga/water.png";
	public static final String SPLASH           = "taiga/splash.png";

	public static final String ITEMS            = "taiga/items.png";
	public static final String VILLAGERS        = "taiga/villagers.png";
	public static final String TRADERS          = "taiga/traders.png";
	public static final String ANIMALS          = "taiga/animals.png";

	public static final String MESSAGES         = "taiga/messages/taiga";

	//the terrain overlays pick their sheet by region, the village has its own versions of them
	public static String forLevel( String asset ){
		if (Dungeon.level instanceof TaigaTownLevel){
			if (asset.equals(Assets.Environment.TERRAIN_FEATURES)) return TERRAIN_FEATURES;
			if (asset.equals(Assets.Environment.RAISED_TERRAIN))   return RAISED_TERRAIN;
		}
		return asset;
	}
}
