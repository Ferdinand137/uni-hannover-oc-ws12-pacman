package game.player.pacman.lcs;

public class Path {
	final int path[];
	
	public Path(int path[]) {
		this.path = path;
	}
	
	public int length() {
		return path.length;
	}
	

	public String toString() {
		String ausgabe = length() + ": ";
		for (int i : path) {
			ausgabe += i + ", ";
		}
		return ausgabe;
	}

	public boolean goesOver(int pos) {
		for (int i : path) {
			if(i == pos) return true;
		}
		return false;
	}

}