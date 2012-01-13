package game.player.pacman.lcs;

import java.util.HashMap;
import java.util.Random;

public class FitnessSave {
	static HashMap<String, Float> fitness = new HashMap<String, Float>();
	static Random random = new Random();
	
	static void set(String id, float value) {
		System.out.println("new fitness for " + id + " -> " + value);
		fitness.put(id, value);
	}
	
	static float get(String id) {
		// add all values to storage. this is required for mutation!
		if(!fitness.containsKey(id)) {
			System.out.println("default fitness init for previously unused rule:");
			set(id, 1.0f);
		}

		return fitness.get(id);
	}

	public static void mutate() {
		int randomId = random.nextInt(fitness.size());
		String randomKey = (String) fitness.keySet().toArray()[randomId];
		float value = get(randomKey);
		
		switch(random.nextInt(4)) {
		case 0: value -= 0.5f; break;
		case 1: value -= 0.1f; break;
		case 2: value += 0.1f; break;
		case 3: value += 0.5f; break;
		default: throw new RuntimeException("invalid random case");
		}
		
		set(randomKey, value);
	}
}
