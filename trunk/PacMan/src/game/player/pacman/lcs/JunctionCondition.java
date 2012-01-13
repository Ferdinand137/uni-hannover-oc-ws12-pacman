package game.player.pacman.lcs;

import game.core.Game;

/**
 * This will only check Junctions that are on the way to any ghost!
 */
public class JunctionCondition implements Condition {

	/**
	 * TODO das total ungenau so
	 * @return true if PacMan can reach any junction before the corresponding ghost
	 */
	@Override
	public boolean match(Game game) {
		for (Path path : RuleFunctions.getAllGhostPaths().getIterable()) {
			for(int posInPath = 0; posInPath < path.length(); posInPath++) {
				if(game.isJunction(path.path[posInPath])) {
					// found junction!
					if(posInPath < path.length()) {
						// PacMAn kommt vorm Geist an!
						return true;
					}
					
					// next ghost
					break;
				}
			}
		}
		
		return false;
	}

}
