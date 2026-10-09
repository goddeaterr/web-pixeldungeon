package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaTownLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.FurTrader;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Herbalist;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.LastHearthMerchant;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.TownTrader;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Villager;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;
import com.watabou.utils.Bundle;

import java.util.Arrays;

/** The last inhabited shelter, immediately above the Leshy's grove. */
public class SwampVillageLevel extends TaigaTownLevel {

	private static final int W = 35;
	private static final int H = 27;

	{
		color1 = 0xa9b3bd;
		color2 = 0x343e49;
	}

	private int cell(int x, int y){ return x + y * W; }
	@Override public String tilesTex(){ return TaigaAssets.SWAMP_TILES; }
	@Override public String waterTex(){ return TaigaAssets.SWAMP_WATER; }

	@Override
	protected boolean build() {
		setSize(W, H);
		Arrays.fill(map, Terrain.WALL);
		for (int y = 2; y < H - 2; y++) for (int x = 2; x < W - 2; x++)
			map[cell(x, y)] = Random.Int(12) == 0 ? Terrain.EMPTY_DECO : Terrain.EMPTY;
		// Uneven banks and a narrow road replace the old square, symmetrical clearing.
		for (int y = 3; y < H - 3; y++) for (int x = 2; x < W - 2; x++){
			if ((x < 9 && y > 18) || (x > 23 && y > 16) || (x > 27 && y < 8)){
				if (Random.Int(5) != 0) map[cell(x, y)] = Terrain.WATER;
			}
		}
		cabin(3, 8, 16, 18);  // supply house
		cabin(22, 5, 31, 15); // chapel and herbalist
		cabin(24, 18, 30, 23); // shuttered, partly drowned home
		map[cell(9, 18)] = Terrain.DOOR;
		map[cell(26, 15)] = Terrain.DOOR;
		map[cell(26, 23)] = Terrain.WALL_DECO;
		for (int y = 3; y <= H - 3; y++) for (int x = 17; x <= 19; x++)
			map[cell(x, y)] = Terrain.EMPTY_SP;
		for (int x = 10; x <= 17; x++) map[cell(x, 19)] = Terrain.EMPTY_SP;
		for (int x = 19; x <= 26; x++) map[cell(x, 16)] = Terrain.EMPTY_SP;
		for (int y = 16; y <= 20; y++) map[cell(26, y)] = Terrain.EMPTY_SP;
		map[cell(17, 12)] = Terrain.WELL;
		for (int x : new int[]{5, 8, 12, 21, 25, 29}) map[cell(x, 4)] = Terrain.CUSTOM_DECO;
		for (int x : new int[]{11, 14, 22}) map[cell(x, 21)] = Terrain.CUSTOM_DECO;
		int down = cell(17, H - 3);
		int up = cell(17, 2);
		map[down] = Terrain.EXIT;
		map[up] = Terrain.ENTRANCE;
		transitions.add(new LevelTransition(this, down, LevelTransition.Type.REGULAR_ENTRANCE));
		transitions.add(new LevelTransition(this, up, LevelTransition.Type.REGULAR_EXIT));
		Arrays.fill(mapped, true);
		return true;
	}

	private void cabin(int left, int top, int right, int bottom){
		for (int y = top; y <= bottom; y++) for (int x = left; x <= right; x++){
			map[cell(x, y)] = x == left || x == right || y == top || y == bottom
					? Terrain.BOOKSHELF : Terrain.EMPTY_SP;
		}
		map[cell((left + right) / 2, bottom)] = Terrain.DOOR;
	}

	@Override
	protected void createMobs() {
		LastHearthMerchant merchant = new LastHearthMerchant();
		merchant.pos = cell(9, 12);
		mobs.add(merchant);
		Herbalist herbs = new Herbalist();
		herbs.pos = cell(26, 9);
		herbs.stalls = new int[]{cell(23, 7), cell(24, 7), cell(28, 7), cell(29, 7), cell(23, 11), cell(29, 11)};
		mobs.add(herbs);
		Villager villager = new Villager();
		villager.pos = cell(18, 20);
		mobs.add(villager);
	}

	@Override
	protected void createItems() {
		stockMerchant(4, 9, 15, 17);
		for (Mob mob : mobs) if (mob instanceof TownTrader){
			TownTrader trader = (TownTrader) mob;
			java.util.ArrayList<Item> stock = trader.stock(1);
			for (int i = 0; i < trader.stalls.length && i < stock.size(); i++){
				Heap heap = drop(stock.get(i), trader.stalls[i]);
				heap.type = Heap.Type.FOR_SALE;
			}
		}
	}

	private void stockMerchant(int left, int top, int right, int bottom) {
		java.util.ArrayList<Item> supplies = LastHearthMerchant.stock();
		int placed = 0;
		for (int y = top; y <= bottom && placed < supplies.size(); y++)
			for (int x = left; x <= right && placed < supplies.size(); x++){
				if (x != left && x != right && y != top && y != bottom) continue;
				int cell = cell(x, y);
				if (map[cell] != Terrain.EMPTY_SP || findMob(cell) != null || heaps.get(cell) != null) continue;
				Heap heap = new Heap();
				heap.type = Heap.Type.FOR_SALE;
				heap.pos = cell;
				heap.drop(supplies.get(placed++));
				heaps.put(cell, heap);
			}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		boolean hasMerchant = false;
		for (Mob mob : mobs) if (mob instanceof LastHearthMerchant) hasMerchant = true;
		if (!hasMerchant) {
			// Old +6 saves had a small fur stall. Keep the run and upgrade that cabin in place.
			mobs.removeIf(mob -> mob instanceof FurTrader);
			for (int y = 9; y <= 15; y++) for (int x = 6; x <= 13; x++){
				int cell = cell(x, y);
				Heap heap = heaps.get(cell);
				if (heap != null && heap.type == Heap.Type.FOR_SALE) heaps.remove(cell);
			}
			LastHearthMerchant merchant = new LastHearthMerchant();
			merchant.pos = cell(9, 11);
			mobs.add(merchant);
			stockMerchant(6, 9, 13, 15);
		}
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.CAVES_TENSE, true);
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		Gravestones.add(visuals, this);
		visuals.add(new Snowfall(width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE, 2.5f));
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

	@Override
	public int randomRespawnCell(Char ch){ return -1; }

	@Override
	public int randomDestination(Char ch){ return cell(16 + Random.Int(3), 17 + Random.Int(5)); }

	@Override
	public Mob createMob(){ return null; }

	@Override
	public int mobLimit(){ return 0; }
}
