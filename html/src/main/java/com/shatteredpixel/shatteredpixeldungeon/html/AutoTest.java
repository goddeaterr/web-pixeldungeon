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

package com.shatteredpixel.shatteredpixeldungeon.html;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.utils.PathFinder;

import java.util.ArrayDeque;

/**
 * Smoke-test driver, only active when switched on from the browser console with spd.autotest(true).
 * Not part of the game: it plays a simple run by giving the hero the same commands a player's taps
 * give (Hero.handle + next, like GameScene's cell listener, and Hero.rest like the wait button):
 * fight visible enemies, rest when hurt, otherwise walk to the stairs down and descend.
 * Used to check that a real run reaches the first boss without errors in the console.
 */
public class AutoTest {

	private static boolean enabled;
	private static int actions;
	private static String last = "";
	private static int stuck;

	public static void setEnabled( boolean on ){
		enabled = on;
	}

	public static String status(){
		Hero hero = Dungeon.hero;
		if (hero == null) return "no hero, scene=" + Game.scene().getClass().getSimpleName();
		return "enabled=" + enabled + " depth=" + Dungeon.depth + " hp=" + hero.HP + "/" + hero.HT + " lvl=" + hero.lvl
				+ " pos=" + hero.pos + " exit=" + (Dungeon.level == null ? -1 : Dungeon.level.exit())
				+ " ready=" + hero.ready + " alive=" + hero.isAlive() + " actions=" + actions + " last=" + last
				+ " scene=" + Game.scene().getClass().getSimpleName() + (stuck > 0 ? "\n" + around(hero.pos, 9) : "");
	}

	//terrain codes around a cell, '*' = hero, lowercase hex = impassable, '.' = passable; for debugging the driver
	private static String around( int center, int radius ){
		Level level = Dungeon.level;
		int w = level.width();
		StringBuilder sb = new StringBuilder();
		for (int y = center / w - radius; y <= center / w + radius; y++){
			for (int x = center % w - radius; x <= center % w + radius; x++){
				if (x < 0 || y < 0 || x >= w || y >= level.height()) { sb.append(' '); continue; }
				int c = x + y * w;
				if (c == center) sb.append('*');
				else if (c == level.exit()) sb.append('X');
				else sb.append(level.passable[c] ? '.' : Character.forDigit(level.map[c] % 36, 36));
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	//called on the game thread after every frame
	public static void step(){
		if (!enabled || !(Game.scene() instanceof GameScene)) return;
		Hero hero = Dungeon.hero;
		if (hero == null || !hero.isAlive() || !hero.ready || Dungeon.level == null) return;

		//dismiss story popups and other windows, like tapping outside of them
		Gizmo window = Game.scene().getFirstAvailable(Window.class);
		if (window != null){
			((Window) window).hide();
			last = "close " + window.getClass().getSimpleName();
			return;
		}

		Level level = Dungeon.level;
		Mob target = null;
		int bestDist = Integer.MAX_VALUE;
		for (Mob m : level.mobs.toArray(new Mob[0])){
			if (m.alignment == Char.Alignment.ENEMY && level.heroFOV[m.pos] && m.isAlive()){
				int d = level.distance(hero.pos, m.pos);
				if (d < bestDist){
					bestDist = d;
					target = m;
				}
			}
		}

		if (target != null && (bestDist <= 1 || hero.HP > hero.HT / 3)){
			command(target.pos, "attack " + target.getClass().getSimpleName());
			return;
		}

		Hunger hunger = hero.buff(Hunger.class);
		if (hunger != null && hunger.isStarving() && target == null){
			Food food = hero.belongings.getItem(Food.class);
			if (food != null){
				last = "eat";
				actions++;
				food.execute(hero, Food.AC_EAT);
				return;
			}
		}

		if (hero.HP < hero.HT * 2 / 3 && target == null){
			last = "rest";
			actions++;
			hero.rest(false);
			return;
		}

		int exit = level.exit();
		if (hero.pos == exit){
			command(exit, "descend");
			return;
		}

		int next = nextStepTo(level, hero.pos, exit);
		if (next == -1){
			//nothing reachable: search for hidden doors
			stuck++;
			last = "search";
			actions++;
			hero.rest(false);
			return;
		}
		command(next, "move");
	}

	private static void command( int cell, String what ){
		Hero hero = Dungeon.hero;
		last = what + "@" + cell;
		actions++;
		if (hero.handle(cell)){
			hero.next();
		} else {
			hero.rest(false);
		}
	}

	//breadth first search on the real map, avoiding visible traps and hazards
	private static int nextStepTo( Level level, int from, int to ){
		int length = level.length();
		int[] prev = new int[length];
		java.util.Arrays.fill(prev, -2);
		ArrayDeque<Integer> queue = new ArrayDeque<>();
		queue.add(from);
		prev[from] = -1;
		while (!queue.isEmpty()){
			int cell = queue.poll();
			if (cell == to) break;
			for (int offset : PathFinder.NEIGHBOURS8){
				int n = cell + offset;
				if (n < 0 || n >= length || prev[n] != -2) continue;
				if (!level.passable[n] || level.avoid[n]) continue;
				Trap trap = level.traps.get(n);
				if (trap != null && trap.visible && trap.active) continue;
				if (n != to && Actor.findChar(n) != null) continue;
				prev[n] = cell;
				queue.add(n);
			}
		}
		if (prev[to] == -2) return -1;
		int cell = to;
		while (prev[cell] != from && prev[cell] != -1){
			cell = prev[cell];
		}
		return cell;
	}
}
