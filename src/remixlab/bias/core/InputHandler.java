/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import remixlab.bias.event.BogusEvent;

/**
 * <h1>Introduction to BIAS</h1>
 * 
 * BIAS, (B)ogus-(I)nput (A)ction-(S)elector package. A package defining an interface between application event input
 * data (including but not limited to hardware input) and user-defined actions based on that input. The idea being that
 * various sorts of input data, mainly that gathered from an user-interaction (e.g., a mouse button being pressed and
 * dragged), may be modeled and reduced into high-level events. Those "bogus" events are then taken as input to
 * implement user-defined actions on application objects (e.g., push that button or select that geometry on the screen
 * and move it close to me).
 * 
 * <h2>Targeted applications</h2>
 * 
 * Depending on whether or not the user define his own set of actions, targeted applications are:
 * 
 * <h3>Action-less applications</h3>
 * 
 * Action-less applications simple require to reduce input data into a raw {@link remixlab.bias.event.BogusEvent}.
 * 
 * <h3>Action-driven applications</h3>
 * 
 * In this case, the targeted applications for the package are those able to:
 * <p>
 * <ol>
 * <li>Itemize the application functionality into a list of actions (see {@link remixlab.bias.core.Action}).</li>
 * <li>Reduce input data into a {@link remixlab.bias.event.BogusEvent} and characterize it with a
 * {@link remixlab.bias.event.shortcut.Shortcut} (which are used to bind the user-defined
 * {@link remixlab.bias.core.Action}s.</li>
 * <li>Implement each action item taking as input those (reduced) BogusEvents (see {@link remixlab.bias.core.Grabbable}
 * and {@link remixlab.bias.core.Grabbable#performInteraction(BogusEvent)} ).</li>
 * </ol>
 * 
 * <p>
 * 
 * <b>Observation</b> Third parties may not always need to implement their own {@link remixlab.bias.event.BogusEvent}s
 * but simply use (depart from) those already conveniently provided here:
 * 
 * <ol>
 * <li>{@link remixlab.bias.event.KeyboardEvent}, representing any keyboard.</li>
 * <li>{@link remixlab.bias.event.ClickEvent} which stands for a button clicked.</li>
 * <li>{@link remixlab.bias.event.MotionEvent} which represents data gathered from user motion, e.g., the user moves her
 * hand in front of a kinect, or a finger is being dragged on a touch screen surface. MotionEvents were modeled
 * according to their <a
 * href="http://en.wikipedia.org/wiki/Degrees_of_freedom_(mechanics)">"degrees-of-freedom (DOFs)"</a> (see
 * {@link remixlab.bias.event.DOF1Event}, {@link remixlab.bias.event.DOF2Event}, {@link remixlab.bias.event.DOF3Event}
 * and {@link remixlab.bias.event.DOF6Event}), not only because they (DOF's) represent a nice property to classify input
 * devices, but mainly because manipulating stuff on 3D may be performed differently given events carrying different
 * DOF's. Intuitively, the greater the DOF's the richer the user experience may be.</li>
 * </ol>
 * 
 * These default bogus-event set should serve as a common ground to all sorts of tangible interfaces manipulating
 * geometry on a 2D/3D space.
 * 
 * <h2>Usage</h2>
 * Usage is simple:
 * <ol>
 * <li>Instantiate an InputHandler.</li>
 * <li>Define your bogus events.</li>
 * <li>Define/implement some {@code remixlab.bias.core.Agent}(s) capable of dealing with your events and register them
 * at the handler ({@link #registerAgent(Agent)}).</li>
 * <li>Action-driven applications should additionally implement user-defined actions (
 * {@link remixlab.bias.core.Grabbable#performInteraction(BogusEvent)}). In this case, to customize the user experience
 * simply bind bogus event {@link remixlab.bias.event.shortcut.Shortcut}s (
 * {@link remixlab.bias.event.BogusEvent#shortcut()}) to user-defined actions using the Agent
 * {@link remixlab.bias.generic.profile.Profile}(s).</li>
 * <li>Attach a call to {@link #handle()} at the end of your main event (drawing) loop.</li>
 * </ol>
 * 
 * <h1>The InputHandler Class</h1>
 * 
 * The InputHandler object is the high level package handler which holds a collection of {@link #agents()}, and an event
 * dispatcher queue of {@link remixlab.bias.core.EventGrabberTuple}s ({@link #eventTupleQueue()}). Such tuple represents
 * a message passing to application objects, allowing an object to be instructed to perform a particular user-defined
 * {@link remixlab.bias.core.Action} from a given {@link remixlab.bias.event.BogusEvent}.
 * <p>
 * At runtime, the input handler should continuously run the two loops defined in {@link #handle()}. Therefore, simply
 * attach a call to {@link #handle()} at the end of your main event (drawing) loop for that to take effect (like it's
 * done in </b>dandelion</b> by the <b>AbstractScene.postdraw()</b> method).
 */
