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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.TaigaBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.TaigaLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.SwampVillageLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.SwampLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.SwampBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.OrchardLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.OrchardWaystationLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.OrchardBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.HighlandOutpostLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.HighlandLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.HighlandBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/**
 * MOD (taiga town): the floors above the village, beginning with the Old Taiga.
 *
 * They are a dungeon branch of their own (like the mining and vault quest areas), so their depths
 * (counted upwards from the village) never mix with the dungeon's: separate level files and seeds.
 * The village trail leads to +1, the snowy swamp to +10, the Grey Orchard to +15,
 * and the Windward Barrens to +20.
 */
public class TaigaBranch {

	//branch 1 is used by the dungeon's quest areas
	public static final int BRANCH = 4;
	public static final int FLOORS = 20;

	public static boolean active(){
		return Dungeon.branch == BRANCH;
	}

	public static Level newLevel( int depth ){
		if (depth <= 4) return new TaigaLevel();
		if (depth == 5) return new TaigaBossLevel();
		if (depth == 6) return new SwampVillageLevel();
		if (depth <= 9) return new SwampLevel();
		if (depth == 10) return new SwampBossLevel();
		if (depth == 11) return new OrchardWaystationLevel();
		if (depth <= 14) return new OrchardLevel();
		if (depth == 15) return new OrchardBossLevel();
		if (depth == 16) return new HighlandOutpostLevel();
		if (depth <= 19) return new HighlandLevel();
		return new HighlandBossLevel();
	}

	public static String floorName( int depth ){
		return Messages.get(TaigaBranch.class, "floor_" + Math.max(1, Math.min(FLOORS, depth)));
	}

	//"You climb to ..." and a line about the place, when arriving on a taiga floor
	public static void announceArrival(){
		boolean down = InterlevelScene.mode == InterlevelScene.Mode.ASCEND;
		GLog.h( Messages.get(TaigaBranch.class, down ? "return" : "arrive", floorName(Dungeon.depth)) );
		GLog.i( Messages.get(TaigaBranch.class, "flavor_" + Math.max(1, Math.min(FLOORS, Dungeon.depth))) );
	}

	//the depth shown in the menu: upper floors count upwards
	public static String depthLabel(){
		return active() ? "+" + Dungeon.depth : Integer.toString(Dungeon.depth);
	}

	private static boolean taigaTransition(){
		LevelTransition t = InterlevelScene.curTransition;
		return active() || (t != null && t.destBranch == BRANCH);
	}

	//true if the loading screen should show the taiga picture: the destination is the village or the taiga
	public static boolean taigaLoading( int loadingDepth ){
		LevelTransition t = InterlevelScene.curTransition;
		if (t != null){
			return t.destBranch == BRANCH || (t.destBranch == 0 && t.destDepth == 0);
		}
		return loadingDepth == 0 || (active() && InterlevelScene.mode != InterlevelScene.Mode.CONTINUE);
	}

	public static boolean swampLoading( int loadingDepth ){
		LevelTransition t = InterlevelScene.curTransition;
		return (t != null && t.destBranch == BRANCH && t.destDepth >= 6 && t.destDepth <= 10)
				|| (t == null && active() && loadingDepth >= 6 && loadingDepth <= 10);
	}

	public static boolean orchardLoading( int loadingDepth ){
		LevelTransition t = InterlevelScene.curTransition;
		return (t != null && t.destBranch == BRANCH && t.destDepth >= 11 && t.destDepth <= 15)
				|| (t == null && active() && loadingDepth >= 11 && loadingDepth <= 15);
	}

	public static boolean highlandLoading(int loadingDepth){
		LevelTransition t = InterlevelScene.curTransition;
		return (t != null && t.destBranch == BRANCH && t.destDepth >= 16)
				|| (t == null && active() && loadingDepth >= 16);
	}

	//"Descending..." would be wrong when climbing the mountain
	public static String loadingText( InterlevelScene.Mode mode ){
		if (mode == InterlevelScene.Mode.DESCEND && Dungeon.hero == null)
			return Messages.get(TaigaBranch.class, "waking");
		if (taigaTransition()){
			if (mode == InterlevelScene.Mode.DESCEND) return Messages.get(TaigaBranch.class, "climbing");
			if (mode == InterlevelScene.Mode.ASCEND)  return Messages.get(TaigaBranch.class, "going_down");
		}
		return null;
	}
}
