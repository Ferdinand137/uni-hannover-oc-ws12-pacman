package game.player.pacman.lcs;


import game.core.Game;

import java.util.Vector;

public class Rule implements Condition {
	private final Vector<Condition> conditions = new Vector<Condition>();
	private MoveAction action;
	private MoveRecommendation move;

	/*
	 * > 0 good rule
	 * < 0 bad rule, e.g. ghost near -> go to ghost; wird negativ gewertet
	 */
	// private float fitness = 1;

	// TODO Regeln brauchen ne art Wahrscheinlichkeit - evtl das gleiche wie fitness
	// erstmal einfach alle den gleichen wert :)

	@Override
	public boolean match(final Game game) {
		move = null;

		for (final Condition condition : conditions) {
			if(!condition.match(game))
				return false;
		}
		return true;
	}

	public Rule add(final Condition condition) {
		conditions.add(condition);
		return this;
	}

	public Rule setAction(final MoveAction action) {
		this.action = action;
		return this;
	}

	public Rule setFitness(final float fitness) {
		// this.fitness = fitness;
		FitnessSave.set(toId(), fitness);
		return this;
	}

	public MoveRecommendation generateMove(final Game game) {
		final float fitness = getFitness();
		move = action.getMove(game, fitness);
		return move;
	}

	public MoveRecommendation getMove() {
		return move;
	}

	@Override
	public String toId() {
		String r = "RULE:";
		for (final Condition c : conditions) {
			r += c.toId();
		}
		r += action.toId();
		return r;
	}


	@Override
	public String toString() {
		String r = "RULE: If";

		boolean first = true;
		if(conditions.size() > 0) {
			for (final Condition c : conditions) {
				if(first) {
					first = false;
				} else {
					r += " and";
				}
				r += " (" + c.toString() + ")";
			}
		} else {
			r += " (true)";
		}
		r += " then " + action.toString();
		r += " with fitness " + Math.round(getFitness()*10)/10.0f;
		return r;
	}

	public float getFitness() {
		return FitnessSave.get(toId());
	}
}