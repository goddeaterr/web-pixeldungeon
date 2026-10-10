package com.shatteredpixel.shatteredpixeldungeon.taiga.items;

import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaItemSprites;

/** A hard, wax-wrapped ration found in abandoned survey packs. */
public class SurveyRation extends Food {
	{ image=TaigaItemSprites.SURVEY_RATION; energy=180; }
	@Override public int value(){return 16*quantity;}
}
