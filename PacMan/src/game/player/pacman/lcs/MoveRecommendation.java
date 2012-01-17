package game.player.pacman.lcs;

import game.player.pacman.LcsPacMan;

import java.util.Random;


public class MoveRecommendation {
	private final float[] fitnessArr;
	private static final Random random = new Random();

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
	public Direction getRouletteFitness(final int currentPos) {
		int bestDir = -1;

		System.out.println(this);

		{
			float best = Float.NEGATIVE_INFINITY;
			for (int i = 0; i < 4; i++) {
				if(RuleFunctions.game.getNeighbour(currentPos, i) != -1) {
					// nur wo keine wand ist
					if(fitnessArr[i] > best) {
						best = fitnessArr[i];
						bestDir = i;
					}
				}
			}
		}

		// just pick the best one most of the time!
		if(random.nextFloat() < 0.95)
			return Direction.createFromInt(bestDir);

		fitnessArr[bestDir] *= 0.001f; // try choosing a different direction

		// choose random direction according to fitness
		float totalFitness = 0;

		for (int i = 0; i < 4; i++) {
			if(RuleFunctions.game.getNeighbour(currentPos, i) == -1) {
				// in die richtung ist ne wand!
				fitnessArr[i] = Float.NEGATIVE_INFINITY;
			} else {
				totalFitness += fitnessArr[i];
			}
		}

		// we need fitness 30 to win over fitness 10 in more than 75% of all cases
		// so lets try 30*30=900 vs 10*10=100 -> 88%
		// for (int i = 0; i < 4; i++) {
		//	fitnessArr[i] = fitnessArr[i] * fitnessArr[i];
		// }
		// BUG -unendlich * -unendlich = unendlich!



		float randomFloat = random.nextFloat() * totalFitness;
		// TODO vorher: dir = -1 ... wieso? Stürzt dann aber ab
		int dir = 0;

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
		if(randomFloat >= 0) {
			System.out.println("0: " + fitnessArr[0]);
			System.out.println("1: " + fitnessArr[1]);
			System.out.println("2: " + fitnessArr[2]);
			System.out.println("3: " + fitnessArr[3]);
			System.out.println("randomFloat: " + randomFloat);
			assert false;
		}

		if (dir < 0) {
			System.out.println("ACHTUNG keine passende Regel, was nu? " + dir); // FIXME
		}

		LcsPacMan.debug("choosing random direction: " + Direction.createFromInt(dir) + " instead of " + Direction.createFromInt(bestDir));

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

	public float getFitness(final Direction dir) {
		return fitnessArr[dir.toInt()];
	}
}
