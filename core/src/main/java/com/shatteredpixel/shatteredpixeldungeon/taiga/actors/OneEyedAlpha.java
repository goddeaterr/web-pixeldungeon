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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.AlphaFang;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

//MOD (taiga town): the trapper's quest target (taiga floor 3). Calls its pack when it is hurt.
public class OneEyedAlpha extends FrostWolf {

	{
		spriteClass = TaigaSprites.AlphaSprite.class;

		HP = HT = 60;
		defenseSkill = 8;

		EXP = 15;
		maxLvl = 20;

		loot = AlphaFang.class;
		lootChance = 1f;

		properties.add(Property.MINIBOSS);
		state = WANDERING;
	}

	private boolean howled = false;
	private boolean announced = false;

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 4, 9 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 15;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( 1, 3 );
	}

	@Override
	public void notice() {
		super.notice();
		if (!announced){
			announced = true;
			yell( Messages.get(this, "notice") );
		}
	}

	@Override
	public void damage( int dmg, Object src ) {
		super.damage( dmg, src );
		if (!howled && isAlive() && HP * 2 <= HT){
			howl();
		}
	}

	private void howl(){
		howled = true;
		yell( Messages.get(this, "howl") );
		Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
		int called = 0;
		for (int i : PathFinder.NEIGHBOURS8){
			if (called >= 2) break;
			int cell = pos + i;
			if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null){
				FrostWolf wolf = new FrostWolf();
				wolf.pos = cell;
				wolf.state = wolf.HUNTING;
				GameScene.add( wolf, 1f );
				called++;
			}
		}
	}

	private static final String HOWLED = "howled";
	private static final String ANNOUNCED = "announced";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( HOWLED, howled );
		bundle.put( ANNOUNCED, announced );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		howled = bundle.getBoolean( HOWLED );
		announced = bundle.getBoolean( ANNOUNCED );
	}
}
