package com.shatteredpixel.shatteredpixeldungeon.taiga.effects;

import com.watabou.noosa.Game;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

/** Slow dark ash drifting across the orchard and the final outpost. */
public class Ashfall extends Emitter {
	public Ashfall(float width, float height, float intensity){
		pos(0, -16, width, height);
		pour(Ash.FACTORY, 38f / Math.max(1f, (width * height) / 256f) / intensity);
	}
	public static class Ash extends PixelParticle {
		public static final Factory FACTORY = new Factory(){
			@Override public void emit(Emitter emitter, int index, float x, float y){
				((Ash) emitter.recycle(Ash.class)).reset(x, y);
			}
		};
		private float phase;
		public Ash(){ color(0x9c9996); }
		private void reset(float x, float y){
			revive(); this.x = x; this.y = y;
			left = lifespan = Random.Float(3f, 5f);
			speed.set(Random.Float(8f, 18f), Random.Float(2f, 7f));
			size(Random.Int(5) == 0 ? 2 : 1);
			phase = Random.Float(6.28f); am = 0;
		}
		@Override public void update(){
			super.update();
			phase += Game.elapsed * 1.5f;
			speed.x += (float)Math.sin(phase) * Game.elapsed * 2f;
			float p = left / lifespan;
			am = (p > 0.8f ? (1f-p)/0.2f : Math.min(1f, p*3f)) * 0.65f;
		}
	}
}
