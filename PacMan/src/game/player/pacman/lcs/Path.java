package game.player.pacman.lcs;

import game.core.Game;

public class Path {
	public static final Path ENDLESS = new Path(null) {
		@Override
		public int length() { return Integer.MAX_VALUE; }
		@Override
		public Direction getStartDirection(final Game game) { throw new RuntimeException("TODO"); }
		@Override
		public boolean goesOver(final int pos) { return false; } // konsequenterweise eigtl true, aber false macht mehr sinn
		@Override
		public String toString() { return "ENDLESS path"; }
	};

	final int path[];

	public Path(final int path[]) {
		this.path = path;
	}

	public int length() {
		assert path != null;

		return path.length;
	}

	public Direction getStartDirection(final Game game) {
		assert path != null;
		if(path.length < 2) return Direction.INVALID;

		for (final Direction direction : Direction.iter()) {
			if (game.getNeighbour(path[0], direction.toInt()) == path[1])
				return direction;
		}

		throw new RuntimeException("Weg führt nicht über Nachbarzelle");
	}

	@Override
	public String toString() {
		assert path != null;

		String ausgabe = length() + ": ";
		for (final int i : path) {
			ausgabe += i + ", ";
		}
		return ausgabe;
	}

	public boolean goesOver(final int pos) {
		assert path != null;

		for (final int i : path) {
			if(i == pos) return true;
		}
		return false;
	}

}
