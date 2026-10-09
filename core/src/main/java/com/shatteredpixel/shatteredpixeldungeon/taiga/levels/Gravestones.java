package com.shatteredpixel.shatteredpixeldungeon.taiga.levels;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;

/** Tiny terrain decorations drawn in code so the swamp keeps the taiga atlas layout. */
public class Gravestones {
	public static void add(Group visuals, Level level){
		for (int cell = 0; cell < level.length(); cell++){
			if (level.map[cell] != Terrain.CUSTOM_DECO) continue;
			int x = cell % level.width() * DungeonTilemap.SIZE;
			int y = cell / level.width() * DungeonTilemap.SIZE;
			block(visuals, x + 2, y + 13, 12, 2, 0xFF1B2029);
			block(visuals, x + 3, y + 5, 10, 8, 0xFF252C35);
			block(visuals, x + 4, y + 3, 8, 3, 0xFF858E97);
			block(visuals, x + 4, y + 6, 8, 6, 0xFF636C77);
			block(visuals, x + 5, y + 7, 1, 4, 0xFFAFB7BD);
			block(visuals, x + 7, y + 8, 4, 1, 0xFF303843);
			block(visuals, x + 8, y + 7, 1, 4, 0xFF303843);
		}
	}

	private static void block(Group group, int x, int y, int w, int h, int color){
		ColorBlock piece = new ColorBlock(w, h, color);
		piece.x = x;
		piece.y = y;
		group.add(piece);
	}

	public static String tileName(){ return Messages.get(Gravestones.class, "name"); }
	public static String tileDesc(){ return Messages.get(Gravestones.class, "desc"); }
}
