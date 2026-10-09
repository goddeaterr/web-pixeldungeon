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

package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PatchRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.WaterBridgeRoom;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Trapper;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

/**
 * MOD (taiga town): the rooms of the taiga floors. Walls are drawn as forest (taiga tile sheet),
 * so rooms read as clearings and the corridors between them as trails through the trees.
 */
public class TaigaRooms {

	private static final Class<?>[] STANDARD = {
			GladeRoom.class, SpruceGroveRoom.class, ThicketRoom.class, FrozenPondRoom.class,
			EmptyRoom.class, WaterBridgeRoom.class, CaveRoom.class
	};

	public static StandardRoom createStandard( int depth ){
		float[] chances;
		switch (depth){
			case 1: default: chances = new float[]{5, 3, 2, 1, 2, 1, 1}; break;
			case 2:          chances = new float[]{4, 4, 2, 2, 1, 1, 1}; break;
			case 3:          chances = new float[]{3, 2, 1, 5, 1, 3, 1}; break; //the frozen lakes
			case 4:          chances = new float[]{3, 3, 4, 1, 1, 1, 2}; break; //the shaman's hills
		}
		return (StandardRoom) Reflection.newInstance(STANDARD[Random.chances(chances)]);
	}

	//a clearing among clumps of forest
	public static class GladeRoom extends PatchRoom {

		@Override
		public float[] sizeCatProbs() {
			return new float[]{3, 3, 1};
		}

		@Override
		public int minWidth() { return Math.max(5, super.minWidth()); }

		@Override
		public int minHeight() { return Math.max(5, super.minHeight()); }

		@Override
		protected float fill() {
			int scale = Math.min(width()*height(), 18*18);
			return 0.22f + scale/1400f;
		}

		@Override
		protected int clustering() { return 3; }

		@Override
		protected boolean ensurePath() { return connected.size() > 0; }

		@Override
		protected boolean cleanEdges() { return true; }

		@Override
		public void paint( Level level ) {
			Painter.fill( level, this, Terrain.WALL );
			Painter.fill( level, this, 1, Terrain.EMPTY );
			for (Door door : connected.values()) {
				door.set( Door.Type.REGULAR );
			}
			setupPatch(level);
			fillPatch(level, Terrain.WALL);
		}
	}

	//an icy pond in a clearing
	public static class FrozenPondRoom extends PatchRoom {

		@Override
		public float[] sizeCatProbs() {
			return new float[]{2, 3, 1};
		}

		@Override
		public int minWidth() { return Math.max(6, super.minWidth()); }

		@Override
		public int minHeight() { return Math.max(6, super.minHeight()); }

		@Override
		protected float fill() { return 0.40f; }

		@Override
		protected int clustering() { return 5; }

		@Override
		protected boolean ensurePath() { return connected.size() > 0; }

		@Override
		protected boolean cleanEdges() { return true; }

		@Override
		public void paint( Level level ) {
			Painter.fill( level, this, Terrain.WALL );
			Painter.fill( level, this, 1, Terrain.EMPTY );
			for (Door door : connected.values()) {
				door.set( Door.Type.REGULAR );
			}
			setupPatch(level);
			fillPatch(level, Terrain.WATER);
		}
	}

	//a clearing overgrown with snowy juniper
	public static class ThicketRoom extends PatchRoom {

		@Override
		public float[] sizeCatProbs() {
			return new float[]{3, 2, 1};
		}

		@Override
		protected float fill() { return 0.45f; }

		@Override
		protected int clustering() { return 2; }

		@Override
		protected boolean ensurePath() { return false; }

		@Override
		protected boolean cleanEdges() { return false; }

		@Override
		public void paint( Level level ) {
			Painter.fill( level, this, Terrain.WALL );
			Painter.fill( level, this, 1, Terrain.GRASS );
			for (Door door : connected.values()) {
				door.set( Door.Type.REGULAR );
			}
			setupPatch(level);
			fillPatch(level, Terrain.HIGH_GRASS);
		}
	}

	//a clearing with single spruces standing in the snow
	public static class SpruceGroveRoom extends StandardRoom {

		@Override
		public float[] sizeCatProbs() {
			return new float[]{3, 3, 1};
		}

		@Override
		public void paint( Level level ) {
			Painter.fill( level, this, Terrain.WALL );
			Painter.fill( level, this, 1, Terrain.EMPTY );
			for (Door door : connected.values()) {
				door.set( Door.Type.REGULAR );
			}
			for (int y = top + 2; y < bottom - 1; y++){
				for (int x = left + 2; x < right - 1; x++){
					if (Random.Int(6) != 0) continue;
					int cell = x + y * level.width();
					boolean open = true;
					for (int dy = -1; dy <= 1 && open; dy++){
						for (int dx = -1; dx <= 1; dx++){
							if (level.map[cell + dx + dy * level.width()] != Terrain.EMPTY){
								open = false;
								break;
							}
						}
					}
					if (open) level.map[cell] = Terrain.STATUE;
				}
			}
		}
	}

	//the way down: back towards the village
	public static class TaigaEntranceRoom extends StandardRoom {

		@Override
		public int minWidth() { return Math.max(super.minWidth(), 5); }

