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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;

public class WebLauncher {

	public static void main(String[] args) {

		WebCrashHandler.install();

		Game.version = WebBuildConfig.VERSION;
		Game.versionCode = WebBuildConfig.VERSION_CODE;

		//Updates.service and News.service are left null: update checks and news are not available on web

		//DeviceCompat decides between the desktop and the mobile UI from SharedLibraryLoader.os.
		// Desktop browsers get the desktop experience (keyboard, window-style UI scale),
		// phones and tablets the matching mobile one (touch-first UI, iOS mp3 music, etc.)
		if (WebJS.isIOS()) {
			SharedLibraryLoader.os = Os.IOS;
		} else if (WebJS.isAndroid()) {
			SharedLibraryLoader.os = Os.Android;
		} else if (WebJS.isMac()) {
			SharedLibraryLoader.os = Os.MacOsX;
		} else if (WebJS.isWindows()) {
			SharedLibraryLoader.os = Os.Windows;
		} else {
			SharedLibraryLoader.os = Os.Linux;
		}

		//all game files (saves, rankings, badges, journal) are kept in IndexedDB through FileType.Local
		FileUtils.setDefaultFileProperties(Files.FileType.Local, "");

		WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
		//fill the browser window and follow its size
		config.width = 0;
		config.height = 0;
		//render at device resolution on HiDPI screens, like the native builds
		config.usePhysicalPixels = true;
		//Shattered only uses GLES 2.0
		config.useGL30 = false;
		config.antialiasing = false;
		config.stencil = false;
		config.alpha = false;
		config.storagePrefix = "shatteredpixeldungeon";
		config.localStoragePrefix = "shatteredpixeldungeon-files";
		config.baseUrlProvider = new WebBaseUrl();
		config.preloadListener = assetLoader -> assetLoader.loadScript("freetype.js");

		GameThread game = new GameThread(new ShatteredPixelDungeon(new WebPlatformSupport()));
		new SPDWebApplication(game, new WebPreloader(), config);
	}

}
