package com.shatteredpixel.shatteredpixeldungeon.taiga;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.TaigaBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.SwampVillageLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.TownTrader;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.GraveWarden;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.LastHearthMerchant;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.BellWraith;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.GraveMoth;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.MireHusk;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import com.watabou.utils.SparseArray;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/** Fast deterministic checks for the new upper branch, runnable with :core:swampSmoke. */
public class SwampGenerationSmoke {
	public static void main(String[] args) throws Exception {
		Game.version = "test";
		Game.versionCode = ShatteredPixelDungeon.v3_1_1;
		for (long seed = 1; seed <= 50; seed++){
			Dungeon.seed = seed;
			Dungeon.branch = TaigaBranch.BRANCH;
			for (int depth = 5; depth <= 10; depth++){
				Dungeon.depth = depth;
				Level level = TaigaBranch.newLevel(depth);
				Dungeon.level = level;
				// Build terrain without item sprites, which require a running graphics backend.
				level.transitions = new ArrayList<>();
				level.mobs = new HashSet<>();
				level.blobs = new HashMap<>();
				level.traps = new SparseArray<>();
				level.plants = new SparseArray<>();
				level.heaps = new SparseArray<>();
				Random.pushGenerator(Dungeon.seedCurDepth());
				try { invoke(level, "build"); }
				finally { Random.popGenerator(); }
				level.buildFlagMaps();
				level.cleanWalls();
				if (depth == 5 && seed == 1){
					// Simulate a grove saved before +6 existed, then load it through the real save path.
					level.transitions.removeIf(t -> t.type == LevelTransition.Type.REGULAR_EXIT);
					for (int y = 1; y < 19; y++) level.map[20 + y * level.width()] = Terrain.WALL;
					level.locked = true;
					level.customTiles = new ArrayList<>();
					level.customTerrain = new ArrayList<>();
					level.customWalls = new ArrayList<>();
					Bundle saved = new Bundle();
					level.storeInBundle(saved);
					level = new TaigaBossLevel();
					Dungeon.level = level;
					level.restoreFromBundle(saved);
					check(level.locked, "boss fight state lost on restore", seed, depth);
					check(level.map[20 + level.width()] == Terrain.ENTRANCE, "old grove pass stays blocked", seed, depth);
					check(level.transitions.stream().filter(t -> t.type == LevelTransition.Type.REGULAR_EXIT).count() == 1,
							"old grove has duplicate or missing exit", seed, depth);
				}
				check(level.entrance() >= 0, "missing way down", seed, depth);
				if (depth < 10){
					check(level.exit() >= 0, "missing way up", seed, depth);
					if (!reachable(level, level.entrance(), level.exit())) dump(level);
					check(reachable(level, level.entrance(), level.exit()), "unreachable way up", seed, depth);
				}
				if (depth == 6){
					check(level instanceof SwampVillageLevel, "wrong village", seed, depth);
					check(TaigaAssets.SWAMP_TILES.equals(level.tilesTex()), "village still uses forest tiles", seed, depth);
					invoke(level, "createMobs");
					int shops = 0, dungeonMerchants = 0;
					for (Mob mob : level.mobs) if (mob instanceof TownTrader){
						shops++;
						check(((TownTrader) mob).stalls.length >= 4, "shop has no stall", seed, depth);
					}
					for (Mob mob : level.mobs) if (mob instanceof LastHearthMerchant) dungeonMerchants++;
					check(shops == 1 && dungeonMerchants == 1, "village lacks full merchant and herbalist", seed, depth);
				}
				if (depth >= 7 && depth <= 9){
					check(TaigaAssets.SWAMP_TILES.equals(level.tilesTex()), "swamp still uses forest tiles", seed, depth);
					for (int i = 0; i < 20; i++){
						Mob mob = level.createMob();
						check(mob instanceof MireHusk || mob instanceof GraveMoth || mob instanceof BellWraith,
								"swamp generated a stock enemy", seed, depth);
					}
				}
				if (depth == 10){
					check(TaigaAssets.SWAMP_TILES.equals(level.tilesTex()), "boss floor still uses forest tiles", seed, depth);
					invoke(level, "createMobs");
					check(level.mobs.size() == 1 && level.mobs.iterator().next() instanceof GraveWarden,
							"graveyard lacks its warden", seed, depth);
				}
			}
		}
		System.out.println("50 seeds: +5 save migrates; +6 shop stocked; +7 to +9 use custom mobs and tiles; routes valid");
	}

	private static void invoke(Level level, String name) throws Exception {
		Class<?> type = level.getClass();
		while (type != null){
			try {
				Method method = type.getDeclaredMethod(name);
				method.setAccessible(true);
				method.invoke(level);
				return;
			} catch (NoSuchMethodException missing){
				type = type.getSuperclass();
			}
		}
		throw new NoSuchMethodException(name);
	}

	private static boolean reachable(Level level, int from, int to){
		boolean[] seen = new boolean[level.length()];
		ArrayDeque<Integer> queue = new ArrayDeque<>();
		queue.add(from);
		seen[from] = true;
		int w = level.width();
		while (!queue.isEmpty()){
			int cell = queue.removeFirst();
			if (cell == to) return true;
			for (int next : new int[]{cell - 1, cell + 1, cell - w, cell + w}){
				if (next < 0 || next >= seen.length || seen[next] || !level.passable[next]) continue;
				if (Math.abs(next % w - cell % w) > 1) continue;
				seen[next] = true;
				queue.addLast(next);
			}
		}
		return false;
	}

	private static void dump(Level level){
		System.out.println("entrance=" + level.entrance() + " exit=" + level.exit());
		for (int y = 0; y < level.height(); y++){
			StringBuilder row = new StringBuilder();
			for (int x = 0; x < level.width(); x++){
				int c = x + y * level.width();
				row.append(c == level.entrance() ? 'D' : c == level.exit() ? 'U'
						: level.passable[c] ? '.' : '#');
			}
			System.out.println(row);
		}
	}

	private static void check(boolean okay, String issue, long seed, int depth){
		if (!okay) throw new AssertionError(issue + " for seed " + seed + " on +" + depth);
	}
}
