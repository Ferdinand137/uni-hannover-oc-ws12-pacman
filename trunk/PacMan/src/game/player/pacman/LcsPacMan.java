package game.player.pacman;

import game.core.G;
import game.core.Game;
import game.player.pacman.lcs.RuleFunctions;
import gui.AbstractPlayer;

import java.util.Vector;

public final class LcsPacMan extends AbstractPlayer{

	interface Condition {
		boolean match();
	}

	abstract class DistanceContition implements Condition {
		float min, max;

		public DistanceContition(float min, float max) {
			this.min = min;
			this.max = max;
		}
	}

	class GhostDistanceContition extends DistanceContition {
		
		public GhostDistanceContition(float min, float max) {
			super(min, max);
		}

		public boolean match() {
			int dist = ruleFunctions.getNextGhostDistance();
			return min <= dist && dist <= max;
		}
	}
	
	class MultipleConditions implements Condition {
		Vector<Condition> conditions = new Vector<Condition>();
		
		public boolean match() {
			for (Condition condition : conditions) {
				if(!condition.match())
					return false;
			}
			return true;
		}
	}
	
	RuleFunctions ruleFunctions;
	static Game game;
	
	@Override
	public int getAction(Game game,long timeDue){
		Vector<MultipleConditions> conditions = new Vector<MultipleConditions>();
		
		MultipleConditions test = new MultipleConditions();
		test.conditions.add(new GhostDistanceContition(5, 10));
		
		this.game = game;
		
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