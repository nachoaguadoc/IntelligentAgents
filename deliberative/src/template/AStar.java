package template;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import logist.agent.Agent;
import logist.plan.Plan;
import template.DeliberativeTemplate.Algorithm;

import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * A* algorithm
 * @author Ignacio Aguado, Darío Martínez
 */
public class AStar {
	
	TreeMap<Double, DeliberativeState> toVisit = new TreeMap<Double, DeliberativeState>();
	enum HeuristicEnum { BENEFITS, DISTANCE }
	
	/**
	 * Performs the A* search for a given State
	 * 
	 * @param initialState: State where the vehicle is when the search begins
	 * @param agent to read the heuristic
	 * @returns a List of Actions to take from that State to arrive to the goal
	 * 
	 */
	public DeliberativeState search(DeliberativeState initialState, Agent agent) {
		String heuristicName =  agent.readProperty("heuristic", String.class, "BENEFITS");
		Heuristic heuristic;
		HeuristicEnum h = HeuristicEnum.valueOf(heuristicName.toUpperCase());
		// Compute the search with the selected heuristic.
		switch (h) {
			case BENEFITS:
				heuristic = new BenefitsHeuristic();
				break;
			case DISTANCE:
				heuristic = new DistanceHeuristic();
				break;
			default:
				throw new AssertionError("Should not happen.");
		}		
		
		toVisit.put(0.0, initialState);
		int iterations = 0;
		
		// We search until we find a final State
		while (true){
			iterations++;
			DeliberativeState currentState = toVisit.pollFirstEntry().getValue();
			
			// First state found is the one chosen
			if (currentState.isGoal()) {
				System.out.println("A* results with Heuristic " + heuristicName + ": " + iterations + " iterations, " + currentState.actionHistory.size() + " actions with a total benefit of " + currentState.totalBenefits);
				return currentState;
			} else {
				if (!currentState.knownState()) {
					List<DeliberativeState> nextStates = currentState.getNextStates();
					for (DeliberativeState state : nextStates) {
						double f = heuristic.getF(state);
						toVisit.put(f, state);
					}
				}
			}
		}
			
	}
}
