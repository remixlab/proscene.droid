/*********************************************************************************
 * TerseHandling
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.tersehandling.core;

import java.util.ArrayList;
import java.util.List;

import remixlab.tersehandling.event.TerseEvent;

/**
 * An Agent is a high-level {@link remixlab.tersehandling.event.TerseEvent} parser, which holds a {@link #pool()} of
 * grabbers: application objects implementing actions to be triggered from a
 * {@link remixlab.tersehandling.event.TerseEvent} (by means of the {@link remixlab.tersehandling.core.Grabbable}
 * interface).
 * <p>
 * The agent also holds a {@link #grabber()} which is the object in the {@link #pool()} that grabs input at a given time:
 * the object to which the agent transmits events, specifically when {@link #handle(TerseEvent)} is called (which is
 * done every frame by the {@link #terseHandler()} this agent is register to).
 * <p>
 * The agent's {@link #grabber()} may be set by querying the pool with {@link #updateGrabber(TerseEvent)}. Each object
 * in the pool will then check if the {@link remixlab.tersehandling.core.Grabbable#checkIfGrabsInput(TerseEvent)})
 * condition is met. Note that the first object meeting the condition will be set as the {@link #grabber()} and that it
 * may be null if no object meets it. A {@link #grabber()} may also be enforced simply with
 * {@link #setDefaultGrabber(Grabbable)}.
 * <p>
 * There are non-generic and generic agents. Non-generic agents simply act as a channel between non-generic terse events
 * and grabbers. In this case, the agent simply transmits TerseEvent (reduced input event) to its {@link #grabber()}.
 * <p>
 * More specialized, generic, agents also hold Profiles, each containing a mapping between TerseEvent shortcuts and
 * user-defined actions. Hence, thanks to its Profiles, generic agents further parse TerseEvents to determine the
 * user-defined action its input grabber should perform, which is done by explicitly invoking
 * {@link #handle(TerseEvent)}.
 */
public class Agent {
	protected TerseHandler handler;
	protected String nm;
	protected List<Grabbable> grabbers;
	protected Grabbable trackedGrabber;
	protected Grabbable defaultGrabber;
	protected boolean agentTrckn;

	/**
	 * Constructs an Agent with the given name and registers is at the given terseHandler.
	 */
	public Agent(TerseHandler terseHandler, String name) {
		handler = terseHandler;
		nm = name;
		grabbers = new ArrayList<Grabbable>();
		setTracking(true);
		handler.registerAgent(this);
	}

	/**
	 * @return Agents name
	 */
	public String name() {
		return nm;
	}

	/**
	 * Returns {@code true} if this agent is tracking its grabbers.
	 * <p>
	 * You may need to {@link #enableTracking()} first.
	 */
	public boolean isTracking() {
		return agentTrckn;
	}

	/**
	 * Enables tracking so that the {@link #grabber()} may be updated when calling {@link #updateGrabber(TerseEvent)}.
	 * 
	 * @see #disableTracking()
	 */
	public void enableTracking() {
		setTracking(true);
	}

	/**
	 * Disables tracking.
	 * 
	 * @see #enableTracking()
	 */
	public void disableTracking() {
		setTracking(false);
	}

	/**
	 * Sets the {@link #isTracking()} value.
	 */
	public void setTracking(boolean enable) {
		agentTrckn = enable;
		if (!isTracking())
			setTrackedGrabber(null);
	}

	/**
	 * Calls {@link #setTracking(boolean)} to toggle the {@link #isTracking()} value.
	 */
	public void toggleTracking() {
		setTracking(!isTracking());
	}

	/**
	 * If {@link #isTracking()} is enabled and the agent is registered at the {@link #terseHandler()} then queries each
	 * object in the {@link #pool()} to check if the
	 * {@link remixlab.tersehandling.core.Grabbable#checkIfGrabsInput(TerseEvent)}) condition is met. The first object
	 * meeting the condition will be set as the {@link #grabber()} and returned. Note that a null grabber means that no
	 * object in the {@link #pool()} met the condition. A {@link #grabber()} may also be enforced simply with
	 * {@link #setDefaultGrabber(Grabbable)}.
	 * <p>
	 * <b>Note</b> you don't have to call this method since the {@link #terseHandler()} handler does it automatically
	 * every frame.
	 * 
	 * @param event
	 *          to query the {@link #pool()}
	 * @return the new grabber which may be null.
	 * 
	 * @see #setDefaultGrabber(Grabbable)
	 * @see #isTracking()
	 */
	public Grabbable updateGrabber(TerseEvent event) {
		if (event == null || !terseHandler().isAgentRegistered(this) || !isTracking())
			return trackedGrabber();

		Grabbable g = trackedGrabber();

		// We first check if tracked grabber remains the same
		if (g != null)
			if (g.checkIfGrabsInput(event))
				return trackedGrabber();

		setTrackedGrabber(null);
		for (Grabbable mg : pool()) {
			// take whatever. Here the first one
			if (mg.checkIfGrabsInput(event)) {
				setTrackedGrabber(mg);
				return trackedGrabber();
			}
		}
		return trackedGrabber();
	}

	/**
	 * Calls {@link remixlab.tersehandling.core.TerseHandler#enqueueEventTuple(EventGrabberTuple)} to enqueue the
	 * {@link remixlab.tersehandling.core.EventGrabberTuple} for later execution.
	 * <p>
	 * <b>Note</b> that this method is automatically called by {@link #handle(TerseEvent)}.
	 * 
	 * @see #handle(TerseEvent)
	 */
	public void enqueueEventTuple(EventGrabberTuple eventTuple) {
		if (eventTuple != null && handler.isAgentRegistered(this))
			terseHandler().enqueueEventTuple(eventTuple);
	}

