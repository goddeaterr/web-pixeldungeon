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

package com.shatteredpixel.shatteredpixeldungeon.taiga.actors;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;

//MOD (taiga town): a quest giver of the taiga floors: stays put, can't be harmed, talks when tapped
public abstract class TaigaQuestGiver extends NPC {

	{
		properties.add( Property.IMMOVABLE );
	}

	@Override
	protected boolean act() {
		if (Dungeon.level.heroFOV[pos] && sprite != null && Dungeon.hero != null){
			sprite.turnTo( pos, Dungeon.hero.pos );
		}
		return super.act();
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage( int dmg, Object src ) {
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	//picks what to say or offer; called on the game's logic thread
	protected abstract Window dialog();

	@Override
	public boolean interact( Char c ) {
		if (sprite != null) sprite.turnTo( pos, c.pos );
		if (c != Dungeon.hero) return true;
		final Window wnd = dialog();
		Game.runOnRenderThread( () -> GameScene.show( wnd ) );
		return true;
	}

	protected Window say( String text ){
		return new WndQuest( this, text );
	}
}
