package game.player.pacman.lcs;

import game.core.Game;

public class Path {
	final int path[];
	
	public Path(int path[]) {
		this.path = path;
	}
	
	public int length() {
		assert path != null;

		return path.length;
	}
	
	public Direction getStartDirection(Game game) {
		assert path != null;
		assert path.length > 1;

		for (Direction direction : Direction.values()) {
			if (game.getNeighbour(path[0], direction.toInt()) == path[1])
				return direction;
		}
		
		throw new RuntimeException("Weg führt nicht über Nachbarzelle");
	}

	public String toString() {
		assert path != null;

		String ausgabe = length() + ": ";
		for (int i : path) {
			ausgabe += i + ", ";
		}
		return ausgabe;
	}

	public boolean goesOver(int pos) {
		assert path != null;

		for (int i : path) {
			if(i == pos) return true;
		}
		return false;
	}

}
