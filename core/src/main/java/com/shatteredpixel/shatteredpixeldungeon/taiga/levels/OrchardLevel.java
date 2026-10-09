package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.AshGardener;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.GlassMoth;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.HollowScarecrow;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Ashfall;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.AshglassShard;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.BlackFruit;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.GlassSickle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

/** +12 to +14: the dying orchard becomes steadily barer toward the glasshouse. */
public class OrchardLevel extends TaigaLevel {
	{
		color1 = 0x92949b;
		color2 = 0x25272d;
	}
	@Override public String tilesTex(){ return TaigaAssets.ORCHARD_TILES; }
	@Override public String waterTex(){ return TaigaAssets.ORCHARD_WATER; }
	@Override protected boolean build(){
		if (!super.build()) return false;
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
			int cell = queue.removeFirst(), x = cell % w, y = cell / w;
			for (int next : new int[]{cell - 1, cell + 1, cell - w, cell + w}){
				if (next < 0 || next >= length()) continue;
				int nx = next % w, ny = next / w;
				if (nx < 1 || nx >= w - 1 || ny < 1 || ny >= h - 1
						|| Math.abs(nx-x) + Math.abs(ny-y) != 1) continue;
				int wall = (Terrain.flags[map[next]] & Terrain.PASSABLE) != 0 ? 0 : 1;
				if (cost[cell] + wall >= cost[next]) continue;
				cost[next] = cost[cell] + wall;
				previous[next] = cell;
				if (wall == 0) queue.addFirst(next); else queue.addLast(next);
			}
		}
		for (int cell = goal; cell != start && cell >= 0; cell = previous[cell]){
			if ((Terrain.flags[map[cell]] & Terrain.PASSABLE) == 0){
				map[cell] = Terrain.EMPTY_SP;
				traps.remove(cell);
			}
		}
	}
	@Override protected int standardRooms(boolean forceMax){ return forceMax ? 11 : 8 + Random.Int(3); }
	@Override protected ArrayList<Room> initRooms(){
		ArrayList<Room> rooms = new ArrayList<>();
		rooms.add(roomEntrance = new TaigaRooms.TaigaEntranceRoom());
		rooms.add(roomExit = new TaigaRooms.TaigaExitRoom());
		int count = standardRooms(false);
		for (int i = 0; i < count; i++){
			StandardRoom room;
			switch (Random.Int(3)){
				case 0: room = new OrchardRooms.DeadRows(); break;
				case 1: room = new OrchardRooms.Glasshouse(); break;
				default: room = new OrchardRooms.DryCistern(); break;
			}
			if (!room.setSizeCat(count - i)){ i--; continue; }
			i += room.sizeFactor() - 1;
			rooms.add(room);
		}
		return rooms;
	}
	@Override protected Painter painter(){
		return new OrchardPainter().setWater(0.16f - (Dungeon.depth - 12) * 0.04f, 3)
				.setGrass(0.04f, 1).setTraps(nTraps(), trapClasses(), trapChances());
	}
	@Override public int mobLimit(){ return 9 + (Dungeon.depth - 12) * 2; }
	@Override public Mob createMob(){
		switch (Random.Int(10)){
			case 0: case 1: case 2: case 3: return new AshGardener();
			case 4: case 5: case 6: return new GlassMoth();
			default: return new HollowScarecrow();
		}
	}
	@Override protected void createItems(){
		for (int i = 0; i < 5 + Random.Int(2); i++){
			Item item = Random.Int(7) == 0 ? new GlassSickle()
					: Random.Int(2) == 0 ? new AshglassShard().quantity(2) : new BlackFruit();
			dropOrchard(item);
		}
		if (Dungeon.depth != 13) dropOrchard(new ScrollOfUpgrade());
		if (Dungeon.depth == 13) dropOrchard(new PotionOfStrength());
		if (Random.Int(2) == 0) dropOrchard(new PotionOfHealing());
	}
	private void dropOrchard(Item item){
		int cell = randomDropCell();
		if (cell != -1) drop(item, cell).type = Heap.Type.HEAP;
	}
	@Override public void playLevelMusic(){ Music.INSTANCE.play(Assets.Music.HALLS_TENSE, true); }
	@Override public Group addVisuals(){
		super.addVisuals();
		visuals.add(new Ashfall(width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE,
				1f + (Dungeon.depth - 12) * 0.45f));
		return visuals;
	}
	@Override public String tileName(int tile){
		String name = OrchardTerrain.name(tile);
		return name != null ? name : super.tileName(tile);
	}
	@Override public String tileDesc(int tile){
		String desc = OrchardTerrain.desc(tile);
		return desc != null ? desc : super.tileDesc(tile);
	}
}
