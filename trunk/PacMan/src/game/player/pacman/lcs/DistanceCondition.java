package game.player.pacman.lcs;

import game.core.G;
import game.core.Game;

public class DistanceCondition implements Condition {
	float min, max;
	Thing thing;

	public DistanceCondition(final Thing thing, final float min, final float max) {
		this.thing = thing;
		this.min = min;
		this.max = max;

		assert min < max;
	}

	@Override
	public boolean matchForPacMan(final Game game) {
		float dist = -1;

		switch (thing) {
		case GHOST:
			dist = RuleFunctions.getNextGhostDistance();
			break;

		case PILL:
			dist = RuleFunctions.getNextPillDistance();
			break;

		case POWER_PILL:
			dist = RuleFunctions.getNextPowerPillDistance();
			break;

		default:
			throw new RuntimeException("fall fehlt");
		}

		return min <= dist && dist <= max;
	}

	@Override
	public String toId() {
		return "#DC:" + thing.toId() + ':' + min + ':' + max + '#';
	}

	@Override
	public String toString() {
		return thing + " between " + min + " and " + max;
	}

	@Override
	public boolean matchForGhost(final Game game, final int whichGhost) {
		float dist = -1;
		switch (thing) {
		case PACMAN:
			dist = game.getPathDistance(game.getCurPacManLoc(), game.getCurGhostLoc(whichGhost));
			break;
		case GHOST:
			dist = Integer.MAX_VALUE;

			for (int i = 0; i < G.NUM_GHOSTS; i++) {
				if (game.getLairTime(i) > 0 || i == whichGhost)
				{
					continue; // ignore erstmal
				}

				final int distGhostToGhost = game.getPathDistance(game.getCurGhostLoc(whichGhost), game.getCurGhostLoc(i));

				if(distGhostToGhost < dist) {
					dist = distGhostToGhost;
				}
			}

			// System.out.println("next ghost: " + dist);
			break;
		case POWER_PILL:
			final int curLoc = game.getCurGhostLoc(whichGhost);
			final int nearest = game.getTarget(curLoc, game.getPowerPillIndicesActive(), true, Game.DM.PATH);
			dist = game.getPathDistance(curLoc, nearest);
			break;

		default:
			throw new RuntimeException("fall fehlt");
		}
		return min <= dist && dist <= max;
	}
}
