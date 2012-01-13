package game.player.pacman.lcs;

import game.core.Game;

public class EdibleCondition implements Condition {
	
	final boolean edible;
	
	public EdibleCondition(boolean edible) {
		this.edible = edible;
	}

	@Override
	public boolean match(Game game) {
		final int nearestGhost = RuleFunctions.getNextGhostId();
		return edible == game.isEdible(nearestGhost);
	}

	@Override
	public String toId() {
		// TODO Auto-generated method stub
		return "#EC:" + (edible ? 'T' : 'F') + '#';
	}
}
