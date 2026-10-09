package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaTownLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.OrchardMerchant;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Ashfall;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;

/** +11: a roofless supply station between the drowned graveyard and the dead orchard. */
public class OrchardWaystationLevel extends Level {
	protected static final int W = 35, H = 27;
	protected int cell(int x, int y){ return x + y * W; }
	{
		color1 = 0x9b9ba2;
		color2 = 0x2d2b30;
	}
	@Override public String tilesTex(){ return TaigaAssets.ORCHARD_TILES; }
	@Override public String waterTex(){ return TaigaAssets.ORCHARD_WATER; }
	@Override protected boolean build(){
		setSize(W, H);
		Arrays.fill(map, Terrain.WALL);
		for (int y = 2; y < H - 2; y++) for (int x = 2; x < W - 2; x++)
			map[cell(x,y)] = Random.Int(17) == 0 ? Terrain.EMPTY_DECO : Terrain.EMPTY;
		// A broken glass roof, open court and three rows of dead trees.
		for (int y = 5; y <= 18; y++) for (int x = 3; x <= 14; x++)
			map[cell(x,y)] = x == 3 || x == 14 || y == 5 || y == 18 ? Terrain.WALL_DECO : Terrain.EMPTY_SP;
		map[cell(14,12)] = Terrain.DOOR;
		for (int y = 5; y <= 19; y += 4) for (int x = 22; x <= 30; x += 4)
			map[cell(x,y)] = Terrain.CUSTOM_DECO;
		for (int y = 3; y <= H - 3; y++) for (int x = 16; x <= 18; x++) map[cell(x,y)] = Terrain.EMPTY_SP;
		for (int x = 14; x <= 18; x++) map[cell(x,12)] = Terrain.EMPTY_SP;
		for (int x = 7; x <= 12; x++) map[cell(x,11)] = Terrain.EMPTY_SP;
		map[cell(9,8)] = Terrain.REGION_DECO_ALT;
		int down = cell(17,H-3), up = cell(17,2);
		map[down] = Terrain.EXIT;
		map[up] = Terrain.ENTRANCE;
		transitions.add(new LevelTransition(this, down, LevelTransition.Type.REGULAR_ENTRANCE));
		transitions.add(new LevelTransition(this, up, LevelTransition.Type.REGULAR_EXIT));
		Arrays.fill(mapped, true);
		return true;
	}
	@Override protected void createMobs(){
		OrchardMerchant merchant = new OrchardMerchant();
		merchant.pos = cell(8,12);
		mobs.add(merchant);
	}
	@Override protected void createItems(){
		ArrayList<Item> stock = OrchardMerchant.orchardStock();
		int placed = 0;
		for (int y = 6; y <= 17 && placed < stock.size(); y++) for (int x = 4; x <= 13 && placed < stock.size(); x++){
			if (x != 4 && x != 13 && y != 6 && y != 17) continue;
			int cell = cell(x,y);
			if (map[cell] != Terrain.EMPTY_SP || findMob(cell) != null) continue;
			Heap heap = drop(stock.get(placed++), cell);
			heap.type = Heap.Type.FOR_SALE;
		}
	}
	@Override public Mob createMob(){ return null; }
	@Override public int mobLimit(){ return 0; }
	@Override public Actor addRespawner(){ return null; }
	@Override public int randomRespawnCell(Char ch){ return -1; }
	@Override public void playLevelMusic(){ Music.INSTANCE.play(Assets.Music.HALLS_TENSE, true); }
	@Override public Group addVisuals(){
		super.addVisuals();
		if (hasFire()) visuals.add(new TaigaTownLevel.Campfire(cell(9,8)));
		visuals.add(new Snowfall(width()*DungeonTilemap.SIZE, height()*DungeonTilemap.SIZE, 2f));
		visuals.add(new Ashfall(width()*DungeonTilemap.SIZE, height()*DungeonTilemap.SIZE, 0.7f));
		return visuals;
	}
	protected boolean hasFire(){ return true; }
	@Override public String tileName(int tile){
		String name = OrchardTerrain.name(tile);
		return name != null ? name : super.tileName(tile);
	}
	@Override public String tileDesc(int tile){
		String desc = OrchardTerrain.desc(tile);
		return desc != null ? desc : super.tileDesc(tile);
	}
}
