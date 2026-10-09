package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;
import java.util.ArrayList;

/** Sparse ash and repeating dead rows replace the swamp's reeds and burial stones. */
public class OrchardPainter extends RegularPainter {
	@Override protected void decorate(Level level, ArrayList<Room> rooms){
		int w = level.width();
		for (int i = w + 1; i < level.length() - w - 1; i++){
			if (level.map[i] == Terrain.DOOR) level.map[i] = Terrain.EMPTY_SP;
			if (level.map[i] != Terrain.EMPTY) continue;
			int x = i % w, y = i / w;
			if (x % 5 == 0 && y % 4 == 0 && Random.Int(4) == 0){
				boolean clear = true;
				for (int d : new int[]{-1, 1, -w, w}) if (level.map[i+d] != Terrain.EMPTY) clear = false;
				if (clear) level.map[i] = Terrain.CUSTOM_DECO;
			} else if (Random.Int(22 + (Dungeon.depth - 12) * 14) == 0){
				level.map[i] = Terrain.EMPTY_DECO;
			}
		}
	}
}
