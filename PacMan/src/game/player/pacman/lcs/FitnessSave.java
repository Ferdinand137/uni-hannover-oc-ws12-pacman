package game.player.pacman.lcs;

import game.player.ghost.lcs.LcsGhost;

import java.util.HashMap;
import java.util.Random;

public class FitnessSave {
	static HashMap<String, Float> fitness = new HashMap<String, Float>();
	static Random random = new Random();

	static String lastMutationId = null;
	static float lastPreMutationValue;

	static void set(final String id, float value) {
		if(value < 0.1f) {
			System.err.println("WARNING: Fitness < 0.1 --> Fitness = 0.1");
			value = 0.1f;
		}

		//if(Math.abs(get(id) - value) > 1) {
			//System.out.println("new fitness for " + id + "   " + get(id) + " -> " + value);
		//}
		fitness.put(id, value);
	}

	static float get(final String id) {
		// add all values to storage. this is required for mutation!
		if(!fitness.containsKey(id)) {
			System.out.println("default fitness init for previously unused rule: " + id);
			fitness.put(id, 20.0f);
		}

		return fitness.get(id);
	}

	public static void mutate() {
		final int randomId = random.nextInt(fitness.size());
		lastMutationId = (String) fitness.keySet().toArray()[randomId];
		float value = get(lastMutationId);
		lastPreMutationValue = value;

		switch(random.nextInt(4)) {
		case 0: value -= 4.0f; break;
		case 1: value -= 1.0f; break;
		case 2: value += 1.0f; break;
		case 3: value += 4.0f; break;
		default: throw new RuntimeException("invalid random case");
		}

		boolean found = false;
		for(final Rule rule : LcsGhost.ruleSet) {
			//System.out.println(lastMutationId + " vs " + rule.toId());
			if(lastMutationId.equals(rule.toId())) {
				System.out.println("fitness change of " + rule + " -> " + value);
				found = true;
				break;
			}
		}
		assert found == true;

		set(lastMutationId, value);
	}

	public static void revertMutation() {
		set(lastMutationId, lastPreMutationValue);
	}

	public static void dump() {
		System.out.println(fitness);
	}

	public static void clear() {
		fitness.clear();
	}
}
