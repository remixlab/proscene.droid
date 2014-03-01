/*********************************************************************************
 * bogusinput_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.bogusinput.core;

import java.util.LinkedList;

import remixlab.bogusinput.event.BogusEvent;

/**
 * [{@link remixlab.bogusinput.event.BogusEvent},{@link remixlab.bogusinput.core.Grabbable}] tuples which encapsulate
 * message passing from {@link remixlab.bogusinput.event.BogusEvent} to {@link remixlab.bogusinput.core.Grabbable} to
 * perform actions.
 */
public class EventGrabberTuple {
	protected BogusEvent event;
	protected Grabbable grabber;

	/**
	 * Constructs <{@link remixlab.bogusinput.event.BogusEvent},{@link remixlab.bogusinput.core.Grabbable}> tuple
	 * 
	 * @param e
	 *          event
	 * @param g
	 *          grabber
	 */
	public EventGrabberTuple(BogusEvent e, Grabbable g) {
		event = e;
		grabber = g;
	}

	/**
	 * Calls {@link remixlab.bogusinput.core.Grabbable#performInteraction(BogusEvent)}.
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
	public Grabbable grabber() {
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
