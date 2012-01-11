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


	int getDirection(Game game) {
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
		
		if(moveAway)
			direction = game.getReverse(direction);
		
		return direction;
	}
}
