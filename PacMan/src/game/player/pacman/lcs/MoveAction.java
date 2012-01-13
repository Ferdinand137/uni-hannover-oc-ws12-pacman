package game.player.pacman.lcs;

import game.core.Game;
import game.core.GameView;

import java.awt.Color;

public class MoveAction {
	Thing thing;
	
	/**
	 * move towards thing
	 */
	public MoveAction(Thing thing) {
		this.thing = thing;
	}


	MoveRecommendation getDirection(Game game, float fitness) {
		MoveRecommendation moveRecommendation = new MoveRecommendation();
		
		switch (thing) {
		case GHOST:
			for (Path path : RuleFunctions.getAllGhostPaths().getIterable()) {
				GameView.addPoints(game, Color.CYAN, path.path);
				
				int direction = RuleFunctions.getDirectionOfPath(path);
				moveRecommendation.fitness[direction] = fitness; //1.0f / path.length() * fitness;
				System.out.println(direction + " <- " + path.length() + " ::: " + path);
			}
			return moveRecommendation;
			
		case PILL:
			moveRecommendation.fitness[RuleFunctions.getNextPillDirection()] = fitness;
			return moveRecommendation;
			
		case POWER_PILL:
			moveRecommendation.fitness[RuleFunctions.getNextPowerPillDirection()] = fitness;
			return moveRecommendation;
			
		case JUNCTION:
			// TODO junctions berücksichtigen die nicht aufm weg zu einem geist liegen
			for (Path path : RuleFunctions.getAllGhostPaths().getIterable()) {
				for(int posInPath = 1; posInPath < path.length() / 2; posInPath++) {
					if(game.isJunction(path.path[posInPath])) {
						// found junction!
						// PacMAn kommt vorm Geist an!
						// TODO berücksichtigen dass geist sich nicht drehen kann
						// route vom geist aus planen?
						moveRecommendation.fitness[RuleFunctions.getDirectionOfPath(path)] = 1.0f / posInPath * fitness;
						break; // next ghost
					}
				}
			}
			
			return moveRecommendation;
			
		default:
			throw new RuntimeException("this never ever happens... but there is an uninitialized direction error without this");
		}
		
		
	}
}
