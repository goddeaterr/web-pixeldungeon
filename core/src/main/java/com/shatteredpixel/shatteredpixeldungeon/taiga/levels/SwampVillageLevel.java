package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaTownLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.FurTrader;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Herbalist;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.TownTrader;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Villager;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;

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

	@Override
	protected boolean build() {
		setSize(W, H);
		Arrays.fill(map, Terrain.WALL);
		for (int y = 2; y < H - 2; y++) for (int x = 2; x < W - 2; x++) {
			map[cell(x, y)] = Random.Int(18) == 0 ? Terrain.EMPTY_DECO : Terrain.EMPTY;
		}
		//Two shuttered cabins flank the road. The village has no bonfire or living garden.
		cabin(5, 8, 14, 16);
		cabin(20, 8, 29, 16);
		for (int y = 3; y < H - 3; y++){
			for (int x = 16; x <= 18; x++) map[cell(x, y)] = Terrain.EMPTY;
		}
		for (int x = 4; x <= 11; x++) map[cell(x, 21)] = Terrain.WATER;
		for (int x = 23; x <= 30; x++) map[cell(x, 21)] = Terrain.WATER;
		for (int x = 7; x <= 10; x += 3) map[cell(x, 5)] = Terrain.CUSTOM_DECO;
		for (int x = 24; x <= 27; x += 3) map[cell(x, 5)] = Terrain.CUSTOM_DECO;
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
		FurTrader fur = new FurTrader();
		fur.pos = cell(9, 11);
		fur.stalls = new int[]{cell(7, 10), cell(8, 10), cell(10, 10), cell(11, 10), cell(7, 12), cell(11, 12)};
		mobs.add(fur);
		Herbalist herbs = new Herbalist();
		herbs.pos = cell(24, 11);
		herbs.stalls = new int[]{cell(22, 10), cell(23, 10), cell(25, 10), cell(26, 10), cell(22, 12), cell(26, 12)};
		mobs.add(herbs);
		Villager villager = new Villager();
		villager.pos = cell(17, 18);
		mobs.add(villager);
	}

	@Override
	protected void createItems() {
		for (Mob mob : mobs) if (mob instanceof TownTrader){
			TownTrader trader = (TownTrader) mob;
			java.util.ArrayList<Item> stock = trader.stock(1);
			for (int i = 0; i < trader.stalls.length && i < stock.size(); i++){
				Heap heap = drop(stock.get(i), trader.stalls[i]);
				heap.type = Heap.Type.FOR_SALE;
			}
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
		return tile == Terrain.CUSTOM_DECO ? Gravestones.tileName() : super.tileName(tile);
	}

	@Override
	public String tileDesc(int tile){
		return tile == Terrain.CUSTOM_DECO ? Gravestones.tileDesc() : super.tileDesc(tile);
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
