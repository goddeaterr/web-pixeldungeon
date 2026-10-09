package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Ashfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.utils.Random;
import java.util.Arrays;

/** +16: a silent threshold. The climb beyond is reserved for the next biome update. */
public class HighlandOutpostLevel extends OrchardWaystationLevel {
	{
		color1 = 0x77777d;
		color2 = 0x1b1b21;
	}
	@Override protected boolean build(){
		setSize(W,H);
		Arrays.fill(map, Terrain.WALL);
		for (int y = 2; y < H-2; y++) for (int x = 2; x < W-2; x++)
			map[cell(x,y)] = Random.Int(60) == 0 ? Terrain.EMPTY_DECO : Terrain.EMPTY_SP;
		for (int y = 4; y <= 18; y += 5) for (int x = 5; x <= 29; x += 6)
			map[cell(x,y)] = Terrain.CUSTOM_DECO;
		for (int y = 3; y <= H-3; y++) for (int x = 16; x <= 18; x++) map[cell(x,y)] = Terrain.EMPTY_SP;
		for (int y = 7; y <= 15; y++) for (int x = 5; x <= 12; x++)
			map[cell(x,y)] = x == 5 || x == 12 || y == 7 || y == 15 ? Terrain.WALL_DECO : Terrain.EMPTY_SP;
		map[cell(12,11)] = Terrain.DOOR;
		int down = cell(17,H-3), closed = cell(17,2);
		map[down] = Terrain.EXIT;
		map[closed] = Terrain.LOCKED_EXIT;
		transitions.add(new LevelTransition(this, down, LevelTransition.Type.REGULAR_ENTRANCE));
		Arrays.fill(mapped, true);
		return true;
	}
	@Override protected void createMobs(){}
	@Override protected void createItems(){}
	@Override protected boolean hasFire(){ return false; }
	@Override public Group addVisuals(){
		super.addVisuals();
		visuals.add(new Ashfall(width()*DungeonTilemap.SIZE, height()*DungeonTilemap.SIZE, 2f));
		return visuals;
	}
	@Override public String tileName(int tile){
		return tile == Terrain.LOCKED_EXIT ? Messages.get(this,"closed_name") : super.tileName(tile);
	}
	@Override public String tileDesc(int tile){
		return tile == Terrain.LOCKED_EXIT ? Messages.get(this,"closed_desc") : super.tileDesc(tile);
	}
}
