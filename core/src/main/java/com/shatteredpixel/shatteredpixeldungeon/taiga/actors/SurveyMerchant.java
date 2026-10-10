package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.SurveyRation;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.WindglassShard;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.GraveSalt;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.MireRoot;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.FuneralLantern;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.HighlandSprites;
import java.util.ArrayList;

/** The final living surveyor runs a full supply shop from the ridge shelter. */
public class SurveyMerchant extends LastHearthMerchant {
	{ spriteClass=HighlandSprites.MerchantSprite.class; }
	public static ArrayList<Item> stock(){
		ArrayList<Item> items=LastHearthMerchant.stock();
		for(int i=0;i<3;i++)items.remove(items.size()-1);
		for(int i=items.size()-1;i>=0;i--){
			Item item=items.get(i);
			if(item instanceof GraveSalt || item instanceof MireRoot || item instanceof FuneralLantern) items.remove(i);
		}
		items.add(clean(Generator.randomArmor(3)));
		items.add(clean(Generator.randomWeapon(3,true)));
		items.add(clean(Generator.randomUsingDefaults(Generator.Category.WAND)));
		items.add(new PotionBandolier()); items.add(new MagicalHolster()); items.add(new VelvetPouch());
		items.add(new SurveyRation().quantity(2)); items.add(new WindglassShard().quantity(3));
		return items;
	}
	private static Item clean(Item item){item.cursed=false;item.level(0);return item.identify(false);}
	@Override public String chatText(){return Messages.get(this,"chat");}
}