	/**
	 * Returns a detailed description of this Agent as a String.
	 */
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		description += "Nothing to be said, except that generic Agents hold more interesting info\n";
		return description;
	}

	/**
	 * Main agent method. Generic agents (like this one) simply call
	 * {@code terseHandler().enqueueEventTuple(new EventGrabberTuple(event, grabber()))}.
	 * <p>
	 * Non-generic agents parse the TerseEvent to determine the* user-defined action the {@link #grabber()} should
	 * perform.
	 * <p>
	 * <b>Note</b> that the agent must be registered at the {@link #terseHandler()} for this method to take effect.
	 * 
	 * @see #grabber()
	 */
	public void handle(TerseEvent event) {
		if (event == null || !handler.isAgentRegistered(this)
						|| grabber() == null)
			return;
		terseHandler().enqueueEventTuple(new EventGrabberTuple(event, grabber()));
	}

	/**
	 * User space event reduction method. This method should return a TerseEvent from the actual hardware input event,
	 * i.e., reduces (hardware) input events into (virtual) TerseEvents.
	 * <p>
	 * <b>Note</b> that depending on the type of input and how it's handled by the application, its reduction into a
	 * TerseEvent could happened in several different ways. For instance, in Java it should take place within the
	 * implementation of a mouse listener interface. Similarly, in Processing, it should take place when registering the
	 * mouseEvent method at the PApplet. The {@link #feed()} is an alternative way when none of these mechanism is
	 * available, as it use to happen when dealing with specialized, non-default input hardware.
	 * <p>
	 * See the Space Navigator example.
	 * <p>
	 * <b>Note</b> that this method is automatically called by {@link remixlab.tersehandling.core.TerseHandler#handle()}
	 * 
	 * @see remixlab.tersehandling.core.TerseHandler#handle()
	 */
	public TerseEvent feed() {
		return null;
	}

	/**
	 * Returns the {@link remixlab.tersehandling.core.TerseHandler} this agent is registered to.
	 */
	public TerseHandler terseHandler() {
		return handler;
	}

	/**
	 * Returns a list containing references to all the active grabbers.
	 * <p>
	 * Used to parse all the grabbers and to check if any of them
	 * {@link remixlab.tersehandling.core.Grabbable#grabsAgent(Agent)}.
	 */
	public List<Grabbable> pool() {
		return grabbers;
	}

	/**
	 * Removes the grabber from the {@link #pool()}.
	 * <p>
	 * See {@link #addInPool(Grabbable)} for details. Removing a grabber that is not in {@link #pool()} has no effect.
	 */
	public boolean removeFromPool(Grabbable deviceGrabber) {
		return pool().remove(deviceGrabber);
	}

	/**
	 * Clears the {@link #pool()}.
	 * <p>
	 * Use this method only if it is faster to clear the {@link #pool()} and then to add back a few grabbers than to
	 * remove each one independently.
	 */
	public void clearPool() {
		pool().clear();
	}

	/**
	 * Returns true if the grabber is currently in the agents {@link #pool()} list.
	 * <p>
	 * When set to false using {@link #removeFromPool(Grabbable)}, the handler no longer
	 * {@link remixlab.tersehandling.core.Grabbable#checkIfGrabsInput(TerseEvent)} on this grabber. Use
	 * {@link #addInPool(Grabbable)} to insert it * back.
	 */
	public boolean isInPool(Grabbable deviceGrabber) {
		return pool().contains(deviceGrabber);
	}

	/**
	 * Returns the grabber set after {@link #updateGrabber(TerseEvent)} is called. It may be null.
	 */
	public Grabbable trackedGrabber() {
		return trackedGrabber;
	}

	/**
	 * If {@link #trackedGrabber()} is non null, returns it. Otherwise returns the {@link #defaultGrabber()}.
	 * 
	 * @see #trackedGrabber()
	 */
	public Grabbable grabber() {
		if (trackedGrabber() != null)
			return trackedGrabber();
		else
			return defaultGrabber();
	}

	/**
	 * Default {@link #grabber()} returned when {@link #trackedGrabber()} is null and set with
	 * {@link #setDefaultGrabber(Grabbable)}.
	 * 
	 * @see #grabber()
	 * @see #trackedGrabber()
	 */
	public Grabbable defaultGrabber() {
		return defaultGrabber;
	}

	/**
	 * Sets the {@link #defaultGrabber()}
	 * 
	 * {@link #grabber()}
	 */
	public void setDefaultGrabber(Grabbable g) {
		defaultGrabber = g;
	}

	/**
	 * Adds the grabber in the {@link #pool()}.
	 * <p>
	 * Use {@link #removeFromPool(Grabbable)} to remove the grabber from the pool, so that it is no longer tested with
	 * {@link remixlab.tersehandling.core.Grabbable#checkIfGrabsInput(TerseEvent)} by the handler, and hence can no longer
	 * grab the agent focus. Use {@link #isInPool(Grabbable)} to know the current state of the grabber.
	 */
	public boolean addInPool(Grabbable deviceGrabber) {
		if (deviceGrabber == null)
			return false;
		if (!isInPool(deviceGrabber)) {
			pool().add(deviceGrabber);
			return true;
		}
		return false;
	}

	/**
	 * Internal use
	 */
	private void setTrackedGrabber(Grabbable deviceGrabber) {
		if (deviceGrabber == null) {
			trackedGrabber = null;
		} else if (isInPool(deviceGrabber)) {
			trackedGrabber = deviceGrabber;
		}
	}
}
