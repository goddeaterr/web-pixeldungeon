package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.GlassConservator;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Ashfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;

/** +15: the shattered conservatory, a purpose-built arena of trellises and flooded planting beds. */
public class OrchardBossLevel extends Level {
	private static final int W = 31, H = 31;
	private int cell(int x,int y){ return x+y*W; }
	{
		color1 = 0x85828b;
		color2 = 0x181b23;
	}
	@Override public String tilesTex(){ return TaigaAssets.ORCHARD_TILES; }
	@Override public String waterTex(){ return TaigaAssets.ORCHARD_WATER; }
	@Override public void playLevelMusic(){ Music.INSTANCE.play(locked ? Assets.Music.HALLS_BOSS : Assets.Music.HALLS_TENSE, true); }
	@Override protected boolean build(){
		setSize(W,H);
		for (int y = 2; y < H-2; y++) for (int x = 2; x < W-2; x++){
			map[cell(x,y)] = Random.Int(12) == 0 ? Terrain.EMPTY_DECO : Terrain.EMPTY_SP;
			if (x == 2 || x == W-3 || y == 2 || y == H-3) map[cell(x,y)] = Terrain.WALL_DECO;
		}
		for (int x : new int[]{6,10,20,24}) for (int y : new int[]{7,12,18,23})
			map[cell(x,y)] = Terrain.STATUE;
		for (int x = 5; x < W-5; x++){
			if (x % 4 == 0) continue;
			map[cell(x,9)] = Terrain.WATER;
			map[cell(x,20)] = Terrain.WATER;
		}
		for (int y = 3; y <= H-3; y++) for (int x = 14; x <= 16; x++) map[cell(x,y)] = Terrain.EMPTY_SP;
		for (int y = H-3; y <= H-2; y++) map[cell(15,y)] = Terrain.EMPTY_SP;
		int down = cell(15,H-2), up = cell(15,2);
		map[down] = Terrain.EXIT;
		map[up] = Terrain.ENTRANCE;
		transitions.add(new LevelTransition(this, down, LevelTransition.Type.REGULAR_ENTRANCE));
		transitions.add(new LevelTransition(this, up, LevelTransition.Type.REGULAR_EXIT));
		return true;
	}
	@Override protected void createMobs(){
		GlassConservator boss = new GlassConservator();
		boss.pos = cell(15,13);
		mobs.add(boss);
	}
	@Override protected void createItems(){ drop(new PotionOfHealing(),cell(15,H-5)); }
	@Override public Mob createMob(){ return null; }
	@Override public int mobLimit(){ return 0; }
	@Override public Actor addRespawner(){ return null; }
	@Override public int randomRespawnCell(Char ch){ return cell(15,H-3); }
	@Override public Group addVisuals(){
		super.addVisuals();
		visuals.add(new Ashfall(width()*DungeonTilemap.SIZE,height()*DungeonTilemap.SIZE,2f));
		return visuals;
	}
	@Override public String tileName(int tile){
		String name = OrchardTerrain.name(tile);
		return name != null ? name : super.tileName(tile);
	}
	@Override public String tileDesc(int tile){
		String desc = OrchardTerrain.desc(tile);
		return desc != null ? desc : super.tileDesc(tile);
	}
}
