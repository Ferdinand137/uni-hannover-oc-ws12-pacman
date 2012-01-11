package game.player.pacman.lcs;

import java.util.Vector;

public class SetOfPaths {
	private Vector<int[]> paths = new Vector<int[]>();

	void add(int[] path) {
		paths.add(path);
	}

	public int[] getShortest() {
		if (paths.size() == 0)
			throw new NullPointerException(
					"Ej du Opfer, es sind keine Pfade im Set!");

		int[] shortest = null;

		for (int[] path : paths) {
			if (shortest == null || shortest.length > path.length)
				shortest = path;
		}

		assert shortest != null;
		return shortest;
	}

	public boolean isEmpty() {
		return paths.isEmpty();
	}

	public String toString() {
		String ausgabe = null;
		for (int[] wert : paths) {
			ausgabe = ausgabe + wert + "\n";
		}
		return ausgabe;
	}
}
