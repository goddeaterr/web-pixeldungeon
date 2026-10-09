package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.GraveWarden;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.MireMist;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;
import com.watabou.utils.Bundle;

/** Floor +10, the graveyard at the deepest reach of the snowy swamp. */
public class SwampBossLevel extends Level {
	private static final int W = 31;
	private static final int H = 31;
	private int cell(int x, int y){ return x + y * W; }

	{
		color1 = 0x8b98aa;
		color2 = 0x252c39;
	}

	@Override
	public String tilesTex(){ return TaigaAssets.SWAMP_TILES; }

	@Override
	public String waterTex(){ return TaigaAssets.SWAMP_WATER; }

	@Override
	public void playLevelMusic(){
		Music.INSTANCE.play(locked ? Assets.Music.CAVES_BOSS : Assets.Music.CAVES_TENSE, true);
	}

	@Override
	protected boolean build(){
		setSize(W, H);
		for (int y = 1; y < H - 1; y++) for (int x = 1; x < W - 1; x++){
			float distance = (float)Math.hypot(x - 15, y - 14);
			if (distance < 13) {
				map[cell(x, y)] = Random.Int(12) == 0 ? Terrain.EMPTY_DECO : Terrain.EMPTY;
				// A flooded burial ring, with four raised walks leading to the central bell dais.
				if (distance > 7 && distance < 11 && Math.abs(x - 15) > 1 && Math.abs(y - 14) > 1)
					map[cell(x, y)] = Terrain.WATER;
				if (distance < 3 || (Math.abs(x - 15) <= 1 && y > 3 && y < 25)
						|| (Math.abs(y - 14) <= 1 && x > 3 && x < 27))
					map[cell(x, y)] = Terrain.EMPTY_SP;
			}
		}
		for (int y = 22; y < H - 1; y++) map[cell(15, y)] = Terrain.EMPTY;
		for (int i = 0; i < 55; i++){
			int x = 3 + Random.Int(25), y = 3 + Random.Int(22);
			if (Math.abs(x - 15) < 3 || Math.abs(y - 14) < 2 || map[cell(x, y)] != Terrain.EMPTY) continue;
			map[cell(x, y)] = Random.Int(3) == 0 ? Terrain.WATER : Terrain.CUSTOM_DECO;
		}
		int down = cell(15, H - 2);
		map[down] = Terrain.EXIT;
		transitions.add(new LevelTransition(this, down, LevelTransition.Type.REGULAR_ENTRANCE));
		ensureOrchardPass();
		return true;
	}

	/** Add the climb to +11 to newly generated graveyards and older saved fights. */
	private void ensureOrchardPass(){
		for (LevelTransition t : transitions) if (t.type == LevelTransition.Type.REGULAR_EXIT) return;
		for (int y = 1; y <= 13; y++) map[cell(15, y)] = Terrain.EMPTY_SP;
		int up = cell(15, 1);
		map[up] = Terrain.ENTRANCE;
		transitions.add(new LevelTransition(this, up, LevelTransition.Type.REGULAR_EXIT));
	}

	@Override public void restoreFromBundle(Bundle bundle){
		super.restoreFromBundle(bundle);
		ensureOrchardPass();
		buildFlagMaps();
	}

	@Override
	protected void createMobs(){
		GraveWarden boss = new GraveWarden();
		boss.pos = cell(15, 13);
		mobs.add(boss);
	}

	@Override
	public Mob createMob(){ return null; }

	@Override
	public int mobLimit(){ return 0; }

	@Override
	public Actor addRespawner(){ return null; }

	@Override
	protected void createItems(){ drop(new PotionOfHealing(), cell(15, H - 5)); }

	@Override
	public int randomRespawnCell(Char ch){ return cell(15, H - 3); }

	@Override
	public Group addVisuals(){
		super.addVisuals();
		Gravestones.add(visuals, this);
		visuals.add(new Snowfall(width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE, 6f));
		visuals.add(new MireMist(width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE, 1.6f));
		return visuals;
	}

	@Override
	public String tileName(int tile){
		if (tile == Terrain.CUSTOM_DECO) return Gravestones.tileName();
		String name = SwampTerrain.name(tile);
		return name != null ? name : super.tileName(tile);
	}

	@Override
	public String tileDesc(int tile){
		if (tile == Terrain.CUSTOM_DECO) return Gravestones.tileDesc();
		String desc = SwampTerrain.desc(tile);
		return desc != null ? desc : super.tileDesc(tile);
	}
}
