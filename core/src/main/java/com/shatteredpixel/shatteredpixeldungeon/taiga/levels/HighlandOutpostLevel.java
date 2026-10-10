package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.SurveyMerchant;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;

/** +16: the last staffed shelter, built into a wind-scoured survey station. */
public class HighlandOutpostLevel extends Level {
	private static final int W=35,H=27;
	private int cell(int x,int y){return x+y*W;}
	{ color1 = 0x8898a5; color2 = 0x111923; }
	@Override public String tilesTex(){ return TaigaAssets.HIGHLAND_TILES; }
	@Override public String waterTex(){ return TaigaAssets.HIGHLAND_WATER; }
	@Override protected boolean build(){
		setSize(W,H);
		Arrays.fill(map, Terrain.WALL);
		for (int y=2; y<H-2; y++) for (int x=2; x<W-2; x++)
			map[cell(x,y)] = Random.Int(21)==0 ? Terrain.EMPTY_DECO : Terrain.EMPTY;
		// The hall is sheltered, while the yard's markers stand exposed to the wind.
		for (int y=4; y<=19; y++) for (int x=3; x<=15; x++)
			map[cell(x,y)] = x==3 || x==15 || y==4 || y==19 ? Terrain.WALL_DECO : Terrain.EMPTY_SP;
		for (int x=5; x<=13; x+=4) map[cell(x,6)] = Terrain.STATUE;
		map[cell(15,12)] = Terrain.DOOR;
		map[cell(8,8)] = Terrain.REGION_DECO_ALT;
		for (int y=5; y<=18; y+=4) for (int x=22; x<=30; x+=4) map[cell(x,y)] = Terrain.CUSTOM_DECO;
		for (int y=3; y<H-2; y++) for (int x=16; x<=18; x++) map[cell(x,y)] = Terrain.EMPTY_SP;
		for (int x=13; x<=18; x++) map[cell(x,12)] = Terrain.EMPTY_SP;
		for (int x:new int[]{10,24}) map[cell(x,22)]=Terrain.STATUE;
		for (int x:new int[]{7,27}) map[cell(x,23)]=Terrain.CUSTOM_DECO;
		int down=cell(17,H-3), up=cell(17,2);
		map[down]=Terrain.EXIT; map[up]=Terrain.ENTRANCE;
		transitions.add(new LevelTransition(this,down,LevelTransition.Type.REGULAR_ENTRANCE));
		transitions.add(new LevelTransition(this,up,LevelTransition.Type.REGULAR_EXIT));
		Arrays.fill(mapped,true);
		return true;
	}
	@Override protected void createMobs(){ SurveyMerchant m=new SurveyMerchant(); m.pos=cell(8,12); mobs.add(m); }
	@Override protected void createItems(){ stockShop(); }
	private void stockShop(){
		ArrayList<Item> stock=SurveyMerchant.stock(); int n=0;
		for (int y=5; y<=18 && n<stock.size(); y++) for (int x=4; x<=14 && n<stock.size(); x++){
			if (x!=4 && x!=14 && y!=5 && y!=18) continue;
			int c=cell(x,y);
			if (map[c]!=Terrain.EMPTY_SP || heaps.get(c)!=null || findMob(c)!=null) continue;
			Heap heap=drop(stock.get(n++),c); heap.type=Heap.Type.FOR_SALE;
		}
	}
	/** Older saves had a locked +16 endpoint. Open its trail without resetting the run. */
	@Override public void restoreFromBundle(Bundle bundle){
		super.restoreFromBundle(bundle);
		int up=cell(17,2);
		if (map[up]==Terrain.LOCKED_EXIT){
			for (int y=2; y<=6; y++) for (int x=16; x<=18; x++) map[cell(x,y)]=Terrain.EMPTY_SP;
			map[up]=Terrain.ENTRANCE;
			transitions.add(new LevelTransition(this,up,LevelTransition.Type.REGULAR_EXIT));
			boolean merchant=false;
			for (Mob mob:mobs) if (mob instanceof SurveyMerchant) merchant=true;
			if (!merchant){
				createMobs();
				// The headless generation smoke has no texture backend for item icons.
				if (com.badlogic.gdx.Gdx.files != null) stockShop();
			}
			buildFlagMaps();
		}
	}
	@Override public void playLevelMusic(){ Music.INSTANCE.play(Assets.Music.HALLS_TENSE,true); }
	@Override public Mob createMob(){return null;}
	@Override public int mobLimit(){return 0;}
	@Override public Actor addRespawner(){return null;}
	@Override public int randomRespawnCell(Char ch){return -1;}
	@Override public Group addVisuals(){
		Group group=super.addVisuals();
		visuals.add(new com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaTownLevel.Campfire(cell(8,8)));
		visuals.add(new Snowfall(width()*DungeonTilemap.SIZE,height()*DungeonTilemap.SIZE,4f));
		return group;
	}
	@Override public String tileName(int tile){ String s=HighlandTerrain.name(tile); return s!=null?s:super.tileName(tile); }
	@Override public String tileDesc(int tile){ String s=HighlandTerrain.desc(tile); return s!=null?s:super.tileDesc(tile); }
}
