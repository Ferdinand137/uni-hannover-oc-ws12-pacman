package game.player.pacman.lcs;

import game.core.Game;

public interface Condition {
	boolean match(Game game);
	String toId();

	// just a reminder, override this!
	@Override
	String toString();
}