		@Override
		public int minHeight() { return Math.max(super.minHeight(), 5); }

		@Override
		public boolean isEntrance() { return true; }

		@Override
		public void paint( Level level ) {
			Painter.fill( level, this, Terrain.WALL );
			Painter.fill( level, this, 1, Terrain.EMPTY );
			for (Door door : connected.values()) {
				door.set( Door.Type.REGULAR );
			}
			int cell = level.pointToCell(random(2));
			Painter.set( level, cell, Terrain.EXIT );
			if (Dungeon.depth == 1){
				//the trail back to the village (depth 0 of the main branch)
				level.transitions.add(new LevelTransition(level, cell, LevelTransition.Type.BRANCH_ENTRANCE,
						0, 0, LevelTransition.Type.BRANCH_EXIT));
			} else {
				level.transitions.add(new LevelTransition(level, cell, LevelTransition.Type.REGULAR_ENTRANCE));
			}
		}

		@Override
		public boolean canPlaceCharacter( Point p, Level l ) {
			return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.entrance();
		}
	}

	//the way up the mountain
	public static class TaigaExitRoom extends StandardRoom {

		@Override
		public int minWidth() { return Math.max(super.minWidth(), 5); }

		@Override
		public int minHeight() { return Math.max(super.minHeight(), 5); }

		@Override
		public boolean isExit() { return true; }

		@Override
		public void paint( Level level ) {
			Painter.fill( level, this, Terrain.WALL );
			Painter.fill( level, this, 1, Terrain.EMPTY );
			for (Door door : connected.values()) {
				door.set( Door.Type.REGULAR );
			}
			int cell = level.pointToCell(random(2));
			Painter.set( level, cell, Terrain.ENTRANCE );
			level.transitions.add(new LevelTransition(level, cell, LevelTransition.Type.REGULAR_EXIT));
		}

		@Override
		public boolean canPlaceCharacter( Point p, Level l ) {
			return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
		}
	}

	//a hunter's lean-to with a chest of supplies
	public static class HuntersCacheRoom extends SpecialRoom {

		@Override
		public void paint( Level level ) {
			Painter.fill( level, this, Terrain.WALL );
			Painter.fill( level, this, 1, Terrain.EMPTY_SP );
			entrance().set( Door.Type.REGULAR );

			Point c = center();
			Heap chest = level.drop( TaigaLevel.randomLoot(), level.pointToCell(c) );
			chest.type = Heap.Type.CHEST;
			chest.drop( TaigaLevel.randomLoot() );
			chest.drop( TaigaLevel.randomLoot() );

			Painter.set( level, left + 1, top + 1, Terrain.REGION_DECO );
			Painter.set( level, right - 1, top + 1, Terrain.REGION_DECO );
		}
	}

	//the trapper's camp: a fire, firewood and the trapper himself (taiga floor 2)
	public static class TrapperCampRoom extends SpecialRoom {

		@Override
		public int minWidth() { return 7; }

		@Override
		public int minHeight() { return 7; }

		@Override
		public int maxWidth() { return 9; }

		@Override
		public int maxHeight() { return 9; }

		@Override
		public void paint( Level level ) {
			Painter.fill( level, this, Terrain.WALL );
			Painter.fill( level, this, 1, Terrain.EMPTY );
			entrance().set( Door.Type.REGULAR );

			Point c = center();
			Painter.set( level, c, Terrain.REGION_DECO_ALT );
			Painter.set( level, left + 1, top + 1, Terrain.REGION_DECO );
			Painter.set( level, right - 1, bottom - 1, Terrain.REGION_DECO );
			Painter.set( level, left + 1, bottom - 1, Terrain.EMPTY_SP );

			Trapper trapper = new Trapper();
			trapper.pos = level.pointToCell(new Point(c.x, c.y - 1));
			level.mobs.add(trapper);
		}
	}

	//the shaman's circle of spruces around a fire (taiga floor 4)
	public static class ShamanCircleRoom extends SpecialRoom {

		@Override
		public int minWidth() { return 9; }

		@Override
		public int minHeight() { return 9; }

		@Override
		public int maxWidth() { return 9; }

		@Override
		public int maxHeight() { return 9; }

		@Override
		public void paint( Level level ) {
			Painter.fill( level, this, Terrain.WALL );
			Painter.fill( level, this, 1, Terrain.EMPTY );
			entrance().set( Door.Type.REGULAR );

			Point c = center();
			//a ring of spruces, broken where the trail comes in
			for (int i = 0; i < 12; i++){
				double a = i * Math.PI / 6;
				int x = c.x + (int)Math.round(Math.cos(a) * 3);
				int y = c.y + (int)Math.round(Math.sin(a) * 3);
				Point d = entrance();
				if (Math.abs(x - d.x) + Math.abs(y - d.y) <= 3) continue;
				Painter.set( level, x, y, Terrain.STATUE );
			}
			Painter.set( level, c, Terrain.REGION_DECO_ALT );

			Shaman shaman = new Shaman();
			shaman.pos = level.pointToCell(new Point(c.x + 1, c.y));
			level.mobs.add(shaman);
		}
	}
}
