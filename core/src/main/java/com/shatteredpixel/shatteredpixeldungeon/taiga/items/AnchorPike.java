package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;
import com.watabou.utils.Random;

/** The Last Surveyor's iron anchor, cold with its captive storm. */
public class AnchorPike extends Spear {
	{ image=TaigaItemSprites.ANCHOR_PIKE; tier=4; unique=true; }
	@Override public int proc(Char attacker,Char defender,int damage){
		if(defender.isAlive() && Random.Int(4)==0)Buff.affect(defender,Chill.class,3f);
		return super.proc(attacker,defender,damage);
	}
}
