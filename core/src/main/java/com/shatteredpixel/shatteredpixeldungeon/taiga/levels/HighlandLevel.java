package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.SlateCrawler;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.FrostSurveyor;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.WindEffigy;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.WindglassShard;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.SurveyRation;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

/** +17–19: a bare ridge whose remaining life gives way to ice and machinery. */
public class HighlandLevel extends TaigaLevel {
	{ color1=0x8294a0; color2=0x101722; }
	@Override public String tilesTex(){ return TaigaAssets.HIGHLAND_TILES; }
	@Override public String waterTex(){ return TaigaAssets.HIGHLAND_WATER; }
	@Override protected int standardRooms(boolean forceMax){ return forceMax?11:8+Random.Int(3); }
	@Override protected ArrayList<Room> initRooms(){
		ArrayList<Room> rooms=new ArrayList<>();
		rooms.add(roomEntrance=new TaigaRooms.TaigaEntranceRoom());
		rooms.add(roomExit=new TaigaRooms.TaigaExitRoom());
		int count=standardRooms(false);
		for (int i=0;i<count;i++){
			StandardRoom r;
			if (Dungeon.depth==17 && Random.Int(3)!=0) r=new HighlandRooms.Rockfall();
			else if (Dungeon.depth==18 && Random.Int(3)!=0) r=new HighlandRooms.SurveyCamp();
			else if (Dungeon.depth==19 && Random.Int(3)!=0) r=new HighlandRooms.IceTraverse();
			else switch(Random.Int(3)){
				case 0:r=new HighlandRooms.Rockfall();break;
				case 1:r=new HighlandRooms.SurveyCamp();break;
				default:r=new HighlandRooms.IceTraverse();break;
			}
			if (!r.setSizeCat(count-i)){i--;continue;}
			i+=r.sizeFactor()-1; rooms.add(r);
		}
		return rooms;
	}
	@Override protected Painter painter(){
		return new HighlandPainter().setWater(Dungeon.depth==19?.28f:.08f,3)
				.setGrass(0f,0).setTraps(nTraps(),trapClasses(),trapChances());
	}
	@Override protected boolean build(){ if (!super.build()) return false; connectTrail(); return true; }
	private void connectTrail(){
		int start=entrance(),goal=exit(),w=width(),h=height();
		int[] cost=new int[length()],previous=new int[length()];
		Arrays.fill(cost,Integer.MAX_VALUE); Arrays.fill(previous,-1);
		ArrayDeque<Integer> q=new ArrayDeque<>(); cost[start]=0; q.add(start);
		while(!q.isEmpty()){
			int c=q.removeFirst(),x=c%w,y=c/w;
			for (int n:new int[]{c-1,c+1,c-w,c+w}){
				if(n<0||n>=length())continue;
				int nx=n%w,ny=n/w;
				if(nx<1||nx>=w-1||ny<1||ny>=h-1||Math.abs(nx-x)+Math.abs(ny-y)!=1)continue;
				int wall=(Terrain.flags[map[n]]&Terrain.PASSABLE)!=0?0:1;
				if(cost[c]+wall>=cost[n])continue;
				cost[n]=cost[c]+wall; previous[n]=c;
				if(wall==0)q.addFirst(n);else q.addLast(n);
			}
		}
		for(int c=goal;c!=start&&c>=0;c=previous[c]) if((Terrain.flags[map[c]]&Terrain.PASSABLE)==0){
			map[c]=Terrain.EMPTY_SP; traps.remove(c);
		}
	}
	@Override public int mobLimit(){ return 9+(Dungeon.depth-17)*2; }
	@Override public Mob createMob(){
		switch(Random.Int(10)){
			case 0:case 1:case 2:case 3:return new SlateCrawler();
			case 4:case 5:case 6:return new FrostSurveyor();
			default:return new WindEffigy();
		}
	}
	@Override protected void createItems(){
		for(int i=0;i<4+Random.Int(3);i++) dropHighland(Random.Int(3)==0?new WindglassShard().quantity(2):new SurveyRation());
		if(Dungeon.depth!=18)dropHighland(new ScrollOfUpgrade());
		if(Dungeon.depth==18)dropHighland(new PotionOfStrength());
		if(Random.Int(2)==0)dropHighland(new PotionOfHealing());
	}
	private void dropHighland(Item item){ int c=randomDropCell(); if(c!=-1)drop(item,c).type=Heap.Type.HEAP; }
	@Override public void playLevelMusic(){ Music.INSTANCE.play(Assets.Music.HALLS_TENSE,true); }
	@Override public Group addVisuals(){
		Group g=super.addVisuals();
		visuals.add(new Snowfall(width()*DungeonTilemap.SIZE,height()*DungeonTilemap.SIZE,5f+(Dungeon.depth-17)*1.5f));
		return g;
	}
	@Override public String tileName(int tile){String s=HighlandTerrain.name(tile);return s!=null?s:super.tileName(tile);}
	@Override public String tileDesc(int tile){String s=HighlandTerrain.desc(tile);return s!=null?s:super.tileDesc(tile);}
}
