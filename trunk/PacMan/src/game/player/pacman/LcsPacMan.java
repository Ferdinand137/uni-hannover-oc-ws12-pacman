package game.player.pacman;

import game.core.G;
import game.core.Game;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.MoveAction;
import game.player.pacman.lcs.Rule;
import game.player.pacman.lcs.RuleFunctions;
import game.player.pacman.lcs.Thing;
import gui.AbstractPlayer;

import java.util.Vector;

public final class LcsPacMan extends AbstractPlayer{
	
	RuleFunctions ruleFunctions;
	static Game game;
	 public LcsPacMan() {
		System.out.println("neuer pacman");
	}
	 
	@Override
	public int getAction(Game game,long timeDue){
		
		RuleFunctions.prepareNextRound(game);
		
		Vector<Rule> conditions = new Vector<Rule>();
		
		Rule test = new Rule();
		test.add(new DistanceCondition(Thing.GHOST, 5, 10));
		test.add(new DistanceCondition(Thing.PILL, 1, 3));
		test.setAction(new MoveAction(Thing.GHOST, true));
		RuleFunctions.getNextPillDistance();
		RuleFunctions.getNextPowerPillDistance();
		conditions.add(test);
		
		// dann nachm motto:
		if(test.match(game)) test.getActionDirection(game);
		// TODO aus conditions alle raussuchen wo match true
		// von denen raussuchen in welche richtung sie laufen wollen
		// erstmal zB einfache mehrheitentscheidung
		test.getActionDirection(game); // sollte funktionieren sobald marcus seins fertig hat
		
		int[] directions=game.getPossiblePacManDirs(false);
		return directions[G.rnd.nextInt(directions.length)];		
	}

	@Override
	public String getGroupName() {
		return "Gruppe xy - Lcs PacMan";
	}

	public void trainingRoundOver(int round, int totalTrainings, Game game) {
		// TODO Auto-generated method stub
	}

	public void trainingOver(int trainings) {
		// TODO Auto-generated method stub
		
	}
}