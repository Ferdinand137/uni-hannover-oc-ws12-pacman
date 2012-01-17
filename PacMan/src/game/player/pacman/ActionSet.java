package game.player.pacman;

import game.player.pacman.lcs.Rule;

import java.util.Vector;

public class ActionSet {
	final Vector<Rule> actionSet = new Vector<Rule>();

	// verschiedene werte probiert. macht immer keinen sinn
	final static float BETA = 0.3f; // investiere 10% von fitness um bei erfolg 1.0 wiederzubekommen
	final static float DISCOUNT_DELTA = 0.0000000000001f;
	final static float TAX_TAU_NOT_TAKEN = 0.9f;
	final static float TAX_TAU_OTHERS = 0.99f;

	static {
		assert BETA > 0;
		assert BETA <= 1;
		assert DISCOUNT_DELTA > 0;
		assert DISCOUNT_DELTA <= 1;
		assert TAX_TAU_OTHERS > 0;
		assert TAX_TAU_OTHERS <= 1;
		assert TAX_TAU_NOT_TAKEN > 0;
		assert TAX_TAU_NOT_TAKEN <= 1;
	}

	public void add(final Rule rule) {
		actionSet.add(rule);
	}

	public void doStuffEveryGetActionCall(final ActionSet lastActionSet) {
		float bucket = 0;

		// redistribute fitness into bucket
		for (final Rule rule : actionSet) {
			LcsPacMan.debug("action set rule vor beta: " + rule);
			final float fitness = rule.getFitness();
			bucket += fitness * BETA;
			rule.setFitness(fitness * (1-BETA));
			LcsPacMan.debug("action set rule nach beta: " + rule);
		}

		// bucket abwerten
		bucket *= DISCOUNT_DELTA;

		// at first round lastActionSet is empty
		if(lastActionSet != null) {
			// bucket umverteilen auf altes action set
			LcsPacMan.debug("Verteile " + bucket / lastActionSet.size() + " auf lastActionSet");
			lastActionSet.addFitnessToRules(bucket / lastActionSet.size());
		}

		// tax all rules not in here
		for (final Rule rule : LcsPacMan.ruleSet) {
			if(!actionSet.contains(rule)) {
				LcsPacMan.debug("other rule vor tau: " + rule);
				// ist tax überhaupt prozentual?

				if(rule.getMove() != null) {
					rule.setFitness(rule.getFitness() * TAX_TAU_NOT_TAKEN);
					LcsPacMan.debug("other rule nach tau not taken: " + rule);
				} else {
					rule.setFitness(rule.getFitness() * TAX_TAU_OTHERS);
					LcsPacMan.debug("other rule nach tau others: " + rule);
				}
			}
		}

		LcsPacMan.debug("nach doStuffEveryGetActionCall: " + this);
	}

	private void addFitnessToRules(final float fitness) {
		for (final Rule rule : actionSet) {
			rule.setFitness(rule.getFitness() + fitness);
		}
	}

	void doRewardStuff(final float reward) {
		if(reward == 0) return;

		LcsPacMan.debug("vor reward: " + this);
		for (final Rule rule : actionSet) {
			rule.setFitness(rule.getFitness() + reward / size());
		}
		LcsPacMan.debug("nach reward: " + this);
	}

	private float size() {
		return actionSet.size();
	}

	@Override
	public String toString() {
		String r = "ActionSet {";
		for (final Rule rule : actionSet) {
			r += rule + ", ";
		}
		return r + "}";
	}
}