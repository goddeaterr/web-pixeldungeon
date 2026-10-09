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
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/**
 * MOD (taiga town): the floors above the village, "the Old Taiga".
 *
 * They are a dungeon branch of their own (like the mining and vault quest areas), so their depths
 * (1-5, counted upwards from the village) never mix with the dungeon's: separate level files, seeds
 * and statistics. The village trail leads to floor 1, floor 5 is the Leshy's grove.
 */
public class TaigaBranch {

	//branch 1 is used by the dungeon's quest areas
	public static final int BRANCH = 4;
	public static final int FLOORS = 5;

	public static boolean active(){
		return Dungeon.branch == BRANCH;
	}

	public static Level newLevel( int depth ){
		if (depth >= FLOORS){
			return new TaigaBossLevel();
		}
		return new TaigaLevel();
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

	//the depth shown in the menu: taiga floors count upwards, "+1" .. "+5"
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

	//"Descending..." would be wrong when climbing the mountain
	public static String loadingText( InterlevelScene.Mode mode ){
		if (taigaTransition()){
			if (mode == InterlevelScene.Mode.DESCEND) return Messages.get(TaigaBranch.class, "climbing");
			if (mode == InterlevelScene.Mode.ASCEND)  return Messages.get(TaigaBranch.class, "going_down");
		}
		return null;
	}
}
