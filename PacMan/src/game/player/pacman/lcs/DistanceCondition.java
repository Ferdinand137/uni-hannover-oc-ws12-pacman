package game.player.pacman.lcs;

import game.core.Game;

public class DistanceCondition implements Condition {
	float min, max;		
	Thing thing;

	public DistanceCondition(Thing thing, float min, float max) {
		this.thing = thing;
		this.min = min;
		this.max = max;
		
		assert min < max;
	}
	
	public boolean match(Game game) {
		float dist = -1;
		
		switch (thing) {
		case GHOST:
			dist = RuleFunctions.getNextGhostDistance(game);
			break;

		case PILL:
			dist = RuleFunctions.getNextPillDistance(game);
			break;
			
		case POWER_PILL:
			dist = RuleFunctions.getNextPowerPillDistance(game);
			break;
		}
		
		return min <= dist && dist <= max;
	}
}
