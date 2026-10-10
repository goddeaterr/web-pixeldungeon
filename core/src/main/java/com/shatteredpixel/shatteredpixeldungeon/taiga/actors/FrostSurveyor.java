package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.SurveyRation;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.HighlandSprites;
import com.watabou.utils.Random;

/** A dead trail mapper still driving iron stakes into anything that moves. */
public class FrostSurveyor extends Mob {
	{ spriteClass=HighlandSprites.SurveyorSprite.class; HP=HT=48; defenseSkill=17; EXP=12; maxLvl=28;
	  loot=SurveyRation.class; lootChance=.16f; properties.add(Property.UNDEAD); }
	@Override public int damageRoll(){return Random.NormalIntRange(7,13);}
	@Override public int attackSkill(Char target){return 24;}
	@Override public int attackProc(Char enemy,int damage){
		if(Random.Int(3)==0 && enemy.isAlive())Buff.affect(enemy,Chill.class,3f);
		return super.attackProc(enemy,damage);
	}
}
