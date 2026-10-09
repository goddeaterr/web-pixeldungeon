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
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.SwampBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.SwampLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.SwampVillageLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.OrchardLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.OrchardWaystationLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.OrchardBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.HighlandOutpostLevel;

//MOD (taiga town): textures and texts of the village, all drawn by mod/taiga_art.py
public class TaigaAssets {

	public static final String TILES            = "taiga/tiles.png";
	//the taiga floors: like the village sheet, but their way down is a trail instead of the mine shaft
	public static final String TILES_WILD       = "taiga/tiles_wild.png";
	public static final String TERRAIN_FEATURES = "taiga/terrain_features.png";
	public static final String RAISED_TERRAIN   = "taiga/raised_terrain.png";
	public static final String WATER            = "taiga/water.png";
	public static final String SWAMP_TILES      = "taiga/swamp_tiles.png";
	public static final String SWAMP_WATER      = "taiga/swamp_water.png";
	public static final String SWAMP_FEATURES   = "taiga/swamp_terrain_features.png";
	public static final String SWAMP_RAISED     = "taiga/swamp_raised_terrain.png";
	public static final String SWAMP_MOBS       = "taiga/swamp_mobs.png";
	public static final String SWAMP_BOSS       = "taiga/swamp_boss.png";
	public static final String SPLASH           = "taiga/splash.png";
	public static final String VILLAGE_ARRIVAL  = "taiga/village_arrival.png";
	public static final String SWAMP_SPLASH     = "taiga/swamp_splash.png";
	public static final String ORCHARD_TILES    = "taiga/orchard_tiles.png";
	public static final String ORCHARD_WATER    = "taiga/orchard_water.png";
	public static final String ORCHARD_FEATURES = "taiga/orchard_terrain_features.png";
	public static final String ORCHARD_RAISED   = "taiga/orchard_raised_terrain.png";
	public static final String ORCHARD_MOBS     = "taiga/orchard_mobs.png";
	public static final String ORCHARD_BOSS     = "taiga/orchard_boss.png";
	public static final String ORCHARD_ARRIVAL  = "taiga/orchard_arrival.png";

	public static final String ITEMS            = "taiga/items.png";
	public static final String VILLAGERS        = "taiga/villagers.png";
	public static final String TRADERS          = "taiga/traders.png";
	public static final String ANIMALS          = "taiga/animals.png";
	public static final String BEASTS           = "taiga/beasts.png";
	public static final String SPIRITS          = "taiga/spirits.png";
	public static final String LESHY            = "taiga/leshy.png";

	public static final String MESSAGES         = "taiga/messages/taiga";

	//the terrain overlays pick their sheet by region, the village has its own versions of them
	public static String forLevel( String asset ){
		if (Dungeon.level instanceof OrchardLevel || Dungeon.level instanceof OrchardWaystationLevel
				|| Dungeon.level instanceof OrchardBossLevel || Dungeon.level instanceof HighlandOutpostLevel){
			if (asset.equals(Assets.Environment.TERRAIN_FEATURES)) return ORCHARD_FEATURES;
			if (asset.equals(Assets.Environment.RAISED_TERRAIN)) return ORCHARD_RAISED;
		}
		if (Dungeon.level instanceof SwampLevel || Dungeon.level instanceof SwampVillageLevel
				|| Dungeon.level instanceof SwampBossLevel){
			if (asset.equals(Assets.Environment.TERRAIN_FEATURES)) return SWAMP_FEATURES;
			if (asset.equals(Assets.Environment.RAISED_TERRAIN))   return SWAMP_RAISED;
		}
		if (Dungeon.level instanceof TaigaTownLevel || TaigaBranch.active()){
			if (asset.equals(Assets.Environment.TERRAIN_FEATURES)) return TERRAIN_FEATURES;
			if (asset.equals(Assets.Environment.RAISED_TERRAIN))   return RAISED_TERRAIN;
		}
		return asset;
	}
}
