package game.player.pacman.lcs;

import java.util.Random;

public class MoveRecommendation {
	private final float[] fitnessArr;

	public MoveRecommendation() {
		fitnessArr = new float[4];
	}

	/**
	 * @deprecated brauchen wir das?
	 */
	@Deprecated
	public MoveRecommendation(final float fitnessUp, final float fitnessRight, final float fitnessDown, final float fitnessLeft) {
		fitnessArr = new float[4];
		fitnessArr[0] = fitnessUp;
		fitnessArr[1] = fitnessRight;
		fitnessArr[2] = fitnessDown;
		fitnessArr[3] = fitnessLeft;
	}

	public void addFitness(final Direction direction, final float fitness, final boolean allExceptThisDirection) {
		assert fitness >= 0;
		assert direction != null;

		if(allExceptThisDirection) {
			for (final Direction d : Direction.values()) {
				if(d != direction) {
					fitnessArr[d.toInt()] += fitness;
				}
			}
		} else {
			fitnessArr[direction.toInt()] += fitness;
		}
	}

	@Override
	public String toString()  {
		return    fitnessArr[0] + " // "
				+ fitnessArr[1] + " // "
				+ fitnessArr[2] + " // "
				+ fitnessArr[3];
	}

	/**
	 * @return randomly choosen direction according to roulette, fitness... principle
	 */
	public Direction getRouletteFitness() {
		// TODO diese funktion ist mindestens komisch... muss mal getestet werden :(

		// choose random direction according to fitness
		int dir = -1;
		float totalFitness = 0;

		for (int i = 0; i < 4; i++) {
			if(RuleFunctions.game.getNeighbour(RuleFunctions.currentLocation, i) == -1) {
				// in die richtung ist ne wand!
				fitnessArr[i] = Float.NEGATIVE_INFINITY;
			} else {
				totalFitness += fitnessArr[i];
			}
		}

		System.out.print(this);

		// we need fitness 30 to win over fitness 10 in more than 75% of all cases
		// so lets try 30*30=900 vs 10*10=100 -> 88%
		for (int i = 0; i < 4; i++) {
			fitnessArr[i] = fitnessArr[i] * fitnessArr[i];
		}



		float randomFloat = new Random().nextFloat() * totalFitness;

		for (int i = 0; i < 4; i++) {
			if(fitnessArr[i] == Float.NEGATIVE_INFINITY) {
				// da ist wohl ne wand!
				continue;
			}

			randomFloat -= fitnessArr[i];
			if (randomFloat < 0) {
				dir = i;
				break;
			}
		}
		assert randomFloat < 0;

		if (dir < 0) {
			System.out.println("ACHTUNG keine passende Regel, was nu?"); // FIXME
		}

		return Direction.createFromInt(dir);
	}

	public void addFitness(final MoveRecommendation move) {
		assert move != null;
		assert fitnessArr.length == 4;

		fitnessArr[0] += move.fitnessArr[0];
		fitnessArr[1] += move.fitnessArr[1];
		fitnessArr[2] += move.fitnessArr[2];
		fitnessArr[3] += move.fitnessArr[3];
	}
}
