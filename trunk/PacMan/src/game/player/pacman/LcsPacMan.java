package game.player.pacman;

import game.core.G;
import game.core.Game;
import game.core.GameView;
import gui.AbstractPlayer;

import java.awt.Color;
import java.util.Vector;

public final class LcsPacMan extends AbstractPlayer{
	class SetOfPaths {
		private Vector<int[]> paths = new Vector<int[]>();
		
		void add(int[] path) {
			paths.add(path);
		}
		
		int[] getShortest() {
			if(paths.size() == 0) throw new NullPointerException("Ej du Opfer, es sind keine Pfade im Set!");
			
			int[] shortest = null;
			
			for (int[] path : paths) {
				if(shortest == null || shortest.length > path.length)
					shortest = path;
			}
			
			assert shortest != null;
			return shortest;
		}

		public boolean isEmpty() {
			return paths.isEmpty();
		}
	}

	@Override
	public int getAction(Game game,long timeDue){
		int currentLocation = game.getCurPacManLoc();

		int[] activePills = game.getPillIndicesActive();
		
		SetOfPaths pathsToGhosts = new SetOfPaths();
		
		for(int i=0;i<G.NUM_GHOSTS;i++) {
			if(game.getLairTime(i) > 0) continue; // ignore erstmal

			pathsToGhosts.add(game.getPath(currentLocation, game.getCurGhostLoc(i)));
		}
		
		if(!pathsToGhosts.isEmpty()) {
			int[] nextGhost = pathsToGhosts.getShortest();
		
			GameView.addPoints(game,Color.RED, nextGhost);
		}
		
		
		int[] directions=game.getPossiblePacManDirs(false);
		int[] neighbours = game.getPacManNeighbours();
		
		if(!pathsToGhosts.isEmpty()) {
			// TODO wenn neighbor[i] == ghostPfad[1] dann diesen weg abwerten oder so
		}
		return directions[G.rnd.nextInt(directions.length)];		
	}

	@Override
	public String getGroupName() {
		return "Gruppe xy - Lcs PacMan";
	}
}