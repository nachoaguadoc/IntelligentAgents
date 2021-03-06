package template;

import logist.topology.Topology.City;

/**
 * Action Class for the Reactive Agent
 * @author Ignacio Aguado, Darío Martínez
 */
public class ReactiveAction {
	
	public City destination;
	public boolean pickup;
	public String id;
	
	/**
	 * Initializer for the Pickup action
	 */
	public ReactiveAction() {
		this.pickup = true;
		this.destination = null;
		this.id = "pickup";
	}
	
	/**
	 * Initializer for a Move action
	 * 
	 * @param destination City to move 
	 */
	public ReactiveAction(City destination) {
		this.pickup = false;
		this.destination = destination;
		this.id = Integer.toString(destination.id);
	}
	
	/**
	 * @return True if the action is Pickup
	 */
	public boolean isPickup() {
		return this.pickup;
	}
}