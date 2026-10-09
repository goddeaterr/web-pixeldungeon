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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.WoodcutterAxe;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.watabou.utils.Random;

//MOD (taiga town): a traveller who froze to death in the taiga and got up again. Its touch is icy.
public class Frostbitten extends Mob {

	{
		spriteClass = TaigaSprites.FrostbittenSprite.class;

		HP = HT = 26;
		defenseSkill = 6;

		EXP = 6;
		maxLvl = 11;

		loot = Gold.class;
		lootChance = 0.5f;

		properties.add( Property.UNDEAD );
		immunities.add( Chill.class );
		immunities.add( Frost.class );
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 3, 7 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 13;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( 1, 3 );
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int(5) < 2 && enemy.isAlive()){
			Buff.affect( enemy, Chill.class, 2f );
		}
		return super.attackProc( enemy, damage );
	}

	@Override
	public Item createLoot() {
		//once in a while it still carries the axe it was cutting wood with
		if (Random.Int(25) == 0) return new WoodcutterAxe().identify(false);
		return new Gold().random();
	}
}
