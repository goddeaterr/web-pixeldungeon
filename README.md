# Shattered Pixel Dungeon — web version (fork)

This repository is a fork of [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon) **v4.0.2**
with an added **`html`** module: a browser build (libGDX + [TeaVM](https://teavm.org) via
[gdx-teavm](https://github.com/xpenatan/gdx-teavm)) that is deployed as a static site to Vercel.

- **Game logic is untouched:** the `core` and `SPD-classes` modules have **no changes at all** relative to upstream v4.0.2.
  Everything web-specific lives in `html` and in a few root files (see [the list of changes](#list-of-web-port-changes)).
- Authors of the game: **Evan Debenham** (Shattered Pixel Dungeon) and **Oleg Dolya / Watabou** (Pixel Dungeon).
  License: **GNU GPL v3** ([LICENSE.txt](LICENSE.txt)), unchanged. The in-game credits and the "Support the game"
  button are left as they are.

## Contents

- [Quick start](#quick-start)
- [Building](#building)
- [Deploying to Vercel](#deploying-to-vercel)
- [How it works](#how-it-works)
- [1:1 checks with desktop](#11-checks-with-desktop)
- [Differences from the desktop version](#differences-from-the-desktop-version)
- [Merging upstream SPD](#merging-upstream-spd)
- [List of WEB-PORT changes](#list-of-web-port-changes)

## Quick start

Requirements: **JDK 17** and about **8 GB** of free RAM (the TeaVM compiler runs in a separate process with a 4 GB heap).
Android SDK is not needed.

```bash
./gradlew html:dist
```

```bash
python -m http.server 8080 --directory html/build/dist/web
```

Open http://localhost:8080. (Any static server works, e.g. `npx serve html/build/dist/web`.
Opening `index.html` as a file will not work: the game downloads its resources with `fetch`.)

## Building

| Task | What it does |
| --- | --- |
| `./gradlew html:dist` | Release site (optimized and obfuscated JS) → `html/build/dist/web`. This is what CI deploys. `html:build` does the same. |
| `./gradlew html:distDebug` | Unoptimized build with readable names and stack traces → `html/build/dist/web-debug`. |
| `./gradlew html:distDebug -PwebDebug` | Same, but the version is marked `-INDEV` (desktop's `debug` mode: no story on new runs, all classes unlocked, etc.). |
| `./gradlew html:seedCheckDesktop -Pseed=... -Phero=... -Pdepths=5` | Desktop (JVM) half of the level generation check, see [below](#11-checks-with-desktop). |

Site layout:

```
index.html, manifest.webmanifest, icons/, LICENSE.txt   short cache (no-cache for index.html)
b/<content hash>/spd.js                                  game (JS compiled by TeaVM)
b/<content hash>/scripts/                                gdx.wasm.js (pixmaps), freetype.js (fonts), howler.js (sound)
b/<content hash>/assets/                                 game assets; music is loaded lazily on first play
```

The folder name is a hash of its contents, so everything under `b/` is cached forever,
and a new build just gets a new folder that the new `index.html` points to.

Sizes: `spd.js` ≈ 31 MB (≈ 4 MB gzip / less with brotli, Vercel compresses automatically), preloaded assets
≈ 21 MB (mostly the texts of all 23 languages), music (ogg + mp3 for Safari) is downloaded on demand.

## Deploying to Vercel

The workflow [.github/workflows/deploy-web.yml](.github/workflows/deploy-web.yml) builds the site with Gradle on JDK 17
and deploys it with `vercel deploy --prebuilt --prod`. [vercel.json](vercel.json) contains the headers (long cache for `/b/*`,
`no-cache` for `index.html`, `application/wasm` for `.wasm`, `text/plain` for translation files so they get compressed)
and turns Vercel's own install/build steps into no-ops.

One-time setup:

1. Create a project on Vercel and link it locally: `npm i -g vercel`, then `vercel link` in the repository root.
   This creates `.vercel/project.json` (it is in `.gitignore`).
2. In GitHub → Settings → Secrets and variables → Actions add:
   - `VERCEL_TOKEN` — a token from https://vercel.com/account/tokens
   - `VERCEL_ORG_ID` — `orgId` from `.vercel/project.json`
   - `VERCEL_PROJECT_ID` — `projectId` from `.vercel/project.json`
3. Push to `main`/`master` (or run the workflow manually via *Run workflow*). Pull requests are only built,
   the site is uploaded as an artifact.

Manual deploy from your own machine (if you prefer):

```bash
./gradlew html:dist && vercel build --prod && vercel deploy --prebuilt --prod
```

## How it works

The `html` module is a platform layer like `desktop`/`android`:

- **`WebLauncher`** — entry point: version, platform (desktop browsers get the desktop interface; Android/iOS get
  the mobile one, exactly as in the native apps), browser language as the default language, storage, canvas.
- **`WebPlatformSupport`** — implementation of `PlatformSupport`: fonts (FreeType compiled to wasm, the same `pixel_font.ttf` /
  `droid_sans.ttf` as desktop), the Fullscreen API, safe-area insets (notches), vibration (Vibration API or no-op),
  links in a new tab, the on-screen keyboard.
- **`GameThread`** — SPD runs the game logic in a separate "actor thread" and synchronizes it with rendering through
  `wait/notify/synchronized`. In the browser, gdx-teavm calls `render()`/`pause()` straight from `requestAnimationFrame` and DOM events,
  where you can't block. So the whole game runs on a TeaVM green thread (coroutines of the JS backend), and
  frames, lifecycle events, `postRunnable` and input are queued to it. That's why the target is **JS, not WebAssembly**:
  TeaVM's WasmGC target has no threads.
- **Saves** — `FileType.Local` → IndexedDB (via gdx-teavm). Every write is additionally logged synchronously to
  localStorage (a write-ahead log in `index.html`) and replayed on the next launch: browsers may drop IndexedDB
  transactions started while a tab is closing, and that is exactly when the game saves (`pause`). Settings
  go to localStorage (`Preferences`).
- **Audio** — Howler.js, unlocked on the first click/tap/key (plus a fallback in `index.html`). Music is downloaded
  when it's first played; browsers without Ogg Vorbis (Safari) get the mp3 copies from the iOS build.
- **Graphics** — WebGL 1; client-side vertex arrays used by SPD are emulated (`ClientArraysGL20`), HiDPI is supported
  (render at device pixel resolution), as are window resizing and rotation.
- **Input** — mouse, keyboard (the same bindings as desktop), touch; the virtual keyboard comes from a hidden
  `<textarea>`, whose characters are passed to the game as `keyTyped`.
- **Loading screen** — HTML/CSS in `index.html` with progress while assets download; there's also a crash screen
  (with the stack trace) and a "game closed" screen (the Exit button).
- **Disabled**: update checks and news (`Updates.service`/`News.service` are not set — the same mechanism
  as the debug builds), there are no store links or Play Services in the desktop variant of the interface.
  "Support the game" opens the author's Patreon in a new tab, as on desktop.

### TeaVM fixes needed for 1:1

TeaVM's standard library differs from the JDK in places, and that changes game behavior. In each case the class is
replaced in the `html` module (a class with the same name earlier on the classpath), with a `WEB-PORT` comment and a reason:

| What | Problem in TeaVM 0.15 / gdx-teavm 1.6.2 | Consequence without the fix |
| --- | --- | --- |
| `java.util.Random` (`TRandom`) | `nextInt(bound)` and `nextBoolean()` use other algorithms | any seed generated a different dungeon than on desktop |
| `float` (`SPDStrictFloat`, `StrictFloat`) | float is computed as double, with no rounding to 32 bits | `(int)(0.7f*10)` = 6 instead of 7: damage, chances, etc. |
| `Class.getModifiers()` (`ClassReflection`) | the `static` flag of nested classes is lost | `Bundle` silently dropped static nested classes from saves (rankings, many buffs, quests) |
| `Object.wait()` (`TObject`) | an interrupted `wait()` doesn't re-acquire the monitor | crash on every window resize / phone rotation during a run |
| `Deflater`/`Inflater` | `Z_BUF_ERROR` is treated as an error | no save could be written |
| `DecimalFormat`, `Formatter`, `DecimalFormatSymbols` | different rounding, `#.##` patterns, no locale data | numbers in the UI differed (12.34 vs 12.35, `.33` vs `0.33`, `1.5` instead of `1,5` in Russian) |
| `SharedLibraryLoader` | no `os` field in the emulation | `DeviceCompat` didn't compile |
| reflection (`SPDReflection`) | gdx-teavm's reflection for ~1,400 classes runs the compiler out of memory | — (the list is generated from the game code by the `generateReflectionList` task) |

## 1:1 checks with desktop

### Level generation and saves — automatic

`SeedCheck` generates floors 1..N for a list of seeds and classes, exactly as a new game does, and prints the terrain,
transitions, items (class, quantity, upgrade level, curse), mobs, traps, plants, blobs, **the save JSON of each floor
and of the hero**, plus number formatting checks for all 23 languages and `float` semantics.
The same code runs on the JVM (desktop libGDX 1.14.0) and in the browser:

```bash
./gradlew html:seedCheckDesktop -Pseed=PTW-CRP-GWL,AAA-AAA-AAA,KQX-MPD-RTY,ZZZ-ZZZ-ZZZ -Phero=warrior,mage,rogue,huntress,duelist,cleric -Pdepths=5
```

Then open `<site>/?seedcheck=PTW-CRP-GWL,AAA-AAA-AAA,KQX-MPD-RTY,ZZZ-ZZZ-ZZZ&hero=warrior,mage,rogue,huntress,duelist,cleric&depths=5`
→ **Download**, and run:

```bash
node html/tools/seedcheck-compare.mjs html/build/seedcheck/desktop.txt ~/Downloads/seedcheck-web.txt
```

Result at the time of writing: **identical** — 4 seeds × 6 classes × floors 1–5 (120 floors: maps, items, mobs, traps),
plus the save JSON (floors and hero; compared as JSON: key order and missile weapon `set_id`s, which come from
`SecureRandom` on every platform, are ignored) and number formatting in all 23 languages.
What is deliberately not part of the seed (and also differs between two desktop runs): the first guidebook pages are placed
by an unseeded generator (`EntranceRoom.placeEarlyGuidePages`); SeedCheck therefore marks them as found.

### Playing — partly automatic

- Save in the middle of a run → reload the tab → continue: checked (the save written while the tab closes survives
  thanks to the write-ahead log), as are deaths → rankings → the record window.
- Smoke test: in a debug build, open the console and run `spd.autotest(true)` — a bot plays like a player
  (via `Hero.handle` / `rest` / `search`, exactly as the game's taps and buttons call them): it fights, rests,
  walks to the stairs, eats; after a death it starts a new run. `spd.autotestStatus()` shows progress.
  This is how a run to Goo (floor 5) was checked; see the result in the commit history and the report.
- `spd.state()` in the console — the state of the game thread (useful when debugging).

### Browsers and devices

Checked in Chromium (desktop + mobile emulation: touch, portrait/landscape, rotation mid-run, the virtual keyboard,
HiDPI). **Not checked on real devices** — please check by hand:

- [ ] Chrome (desktop), Firefox (desktop), Safari (macOS)
- [ ] Safari iOS (iPhone and iPad), Chrome Android
- [ ] for each: the loading screen; sound after the first tap; music (in Safari — mp3); a new run; save → reload → continue;
      rotating the phone mid-run; text input (journal note / seed); fullscreen; "Add to home screen"

## Differences from the desktop version

What is known and left as it is (it doesn't affect game logic):

1. **Number text in save files**: for some floats, the last digit of the decimal representation in the JSON differs
   (`…812` vs `…813` — two equally valid representations of the same number) — the value read back is identical.
   `Float.toString` differs only for extreme magnitudes (like `1.17549435E-38`), which the game doesn't produce.
2. TeaVM's `Float.parseFloat` is imprecise in the last digit for about 0.1% of strings — the game doesn't use it.
3. **No parallelism**: the browser has one thread, so while a level generates (or a long chain of mob turns runs)
   the screen isn't updated — the loading screen animation pauses for a moment; on desktop it keeps animating.
4. **Saves when closing the tab**: the game saves on `visibilitychange`/`pagehide` (like desktop does on minimize/close).
   If the browser or system crashes, progress since the last save is lost — the same as killing the desktop process.
5. **Music** starts with a small delay the first time a track is played (downloaded on demand).
6. **Fullscreen**: browsers allow it only after a user gesture, so the "Fullscreen" setting (on by default, as on desktop)
   is applied on the first click/tap. Leaving with Esc turns the setting off. On iPhone there is no Fullscreen API —
   the option is shown as unavailable; use "Add to Home Screen" (fullscreen web app via the manifest).
7. **Exit button** → a "game closed" screen with a "Start again" button (a page can't close its own tab).
8. **Keys** the browser reserves for itself (Ctrl+W, Ctrl+T, F11, …) can't be bound.
9. **Language** on first launch is taken from the browser (on desktop — from the system).
10. **News and update checks** are disabled (as in the debug builds); the news screen shows "unavailable".
11. On phones (Android/iOS by User-Agent) the mobile interface is shown, as in the native apps; on tablets
    with a desktop User-Agent (iPadOS) the iOS one is detected via touch support.

## Merging upstream SPD

```bash
git remote -v                     # upstream = https://github.com/00-Evan/shattered-pixel-dungeon.git
git fetch upstream --tags
git merge v4.1.0                  # or the tag/branch you need
```

Conflicts are possible only in the few root files from [the list below](#list-of-web-port-changes) (`settings.gradle`,
`gradle.properties`, `.gitignore`, `README.md`) — `core`/`SPD-classes` don't change in this fork.
After the merge:

1. `./gradlew html:distDebug` — if TeaVM reports `Field ... was not found` / `Method ... was not found`,
   upstream has started using an API that the emulation doesn't have; this is fixed in `html`.
2. Run [the 1:1 check](#11-checks-with-desktop) (SeedCheck) on several seeds and classes, and the
   `spd.autotest(true)` smoke test.
3. If upstream updated libGDX: check `gdxWebVersion` in `gradle.properties` (the version gdx-teavm was built against).
4. When updating **gdx-teavm/TeaVM** (`gdxTeaVMVersion`, `teaVMVersion`): the shadowed classes in
   `html/src/main/java/org/teavm/...` and `html/src/main/java/emu/...` are copies of specific versions with patches —
   compare them with the new versions (each has a WEB-PORT comment explaining what was changed), and drop the
   ones the new release has fixed.
5. New languages in `Languages.java` → add them to `webLocales` in `html/build.gradle`.

## List of WEB-PORT changes

**`core`, `SPD-classes`, `desktop`, `android`, `ios`, `services` — no changes.**

Outside the `html` module (all marked with `WEB-PORT` comments where the format allows):

| File | Change |
| --- | --- |
| `settings.gradle` | `include ':html'` |
| `gradle.properties` | versions `gdxTeaVMVersion`, `gdxWebVersion`, `teaVMVersion` for the `html` module |
| `.gitignore` | `*.hprof` (TeaVM heap dumps), `.claude/`, `.vercel/` |
| `vercel.json` | static hosting configuration and headers |
| `.github/workflows/deploy-web.yml` | build and deploy |
| `README.md` | this section |

The `html` module:

| Path | Purpose |
| --- | --- |
| `html/build.gradle` | TeaVM build, reflection list and nested-class table generation, asset staging, `dist`/`distDebug`/`seedCheckDesktop` |
| `html/webapp/index.html`, `manifest.webmanifest` | page: loading screen, crashes, fullscreen, keyboard, insets, audio unlock, write-ahead log |
| `.../html/WebLauncher`, `SPDWebApplication`, `GameThread`, `QueuedInput` | startup, game thread, input |
| `.../html/WebPlatformSupport`, `WebKeyboard`, `WebJS`, `WebBaseUrl`, `WebPreloader`, `WebCrashHandler` | platform layer |
| `.../html/WebAudioSupport` | lazy music loading, mp3 for Safari |
| `.../html/ClientArraysGL20`, `SPDWebGLGraphics` | client-side vertex arrays on top of WebGL |
| `.../html/StrictFloat`, `.../html/build/SPDStrictFloat` | 32-bit float semantics |
| `.../html/build/SPDReflection` | reflection for `Bundle` (build-time TeaVM plugin) |
| `.../html/SeedCheck`, `MemoryPreferences`, `src/seedcheck/...`, `tools/seedcheck-compare.mjs` | 1:1 check |
| `.../html/AutoTest`, `com/watabou/noosa/WebGroupAccess` | smoke-test bot (enabled only from the console) |
| `emu/com/badlogic/gdx/utils/SharedLibraryLoader` | emulation with the `os` field |
| `emu/com/badlogic/gdx/utils/reflect/ClassReflection` | correct `isMemberClass`/`isStaticClass` for game classes |
| `org/teavm/classlib/java/util/TRandom` | `java.util.Random` with the JDK algorithms |
| `org/teavm/classlib/java/lang/TObject` | `wait()` re-acquires the monitor after an interrupt |
| `org/teavm/classlib/java/util/zip/TDeflater`, `TInflater` | `Z_BUF_ERROR` is not an error |
| `org/teavm/classlib/java/text/TDecimalFormat`, `TDecimalFormatSymbols`, `java/util/TFormatter` | number formatting as in the JDK |

The copies of TeaVM (Apache 2.0) and gdx-teavm (Apache 2.0) classes keep their original license headers; Apache 2.0 is
compatible with GPLv3.

---

# Shattered Pixel Dungeon

[Shattered Pixel Dungeon](https://shatteredpixel.com/shatteredpd/) is an open-source traditional roguelike dungeon crawler with randomized levels and enemies, and hundreds of items to collect and use. It's based on the [source code of Pixel Dungeon](https://github.com/00-Evan/pixel-dungeon-gradle), by [Watabou](https://watabou.itch.io/).

Shattered Pixel Dungeon currently compiles for Android, iOS, and Desktop platforms. You can find official releases of the game on:

[![Get it on Google Play](https://shatteredpixel.com/assets/images/badges/gplay.png)](https://play.google.com/store/apps/details?id=com.shatteredpixel.shatteredpixeldungeon)
[![Download on the App Store](https://shatteredpixel.com/assets/images/badges/appstore.png)](https://apps.apple.com/app/shattered-pixel-dungeon/id1563121109)
[![Steam](https://shatteredpixel.com/assets/images/badges/steam.png)](https://store.steampowered.com/app/1769170/Shattered_Pixel_Dungeon/)<br>
[![GOG.com](https://shatteredpixel.com/assets/images/badges/gog.png)](https://www.gog.com/game/shattered_pixel_dungeon)
[![Itch.io](https://shatteredpixel.com/assets/images/badges/itch.png)](https://shattered-pixel.itch.io/shattered-pixel-dungeon)
[![Github Releases](https://shatteredpixel.com/assets/images/badges/github.png)](https://github.com/00-Evan/shattered-pixel-dungeon/releases)

If you like this game, please consider [supporting me on Patreon](https://www.patreon.com/ShatteredPixel)!

There is an official blog for this project at [ShatteredPixel.com](https://www.shatteredpixel.com/blog/).

The game also has a translation project hosted on [Transifex](https://explore.transifex.com/shattered-pixel/shattered-pixel-dungeon/).

Note that **this repository does not accept pull requests!** The code here is provided in hopes that others may find it useful for their own projects, not to allow community contribution. Issue reports of all kinds (bug reports, feature requests, etc.) are welcome.

If you'd like to work with the code, you can find the following guides in `/docs`:
- [Compiling for Android.](docs/getting-started-android.md)
    - **[If you plan to distribute on Google Play please read the end of this guide.](docs/getting-started-android.md#distributing-your-app)**
- [Compiling for desktop platforms.](docs/getting-started-desktop.md)
- [Compiling for iOS.](docs/getting-started-ios.md)
- [Recommended changes for making your own version.](docs/recommended-changes.md)