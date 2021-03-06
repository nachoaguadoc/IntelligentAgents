package template;


import logist.simulation.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import logist.agent.Agent;
import logist.behavior.DeliberativeBehavior;
import logist.plan.Action;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

/**
 * An optimal planner for one vehicle.
 */
@SuppressWarnings("unused")

/**
 * Deliberative Agent
 * @author Ignacio Aguado, Darío Martínez
 */
public class DeliberativeTemplate implements DeliberativeBehavior {

	enum Algorithm { BFS, ASTAR }
	
	/* Environment */
	Topology topology;
	TaskDistribution td;
	
	/* the properties of the agent */
	Agent agent;
	int capacity;

	/* the planning class */
	Algorithm algorithm;
	
	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {
		this.topology = topology;
		this.td = td;
		this.agent = agent;
		this.algorithm = Algorithm.ASTAR;
		// initialize the planner
		int capacity = agent.vehicles().get(0).capacity();
		String algorithmName = agent.readProperty("algorithm", String.class, "ASTAR");
		// Throws IllegalArgumentException if algorithm is unknown
		algorithm = Algorithm.valueOf(algorithmName.toUpperCase());
		

	}
	
	@Override
	public Plan plan(Vehicle vehicle, TaskSet tasks) {
		Plan plan;

		// Compute the plan with the selected algorithm.
		switch (algorithm) {
		case ASTAR:
			System.out.println("ASTAR");
			plan = aStarPlan(vehicle, tasks);
			break;
		case BFS:
			plan = bfsPlan(vehicle, tasks);
			break;
		default:
			throw new AssertionError("Should not happen.");
		}		
		return plan;
	}
	
	/**
	 * A* Planning
	 * @returns Best Plan found for the agent
	 */
	private Plan aStarPlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		int capacity = vehicle.capacity();
		int copyToDelete = vehicle.capacity();
		int costPerKm = vehicle.costPerKm();
		double benefits = 0.0;
		double totalBenefits = 0.0;
		double totalDistance = 0.0;
		List<Task> toDeliverList = new ArrayList(vehicle.getCurrentTasks());
		Plan plan = new Plan(current);
		List<Task> toPickupList = new ArrayList(tasks);
		List<DeliberativeAction> actionHistory = new ArrayList<DeliberativeAction>();
		DeliberativeState initialState = new DeliberativeState(current, capacity, costPerKm, totalDistance, benefits, totalBenefits, toPickupList, toDeliverList, actionHistory, null);
		AStar aStar = new AStar();
		List<DeliberativeAction> actionPlanList = aStar.search(initialState, agent).actionHistory;
		
		for (DeliberativeAction action : actionPlanList) {
			if (action.move) {
				System.out.println(action.id);
				plan.appendMove(action.nextCity);
			} else if (action.pickup) {
				copyToDelete = copyToDelete-action.pickedupTask.weight;
				System.out.println("PICK UP in " + action.pickedupTask.pickupCity + " to " + action.pickedupTask.deliveryCity);
				plan.appendPickup(action.pickedupTask);
			} else {
				System.out.println("DELIVERY in " + action.deliveredTask.deliveryCity);
				copyToDelete = copyToDelete+action.deliveredTask.weight;
				plan.appendDelivery(action.deliveredTask);
			}
		}
		
		return plan;
	}
	
	/**
	 * BFS Planning
	 * @returns Best Plan found for the agent
	 */
	private Plan bfsPlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		int capacity = vehicle.capacity();
		int copyToDelete = vehicle.capacity();
		int costPerKm = vehicle.costPerKm();
		double benefits = 0.0;
		double totalBenefits = 0.0;
		double totalDistance = 0.0;

		List<Task> toDeliverList = new ArrayList(vehicle.getCurrentTasks());
		Plan plan = new Plan(current);
		List<Task> toPickupList = new ArrayList(tasks);
		List<DeliberativeAction> actionHistory = new ArrayList<DeliberativeAction>();
		DeliberativeState initialState = new DeliberativeState(current, capacity, costPerKm, totalDistance, benefits, totalBenefits, toPickupList, toDeliverList, actionHistory, null);
		BFS bfs = new BFS();
		List<DeliberativeAction> actionPlanList = bfs.search(initialState).actionHistory;
		
		// Once we have a list of actions we build a Plan
		for (DeliberativeAction action : actionPlanList) {
			if (action.move) {
				System.out.println(action.id);
				plan.appendMove(action.nextCity);
			} else if (action.pickup) {
				copyToDelete = copyToDelete-action.pickedupTask.weight;
				System.out.println("PICK UP in " + action.pickedupTask.pickupCity + " to " + action.pickedupTask.deliveryCity);
				plan.appendPickup(action.pickedupTask);
			} else {
				System.out.println("DELIVERY in " + action.deliveredTask.deliveryCity);
				copyToDelete = copyToDelete+action.deliveredTask.weight;
				plan.appendDelivery(action.deliveredTask);
			}
		}
		
		return plan;
	}
	
	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity))
				plan.appendMove(city);

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path())
				plan.appendMove(city);

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}

	@Override
	/**
	 * Called when an Agent tries to pick up a task that no longer exists
	 * Used only for the log
	 */
	public void planCancelled(TaskSet carriedTasks) {
		System.out.println("CHANGING MY PLAN");
		if (!carriedTasks.isEmpty()) {
			// This cannot happen for this simple agent, but typically
			// you will need to consider the carriedTasks when the next
			// plan is computed.
		}
	}
}
