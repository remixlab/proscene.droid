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
import java.util.List;

import remixlab.bias.event.BogusEvent;

/**
 * An Agent is a high-level {@link remixlab.bias.event.BogusEvent} parser, which holds a {@link #pool()} of grabbers:
 * application objects implementing (user-defined) actions. The agent also holds a {@link #grabber()} which is the
 * object in the {@link #pool()} that grabs input at a given time, i.e., the targeted object in the call
 * 
 * @link #handle(BogusEvent)}. It's worth noting that {@link #inputHandler()} does it every-frame.
 *       <p>
 *       The agent's {@link #grabber()} may be set by querying the pool with {@link #updateGrabber(BogusEvent)}. Each
 *       object in the pool will then check if the {@link remixlab.bias.core.Grabbable#checkIfGrabsInput(BogusEvent)})
 *       condition is met. Note that the first object meeting the condition will be set as the {@link #grabber()} and
 *       that it may be null if no object meets it. A {@link #grabber()} may also simply be enforced with
 *       {@link #setDefaultGrabber(Grabbable)}.
 *       <p>
 *       There are non-generic and generic agents. Non-generic agents simply act as a channel between bogus events and
 *       grabbers. In this case, the agent simply transmits the (raw) bogus event to its {@link #grabber()}. More
 *       specialized, generic, agents also hold {@link remixlab.bias.generic.profile.Profile}s, each containing a
 *       mapping between bogus event shortcuts and user-defined actions. Hence, generic agents further parse bogus
 *       events to determine the user-defined action the {@link #grabber()} should perform (see
 *       {@link #handle(BogusEvent)}).
 */
public class Agent {
	protected InputHandler		handler;
	protected String					nm;
	protected List<Grabbable>	grabbers;
	protected Grabbable				trackedGrabber;
	protected Grabbable				defaultGrabber;
	protected boolean					agentTrckn;

	/**
	 * Constructs an Agent with the given name and registers is at the given inputHandler.
	 */
	public Agent(InputHandler inputHandler, String name) {
		handler = inputHandler;
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
	 * Enables tracking so that the {@link #grabber()} may be updated when calling {@link #updateGrabber(BogusEvent)}.
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
	 * If {@link #isTracking()} is enabled and the agent is registered at the {@link #inputHandler()} then queries each
	 * object in the {@link #pool()} to check if the {@link remixlab.bias.core.Grabbable#checkIfGrabsInput(BogusEvent)})
	 * condition is met. The first object meeting the condition will be set as the {@link #grabber()} and returned. Note
	 * that a null grabber means that no object in the {@link #pool()} met the condition. A {@link #grabber()} may also be
	 * enforced simply with {@link #setDefaultGrabber(Grabbable)}.
	 * <p>
	 * <b>Note</b> you don't have to call this method since the {@link #inputHandler()} handler does it automatically
	 * every frame.
	 * 
	 * @param event
	 *          to query the {@link #pool()}
	 * @return the new grabber which may be null.
	 * 
	 * @see #setDefaultGrabber(Grabbable)
	 * @see #isTracking()
	 */
	public Grabbable updateGrabber(BogusEvent event) {
		if (event == null || !inputHandler().isAgentRegistered(this) || !isTracking())
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
	 * Calls {@link remixlab.bias.core.InputHandler#enqueueEventTuple(EventGrabberTuple)} to enqueue the
	 * {@link remixlab.bias.core.EventGrabberTuple} for later execution.
	 * <p>
	 * <b>Note</b> that this method is automatically called by {@link #handle(BogusEvent)}.
	 * 
	 * @see #handle(BogusEvent)
	 */
	public void enqueueEventTuple(EventGrabberTuple eventTuple) {
		if (eventTuple != null && handler.isAgentRegistered(this))
			inputHandler().enqueueEventTuple(eventTuple);
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
	 * Main agent method. Non-generic agents (like this one) simply call
	 * {@code inputHandler().enqueueEventTuple(new EventGrabberTuple(event, grabber()))}.
	 * <p>
	 * Non-generic agents parse the action bogus event to determine the user-defined action the {@link #grabber()} should
	 * perform.
	 * <p>
	 * <b>Note</b> that the agent must be registered at the {@link #inputHandler()} for this method to take effect.
	 * 
	 * @see #grabber()
	 */
	public void handle(BogusEvent event) {
		if (event == null || !handler.isAgentRegistered(this)
				|| grabber() == null)
			return;
		inputHandler().enqueueEventTuple(new EventGrabberTuple(event, grabber()));
	}

	/**
	 * Callback (user-space) event reduction routine. Obtains data from the outside world and returns an BogusEvent i.e.,
	 * reduces external data into an BogusEvent. Automatically call by the main event loop (
	 * {@link remixlab.bias.core.InputHandler#handle()}).
	 * <p>
	 * See the Space Navigator example.
	 * 
	 * @see remixlab.bias.core.InputHandler#handle()
	 */
	public BogusEvent feed() {
		return null;
	}

	/**
	 * Returns the {@link remixlab.bias.core.InputHandler} this agent is registered to.
	 */
	public InputHandler inputHandler() {
		return handler;
	}

	/**
	 * Returns a list containing references to all the active grabbers.
	 * <p>
	 * Used to parse all the grabbers and to check if any of them {@link remixlab.bias.core.Grabbable#grabsAgent(Agent)}.
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
	 * {@link remixlab.bias.core.Grabbable#checkIfGrabsInput(BogusEvent)} on this grabber. Use
	 * {@link #addInPool(Grabbable)} to insert it * back.
	 */
	public boolean isInPool(Grabbable deviceGrabber) {
		return pool().contains(deviceGrabber);
	}

	/**
	 * Returns the grabber set after {@link #updateGrabber(BogusEvent)} is called. It may be null.
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
	 * {@link remixlab.bias.core.Grabbable#checkIfGrabsInput(BogusEvent)} by the handler, and hence can no longer grab the
	 * agent focus. Use {@link #isInPool(Grabbable)} to know the current state of the grabber.
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
