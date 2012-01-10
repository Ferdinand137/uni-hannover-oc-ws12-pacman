package game.player.pacman.lcs;

/**
 * Helper to check wether a value is inside a range
 */
public class Range {
	float from, to;
	
	
	public Range(float from, float to) {
		this.from = from;
		this.to = to;
		
		assert from < to;
	}


	boolean isInside(float value) {
		return from <= value && value <= to;
	}
}
