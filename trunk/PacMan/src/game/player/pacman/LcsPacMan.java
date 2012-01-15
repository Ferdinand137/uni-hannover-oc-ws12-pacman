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
public final class LcsPacMan extends AbstractPlayer{

	Vector<Rule> ruleSet = new Vector<Rule>();

	Timer timer_total = new Timer(), timer_prepare = new Timer(), timer_match = new Timer(), timer_getDirection = new Timer(), timer_training = new Timer();

	static Game game;
	 public LcsPacMan() {
		//System.out.println("---");
		//System.out.println("---");
		System.out.println("neuer pacman");
		//System.out.println("---");
		//System.out.println("---");

		ruleSet.add(new Rule().setAction(new MoveAction(Thing.PILL)));
		ruleSet.add(new Rule().add(new JunctionCondition()).setAction(new MoveAction(Thing.JUNCTION)).setFitness(2.0f));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 5)).add(new DistanceCondition(Thing.POWER_PILL, 0, 7.5f)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 2.5f)).add(new DistanceCondition(Thing.POWER_PILL, 0, 10)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 5, 10)).add(new DistanceCondition(Thing.POWER_PILL, 5, 10)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 30)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.GHOST)).setFitness(-10));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.POWER_PILL, 0, 27.5f)).setAction(new MoveAction(Thing.POWER_PILL)).setFitness(-1));

	}

	@Override
	public int getAction(final Game game,final long timeDue){
		timer_total.start();

		//System.out.println("time: " + (timeDue - System.currentTimeMillis()));
		//System.out.println("---");

		timer_prepare.start();
		RuleFunctions.prepareNextRound(game);

		// #UP=0, #RIGHT=1, #DOWN=2, #LEFT=3
		final float[] fitnessPerDirection = new float[4];

		for(int i = 0; i < 4; i++) {
			if(game.getNeighbour(game.getCurPacManLoc(), i) == -1) {
				// in die richtung ist ne wand!
				fitnessPerDirection[i] = Float.NEGATIVE_INFINITY;
			}
		}
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
		System.out.println("-> laufe nach: " + dir);

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

		// for the moment only 10 rounds per training. this is WAY too few but it's so damn slow :(
		final int GAMES_PER_TRAINING = 100;

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
	}

	public void trainingOver(final int trainings) {
		FitnessSave.dump();
	}
}