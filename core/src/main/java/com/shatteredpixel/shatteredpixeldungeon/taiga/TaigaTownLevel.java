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

package com.shatteredpixel.shatteredpixeldungeon.taiga;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.watabou.noosa.Halo;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.FurTrader;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Herbalist;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Hunter;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Husky;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Reindeer;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.SnowHare;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.TaigaBeast;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.WildBoar;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.TownNPC;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.TownTrader;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Villager;
import com.shatteredpixel.shatteredpixeldungeon.taiga.actors.Wolf;
import com.shatteredpixel.shatteredpixeldungeon.taiga.effects.Snowfall;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * MOD (taiga town): floor 0, a snowy taiga village above the dungeon. The stairs up on floor 1 lead here
 * (see SewerLevel); the mine shaft stays barred until the summit relic is recovered. Inside the palisade the village is safe: traders,
 * villagers and animals. Outside it, in the wild outskirts, wolves and snow hares roam.
 *
 * The level is hand made (MAP below) and has its own textures (taiga/*.png, drawn by mod/taiga_art.py).
 * It never touches the item decks or limited drops of the dungeon, so floors 1-26 stay the same for a seed.
 */
public class TaigaTownLevel extends Level {

	{
		color1 = 0xdbe5ee;
		color2 = 0x2a5546;
	}

	/*
	 * Legend (also used by mod/town_preview.py):
	 *  T forest (wall)        . snow            , snow with twigs/tracks   " dry grass     % snowy shrubs
	 *  P spruce (statue)      S festive spruce  L log wall (cabin)         _ plank floor   + door
	 *  # palisade             g gate            ~ icy stream               = bridge        W woodpile
	 *  F campfire             A cauldron        X mine shaft down to floor 1    U the Old Trail up the mountain
	 *  f/h/u fur trader, herbalist, hunter   $ their stalls   v villager   d husky   r reindeer
	 *  w wolf   b snow hare   o wild boar
	 */
	//MAP-BEGIN (generated from mod/town_map.txt)
	public static final String[] MAP = {
			"TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT",
			"TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT",
			"TT..TTT.T..T..\"TT.T.T..TTTTTTTTTT~TTTTTTT#TTTTTTTTTTTTTTTTTTTTTTTTTTTT",
			"TTT...T...%\".T.,.\"T..TTT.TT..TP\".~~...TT.#.T...........TTTTTTTT.....TT",
			"TTT...U...%..P%,%\"P%.......\"%....~~....P\"#.P......%.W.X.W.%........TTT",
			"TTT.W...P.\"......\"....%.%........~~.\"...\"#.........,...............TTT",
			"TTT......,....%.%\",....%..%T..%..~~.b..\".#.LLLLLLLLL.....LLLLLLLLLPTTT",
			"TTP.............%,..P\"P%..T......~~.\".\"..#.L$$$f$$$L.....L$$$h$$$L..TT",
			"TTT.......P..%.........TTP.T.....~~.%.%,.#.L_______L.....L_______L..TT",
			"TT..,...\"......%..%\"....T....\".,.\"~~.....#.L_______L..,..LA______L.TTT",
			"TTT.\"\"\"......T%P%.............w..\"~~.....#.LLLL+LLLL.....LLLL+LLLL..TT",
			"TTTT.....\".P.TT\".T....%%%..T.P....~~...,.#.......W....S.....,..W....TT",
			"TTPTP..........TT..%%...o\".PP%.%%.~~..%\".#......,.............r....TTT",
			"TTT.w..%...\"P%..PP%\"\"......T....\".~~.,..%#.P....v.......W.........PTTT",
			"TTT,.\".b\"...........\"...o...\"....P~~.%...#....,.......F.......,.....TT",
			"TTT\"..%......%.\"..%.....\"T.T.....===....\"g..................v.......TT",
			"TTT...P,....P.,P...P..P%.\"........~~,%%P.#.....v...,.....d..........TT",
			"TTT..%..P\"........\"\"\"\".%..P%.%..\".~~\"..,.#.P.........v...,........P.TT",
			"TTT.%......%%\"..,.....%,.\".....,..~~..%..#...,.....................TTT",
			"TT\".P\"..%....,.\"...\"....,.........~~.P...#.P.............LLLLLLLLL..TT",
			"TTT%.....P........T...\".\".\".\"b.P\".~~.....#.LLLLL.....,...L$$$u$$$LP.TT",
			"TT%\".........,P.,\".TPT,.\"..P....\"\"P~~....#.L___L.LLLLL...L_______L.TTT",
			"TTPT.P..b.........TT.T.....T.......~~.%\".#.L___L.L___L...L______WL.TTT",
			"TTT....%.%.......TT\"T%...,.\".......~~...%#.LL+LL.L___L...LLLL+LLLL.TTT",
			"TTTTT%T\"%.P....TTTT..P.\".%\".%..\"\"..~~w...#.......LL+LL.............TTT",
			"TT.T...%..%......TT.T...P...%...%,.~~,%%,#..W.....P........W.....P.TTT",
			"TT\".TT.,,..TP\"...TT.TT....%....P.%\"~~\"P.P#.........................TTT",
			"TT......\".TT.....T\"\"%T......%.\"....~~...P##############g###########TTT",
			"TT...%.,.T%P\"\"......%%%....T%\"%%,.,~~\"%%.%P.P....%..%.P...P........TTT",
			"TT.,..P...%.P,.T..T\"%......T.......~~.\",...%%\".\"...\"..........wP.%.TTT",
			"TTT........T.T..TTT.%..P...T.P%P\"..~~....P.....%w.P.%......\"P.%...TTTT",
			"TT..,.....\"\"TT,\".\"T..%.\"...%.%.\"\".\"~~~~~~~~~~~~~~~~~~~~=~~~~~~~~~~~TTT",
			"TTTT%.,...,..T.....\"P.....%..\"%P%..~~~~~~~~~~~~~~~~~~~~=~~~~~~~~~~~~TT",
			"TT.T..\".\"...\".b,...P...,..,.,P%%.%..\"%...%.\"....%.%........b.\"......TT",
			"TT.%...\"..,...........P.\"..%..%\".%%%%T..TTT\"TT.T\".P..TT..%...T.....TTT",
			"TT.T......,.\"....TTP%P....%.\"....~~...%....w.......P%\".%..\"\"...\"%..TTT",
			"TTT%.,..%%.\".%TT..%%P.\".,%.,.....~~..%.%....,........%.P.w,....,w\"TTTT",
			"TTT.......~~~~~~~TT~~~~~~=~~~~~~~~~%....%PP....\"..,,\".%b\"........P.TTT",
			"TTTT...~~~~~~~~~~~~~~~~~~=~~~~~~~~~\"........%.P,..\".\".\".%...P...\"...TT",
			"TT.T.,~~~~~~~~~~~~~~~...\"...%,%.,.\"....%\"..\"......%.P..\".%.\"P.\"...T,TT",
			"TT.P.~~~~~~~~~~~~~~~~~.P.....w..\".......%...\"..%.%....PPP.........%TTT",
			"TTT..~~~~~~~,P,~~~~~~~...........b..%.\".........,.,.....P...P....,..TT",
			"TT...~~~~~~~~~~~~~~~~~P.......%....,...P,........\"%\".P%.%.\",\".....T.TT",
			"TT..T.~~~~~~~~~~~~~~~....%\".,....\"......\"\"\".%.%..\"%.P%....%,.......TTT",
			"TTT.TT.~~~~~~~~~~~~~.%.\"..%.\".\".%........\".\".\".\"......P.......\"..wTTTT",
			"TTTTT....P~~~~~~~\"..........%P.%...%\"......,P........\"%..\"..\"...P.T.TT",
			"TTT..%..P........,..\"...,.,%.\"....P.,......%...\".\"o.,........%...PTTTT",
			"TT.T..\".........P...............P,...%.,\"\"......P...........,....P\"TTT",
			"TT...,....\".T\"%TT%.%TT,.T.TT,T...TT\"TTT...PT%T...\"..TP.T..T.TTPT..TTTT",
			"TT.TT.TTT%T..T\".TTTT,TTT.%.T.TTT.TTT.TT...TTTTTTTT%.,T..T.T...TT.T.\"TT",
			"TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT",
			"TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT",
	};
	//MAP-END

	public static final int WIDTH = MAP[0].length();
	public static final int HEIGHT = MAP.length;

	//which cells belong to the village (inside the palisade) and which to the wild outskirts
	private static boolean[] townZone;
	private static boolean[] wildZone;

	@Override
	public String tilesTex() {
		return TaigaAssets.TILES;
	}

	@Override
	public String waterTex() {
		return TaigaAssets.WATER;
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.THEME_1, true);
	}

	//Level.create() also spawns the per-floor food, strength/upgrade drops etc. and rolls a level feeling.
	// None of that belongs to the village, and it would shift the item decks of the real floors.
	@Override
	public void create() {
		TargetedCell.cells.clear();
		Random.pushGenerator( Dungeon.seedCurDepth() );

		width = height = length = 0;
		transitions = new ArrayList<>();
		mobs = new HashSet<>();
		heaps = new SparseArray<>();
		blobs = new HashMap<>();
		plants = new SparseArray<>();
		traps = new SparseArray<>();
		customTiles = new ArrayList<>();
		customTerrain = new ArrayList<>();
		customWalls = new ArrayList<>();

		build();
		buildFlagMaps();
		cleanWalls();

		createMobs();
		createItems();

		Random.popGenerator();
	}

	private static char mapChar( int cell ){
		return MAP[cell / WIDTH].charAt(cell % WIDTH);
	}

	/** The firelit square is the starting point for new runs. */
	public int startCell(){ return 56 + 15 * WIDTH; }

	@Override public int exit(){
		if (getClass() == TaigaTownLevel.class && !TaigaQuests.summitKeyFound){
			LevelTransition trail = getTransition(LevelTransition.Type.BRANCH_EXIT);
			if (trail != null) return trail.cell();
		}
		return super.exit();
	}

	private static int terrainOf( char c ){
		switch (c){
			case 'T': return Terrain.WALL;
			case 't': return Terrain.WALL_DECO;
			case ',': return Terrain.EMPTY_DECO;
			case '"': return Terrain.GRASS;
			case '%': return Terrain.HIGH_GRASS;
			case 'P': return Terrain.STATUE;
			case 'S': return Terrain.STATUE_SP;
			case 'L': return Terrain.BOOKSHELF;
			case '+': return Terrain.DOOR;
			case '#': return Terrain.BARRICADE;
			case '~': return Terrain.WATER;
			case 'W': return Terrain.REGION_DECO;
			case 'F': return Terrain.REGION_DECO_ALT;
			case 'A': return Terrain.ALCHEMY;
			case 'X': return Terrain.EXIT;
			case 'U': return Terrain.ENTRANCE;
			case '_': case '=': case 'g': case '$': case 'f': case 'h': case 'u':
				return Terrain.EMPTY_SP;
			default:
				return Terrain.EMPTY;
		}
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);

		int trail = -1;
		for (int i = 0; i < length(); i++){
			char c = mapChar(i);
			map[i] = terrainOf(c);
			if (c == 'X'){
				//first: arrivals without a destination (the stairs from floor 1) come out of the mine
				transitions.add(0, new LevelTransition(this, i, LevelTransition.Type.REGULAR_EXIT));
			} else if (c == 'U'){
				trail = i;
			}
		}
		//the Old Trail up to the taiga floors (TaigaBranch)
		if (trail != -1){
			transitions.add(new LevelTransition(this, trail, LevelTransition.Type.BRANCH_EXIT,
					1, TaigaBranch.BRANCH, LevelTransition.Type.BRANCH_ENTRANCE));
		}

		//it's the hero's home village, the layout is known from the start
		Arrays.fill(mapped, true);
		return true;
	}

	private static void buildZones(){
		if (townZone != null) return;
		int len = WIDTH * HEIGHT;
		townZone = new boolean[len];
		wildZone = new boolean[len];

		boolean[] walkable = new boolean[len];
		ArrayList<Integer> gates = new ArrayList<>();
		int shaft = -1;
		for (int i = 0; i < len; i++){
			char c = mapChar(i);
			walkable[i] = "TtL#PSWFAg".indexOf(c) == -1;
			if (c == 'g') gates.add(i);
			if (c == 'X') shaft = i;
		}

		flood(shaft, walkable, townZone);
		for (int gate : gates){
			for (int n : new int[]{gate - 1, gate + 1, gate - WIDTH, gate + WIDTH}){
				if (walkable[n] && !townZone[n]){
					flood(n, walkable, wildZone);
				}
			}
		}
	}

	private static void flood( int start, boolean[] walkable, boolean[] out ){
		ArrayList<Integer> queue = new ArrayList<>();
		queue.add(start);
		out[start] = true;
		while (!queue.isEmpty()){
			int c = queue.remove(queue.size() - 1);
			for (int n : new int[]{c - 1, c + 1, c - WIDTH, c + WIDTH}){
				if (n >= 0 && n < out.length && walkable[n] && !out[n]){
					out[n] = true;
					queue.add(n);
				}
			}
		}
	}

	//coming up out of the mine, or back down the trail from the taiga
	public String arrivalMessage(){
		if (InterlevelScene.mode == InterlevelScene.Mode.DESCEND && Dungeon.depth == 0)
			return Messages.get(TaigaTownLevel.class, "begin");
		LevelTransition trail = getTransition( LevelTransition.Type.BRANCH_EXIT );
		boolean fromTrail = trail != null && trail.type == LevelTransition.Type.BRANCH_EXIT
				&& Dungeon.hero != null && trail.inside( Dungeon.hero.pos );
		return Messages.get(TaigaTownLevel.class, fromTrail ? "arrive_trail" : "arrive");
	}

	@Override
	public boolean activateTransition(com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero hero,
			LevelTransition transition){
		if (getClass() == TaigaTownLevel.class && transition.type == LevelTransition.Type.REGULAR_EXIT
				&& !TaigaQuests.summitKeyFound){
			com.shatteredpixel.shatteredpixeldungeon.utils.GLog.w(Messages.get(TaigaTownLevel.class, "shaft_sealed"));
			return false;
		}
		return super.activateTransition(hero, transition);
	}

	public static boolean inTown( int cell ){
		buildZones();
		return cell >= 0 && cell < townZone.length && townZone[cell];
	}

	public static boolean inWild( int cell ){
		buildZones();
		return cell >= 0 && cell < wildZone.length && wildZone[cell];
	}

	//cells a character of the village may not path through: villagers stay inside, wild animals outside
	public static boolean[] forbiddenCells( Char ch ){
		if (Dungeon.level == null || Dungeon.level.getClass() != TaigaTownLevel.class) return null;
		buildZones();
		if (ch instanceof TownNPC || ch instanceof TownTrader) return wildZone;
		if (ch instanceof TaigaBeast) return townZone;
		return null;
	}

	public interface Step {
		boolean take();
	}

	//runs a movement decision of ch with its forbidden cells treated as impassable
	public static boolean restrictedStep( Char ch, Step step ){
		boolean[] forbidden = forbiddenCells(ch);
		if (forbidden == null) return step.take();

		boolean[] passable = Dungeon.level.passable;
		boolean[] saved = passable.clone();
		for (int i = 0; i < passable.length; i++){
			if (forbidden[i]) passable[i] = false;
		}
		try {
			return step.take();
		} finally {
			System.arraycopy(saved, 0, passable, 0, passable.length);
		}
	}

	private int randomCell( boolean town, Char ch, boolean unseen ){
		buildZones();
		boolean[] zone = town ? townZone : wildZone;
		for (int tries = 0; tries < 50; tries++){
			int cell = Random.Int(length());
			if (zone[cell] && passable[cell]
					&& (!unseen || !heroFOV[cell])
					&& Actor.findChar(cell) == null){
				return cell;
			}
		}
		return -1;
	}

	@Override
	public int randomDestination( Char ch ) {
		if (ch instanceof TownNPC){
			int cell = randomCell(true, ch, false);
			if (cell != -1) return cell;
		} else if (ch instanceof TaigaBeast){
			int cell = randomCell(false, ch, false);
			if (cell != -1) return cell;
		}
		return super.randomDestination(ch);
	}

	@Override
	public int randomRespawnCell( Char ch ) {
		return randomCell(false, ch, true);
	}

	@Override
	public int mobLimit() {
		return 9;
	}

	@Override
	public Mob createMob() {
		switch (Random.Int(6)){
			case 0: case 1: return new SnowHare();
			case 2:         return new WildBoar();
			default:        return new Wolf();
		}
	}

	@Override
	protected void createMobs() {
		HashMap<Character, ArrayList<Integer>> stalls = new HashMap<>();
		int look = 0;
		for (int i = 0; i < length(); i++){
			char c = mapChar(i);
			Mob mob = null;
			switch (c){
				case 'f': mob = new FurTrader(); break;
				case 'h': mob = new Herbalist(); break;
				case 'u': mob = new Hunter(); break;
				case 'v':
					Villager v = new Villager();
					v.look = look++ % Villager.LOOKS;
					mob = v;
					break;
				case 'd': mob = new Husky(); break;
				case 'r': mob = new Reindeer(); break;
				case 'w': mob = new Wolf(); break;
				case 'b': mob = new SnowHare(); break;
				case 'o': mob = new WildBoar(); break;
			}
			if (mob != null){
				mob.pos = i;
				mobs.add(mob);
			}
		}

		//each trader owns the stalls in the same row of their cabin
		for (Mob m : mobs){
			if (m instanceof TownTrader){
				ArrayList<Integer> cells = new ArrayList<>();
				for (int dx = -3; dx <= 3; dx++){
					if (dx != 0 && mapChar(m.pos + dx) == '$') cells.add(m.pos + dx);
				}
				int[] arr = new int[cells.size()];
				for (int k = 0; k < arr.length; k++) arr[k] = cells.get(k);
				((TownTrader) m).stalls = arr;
			}
		}
	}

	//the deepest floor the stock of the traders was made for
	private int stockDepth = 0;

	@Override
	protected void createItems() {
		restock();
	}

	private void restock(){
		stockDepth = Statistics.deepestFloor;
		for (Mob m : mobs){
			if (m instanceof TownTrader){
				TownTrader t = (TownTrader) m;
				ArrayList<Item> stock = t.stock( Math.max(0, Math.min(4, stockDepth / 5)) );
				for (int k = 0; k < t.stalls.length; k++){
					int cell = t.stalls[k];
					Heap old = heaps.get(cell);
					if (old != null && old.type == Heap.Type.FOR_SALE){
						heaps.remove(cell);
					}
					if (k < stock.size() && heaps.get(cell) == null){
						Heap h = new Heap();
						h.type = Heap.Type.FOR_SALE;
						h.pos = cell;
						h.drop(stock.get(k));
						heaps.put(cell, h);
					}
				}
			}
		}
	}

	//shopkeepers.Shopkeeper.sellPrice: the village prices follow how deep the hero has been
	public static int price( Item item ){
		return item.value() * 5 * (Statistics.deepestFloor / 5 + 1);
	}

	@Override
	public Group addVisuals() {
		// The Last Hearth subclasses this level for its safe-town creation path, but
		// the home village's restock cycle and fire effects do not belong there.
		if (getClass() != TaigaTownLevel.class) return super.addVisuals();
		//the traders get new goods every time the hero has reached a new floor.
		// Done here as the scene creates the heap sprites right after this, so no sprites are needed yet.
		if (stockDepth < Statistics.deepestFloor){
			restock();
		}

		super.addVisuals();
		for (int i = 0; i < length(); i++){
			if (map[i] == Terrain.REGION_DECO_ALT){
				visuals.add(new Campfire(i));
			}
		}
		visuals.add(new Snowfall(width() * DungeonTilemap.SIZE, height() * DungeonTilemap.SIZE));
		return visuals;
	}

	public static class Campfire extends Emitter {

		private final int pos;

		public Campfire( int pos ){
			super();
			this.pos = pos;
			PointF p = DungeonTilemap.tileCenterToWorld(pos);
			pos(p.x - 3, p.y - 3, 6, 3);
			pour(FlameParticle.FACTORY, 0.1f);
			add(new Halo(20, 0xFFC864, 0.35f).point(p.x, p.y - 2));
			Emitter smoke = new Emitter();
			smoke.pos(p.x - 1, p.y - 12, 2, 2);
			smoke.pour(SmokeParticle.FACTORY, 0.4f);
			add(smoke);
		}

		@Override
		public void update() {
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				super.update();
			}
		}
	}

	@Override
	public String tileName( int tile ) {
		if (getClass() == TaigaTownLevel.class && tile == Terrain.EXIT && !TaigaQuests.summitKeyFound)
			return Messages.get(TaigaTownLevel.class, "sealed_shaft_name");
		String name = taigaTileName(tile, false);
		return name != null ? name : super.tileName(tile);
	}

	@Override
	public String tileDesc( int tile ) {
		if (getClass() == TaigaTownLevel.class && tile == Terrain.EXIT && !TaigaQuests.summitKeyFound)
			return Messages.get(TaigaTownLevel.class, "sealed_shaft_desc");
		String desc = taigaTileDesc(tile, false);
		return desc != null ? desc : super.tileDesc(tile);
	}

	//names of the taiga terrain, for the village and the taiga floors (wild = true), null for the default
	public static String taigaTileName( int tile, boolean wild ) {
		switch (tile){
			case Terrain.WALL: case Terrain.WALL_DECO:
				return Messages.get(TaigaTownLevel.class, "forest_name");
			case Terrain.BOOKSHELF:
				return Messages.get(TaigaTownLevel.class, "logs_name");
			case Terrain.BARRICADE:
				return Messages.get(TaigaTownLevel.class, "palisade_name");
			case Terrain.STATUE:
				return Messages.get(TaigaTownLevel.class, "spruce_name");
			case Terrain.STATUE_SP:
				return Messages.get(TaigaTownLevel.class, "festive_spruce_name");
			case Terrain.REGION_DECO:
				return Messages.get(TaigaTownLevel.class, "woodpile_name");
			case Terrain.REGION_DECO_ALT:
				return Messages.get(TaigaTownLevel.class, "campfire_name");
			case Terrain.WATER:
				return Messages.get(TaigaTownLevel.class, "stream_name");
			case Terrain.EMPTY: case Terrain.EMPTY_DECO:
				return Messages.get(TaigaTownLevel.class, "snow_name");
			case Terrain.GRASS:
				return Messages.get(TaigaTownLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(TaigaTownLevel.class, "shrubs_name");
			case Terrain.FURROWED_GRASS:
				return Messages.get(TaigaTownLevel.class, "trampled_name");
			case Terrain.EMPTY_SP:
				return Messages.get(TaigaTownLevel.class, "planks_name");
			case Terrain.ENTRANCE:
				return Messages.get(TaigaTownLevel.class, "trail_up_name");
			case Terrain.EXIT:
				return Messages.get(TaigaTownLevel.class, wild ? "trail_down_name" : "shaft_name");
			case Terrain.ALCHEMY:
				return Messages.get(TaigaTownLevel.class, "cauldron_name");
			default:
				return null;
		}
	}

	public static String taigaTileDesc( int tile, boolean wild ) {
		switch (tile){
			case Terrain.WALL: case Terrain.WALL_DECO:
				return Messages.get(TaigaTownLevel.class, "forest_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(TaigaTownLevel.class, "logs_desc");
			case Terrain.BARRICADE:
				return Messages.get(TaigaTownLevel.class, "palisade_desc");
			case Terrain.STATUE:
				return Messages.get(TaigaTownLevel.class, "spruce_desc");
			case Terrain.STATUE_SP:
				return Messages.get(TaigaTownLevel.class, "festive_spruce_desc");
			case Terrain.REGION_DECO:
				return Messages.get(TaigaTownLevel.class, "woodpile_desc");
			case Terrain.REGION_DECO_ALT:
				return Messages.get(TaigaTownLevel.class, "campfire_desc");
			case Terrain.WATER:
				return Messages.get(TaigaTownLevel.class, "stream_desc");
			case Terrain.EMPTY_DECO:
				return Messages.get(TaigaTownLevel.class, "snow_desc");
			case Terrain.GRASS:
				return Messages.get(TaigaTownLevel.class, "grass_desc");
			case Terrain.HIGH_GRASS:
				return Messages.get(TaigaTownLevel.class, "shrubs_desc");
			case Terrain.ENTRANCE:
				return Messages.get(TaigaTownLevel.class, "trail_up_desc");
			case Terrain.EXIT:
				return Messages.get(TaigaTownLevel.class, wild ? "trail_down_desc" : "shaft_desc");
			case Terrain.ALCHEMY:
				return Messages.get(TaigaTownLevel.class, "cauldron_desc");
			default:
				return null;
		}
	}

	private static final String STOCK_DEPTH = "stock_depth";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STOCK_DEPTH, stockDepth );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		stockDepth = bundle.getInt( STOCK_DEPTH );
	}
}
