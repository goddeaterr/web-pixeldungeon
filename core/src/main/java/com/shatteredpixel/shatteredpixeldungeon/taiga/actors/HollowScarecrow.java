package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.GlassSickle;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.OrchardSprites;
import com.watabou.utils.Random;

/** A hooked frame that kept watch after everything it guarded died. */
public class HollowScarecrow extends Mob {
	{
		spriteClass = OrchardSprites.ScarecrowSprite.class;
		HP = HT = 48; defenseSkill = 9; EXP = 11; maxLvl = 21;
		loot = GlassSickle.class; lootChance = 0.08f;
		properties.add(Property.INORGANIC);
	}
	@Override public int damageRoll(){ return Random.NormalIntRange(8, 14); }
	@Override public int attackSkill(Char target){ return 17; }
	@Override public int drRoll(){ return super.drRoll() + Random.IntRange(3, 6); }
}
