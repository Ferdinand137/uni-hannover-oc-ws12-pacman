package game.player.pacman.lcs;

import game.core.Game;

public class BlinkingCondition implements Condition {

	boolean blinking;
	
	public BlinkingCondition(boolean blinking) {
		this.blinking = blinking;
	}
	
	@Override
	public boolean matchForPacMan(Game game) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toId() {
		return "#BC#";
	}
}
