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

package com.shatteredpixel.shatteredpixeldungeon.html;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Reflection;

/**
 * Developer commands for testing, only in debug builds (-PwebDebug): spd.debug("...") in the browser console.
 *
 *   goto DEPTH [BRANCH]   travel to a floor (the taiga floors are branch 4, depth 1-5; the village is 0 0)
 *   tough                 999 health for the hero
 *   reveal                the whole floor becomes known (like a scroll of magic mapping, without searching)
 *   tp NAME               teleport next to the first creature whose class is called NAME (e.g. tp Trapper)
 *   talk NAME             talk to the first creature whose class is called NAME
 *   info NAME             the info window of a creature on this floor (class name), or of a new taiga item
 *   give CLASS [N]        put an item in the backpack, CLASS relative to com.shatteredpixel.shatteredpixeldungeon.
 */
public class WebDebug {

	public static void install(){
		if (!DeviceCompat.isDebug()) return;
		WebJS.registerDebug( (String command) -> {
			SPDWebApplication.gameThread().post( () -> run( command ) );
			return "queued: " + command;
		});
	}

	private static void run( String command ){
		if (!(ShatteredPixelDungeon.scene() instanceof GameScene) || Dungeon.hero == null){
			Game.reportException( new IllegalStateException("debug commands need a game in progress") );
			return;
		}
		String[] parts = command.trim().split("\\s+");
		switch (parts[0]){
			case "goto":
				InterlevelScene.mode = InterlevelScene.Mode.RETURN;
				InterlevelScene.returnDepth = Integer.parseInt(parts[1]);
				InterlevelScene.returnBranch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
				InterlevelScene.returnPos = -1;
				Game.switchScene( InterlevelScene.class );
				break;
			case "tough":
				Dungeon.hero.HTBoost += 999;
				Dungeon.hero.updateHT( true );
				break;
			case "reveal":
				for (int i = 0; i < Dungeon.level.length(); i++){
					Dungeon.level.mapped[i] = true;
					Dungeon.level.visited[i] = true;
				}
				GameScene.updateFog();
				break;
			case "tp":
				for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m : Dungeon.level.mobs){
					if (!m.getClass().getSimpleName().equals(parts[1])) continue;
					for (int n : com.watabou.utils.PathFinder.NEIGHBOURS8){
						int cell = m.pos + n;
						if (Dungeon.level.passable[cell] && com.shatteredpixel.shatteredpixeldungeon.actors.Actor.findChar(cell) == null){
							com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation.appear( Dungeon.hero, cell );
							Dungeon.observe();
							GameScene.updateFog();
							return;
						}
					}
				}
				break;
			case "talk":
				for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m : Dungeon.level.mobs){
					if (m.getClass().getSimpleName().equals(parts[1])){
						m.interact( Dungeon.hero );
						return;
					}
				}
				break;
			case "info":
				for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m : Dungeon.level.mobs){
					if (m.getClass().getSimpleName().equals(parts[1])){
						GameScene.show( new com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob( m ) );
						return;
					}
				}
				Class<?> c = Reflection.forName( "com.shatteredpixel.shatteredpixeldungeon.taiga.items." + parts[1] );
				if (c == null) c = Reflection.forName( "com.shatteredpixel.shatteredpixeldungeon.taiga.actors." + parts[1] );
				Object o = c == null ? null : Reflection.newInstance( c );
				if (o instanceof Item){
					GameScene.show( new com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem( (Item) o ) );
				} else if (o instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob){
					GameScene.show( new com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob( (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob) o ) );
				}
				break;
			case "give":
				Class<?> cls = Reflection.forName( "com.shatteredpixel.shatteredpixeldungeon." + parts[1] );
				Item item = (Item) Reflection.newInstance( cls );
				if (item != null){
					if (parts.length > 2) item.quantity( Integer.parseInt(parts[2]) );
					item.identify( false );
					item.collect();
				}
				break;
		}
	}
}
