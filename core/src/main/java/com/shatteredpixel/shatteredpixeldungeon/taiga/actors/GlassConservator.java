package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.ConservatorShears;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.OrchardSprites;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/** The orchard's final keeper sweeps rows and columns with warned, dodgeable glass light. */
public class GlassConservator extends Mob {
	private boolean awakened, warning;
	private boolean warnedPhase;
	private int beamLine = -1, cooldown = 3;
	private boolean horizontal;
	{
		spriteClass = OrchardSprites.ConservatorSprite.class;
		HP = HT = 145; defenseSkill = 17; EXP = 40; maxLvl = 26;
		lootChance = 0;
		properties.add(Property.BOSS);
		properties.add(Property.INORGANIC);
	}
	@Override public int damageRoll(){ return Random.NormalIntRange(8,14); }
	@Override public int attackSkill(Char target){ return 21; }
	@Override public int drRoll(){ return super.drRoll()+Random.NormalIntRange(3,6); }
	@Override public void notice(){
		super.notice();
		if (!awakened){
			awakened = true;
			Dungeon.level.seal();
			BossHealthBar.assignBoss(this);
			Music.INSTANCE.play(Assets.Music.HALLS_BOSS,true);
			yell(Messages.get(this,"notice"));
		}
	}
	@Override protected boolean act(){
		if (awakened && Dungeon.hero != null && Dungeon.hero.isAlive()){
			if (!warnedPhase && HP <= HT/2){
				warnedPhase = true;
				cooldown = 1;
				yell(Messages.get(this,"phase"));
			}
			if (warning){
				warning = false;
				int target = horizontal ? Dungeon.hero.pos/Dungeon.level.width() : Dungeon.hero.pos%Dungeon.level.width();
				if (target == beamLine){
					Dungeon.hero.damage(warnedPhase ? Random.IntRange(9,13) : Random.IntRange(6,9),this);
					if (Dungeon.hero.isAlive()) Buff.prolong(Dungeon.hero,Blindness.class,2f);
					GLog.w(Messages.get(this,"shatter"));
				} else GLog.i(Messages.get(this,"dodged"));
				beamLine = -1;
				cooldown = warnedPhase ? 2 : 4;
				spend(TICK);
				return true;
			}
			if (Dungeon.level.distance(pos,Dungeon.hero.pos) <= 7 && --cooldown <= 0){
				warning = true;
				horizontal = Random.Int(2) == 0;
				beamLine = horizontal ? Dungeon.hero.pos/Dungeon.level.width() : Dungeon.hero.pos%Dungeon.level.width();
				int w = Dungeon.level.width();
				for (int n = 2; n < w-2; n += 2){
					int cell = horizontal ? n+beamLine*w : beamLine+n*w;
					if (Dungeon.level.insideMap(cell)) CellEmitter.get(cell).burst(Speck.factory(Speck.DISCOVER),1);
				}
				yell(Messages.get(this,horizontal ? "warning_row" : "warning_column"));
				spend(TICK);
				return true;
			}
		}
		return super.act();
	}
	@Override public void die(Object cause){
		super.die(cause);
		Dungeon.level.unseal();
		GameScene.bossSlain();
		GLog.h(Messages.get(this,"defeated"));
		Dungeon.level.drop(new ConservatorShears().identify(false),pos).sprite.drop();
		Dungeon.level.drop(new PotionOfStrength(),pos).sprite.drop();
		Dungeon.level.drop(new ScrollOfUpgrade(),pos).sprite.drop();
		Dungeon.level.playLevelMusic();
	}
	@Override public void storeInBundle(Bundle bundle){
		super.storeInBundle(bundle);
		bundle.put("awakened",awakened);
		bundle.put("warning",warning);
		bundle.put("warned_phase",warnedPhase);
		bundle.put("beam_line",beamLine);
		bundle.put("horizontal",horizontal);
		bundle.put("cooldown",cooldown);
	}
	@Override public void restoreFromBundle(Bundle bundle){
		super.restoreFromBundle(bundle);
		awakened = bundle.getBoolean("awakened");
		warning = bundle.getBoolean("warning");
		warnedPhase = bundle.getBoolean("warned_phase");
		beamLine = bundle.getInt("beam_line");
		horizontal = bundle.getBoolean("horizontal");
		cooldown = bundle.getInt("cooldown");
		if (awakened) BossHealthBar.assignBoss(this);
	}
}
