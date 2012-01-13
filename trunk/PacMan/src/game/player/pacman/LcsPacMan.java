package game.player.pacman;

import game.core.Game;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.EdibleCondition;
import game.player.pacman.lcs.JunctionCondition;
import game.player.pacman.lcs.MoveAction;
import game.player.pacman.lcs.MoveRecommendation;
import game.player.pacman.lcs.Rule;
import game.player.pacman.lcs.RuleFunctions;
import game.player.pacman.lcs.Thing;
import gui.AbstractPlayer;

import java.util.Vector;

public final class LcsPacMan extends AbstractPlayer{
	
	RuleFunctions ruleFunctions;
	static Game game;
	 public LcsPacMan() {
		System.out.println("---");
		System.out.println("---");
		System.out.println("neuer pacman");
		System.out.println("---");
		System.out.println("---");
	}
	 
	@Override
	public int getAction(Game game,long timeDue){
		System.out.println("---");
		
		RuleFunctions.prepareNextRound(game);
		
		Vector<Rule> ruleSet = new Vector<Rule>();
		
//		Rule test = new Rule();
//		test.add(new DistanceCondition(Thing.GHOST, 5, 10));
//		test.add(new DistanceCondition(Thing.PILL, 1, 3));
//		test.setAction(new MoveAction(Thing.GHOST, true));
//		RuleFunctions.getNextPillDistance();
//		RuleFunctions.getNextPowerPillDistance();
//		conditions.add(test);

		Rule juncTest = new Rule();
		juncTest.add(new JunctionCondition());
		juncTest.setAction(new MoveAction(Thing.JUNCTION));
		ruleSet.add(juncTest);
		
		/*
		Rule rule1 = new Rule();
		rule1.add(new DistanceCondition(Thing.GHOST, 0, 5));
		rule1.add(new DistanceCondition(Thing.POWER_PILL, 0, 7.5f));
		rule1.add(new EdibleCondition(false));
		rule1.setAction(new MoveAction(Thing.POWER_PILL));
		ruleSet.add(rule1);

		// TODO: was ist der Unterschied zwischen "not edible" und "ungleich edible"??? 
//		Rule rule2 = new Rule();
//		rule2.add(new DistanceCondition(Thing.GHOST, 0, 2.5f));
//		rule2.add(new DistanceCondition(Thing.POWER_PILL, 0, 10));
//		rule2.add(new EdibleCondition(false));
//		rule2.setAction(new MoveAction(Thing.POWER_PILL, true));
//		conditions.add(rule2);

/* erstma weg da die regel 0 sinn macht
		rule3.add(new DistanceCondition(Thing.GHOST, 38.671875f, 40.390625f));
		rule3.add(new EdibleCondition(false));
		rule3.setAction(new MoveAction(Thing.GHOST, true));
		ruleSet.add(rule3);
*
		
		Rule rule4 = new Rule();
		rule4.add(new DistanceCondition(Thing.GHOST, 5, 10));
		rule4.add(new DistanceCondition(Thing.POWER_PILL, 5, 10));
		rule4.add(new EdibleCondition(false));
		rule4.setAction(new MoveAction(Thing.POWER_PILL));
		ruleSet.add(rule4);

		Rule rule5 = new Rule();
//		rule5.add(new DistanceCondition(Thing.PILL, 0, 55));
		rule5.setAction(new MoveAction(Thing.PILL));
		ruleSet.add(rule5);
*/
		ruleSet.add(new Rule()
			.add(new DistanceCondition(Thing.GHOST, 0, 13.75f))
			.add(new EdibleCondition(false))
			.setAction(new MoveAction(Thing.GHOST))
			.setFitness(-10));

		// TODO: junction ...
		//Rule rule7 = new Rule();
		/*
		Rule rule8 = new Rule();
		rule8.add(new DistanceCondition(Thing.POWER_PILL, 0, 27.5f));
		rule8.setAction(new MoveAction(Thing.POWER_PILL, true));
		ruleSet.add(rule8);
		*/
		
		// #UP=0, #RIGHT=1, #DOWN=2, #LEFT=3 
		float[] directionCounter = new float[4];
		
		for(int i = 0; i < 4; i++) {
			if(game.getNeighbour(game.getCurPacManLoc(), i) == -1) {
				// in die richtung ist ne wand!
				directionCounter[i] = Float.NEGATIVE_INFINITY;
			}
		}
		
		//float[] direction_weight = new float[4];
		
		int regelZumAusgebenNurBlub = 0;
		for (Rule rule : ruleSet) {
			++regelZumAusgebenNurBlub;
			if(rule.match(game)) {
				MoveRecommendation dir = rule.getActionDirection(game);
				System.out.print(regelZumAusgebenNurBlub + ". rule matches: ");
				for (int i = 0; i < 4; i++) {
					System.out.println("getActionDirection: " + i + " @ " + dir.fitness[i]);
					directionCounter[i] += dir.fitness[i];
				}
			}
		}
		
		int dir = -1;
		float maxDirCount = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < 4; i++) {
			// TODO: Entscheidungsregel, wenn zwei oder mehr Richtungen
			// gleichhaeufig vorkommen
			if (directionCounter[i] > maxDirCount) {
				dir = i;
				maxDirCount = directionCounter[i];
			}

		}
		if (dir < 0) {
			System.out.println("ACHTUNG keine passende Regel, was nu?"); // FIXME
		}
		
		
		// dann nachm motto:
		//if(test.match(game)) test.getActionDirection(game);
		// TODO aus conditions alle raussuchen wo match true
		// von denen raussuchen in welche richtung sie laufen wollen
		// erstmal zB einfache mehrheitentscheidung
		//test.getActionDirection(game); // sollte funktionieren sobald marcus seins fertig hat
		
		int[] directions=game.getPossiblePacManDirs(false);
		//return directions[G.rnd.nextInt(directions.length)];		
		return dir;
	}

	@Override
	public String getGroupName() {
		return "Gruppe2_PacMan";
	}

	public void trainingRoundOver(int round, int totalTrainings, Game game) {
		// TODO Auto-generated method stub
	}

	public void trainingOver(int trainings) {
		// TODO Auto-generated method stub
		
	}
}