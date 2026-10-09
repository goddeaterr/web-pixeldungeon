package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Music;

/** A necromancer who kept watch while the graveyard sank into the swamp. */
public class GraveWarden extends Necromancer {
	private boolean awakened;

	{
		HP = HT = 130;
		defenseSkill = 18;
		EXP = 35;
		maxLvl = 30;
		lootChance = 0;
		properties.add(Property.BOSS);
	}

	@Override
	public void notice(){
		super.notice();
		if (!awakened){
			awakened = true;
			Dungeon.level.seal();
			BossHealthBar.assignBoss(this);
			Music.INSTANCE.play(Assets.Music.CAVES_BOSS, true);
			yell(Messages.get(this, "notice"));
		}
	}

	@Override
	public int attackSkill(Char target){ return 18; }

	@Override
	public void die(Object cause){
		super.die(cause);
		Dungeon.level.unseal();
		GameScene.bossSlain();
		GLog.h(Messages.get(this, "defeated"));
		Dungeon.level.drop(new PotionOfStrength(), pos).sprite.drop();
		Dungeon.level.drop(new ScrollOfUpgrade(), pos).sprite.drop();
		Dungeon.level.playLevelMusic();
	}

	private static final String AWAKENED = "awakened";

	@Override
	public void storeInBundle(com.watabou.utils.Bundle bundle){
		super.storeInBundle(bundle);
		bundle.put(AWAKENED, awakened);
	}

	@Override
	public void restoreFromBundle(com.watabou.utils.Bundle bundle){
		super.restoreFromBundle(bundle);
		awakened = bundle.getBoolean(AWAKENED);
		if (awakened) BossHealthBar.assignBoss(this);
	}
}
