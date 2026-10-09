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

package com.shatteredpixel.shatteredpixeldungeon.taiga.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.IceWisp;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.SpruceTreant;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Villager;
import com.watabou.noosa.audio.Sample;

//MOD (taiga town): sprites of the village characters, rows of the sheets drawn by mod/taiga_art.py
public class TaigaSprites {

	public static class VillagerSprite extends TaigaMobSprite {
		public VillagerSprite(){
			super();
			setup( TaigaAssets.VILLAGERS, 0, 2 );
		}

		@Override
		public void linkVisuals( Char ch ) {
			super.linkVisuals( ch );
			if (ch instanceof Villager){
				setup( TaigaAssets.VILLAGERS, ((Villager) ch).look, 2 );
			}
		}
	}

	public static class FurTraderSprite extends TaigaMobSprite {
		public FurTraderSprite(){
			super();
			setup( TaigaAssets.TRADERS, 0, 2 );
		}
	}

	public static class HerbalistSprite extends TaigaMobSprite {
		public HerbalistSprite(){
			super();
			setup( TaigaAssets.TRADERS, 1, 2 );
		}
	}

	public static class HunterSprite extends TaigaMobSprite {
		public HunterSprite(){
			super();
			setup( TaigaAssets.TRADERS, 2, 2 );
		}
	}

	public static class HuskySprite extends TaigaMobSprite {
		public HuskySprite(){
			super();
			setup( TaigaAssets.ANIMALS, 0, 4 );
		}
	}

	public static class ReindeerSprite extends TaigaMobSprite {
		public ReindeerSprite(){
			super();
			setup( TaigaAssets.ANIMALS, 1, 2 );
		}
	}

	public static class WolfSprite extends TaigaMobSprite {
		public WolfSprite(){
			super();
			setup( TaigaAssets.ANIMALS, 2, 3 );
		}
	}

	public static class SnowHareSprite extends TaigaMobSprite {
		public SnowHareSprite(){
			super();
			setup( TaigaAssets.ANIMALS, 3, 3 );
		}
	}

	// ---- the taiga floors (beasts.png, spirits.png, leshy.png) ----

	public static class FrostWolfSprite extends TaigaMobSprite {
		public FrostWolfSprite(){ super(); setup( TaigaAssets.BEASTS, 0, 3 ); }
	}

	public static class AlphaSprite extends TaigaMobSprite {
		public AlphaSprite(){ super(); setup( TaigaAssets.BEASTS, 1, 3 ); }
	}

	public static class BoarSprite extends TaigaMobSprite {
		public BoarSprite(){ super(); setup( TaigaAssets.BEASTS, 2, 3 ); }
	}

	public static class BearSprite extends TaigaMobSprite {
		public BearSprite(){ super(); setup( TaigaAssets.BEASTS, 3, 2 ); }
	}

	public static class WhiteStagSprite extends TaigaMobSprite {
		public WhiteStagSprite(){ super(); setup( TaigaAssets.BEASTS, 4, 2 ); }
	}

	public static class SableSprite extends TaigaMobSprite {
		public SableSprite(){ super(); setup( TaigaAssets.BEASTS, 5, 4 ); }
	}

	public static class IceWispSprite extends TaigaMobSprite {
		public IceWispSprite(){
			super();
			setup( TaigaAssets.SPIRITS, 0, 6 );
			zap = attack.clone();
		}

		@Override
		public void zap( int cell ) {
			super.zap( cell );
			MagicMissile.boltFromChar( parent, MagicMissile.FROST, this, cell,
					() -> ((IceWisp) ch).onZapComplete() );
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

		@Override
		public void onComplete( Animation anim ) {
			if (anim == zap) idle();
			super.onComplete( anim );
		}
	}

	public static class FrostbittenSprite extends TaigaMobSprite {
		public FrostbittenSprite(){ super(); setup( TaigaAssets.SPIRITS, 1, 2 ); }
	}

	//looks like a plain spruce while it waits (frames of the next row)
	public static class TreantSprite extends TaigaMobSprite {

		private Animation disguise;

		public TreantSprite(){
			super();
			setup( TaigaAssets.SPIRITS, 2, 2 );
			disguise = new Animation( 1, true );
			disguise.frames( frames, 3 * FRAMES_PER_ROW, 3 * FRAMES_PER_ROW, 3 * FRAMES_PER_ROW + 1 );
		}

		@Override
		public void idle() {
			if (ch instanceof SpruceTreant && ((SpruceTreant) ch).disguised()){
				play( disguise );
			} else {
				super.idle();
			}
		}

		@Override
		public void linkVisuals( Char ch ) {
			super.linkVisuals( ch );
			if (ch instanceof SpruceTreant && ((SpruceTreant) ch).disguised()) play( disguise );
		}
	}

	public static class TotemSprite extends TaigaMobSprite {
		public TotemSprite(){ super(); setup( TaigaAssets.SPIRITS, 4, 3 ); }
	}

	public static class TrapperSprite extends TaigaMobSprite {
		public TrapperSprite(){ super(); setup( TaigaAssets.SPIRITS, 5, 2 ); }
	}

	public static class ShamanSprite extends TaigaMobSprite {
		public ShamanSprite(){ super(); setup( TaigaAssets.SPIRITS, 6, 3 ); }
	}

	//24x24 frames; frames 0-11 like the others, 12-14 raise the crook (cast)
	public static class LeshySprite extends TaigaMobSprite {

		private Animation cast;

		public LeshySprite(){
			super();
			setup( TaigaAssets.LESHY, 0, 2, 24 );
			cast = new Animation( 10, false );
			cast.frames( frames, 12, 13, 14, 13, 12 );
		}

		public void cast(){
			play( cast );
		}

		@Override
		public void onComplete( Animation anim ) {
			if (anim == cast) {
				idle();
			}
			super.onComplete( anim );
		}
	}
}
