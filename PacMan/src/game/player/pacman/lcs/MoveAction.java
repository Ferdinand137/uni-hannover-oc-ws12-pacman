package game.player.pacman.lcs;

import game.core.Game;

public class MoveAction {
	Thing thing;
	boolean moveAway;
	
	/**
	 * move towards thing
	 */
	public MoveAction(Thing thing) {
		this.thing = thing;
		this.moveAway = false;
	}
	
	
	public MoveAction(Thing thing, boolean moveAway) {
		this.thing = thing;
		this.moveAway = moveAway;
	}


	MoveRecommendation getDirection(Game game) {
		int direction;
		
		switch (thing) {
		case GHOST:
			direction = RuleFunctions.getNextGhostDirection();
			break;
		case PILL:
			direction = RuleFunctions.getNextPillDirection();
			break;
		case POWER_PILL:
			direction = RuleFunctions.getNextPowerPillDirection();
			break;
		default:
			throw new RuntimeException("this never ever happens... but there is an uninitialized direction error without this");
		}
		
		/*
		if(moveAway) {
			direction = game.getReverse(direction);
		
			if(game.getNeighbour(game.getCurPacManDir(), direction) == -1) {
				// so n pech aber auch, in die richtung gehts nicht weiter!
			}
		}
		*/
		
		return new MoveRecommendation(direction, moveAway ? -1 : 1);
	}
}
