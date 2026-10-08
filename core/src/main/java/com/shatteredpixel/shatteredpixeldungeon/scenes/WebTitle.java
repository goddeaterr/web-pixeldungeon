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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

// WEB-PORT: fork branding requested by the project owner. The title and welcome screens show the game's
// name as text in the game's own pixel font instead of the Shattered Pixel Dungeon banner artwork.
// The original credits (About screen) are unchanged, as the GPLv3 requires.
public class WebTitle {

	public static final String NAME_LANDSCAPE = "PIXEL DUNGEON WEB EDITION";
	public static final String NAME_PORTRAIT = "PIXEL DUNGEON\nWEB EDITION";
	public static final String STUDIO = "by gdls studio";

	//adds the name and studio line, centered in the area the banner image occupies (the banner itself is hidden)
	public static RenderedTextBlock[] add( PixelScene scene, Image banner, boolean landscape ){
		RenderedTextBlock name = PixelScene.renderTextBlock(landscape ? NAME_LANDSCAPE : NAME_PORTRAIT, 16);
		name.maxWidth((int)Math.max(banner.width(), 120));
		name.align(RenderedTextBlock.CENTER_ALIGN);
		name.hardlight(Window.TITLE_COLOR);

		RenderedTextBlock studio = PixelScene.renderTextBlock(STUDIO, 8);
		studio.hardlight(0xCCCCCC);

		float gap = 4;
		float height = name.height() + gap + studio.height();
		name.setPos(banner.x + (banner.width() - name.width()) / 2f, banner.y + (banner.height() - height) / 2f);
		studio.setPos(banner.x + (banner.width() - studio.width()) / 2f, name.bottom() + gap);
		PixelScene.align(name);
		PixelScene.align(studio);

		scene.add(name);
		scene.add(studio);
		return new RenderedTextBlock[]{name, studio};
	}
}
