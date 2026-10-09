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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Berry;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.PineNutBread;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaQuests;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaTownLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.CorruptedTotem;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.OneEyedAlpha;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.TaigaMobs;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.SmokedFish;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * MOD (taiga town): taiga floors 1-4 (TaigaBranch). Generated like the dungeon's floors, but with
 * forest instead of walls, about twice the size of a sewer floor, and their own rooms, mobs and loot.
 * Loot never comes from the item decks of the dungeon (see randomLoot).
 */
public class TaigaLevel extends RegularLevel {

	{
		color1 = 0xdbe5ee;
		color2 = 0x2a5546;
	}

	@Override
	public String tilesTex() {
		return TaigaAssets.TILES_WILD;
	}

	@Override
	public String waterTex() {
		return TaigaAssets.WATER;
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.CAVES_1, Assets.Music.CAVES_2, Assets.Music.CAVES_3},
				new float[]{1, 1, 0.5f},
				false);
	}

	//a sewer floor has 4-6 standard rooms
	@Override
	protected int standardRooms( boolean forceMax ) {
		return forceMax ? 12 : 9 + Random.Int(3);
	}

	@Override
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> rooms = new ArrayList<>();
		rooms.add( roomEntrance = new TaigaRooms.TaigaEntranceRoom() );
		rooms.add( roomExit = new TaigaRooms.TaigaExitRoom() );

		int standards = standardRooms(false);
		for (int i = 0; i < standards; i++) {
			StandardRoom s;
			do {
				s = TaigaRooms.createStandard(Dungeon.depth);
			} while (!s.setSizeCat( standards - i ));
			i += s.sizeFactor() - 1;
			rooms.add(s);
		}

		rooms.add( new TaigaRooms.HuntersCacheRoom() );
		rooms.add( new TaigaRooms.HuntersCacheRoom() );
		if (Dungeon.depth == 2) rooms.add( new TaigaRooms.TrapperCampRoom() );
		if (Dungeon.depth == 4) rooms.add( new TaigaRooms.ShamanCircleRoom() );
		return rooms;
	}

	@Override
	protected Painter painter() {
		return new TaigaPainter()
				.setWater(Dungeon.depth == 3 ? 0.55f : 0.20f, 5)
				.setGrass(Dungeon.depth == 4 ? 0.55f : 0.30f, 4)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}

	@Override
	protected int nTraps() {
		return Random.NormalIntRange( 3, 5 );
	}

	@Override
	protected Class<?>[] trapClasses() {
		return new Class<?>[]{ ChillingTrap.class, WornDartTrap.class, AlarmTrap.class,
				GrippingTrap.class, FlockTrap.class, TeleportationTrap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{ 4, 4, 2, 2, 1, 1 };
	}

	@Override
	public int mobLimit() {
		return 7 + Dungeon.depth + Random.Int(2);
	}

	@Override
	public Mob createMob() {
		return TaigaMobs.random(Dungeon.depth);
	}

	//standard room cells away from the entrance, where mobs may start
	private ArrayList<Integer> spawnCells(){
		PathFinder.buildDistanceMap(entrance(), passable, 10);
		ArrayList<Integer> cells = new ArrayList<>();
		for (Room r : rooms){
			if (!(r instanceof StandardRoom) || r == roomEntrance) continue;
			for (int y = r.top + 1; y < r.bottom; y++){
				for (int x = r.left + 1; x < r.right; x++){
					int cell = x + y * width();
					if (passable[cell] && !solid[cell] && PathFinder.distance[cell] == Integer.MAX_VALUE
							&& traps.get(cell) == null && plants.get(cell) == null && cell != exit()){
						cells.add(cell);
					}
				}
			}
		}
		return cells;
	}

	@Override
	protected void createMobs() {
		ArrayList<Integer> cells = spawnCells();
		Random.shuffle(cells);
		int count = mobLimit();
		for (int cell : cells){
			if (count <= 0) break;
			if (findMob(cell) != null) continue;
			Mob mob = createMob();
			mob.pos = cell;
			mobs.add(mob);
			count--;
		}

		//the shaman's floor has three corrupted totems, far apart
		if (Dungeon.depth == 4){
			int placed = 0;
			ArrayList<Integer> totems = new ArrayList<>();
			for (int cell : cells){
				if (placed >= TaigaQuests.TOTEMS) break;
				if (findMob(cell) != null) continue;
				boolean far = true;
				for (int t : totems){
					if (distance(t, cell) < 12) { far = false; break; }
				}
				if (!far) continue;
				CorruptedTotem totem = new CorruptedTotem();
				totem.pos = cell;
				mobs.add(totem);
				totems.add(cell);
				placed++;
			}
		}

		for (Mob m : mobs){
			if (map[m.pos] == Terrain.HIGH_GRASS || map[m.pos] == Terrain.FURROWED_GRASS) {
				map[m.pos] = Terrain.GRASS;
				losBlocking[m.pos] = false;
			}
		}
	}

	//taiga loot: never uses the item decks of the dungeon, so the dungeon's floors stay as the seed made them
	private static final Generator.Category[] LOOT = {
			Generator.Category.GOLD, Generator.Category.POTION, Generator.Category.SCROLL,
			Generator.Category.SEED, Generator.Category.STONE, Generator.Category.WEAPON,
			Generator.Category.ARMOR, Generator.Category.MISSILE, Generator.Category.FOOD,
			Generator.Category.WAND, Generator.Category.RING
	};
	private static final float[] LOOT_CHANCES = { 20, 18, 18, 6, 7, 9, 7, 6, 4, 4, 3 };

	public static Item randomLoot(){
		return Generator.randomUsingDefaults( LOOT[Random.chances(LOOT_CHANCES)] );
	}

	//the taiga is generous: far more loot than a dungeon floor (3-5 items), and every floor has
	// a potion of strength and at least one scroll of upgrade, so better gear can actually be used
	@Override
	protected void createItems() {
		int items = 14 + Random.Int(5);
		for (int i = 0; i < items; i++){
			dropLoot( randomLoot() );
		}
		dropLoot( new PotionOfStrength() );
		dropLoot( new ScrollOfUpgrade() );
		if (Random.Int(2) == 0) dropLoot( new ScrollOfUpgrade() );
		dropLoot( new PotionOfHealing() );
		dropLoot( Random.Int(2) == 0 ? new SmokedFish() : new Berry().quantity(2) );
		dropLoot( new PineNutBread() );
	}

	private void dropLoot( Item item ){
		int cell = randomDropCell();
		if (cell != -1) drop( item, cell ).type = Heap.Type.HEAP;
	}

	@Override
	public Group addVisuals() {
		//quest monsters that appear once a quest has been given, even if this floor was made earlier
		if (Dungeon.depth == 3 && TaigaQuests.trapperGiven && !TaigaQuests.alphaSpawned){
			ArrayList<Integer> cells = spawnCells();
			if (!cells.isEmpty()){
				OneEyedAlpha alpha = new OneEyedAlpha();
				alpha.pos = Random.element(cells);
				if (findMob(alpha.pos) == null){
					mobs.add(alpha);
					com.shatteredpixel.shatteredpixeldungeon.actors.Actor.add(alpha);
					TaigaQuests.alphaSpawned = true;
				}
			}
		}

		super.addVisuals();
		for (int i = 0; i < length(); i++){
			if (map[i] == Terrain.REGION_DECO_ALT){
				visuals.add(new TaigaTownLevel.Campfire(i));
			}
		}
		visuals.add(new Snowfall(width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE));
		return visuals;
	}

	@Override
	public String tileName( int tile ) {
		String name = TaigaTownLevel.taigaTileName(tile, true);
		return name != null ? name : super.tileName(tile);
	}

	@Override
	public String tileDesc( int tile ) {
		String desc = TaigaTownLevel.taigaTileDesc(tile, true);
		return desc != null ? desc : super.tileDesc(tile);
	}
}
