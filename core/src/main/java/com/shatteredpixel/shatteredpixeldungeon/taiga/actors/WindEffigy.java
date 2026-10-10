package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.WindglassShard;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.HighlandSprites;
import com.watabou.utils.Random;

/** A survey vane made mobile by the storm trapped in its iron core. */
public class WindEffigy extends Mob {
	{ spriteClass=HighlandSprites.EffigySprite.class; HP=HT=40; defenseSkill=20; EXP=13; maxLvl=28;
	  loot=WindglassShard.class; lootChance=.28f; properties.add(Property.INORGANIC); }
	@Override public int damageRoll(){return Random.NormalIntRange(6,11);}
	@Override public int attackSkill(Char target){return 25;}
	@Override public int drRoll(){return super.drRoll()+Random.IntRange(2,4);}
	@Override public int attackProc(Char enemy,int damage){
		if(Random.Int(5)==0 && enemy.isAlive())Buff.prolong(enemy,Blindness.class,2f);
		return super.attackProc(enemy,damage);
	}
}
