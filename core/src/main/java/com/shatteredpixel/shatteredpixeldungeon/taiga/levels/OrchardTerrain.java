package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class OrchardTerrain {
	public static String name(int tile){ return text(tile, false); }
	public static String desc(int tile){ return text(tile, true); }
	private static String text(int tile, boolean desc){
		String key;
		switch (tile){
			case Terrain.WALL: case Terrain.WALL_DECO: key = "glasswall"; break;
			case Terrain.BOOKSHELF: key = "timber"; break;
			case Terrain.STATUE: case Terrain.STATUE_SP: key = "effigy"; break;
			case Terrain.WATER: key = "cistern"; break;
			case Terrain.EMPTY: case Terrain.EMPTY_DECO: key = "ash"; break;
			case Terrain.EMPTY_SP: key = "flagstones"; break;
			case Terrain.CUSTOM_DECO: key = "tree"; break;
			case Terrain.ENTRANCE: key = "up"; break;
			case Terrain.EXIT: key = "down"; break;
			default: return null;
		}
		return Messages.get(OrchardTerrain.class, key + (desc ? "_desc" : "_name"));
	}
}
