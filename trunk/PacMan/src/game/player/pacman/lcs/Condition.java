package game.player.pacman.lcs;

import game.core.Game;

public interface Condition {
	boolean matchForPacMan(Game game);
	boolean matchForGhost(Game game, int whichGhost);

	String toId();

	// just a reminder, override this!
	@Override
	String toString();
}
