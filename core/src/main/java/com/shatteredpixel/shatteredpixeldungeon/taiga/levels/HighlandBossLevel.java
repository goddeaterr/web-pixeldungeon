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
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.LastSurveyor;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.WindAnchor;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;
import java.util.Arrays;

/** +20: four breakable wind anchors surround a sheltered central fighting ground. */
public class HighlandBossLevel extends Level {
	private static final int W=31,H=31;
	private int cell(int x,int y){return x+y*W;}
	{color1=0x78909c;color2=0x0b111c;}
	@Override public String tilesTex(){return TaigaAssets.HIGHLAND_TILES;}
	@Override public String waterTex(){return TaigaAssets.HIGHLAND_WATER;}
	@Override public void playLevelMusic(){Music.INSTANCE.play(locked?Assets.Music.HALLS_BOSS:Assets.Music.HALLS_TENSE,true);}
	@Override protected boolean build(){
		setSize(W,H);Arrays.fill(map,Terrain.WALL);
		for(int y=2;y<H-2;y++)for(int x=2;x<W-2;x++)
			map[cell(x,y)]=x==2||x==W-3||y==2||y==H-3?Terrain.WALL_DECO
					:Random.Int(14)==0?Terrain.EMPTY_DECO:Terrain.EMPTY_SP;
		// Windbreak pillars create shelter without closing any route through the arena.
		for(int x:new int[]{7,12,18,23})for(int y:new int[]{8,13,18,23})map[cell(x,y)]=Terrain.STATUE;
		for(int y=5;y<=25;y+=5){map[cell(4,y)]=Terrain.CUSTOM_DECO;map[cell(26,y)]=Terrain.CUSTOM_DECO;}
		for(int x=5;x<26;x++){if(x%4!=0){map[cell(x,10)]=Terrain.WATER;map[cell(x,20)]=Terrain.WATER;}}
		for(int y=3;y<H-2;y++)for(int x=14;x<=16;x++)map[cell(x,y)]=Terrain.EMPTY_SP;
		int down=cell(15,H-2),closed=cell(15,2);
		map[down]=Terrain.EXIT;map[closed]=Terrain.LOCKED_EXIT;
		transitions.add(new LevelTransition(this,down,LevelTransition.Type.REGULAR_ENTRANCE));
		return true;
	}
	@Override protected void createMobs(){
		LastSurveyor boss=new LastSurveyor();boss.pos=cell(15,15);mobs.add(boss);
		for(int c:new int[]{cell(5,5),cell(25,5),cell(5,25),cell(25,25)}){
			WindAnchor anchor=new WindAnchor();anchor.pos=c;mobs.add(anchor);
		}
	}
	@Override protected void createItems(){drop(new PotionOfHealing(),cell(15,H-5));}
	@Override public Mob createMob(){return null;}
	@Override public int mobLimit(){return 0;}
	@Override public Actor addRespawner(){return null;}
	@Override public int randomRespawnCell(Char ch){return cell(15,H-3);}
	@Override public Group addVisuals(){super.addVisuals();visuals.add(new Snowfall(width()*DungeonTilemap.SIZE,height()*DungeonTilemap.SIZE,8f));return visuals;}
	@Override public String tileName(int tile){String s=HighlandTerrain.name(tile);return s!=null?s:super.tileName(tile);}
	@Override public String tileDesc(int tile){String s=HighlandTerrain.desc(tile);return s!=null?s:super.tileDesc(tile);}
}
