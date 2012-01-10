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
		// FIXME
		return Game.UP;
	}
}
