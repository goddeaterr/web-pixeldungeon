package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Random;

/** The orchard is planted in rows; its glasshouses and dry irrigation beds shape the route. */
public class OrchardRooms {
	private static void doors(StandardRoom room){
		for (com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room.Door door : room.connected.values())
			door.set(com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room.Door.Type.REGULAR);
	}

	public static class DeadRows extends StandardRoom {
		@Override public float[] sizeCatProbs(){ return new float[]{2, 3, 2}; }
		@Override public void paint(Level level){
			Painter.fill(level, this, Terrain.WALL);
			Painter.fill(level, this, 1, Terrain.EMPTY);
			doors(this);
			for (int y = top + 2; y < bottom - 1; y += 3)
				for (int x = left + 2; x < right - 1; x += 3)
					if (Random.Int(4) != 0) level.map[x + y * level.width()] = Terrain.CUSTOM_DECO;
		}
	}

	public static class Glasshouse extends StandardRoom {
		@Override public float[] sizeCatProbs(){ return new float[]{1, 3, 2}; }
		@Override public void paint(Level level){
			Painter.fill(level, this, Terrain.WALL_DECO);
			Painter.fill(level, this, 1, Terrain.EMPTY_SP);
			doors(this);
			for (int x = left + 2; x < right - 1; x += 3)
				for (int y = top + 2; y < bottom - 1; y += 3)
					level.map[x + y * level.width()] = Terrain.STATUE;
		}
	}

	public static class DryCistern extends StandardRoom {
		@Override public float[] sizeCatProbs(){ return new float[]{2, 3, 1}; }
		@Override public void paint(Level level){
			Painter.fill(level, this, Terrain.WALL);
			Painter.fill(level, this, 1, Terrain.EMPTY);
			doors(this);
			int cx = (left + right) / 2, cy = (top + bottom) / 2;
			for (int y = top + 2; y < bottom - 1; y++) for (int x = left + 2; x < right - 1; x++){
				int cell = x + y * level.width();
				if (Math.abs(x - cx) <= 1 || Math.abs(y - cy) <= 1) level.map[cell] = Terrain.EMPTY_SP;
				else if (Random.Int(4) != 0) level.map[cell] = Terrain.WATER;
			}
		}
	}
}
