package game.player.pacman;

import game.core.Game;
import game.player.pacman.lcs.Direction;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.EdibleCondition;
import game.player.pacman.lcs.FitnessSave;
import game.player.pacman.lcs.JunctionCondition;
import game.player.pacman.lcs.MoveAction;
import game.player.pacman.lcs.MoveRecommendation;
import game.player.pacman.lcs.Rule;
import game.player.pacman.lcs.RuleFunctions;
import game.player.pacman.lcs.Thing;
import gui.AbstractPlayer;

import java.util.Vector;

/**
 * For debugging time consumption. According to first measurements this does not slow down execution at all. (0,002 ms over 10 secs of 100% cpu)
 */
class Timer {
	private long time, counter, start;

	void start() {
		start = System.nanoTime();
	}

	void stop() {
		time += System.nanoTime() - start;
		counter++;
	}

	float getAvgInMs() {
		return time / counter / 1000 / 1000.0f;
	}
}

class ActionSet {
	final Vector<Rule> actionSet = new Vector<Rule>();
	final static float beta = 0.5f;
	final static float discount_delta = 0.99f;
	final static float tax_tau = 0.9999f;

	static {
		assert beta > 0;
		assert beta <= 1;
		assert discount_delta > 0;
		assert discount_delta <= 1;
		assert tax_tau > 0;
	}

	public void add(final Rule rule) {
		actionSet.add(rule);
	}

	public void doStuff(final ActionSet lastActionSet) {
		float bucket = 0;

		// redistribute fitness
		for (final Rule rule : actionSet) {
			final float fitness = rule.getFitness();
			bucket += fitness * beta;
			rule.setFitness(fitness * (1-beta));
		}

		bucket *= discount_delta;

		// at first round lastActionSet is empty
		if(lastActionSet != null) {
			lastActionSet.addFitnessToRules(bucket / lastActionSet.size());
		}

		// tax all rules not in here
		for (final Rule rule : LcsPacMan.ruleSet) {
			if(!actionSet.contains(rule)) {
				rule.setFitness(rule.getFitness() * tax_tau);
			}
		}
	}

	private void addFitnessToRules(final float fitness) {
		for (final Rule rule : actionSet) {
			rule.setFitness(rule.getFitness() + fitness);
		}
	}

	void doRewardStuff(final float reward) {
		for (final Rule rule : actionSet) {
			rule.setFitness(rule.getFitness() + reward * beta / size());
		}
	}

	private float size() {
		return actionSet.size();
	}
}

public final class LcsPacMan extends AbstractPlayer{

	final static Vector<Rule> ruleSet = new Vector<Rule>();

	final static Timer timer_total = new Timer(), timer_prepare = new Timer(), timer_match = new Timer(), timer_getDirection = new Timer(), timer_training = new Timer();

	ActionSet lastActionSet;

	public LcsPacMan() {
		//System.out.println("---");
		//System.out.println("---");
		System.out.println("neuer pacman");
		//System.out.println("---");
		//System.out.println("---");
	}

