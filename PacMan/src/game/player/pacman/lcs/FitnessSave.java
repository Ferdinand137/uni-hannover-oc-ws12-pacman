package game.player.pacman.lcs;

import java.util.HashMap;

public class FitnessSave {
	static HashMap<String, Float> fitness = new HashMap<String, Float>();
	
	static void set(String id, float value) {
		System.out.println("new fitness for " + id + " -> " + value);
		fitness.put(id, value);
	}
	
	static float get(String id) {
		if(!fitness.containsKey(id))
			return 1.0f;

		return fitness.get(id);
	}
}
