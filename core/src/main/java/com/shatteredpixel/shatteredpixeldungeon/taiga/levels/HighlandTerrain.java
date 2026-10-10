package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/** Names describe the actual slate, iron, and ice painted on the Barrens sheet. */
public class HighlandTerrain {
	public static String name(int tile){ return text(tile,false); }
	public static String desc(int tile){ return text(tile,true); }
	private static String text(int tile, boolean desc){
		String key;
		switch(tile){
			case Terrain.WALL: case Terrain.WALL_DECO: key="slate"; break;
			case Terrain.STATUE: case Terrain.STATUE_SP: key="windbreak"; break;
			case Terrain.WATER: key="blackice"; break;
			case Terrain.EMPTY: case Terrain.EMPTY_DECO: key="scree"; break;
			case Terrain.EMPTY_SP: key="surveyroad"; break;
			case Terrain.CUSTOM_DECO: key="marker"; break;
			case Terrain.REGION_DECO_ALT: key="brazier"; break;
			case Terrain.ENTRANCE: key="up"; break;
			case Terrain.EXIT: key="down"; break;
			case Terrain.LOCKED_EXIT: key="sealed"; break;
			default: return null;
		}
		return Messages.get(HighlandTerrain.class,key+(desc?"_desc":"_name"));
	}
}
