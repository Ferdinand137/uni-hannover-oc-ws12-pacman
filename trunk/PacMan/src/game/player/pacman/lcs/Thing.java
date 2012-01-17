package game.player.pacman.lcs;

public enum Thing {
	GHOST, PILL, POWER_PILL, JUNCTION, TURN_BACK, PACMAN;

	String toId() {
		switch(this) {
		case GHOST: return "G";
		case PILL: return "P";
		case POWER_PILL: return "PP";
		case JUNCTION: return "J";
		case TURN_BACK: return "C";
		case PACMAN: return "PA";
		default: throw new RuntimeException("invalid thing");
		}
	}
}