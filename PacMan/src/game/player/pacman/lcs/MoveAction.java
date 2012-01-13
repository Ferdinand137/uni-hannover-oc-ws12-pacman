package game.player.pacman.lcs;

import game.core.Game;

public class MoveAction {
	Thing thing;
	
	/**
	 * move towards thing
	 */
	public MoveAction(Thing thing) {
		this.thing = thing;
	}


	int getDirection(Game game) {
		switch (thing) {
		case GHOST:
			return RuleFunctions.getNextGhostDirection();
		case PILL:
			return RuleFunctions.getNextPillDirection();
		case POWER_PILL:
			return RuleFunctions.getNextPowerPillDirection();
		case JUNCTION:
			for (Path path : RuleFunctions.getAllGhostPaths().getIterable()) {
				for(int posInPath = 0; posInPath < path.length(); posInPath++) {
					if(game.isJunction(path.path[posInPath])) {
						// found junction!
						if(posInPath < path.length()) {
							// PacMAn kommt vorm Geist an!
							// TODO berücksichtigen dass geist sich nicht drehen kann
							// route vom geist aus planen?
							return RuleFunctions.getDirectionOfPath(path);
						}
						
						// next ghost
						break;
					}
				}
			}
			
			System.out.println("alles verloren! kein ausweg");
			return 0; // gibt keine sichere richtung, alles egal
		default:
			throw new RuntimeException("this never ever happens... but there is an uninitialized direction error without this");
		}
	}
}
