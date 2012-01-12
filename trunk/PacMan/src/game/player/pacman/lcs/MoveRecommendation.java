package game.player.pacman.lcs;

public class MoveRecommendation {
	public final int direction;
	public final float keinPlan;
	
	public MoveRecommendation(int direction, float keinPlan) {
		this.direction = direction;
		this.keinPlan = keinPlan;
	}
}
