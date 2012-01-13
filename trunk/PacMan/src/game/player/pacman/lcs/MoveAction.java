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
				System.out.println(Direction.createFromInt(direction) + " <- " + path.length() + " ::: " + path);
			}
			return moveRecommendation;
			
		case PILL:
			moveRecommendation.fitness[RuleFunctions.getNextPillDirection()] = fitness;
			return moveRecommendation;
			
		case POWER_PILL:
			moveRecommendation.fitness[RuleFunctions.getNextPowerPillDirection()] = fitness;
			return moveRecommendation;
			
		case JUNCTION:

			int[] junctions = getPacManNeighboringJunctions(game);

			for (Direction direction : Direction.values()) {

				// kürzesten pfad von geist zu junction finden
				int minGhostDist = Integer.MAX_VALUE;
				for(int ghostId = 0; ghostId < G.NUM_GHOSTS; ghostId++) {
					if(game.getLairTime(ghostId) > 0) continue;
					
					int dist = game.getGhostPathDistance(ghostId, junctions[direction.toInt()]);
					if(dist < minGhostDist)
						minGhostDist = dist;
				}
				
				int minPacManDist = game.getPath(RuleFunctions.currentLocation, junctions[direction.toInt()]).length;
				
				moveRecommendation.fitness[direction.toInt()] = (float)minGhostDist / (float)minPacManDist * fitness;
			}

			return moveRecommendation;
			
		default:
			throw new RuntimeException("this never ever happens... but there is an uninitialized direction error without this");
		}
		
		
	}


	private int[] getPacManNeighboringJunctions(Game game) {
		int[] junctions = new int[4];
		
		for (Direction startDirection : Direction.values()) {
			int pos = game.getPacManNeighbours()[startDirection.toInt()];
			// nicht gegen die Wand planen!
			if(pos == -1) {
				System.out.println("wand: " + startDirection);
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
		
		return junctions;
	}
}
