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

package com.shatteredpixel.shatteredpixeldungeon.taiga.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSadGhost;
import com.watabou.utils.Callback;

/**
 * MOD (taiga town): a quest giver offers a choice of rewards (like the wandmaker's window).
 * Tapping a reward shows its details with confirm / cancel.
 */
public class WndTaigaReward extends Window {

	private static final int WIDTH    = 120;
	private static final int BTN_SIZE = 32;
	private static final int BTN_GAP  = 5;
	private static final int GAP      = 2;

	private final NPC giver;
	private final Item questItem;
	private final Callback onReward;

	public WndTaigaReward( NPC giver, Item questItem, String text, Callback onReward, Item... rewards ) {
		super();
		this.giver = giver;
		this.questItem = questItem;
		this.onReward = onReward;

		IconTitle titlebar = new IconTitle();
		titlebar.icon( giver.sprite() );
		titlebar.label( Messages.titleCase( giver.name() ) );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );

		RenderedTextBlock message = PixelScene.renderTextBlock( text, 6 );
		message.maxWidth( WIDTH );
		message.setPos( 0, titlebar.bottom() + GAP );
		add( message );

		float total = rewards.length * BTN_SIZE + (rewards.length - 1) * BTN_GAP;
		float x = (WIDTH - total) / 2f;
		float y = message.top() + message.height() + BTN_GAP;
		float bottom = y;
		for (final Item reward : rewards){
			ItemButton btn = new ItemButton(){
				@Override
				protected void onClick() {
					GameScene.show( new RewardWindow( reward ) );
				}
			};
			btn.item( reward );
			btn.setRect( x, y, BTN_SIZE, BTN_SIZE );
			add( btn );
			x += BTN_SIZE + BTN_GAP;
			bottom = btn.bottom();
		}

		resize( WIDTH, (int) bottom );
	}

	private void selectReward( Item reward ){
		hide();
		if (questItem != null && questItem.isEquipped( Dungeon.hero ) == false){
			questItem.detach( Dungeon.hero.belongings.backpack );
		}
		reward.identify( false );
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Messages.capitalize( Messages.get(Dungeon.hero, "you_now_have", reward.name()) ) );
		} else {
			Dungeon.level.drop( reward, Dungeon.hero.pos ).sprite.drop();
		}
		if (onReward != null) onReward.call();
	}

	private class RewardWindow extends WndInfoItem {

		public RewardWindow( final Item item ) {
			super( item );

			RedButton btnConfirm = new RedButton( Messages.get(WndSadGhost.class, "confirm") ){
				@Override
				protected void onClick() {
					RewardWindow.this.hide();
					selectReward( item );
				}
			};
			btnConfirm.setRect( 0, height + 2, width / 2 - 1, 16 );
			add( btnConfirm );

			RedButton btnCancel = new RedButton( Messages.get(WndSadGhost.class, "cancel") ){
				@Override
				protected void onClick() {
					hide();
				}
			};
			btnCancel.setRect( btnConfirm.right() + 2, height + 2, btnConfirm.width(), 16 );
			add( btnCancel );

			resize( width, (int) btnCancel.bottom() );
		}
	}
}
