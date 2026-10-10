package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Random;

/** Three new layouts: a rockfall, a surveyed camp, and fractured black ice. */
public class HighlandRooms {
	private static void doors(StandardRoom room){
		for (Room.Door d:room.connected.values()) d.set(Room.Door.Type.REGULAR);
	}
	public static class Rockfall extends StandardRoom {
		@Override public float[] sizeCatProbs(){ return new float[]{2,3,2}; }
		@Override public void paint(Level level){
			Painter.fill(level,this,Terrain.WALL); Painter.fill(level,this,1,Terrain.EMPTY); doors(this);
			int w=level.width();
			for (int y=top+2;y<bottom-1;y++) for (int x=left+2;x<right-1;x++)
				if (Random.Int(9)==0) level.map[x+y*w]=Terrain.WALL_DECO;
		}
	}
	public static class SurveyCamp extends StandardRoom {
		@Override public float[] sizeCatProbs(){ return new float[]{1,3,2}; }
		@Override public void paint(Level level){
			Painter.fill(level,this,Terrain.WALL_DECO); Painter.fill(level,this,1,Terrain.EMPTY_SP); doors(this);
			int w=level.width();
			for (int x=left+2;x<right-1;x+=4) for (int y=top+2;y<bottom-1;y+=4)
				level.map[x+y*w]=Terrain.CUSTOM_DECO;
		}
	}
	public static class IceTraverse extends StandardRoom {
		@Override public float[] sizeCatProbs(){ return new float[]{1,2,3}; }
		@Override public void paint(Level level){
			Painter.fill(level,this,Terrain.WALL); Painter.fill(level,this,1,Terrain.EMPTY); doors(this);
			int w=level.width(),cx=(left+right)/2,cy=(top+bottom)/2;
			for (int y=top+2;y<bottom-1;y++) for (int x=left+2;x<right-1;x++){
				int c=x+y*w;
				if (Math.abs(x-cx)<=1 || Math.abs(y-cy)<=1) level.map[c]=Terrain.EMPTY_SP;
				else if (Random.Int(4)!=0) level.map[c]=Terrain.WATER;
			}
		}
	}
}
