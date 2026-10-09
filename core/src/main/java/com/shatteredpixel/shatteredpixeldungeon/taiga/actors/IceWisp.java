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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

//MOD (taiga town): a little spirit of the frost. Flies, and spits shards of ice that chill.
public class IceWisp extends Mob {

	{
		spriteClass = TaigaSprites.IceWispSprite.class;

		HP = HT = 12;
		defenseSkill = 9;

		EXP = 5;
		maxLvl = 10;

		flying = true;

		immunities.add( Chill.class );
		immunities.add( Frost.class );
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 3 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy)
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT ).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack( Char enemy ) {
		if (Dungeon.level.adjacent( pos, enemy.pos )
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT ).collisionPos != enemy.pos) {
			return super.doAttack( enemy );
		}
		if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
			sprite.zap( enemy.pos );
			return false;
		} else {
			zap();
			return true;
		}
	}

	//so resistances can tell the shard from a melee hit
	public static class IceShard {}

	private void zap(){
		spend( 1f );
		Invisibility.dispel( this );
		Char enemy = this.enemy;
		if (enemy == null) return;
		if (hit( this, enemy, true )) {
			enemy.damage( Random.NormalIntRange( 2, 5 ), new IceShard() );
			if (enemy.isAlive()) {
				Buff.affect( enemy, Chill.class, 3f );
			}
			if (enemy == Dungeon.hero) Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC );
			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Dungeon.fail( this );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL, enemy.defenseVerb() );
		}
	}

	public void onZapComplete() {
		zap();
		next();
	}
}
