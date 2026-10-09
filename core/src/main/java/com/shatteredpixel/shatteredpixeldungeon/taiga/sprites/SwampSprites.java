package com.shatteredpixel.shatteredpixeldungeon.taiga.sprites;

import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaAssets;

/** Original sprites drawn by mod/swamp_art.py. */
public class SwampSprites {
	public static class MireHuskSprite extends TaigaMobSprite {
		public MireHuskSprite() { setup(TaigaAssets.SWAMP_MOBS, 0, 2); }
	}
	public static class GraveMothSprite extends TaigaMobSprite {
		public GraveMothSprite() { setup(TaigaAssets.SWAMP_MOBS, 1, 3); }
	}
	public static class BellWraithSprite extends TaigaMobSprite {
		public BellWraithSprite() { setup(TaigaAssets.SWAMP_MOBS, 2, 3); }
	}
	public static class WardenSprite extends TaigaMobSprite {
		public WardenSprite() { setup(TaigaAssets.SWAMP_BOSS, 0, 2, 24); }
	}
}
