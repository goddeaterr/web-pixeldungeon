package com.shatteredpixel.shatteredpixeldungeon.taiga.effects;

import com.watabou.noosa.Game;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

/** Low grey vapour carried sideways through the snowy swamp. */
public class MireMist extends Emitter {
	public MireMist(float width, float height, float intensity){
		pos(0, 0, width, height);
		pour(Wisp.FACTORY, 80f / Math.max(1f, (width*height)/256f) / intensity);
	}
	public static class Wisp extends PixelParticle {
		public static final Factory FACTORY = new Factory(){
			@Override public void emit(Emitter emitter, int index, float x, float y){
				((Wisp)emitter.recycle(Wisp.class)).reset(x,y);
			}
		};
		private float phase;
		public Wisp(){ color(0x8d9a9e); }
		private void reset(float x,float y){
			revive(); this.x=x; this.y=y;
			left=lifespan=Random.Float(2f,4f);
			speed.set(Random.Float(5f,12f),Random.Float(-2f,1f));
			size(Random.Int(3)==0 ? 3 : 2);
			phase=Random.Float(6.28f); am=0;
		}
		@Override public void update(){
			super.update();
			phase += Game.elapsed;
			speed.y += (float)Math.sin(phase)*Game.elapsed;
			float p=left/lifespan;
			am=(p>0.75f ? (1-p)/0.25f : Math.min(1f,p*2.5f))*0.32f;
		}
	}
}
