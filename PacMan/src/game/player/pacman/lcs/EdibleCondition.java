package game.player.pacman.lcs;

import game.core.Game;

public class EdibleCondition implements Condition {
	
	boolean edible;
	
	public EdibleCondition(boolean edible) {
		this.edible = edible;
	}

	@Override
	public boolean match(Game game) {
		// TODO Auto-generated method stub
		return false;
	}

}
