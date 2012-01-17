package game.player.pacman.lcs;

import game.core.G;
import game.core.Game;

public class RuleFunctions {

	public static int currentLocation;
	static SetOfPaths pathsToGhosts;
	static PathToGhost closestGhost;
	static Path closestPill;
	static Path closestPowerPill;
	static int distancePill;
	static Game game;

	private RuleFunctions() {}

	public static void prepareNextRound(final Game game) {
		RuleFunctions.game = game;

		currentLocation = game.getCurPacManLoc();

		pathsToGhosts = new SetOfPaths();

		for (int i = 0; i < G.NUM_GHOSTS; i++) {
			if (game.getLairTime(i) > 0)
			 {
				continue; // ignore erstmal
			}
			pathsToGhosts.add(new PathToGhost(i, getPath(currentLocation, game.getCurGhostLoc(i))));
		}

		closestGhost = (PathToGhost) (pathsToGhosts.isEmpty() ? null : pathsToGhosts.getShortest());

		// ACHTUNG: manhattan ist NICHT schneller! PATH distances sind eine direkte Formell!
		closestPill = getPath(currentLocation, game.getTarget(currentLocation, game.getPillIndicesActive(), true, Game.DM.PATH));
		closestPowerPill = getPath(currentLocation, game.getTarget(currentLocation, game.getPowerPillIndicesActive(), true, Game.DM.PATH));
	}

	/**
	 * game.getPath() tut leider nicht das was es behauptet, der Zielpunkt fehlt!
	 */
	public static Path getPath(final int from, final int to) {
		if(from == -1 || to == -1) return Path.ENDLESS;

		final int path[] = game.getPath(from, to);
		final int fixed[] = new int[path.length+1];
		System.arraycopy(path, 0, fixed, 0, path.length);
		fixed[path.length] = to;
		return new Path(fixed);
	}

	/**
	 * game.getPath() tut leider nicht das was es behauptet, der Zielpunkt fehlt!
	 */
	public static Path getGhostPath(final int whichGhost, final int to) {
		if(whichGhost == -1 || to == -1) return Path.ENDLESS;

		final int path[] = game.getGhostPath(whichGhost, to);
		final int fixed[] = new int[path.length+1];
		System.arraycopy(path, 0, fixed, 0, path.length);
		fixed[path.length] = to;
		return new Path(fixed);
	}

	public static int getNextGhostId() {
		return closestGhost.ghostId;
	}

	public static int getNextGhostDistance() {
		return getLengthOfPath(closestGhost);
	}

	public static int getNextPillDistance() {
		return getLengthOfPath(closestPill);
	}

	public static int getNextPowerPillDistance() {
		return getLengthOfPath(closestPowerPill);
	}

	/**
	 * to avoid null pointer. TODO: better Path.endlessPath instead of null
	 */
	private static int getLengthOfPath(final Path path) {
		if(path == null)
			return Integer.MAX_VALUE;
		return path.length();
	}

	public static Direction getDirectionOfPath(final Path path) {
		// FIXME Achtung völlig ungetestet! 0 plan obs stimmt
		assert path != null;

		// FIXME achtung gilt nur für pacman
		if(path.length() == 1 || path == Path.ENDLESS)
			return Direction.createFromInt(game.getCurPacManDir());

		for (final Direction direction : Direction.values()) {
			if (game.getNeighbour(currentLocation, direction.toInt()) == path.path[1])
				return direction;
		}

		throw new RuntimeException("Weg zum führt nicht über Nachbarzelle");
	}

	public static Direction getNextGhostDirection() {
		return getDirectionOfPath(closestGhost);
	}

	public static Direction getNextPillDirection() {
		return getDirectionOfPath(closestPill);
	}

	public static Direction getNextPowerPillDirection() {
		return getDirectionOfPath(closestPowerPill);
	}

	public static SetOfPaths getAllGhostPaths() {
		return pathsToGhosts;
	}
}
