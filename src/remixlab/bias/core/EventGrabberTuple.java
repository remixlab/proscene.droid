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

import java.util.LinkedList;

import remixlab.bias.event.BogusEvent;

/**
 * [{@link remixlab.bias.event.BogusEvent},{@link remixlab.bias.core.Grabber}] tuples which encapsulate message passing
 * from {@link remixlab.bias.event.BogusEvent} to {@link remixlab.bias.core.Grabber} to perform actions.
 */
public class EventGrabberTuple {
	protected BogusEvent	event;
	protected Grabber			grabber;

	/**
	 * Constructs <{@link remixlab.bias.event.BogusEvent},{@link remixlab.bias.core.Grabber}> tuple
	 * 
	 * @param e
	 *          event
	 * @param g
	 *          grabber
	 */
	public EventGrabberTuple(BogusEvent e, Grabber g) {
		event = e;
		grabber = g;
	}

	/**
	 * Calls {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)}.
	 * 
	 * @return true if succeeded and false otherwise.
	 */
	public boolean perform() {
		if (grabber != null) {
			grabber.performInteraction(event);
			return true;
		}
		return false;
	}

	/**
	 * Returns the event from the tupple.
	 */
	public BogusEvent event() {
		return event;
	}

	/**
	 * Returns the object Grabber in the tuple.
	 */
	public Grabber grabber() {
		return grabber;
	}

	/**
	 * Enqueues the tuple for later execution.
	 */
	public boolean enqueue(LinkedList<EventGrabberTuple> queue) {
		if (!event().isNull()) {
			queue.add(this);
			return true;
		}
		return false;
	}
}
