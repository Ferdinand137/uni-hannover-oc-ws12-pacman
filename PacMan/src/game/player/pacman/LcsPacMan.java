package game.player.pacman;

import java.awt.Color;

import game.core.G;
import game.core.Game;
import game.core.GameView;
import gui.AbstractPlayer;

public final class LcsPacMan extends AbstractPlayer{	
	@Override
	public int getAction(Game game,long timeDue){
		int currentLocation = game.getCurPacManLoc();
		
		
		
		for(int i=0;i<G.NUM_GHOSTS;i++)										
			if(game.getLairTime(i)==0)
				if(game.isEdible(i))
					GameView.addPoints(game,Color.GREEN,game.getPath(currentLocation,game.getCurGhostLoc(i)));
				else
					GameView.addPoints(game,Color.RED,game.getPath(currentLocation,game.getCurGhostLoc(i)));
		
		
		int[] directions=game.getPossiblePacManDirs(false);		//set flag as false to prevent reversals	
		return directions[G.rnd.nextInt(directions.length)];		
	}

	@Override
	public String getGroupName() {
		return "Gruppe xy - Lcs PacMan";
	}
}