package game.player.pacman.lcs;

import game.core.Game;

public class EdibleCondition implements Condition {
	
	boolean edible;
	
	public EdibleCondition(boolean edible) {
		this.edible = edible;
	}

	@Override
	public boolean match(Game game) {
		int nearestGhost = RuleFunctions.getNextGhostId();
		System.out.println(nearestGhost);
		System.out.println(game.isEdible(nearestGhost));
		// TODO Auto-generated method stub
		return edible == game.isEdible(nearestGhost);
	}

}
