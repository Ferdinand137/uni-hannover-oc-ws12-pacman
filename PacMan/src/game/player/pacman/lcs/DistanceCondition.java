package game.player.pacman.lcs;

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
		dist = game.getPathDistance(game.getCurPacManLoc(), whichGhost);
		return min <= dist && dist <= max;
	}
}
