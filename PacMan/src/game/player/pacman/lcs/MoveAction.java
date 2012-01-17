package game.player.pacman.lcs;

import game.core.G;
import game.core.Game;
import game.core.GameView;

import java.awt.Color;
import java.util.Vector;

public class MoveAction {
	Thing thing;
	boolean moveAway = false;

	/**
	 * move towards thing
	 */
	public MoveAction(final Thing thing) {
		this.thing = thing;
	}

	public MoveAction(final Thing thing, final boolean moveAway) {
		this.thing = thing;
		this.moveAway = moveAway;
	}

	MoveRecommendation getGhostMove(final Game game, final float fitness, final int whichGhost) {
		final MoveRecommendation move = new MoveRecommendation();

		switch (thing) {
		case PACMAN:
			System.out.println("------------");
			if(moveAway) {
				final Path path = RuleFunctions.getPath(game.getCurGhostLoc(whichGhost), game.getCurPacManLoc());
				GameView.addPoints(game, Color.RED, path.path);
				move.addFitness(path.getStartDirection(game), fitness, true);
			} else {
				final Path path = RuleFunctions.getGhostPath(whichGhost, game.getCurPacManLoc());
				GameView.addPoints(game, Color.GREEN, path.path);
				move.addFitness(path.getStartDirection(game), fitness, false);
			}
		case POWER_PILL:
			move.addFitness(RuleFunctions.getNextPowerPillDirection(whichGhost), fitness, moveAway);
			break;

		default:
			throw new RuntimeException();
		}

		return move;
	}

	MoveRecommendation getMove(final Game game, final float fitness) {
		final MoveRecommendation move = new MoveRecommendation();

		switch (thing) {
		case GHOST:
			// FIXME nicht nur kürzesten pfad betrachten.
			// zB gibts manchmal 2 pfade die exakt gleich lang sind
			for (final Path path : RuleFunctions.getAllGhostPaths().getIterable()) {
				GameView.addPoints(game, Color.CYAN, path.path);

				// geister weit weg ignorieren
				// TODO 60... tjoa... hab ich ma so reingetan :)
				if(path.length() > 60) {
					continue;
				}

				final Direction direction = RuleFunctions.getDirectionOfPath(path);

				move.addFitness(direction, fitness, moveAway);

				//moveRecommendation.fitness[direction.toInt()] = fitness; //1.0f / path.length() * fitness;
				//System.out.println(Direction.createFromInt(direction) + " <- " + path.length() + " ::: " + path);
			}
			break;

		case PILL:
			move.addFitness(RuleFunctions.getNextPillDirection(), fitness, moveAway);
			break;

		case POWER_PILL:
			move.addFitness(RuleFunctions.getNextPowerPillDirection(), fitness, moveAway);
			break;

		case TURN_BACK:
			move.addFitness(Direction.createFromInt(game.getReverse(game.getCurPacManDir())), fitness, moveAway);
			break;

		case JUNCTION:
			// FIXME isEdible berücksichtigen... zu edible geistern (die genug zeit noch haben) darf pacman ruhig hin

			final Vector<Integer> junctions = getPacManNeighboringJunctions2(game);

			junctionLoop:
			for (final Integer junction : junctions) {

				final Path pacManPath = RuleFunctions.getPath(RuleFunctions.currentLocation, junction);

				// TODO in eigene funktion auslagern
				for (final Integer j : junctions) {
					if(junction == j)
					 {
						continue; // endpoint => goesOver = true
					}

					if(pacManPath.goesOver(j)) {
						continue junctionLoop;
					}
				}

				// System.out.println(RuleFunctions.currentLocation + " -> " + junction + "; path: " + pacManPath);


				// kürzesten pfad von geist zu junction finden
				// TODO in RuleFunctions.getShortestGhostPath verschieben
				int minGhostDist = Integer.MAX_VALUE;
				for(int ghostId = 0; ghostId < G.NUM_GHOSTS; ghostId++) {
					if(game.getLairTime(ghostId) > 0) {
						continue;
					}

					if(game.isEdible(ghostId) && game.getEdibleTime(ghostId) > 5) {
						// FIXME diese junctions verdienen eine positive bewertung! passiert das?
						continue;
					}

					final int dist = game.getGhostPathDistance(ghostId, junction);
					if(dist < minGhostDist) {
						minGhostDist = dist;
					}
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

				final Direction direction = pacManPath.getStartDirection(game);
				if(minGhostDist < pacManPath.length()) {
					// geist ist näher an junction
					//System.out.println("bad");
					//moveRecommendation.fitness[direction] -= fitness;
					//move.addFitness(direction, fitness, true);
					//System.out.println("junction " + direction + " ist der geist näher. werte andere auf");
				} else {
					// pacman ist näher
					//System.out.println("good");
					//moveRecommendation.fitnessArr[direction] =+ fitness;
					move.addFitness(direction, fitness, false);
					// System.out.println("junction " + direction + " ist pacman näher. werte auf. " + minGhostDist + " vs " + pacManPath.length());
				}

				GameView.addLines(game, minGhostDist < pacManPath.length() ? Color.RED : Color.GREEN, RuleFunctions.currentLocation, junction);

			}
			break;

		default:
			throw new RuntimeException("this never ever happens... but there is an uninitialized direction error without this");
		}

		return move;
	}


	private Vector<Integer> getPacManNeighboringJunctions2(final Game game) {
		final Vector<Integer> junctions = new Vector<Integer>();

		for (final int junction : game.getJunctionIndices()) {
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
	private int[] getPacManNeighboringJunctions(final Game game) {
		final int[] junctions = new int[4];

		for (final Direction startDirection : Direction.values()) {
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
					for (final Direction newDirection : Direction.values()) {
						newPos = game.getNeighbour(pos, newDirection.toInt());
						if(newPos != pos && newPos >= 0) {
							// ausweg gefunden
							System.out.println("biege ab: " + currentDirection + " -> " + newDirection);
							currentDirection = newDirection;
							break;
						}
					}
				}

				if(game.isJunction(newPos)) {
					break;
				}

				pos = newPos;
			}
			// pos is junction

			junctions[startDirection.toInt()] = pos;
		}

		System.out.println(junctions[0] + " - " + junctions[2] + " - " + junctions[2] + " - " + junctions[3]);
		return junctions;
	}

	String toId() {
		return "#MA:" + thing.toId() + ":" + (moveAway ? 'A' : 'T') + "#";
	}

	@Override
	public String toString() {
		return "Move " + (moveAway ? "away from " : "towards ") + thing.toString();
	}
}
