package game.player.pacman.lcs;

import game.core.Game;

/**
 * TODO Wenn 2 Geister in der nähe sind würde aktuell Edible true zurückgeben wenn der eine essbar ist... kann böse enden
 */
// Also alle Geister durchgehen und wenn in einer bestimmten Distanz z.b. 2 Geister sind, einer essbar der andere nicht, dann flüchten
public class EdibleCondition implements Condition {

	final boolean edible;

	public EdibleCondition(final boolean edible) {
		this.edible = edible;
	}

	@Override
	public boolean match(final Game game) {
		final int nearestGhost = RuleFunctions.getNextGhostId();
		return edible == game.isEdible(nearestGhost);
	}

	@Override
	public String toId() {
		return "#EC:" + (edible ? 'T' : 'F') + '#';
	}

	@Override
	public String toString() {
		return "edible is " + edible;
	}
}
