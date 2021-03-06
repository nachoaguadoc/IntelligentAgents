package template;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Breadth-First Search algorithm
 * @author Ignacio Aguado, Darío Martínez
 */
public class BFS {
	
	private Queue<DeliberativeState> toVisit = new LinkedList<DeliberativeState>();

	private DeliberativeState goal;
	
	/**
	 * Performs the BFS search for a given State
	 * 
	 * @param initialState: State where the vehicle is when the search begins
	 * @returns a List of Actions to take from that State to arrive to the goal
	 * 
	 */
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
				// If we haven't found any final state or we haven't finished to analyze the final state level of depth
				if (goal == null || currentState.getLevelOfDepth() <= goal.getLevelOfDepth()) {
					// If we haven't been there before 
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
