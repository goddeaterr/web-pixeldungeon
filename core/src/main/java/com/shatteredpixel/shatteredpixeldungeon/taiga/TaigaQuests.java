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

import com.watabou.utils.Bundle;

/**
 * MOD (taiga town): state of the taiga quests for the current run, saved with the game
 * (Dungeon.saveGame / loadGame, like the dungeon's quests).
 *
 * - The trapper (taiga floor 2) asks the hero to hunt the One-Eyed Alpha, who prowls floor 3.
 * - The shaman (taiga floor 4) asks the hero to break the three corrupted totems on her floor.
 * - The Leshy waits in his grove on floor 5.
 */
public class TaigaQuests {

	public static final int TOTEMS = 3;

	public static boolean trapperGiven;
	public static boolean alphaSpawned;
	public static boolean trapperDone;

	public static boolean shamanGiven;
	public static int totemsBroken;
	public static boolean shamanDone;

	public static boolean leshyDefeated;
	public static boolean villageIntroSeen;
	public static boolean swampIntroSeen;

	public static void reset(){
		trapperGiven = alphaSpawned = trapperDone = false;
		shamanGiven = shamanDone = false;
		totemsBroken = 0;
		leshyDefeated = false;
		villageIntroSeen = swampIntroSeen = false;
	}

	private static final String NODE = "taiga_quests";

	public static void storeInBundle( Bundle bundle ){
		Bundle node = new Bundle();
		node.put("trapper_given", trapperGiven);
		node.put("alpha_spawned", alphaSpawned);
		node.put("trapper_done", trapperDone);
		node.put("shaman_given", shamanGiven);
		node.put("totems_broken", totemsBroken);
		node.put("shaman_done", shamanDone);
		node.put("leshy_defeated", leshyDefeated);
		node.put("village_intro_seen", villageIntroSeen);
		node.put("swamp_intro_seen", swampIntroSeen);
		bundle.put(NODE, node);
	}

	public static void restoreFromBundle( Bundle bundle ){
		reset();
		if (!bundle.contains(NODE)) return;
		Bundle node = bundle.getBundle(NODE);
		trapperGiven = node.getBoolean("trapper_given");
		alphaSpawned = node.getBoolean("alpha_spawned");
		trapperDone = node.getBoolean("trapper_done");
		shamanGiven = node.getBoolean("shaman_given");
		totemsBroken = node.getInt("totems_broken");
		shamanDone = node.getBoolean("shaman_done");
		leshyDefeated = node.getBoolean("leshy_defeated");
		villageIntroSeen = node.getBoolean("village_intro_seen");
		swampIntroSeen = node.getBoolean("swamp_intro_seen");
	}
}
