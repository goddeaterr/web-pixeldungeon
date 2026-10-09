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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.PineResin;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

/**
 * MOD (taiga town): a spruce that walks. Until something comes close it stands still and looks exactly
 * like the other spruces (neutral, so it doesn't show up as an enemy either). Its roots grab what it hits.
 */
public class SpruceTreant extends Mob {

	{
		spriteClass = TaigaSprites.TreantSprite.class;

		HP = HT = 34;
		defenseSkill = 2;

		EXP = 8;
		maxLvl = 13;

		baseSpeed = 0.75f;

		loot = PineResin.class;
		lootChance = 0.4f;

		state = PASSIVE;
		alignment = Alignment.NEUTRAL;

		immunities.add( Roots.class );
	}

	public boolean disguised(){
		return state == PASSIVE;
	}

	public void wake(){
		if (!disguised()) return;
		alignment = Alignment.ENEMY;
		state = HUNTING;
		enemy = Dungeon.hero;
		target = Dungeon.hero.pos;
		if (sprite != null){
			sprite.showAlert();
			sprite.idle();
		}
		if (Dungeon.level.heroFOV[pos]){
			GLog.w( Messages.get(this, "awakens") );
		}
	}

	@Override
	protected boolean act() {
		if (disguised() && Dungeon.hero != null && Dungeon.hero.isAlive()
				&& Dungeon.level.adjacent( pos, Dungeon.hero.pos )){
			wake();
		}
		return super.act();
	}

	@Override
	public void damage( int dmg, Object src ) {
		wake();
		super.damage( dmg, src );
	}

	@Override
	public boolean interact( Char c ) {
		if (disguised() && c == Dungeon.hero){
			wake();
			return true;
		}
		return super.interact( c );
	}

	@Override
	public String name() {
		return disguised() ? Messages.get(this, "disguise_name") : super.name();
	}

	@Override
	public String description() {
		return disguised() ? Messages.get(this, "disguise_desc") : super.description();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 3, 8 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 13;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( 2, 5 );
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int(10) < 3 && enemy.isAlive()){
			Buff.prolong( enemy, Roots.class, 2f );
		}
		return super.attackProc( enemy, damage );
	}
}
