package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

/** Bitter marsh root: small healing and food, useful when ordinary plants have died. */
public class MireRoot extends Item {
	private static final String AC_EAT = "EAT";
	{
		image = TaigaItemSprites.MIRE_ROOT;
		stackable = true;
		defaultAction = AC_EAT;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_EAT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_EAT.equals(action)) {
			detach(hero.belongings.backpack);
			Buff.affect(hero, Healing.class).setHeal(8, 0.5f, 0);
			Buff.affect(hero, Hunger.class).satisfy(Hunger.HUNGRY / 8f);
			hero.sprite.operate(hero.pos);
			hero.busy();
			hero.spend(1f);
			Sample.INSTANCE.play(Assets.Sounds.EAT);
		}
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 18 * quantity; }
}
