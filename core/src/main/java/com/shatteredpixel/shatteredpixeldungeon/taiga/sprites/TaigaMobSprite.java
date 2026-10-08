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

package com.shatteredpixel.shatteredpixeldungeon.taiga.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;

/**
 * MOD (taiga town): all village characters share one sheet layout (mod/taiga_art.py):
 * 16x16 frames, one row per character, 12 frames per row:
 * 0-1 idle, 2-5 run, 6-8 attack, 9-11 death.
 */
public abstract class TaigaMobSprite extends MobSprite {

	public static final int FRAMES_PER_ROW = 12;

	protected void setup( String sheet, int row, int idleFps ){
		texture( sheet );
		TextureFilm frames = new TextureFilm( texture, 16, 16 );
		int b = row * FRAMES_PER_ROW;

		idle = new Animation( idleFps, true );
		idle.frames( frames, b, b, b, b + 1 );

		run = new Animation( 10, true );
		run.frames( frames, b + 2, b + 3, b + 4, b + 5 );

		attack = new Animation( 14, false );
		attack.frames( frames, b + 6, b + 7, b + 8, b );

		die = new Animation( 10, false );
		die.frames( frames, b + 9, b + 10, b + 11 );

		play( idle );
	}
}
