package game.player.pacman.lcs;

import game.core.G;
import game.core.Game;

public class RuleFunctions {

	private RuleFunctions() {}
	
	public static int getNextGhostDistance(Game game) {
		int currentLocation = game.getCurPacManLoc();
		SetOfPaths pathsToGhosts = new SetOfPaths();
		
		for(int i=0;i<G.NUM_GHOSTS;i++) {
			if(game.getLairTime(i) > 0) continue; // ignore erstmal

			pathsToGhosts.add(game.getPath(currentLocation, game.getCurGhostLoc(i)));
		}
		
		if(pathsToGhosts.isEmpty()) return Integer.MAX_VALUE;
		
		return pathsToGhosts.getShortest().length;
	}

	public static int getNextPillDistance(Game game) {
		// TODO
		return 0;
	}

	public static int getNextPowerPillDistance(Game game) {
		// TODO
		return 0;
	}
	
	
}
