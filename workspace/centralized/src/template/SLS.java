package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import logist.LogistSettings;

import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.behavior.CentralizedBehavior;
import logist.agent.Agent;
import logist.config.Parsers;
import logist.simulation.Vehicle;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;
public class SLS {
	
	public List<Vehicle> vehicles = new ArrayList<Vehicle>();
	public List<Task> tasks = new ArrayList<Task>();

	public SLS(List<Vehicle> vehicles, List<Task> tasks) {
		this.vehicles = vehicles;
		this.tasks = tasks;
	}
	
	public CentralizedPlan selectInitialSolution() {
		CentralizedPlan initialPlan = new CentralizedPlan(vehicles);
		
		int maxCapacity = 0;
		Vehicle biggestVehicle = vehicles.get(0);
		for (Vehicle v : vehicles) {
			initialPlan.planTasks.put(v.id(), new LinkedList<CentralizedTask>());
			if (v.capacity() > maxCapacity) {
				maxCapacity = v.capacity();
				biggestVehicle = v;
			}
		}
		
		for (Task task : tasks) {
			CentralizedTask pickupTask = new CentralizedTask("PICKUP", task);
			CentralizedTask deliveryTask = new CentralizedTask("DELIVERY", task);
			
			LinkedList<CentralizedTask> taskList = initialPlan.planTasks.get(biggestVehicle.id());
			taskList.addLast(pickupTask);
			taskList.addLast(deliveryTask);
			initialPlan.planTasks.put(biggestVehicle.id(), taskList);
		}
		return initialPlan;
		
	}
	public ArrayList<CentralizedPlan> changeVehicle(CentralizedPlan plan, int initialVehicle) {
		ArrayList<CentralizedPlan> neighbors = new ArrayList<CentralizedPlan>();
		CentralizedTask movedPickupTask = plan.planTasks.get(initialVehicle).pollFirst();
		CentralizedTask movedDeliveryTask = null;
		LinkedList<CentralizedTask> tasksCopy = new LinkedList<CentralizedTask>(plan.planTasks.get(initialVehicle));

		for (CentralizedTask t : tasksCopy ) {
			if (t.task.id == movedPickupTask.task.id) {
				movedDeliveryTask = plan.planTasks.get(initialVehicle).remove(plan.planTasks.get(initialVehicle).indexOf(t));
			}
		}

		for (Integer vehicle : plan.planTasks.keySet()){

			if (vehicle != initialVehicle) {
				CentralizedPlan neighbor = new CentralizedPlan(plan);
				LinkedList<CentralizedTask> tasksNeighbor = neighbor.planTasks.get(vehicle);
				tasksNeighbor.addFirst(movedDeliveryTask);
				tasksNeighbor.addFirst(movedPickupTask);
				neighbor.planTasks.put(vehicle, tasksNeighbor);
				if (neighbor.validConstraints()) {
					if (neighbor.planTasks.get(0).size() < 56)
					System.out.println(neighbor.planTasks.get(0).size() + " " +  neighbor.planTasks.get(1).size() + " " + neighbor.planTasks.get(2).size() + " "+ neighbor.planTasks.get(3).size() );
					neighbors.add(neighbor);
				}
			}
		}

		return neighbors;

	}
	
	public ArrayList<CentralizedPlan> changeOrder(CentralizedPlan plan, int selectedVehicle) {
		ArrayList<CentralizedPlan> neighbors = new ArrayList<CentralizedPlan>();
		CentralizedTask selectedTask = plan.planTasks.get(selectedVehicle).getFirst();
		CentralizedPlan newPlan = new CentralizedPlan(plan);
		LinkedList<CentralizedTask> taskList = newPlan.planTasks.get(selectedVehicle);
		int firstIndex = taskList.indexOf(selectedTask);
		CentralizedTask firstTask = taskList.remove(firstIndex);
		
		LinkedList<CentralizedTask> tasksCopy = new LinkedList<CentralizedTask>(newPlan.planTasks.get(selectedVehicle));
		CentralizedTask secondTask = null;
		for (CentralizedTask t : tasksCopy ) {
			if (t.task.id == firstTask.task.id && !t.equals(firstTask)) {
				int secondIndex = taskList.indexOf(t);
				secondTask = taskList.remove(secondIndex);
				break;
			}
		}
		newPlan.planTasks.put(selectedVehicle, taskList);
		for (int i= 0; i<taskList.size(); i++) {
			for (int j = i+1; j<taskList.size(); j++) {
				tasksCopy = new LinkedList<CentralizedTask>(newPlan.planTasks.get(selectedVehicle));
				CentralizedPlan neighbor = new CentralizedPlan(newPlan);
				tasksCopy.add(i, firstTask);
				tasksCopy.add(j, secondTask);
				neighbor.planTasks.put(selectedVehicle, tasksCopy);
				if (neighbor.validConstraints()) {
					neighbors.add(neighbor);
				}
			}
		}
		return neighbors;	
	}
	
	public ArrayList<CentralizedPlan> chooseNeighbors(CentralizedPlan plan) {
		Random r = new Random();
		int randomVehicle = r.nextInt(vehicles.size());
		//System.out.println(randomVehicle);
		ArrayList<CentralizedPlan> neighbors = new ArrayList<CentralizedPlan>();
		if (plan.planTasks.get(randomVehicle).size() > 0) {
			neighbors.addAll(changeVehicle(new CentralizedPlan(plan), randomVehicle));
			//neighbors.addAll(changeOrder(new CentralizedPlan(plan), randomVehicle));
			return neighbors;
		} else {
			return null;
		}	
	}
	
	public CentralizedPlan localChoice(CentralizedPlan oldPlan, ArrayList<CentralizedPlan> neighbors) {

		int bestCost = oldPlan.planCost();
		CentralizedPlan chosenPlan = oldPlan;
		for (CentralizedPlan neighbor : neighbors) {
        	System.out.println(neighbor.planTasks.get(0).size());

			if (neighbor.cost < bestCost) {
				System.out.println("MEJOR");
				System.out.println(neighbor.cost + " - " + bestCost);
				chosenPlan = neighbor;
				bestCost = neighbor.cost;
			}
		}
		
		Random r = new Random();
		int choice = r.nextInt(100);
		
		if (choice <= 35) {
			return chosenPlan;
		} else {
			return oldPlan;
		}
		
	}
	
}
