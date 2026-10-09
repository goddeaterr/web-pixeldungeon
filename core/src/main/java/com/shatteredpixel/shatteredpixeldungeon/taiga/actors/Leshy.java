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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.taiga.TaigaQuests;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.HeartOfTheTaiga;
import com.shatteredpixel.shatteredpixeldungeon.taiga.items.LeshyCrook;
import com.shatteredpixel.shatteredpixeldungeon.taiga.levels.TaigaBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.taiga.sprites.TaigaSprites;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * MOD (taiga town): the Leshy, lord of the taiga, maddened by the evil rising from the dungeon (taiga floor 5).
 *
 * Phase 1: fights with his crook, marks the ground around the hero and makes roots burst out of it a turn later,
 *          slips into a spruce and steps out of another one, calls two frost wolves when first wounded.
 * Phase 2 (half health): a blizzard starts, two spruces of the grove wake up as treants, roots come faster
 *          and he fells spruces across the grove (the trunk falls along the marked line a turn later).
 */
public class Leshy extends Mob {

	{
		spriteClass = TaigaSprites.LeshySprite.class;

		HP = HT = 180;
		defenseSkill = 12;

		EXP = 40;
		maxLvl = 30;

		state = PASSIVE;

		properties.add( Property.BOSS );
		immunities.add( Roots.class );
		immunities.add( Chill.class );
	}

	private int phase = 1;
	private int rootsCooldown = 3;
	private int stepCooldown = 9;
	private int fallCooldown = 3;
	private boolean packCalled = false;

	private int[] pendingRoots = new int[0];
	private int[] pendingFall = new int[0];
	private int fallingTree = -1;

