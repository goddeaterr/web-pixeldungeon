package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Sparse remnants of forest around flooded, snow-covered graves. */
public class SwampPainter extends RegularPainter {
	@Override
	protected void decorate(Level level, ArrayList<Room> rooms) {
		int w = level.width();
		for (int i = w + 1; i < level.length() - w - 1; i++){
			if (level.map[i] == Terrain.DOOR) level.map[i] = Terrain.EMPTY;
			if (level.map[i] == Terrain.WALL && level.map[i + w] == Terrain.WATER && Random.Int(3) == 0)
				level.map[i] = Terrain.WALL_DECO;
			if (level.map[i] != Terrain.EMPTY) continue;
			if (Random.Int(45) == 0){
				boolean open = true;
				for (int dy = -1; dy <= 1 && open; dy++) for (int dx = -1; dx <= 1; dx++){
					int t = level.map[i + dx + dy * w];
					if (t != Terrain.EMPTY && t != Terrain.EMPTY_DECO) open = false;
				}
				if (open) level.map[i] = Terrain.CUSTOM_DECO;
			} else if (Dungeon.depth == 7 && Random.Int(32) == 0){
				level.map[i] = Terrain.HIGH_GRASS;
			} else if (Random.Int(30) == 0){
				level.map[i] = Terrain.EMPTY_DECO;
			} else if (Dungeon.depth == 7 && Random.Int(180) == 0){
				level.map[i] = Terrain.STATUE;
			}
		}
	}
}
