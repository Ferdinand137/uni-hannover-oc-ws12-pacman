package game.player.pacman.lcs;


import game.core.Game;

import java.util.Vector;

public class MultipleConditions implements Condition {
	Vector<Condition> conditions = new Vector<Condition>();
	
	public boolean match(Game game) {
		for (Condition condition : conditions) {
			if(!condition.match(game))
				return false;
		}
		return true;
	}

	public void add(DistanceCondition condition) {
		conditions.add(condition);
	}
}