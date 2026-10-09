package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.WaterBridgeRoom;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Frostbitten;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;

/** Floors +7 to +9: burial paths sinking into a snowy swamp. */
public class SwampLevel extends TaigaLevel {
	{
		color1 = 0xa2abb8;
		color2 = 0x343e49;
	}

	@Override
	protected boolean build(){
		if (!super.build()) return false;
		// Water and cave rooms can occasionally leave a one-tile wall across the route.
		// Join the two trails by removing as few blocking tiles as possible.
		connectTrail();
		return true;
	}

	private void connectTrail(){
		int start = entrance(), goal = exit(), w = width(), h = height();
		int[] cost = new int[length()], previous = new int[length()];
		Arrays.fill(cost, Integer.MAX_VALUE);
		Arrays.fill(previous, -1);
		ArrayDeque<Integer> queue = new ArrayDeque<>();
		cost[start] = 0;
		queue.add(start);
		while (!queue.isEmpty()){
			int cell = queue.removeFirst();
			int x = cell % w, y = cell / w;
			for (int next : new int[]{cell - 1, cell + 1, cell - w, cell + w}){
				int nx = next % w, ny = next / w;
				if (next < 0 || next >= length() || nx < 1 || nx >= w - 1 || ny < 1 || ny >= h - 1
						|| Math.abs(nx - x) + Math.abs(ny - y) != 1) continue;
				int wall = (Terrain.flags[map[next]] & Terrain.PASSABLE) != 0 ? 0 : 1;
				if (cost[cell] + wall >= cost[next]) continue;
				cost[next] = cost[cell] + wall;
				previous[next] = cell;
				if (wall == 0) queue.addFirst(next); else queue.addLast(next);
			}
		}
		for (int cell = goal; cell != start && cell >= 0; cell = previous[cell]){
			if ((Terrain.flags[map[cell]] & Terrain.PASSABLE) == 0){
				map[cell] = Terrain.EMPTY;
				traps.remove(cell);
			}
		}
	}

	@Override
	protected int standardRooms(boolean forceMax){ return forceMax ? 11 : 8 + Random.Int(3); }

	@Override
	protected ArrayList<Room> initRooms(){
		ArrayList<Room> rooms = new ArrayList<>();
		rooms.add(roomEntrance = new TaigaRooms.TaigaEntranceRoom());
		rooms.add(roomExit = new TaigaRooms.TaigaExitRoom());
		int count = standardRooms(false);
		for (int i = 0; i < count; i++){
			StandardRoom room;
			switch (Random.Int(5)){
				case 0: room = new WaterBridgeRoom(); break;
				case 1: room = new CaveRoom(); break;
				default: room = new EmptyRoom(); break;
			}
			if (!room.setSizeCat(count - i)) { i--; continue; }
			i += room.sizeFactor() - 1;
			rooms.add(room);
		}
		return rooms;
	}

	@Override
	protected Painter painter(){
		return new SwampPainter().setWater(0.55f + 0.06f * (Dungeon.depth - 7), 5)
				.setGrass(0.04f, 2).setTraps(nTraps(), trapClasses(), trapChances());
	}

	@Override
	public int mobLimit(){ return 7 + (Dungeon.depth - 7) * 2; }

	@Override
	public Mob createMob(){
		switch (Random.Int(10)){
			case 0: case 1: case 2: case 3: return new Frostbitten();
			case 4: case 5: case 6: case 7: return new Skeleton();
			case 8: return new Necromancer();
			default: return new Wraith();
		}
	}

	@Override
	public void playLevelMusic(){ Music.INSTANCE.play(Assets.Music.CAVES_TENSE, true); }

	@Override
	public Group addVisuals(){
		super.addVisuals();
		Gravestones.add(visuals, this);
		visuals.add(new Snowfall(width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE,
				3.5f + (Dungeon.depth - 7)));
		return visuals;
	}

	@Override
	public String tileName(int tile){
		return tile == Terrain.CUSTOM_DECO ? Gravestones.tileName() : super.tileName(tile);
	}

	@Override
	public String tileDesc(int tile){
		return tile == Terrain.CUSTOM_DECO ? Gravestones.tileDesc() : super.tileDesc(tile);
	}
}
