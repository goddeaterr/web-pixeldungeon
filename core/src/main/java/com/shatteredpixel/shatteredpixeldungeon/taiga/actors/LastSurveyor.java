package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.AnchorPike;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.HighlandSprites;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/** The survey chief fused to his wind engine. Warned crosswinds can be dodged or sheltered. */
public class LastSurveyor extends Mob {
	private boolean awakened,warning,secondPhase,horizontal;
	private int line=-1,cooldown=3;
	{ spriteClass=HighlandSprites.LastSurveyorSprite.class; HP=HT=165; defenseSkill=19; EXP=46; maxLvl=30;
	  lootChance=0; properties.add(Property.BOSS); properties.add(Property.INORGANIC); }
	@Override public int damageRoll(){return Random.NormalIntRange(9,15);}
	@Override public int attackSkill(Char target){return 24;}
	private int anchors(){int count=0;for(Mob m:Dungeon.level.mobs)if(m instanceof WindAnchor && m.isAlive())count++;return count;}
	@Override public int drRoll(){return super.drRoll()+Random.IntRange(2,4)+anchors()*2;}
	@Override public void notice(){
		super.notice();
		if(!awakened){awakened=true;Dungeon.level.seal();BossHealthBar.assignBoss(this);
			Music.INSTANCE.play(Assets.Music.HALLS_BOSS,true);yell(Messages.get(this,"notice"));}
	}
	@Override protected boolean act(){
		if(awakened && Dungeon.hero!=null && Dungeon.hero.isAlive()){
			if(!secondPhase && HP<=HT/2){secondPhase=true;cooldown=1;yell(Messages.get(this,"phase"));}
			if(warning){
				warning=false;
				int heroLine=horizontal?Dungeon.hero.pos/Dungeon.level.width():Dungeon.hero.pos%Dungeon.level.width();
				if(heroLine==line && !sheltered(Dungeon.hero.pos)){
					Dungeon.hero.damage(Random.IntRange(6,9)+anchors()*2+(secondPhase?3:0),this);
					if(Dungeon.hero.isAlive())Buff.affect(Dungeon.hero,Chill.class,3f);
					GLog.w(Messages.get(this,"struck"));
				}else GLog.i(Messages.get(this,"escaped"));
				line=-1;cooldown=secondPhase?2:4;spend(TICK);return true;
			}
			if(Dungeon.level.distance(pos,Dungeon.hero.pos)<=8 && --cooldown<=0){
				warning=true;horizontal=Random.Int(2)==0;
				line=horizontal?Dungeon.hero.pos/Dungeon.level.width():Dungeon.hero.pos%Dungeon.level.width();
				int w=Dungeon.level.width();
				for(int n=2;n<w-2;n+=2){int c=horizontal?n+line*w:line+n*w;
					if(Dungeon.level.insideMap(c))CellEmitter.get(c).burst(Speck.factory(Speck.DISCOVER),1);}
				yell(Messages.get(this,horizontal?"warning_row":"warning_column"));spend(TICK);return true;
			}
		}
		return super.act();
	}
	private boolean sheltered(int c){
		int w=Dungeon.level.width();
		for(int n:new int[]{c-1,c+1,c-w,c+w})
			if(Dungeon.level.insideMap(n) && Dungeon.level.map[n]==Terrain.STATUE) return true;
		return false;
	}
	@Override public void die(Object cause){
		super.die(cause);Dungeon.level.unseal();GameScene.bossSlain();
		GLog.h(Messages.get(this,"defeated"));
		Dungeon.level.drop(new AnchorPike().identify(false),pos).sprite.drop();
		Dungeon.level.drop(new PotionOfStrength(),pos).sprite.drop();
		Dungeon.level.drop(new ScrollOfUpgrade(),pos).sprite.drop();
		Dungeon.level.playLevelMusic();
	}
	@Override public void storeInBundle(Bundle b){super.storeInBundle(b);b.put("awakened",awakened);b.put("warning",warning);
		b.put("second_phase",secondPhase);b.put("horizontal",horizontal);b.put("line",line);b.put("cooldown",cooldown);}
	@Override public void restoreFromBundle(Bundle b){super.restoreFromBundle(b);awakened=b.getBoolean("awakened");warning=b.getBoolean("warning");
		secondPhase=b.getBoolean("second_phase");horizontal=b.getBoolean("horizontal");line=b.getInt("line");cooldown=b.getInt("cooldown");
		if(awakened)BossHealthBar.assignBoss(this);}
}