	static {
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.PILL)));
		ruleSet.add(new Rule().add(new JunctionCondition()).setAction(new MoveAction(Thing.JUNCTION)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 5)).add(new DistanceCondition(Thing.POWER_PILL, 0, 7.5f)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 2.5f)).add(new DistanceCondition(Thing.POWER_PILL, 0, 10)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 5, 10)).add(new DistanceCondition(Thing.POWER_PILL, 5, 10)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 50)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.GHOST, true)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.POWER_PILL, 0, 27.5f)).setAction(new MoveAction(Thing.POWER_PILL, true)));

		// junction rule on steroids
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 50)).add(new JunctionCondition()).setAction(new MoveAction(Thing.JUNCTION)));
	}

	@Override
	public int getAction(final Game game,final long timeDue){
		timer_total.start();

		//System.out.println("time: " + (timeDue - System.currentTimeMillis()));
		//System.out.println("---");

		timer_prepare.start();
		RuleFunctions.prepareNextRound(game);
		timer_prepare.stop();

		//float[] direction_weight = new float[4];

//		int regelZumAusgebenNurBlub = 0;

		final MoveRecommendation move = new MoveRecommendation();
		for (final Rule rule : ruleSet) {
//			++regelZumAusgebenNurBlub;

			timer_match.start();
			final boolean match = rule.match(game);
			timer_match.stop();

			if(match) {
				timer_getDirection.start();
				final MoveRecommendation ruleMove = rule.generateMove(game);
				move.addFitness(ruleMove);
				timer_getDirection.stop();
			}
		}

		final Direction dir = move.getRouletteFitness();
		//System.out.println(move + " -> laufe nach: " + dir);

		final ActionSet actionSet = new ActionSet();
		for (final Rule rule : ruleSet) {
			final MoveRecommendation ruleMove = rule.getMove();
			if(ruleMove != null && ruleMove.getFitness(dir) > 0) {
				actionSet.add(rule);
			}
		}

		actionSet.doStuff(lastActionSet);
/*
		System.out.println("\n\nRegeln vor doStuff:");
		for (final Rule rule : ruleSet) {
			System.out.println(rule);
		}
		actionSet.doStuff(lastActionSet);
		System.out.println("\nRegeln nach doStuff:");
		for (final Rule rule : ruleSet) {
			System.out.println(rule);
		}
		System.out.println("\n\n\n\n");
*/
		lastActionSet = actionSet;

		// dann nachm motto:
		//if(test.match(game)) test.getActionDirection(game);
		// TODO aus conditions alle raussuchen wo match true
		// von denen raussuchen in welche richtung sie laufen wollen
		// erstmal zB einfache mehrheitentscheidung
		//test.getActionDirection(game); // sollte funktionieren sobald marcus seins fertig hat
		timer_total.stop();

		return dir.toInt();
	}

	@Override
	public String getGroupName() {
		return "Gruppe2_PacMan";
	}

	public void trainingBegin(final int totalTrainings) {
		timer_training.start();
	}

	int lastAvgScore = -1;
	int trainingScore = 0;
	public void trainingRoundOver(final int round, final int totalTrainings, final Game game) {
		trainingScore += game.getScore();

		System.out.println("\n\nRegeln nach Runde " + round + ":");
		for (final Rule rule : ruleSet) {
			System.out.println(rule);
		}

		lastActionSet.doRewardStuff(game.getScore());

		System.out.println("\n\nRegeln nach Runde " + round + " + Reward:");
		for (final Rule rule : ruleSet) {
			System.out.println(rule);
		}

		lastActionSet = null;

		// for the moment only 10 rounds per training. this is WAY too few but it's so damn slow :(
		final int GAMES_PER_TRAINING = 100;

		/*
		if((round+1) % GAMES_PER_TRAINING == 0) {
			timer_training.stop();

			trainingScore /= GAMES_PER_TRAINING;

			System.out.println("\n\n");

			if(lastAvgScore >= 0) {
				// TODO evtl erst berücksichtigen wenn änderung > 1%
				if(lastAvgScore > trainingScore) {
					System.out.println("bad mutation happened: " + lastAvgScore + " > " + trainingScore);
					FitnessSave.revertMutation();
				} else {
					System.out.println("good mutation happened: " + lastAvgScore + " > " + trainingScore);
					lastAvgScore = trainingScore;
				}
			} else {
				lastAvgScore = trainingScore;
			}


			System.out.println("");
			System.out.println("Avg getAction    time: " + timer_total.getAvgInMs()        + "ms");
			System.out.println("Avg prepare      time: " + timer_prepare.getAvgInMs()      + "ms");
			System.out.println("Avg match        time: " + timer_match.getAvgInMs()        + "ms");
			System.out.println("Avg getDirection time: " + timer_getDirection.getAvgInMs()  + "ms");
			System.out.println("Avg training     time: " + timer_training.getAvgInMs()/GAMES_PER_TRAINING + "ms");
			System.out.println("    training     time: " + timer_training.getAvgInMs()/1000.0f + "s");

			System.out.println("Avg score after " + GAMES_PER_TRAINING + " rounds: " + trainingScore);
			System.out.println("Training " + round * 100.0f / totalTrainings + "% complete");
			System.out.println("");
			FitnessSave.mutate();

			timer_training.start();
		}
		*/
	}

	public void trainingOver(final int trainings) {
		FitnessSave.dump();
	}
}