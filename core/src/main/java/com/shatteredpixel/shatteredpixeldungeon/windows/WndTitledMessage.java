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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndTitledMessage extends Window {

	protected static final int WIDTH_MIN    = 120;
	protected static final int WIDTH_MAX    = 220;
	protected static final int GAP	= 2;
	private ScrollPane scrollingText;

	public WndTitledMessage( Image icon, String title, String message ) {
		
		this( new IconTitle( icon, title ), message );

	}
	
	public WndTitledMessage( Component titlebar, String message ) {

		super();

		int width = WIDTH_MIN;

		titlebar.setRect( 0, 0, width, 0 );
		add(titlebar);

		RenderedTextBlock text = PixelScene.renderTextBlock( 6 );
		if (!useHighlighting()) text.setHightlighting(false);
		text.text( message, width );
		text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );

		//Subclasses use targetHeight() to leave room for controls below the text.
		int reserved = (int)Math.max(0, PixelScene.MIN_HEIGHT_L - 10 - targetHeight());
		int maxHeight = (int)PixelScene.uiCamera.height - 8 - reserved;
		int maxWidth = Math.min( WIDTH_MAX, (int)PixelScene.uiCamera.width - 8 );
		while (text.bottom() + 2 > maxHeight && width + 20 <= maxWidth){
			width += 20;
			titlebar.setRect(0, 0, width, 0);
			text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );
			text.maxWidth(width);
		}

		bringToFront(titlebar);

		if (text.bottom() + 2 > maxHeight){
			float top = titlebar.bottom() + 2*GAP;
			text.setPos(0, 0);
			Component content = new Component();
			content.add(text);
			content.setSize(width, text.height() + 2);
			scrollingText = new ScrollPane(content);
			add(scrollingText);
			resize(width, maxHeight);
			scrollingText.setRect(0, top, width, maxHeight - top - 2);
		} else {
			add(text);
			resize( width, (int)text.bottom() + 2 );
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		if (scrollingText != null && scrollingText.height() > 0) {
			scrollingText.setRect(scrollingText.left(), scrollingText.top(),
					scrollingText.width(), scrollingText.height());
		}
	}

	protected boolean useHighlighting(){
		return true;
	}

	protected float targetHeight() {
		return PixelScene.MIN_HEIGHT_L - 10;
	}
}