public class InputHandler {
	// D E V I C E S & E V E N T S
	protected HashMap<String, Agent>				agents;
	protected LinkedList<EventGrabberTuple>	eventTupleQueue;

	public InputHandler() {
		// agents
		agents = new HashMap<String, Agent>();
		// events
		eventTupleQueue = new LinkedList<EventGrabberTuple>();
	}

	/**
	 * Main handler method. Call it at the end of your main event (drawing) loop (like it's done in </b>dandelion</b> by
	 * the <b>AbstractScene.postdraw()</b> method)
	 * <p>
	 * The handle comprises the following two loops:
	 * <p>
	 * 1. {@link remixlab.bias.core.EventGrabberTuple} producer loop which for each registered agent calls:
	 * {@link remixlab.bias.core.Agent#handle(BogusEvent)}. Note that the bogus event is obtained from the agents callback
	 * {@link remixlab.bias.core.Agent#feed()} method.<br>
	 * 2. User-defined action consumer loop: which for each {@link remixlab.bias.core.EventGrabberTuple} calls
	 * {@link remixlab.bias.core.EventGrabberTuple#perform()}.<br>
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
	 * Returns a description of all registered agents' bindings and shortcuts as a String
	 */
	public String info() {
		String description = new String();
		description += "Agents' info\n";
		int index = 1;
		for (Agent agent : agents()) {
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

	/**
	 * Returns true if the given agent is registered.
	 */
	public boolean isAgentRegistered(Agent agent) {
		return agents.containsKey(agent.name());
	}

	/**
	 * Returns true if the agent (given by its name) is registered.
	 */
	public boolean isAgentRegistered(String name) {
		return agents.containsKey(name);
	}

	/**
	 * Returns the agent by its name. The agent mus be {@link #isAgentRegistered(Agent)}.
	 */
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

	/**
	 * Enqueues the eventTuple for later execution which happens at the end of {@link #handle()}.
	 * 
	 * @see #handle()
	 */
	public void enqueueEventTuple(EventGrabberTuple eventTuple) {
		if (!eventTupleQueue.contains(eventTuple))
			eventTuple.enqueue(eventTupleQueue);
	}

	/**
	 * Removes the given event from the event queue. No action is executed.
	 * 
	 * @param event
	 *          to be removed.
	 */
	public void removeEventTuple(BogusEvent event) {
		eventTupleQueue.remove(event);
	}

	/**
	 * Clears the event queue. Nothing is executed.
	 */
	public void removeAllEventTuples() {
		eventTupleQueue.clear();
	}

	/**
	 * Returns {@code true} if the given {@code grabber} is in the {@code agent} pool and {@code false} otherwise.
	 */
	public boolean isInAgentPool(Grabbable grabber, Agent agent) {
		if (agent == null)
			return false;
		return agent.isInPool(grabber);
	}

	/**
	 * Adds {@code grabber} to the {@code agent} {@link remixlab.bias.core.Agent#pool()}.
	 */
	public boolean addInAgentPool(Grabbable grabber, Agent agent) {
		if (agent == null)
			return false;
		return agent.addInPool(grabber);
	}

	/**
	 * Removes {@code grabber} from the {@code agent} {@link remixlab.bias.core.Agent#pool()}.
	 */
	public boolean removeFromAgentPool(Grabbable grabber, Agent agent) {
		if (agent == null)
			return false;
		return agent.removeFromPool(grabber);
	}

	/**
	 * Clears the {@code agent} {@link remixlab.bias.core.Agent#pool()}.
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
	 * Clears all registered agent's {@link remixlab.bias.core.Agent#pool()}.
	 */
	public void clearAllAgentPools() {
		for (Agent agent : agents.values())
			agent.clearPool();
	}

	/**
	 * Returns a list containing all Grabber objects registered at all agents.
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
