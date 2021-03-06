package template;

import java.util.Random;

import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import uchicago.src.sim.analysis.Plot;
/**
 * Lazy Agent for tests
 * Only accepts tasks under a given distance
 * @author Ignacio Aguado, Darío Martínez
 */
public class LazyTemplate implements ReactiveBehavior {

	private Random random;
	private double pPickup;
	private int numActions;
	private Agent myAgent;
	private double maxDistance;
	
	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);
		Double maxDistance = agent.readProperty("max-distance", Double.class,
				800.0);
		this.random = new Random();
		this.pPickup = discount;
		this.maxDistance = maxDistance;
		this.numActions = 0;
		this.myAgent = agent;
	}

	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action;

		if (availableTask == null) {
			City currentCity = vehicle.getCurrentCity();
			City randomNeighbor = currentCity.randomNeighbor(random);
			System.out.println(currentCity.name + ": NO TASKS. MOVING TO " + randomNeighbor.name);
			action = new Move(randomNeighbor);
		} else {
			
			City currentCity = vehicle.getCurrentCity();	
			City nextCity = availableTask.deliveryCity;
			double distance = currentCity.distanceTo(nextCity);
			if (distance > maxDistance) {
				City randomNeighbor = currentCity.randomNeighbor(random);
				System.out.println(currentCity.name + ": TOO FAR. MOVING TO " + randomNeighbor.name);
				action = new Move(randomNeighbor);
			} else {
				System.out.println(currentCity.name + ": TASK TO " + availableTask.deliveryCity + ". ACCEPTED.");
				action = new Pickup(availableTask);	
			}
		}
		
		if (numActions >= 1) {
			System.out.println("The total profit after "+numActions+" actions is "+myAgent.getTotalProfit()+" (average profit: "+(myAgent.getTotalProfit() / (double)numActions)+")");
		}
		numActions++;
		
		return action;
	}
}
