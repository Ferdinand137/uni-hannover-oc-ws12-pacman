package game.player.pacman.lcs;

import game.core.G;
import game.core.Game;

public class RuleFunctions {

	static int currentLocation;
	static SetOfPaths pathsToGhosts;
	static int[] closestGhost;
	static int[] closestPill;
	static int[] closestPowerPill;
	static Game game;

	private RuleFunctions() {}
	
	public static void prepareNextRound(Game game) {
		RuleFunctions.game = game;
		
		currentLocation = game.getCurPacManLoc();
		
		pathsToGhosts = new SetOfPaths();
		for(int i=0;i<G.NUM_GHOSTS;i++) {
			if(game.getLairTime(i) > 0) continue; // ignore erstmal

			pathsToGhosts.add(game.getPath(currentLocation, game.getCurGhostLoc(i)));
		}
		
		closestGhost = pathsToGhosts.isEmpty() ? null : pathsToGhosts.getShortest();
		
		
		
		// TODO Marcus (war doch c?? irgendwann merk ich mir das mal) cloestPill und closestPowerPill
		// da die konkreten wege reinspeichern
	}
	
	public static int getNextGhostDistance() {
		if(closestGhost == null) return Integer.MAX_VALUE;
		return closestGhost.length;
	}

	public static int getNextPillDistance() {
		// TODO distance nach cloestPill, müsste eigtl genau wie bei ghost gehen
		return 0;
	}

	public static int getNextPowerPillDistance() {
		// TODO distance nach cloestPowerPill
		return 0;
	}
	
	public static int getNextGhostDirection() {
		// FIXME Achtung völlig ungetestet! 0 plan obs stimmt
		assert closestGhost != null;
		
		for(int i = 0; i < 4; i++)
			if(game.getNeighbour(currentLocation, i) == closestGhost[1]) return i;
		
		throw new RuntimeException("Weg zum Geist führt nicht über Nachbarzelle");
	}
	
	public static int getNextPillDirection() {
		assert closestPill != null;
		return closestPill[1]; // TODO fix wie getNextGhostDirection
	}
	
	public static int getNextPowerPillDirection() {
		assert closestPowerPill != null;
		return closestPowerPill[1]; // TODO fix wie getNextGhostDirection
	}
}
