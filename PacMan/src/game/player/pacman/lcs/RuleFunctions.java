package game.player.pacman.lcs;

import game.core.G;
import game.core.Game;

public class RuleFunctions {
	Game game;
	
	SetOfPaths pathsToGhosts;
	int currentLocation;

	public RuleFunctions(Game game) {
		this.game = game;
		
		// einige sachen berechnen, da die getter das später eh brauchen werden
		currentLocation = game.getCurPacManLoc();
		
		for(int i=0;i<G.NUM_GHOSTS;i++) {
			if(game.getLairTime(i) > 0) continue; // ignore erstmal

			pathsToGhosts.add(game.getPath(currentLocation, game.getCurGhostLoc(i)));
		}
	}
	
	int getNextGhostDistance() {
		return pathsToGhosts.getShortest().length;
	}
	
	int getNextPillDistance() {
		// TODO
		return 0;
	}
	
}
