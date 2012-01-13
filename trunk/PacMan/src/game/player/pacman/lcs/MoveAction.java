package game.player.pacman.lcs;

import game.core.G;
import game.core.Game;
import game.core.GameView;

import java.awt.Color;
import java.util.Vector;

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
				//System.out.println(Direction.createFromInt(direction) + " <- " + path.length() + " ::: " + path);
			}
			return moveRecommendation;
			
		case PILL:
			moveRecommendation.fitness[RuleFunctions.getNextPillDirection()] = fitness;
			return moveRecommendation;
			
		case POWER_PILL:
			moveRecommendation.fitness[RuleFunctions.getNextPowerPillDirection()] = fitness;
			return moveRecommendation;
			
		case JUNCTION:
			// FIXME isEdible berücksichtigen... zu edible geistern (die genug zeit noch haben) darf pacman ruhig hin
			
			Vector<Integer> junctions = getPacManNeighboringJunctions2(game);
			
			junctionLoop:
			for (Integer junction : junctions) {
				
				Path pacManPath = RuleFunctions.getPath(RuleFunctions.currentLocation, junction);
				
				for (Integer j : junctions) {
					if(junction == j) continue; // endpoint => goesOver = true

					if(pacManPath.goesOver(j)) {
						continue junctionLoop;
					}
				}
				
				// System.out.println(RuleFunctions.currentLocation + " -> " + junction + "; path: " + pacManPath);
				

				
				// kürzesten pfad von geist zu junction finden
				// TODO in RuleFunctions.getShortestGhostPath verschieben
				int minGhostDist = Integer.MAX_VALUE;
				for(int ghostId = 0; ghostId < G.NUM_GHOSTS; ghostId++) {
					if(game.getLairTime(ghostId) > 0) continue;
					
					int dist = game.getGhostPathDistance(ghostId, junction);
					if(dist < minGhostDist)
						minGhostDist = dist;
				}
				
				
				if(pacManPath.length() < minGhostDist) {
					// manchmal ist der geist langsamer weil pacman einfach über ihn drüberlaufen will wärend der geist nicht drehen kann
					for(int i = 0; i < G.NUM_GHOSTS; i++) {
						if(pacManPath.goesOver(game.getCurGhostLoc(i))) {
							// durch nen geist laufen ist in der regel ne schlechte idee!
							minGhostDist = 0;
						}
					}
				}
				
				// so n pacman ist ganz schön dick, der hat nen radius! so grob 2 bis 10... ka
				minGhostDist -= 10;
				
				//System.out.print(pacManPath.getStartDirection(game) + ": " + minGhostDist + " / " + pacManPath.length() + " -> ");
				
				int direction = pacManPath.getStartDirection(game).toInt();
				if(minGhostDist < pacManPath.length()) {
					// geist ist näher an junction
					//System.out.println("bad");
					//moveRecommendation.fitness[direction] -= fitness;
					moveRecommendation.fitness[direction] =- fitness;
				} else {
					// pacman ist näher
					//System.out.println("good");
					//moveRecommendation.fitness[direction] += fitness;
					moveRecommendation.fitness[direction] =+ fitness;
				}
				
				GameView.addLines(game, minGhostDist < pacManPath.length() ? Color.RED : Color.GREEN, RuleFunctions.currentLocation, junction);

			}

			return moveRecommendation;
			
		default:
			throw new RuntimeException("this never ever happens... but there is an uninitialized direction error without this");
		}
		
		
	}


	private Vector<Integer> getPacManNeighboringJunctions2(Game game) {
		Vector<Integer> junctions = new Vector<Integer>();
		
		for (int junction : game.getJunctionIndices()) {
			if(RuleFunctions.currentLocation == junction || game.getManhattenDistance(RuleFunctions.currentLocation, junction) > 55) {
				// ignore far away junction
				continue;
			}
			
			junctions.add(new Integer(junction));
		}
		
		return junctions;
	}
	
	@Deprecated
	@SuppressWarnings("unused")
	private int[] getPacManNeighboringJunctions(Game game) {
		int[] junctions = new int[4];
		
		for (Direction startDirection : Direction.values()) {
			int pos = game.getPacManNeighbours()[startDirection.toInt()];
			// nicht gegen die Wand planen!
			if(pos == -1) {
				System.out.println("wand: " + startDirection);
				junctions[startDirection.toInt()] = -1;
				continue;
			}
			
			Direction currentDirection = startDirection;

			while(true) {
				int newPos = game.getNeighbour(pos, currentDirection.toInt());
				
				if(newPos < 0) {
					// hups da ist ne wand. abbiegen!
					for (Direction newDirection : Direction.values()) {
						newPos = game.getNeighbour(pos, newDirection.toInt());
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

			junctions[startDirection.toInt()] = pos;
		}
		
		System.out.println(junctions[0] + " - " + junctions[2] + " - " + junctions[2] + " - " + junctions[3]);
		return junctions;
	}
}
