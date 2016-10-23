package template;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFS {
	
	private Queue<DeliberativeState> toVisit = new LinkedList<DeliberativeState>();

	private DeliberativeState goal;
	
	public DeliberativeState search(DeliberativeState initialState) {
		toVisit.add(initialState);
		double bestBenefits = Double.NEGATIVE_INFINITY;
		int iterations = 0;
		
		while (!toVisit.isEmpty()){
			iterations++;
			DeliberativeState currentState = toVisit.poll();
			if (currentState.isGoal() && currentState.totalBenefits > bestBenefits) {
				goal = currentState;
				bestBenefits = currentState.totalBenefits;
			} else {
				if (goal == null || currentState.getLevelOfDepth() <= goal.getLevelOfDepth()) {
					if (!currentState.knownState()) {
						List<DeliberativeState> nextStates = currentState.getNextStates();
						for (DeliberativeState state : nextStates) {
							toVisit.add(state);
						}
					}
				} else {
					break;
				}
			}
			
		}
		System.out.println("BFS results: " + iterations + " iterations, " + goal.actionHistory.size() + " actions with a total benefit of " + goal.totalBenefits);
		return goal;
	}
}