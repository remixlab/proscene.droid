/*********************************************************************************
 * TerseHandling
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.tersehandling.generic.event;

import remixlab.tersehandling.core.Action;
import remixlab.tersehandling.core.GenericEvent;
import remixlab.tersehandling.event.DOF6Event;

/**
 * Generic version of {@link remixlab.tersehandling.event.DOF6Event}.
 * <p>
 * Generic events attach an {@link #action()} to the non-generic version of the event. The idea being that an
 * {@link remixlab.tersehandling.core.Agent} will at some point automatically attach the action to the event (
 * {@link remixlab.tersehandling.core.Agent#handle(remixlab.tersehandling.event.TerseEvent)}).
 * 
 * @param <A>
 *          user-defined action
 */
public class GenericDOF6Event<A extends Action<?>> extends DOF6Event implements GenericEvent<A> {
	Action<?> action;

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF6Event} one.
	 */
	public GenericDOF6Event(float x, float y, float z, float rx, float ry, float rz, int modifiers, int button) {
		super(x, y, z, rx, ry, rz, modifiers, button);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF6Event} one.
	 */
	public GenericDOF6Event(GenericDOF6Event<A> prevEvent, float x, float y, float z, float rx, float ry, float rz,
					int modifiers, int button) {
		super(prevEvent, x, y, z, rx, ry, rz, modifiers, button);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF6Event} one.
	 */
	public GenericDOF6Event(float x, float y, float z, float rx, float ry, float rz) {
		super(x, y, z, rx, ry, rz);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF6Event} one.
	 */
	public GenericDOF6Event(GenericDOF6Event<A> prevEvent, float x, float y, float z, float rx, float ry, float rz) {
		super(prevEvent, x, y, z, rx, ry, rz);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF6Event} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericDOF6Event(float x, float y, float z, float rx, float ry, float rz, int modifiers, int button,
					Action<?> a) {
		super(x, y, z, rx, ry, rz, modifiers, button);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF6Event} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericDOF6Event(GenericDOF6Event<A> prevEvent, float x, float y, float z, float rx, float ry, float rz,
					int modifiers, int button, Action<?> a) {
		super(prevEvent, x, y, z, rx, ry, rz, modifiers, button);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF6Event} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericDOF6Event(float x, float y, float z, float rx, float ry, float rz, Action<?> a) {
		super(x, y, z, rx, ry, rz);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF6Event} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericDOF6Event(GenericDOF6Event<A> prevEvent, float x, float y, float z, float rx, float ry, float rz,
					Action<?> a) {
		super(prevEvent, x, y, z, rx, ry, rz);
		action = a;
	}

	protected GenericDOF6Event(GenericDOF6Event<A> other) {
		super(other);
		action = other.action;
	}

	@Override
	public Action<?> action() {
		return action;
	}

	@Override
	public void setAction(Action<?> a) {
		if (a instanceof Action<?>)
			action = a;
	}

	@Override
	public GenericDOF6Event<A> get() {
		return new GenericDOF6Event<A>(this);
	}
}
