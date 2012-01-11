package game.player.pacman.lcs;

import game.core.G;
import game.core.Game;

public class RuleFunctions {

	static int currentLocation;
	static SetOfPaths pathsToGhosts;
	static SetOfPaths pathsToPill;
	static SetOfPaths pathsToPowerPill;
	static int[] closestGhost;
	static int[] closestPill;
	static int[] closestPowerPill;
	static int distancePill;
	static Game game;

	private RuleFunctions() {
	}

	public static void prepareNextRound(Game game) {
		RuleFunctions.game = game;

		currentLocation = game.getCurPacManLoc();

		pathsToGhosts = new SetOfPaths();
		pathsToPill = new SetOfPaths();
		pathsToPowerPill = new SetOfPaths();

		for (int i = 0; i < G.NUM_GHOSTS; i++) {
			if (game.getLairTime(i) > 0)
				continue; // ignore erstmal
			pathsToGhosts.add(game.getPath(currentLocation,
					game.getCurGhostLoc(i)));
		}

		closestGhost = pathsToGhosts.isEmpty() ? null : pathsToGhosts
				.getShortest();

		// alle aktiven Pillen holen
		int[] activePills = game.getPillIndicesActive();
		
		// alle aktiven PowerPillen holen
		int[] activePowerPills = game.getPowerPillIndicesActive();

		// Alle aktiven Pillen als Pfad eintragen
		for (int i = 0; i < activePills.length; i++) {
			pathsToPill.add(game.getPath(currentLocation, activePills[i]));
		}

		closestPill = pathsToPill.isEmpty() ? null : pathsToPill
				.getShortest();
		
		// Alle aktiven Pillen als Pfad eintragen
		for (int i = 0; i < activePowerPills.length; i++) {
			pathsToPowerPill.add(game.getPath(currentLocation, activePowerPills[i]));
		}

		closestPowerPill = pathsToPowerPill.isEmpty() ? null : pathsToPowerPill
				.getShortest();
	}

	public static int getNextGhostDistance() {
		if (closestGhost == null)
			return Integer.MAX_VALUE;
		return closestGhost.length;
	}

	// FIXME Tut das Ding hier was es soll?
	public static int getNextPillDistance() {
		System.out.println("closest Pill " + closestPill);
		System.out.println("closest Pill.length " + closestPill.length);
		return closestPill.length;
	}

	// FIXME Tut das Ding hier was es soll?
	public static int getNextPowerPillDistance() {
		System.out.println("closest PowerPill " + closestPowerPill);
		System.out.println("closest PowerPill.length " + closestPowerPill.length);
		return closestPowerPill.length;
	}

	public static int getNextGhostDirection() {
		// FIXME Achtung völlig ungetestet! 0 plan obs stimmt
		assert closestGhost != null;

		for (int i = 0; i < 4; i++)
			if (game.getNeighbour(currentLocation, i) == closestGhost[1])
				return i;

		throw new RuntimeException(
				"Weg zum Geist führt nicht über Nachbarzelle");
	}

	public static int getNextPillDirection() {
		assert closestPill != null;
		return closestPill[1]; // TODO fix wie getNextGhostDirection
	}

	public static int getNextPowerPillDirection() {
		assert closestPowerPill != null;
		return closestPowerPill[1]; // TODO fix wie getNextGhostDirection
	}
}
