package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/** Inspect text shared by the swamp village, marsh and graveyard. */
public class SwampTerrain {
	public static String name(int tile) { return text(tile, false); }
	public static String desc(int tile) { return text(tile, true); }
	private static String text(int tile, boolean desc) {
		String key;
		switch (tile) {
			case Terrain.WALL: case Terrain.WALL_DECO: key = "shale"; break;
			case Terrain.BOOKSHELF: key = "timber"; break;
			case Terrain.STATUE: case Terrain.STATUE_SP: key = "stump"; break;
			case Terrain.WATER: key = "blackwater"; break;
			case Terrain.EMPTY: case Terrain.EMPTY_DECO: key = "peat"; break;
			case Terrain.EMPTY_SP: key = "boards"; break;
			case Terrain.GRASS: case Terrain.HIGH_GRASS: key = "reeds"; break;
			case Terrain.WELL: key = "well"; break;
			case Terrain.ENTRANCE: key = "up"; break;
			case Terrain.EXIT: key = "down"; break;
			default: return null;
		}
		return Messages.get(SwampTerrain.class, key + (desc ? "_desc" : "_name"));
	}
}
