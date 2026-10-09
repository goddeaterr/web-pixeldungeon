package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;

/** Fruit preserved by frost and ash, still edible at a price. */
public class BlackFruit extends Food {
	{
		image = TaigaItemSprites.BLACK_FRUIT;
		energy = 120;
	}
	@Override protected void satisfy(Hero hero){
		super.satisfy(hero);
		Buff.affect(hero, Chill.class, 2f);
	}
	@Override public int value(){ return 12 * quantity; }
}
