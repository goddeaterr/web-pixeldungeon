package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.HighlandSprites;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

/** Destructible iron vane; each surviving anchor strengthens the boss's gust. */
public class WindAnchor extends Mob {
	{ spriteClass=HighlandSprites.EffigySprite.class; HP=HT=28; defenseSkill=7; EXP=0; maxLvl=30;
	  lootChance=0; properties.add(Property.INORGANIC); }
	@Override protected boolean act(){spend(TICK);return true;}
	@Override public int damageRoll(){return 0;}
	@Override public int attackSkill(Char target){return 0;}
	@Override public int drRoll(){return Random.IntRange(0,2);}
	@Override public void die(Object cause){super.die(cause);GLog.i(Messages.get(this,"broken"));}
}
