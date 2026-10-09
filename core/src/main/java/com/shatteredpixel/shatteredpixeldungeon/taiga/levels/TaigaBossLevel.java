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

package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaBranch;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaQuests;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaTownLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Leshy;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/**
 * MOD (taiga town): taiga floor 5, the Leshy's grove. A great round clearing full of old spruces, reached by a
 * trail from the south. The grove is sealed while the Leshy lives. The northern pass leads to +6.
 */
public class TaigaBossLevel extends Level {

	{
		color1 = 0xdbe5ee;
		color2 = 0x2a5546;
	}

	private static final int W = 41;
	private static final int H = 43;
	private static final int CX = 20;
	private static final int CY = 19;
	private static final int R = 14;

	private boolean blizzard = false;

	@Override
	public String tilesTex() {
		return TaigaAssets.TILES_WILD;
	}

	@Override
	public String waterTex() {
		return TaigaAssets.WATER;
	}

	@Override
	public void playLevelMusic() {
		if (locked){
			Music.INSTANCE.play( blizzard ? Assets.Music.CAVES_BOSS_FINALE : Assets.Music.CAVES_BOSS, true );
		} else if (TaigaQuests.leshyDefeated){
			Music.INSTANCE.play( Assets.Music.THEME_2, true );
		} else {
			Music.INSTANCE.play( Assets.Music.CAVES_TENSE, true );
		}
	}

	@Override
	protected boolean build() {
		setSize( W, H );

		//the grove: a slightly ragged circle
		for (int y = 1; y < H - 1; y++){
			for (int x = 1; x < W - 1; x++){
				double d = Math.hypot( x - CX, (y - CY) * 1.05 );
				if (d <= R - 0.5 + Random.Float(-0.6f, 0.6f)){
					map[x + y * W] = Random.Int(9) == 0 ? Terrain.EMPTY_DECO : Terrain.EMPTY;
				}
			}
		}

		//the trail in from the south
		int bottom = H - 3;
		for (int y = CY + R - 2; y <= bottom; y++){
			for (int x = CX - 1; x <= CX + 1; x++){
				map[x + y * W] = (x == CX || Random.Int(3) > 0) ? Terrain.EMPTY : Terrain.WALL;
			}
		}
		int entrance = CX + bottom * W;
		map[entrance] = Terrain.EXIT;
		transitions.add( new LevelTransition( this, entrance, LevelTransition.Type.REGULAR_ENTRANCE ) );

		//old spruces all over the grove, leaving the middle and the trail clear
		int spruces = 0;
		for (int tries = 0; tries < 2000 && spruces < 26; tries++){
			int x = CX + Random.IntRange( -R + 2, R - 2 );
			int y = CY + Random.IntRange( -R + 2, R - 2 );
			int cell = x + y * W;
			if (Math.hypot( x - CX, y - CY ) < 4) continue;
			if (Math.abs( x - CX ) <= 1 && y > CY) continue;
			boolean open = true;
			for (int dy = -1; dy <= 1 && open; dy++){
				for (int dx = -1; dx <= 1; dx++){
					int n = map[cell + dx + dy * W];
					if (n != Terrain.EMPTY && n != Terrain.EMPTY_DECO){
						open = false;
						break;
					}
				}
			}
			if (open){
				map[cell] = Terrain.STATUE;
				spruces++;
			}
		}

		//snowy juniper here and there
		for (int i = 0; i < 8; i++){
			int x = CX + Random.IntRange( -R + 3, R - 3 );
			int y = CY + Random.IntRange( -R + 3, R - 3 );
			if (Math.hypot( x - CX, y - CY ) < 5) continue;
			for (int j = 0; j < 4; j++){
				int cell = x + Random.IntRange(-1, 1) + (y + Random.IntRange(-1, 1)) * W;
				if (map[cell] == Terrain.EMPTY) map[cell] = Terrain.HIGH_GRASS;
			}
		}

		ensureNorthernPass();

		return true;
	}

	/** Adds the route to +6 to newly built groves and saves made before that route existed. */
	private boolean ensureNorthernPass() {
		for (LevelTransition transition : transitions) {
			if (transition.type == LevelTransition.Type.REGULAR_EXIT) return false;
		}
		for (int y = 1; y < CY; y++) map[CX + y * W] = Terrain.EMPTY;
		int pass = CX + W;
		map[pass] = Terrain.ENTRANCE;
		transitions.add(new LevelTransition(this, pass, LevelTransition.Type.REGULAR_EXIT,
				6, TaigaBranch.BRANCH, LevelTransition.Type.REGULAR_ENTRANCE));
		return true;
	}

	@Override
	protected void createMobs() {
		Leshy leshy = new Leshy();
		leshy.pos = CX + CY * W;
		mobs.add( leshy );
	}

	@Override
	public Mob createMob() {
		return null;
	}

	@Override
	public int mobLimit() {
		return 0;
	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	protected void createItems() {
		//The fifth taiga floor has supplies too; keep the essentials on the approach
		//so they can be picked up before entering the Leshy's grove.
		drop(new PotionOfStrength(), CX + (H - 6) * W);
		drop(new ScrollOfUpgrade(), CX + (H - 7) * W);
		drop(new PotionOfHealing(), CX + (H - 8) * W);
		int placed = 0;
		for (int tries = 0; tries < 500 && placed < 12; tries++){
			int x = CX + Random.IntRange(-R + 2, R - 2);
			int y = CY + Random.IntRange(-R + 2, R - 2);
			int cell = x + y * W;
			if (!passable[cell] || heaps.get(cell) != null || findMob(cell) != null
					|| distance(cell, CX + CY * W) < 5) continue;
			drop(TaigaLevel.randomLoot(), cell).type = Heap.Type.HEAP;
			placed++;
		}
	}

	@Override
	public int randomRespawnCell( Char ch ) {
		return entrance() - W;
	}

	public void startBlizzard(){
		if (blizzard) return;
		blizzard = true;
		if (visuals != null){
			visuals.add( new Snowfall( width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE, 6f ) );
		}
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		visuals.add( new Snowfall( width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE ) );
		if (blizzard){
			visuals.add( new Snowfall( width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE, 6f ) );
		}
		return visuals;
	}

	@Override
	public String tileName( int tile ) {
		String name = TaigaTownLevel.taigaTileName( tile, true );
		return name != null ? name : super.tileName( tile );
	}

	@Override
	public String tileDesc( int tile ) {
		if (tile == Terrain.STATUE) return Messages.get(TaigaBossLevel.class, "spruce_desc");
		String desc = TaigaTownLevel.taigaTileDesc( tile, true );
		return desc != null ? desc : super.tileDesc( tile );
	}

	private static final String BLIZZARD = "blizzard";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( BLIZZARD, blizzard );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if (ensureNorthernPass()) {
			buildFlagMaps();
			cleanWalls();
		}
		blizzard = bundle.getBoolean( BLIZZARD );
	}
}
