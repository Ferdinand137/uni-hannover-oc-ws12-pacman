package game.player.ghost.lcs;

import game.core.Game;
import game.player.pacman.lcs.Direction;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.EdibleCondition;
import game.player.pacman.lcs.MoveAction;
import game.player.pacman.lcs.MoveRecommendation;
import game.player.pacman.lcs.Rule;
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
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.PACMAN, 0, 50)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.PACMAN)));
	}

	public static void debug(final String s) {
		System.out.println(s);
	}

	@Override
	public int[] getActions(final Game game, final long timeDue) {
		final int response[] = new int [Game.NUM_GHOSTS];

		ghostLoop:
		for (int ghostId = 0; ghostId < Game.NUM_GHOSTS; ghostId++) {
			if(!game.ghostRequiresAction(ghostId)) {
				continue ghostLoop;
			}


			final MoveRecommendation move = new MoveRecommendation();
			for (final Rule rule : ruleSet) {
				if(rule.matchForPacMan(game)) {
					move.addFitness(rule.generateMove(game));
				}
			}

			final Direction dir = move.getRouletteFitness();
			debug("Geist " + ghostId + " :: " + move + " -> laufe nach: " + dir);

			response[ghostId] = dir.toInt();
		}
		return response;
	}
}