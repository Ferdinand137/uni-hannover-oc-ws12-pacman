package game.player.pacman.lcs;


import game.core.Game;

import java.util.Vector;

public class Rule implements Condition {
	private Vector<Condition> conditions = new Vector<Condition>();
	private MoveAction action;
	
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

	public void setAction(MoveAction action) {
		this.action = action;
	}
	
	public int getActionDirection(Game game) {
		return action.getDirection(game);
	}
}