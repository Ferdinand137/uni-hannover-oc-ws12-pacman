package game.player.pacman.lcs;

import java.util.Vector;

public class SetOfPaths {
	private Vector<Path> paths = new Vector<Path>();

	void add(Path path) {
		paths.add(path);
	}

	public Path getShortest() {
		if (paths.size() == 0)
			throw new NullPointerException(
					"Ej du Opfer, es sind keine Pfade im Set!");

		Path shortest = null;

		for (Path path : paths) {
			if (shortest == null || shortest.length() > path.length())
				shortest = path;
		}

		assert shortest != null;
		return shortest;
	}

	public boolean isEmpty() {
		return paths.isEmpty();
	}

	public String toString() {
		String ausgabe = "";
		for (Path wert : paths) {
			ausgabe += wert.length() + ": ";
			for (int i : wert.path) {
				ausgabe += i + ", ";
			}
			ausgabe += "\n";
		}
		return ausgabe;
	}

	public Iterable<Path> getIterable() {
		return paths;
	}
}
