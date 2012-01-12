package game.player.pacman;

import game.core.Game;
import game.player.pacman.lcs.BlinkingCondition;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.EdibleCondition;
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
		
		System.out.println("next ghost: " + RuleFunctions.getNextGhostDistance());
		System.out.println("next power pill: " + RuleFunctions.getNextPowerPillDistance());
		
		
		Vector<Rule> ruleSet = new Vector<Rule>();
		
//		Rule test = new Rule();
//		test.add(new DistanceCondition(Thing.GHOST, 5, 10));
//		test.add(new DistanceCondition(Thing.PILL, 1, 3));
//		test.setAction(new MoveAction(Thing.GHOST, true));
//		RuleFunctions.getNextPillDistance();
//		RuleFunctions.getNextPowerPillDistance();
//		conditions.add(test);

		Rule rule1 = new Rule();
		rule1.add(new DistanceCondition(Thing.GHOST, 3.75f, 5));
		rule1.add(new DistanceCondition(Thing.POWER_PILL, 2.5f, 7.5f));
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

		Rule rule3 = new Rule();
		rule3.add(new DistanceCondition(Thing.GHOST, 38.671875f, 40.390625f));
		rule3.add(new EdibleCondition(false));
		rule3.setAction(new MoveAction(Thing.GHOST, true));
		ruleSet.add(rule3);

		Rule rule4 = new Rule();
		rule4.add(new DistanceCondition(Thing.GHOST, 5, 10));
		rule4.add(new DistanceCondition(Thing.POWER_PILL, 5, 10));
		rule4.add(new EdibleCondition(false));
		rule4.setAction(new MoveAction(Thing.PILL));
		ruleSet.add(rule4);

		Rule rule5 = new Rule();
//		rule5.add(new DistanceCondition(Thing.PILL, 0, 55));
		rule5.setAction(new MoveAction(Thing.PILL));
		ruleSet.add(rule5);

		Rule rule6 = new Rule();
		rule6.add(new DistanceCondition(Thing.GHOST, 0, 13.75f));
		rule6.add(new BlinkingCondition(false));
		rule6.add(new EdibleCondition(false));
		rule6.setAction(new MoveAction(Thing.GHOST, true));
		ruleSet.add(rule6);

		// TODO: junction ...
		//Rule rule7 = new Rule();
		/*
		Rule rule8 = new Rule();
		rule8.add(new DistanceCondition(Thing.POWER_PILL, 0, 27.5f));
		rule8.setAction(new MoveAction(Thing.POWER_PILL, true));
		ruleSet.add(rule8);
		*/
		
		// #UP=0, #RIGHT=1, #DOWN=2, #LEFT=3, #gesamt=4 
		int[] direction_counter = new int[5];
		float[] direction_weight = new float[4];
		
		int regelZumAusgebenNurBlub = 0;
		for (Rule rule : ruleSet) {
			++regelZumAusgebenNurBlub;
			if(rule.match(game))
				System.out.println(regelZumAusgebenNurBlub + "st rule matches");

			if (rule.match(game)) {
				// FIXME: game.player.pacman.lcs.RuleFunctions.getNextPillDirection wirft ArrayIndexOutOfBoundsException:
				int dir = rule.getActionDirection(game);
				// FIXME: getActionDirection liefert immer 4
				// sollte ja eigentlich immer zwichen 0 und 3 liefern
				// oder habe ich da was falsch verstanden?
				System.out.println("getActionDirection: " + dir);
				direction_counter[dir]++;
				direction_counter[4]++;
			}
		}
		
		int dir = -1;
		for (int i = 0; i < 4; i++) {
			float max_weight = 0;
			if (direction_counter[4] > 0) {
				direction_weight[i] = ((float)direction_counter[i])/direction_counter[4];
				// TODO: Entscheidungsregel, wenn zwei oder mehr Richtungen gleichhaeufig vorkommen
				if (direction_weight[i] > max_weight) {
					dir = i;
					max_weight = direction_weight[i];
				}
			} else {
				System.out.println("ACHTUNG keine passende Regel, was nu?"); // FIXME
			}
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