package game.player.pacman;

import game.core.G;
import game.core.Game;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.MultipleConditions;
import game.player.pacman.lcs.RuleFunctions;
import game.player.pacman.lcs.Thing;
import gui.AbstractPlayer;

import java.util.Vector;

public final class LcsPacMan extends AbstractPlayer{
	
	RuleFunctions ruleFunctions;
	static Game game;
	
	@Override
	public int getAction(Game game,long timeDue){
		Vector<MultipleConditions> conditions = new Vector<MultipleConditions>();
		
		MultipleConditions test = new MultipleConditions();
		test.add(new DistanceCondition(Thing.GHOST, 5, 10));
		test.add(new DistanceCondition(Thing.PILL, 1, 3));
		conditions.add(test);
		
		this.game = game;

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