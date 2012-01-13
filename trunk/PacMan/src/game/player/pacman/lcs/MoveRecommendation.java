package game.player.pacman.lcs;

public class MoveRecommendation {
	public final float[] fitness;
	
	public MoveRecommendation() {
		fitness = new float[4];
	}
	
	/**
	 * @deprecated brauchen wir das?
	 */
	public MoveRecommendation(float fitnessUp, float fitnessRight, float fitnessDown, float fitnessLeft) {
		fitness = new float[4];
		fitness[0] = fitnessUp;
		fitness[1] = fitnessRight;
		fitness[2] = fitnessDown;
		fitness[3] = fitnessLeft;
	}
}
