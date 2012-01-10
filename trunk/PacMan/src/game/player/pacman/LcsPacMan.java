package game.player.pacman;

import game.core.G;
import game.core.Game;
import game.player.pacman.lcs.RuleFunctions;
import gui.AbstractPlayer;

public final class LcsPacMan extends AbstractPlayer{
	
	RuleFunctions ruleFunctions;
	
	@Override
	public int getAction(Game game,long timeDue){

		ruleFunctions = new RuleFunctions(game);
		
		int[] directions=game.getPossiblePacManDirs(false);
		int[] neighbours = game.getPacManNeighbours();
		
//		if(!pathsToGhosts.isEmpty()) {
			// TODO wenn neighbor[i] == ghostPfad[1] dann diesen weg abwerten oder so
//		}
		return directions[G.rnd.nextInt(directions.length)];		
	}

	@Override
	public String getGroupName() {
		return "Gruppe xy - Lcs PacMan";
	}
}