package game.player.pacman.lcs;

public enum Thing {
	GHOST, PILL, POWER_PILL, JUNCTION;

	String toId() {
		switch(this) {
		case GHOST: return "G";
		case PILL: return "P";
		case POWER_PILL: return "PP";
		case JUNCTION: return "J";
		default: throw new RuntimeException("invalid thing");
		}
	}
}