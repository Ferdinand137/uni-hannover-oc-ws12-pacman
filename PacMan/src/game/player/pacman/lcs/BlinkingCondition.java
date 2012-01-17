package game.player.pacman.lcs;

import game.core.Game;

public class BlinkingCondition implements Condition {

	boolean blinking;

	public BlinkingCondition(final boolean blinking) {
		this.blinking = blinking;
	}

	@Override
	public boolean matchForPacMan(final Game game) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toId() {
		return "#BC#";
	}

	@Override
	public boolean matchForGhost(final Game game, final int whichGhost) {
		// TODO Auto-generated method stub
		return false;
	}
}
