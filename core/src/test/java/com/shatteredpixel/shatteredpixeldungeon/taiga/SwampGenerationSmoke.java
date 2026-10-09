package com.shatteredpixel.shatteredpixeldungeon.taiga;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.SwampVillageLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.TownTrader;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.GraveWarden;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import com.watabou.utils.SparseArray;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;

/** Fast deterministic checks for the new upper branch, runnable with :core:swampSmoke. */
public class SwampGenerationSmoke {
	public static void main(String[] args) throws Exception {
		Game.version = "test";
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
				check(level.entrance() >= 0, "missing way down", seed, depth);
				if (depth < 10){
					check(level.exit() >= 0, "missing way up", seed, depth);
					if (!reachable(level, level.entrance(), level.exit())) dump(level);
					check(reachable(level, level.entrance(), level.exit()), "unreachable way up", seed, depth);
				}
				if (depth == 6){
					check(level instanceof SwampVillageLevel, "wrong village", seed, depth);
					invoke(level, "createMobs");
					int shops = 0;
					for (Mob mob : level.mobs) if (mob instanceof TownTrader){
						shops++;
						check(((TownTrader) mob).stalls.length >= 4, "shop has no stall", seed, depth);
					}
					check(shops == 2, "village lacks two shops", seed, depth);
				}
				if (depth == 10){
					invoke(level, "createMobs");
					check(level.mobs.size() == 1 && level.mobs.iterator().next() instanceof GraveWarden,
							"graveyard lacks its warden", seed, depth);
				}
			}
		}
		System.out.println("50 seeds: floors +5 to +10 have valid routes and two shopkeepers on +6");
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
