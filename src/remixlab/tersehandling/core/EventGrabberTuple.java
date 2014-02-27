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

import java.util.LinkedList;

import remixlab.tersehandling.event.TerseEvent;

/**
 * [{@link remixlab.tersehandling.event.TerseEvent},{@link remixlab.tersehandling.core.Grabbable}] tuples which
 * encapsulate message passing from {@link remixlab.tersehandling.core.Grabbable} to
 * {@link remixlab.tersehandling.core.Grabbable} to perform actions.
 * 
 * @author pierre
 */
public class EventGrabberTuple {
	protected TerseEvent event;
	protected Grabbable grabber;

	/**
	 * Constructs <{@link remixlab.tersehandling.event.TerseEvent},{@link remixlab.tersehandling.core.Grabbable}> tuple
	 * 
	 * @param e
	 *          event
	 * @param g
	 *          grabber
	 */
	public EventGrabberTuple(TerseEvent e, Grabbable g) {
		event = e;
		grabber = g;
	}

	/**
	 * Calls {@link remixlab.tersehandling.core.Grabbable#performInteraction(TerseEvent)}.
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
	public TerseEvent event() {
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
