package game.player.pacman.lcs;

import game.core.Game;

/**
 * TODO Wenn 2 Geister in der nähe sind würde aktuell Edible true zurückgeben wenn der eine essbar ist... kann böse enden
 */
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
		return "#EC:" + (edible ? 'T' : 'F') + '#';
	}
}
