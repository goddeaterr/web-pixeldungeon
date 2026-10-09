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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaBranch;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaQuests;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/**
 * MOD (taiga town): a carved spirit pole, corrupted by the Leshy's anger (taiga floor 4).
 * It can't move or fight, but keeps calling ice wisps while someone is near. The shaman wants all three broken.
 */
public class CorruptedTotem extends Mob {

	{
		spriteClass = TaigaSprites.TotemSprite.class;

		HP = HT = 25;
		defenseSkill = 0;

		EXP = 4;
		maxLvl = 20;

		state = HUNTING;

		properties.add( Property.IMMOVABLE );
		properties.add( Property.INORGANIC );
	}

	private int summonCooldown = 4;

	@Override
	protected boolean act() {
		if (Dungeon.level.heroFOV[pos] && --summonCooldown <= 0){
			int wisps = 0;
			for (Mob m : Dungeon.level.mobs){
				if (m instanceof IceWisp && Dungeon.level.distance( pos, m.pos ) <= 6) wisps++;
			}
			if (wisps < 2){
				int cell = -1;
				for (int i : PathFinder.NEIGHBOURS8){
					int c = pos + i;
					if (Dungeon.level.passable[c] && Actor.findChar(c) == null){
						cell = c;
						break;
					}
				}
				if (cell != -1){
					IceWisp wisp = new IceWisp();
					wisp.pos = cell;
					wisp.state = wisp.HUNTING;
					GameScene.add( wisp, 1f );
					CellEmitter.get( cell ).burst( Speck.factory( Speck.STEAM ), 6 );
					if (sprite != null) sprite.showStatus( 0x88CCFF, Messages.get(this, "summon") );
				}
			}
			summonCooldown = 14 + Random.Int(4);
		}
		spend( TICK );
		return true;
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return false;
	}

	@Override
	public int damageRoll() {
		return 0;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( 1, 3 );
	}

	@Override
	public boolean add( Buff buff ) {
		//it's wood and spirit, not flesh
		return false;
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );
		if (TaigaBranch.active() && Dungeon.depth == 4){
			TaigaQuests.totemsBroken = Math.min(TaigaQuests.TOTEMS, TaigaQuests.totemsBroken + 1);
			GLog.p( Messages.get(this, "broken", TaigaQuests.totemsBroken, TaigaQuests.TOTEMS) );
		}
	}

	private static final String COOLDOWN = "cooldown";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( COOLDOWN, summonCooldown );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		summonCooldown = bundle.getInt( COOLDOWN );
	}
}
