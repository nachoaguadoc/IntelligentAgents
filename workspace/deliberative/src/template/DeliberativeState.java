package template;

import logist.topology.Topology.City;

import java.util.ArrayList;
import java.util.List;

import logist.task.Task;

/**
 * State Class for the Deliberative Agent
 * @author Ignacio Aguado, Darío Martínez
 */
public class DeliberativeState {
	
	public City currentCity;
	public int capacity;
	public double costPerKm;
	public double benefits;
	public List<Task> toPickupList;
	public List<Task> toDeliverList;

	/**
	 * Initializer for a state
	 * 
	 * @param origin City where the agent is located without a task
	 */
	public DeliberativeState(City currentCity, int capacity, double costPerKm, double benefits, List<Task> toPickupList, List<Task> toDeliverList) {
		this.currentCity = currentCity;
		this.capacity = capacity;
		this.costPerKm = costPerKm;
		this.benefits = benefits;
		this.toPickupList = toPickupList;
		this.toDeliverList = toDeliverList;
	}
	
	public DeliberativeState move(City nextCity) {
		double newBenefits = -currentCity.distanceTo(nextCity)*costPerKm;
		DeliberativeState newState = new DeliberativeState(nextCity, capacity, costPerKm, newBenefits, toPickupList, toDeliverList);
		return newState;
	}
	
	public DeliberativeState pickup(Task pickedup) {
		int newCapacity = capacity - pickedup.weight;
		double newBenefits = 0;
		List<Task> newToPickupList = toPickupList;
		newToPickupList.remove(pickedup);
		List<Task> newToDeliverList = toDeliverList;
		newToDeliverList.add(pickedup);
		DeliberativeState newState = new DeliberativeState(currentCity, newCapacity, costPerKm, newBenefits, newToPickupList, newToDeliverList);
		return newState;
	}
	
	public DeliberativeState deliver(Task delivered) {
		int newCapacity = capacity + delivered.weight;
		double newBenefits = delivered.reward;
		List<Task> newToDeliverList = toDeliverList;
		newToDeliverList.remove(delivered);
		DeliberativeState newState = new DeliberativeState(currentCity, newCapacity, costPerKm, newBenefits, toPickupList, newToDeliverList);
		return newState;
	}
	
	public boolean canPickup(Task toPickup) {
		return capacity-toPickup.weight >= 0;
	}
	
	public List<DeliberativeState> getNextStates() {
		List<DeliberativeState> nextStates = new ArrayList<DeliberativeState>();
		
		for (Task task : toDeliverList) {
			if (task.deliveryCity.equals(currentCity)) {
				DeliberativeState newState = deliver(task);
				nextStates.add(newState);
				return nextStates;
			}
		}
		
		for (Task task : toPickupList) {
			if (task.pickupCity.equals(currentCity)) {
				DeliberativeState newState = pickup(task);
				nextStates.add(newState);
			}
		}
		
		for (City neighborCity : currentCity.neighbors()){
			DeliberativeState newState = move(neighborCity);
			nextStates.add(newState);
		}

		return nextStates;
	}
}
