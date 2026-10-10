package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;
import java.util.ArrayList;

/** Sparse iron survey markers and slate rubble; decoration thins with altitude. */
public class HighlandPainter extends RegularPainter {
	@Override protected void decorate(Level level, ArrayList<Room> rooms){
		int w=level.width();
		for (int i=w+1;i<level.length()-w-1;i++){
			if (level.map[i]==Terrain.DOOR) level.map[i]=Terrain.EMPTY_SP;
			if (level.map[i]!=Terrain.EMPTY) continue;
			int markerRate=Dungeon.depth==18?45:Dungeon.depth==17?120:160;
			if (Random.Int(markerRate)==0) level.map[i]=Terrain.CUSTOM_DECO;
			else if (Random.Int(16+(Dungeon.depth-17)*9)==0) level.map[i]=Terrain.EMPTY_DECO;
		}
	}
}
