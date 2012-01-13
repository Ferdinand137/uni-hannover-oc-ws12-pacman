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

	public Rule add(Condition condition) {
		conditions.add(condition);
		return this;
	}

	public Rule setAction(MoveAction action) {
		this.action = action;
		return this;
	}
	
	public Rule setFitness(float fitness) {
		this.fitness = fitness;
		return this;
	}
	
	public MoveRecommendation getActionDirection(Game game) {
		return action.getDirection(game, fitness);
	}
}