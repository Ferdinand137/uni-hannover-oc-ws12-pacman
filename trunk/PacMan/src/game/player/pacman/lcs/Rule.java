package game.player.pacman.lcs;


import game.core.Game;

import java.util.Vector;

public class Rule implements Condition {
	private Vector<Condition> conditions = new Vector<Condition>();
	private MoveAction action;
	
	/*
	 * > 0 good rule
	 * < 0 bad rule, e.g. ghost near -> go to ghost; wird negativ gewertet
	 */
	private float fitness = 1;
	
	// TODO Regeln brauchen ne art Wahrscheinlichkeit - evtl das gleiche wie fitness
	// erstmal einfach alle den gleichen wert :)
	
	public boolean match(Game game) {
		for (Condition condition : conditions) {
			if(!condition.match(game))
				return false;
		}
		return true;
	}

	public void add(Condition condition) {
		conditions.add(condition);
	}

	public void setAction(MoveAction action) {
		this.action = action;
	}
	
	public void setFitness(float fitness) {
		this.fitness = fitness;
	}
	
	public MoveRecommendation getActionDirection(Game game) {
		return new MoveRecommendation(action.getDirection(game), fitness);
	}
}