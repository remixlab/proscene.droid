/*********************************************************************************
 * TerseHandling
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.tersehandling.generic.agent;

import java.util.LinkedList;

import remixlab.tersehandling.core.Action;
import remixlab.tersehandling.core.Agent;
import remixlab.tersehandling.core.EventGrabberTuple;
import remixlab.tersehandling.core.GenericEvent;
import remixlab.tersehandling.core.Grabbable;
import remixlab.tersehandling.core.TerseHandler;
import remixlab.tersehandling.event.TerseEvent;
import remixlab.tersehandling.generic.profile.GenericProfile;

/**
 * A GenericAgent is just a parameterized {@link remixlab.tersehandling.core.Agent} with a
 * {@link remixlab.tersehandling.generic.profile.GenericProfile}. We use a single profile attribute ({@link #profile()})
 * to define {@link remixlab.tersehandling.event.shortcut.Shortcut} -> {@link remixlab.tersehandling.core.Action}
 * mappings. These mappings provide means to parse a generic {@link remixlab.tersehandling.event.TerseEvent} into an
 * user-defined action ({@link #handle(TerseEvent)}).
 * 
 * @param <P>
 *          {@link remixlab.tersehandling.generic.profile.GenericProfile} to parameterize the Agent with.
 */
public class GenericAgent<P extends GenericProfile<?, ?>> extends Agent {
	/**
	 * Internal class that extends {@link remixlab.tersehandling.core.EventGrabberTuple} to be able to deal with
	 * user-defined actions.
	 */
	public class GenericEventGrabberTuple extends EventGrabberTuple {

		/**
		 * @param e
		 *          {@link remixlab.tersehandling.event.TerseEvent}
		 * @param a
		 *          {@link remixlab.tersehandling.core.Action}
		 * @param g
		 *          {@link remixlab.tersehandling.core.Grabbable}
		 */
		public GenericEventGrabberTuple(TerseEvent e, Action<?> a, Grabbable g) {
			super(e, g);
			if (event instanceof GenericEvent)
				((GenericEvent<?>) event).setAction(a);
			else
				System.out.println("Action will not be handled by grabber using this event type. Supply a Duoble event");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see remixlab.tersehandling.core.EventGrabberTuple#enqueue(java.util.LinkedList)
		 */
		@Override
		public boolean enqueue(LinkedList<EventGrabberTuple> queue) {
			if (event().isNull())
				return false;
			if (event instanceof GenericEvent) {
				if (((GenericEvent<?>) event).action() != null) {
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
	 *          {@link remixlab.tersehandling.generic.profile.GenericProfile}
	 * @param tHandler
	 *          {@link remixlab.tersehandling.core.TerseHandler} to register this Agent to
	 * @param n
	 *          Agent name
	 */
	public GenericAgent(P p, TerseHandler tHandler, String n) {
		super(tHandler, n);
		profile = p;
	}

	/**
	 * @return the agents {@link remixlab.tersehandling.generic.profile.GenericProfile} instance.
	 */
	public P profile() {
		return profile;
	}

	/**
	 * Sets the {@link remixlab.tersehandling.generic.profile.GenericProfile}
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
	 * @see remixlab.tersehandling.core.Agent#info()
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
	 * Overriding of the {@link remixlab.tersehandling.core.Agent} main method. Here we use the {@link #profile()} to
	 * parse the event into an user-defined action which is then enqueued as an event-grabber tuple (
	 * {@link #enqueueEventTuple(EventGrabberTuple)}). That tuple is used to instruct a {@link #grabber()} the
	 * user-defined action to perform.
	 */
	@Override
	public void handle(TerseEvent event) {
		// overkill but feels safer ;)
		if (event == null || !handler.isAgentRegistered(this) || grabber() == null)
			return;
		if (event instanceof GenericEvent<?>)
			if (foreignGrabber())
				enqueueEventTuple(new EventGrabberTuple(event, grabber()));
			else
				enqueueEventTuple(new GenericEventGrabberTuple(event, profile().handle((GenericEvent<?>) event), grabber()));
	}
}
