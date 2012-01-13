package game.player.pacman.lcs;

import game.core.G;
import game.core.Game;

public class RuleFunctions {

	static int currentLocation;
	static SetOfPaths pathsToGhosts;
	static SetOfPaths pathsToPill;
	static SetOfPaths pathsToPowerPill;
	static PathToGhost closestGhost;
	static Path closestPill;
	static Path closestPowerPill;
	static int distancePill;
	static Game game;

	private RuleFunctions() {}

	public static void prepareNextRound(Game game) {
		RuleFunctions.game = game;

		currentLocation = game.getCurPacManLoc();

		pathsToGhosts = new SetOfPaths();
		pathsToPill = new SetOfPaths();
		pathsToPowerPill = new SetOfPaths();

		for (int i = 0; i < G.NUM_GHOSTS; i++) {
			if (game.getLairTime(i) > 0)
				continue; // ignore erstmal
			pathsToGhosts.add(new PathToGhost(i, getPath(currentLocation, game.getCurGhostLoc(i))));
		}

		closestGhost = (PathToGhost) (pathsToGhosts.isEmpty() ? null : pathsToGhosts.getShortest());

		// alle aktiven Pillen holen
		int[] activePills = game.getPillIndicesActive();
		
		// alle aktiven PowerPillen holen
		int[] activePowerPills = game.getPowerPillIndicesActive();

		// Alle aktiven Pillen als Pfad eintragen
		for (int i = 0; i < activePills.length; i++) {
			pathsToPill.add(getPath(currentLocation, activePills[i]));
		}

		closestPill = pathsToPill.isEmpty() ? null : pathsToPill.getShortest();
		
		// Alle aktiven Pillen als Pfad eintragen
		for (int i = 0; i < activePowerPills.length; i++) {
			pathsToPowerPill.add(getPath(currentLocation, activePowerPills[i]));
		}

		closestPowerPill = pathsToPowerPill.isEmpty() ? null : pathsToPowerPill.getShortest();
	}

	/**
	 * game.getPath() tut leider nicht das was es behauptet, der Zielpunkt fehlt!
	 */
	public static Path getPath(int from, int to) {
		int path[] = game.getPath(from, to);
		int fixed[] = new int[path.length+1];
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
	private static int getLengthOfPath(Path path) {
		if(path == null)
			return Integer.MAX_VALUE;
		return path.length();
	}
	
	public static int getDirectionOfPath(Path path) {
		// FIXME Achtung völlig ungetestet! 0 plan obs stimmt
		assert path != null;

		// FIXME achtung gilt nur für pacman
		if(path.length() == 1)
			return game.getCurPacManDir();
		
		for (int i = 0; i < 4; i++)
			if (game.getNeighbour(currentLocation, i) == path.path[1])
				return i;

		throw new RuntimeException("Weg zum führt nicht über Nachbarzelle");
	}
	
	public static int getNextGhostDirection() {
		return getDirectionOfPath(closestGhost);
	}

	public static int getNextPillDirection() {
		return getDirectionOfPath(closestPill);
	}

	public static int getNextPowerPillDirection() {
		return getDirectionOfPath(closestPowerPill);
	}
	
	public static SetOfPaths getAllGhostPaths() {
		return pathsToGhosts;
	}
}
