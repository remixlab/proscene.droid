/*******************************************************************************
 * TerseHandling (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.tersehandling.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import remixlab.tersehandling.event.TerseEvent;

/**
 * Every TerseHandling application should instantiate a single
 * TerseHandler object which is the high level package handler.
 * The handler holds a collection of {@link #agents()}, and an event
 * dispatcher queue of [terseEvent, grabber] tuples ({@link #eventTupleQueue()}).
 * Such tuple represents a message passing to application objects,
 * allowing an object to be instructed to perform a particular
 * user-defined action from a given TerseEvent.
 * <p>
 * A handler continuously runs the following two loops during runtime:
 * 1. Agent_i.handle(Agent_i.feed()), (see {@link remixlab.tersehandling.core.Agent#handle(TerseEvent)}
 * and {@link remixlab.tersehandling.core.Agent#feed()}); and 2. eventTupleQueue.remove().perform(),
 * an application object action callback.
 * 
 * @author pierre
 */
public class TerseHandler {
	// D E V I C E S & E V E N T S
	protected HashMap<String, Agent> agents;
	protected LinkedList<EventGrabberTuple> eventTupleQueue;
	
	public TerseHandler() {
		// agents
		agents = new HashMap<String, Agent>();
		// events
		eventTupleQueue = new LinkedList<EventGrabberTuple>();
	}

	/**
	 * This function should be called at the end of the main drawing loop.
	 */
	public void handle() {
		// 1. Agents
		for (Agent agent : agents.values())
			agent.handle(agent.feed());

		// 2. Low level events
		while (!eventTupleQueue.isEmpty())
			eventTupleQueue.remove().perform();
	}
	
	/**
	 * Returns a description of all registered agents' bindings and shortcuts
	 * as a String
	 */
	public String info() {
		String description = new String();
		description += "Agents' info\n";
		int index = 1;
		for( Agent agent : agents() ) {
			description += index;
			description += ". ";
			description += agent.info();
			index++;
		}
		return description;
	}

	/**
	 * Returns an array of the registered agents.
	 * 
	 * @see #agents()
	 */
	public Agent[] agentsArray() {
		return agents.values().toArray(new Agent[0]);
	}

	/**
	 * Returns a list of the registered agents.
	 * 
	 * @see #agentsArray()
	 */
	public List<Agent> agents() {
		List<Agent> list = new ArrayList<Agent>();
		for (Agent agent : agents.values())
			list.add(agent);

		return list;
	}

	/**
	 * Registers the given agent.
	 */
	public void registerAgent(Agent agent) {
		if (!isAgentRegistered(agent))
			agents.put(agent.name(), agent);
		else {
			System.out.println("Nothing done. An agent with the same name is already registered. Current agent names are:");
			for (Agent ag : agents.values())
				System.out.println(ag.name());
		}
	}

	public boolean isAgentRegistered(Agent agent) {
		return agents.containsKey(agent.name());
	}

	public boolean isAgentRegistered(String name) {
		return agents.containsKey(name);
	}

	public Agent agent(String name) {
		return agents.get(name);
	}

	/**
	 * Unregisters the given agent and returns it.
	 */
	public Agent unregisterAgent(Agent agent) {
		return agents.remove(agent.name());
	}

	/**
	 * Unregisters the given agent by its name and returns it.
	 */
	public Agent unregisterAgent(String name) {
		return agents.remove(name);
	}

	/**
	 * Unregisters all agents from the handler.
	 */
	public void unregisterAllAgents() {
		agents.clear();
	}

	/**
	 * Returns the event tuple queue. Rarely needed.
	 */
	public LinkedList<EventGrabberTuple> eventTupleQueue() {
		return eventTupleQueue;
	}

	public void enqueueEventTuple(EventGrabberTuple eventTuple) {
		if (!eventTupleQueue.contains(eventTuple))
			eventTuple.enqueue(eventTupleQueue);
	}

	/**
	 * Removes the given event from the event queue. No action
	 * is executed.
	 * 
	 * @param event to be removed.
	 */
	public void removeEventTuple(TerseEvent event) {
		eventTupleQueue.remove(event);
	}

	/**
	 * Clears the event queue. Nothing is executed.
	 */
	public void removeAllEventTuples() {
		eventTupleQueue.clear();
	}

	/**
	 * Returns {@code true} if the given {@code grabber} is in
	 * the {@code agent} pool and {@code false} otherwise.
	 */
	public boolean isInAgentPool(Grabbable grabber, Agent agent) {
		if (agent == null)
			return false;
		return agent.isInPool(grabber);
	}

	/**
	 * Adds {@code grabber} to the {@code agent} {@link remixlab.tersehandling.core.Agent#pool()}.
	 */
	public boolean addInAgentPool(Grabbable grabber, Agent agent) {
		if (agent == null)
			return false;
		return agent.addInPool(grabber);
	}

	/**
	 * Removes {@code grabber} from the {@code agent} {@link remixlab.tersehandling.core.Agent#pool()}.
	 */
	public boolean removeFromAgentPool(Grabbable grabber, Agent agent) {
		if (agent == null)
			return false;
		return agent.removeFromPool(grabber);
	}
	
	/**
	 * Clears the {@code agent} {@link remixlab.tersehandling.core.Agent#pool()}.
	 */
	public void clearAgentPool(Agent agent) {
		agent.clearPool();
	}

	/**
	 * Adds {@code grabber} into all registered agents.
	 */
	public void addInAllAgentPools(Grabbable grabber) {
		for (Agent agent : agents.values())
			if (!agent.isInPool(grabber))
				agent.addInPool(grabber);
	}

	/**
	 * Removes {@code grabber} from all registered agents.
	 */
	public void removeFromAllAgentPools(Grabbable grabber) {
		for (Agent agent : agents.values())
			agent.removeFromPool(grabber);
	}

	/**
	 * Clears all registered agent's {@link remixlab.tersehandling.core.Agent#pool()}.
	 */
	public void clearAllAgentPools() {
		for (Agent agent : agents.values())
			agent.clearPool();
	}

	/**
	 * Returns a list containing all Grabbable objects registered at all agents.
	 */
	public List<Grabbable> globalGrabberList() {
		List<Grabbable> msGrabberPool = new ArrayList<Grabbable>();
		for (Agent device : agents.values())
			for (Grabbable grabber : device.pool())
				if (!msGrabberPool.contains(grabber))
					msGrabberPool.add(grabber);

		return msGrabberPool;
	}
}
