package game.player.pacman;

import game.core.Game;
import game.player.pacman.lcs.Direction;
import game.player.pacman.lcs.DistanceCondition;
import game.player.pacman.lcs.EdibleCondition;
import game.player.pacman.lcs.FitnessSave;
import game.player.pacman.lcs.JunctionCondition;
import game.player.pacman.lcs.MoveAction;
import game.player.pacman.lcs.MoveRecommendation;
import game.player.pacman.lcs.Rule;
import game.player.pacman.lcs.RuleFunctions;
import game.player.pacman.lcs.Thing;
import gui.AbstractPlayer;

import java.util.Vector;

/**
 * For debugging time consumption. According to first measurements this does not slow down execution at all. (0,002 ms over 10 secs of 100% cpu)
 */
class Timer {
	private long time, counter, start;
	
	void start() {
		start = System.nanoTime();
	}
	
	void stop() {
		time += (System.nanoTime() - start);
		counter++;
	}
	
	float getAvgInMs() {
		return time / counter / 1000 / 1000.0f;
	}
}
public final class LcsPacMan extends AbstractPlayer{
	
	RuleFunctions ruleFunctions;
	Vector<Rule> ruleSet = new Vector<Rule>();

	Timer timer_total = new Timer(), timer_prepare = new Timer(), timer_match = new Timer(), timer_getDirection = new Timer(), timer_training = new Timer();
	
	static Game game;
	 public LcsPacMan() {
		//System.out.println("---");
		//System.out.println("---");
		System.out.println("neuer pacman");
		//System.out.println("---");
		//System.out.println("---");
		
		ruleSet.add(new Rule().setAction(new MoveAction(Thing.PILL)));
		ruleSet.add(new Rule().add(new JunctionCondition()).setAction(new MoveAction(Thing.JUNCTION)).setFitness(2.0f));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 5)).add(new DistanceCondition(Thing.POWER_PILL, 0, 7.5f)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 2.5f)).add(new DistanceCondition(Thing.POWER_PILL, 0, 10)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 5, 10)).add(new DistanceCondition(Thing.POWER_PILL, 5, 10)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.POWER_PILL)));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.GHOST, 0, 30)).add(new EdibleCondition(false)).setAction(new MoveAction(Thing.GHOST)).setFitness(-10));
		ruleSet.add(new Rule().add(new DistanceCondition(Thing.POWER_PILL, 0, 27.5f)).setAction(new MoveAction(Thing.POWER_PILL)).setFitness(-1));

	}
	 
	@Override
	public int getAction(Game game,long timeDue){
		timer_total.start();
		
		//System.out.println("time: " + (timeDue - System.currentTimeMillis()));
		//System.out.println("---");
		
		timer_prepare.start();
		RuleFunctions.prepareNextRound(game);

		// #UP=0, #RIGHT=1, #DOWN=2, #LEFT=3 
		float[] directionCounter = new float[4];
		
		for(int i = 0; i < 4; i++) {
			if(game.getNeighbour(game.getCurPacManLoc(), i) == -1) {
				// in die richtung ist ne wand!
				directionCounter[i] = Float.NEGATIVE_INFINITY;
			}
		}
		timer_prepare.stop();
		
		//float[] direction_weight = new float[4];
		
//		int regelZumAusgebenNurBlub = 0;
		for (Rule rule : ruleSet) {
//			++regelZumAusgebenNurBlub;
			
			timer_match.start();
			if(rule.match(game)) {
				timer_match.stop();
				
				timer_getDirection.start();
				MoveRecommendation dir = rule.getActionDirection(game);
				timer_getDirection.stop();
				
				//System.out.print(regelZumAusgebenNurBlub + ". rule matches:\n");
				for (Direction direction : Direction.values()) {
					//System.out.println(direction + ": " + dir.fitness[direction.toInt()]);
					directionCounter[direction.toInt()] += dir.fitness[direction.toInt()];
				}
			} else {
				timer_match.stop();
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
		//System.out.println("-> laufe nach: " + Direction.createFromInt(dir));
		
		// dann nachm motto:
		//if(test.match(game)) test.getActionDirection(game);
		// TODO aus conditions alle raussuchen wo match true
		// von denen raussuchen in welche richtung sie laufen wollen
		// erstmal zB einfache mehrheitentscheidung
		//test.getActionDirection(game); // sollte funktionieren sobald marcus seins fertig hat
		timer_total.stop();
		
		return dir;
	}

	@Override
	public String getGroupName() {
		return "Gruppe2_PacMan";
	}

	public void trainingBegin(int totalTrainings) {
		timer_training.start();
	}
	
	int trainingScore = 0;
	public void trainingRoundOver(int round, int totalTrainings, Game game) {
		trainingScore += game.getScore();

		// for the moment only 10 rounds per training. this is WAY too few but it's so damn slow :(
		final int GAMES_PER_TRAINING = 20;
		
		if((round+1) % GAMES_PER_TRAINING == 0) {
			timer_training.stop();
			
			trainingScore /= GAMES_PER_TRAINING;
			
			System.out.println("");
			System.out.println("Avg getAction    time: " + timer_total.getAvgInMs()        + "ms");
			System.out.println("Avg prepare      time: " + timer_prepare.getAvgInMs()      + "ms");
			System.out.println("Avg match        time: " + timer_match.getAvgInMs()        + "ms");
			System.out.println("Avg getDirection time: " + timer_getDirection.getAvgInMs()  + "ms");
			System.out.println("Avg training     time: " + timer_training.getAvgInMs()/(float)GAMES_PER_TRAINING + "ms");
			System.out.println("    training     time: " + timer_training.getAvgInMs()/1000.0f + "s");
			
			System.out.println("Avg score after " + GAMES_PER_TRAINING + " rounds: " + trainingScore);
			System.out.println("");
			FitnessSave.mutate();
			
			timer_training.start();
		}
	}

	public void trainingOver(int trainings) {
		// TODO Auto-generated method stub
		
	}
}