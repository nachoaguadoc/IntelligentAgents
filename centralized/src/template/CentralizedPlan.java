package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import logist.simulation.Vehicle;
import logist.topology.Topology.City;

/**
 * Stochastic Plan for the Centralized Behaviour
 * @author Ignacio Aguado, Darío Martínez
 */
public class CentralizedPlan {
	public HashMap<Integer, LinkedList<CentralizedTask>> planTasks;
	public int cost;
	public List<Vehicle> vehicles;
	
	/**
	 * Initializer for an empty new Plan
	 * 
	 * @param: vehicles: List of vehicles
	 */
	public CentralizedPlan(List<Vehicle> vehicles) {
		this.planTasks = new HashMap<Integer, LinkedList<CentralizedTask>>();
		this.vehicles = vehicles;
	}
	
	/**
	 * Initializer for Plan from a list of tasks and vehicles
	 * 
	 * @param: vehicles: List of vehicles
	 * @ param planTasks: Sorted list of tasks
	 */
	public CentralizedPlan(HashMap<Integer, LinkedList<CentralizedTask>> planTasks, List<Vehicle> vehicles) {
		this.planTasks = planTasks;
		this.vehicles = vehicles;
	}
	
	/**
	 * Initializer for a plan from another plan
	 * 
	 * @param: vehicles: List of vehicles
	 */
	public CentralizedPlan(CentralizedPlan plan) {
		this.planTasks = cloneHashmap(plan.planTasks);
		this.vehicles = plan.vehicles;
	}
	
	/**
	 * Calculate the cost of a plan
	 * 
	 * @return: final cost
	 */
	public double planCost() {
		double tempCost = 0;
		for (Integer i : planTasks.keySet()) {
			Vehicle vehicle = vehicles.get(i);
			City currentCity = vehicle.getCurrentCity();
			for (CentralizedTask task : planTasks.get(i)) {
				if (task.pickup) {
					double distance = currentCity.distanceTo(task.pickupCity);
					double cost = distance*vehicle.costPerKm();
					currentCity = task.pickupCity;
					tempCost += cost;
				} else {
					double distance = currentCity.distanceTo(task.deliveryCity);
					double cost = distance*vehicle.costPerKm();
					currentCity = task.deliveryCity;
					tempCost += cost;
				}
			}	
		}
		return tempCost;
	}
	
	/**
	 * Calculate the distance driven of a plan
	 * 
	 * @return: final distance
	 */
	public int planDistance() {
		int distance = 0;
		for (Integer i : planTasks.keySet()) {
			Vehicle vehicle = vehicles.get(i);
			City currentCity = vehicle.getCurrentCity();
			for (CentralizedTask task : planTasks.get(i)) {
				if (task.pickup) {
					distance += currentCity.distanceTo(task.pickupCity);
					currentCity = task.pickupCity;
				} else {
					distance += currentCity.distanceTo(task.deliveryCity);
					currentCity = task.deliveryCity;
				}
			}	
		}
		return distance;
	}
	
	/**
	 * Check if the Plan follows the constraints of the problem
	 * 
	 * @return: boolean 
	 */
	public boolean validConstraints() {
		for (Integer i : planTasks.keySet()) {
			int weights = 0;
			List<String> toPickup = new ArrayList<String>();

			for (CentralizedTask task : planTasks.get(i)) {
				
				if (task.pickup) {
					toPickup.add(Integer.toString(task.task.id));
					weights += task.weight;
					if (weights > vehicles.get(i).capacity()) {
						//System.out.println("Weights");
						return false;
					}
				} else {

					if (!toPickup.contains(Integer.toString(task.task.id))) {
						//System.out.println("Not pickup");
						return false;
					} else {
						toPickup.remove(Integer.toString(task.task.id));
						weights -= task.weight;
					}
				}
			}
			if (toPickup.size() > 0) {
				//System.out.println("Not empty");
				return false;
			}
		}
		return true;

	}
	
	/**
	 * Clone a HashMap from another plan (NOT a shallow copy)
	 * 
	 * @return: new hashmap
	 */
    public HashMap<Integer, LinkedList<CentralizedTask>> cloneHashmap(HashMap<Integer, LinkedList<CentralizedTask>> plan) {
    	HashMap<Integer, LinkedList<CentralizedTask>> copy = new HashMap<Integer, LinkedList<CentralizedTask>>();
    	for(Entry<Integer, LinkedList<CentralizedTask>> entry : plan.entrySet()){
    	    copy.put(entry.getKey(), new LinkedList<CentralizedTask>(entry.getValue()));
    	}
    	return copy;
    }
    
	/**
	 * Print ordered list of tasks assigned to each vehicle
	 * 
	 */
    public void paint() {
    	for (Vehicle v : vehicles) {
    		System.out.println("Vehicle " + v.id());
    		for (CentralizedTask t: planTasks.get(v.id())) {
    			System.out.println(t.task.id);
    		}
    	}
    }
    
	/**
	 * Number of tasks per vehicle
	 * 
	 * @return: String 
	 */
    public String toString() {
    	String distribution = "";
    	for (Vehicle v : vehicles) {
    		distribution += planTasks.get(v.id()).size() + " ";
    	}
    	return distribution;
    }
    
	/**
	 * Number of vehicles working in the plan
	 * 
	 * @return: number of vehicles
	 */
	public int getVehicles() {
		int nVehicles = 0;
    	for (Vehicle v : vehicles) {
    		if (planTasks.get(v.id()).size() > 0) {
    			nVehicles++;
    		}
    	}
    	return nVehicles;
	}
	
}
