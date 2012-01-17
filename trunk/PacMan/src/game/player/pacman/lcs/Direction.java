package game.player.pacman.lcs;

public enum Direction {
	UP, RIGHT, LEFT, DOWN;

	public static Direction createFromInt(final int dir) {
		switch (dir) {
		case 0:  return UP;
		case 1:  return RIGHT;
		case 2:  return DOWN;
		case 3:  return LEFT;
		default: throw new RuntimeException("ungültige Richtung " + dir);
		}
	}

	public int toInt() {
		switch(this) {
		case UP:    return 0;
		case RIGHT: return 1;
		case DOWN:  return 2;
		case LEFT:  return 3;
		default:	throw new RuntimeException("ungültige Richtung");
		}
	}
}
