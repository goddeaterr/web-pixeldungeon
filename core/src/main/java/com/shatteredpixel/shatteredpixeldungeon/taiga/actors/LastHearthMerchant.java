package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.GraveSalt;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.MireRoot;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.FuneralLantern;

import java.util.ArrayList;

/** The full dungeon-style supply shop at the last inhabited stop. */
public class LastHearthMerchant extends Shopkeeper {

	public static ArrayList<Item> stock() {
		ArrayList<Item> items = new ArrayList<>();
		items.add(new ScrollOfUpgrade().identify(false));
		items.add(new ScrollOfUpgrade().identify(false));
		items.add(new PotionOfStrength().identify(false));
		items.add(new PotionOfHealing().identify(false));
		items.add(new PotionOfHealing().identify(false));
		items.add(new ScrollOfIdentify().identify(false));
		items.add(new ScrollOfRemoveCurse().identify(false));
		items.add(new ScrollOfMagicMapping().identify(false));
		items.add(new StoneOfAugmentation());
		items.add(new Stylus());
		items.add(new Ankh());
		items.add(new ScrollHolder());
		items.add(new SmallRation());
		items.add(new SmallRation());
		items.add(new GraveSalt().quantity(2));
		items.add(new MireRoot().quantity(2));
		items.add(new FuneralLantern());
		items.add(clean(Generator.randomArmor(1)));
		items.add(clean(Generator.randomWeapon(1, true)));
		items.add(clean(Generator.randomUsingDefaults(Generator.Category.WAND)));
		return items;
	}

	private static Item clean(Item item) {
		item.cursed = false;
		item.level(0);
		return item.identify(false);
	}
}
