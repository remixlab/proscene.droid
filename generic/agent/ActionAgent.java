/*********************************************************************************
 * bogusinput_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.bogusinput.generic.agent;

import java.util.LinkedList;

import remixlab.bogusinput.core.Action;
import remixlab.bogusinput.core.Agent;
import remixlab.bogusinput.core.EventGrabberTuple;
import remixlab.bogusinput.core.Grabbable;
import remixlab.bogusinput.core.InputHandler;
import remixlab.bogusinput.event.BogusEvent;
import remixlab.bogusinput.generic.event.ActionBogusEvent;
import remixlab.bogusinput.generic.profile.Profile;

/**
 * An ActionAgent is just an {@link remixlab.bogusinput.core.Agent} holding some
 * {@link remixlab.bogusinput.generic.profile.Profile}s. The Agent uses the
 * {@link remixlab.bogusinput.event.shortcut.Shortcut} -> {@link remixlab.bogusinput.core.Action} mappings defined by
 * each of its Profiles to parse the {@link remixlab.bogusinput.generic.event.ActionBogusEvent} into an user-defined
 * {@link remixlab.bogusinput.core.Action} (see {@link #handle(BogusEvent)}).
 * <p>
 * The default implementation here holds only a single {@link remixlab.bogusinput.generic.profile.Profile} (see
 * {@link #profile()}) attribute (note that we use the type of the Profile to parameterize the ActionAgent). Different
 * profile groups are provided by the {@link remixlab.bogusinput.generic.agent.ActionMotionAgent}, the
 * {@link remixlab.bogusinput.generic.agent.ActionWheeledMotionAgent} and the
 * {@link remixlab.bogusinput.generic.agent.ActionKeyboardAgent} specializations, which roughly represent an HIDevice
 * (like a kinect), a wheeled HIDevice (like a mouse) and a generic keyboard, respectively.
 * <p>
 * Third-parties implementations should "simply": 1. Derive from the ActionAgent above that best fits their needs; 2.
 * Supply a routine to reduce application-specific input data into BogusEvents (given them thier name); and, 3. Properly
 * call {@link #updateGrabber(BogusEvent)} and {@link #handle(BogusEvent)} on them. The
 * <b>remixlab.proscene.Scene.ProsceneMouse</b> and <b>remixlab.proscene.Scene.ProsceneKeyboard</b> classes provide good
 * example implementations. Note that the ActionAgent methods defined in this package (bogusinput) should rarely be in
 * need to be overridden, not even {@link #handle(BogusEvent)}.
 * 
 * @param <P>
 *          {@link remixlab.bogusinput.generic.profile.Profile} to parameterize the Agent with.
 */
public class ActionAgent<P extends Profile<?, ?>> extends Agent {
	/**
	 * Internal class that extends {@link remixlab.bogusinput.core.EventGrabberTuple} to be able to deal with user-defined
	 * actions.
	 */
	public class ActionEventGrabberTuple extends EventGrabberTuple {

		/**
		 * @param e
		 *          {@link remixlab.bogusinput.event.BogusEvent}
		 * @param a
		 *          {@link remixlab.bogusinput.core.Action}
		 * @param g
		 *          {@link remixlab.bogusinput.core.Grabbable}
		 */
		public ActionEventGrabberTuple(BogusEvent e, Action<?> a, Grabbable g) {
			super(e, g);
			if (event instanceof ActionBogusEvent)
				((ActionBogusEvent<?>) event).setAction(a);
			else
				System.out.println("Action will not be handled by grabber using this event type. Supply a Duoble event");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see remixlab.bogusinput.core.EventGrabberTuple#enqueue(java.util.LinkedList)
		 */
		@Override
		public boolean enqueue(LinkedList<EventGrabberTuple> queue) {
			if (event().isNull())
				return false;
			if (event instanceof ActionBogusEvent) {
				if (((ActionBogusEvent<?>) event).action() != null) {
					queue.add(this);
					return true;
				}
				else
					return false;
			}
			else
				return super.enqueue(queue);
		}
	}

	protected P profile;

	/**
	 * @param p
	 *          {@link remixlab.bogusinput.generic.profile.Profile}
	 * @param tHandler
	 *          {@link remixlab.bogusinput.core.InputHandler} to register this Agent to
	 * @param n
	 *          Agent name
	 */
	public ActionAgent(P p, InputHandler tHandler, String n) {
		super(tHandler, n);
		profile = p;
	}

	/**
	 * @return the agents {@link remixlab.bogusinput.generic.profile.Profile} instance.
	 */
	public P profile() {
		return profile;
	}

	/**
	 * Sets the {@link remixlab.bogusinput.generic.profile.Profile}
	 * 
	 * @param p
	 */
	public void setProfile(P p) {
		profile = p;
	}

	/**
	 * Returns false. More interesting things may happen in derived classes.
	 */
	protected boolean foreignGrabber() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bogusinput.core.Agent#info()
	 */
	@Override
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if (profile().description().length() != 0) {
			description += "Shortcuts\n";
			description += profile().description();
		}
		return description;
	}

	/**
	 * Overriding of the {@link remixlab.bogusinput.core.Agent} main method. Here we use the {@link #profile()} to parse
	 * the event into an user-defined action which is then enqueued as an event-grabber tuple (
	 * {@link #enqueueEventTuple(EventGrabberTuple)}). That tuple is used to instruct a {@link #grabber()} the
	 * user-defined action to perform.
	 * 
	 * <b>Note:</b> This method should only be overridden in the rare case a custom set of rules its needed to select
	 * which Profile should parse the given bogus event.
	 */
	@Override
	public void handle(BogusEvent event) {
		// overkill but feels safer ;)
		if (event == null || !handler.isAgentRegistered(this) || grabber() == null)
			return;
		if (event instanceof ActionBogusEvent<?>)
			if (foreignGrabber())
				enqueueEventTuple(new EventGrabberTuple(event, grabber()));
			else
				enqueueEventTuple(new ActionEventGrabberTuple(event, profile().handle((ActionBogusEvent<?>) event), grabber()));
	}
}
