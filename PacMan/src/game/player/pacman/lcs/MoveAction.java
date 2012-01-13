package game.player.pacman.lcs;

import game.core.G;
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
			for(int startDirection = 0; startDirection < 4; startDirection++) {
				
				int pos = game.getPacManNeighbours()[startDirection];
				// nicht gegen die Wand planen!
				if(pos == -1) {
					System.out.println("wand: " + startDirection);
					continue;
				}
				
				int currentDirection = startDirection;

				while(true) {
					int newPos = game.getNeighbour(pos, currentDirection);
					
					if(newPos < 0) {
						// hups da ist ne wand. abbiegen!
						for(int newDirection = 0; newDirection < 4; newDirection++) {
							newPos = game.getNeighbour(pos, newDirection);
							if(newPos != pos && newPos >= 0) {
								// ausweg gefunden
								System.out.println("biege ab: " + currentDirection + " -> " + newDirection);
								currentDirection = newDirection;
								break;
							}
						}
					}
					
					if(game.isJunction(newPos))
						break;
					
					pos = newPos;
				}
				// pos is junction

				// kürzesten pfad von geist zu junction finden
				int minGhostDist = Integer.MAX_VALUE;
				for(int ghostId = 0; ghostId < G.NUM_GHOSTS; ghostId++) {
					if(game.getLairTime(ghostId) > 0) continue;
					
					int dist = game.getGhostPathDistance(ghostId, pos);
					if(dist < minGhostDist)
						minGhostDist = dist;
				}
				
				int minPacManDist = game.getPath(RuleFunctions.currentLocation, pos).length;
				
				moveRecommendation.fitness[startDirection] = (float)minGhostDist / (float)minPacManDist * fitness;
			}
			/*
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
			*/
			return moveRecommendation;
			
		default:
			throw new RuntimeException("this never ever happens... but there is an uninitialized direction error without this");
		}
		
		
	}
}
