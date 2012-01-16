package game.player.pacman;

import game.core.Game;
import game.player.pacman.lcs.Direction;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.FitnessSave;
import game.player.pacman.lcs.MoveAction;
import game.player.pacman.lcs.MoveRecommendation;
import game.player.pacman.lcs.Rule;
import game.player.pacman.lcs.RuleFunctions;
import game.player.pacman.lcs.Thing;
import gui.AbstractPlayer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

public final class LcsPacMan extends AbstractPlayer{

	final static Vector<Rule> ruleSet = new Vector<Rule>();
	private static boolean lastTrainingRound = true;
	static BufferedWriter log;
	public static void debug(final String s) {
		if(!lastTrainingRound) return;

		try {
			log.append(s);
			log.append('\n');
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
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
		/*
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.PILL)));
		ruleSet.add(new Rule().add(new JunctionCondition()).setAction(new MoveAction(Thing.JUNCTION)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 5)).add(new DistanceCondition(Thing.POWER_PILL, 0, 7.5f)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 2.5f)).add(new DistanceCondition(Thing.POWER_PILL, 0, 10)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 5, 10)).add(new DistanceCondition(Thing.POWER_PILL, 5, 10)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 50)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.GHOST, true)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.POWER_PILL, 0, 27.5f)).setAction(new MoveAction(Thing.POWER_PILL, true)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 50)).add(new JunctionCondition()).setAction(new MoveAction(Thing.JUNCTION)));
		*/

		// rein probehalber mal simple regeln:
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.PILL)));
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.PILL, true)));
		//ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 20)).setAction(new MoveAction(Thing.GHOST)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 20)).setAction(new MoveAction(Thing.GHOST, true)));
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.TURN_BACK, true)));

		try {
			final File f = File.createTempFile("pacman", "log");
			f.deleteOnExit();

			log = new BufferedWriter(new FileWriter(f));

			System.out.println(f.getAbsolutePath());
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int scoreAtLastGetActionCall = 0;

	@Override
	public int getAction(final Game game,final long timeDue){
		timer_total.start();

		debug("---------------------------------------");

		assert game.getScore() >= scoreAtLastGetActionCall;
		if(lastActionSet != null && game.getScore() != scoreAtLastGetActionCall) {
			debug("extra reward: " + (game.getScore() - scoreAtLastGetActionCall));
			lastActionSet.doRewardStuff(game.getScore() - scoreAtLastGetActionCall);
		}
		scoreAtLastGetActionCall = game.getScore();

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
		debug(move + " -> laufe nach: " + dir);

		final ActionSet actionSet = new ActionSet();
		for (final Rule rule : ruleSet) {
			final MoveRecommendation ruleMove = rule.getMove();
			if(ruleMove != null && ruleMove.getFitness(dir) > 0) {
				debug("rule {{" + rule + "}} sagte " + dir + " ebenfalls vorraus: " + ruleMove);
				actionSet.add(rule);
			}
		}

		if(lastActionSet != null) {
			lastActionSet.doRewardStuff(1.0f);
			if(lastTrainingRound) {
				debug("\n\nRegeln nach reward set krams:");
				for (final Rule rule : ruleSet) {
					debug(rule.toString());
				}
				debug("\n");
			}
		}

		actionSet.doStuffEveryGetActionCall(lastActionSet);

		if(lastTrainingRound) {
			debug("\n\nRegeln nach action set krams:");
			for (final Rule rule : ruleSet) {
				debug(rule.toString());
			}
		}
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
		lastTrainingRound = false;
	}

	int lastAvgScore = -1;
	int trainingScore = 0;
	public void trainingRoundOver(final int round, final int totalTrainings, final Game game) {
		if(round == totalTrainings-1) {
			lastTrainingRound = true;
		}

		trainingScore += game.getScore();
		scoreAtLastGetActionCall = 0;

		System.out.println("\n\nRegeln nach Runde " + round + ":");
		for (final Rule rule : ruleSet) {
			System.out.println(rule);
		}

		// abzug für tot! nur kleiner abzug da diese regel evtl gar nichts dafür kann
		// lastActionSet.doRewardStuff(-1.0f);

		lastActionSet = null;

		// for the moment only 10 rounds per training. this is WAY too few but it's so damn slow :(
		//final int GAMES_PER_TRAINING = 100;

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