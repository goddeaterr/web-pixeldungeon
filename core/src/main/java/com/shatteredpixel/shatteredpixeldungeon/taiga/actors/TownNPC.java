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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaTownLevel;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

/**
 * MOD (taiga town): a harmless inhabitant of the village. Strolls around inside the palisade,
 * says something when the hero bumps into them and steps aside (swaps places, like allies do).
 */
public abstract class TownNPC extends NPC {

	{
		state = WANDERING;
		baseSpeed = 0.75f;
	}

	protected abstract int lines();

	public String line(){
		return Messages.get(this, "line_" + Random.IntRange(1, lines()));
	}

	@Override
	protected boolean act() {
		//villagers take their time
		if (state == WANDERING && Random.Int(3) == 0){
			spend( TICK );
			return true;
		}
		return super.act();
	}

	@Override
	protected boolean getCloser( final int target ) {
		return TaigaTownLevel.restrictedStep(this, () -> super.getCloser(target));
	}

	@Override
	protected boolean getFurther( final int target ) {
		return TaigaTownLevel.restrictedStep(this, () -> super.getFurther(target));
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage( int dmg, Object src ) {
		//nothing happens in the village
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public boolean interact( Char c ) {
		if (c == Dungeon.hero){
			String line = line();
			if (sprite != null) sprite.showStatus( CharSprite.NEUTRAL, "!" );
			GLog.i( "%s: \"%s\"", Messages.titleCase(name()), line );
		}
		return super.interact( c );
	}
}
