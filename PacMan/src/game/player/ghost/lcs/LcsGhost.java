package game.player.ghost.lcs;

import game.core.Game;
import game.player.pacman.lcs.Direction;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.EdibleCondition;
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
	static Vector<Rule> ruleSet = new Vector<Rule>();

	@Override
	public String getGhostGroupName() {
		return "Gruppe2_Geist";
	}

	static {
		// "default" geh richtung pacman
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.POWER_PILL)).setFitness(0.1f));
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.PILL)).setFitness(0.1f));

		// pacman nahe + !essbar => gehe hin
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 150)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.PACMAN)).setFitness(15));

		// pacman nahe + essbar => gehe weg
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 100)).add(new EdibleCondition(true )).setAction(new MoveAction(Thing.PACMAN, true)).setFitness(17));

		// anderer geist sehr nahe => gehe weg
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 50)).add(new DistanceCondition(Thing.PACMAN, 50, 10000)).setAction(new MoveAction(Thing.GHOST, true)));

		// anderer geist sehr nahe => gehe weg
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 30)).add(new DistanceCondition(Thing.PACMAN, 0, 30)).add(new EdibleCondition(true)).setAction(new MoveAction(Thing.GHOST, true)));
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

			System.out.println("\nBerechne Geist " + ghostId);

			final MoveRecommendation move = new MoveRecommendation();
			for (final Rule rule : ruleSet) {
				if(rule.matchForGhost(game, ghostId)) {
					move.addFitness(rule.generateMove(game, ghostId));
				}
			}

			final Direction dir = move.getRouletteFitness(game.getCurGhostLoc(ghostId));
			debug("Geist " + ghostId + " :: " + move + " -> laufe nach: " + dir);

			response[ghostId] = dir.toInt();
		}
		return response;
	}
}