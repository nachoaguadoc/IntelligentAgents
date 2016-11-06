package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import logist.simulation.Vehicle;
import logist.topology.Topology.City;

public class CentralizedPlan {
	public HashMap<Integer, LinkedList<CentralizedTask>> planTasks;
	public int cost;
	public List<Vehicle> vehicles;
	
	public CentralizedPlan(List<Vehicle> vehicles) {
		this.planTasks = new HashMap<Integer, LinkedList<CentralizedTask>>();
		this.vehicles = vehicles;
		this.cost = planCost();

	}
	public CentralizedPlan(HashMap<Integer, LinkedList<CentralizedTask>> planTasks, List<Vehicle> vehicles) {
		this.planTasks = planTasks;
		this.vehicles = vehicles;
		this.cost = planCost();

	}
	public CentralizedPlan(CentralizedPlan plan) {
		this.planTasks = cloneHashmap(plan.planTasks);
		this.vehicles = plan.vehicles;
		this.cost = planCost();
	}
	
	public int planCost() {
		int tempCost = 0;
		for (Integer i : planTasks.keySet()) {
			Vehicle vehicle = vehicles.get(i);
			City currentCity = vehicle.getCurrentCity();
			for (CentralizedTask task : planTasks.get(i)) {
				if (task.pickup) {
					double distance = currentCity.distanceTo(task.pickupCity);
					int cost = (int) (distance*vehicle.costPerKm());
					currentCity = task.pickupCity;
					tempCost += cost;
				} else {
					double distance = currentCity.distanceTo(task.deliveryCity);
					int cost = (int) (distance*vehicle.costPerKm() - task.reward);
					currentCity = task.deliveryCity;
					tempCost += cost;
				}
			}	
		}
		return tempCost;
	}
	
	public CentralizedTask nextTask(int vehicle) {
		return planTasks.get(vehicle).getFirst();
	}
	
	public CentralizedTask nextTask(CentralizedTask task) {
		for (Integer i : planTasks.keySet()) {
			for (CentralizedTask t : planTasks.get(i)) {
				if (t.equals(task)) {
					int index = planTasks.get(i).indexOf(t);
					if (planTasks.get(i).size() <= index+1) {
						return null;
					} else {
						return planTasks.get(i).get(index+1);
					}
				}
			}
		}
		return null;
	}
	
	public boolean validConstraints() {
		for (Integer i : planTasks.keySet()) {
			int weights = 0;
			List<String> toPickup = new ArrayList<String>();

			for (CentralizedTask task : planTasks.get(i)) {
				
				if (task.pickup) {
					toPickup.add(Integer.toString(task.task.id));
					weights += task.weight;
					if (weights > vehicles.get(i).capacity()) {
						System.out.println("Weights");
						return false;
					}
				} else {

					if (!toPickup.contains(Integer.toString(task.task.id))) {
						System.out.println("Not pickup");

						return false;
					} else {
						toPickup.remove(Integer.toString(task.task.id));
						weights -= task.weight;
					}
				}
			}
			if (toPickup.size() > 0) {
				System.out.println("Not empty");

				return false;
			}
		}
		return true;

	}
    public HashMap<Integer, LinkedList<CentralizedTask>> cloneHashmap(HashMap<Integer, LinkedList<CentralizedTask>> plan) {
    	HashMap<Integer, LinkedList<CentralizedTask>> copy = new HashMap<Integer, LinkedList<CentralizedTask>>();
    	for(Entry<Integer, LinkedList<CentralizedTask>> entry : plan.entrySet()){
    	    copy.put(entry.getKey(), new LinkedList<CentralizedTask>(entry.getValue()));
    	}
    	return copy;
    }
	
	
}