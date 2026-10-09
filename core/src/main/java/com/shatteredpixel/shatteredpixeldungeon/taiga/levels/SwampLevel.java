package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.BellWraith;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.GraveMoth;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.MireHusk;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.MireMist;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.GraveSalt;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.MireRoot;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.FuneralLantern;
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

	@Override public String tilesTex(){ return TaigaAssets.SWAMP_TILES; }
	@Override public String waterTex(){ return TaigaAssets.SWAMP_WATER; }

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
			switch (Random.Int(3)){
				case 0: room = new SwampRooms.SunkenGravesRoom(); break;
				case 1: room = new SwampRooms.BoardwalkRoom(); break;
				default: room = new SwampRooms.MirePoolRoom(); break;
			}
			if (!room.setSizeCat(count - i)) { i--; continue; }
			i += room.sizeFactor() - 1;
			rooms.add(room);
		}
		return rooms;
	}

	@Override
	protected Painter painter(){
		return new SwampPainter().setWater(0.38f + 0.08f * (Dungeon.depth - 7), 5)
				.setGrass(0.10f - 0.03f * (Dungeon.depth - 7), 2)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}

	@Override
	public int mobLimit(){ return 8 + (Dungeon.depth - 7) * 2; }

	@Override
	public Mob createMob(){
		switch (Random.Int(10)){
			case 0: case 1: case 2: case 3: return new MireHusk();
			case 4: case 5: case 6: return new GraveMoth();
			default: return new BellWraith();
		}
	}

	@Override
	protected void createItems(){
		for (int i = 0; i < 5 + Random.Int(3); i++){
			Item item = Random.Int(12) == 0 ? new FuneralLantern()
					: Random.Int(3) == 0 ? new GraveSalt() : new MireRoot();
			dropSwampLoot(item);
		}
		dropSwampLoot(new ScrollOfUpgrade());
		dropSwampLoot(new PotionOfStrength());
		dropSwampLoot(new PotionOfHealing());
	}

	private void dropSwampLoot(Item item){
		int cell = randomDropCell();
		if (cell != -1) drop(item, cell).type = Heap.Type.HEAP;
	}

	@Override
	public void playLevelMusic(){ Music.INSTANCE.play(Assets.Music.CAVES_TENSE, true); }

	@Override
	public Group addVisuals(){
		super.addVisuals();
		Gravestones.add(visuals, this);
		visuals.add(new Snowfall(width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE,
				3.5f + (Dungeon.depth - 7)));
		visuals.add(new MireMist(width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE,
				1f + 0.25f * (Dungeon.depth - 7)));
		return visuals;
	}

	@Override
	public String tileName(int tile){
		if (tile == Terrain.CUSTOM_DECO) return Gravestones.tileName();
		String name = SwampTerrain.name(tile);
		return name != null ? name : super.tileName(tile);
	}

	@Override
	public String tileDesc(int tile){
		if (tile == Terrain.CUSTOM_DECO) return Gravestones.tileDesc();
		String desc = SwampTerrain.desc(tile);
		return desc != null ? desc : super.tileDesc(tile);
	}
}
