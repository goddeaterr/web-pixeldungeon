package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.WardenSpade;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.SwampSprites;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/** A two-wave boss with a warned bell strike that reaches across the graveyard. */
public class GraveWarden extends Mob {
	private boolean awakened;
	private boolean firstWave;
	private boolean secondWave;
	private boolean tollWarning;
	private int tollTarget = -1;
	private int tollCooldown = 3;

	{
		spriteClass = SwampSprites.WardenSprite.class;
		HP = HT = 110;
		defenseSkill = 15;
		EXP = 35;
		maxLvl = 30;
		lootChance = 0;
		properties.add(Property.BOSS);
		properties.add(Property.UNDEAD);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(5, 10); }
	@Override public int attackSkill(Char target) { return 19; }
	@Override public int drRoll() { return super.drRoll() + Random.NormalIntRange(2, 5); }

	@Override
	public void notice() {
		super.notice();
		if (!awakened) {
			awakened = true;
			Dungeon.level.seal();
			BossHealthBar.assignBoss(this);
			Music.INSTANCE.play(Assets.Music.CAVES_BOSS, true);
			yell(Messages.get(this, "notice"));
		}
	}

	@Override
	protected boolean act() {
		if (awakened && Dungeon.hero != null && Dungeon.hero.isAlive()) {
			if (!firstWave && HP <= HT * 2 / 3) {
				firstWave = true;
				summonMirebound(2);
				yell(Messages.get(this, "first_wave"));
			} else if (!secondWave && HP <= HT / 3) {
				secondWave = true;
				summonMirebound(2);
				yell(Messages.get(this, "second_wave"));
			}

			if (tollWarning) {
				tollWarning = false;
				if (Dungeon.hero.pos == tollTarget) {
					Dungeon.hero.damage(Random.IntRange(3, 5), this);
					if (Dungeon.hero.isAlive()) Buff.affect(Dungeon.hero, Chill.class, 3f);
					GLog.w(Messages.get(this, "toll"));
				} else GLog.i(Messages.get(this, "dodged"));
				tollTarget = -1;
				tollCooldown = secondWave ? 2 : 4;
				spend(TICK);
				return true;
			}
			if (Dungeon.level.distance(pos, Dungeon.hero.pos) <= 6 && --tollCooldown <= 0) {
				tollWarning = true;
				tollTarget = Dungeon.hero.pos;
				yell(Messages.get(this, "warning"));
				spend(TICK);
				return true;
			}
		}
		return super.act();
	}

	private void summonMirebound(int count) {
		Level level = Dungeon.level;
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (count == 0) break;
			if (!level.insideMap(cell) || !level.passable[cell] || Actor.findChar(cell) != null) continue;
			MireHusk minion = new MireHusk();
			minion.pos = cell;
			GameScene.add(minion);
			count--;
		}
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Dungeon.level.unseal();
		GameScene.bossSlain();
		GLog.h(Messages.get(this, "defeated"));
		Dungeon.level.drop(new WardenSpade().identify(false), pos).sprite.drop();
		Dungeon.level.drop(new PotionOfStrength(), pos).sprite.drop();
		Dungeon.level.drop(new ScrollOfUpgrade(), pos).sprite.drop();
		Dungeon.level.playLevelMusic();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("awakened", awakened);
		bundle.put("first_wave", firstWave);
		bundle.put("second_wave", secondWave);
		bundle.put("toll_warning", tollWarning);
		bundle.put("toll_target", tollTarget);
		bundle.put("toll_cooldown", tollCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		awakened = bundle.getBoolean("awakened");
		firstWave = bundle.getBoolean("first_wave");
		secondWave = bundle.getBoolean("second_wave");
		tollWarning = bundle.getBoolean("toll_warning");
		if (bundle.contains("toll_target")) tollTarget = bundle.getInt("toll_target");
		if (bundle.contains("toll_cooldown")) tollCooldown = bundle.getInt("toll_cooldown");
		if (awakened) BossHealthBar.assignBoss(this);
	}
}
