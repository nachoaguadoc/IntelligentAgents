package template;

import logist.task.Task;
import logist.topology.Topology.City;

public class AuctionTask {
	public boolean pickup;
	public boolean delivery;
	public City pickupCity;
	public City deliveryCity;
	public Task task;
	public long reward;
	public int weight;
	public String id;
	
	/**
	 * Initializer for a Pickup or Deliver task
	 * 
	 * @param: type: Pickup or Delivery
	 * @param: task to be picked up or delivered
	 */
	public AuctionTask(String type, Task task) {
		if (type.equals("PICKUP")) {
			this.pickup = true;
			this.delivery = false;
			this.pickupCity = task.pickupCity;
			this.task = task;
			this.weight = task.weight;
			this.id = "PICKUP-" + task.pickupCity.name;
		} else if (type.equals("DELIVERY")) {
			this.pickup = false;
			this.delivery = true;
			this.deliveryCity = task.deliveryCity;
			this.task = task;
			this.reward = task.reward;
			this.weight = task.weight;
			this.id = "DELIVERY-" + task.deliveryCity.name;
		}

	}
}