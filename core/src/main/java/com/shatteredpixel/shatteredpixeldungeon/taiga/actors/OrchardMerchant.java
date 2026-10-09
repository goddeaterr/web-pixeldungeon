package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.AshglassShard;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.BlackFruit;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.GlassSickle;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.OrchardSprites;
import java.util.ArrayList;

/** The last porter left at the orchard waystation; still trades under dark glass. */
public class OrchardMerchant extends LastHearthMerchant {
	{ spriteClass = OrchardSprites.PorterSprite.class; }
	public static ArrayList<Item> orchardStock(){
		ArrayList<Item> stock = LastHearthMerchant.stock();
		// The original supply caravan reached the caves; its spare gear is newer than Last Hearth's.
		for (int i = 0; i < 3; i++) stock.remove(stock.size()-1);
		stock.add(clean(Generator.randomArmor(2)));
		stock.add(clean(Generator.randomWeapon(2,true)));
		stock.add(clean(Generator.randomUsingDefaults(Generator.Category.WAND)));
		stock.add(new PotionBandolier());
		stock.add(new MagicalHolster());
		stock.add(new VelvetPouch());
		stock.add(new AshglassShard().quantity(3));
		stock.add(new BlackFruit().quantity(2));
		stock.add(new GlassSickle().identify(false));
		return stock;
	}
	private static Item clean(Item item){
		item.cursed = false;
		item.level(0);
		return item.identify(false);
	}
	@Override public String chatText(){ return Messages.get(this, "chat"); }
}