	public static class RootStrike {}
	public static class FallingTree {}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 5, 11 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 18;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( 1, 4 );
	}

	@Override
	public float speed() {
		return phase == 2 ? super.speed() * 1.25f : super.speed();
	}

	private void awaken(){
		if (state != PASSIVE) return;
		state = HUNTING;
		enemy = Dungeon.hero;
		target = Dungeon.hero.pos;
		BossHealthBar.assignBoss( this );
		Dungeon.level.seal();
		yell( Messages.get(this, "notice") );
		Music.INSTANCE.play( Assets.Music.CAVES_BOSS, true );
		if (sprite != null) sprite.showAlert();
	}

	@Override
	public void notice() {
		super.notice();
		awaken();
	}

	@Override
	protected boolean act() {
		Char hero = Dungeon.hero;

		if (state == PASSIVE){
			if (hero != null && hero.isAlive() && Dungeon.level.heroFOV[pos]
					&& Dungeon.level.distance( pos, hero.pos ) <= 8){
				awaken();
			} else {
				spend( TICK );
				return true;
			}
		}

		//attacks marked on the last turn land now
		if (pendingRoots.length > 0) eruptRoots();
		if (pendingFall.length > 0) fellTree();

		if (phase == 1 && HP * 2 <= HT) {
			enterPhase2();
			spend( TICK );
			return true;
		}

		if (!packCalled && HP * 4 <= HT * 3){
			packCalled = true;
			callPack( 2 );
		}

		boolean seesHero = hero != null && hero.isAlive() && Dungeon.level.heroFOV[pos];

		if (seesHero && --rootsCooldown <= 0){
			markRoots( hero );
			rootsCooldown = phase == 2 ? 4 : 6;
			spend( TICK );
			return true;
		}

		if (phase == 2 && seesHero && --fallCooldown <= 0){
			fallCooldown = 7;
			if (markFallingTree( hero )){
				spend( TICK );
				return true;
			}
		}

		if (--stepCooldown <= 0){
			stepCooldown = phase == 2 ? 7 : 10;
			if (treeStep( hero )){
				spend( TICK );
				return true;
			}
		}

		return super.act();
	}

	private void cast(){
		if (sprite instanceof TaigaSprites.LeshySprite){
			((TaigaSprites.LeshySprite) sprite).cast();
		}
	}

	//the ground around the hero is marked, roots burst out of it on the next turn
	private void markRoots( Char hero ){
		ArrayList<Integer> cells = new ArrayList<>();
		cells.add( hero.pos );
		int extra = phase == 2 ? 6 : 4;
		for (int tries = 0; tries < 30 && cells.size() < extra + 1; tries++){
			int cell = hero.pos + PathFinder.NEIGHBOURS8[Random.Int(8)] + PathFinder.NEIGHBOURS8[Random.Int(8)];
			if (cell >= 0 && cell < Dungeon.level.length() && Dungeon.level.passable[cell]
					&& !cells.contains(cell) && cell != pos){
				cells.add( cell );
			}
		}
		pendingRoots = new int[cells.size()];
		for (int i = 0; i < cells.size(); i++){
			pendingRoots[i] = cells.get(i);
			GameScene.targetedCell( cells.get(i), 0x44FF44, 1f );
		}
		cast();
		Sample.INSTANCE.play( Assets.Sounds.TRAMPLE, 1f, 0.6f );
		if (Dungeon.level.heroFOV[pos]) GLog.w( Messages.get(this, "roots_warn") );
	}

	private void eruptRoots(){
		for (int cell : pendingRoots){
			CellEmitter.get( cell ).burst( LeafParticle.GENERAL, 8 );
			Char ch = Actor.findChar( cell );
			if (ch != null && ch != this && ch.alignment != alignment){
				ch.damage( Random.NormalIntRange( 4, 9 ), new RootStrike() );
				if (ch.isAlive()){
					Buff.prolong( ch, Roots.class, 2f );
				} else if (ch == Dungeon.hero){
					Dungeon.fail( this );
					GLog.n( Messages.get(this, "roots_kill") );
				}
			}
			int terr = Dungeon.level.map[cell];
			if ((terr == Terrain.EMPTY || terr == Terrain.EMPTY_DECO || terr == Terrain.GRASS)
					&& Actor.findChar(cell) == null && Random.Int(4) == 0){
				Level.set( cell, Terrain.HIGH_GRASS );
				GameScene.updateMap( cell );
			}
		}
		Sample.INSTANCE.play( Assets.Sounds.TRAMPLE );
		pendingRoots = new int[0];
	}

	//a spruce near the hero will fall along a line towards them
	private boolean markFallingTree( Char hero ){
		ArrayList<Integer> spruces = new ArrayList<>();
		for (int i = 0; i < Dungeon.level.length(); i++){
			if (Dungeon.level.map[i] == Terrain.STATUE){
				int d = Dungeon.level.distance( i, hero.pos );
				if (d >= 2 && d <= 5) spruces.add( i );
			}
		}
		if (spruces.isEmpty()) return false;
		int tree = Random.element( spruces );
		Ballistica line = new Ballistica( tree, hero.pos, Ballistica.STOP_SOLID );
		ArrayList<Integer> cells = new ArrayList<>();
		for (int i = 1; i < line.path.size() && cells.size() < 6; i++){
			int cell = line.path.get(i);
			if (Dungeon.level.solid[cell]) break;
			cells.add( cell );
		}
		if (cells.isEmpty() || !cells.contains(hero.pos) && cells.size() < 3) return false;
		pendingFall = new int[cells.size()];
		for (int i = 0; i < cells.size(); i++){
			pendingFall[i] = cells.get(i);
			GameScene.targetedCell( cells.get(i), 0xFF4444, 1f );
		}
		fallingTree = tree;
		CellEmitter.get( tree ).burst( LeafParticle.GENERAL, 10 );
		cast();
		if (Dungeon.level.heroFOV[tree]) GLog.w( Messages.get(this, "tree_warn") );
		return true;
	}

	private void fellTree(){
		for (int cell : pendingFall){
			CellEmitter.get( cell ).burst( Speck.factory( Speck.WOOL ), 6 );
			Char ch = Actor.findChar( cell );
			if (ch != null && ch != this && ch.alignment != alignment){
				ch.damage( Random.NormalIntRange( 7, 13 ), new FallingTree() );
				if (!ch.isAlive() && ch == Dungeon.hero){
					Dungeon.fail( this );
					GLog.n( Messages.get(this, "tree_kill") );
				}
			}
		}
		if (fallingTree != -1 && Dungeon.level.map[fallingTree] == Terrain.STATUE){
			Level.set( fallingTree, Terrain.EMPTY_DECO );
			GameScene.updateMap( fallingTree );
		}
		PixelScene.shake( 2, 0.4f );
		Sample.INSTANCE.play( Assets.Sounds.ROCKS );
		pendingFall = new int[0];
		fallingTree = -1;
	}

	//slips into one spruce and steps out of another, away from the hero
	private boolean treeStep( Char hero ){
		ArrayList<Integer> spots = new ArrayList<>();
		for (int i = 0; i < Dungeon.level.length(); i++){
			if (Dungeon.level.map[i] != Terrain.STATUE) continue;
			for (int n : PathFinder.NEIGHBOURS4){
				int cell = i + n;
				if (cell < 0 || cell >= Dungeon.level.length()) continue;
				if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
				int d = hero != null ? Dungeon.level.distance( cell, hero.pos ) : 5;
				if (d >= 3 && d <= 7) spots.add( cell );
			}
		}
		if (spots.isEmpty()) return false;
		int dest = Random.element( spots );
		CellEmitter.get( pos ).burst( LeafParticle.GENERAL, 12 );
		ScrollOfTeleportation.appear( this, dest );
		CellEmitter.get( dest ).burst( LeafParticle.GENERAL, 12 );
		if (Dungeon.level.heroFOV[dest]) GLog.w( Messages.get(this, "step") );
		return true;
	}

	private void callPack( int count ){
		yell( Messages.get(this, "howl") );
		Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
		ArrayList<Integer> cells = new ArrayList<>();
		for (int i = 0; i < Dungeon.level.length(); i++){
			if (Dungeon.level.passable[i] && Actor.findChar(i) == null
					&& Dungeon.hero != null && Dungeon.level.distance( i, Dungeon.hero.pos ) >= 6){
				cells.add( i );
			}
		}
		for (int i = 0; i < count && !cells.isEmpty(); i++){
			FrostWolf wolf = new FrostWolf();
			wolf.pos = cells.remove( Random.Int(cells.size()) );
			wolf.state = wolf.HUNTING;
			GameScene.add( wolf, 1f );
			ScrollOfTeleportation.appearVFX( wolf );
		}
	}

	private void enterPhase2(){
		phase = 2;
		yell( Messages.get(this, "phase2") );
		BossHealthBar.bleed( true );
		Music.INSTANCE.play( Assets.Music.CAVES_BOSS_FINALE, true );
		if (Dungeon.level instanceof TaigaBossLevel){
			((TaigaBossLevel) Dungeon.level).startBlizzard();
		}

		//the two spruces closest to the hero wake up
		Char hero = Dungeon.hero;
		for (int n = 0; n < 2; n++){
			int best = -1;
			for (int i = 0; i < Dungeon.level.length(); i++){
				if (Dungeon.level.map[i] != Terrain.STATUE || Actor.findChar(i) != null) continue;
				if (best == -1 || Dungeon.level.distance( i, hero.pos ) < Dungeon.level.distance( best, hero.pos )){
					best = i;
				}
			}
			if (best == -1) break;
			Level.set( best, Terrain.EMPTY_DECO );
			GameScene.updateMap( best );
			SpruceTreant treant = new SpruceTreant();
			treant.pos = best;
			treant.alignment = Alignment.ENEMY;
			treant.state = treant.HUNTING;
			GameScene.add( treant, 1f );
			CellEmitter.get( best ).burst( LeafParticle.GENERAL, 15 );
		}
	}

	@Override
	public void damage( int dmg, Object src ) {
		if (state == PASSIVE) awaken();
		super.damage( dmg, src );
		if (isAlive() && phase == 1 && HP * 2 <= HT){
			BossHealthBar.bleed( true );
		}
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );

		Dungeon.level.unseal();
		GameScene.bossSlain();
		TaigaQuests.leshyDefeated = true;
		yell( Messages.get(this, "defeated") );

		Dungeon.level.drop( new LeshyCrook().identify(false), pos ).sprite.drop();
		Dungeon.level.drop( new HeartOfTheTaiga(), pos ).sprite.drop();
		Dungeon.level.drop( new PotionOfStrength(), pos ).sprite.drop();
		Dungeon.level.drop( new ScrollOfUpgrade(), pos ).sprite.drop();

		//the grove calms down
		for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
			if (m instanceof SpruceTreant || m instanceof FrostWolf){
				m.die( null );
			}
		}
		Dungeon.level.playLevelMusic();
	}

	private static final String PHASE = "phase";
	private static final String ROOTS_CD = "roots_cd";
	private static final String STEP_CD = "step_cd";
	private static final String FALL_CD = "fall_cd";
	private static final String PACK = "pack";
	private static final String PENDING_ROOTS = "pending_roots";
	private static final String PENDING_FALL = "pending_fall";
	private static final String FALLING_TREE = "falling_tree";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( PHASE, phase );
		bundle.put( ROOTS_CD, rootsCooldown );
		bundle.put( STEP_CD, stepCooldown );
		bundle.put( FALL_CD, fallCooldown );
		bundle.put( PACK, packCalled );
		bundle.put( PENDING_ROOTS, pendingRoots );
		bundle.put( PENDING_FALL, pendingFall );
		bundle.put( FALLING_TREE, fallingTree );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		phase = bundle.getInt( PHASE );
		rootsCooldown = bundle.getInt( ROOTS_CD );
		stepCooldown = bundle.getInt( STEP_CD );
		fallCooldown = bundle.getInt( FALL_CD );
		packCalled = bundle.getBoolean( PACK );
		if (phase < 1) phase = 1;
		pendingRoots = bundle.contains( PENDING_ROOTS ) ? bundle.getIntArray( PENDING_ROOTS ) : new int[0];
		pendingFall = bundle.contains( PENDING_FALL ) ? bundle.getIntArray( PENDING_FALL ) : new int[0];
		fallingTree = bundle.getInt( FALLING_TREE );
		if (state != PASSIVE) BossHealthBar.assignBoss( this );
		if (phase == 2) BossHealthBar.bleed( true );
	}
}
