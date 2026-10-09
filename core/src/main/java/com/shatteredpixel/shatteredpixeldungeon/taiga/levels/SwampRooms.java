package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Random;

/** Room shapes that belong to the drowned graveyard rather than the forest or dungeon. */
public class SwampRooms {
	private static void openDoors(StandardRoom room) {
		for (com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room.Door door : room.connected.values()) {
			door.set(com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room.Door.Type.REGULAR);
		}
	}

	public static class SunkenGravesRoom extends StandardRoom {
		@Override public float[] sizeCatProbs(){ return new float[]{3, 2, 1}; }
		@Override public void paint(Level level) {
			Painter.fill(level, this, Terrain.WALL);
			Painter.fill(level, this, 1, Terrain.EMPTY);
			openDoors(this);
			for (int y = top + 2; y < bottom - 1; y++) for (int x = left + 2; x < right - 1; x++) {
				int cell = x + y * level.width();
				if (Random.Int(8) == 0) level.map[cell] = Terrain.CUSTOM_DECO;
				else if (Random.Int(9) == 0) level.map[cell] = Terrain.EMPTY_DECO;
			}
		}
	}

	public static class BoardwalkRoom extends StandardRoom {
		@Override public float[] sizeCatProbs(){ return new float[]{3, 2, 0}; }
		@Override public void paint(Level level) {
			Painter.fill(level, this, Terrain.WALL);
			Painter.fill(level, this, 1, Terrain.WATER);
			openDoors(this);
			int midX = (left + right) / 2, midY = (top + bottom) / 2;
			for (int x = left + 1; x < right; x++) level.map[x + midY * level.width()] = Terrain.EMPTY_SP;
			for (int y = top + 1; y < bottom; y++) level.map[midX + y * level.width()] = Terrain.EMPTY_SP;
			// Banks next to exits ensure every door reaches the boards.
			for (int y = top + 1; y < bottom; y++) for (int x = left + 1; x < right; x++) {
				if (x == left + 1 || x == right - 1 || y == top + 1 || y == bottom - 1)
					level.map[x + y * level.width()] = Terrain.EMPTY;
			}
		}
	}

	public static class MirePoolRoom extends StandardRoom {
		@Override public float[] sizeCatProbs(){ return new float[]{3, 3, 1}; }
		@Override public void paint(Level level) {
			Painter.fill(level, this, Terrain.WALL);
			Painter.fill(level, this, 1, Terrain.EMPTY);
			openDoors(this);
			int midX = (left + right) / 2, midY = (top + bottom) / 2;
			for (int y = top + 2; y < bottom - 1; y++) for (int x = left + 2; x < right - 1; x++) {
				int cell = x + y * level.width();
				if (Math.abs(x - midX) <= 1 || Math.abs(y - midY) <= 1) level.map[cell] = Terrain.EMPTY_SP;
				else if (Random.Int(5) != 0) level.map[cell] = Terrain.WATER;
			}
		}
	}
}
