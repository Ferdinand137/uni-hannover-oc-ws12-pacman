package game.player.pacman.lcs;

/**
 * Path mit Geister id
 */
public class PathToGhost extends Path {
	final int ghostId;
	
	public PathToGhost(int ghostId, int[] path) {
		super(path);
		this.ghostId = ghostId;
	}
}
