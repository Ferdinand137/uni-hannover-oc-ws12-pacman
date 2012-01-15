package game.player.pacman.lcs;

import java.util.HashMap;
import java.util.Random;

public class FitnessSave {
	static HashMap<String, Float> fitness = new HashMap<String, Float>();
	static Random random = new Random();

	static String lastMutationId = null;
	static float lastPreMutationValue;

	static void set(final String id, float value) {
		if(value <= 0) {
			System.out.println("WARNING: Fitness < 0 --> Fitness = 0");
			value = 0;
		}
		System.out.println("new fitness for " + id + "   " + get(id) + " -> " + value);
		fitness.put(id, value);
	}

	static float get(final String id) {
		// add all values to storage. this is required for mutation!
		if(!fitness.containsKey(id)) {
			System.out.println("default fitness init for previously unused rule: " + id);
			fitness.put(id, 1.0f);
		}

		return fitness.get(id);
	}

	public static void mutate() {
		final int randomId = random.nextInt(fitness.size());
		lastMutationId = (String) fitness.keySet().toArray()[randomId];
		float value = get(lastMutationId);
		lastPreMutationValue = value;

		switch(random.nextInt(4)) {
		case 0: value -= 0.5f; break;
		case 1: value -= 0.1f; break;
		case 2: value += 0.1f; break;
		case 3: value += 0.5f; break;
		default: throw new RuntimeException("invalid random case");
		}

		set(lastMutationId, value);
	}

	public static void revertMutation() {
		set(lastMutationId, lastPreMutationValue);
	}

	public static void dump() {
		System.out.println(fitness);
	}
}
