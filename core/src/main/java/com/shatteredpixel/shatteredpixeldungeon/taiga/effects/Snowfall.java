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

package com.shatteredpixel.shatteredpixeldungeon.taiga.effects;

import com.watabou.noosa.Game;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

//MOD (taiga town): light snow drifting over the whole village
public class Snowfall extends Emitter {

	public Snowfall( float width, float height ){
		super();
		pos( 0, -16, width, height );
		//about one flake per 30 tiles per second
		pour( Flake.FACTORY, 30f / Math.max(1f, (width * height) / 256f) );
	}

	public static class Flake extends PixelParticle {

		public static final Factory FACTORY = new Factory() {
			@Override
			public void emit( Emitter emitter, int index, float x, float y ) {
				((Flake)emitter.recycle( Flake.class )).reset( x, y );
			}

			@Override
			public boolean lightMode() {
				return false;
			}
		};

		private float phase;

		public Flake(){
			super();
			color( 0xFFFFFF );
		}

		public void reset( float x, float y ){
			revive();
			this.x = x;
			this.y = y;
			left = lifespan = Random.Float( 3f, 6f );
			speed.set( Random.Float( -3f, 3f ), Random.Float( 6f, 12f ) );
			size( Random.Int( 3 ) == 0 ? 2 : 1 );
			phase = Random.Float( 6.28f );
			am = 0;
		}

		@Override
		public void update() {
			super.update();
			phase += Game.elapsed * 2f;
			speed.x += (float)Math.sin( phase ) * 4f * Game.elapsed;
			float p = left / lifespan;
			am = p > 0.85f ? (1f - p) / 0.15f : Math.min( 1f, p * 3f );
			am *= 0.85f;
		}
	}
}
