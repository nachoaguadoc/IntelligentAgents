package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import logist.task.Task;

public class AuctionPlan {
	
	private AuctionVehicle biggestVehicle;
	private CentralizedPlan actualPlan;
	private CentralizedPlan bestPlan;	
	
	public AuctionPlan(List<AuctionVehicle> vehicles){
		double capacity = 0;
		this.actualPlan = new CentralizedPlan(vehicles);
		for(AuctionVehicle v : vehicles){
			if(capacity < v.getCapacity()){
				capacity = v.getCapacity();
				biggestVehicle = v;
			}
		}
	}
	
	public AuctionVehicle getBiggestVehicle(){
		return biggestVehicle;
	}
	
	public List<CentralizedPlan> insertTask(Task auctionedTask){
		List<CentralizedPlan> newPlanList = new ArrayList<CentralizedPlan>();
		AuctionTask auctionedPickUp = new AuctionTask("PICKUP", auctionedTask);
		AuctionTask auctionedDeliver = new AuctionTask("DELIVERY", auctionedTask);	
		for(Entry<Integer, LinkedList<AuctionTask>> entry : actualPlan.planTasks.entrySet()){
			LinkedList<AuctionTask> taskList = entry.getValue();
			for(int pos1=0; pos1<taskList.size()+1; pos1++){
				LinkedList <AuctionTask> newTaskList = new LinkedList<AuctionTask>(taskList);
				newTaskList.add(pos1, auctionedPickUp);
				for(int pos2=pos1+1; pos2<newTaskList.size()+1; pos2++){
					HashMap<Integer, LinkedList<AuctionTask>> newPlanTasks = actualPlan.cloneHashmap(actualPlan.planTasks);
					LinkedList <AuctionTask> newNewTaskList = new LinkedList<AuctionTask>(newTaskList);
					newNewTaskList.add(pos2, auctionedDeliver);
					newPlanTasks.put(entry.getKey(), newNewTaskList);
					CentralizedPlan  newCentralizedPlan = new CentralizedPlan(newPlanTasks, actualPlan.vehicles);
					if (newCentralizedPlan.validConstraints()) {
						newPlanList.add(newCentralizedPlan);
					}
				}
			}
		}
		return newPlanList;
	}
	
	private CentralizedPlan chooseBestPlan(CentralizedPlan oldPlan, List<CentralizedPlan> newPLanList){
		CentralizedPlan cheapest = oldPlan; 
		double minCost = Integer.MAX_VALUE;
		for(CentralizedPlan plan : newPLanList){
			double cost = plan.planCost();
			if(cost < minCost){
				cheapest = plan;
				minCost = cost;
			}
		}
		this.bestPlan = cheapest;
		return cheapest;
	}
	
	public CentralizedPlan getNewPlan(Task task){
		List<CentralizedPlan> newPlanList = insertTask(task);
		CentralizedPlan newBestPlan = chooseBestPlan(actualPlan, newPlanList);
		return newBestPlan;
	}
	
	public CentralizedPlan getBestPlan() {
		return bestPlan;
	}
	public void updatePlan(){
		actualPlan = bestPlan;
	}
	
}