package game.player.ghost.lcs;

import game.core.Game;
import game.player.pacman.lcs.Direction;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.EdibleCondition;
import game.player.pacman.lcs.FitnessSave;
import game.player.pacman.lcs.MoveAction;
import game.player.pacman.lcs.MoveRecommendation;
import game.player.pacman.lcs.Rule;
import game.player.pacman.lcs.RuleFunctions;
import game.player.pacman.lcs.Thing;
import gui.AbstractGhost;

import java.util.Vector;

/**
 *
 * Neue LCS Geisterklasse
 *
 */
public class LcsGhost extends AbstractGhost
{
	public static Vector<Rule> ruleSet = new Vector<Rule>();

	public LcsGhost() {
		FitnessSave.clear();
		ruleSet.clear();

		// "default" geh richtung pacman
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.POWER_PILL)).setFitness(7));
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.PILL)).setFitness(9));

		// pacman nahe + !essbar => gehe hin
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 100)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.PACMAN)).setFitness(13));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 100, 250)).setAction(new MoveAction(Thing.PACMAN)).setFitness(9));

		// pacman nahe + essbar => gehe weg
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 70)).add(new EdibleCondition(true )).setAction(new MoveAction(Thing.PACMAN, true)).setFitness(16));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 100)).add(new EdibleCondition(true )).setAction(new MoveAction(Thing.PACMAN, true)).setFitness(17));

		// anderer geist sehr nahe + pacman fern => gehe weg
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 50)).add(new DistanceCondition(Thing.PACMAN, 50, 10000)).setAction(new MoveAction(Thing.GHOST, true)).setFitness(18));

		// anderer geist sehr nahe + pacman nahe => gehe weg
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 30)).add(new DistanceCondition(Thing.PACMAN, 0, 50)).setAction(new MoveAction(Thing.GHOST, true)).setFitness(19));

		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 30)).add(new DistanceCondition(Thing.POWER_PILL, 0, 30)).setAction(new MoveAction(Thing.POWER_PILL, true)).setFitness(19));

	}

	@Override
	public String getGhostGroupName() {
		return "Gruppe2_Geist";
	}

	static {
		// "default" geh richtung pacman
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.POWER_PILL)).setFitness(10));
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.PILL)).setFitness(9));

		// pacman nahe + !essbar => gehe hin
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 250)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.PACMAN)).setFitness(14));

		// pacman nahe + essbar => gehe weg
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 70)).add(new EdibleCondition(true )).setAction(new MoveAction(Thing.PACMAN, true)).setFitness(17));

		// anderer geist sehr nahe + pacman fern => gehe weg
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 50)).add(new DistanceCondition(Thing.PACMAN, 50, 10000)).setAction(new MoveAction(Thing.GHOST, true)).setFitness(17));

		// anderer geist sehr nahe + pacman nahe => gehe weg
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 30)).add(new DistanceCondition(Thing.PACMAN, 0, 50)).add(new EdibleCondition(true)).setAction(new MoveAction(Thing.GHOST, true)).setFitness(18));

		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 50)).add(new DistanceCondition(Thing.POWER_PILL, 0, 50)).setAction(new MoveAction(Thing.POWER_PILL, true)).setFitness(18));
	}

	public static void debug(final String s) {
		System.out.println(s);
	}

	@Override
	public int[] getActions(final Game game, final long timeDue) {
		if(game.gameOver()) {
			System.out.println("YEAH");
			return null; // muhaha gibt nullpointer :)
		}

		final int response[] = new int [Game.NUM_GHOSTS];

		RuleFunctions.prepareNextRound(game);

		ghostLoop:
		for (int ghostId = 0; ghostId < Game.NUM_GHOSTS; ghostId++) {
			if(!game.ghostRequiresAction(ghostId)) {
				continue ghostLoop;
			}

			//System.out.println("\nBerechne Geist " + ghostId);

			final MoveRecommendation move = new MoveRecommendation();
			for (final Rule rule : ruleSet) {
				if(rule.matchForGhost(game, ghostId)) {
					move.addFitness(rule.generateMove(game, ghostId));
				}
			}

			final Direction dir = move.getRouletteFitness(game.getCurGhostLoc(ghostId));
			//debug("Geist " + ghostId + " :: " + move + " -> laufe nach: " + dir);

			response[ghostId] = dir.toInt();
		}
		return response;
	}



	public void trainingBegin(final int totalTrainings) {

		System.out.println("Regeln beim Start:");
		for (final Rule rule : ruleSet) {
			System.out.println(rule);
		}
	}

	int lastAvgScore = -1;
	int trainingScore = 0;
	public void trainingRoundOver(final int round, final int totalTrainings, final Game game) {
		trainingScore += game.getScore();

		//System.out.println("Runde " + round );
//		System.out.println("\n\nRegeln nach Runde " + round + ":");
		for (final Rule rule : ruleSet) {
//			System.out.println(rule);
		}

		// abzug für tot! nur kleiner abzug da diese regel evtl gar nichts dafür kann
		// lastActionSet.doRewardStuff(-1.0f);


		// for the moment only 10 rounds per training. this is WAY too few but it's so damn slow :(
		final int GAMES_PER_TRAINING = 200;

		if((round+1) % GAMES_PER_TRAINING == 0) {
			trainingScore /= GAMES_PER_TRAINING;

			System.out.println("\n\n");

			if(lastAvgScore >= 0) {
				// TODO evtl erst berücksichtigen wenn änderung > 1%
				if(lastAvgScore < trainingScore) {
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
			System.out.println("Avg score after " + GAMES_PER_TRAINING + " rounds: " + trainingScore);
			System.out.println("Training " + round * 100.0f / totalTrainings + "% complete");
			System.out.println("");
			FitnessSave.mutate();

			trainingScore = 0;
		}
	}

	public void trainingOver(final int trainings) {
		FitnessSave.dump();

		System.out.println("\n\nRegeln am Ende:");
		for (final Rule rule : ruleSet) {
			System.out.println(rule);
		}
	}
}