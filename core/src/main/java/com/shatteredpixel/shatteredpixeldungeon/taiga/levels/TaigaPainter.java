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

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;

import java.util.ArrayList;

//MOD (taiga town): rooms of the taiga floors are clearings in the forest, so there are no doors between them
public class TaigaPainter extends RegularPainter {

	@Override
	protected void decorate( Level level, ArrayList<Room> rooms ) {
		int[] map = level.map;
		int w = level.width();
		int l = level.length();

		//gaps in the trees instead of doors
		for (int i = 0; i < l; i++){
			if (map[i] == Terrain.DOOR) map[i] = Terrain.EMPTY;
		}

		for (int i = w + 1; i < l - w - 1; i++) {
			if (map[i] != Terrain.EMPTY) continue;

			//twigs, cones and tracks in the snow, more of them under the trees
			int trees = (map[i + 1] == Terrain.WALL ? 1 : 0) + (map[i - 1] == Terrain.WALL ? 1 : 0)
					+ (map[i + w] == Terrain.WALL ? 1 : 0) + (map[i - w] == Terrain.WALL ? 1 : 0);
			if (Random.Int(16) < trees * trees) {
				map[i] = Terrain.EMPTY_DECO;
				continue;
			}

			//a lone spruce in open snow; never next to anything it could block
			if (Random.Int(28) == 0) {
				boolean open = true;
				for (int dy = -1; dy <= 1 && open; dy++){
					for (int dx = -1; dx <= 1; dx++){
						int n = map[i + dx + dy * w];
						if (n != Terrain.EMPTY && n != Terrain.EMPTY_DECO && n != Terrain.GRASS){
							open = false;
							break;
						}
					}
				}
				if (open) map[i] = Terrain.STATUE;
			}
		}

		//the forest is a little denser where it borders water
		for (int i = w; i < l - w; i++) {
			if (map[i] == Terrain.WALL && map[i + w] == Terrain.WATER && Random.Int(3) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}
	}
}
