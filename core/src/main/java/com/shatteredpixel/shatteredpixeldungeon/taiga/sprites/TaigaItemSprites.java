/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.taiga.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;

/**
 * MOD (taiga town): item images of the village. They live in their own sheet (taiga/items.png, same size
 * as sprites/items.png), their frames are registered in ItemSpriteSheet.film under ids from BASE on,
 * and ItemSprite switches to this sheet for those ids (see ItemSprite.frame).
 *
 * The ids are not compile time constants on purpose: reading one loads this class, which registers the frames.
 */
public class TaigaItemSprites {

	private static final int BASE = 4096;
	private static final int WIDTH = 16;

	public static int SMOKED_FISH   = BASE + 0;
	public static int PINE_BREAD    = BASE + 1;
	public static int HERBAL_TEA    = BASE + 2;
	public static int WOLF_PELT     = BASE + 3;
	public static int PINE_RESIN    = BASE + 4;
	public static int FUR_CLOAK     = BASE + 5;
	public static int MITTENS       = BASE + 6;
	public static int FROST_JAVELIN = BASE + 7;
	public static int TRAPPER_KNIFE = BASE + 8;
	public static int BEARSKIN_COAT = BASE + 9;
	public static int BEAR_CLAW     = BASE + 10;
	public static int WOODCUTTER_AXE= BASE + 11;
	public static int SPIRIT_STAFF  = BASE + 12;
	public static int LESHY_CROOK   = BASE + 13;
	public static int HEART_OF_TAIGA= BASE + 14;
	public static int ELIXIR_STAG   = BASE + 15;
	public static int ELIXIR_NORTH  = BASE + 16;
	public static int ALPHA_FANG    = BASE + 17;
	public static int BEAR_PELT     = BASE + 18;
	public static int SABLE_PELT    = BASE + 19;
	public static int MIRE_ROOT     = BASE + 20;
	public static int GRAVE_SALT    = BASE + 21;
	public static int WARDEN_SPADE  = BASE + 22;
	public static int FUNERAL_LANTERN = BASE + 23;
	public static int ASHGLASS_SHARD = BASE + 24;
	public static int BLACK_FRUIT = BASE + 25;
	public static int GLASS_SICKLE = BASE + 26;
	public static int CONSERVATOR_SHEARS = BASE + 27;
	public static int WINDGLASS_SHARD = BASE + 28;
	public static int SURVEY_RATION = BASE + 29;
	public static int ANCHOR_PIKE = BASE + 30;

	static {
		rect(SMOKED_FISH, 15, 11);
		rect(PINE_BREAD,  14, 11);
		rect(HERBAL_TEA,  12, 12);
		rect(WOLF_PELT,   15, 13);
		rect(PINE_RESIN,  10, 10);
		rect(FUR_CLOAK,   15, 14);
		rect(MITTENS,     14, 13);
		for (int id = FROST_JAVELIN; id <= ANCHOR_PIKE; id++){
			rect(id, 16, 16);
		}
	}

	private static void rect( int id, int w, int h ){
		int slot = id - BASE;
		int x = (slot % WIDTH) * 16;
		int y = (slot / WIDTH) * 16;
		ItemSpriteSheet.film.add( id, x, y, x + w, y + h );
	}

	public static boolean owns( int image ){
		return image >= BASE;
	}

	//the sheet an item image is drawn from
	public static String texture( int image ){
		return owns(image) ? TaigaAssets.ITEMS : Assets.Sprites.ITEMS;
	}
}
