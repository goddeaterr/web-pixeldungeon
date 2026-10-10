package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.WindglassShard;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.HighlandSprites;
import com.watabou.utils.Random;

/** A stone-backed burrower that attacks ankles from loose scree. */
public class SlateCrawler extends Mob {
	{ spriteClass=HighlandSprites.CrawlerSprite.class; HP=HT=55; defenseSkill=15; EXP=11; maxLvl=28;
	  loot=WindglassShard.class; lootChance=.18f; }
	@Override public int damageRoll(){return Random.NormalIntRange(7,12);}
	@Override public int attackSkill(Char target){return 22;}
	@Override public int drRoll(){return super.drRoll()+Random.IntRange(3,6);}
	@Override public int attackProc(Char enemy,int damage){
		if(Random.Int(4)==0 && enemy.isAlive())Buff.prolong(enemy,Cripple.class,2f);
		return super.attackProc(enemy,damage);
	}
}
