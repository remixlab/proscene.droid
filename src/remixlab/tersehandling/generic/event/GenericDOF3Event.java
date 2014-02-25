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

import remixlab.tersehandling.event.DOF3Event;
import remixlab.tersehandling.generic.profile.Actionable;
import remixlab.tersehandling.generic.profile.Duoable;

/**
 * Generic version of {@link remixlab.tersehandling.event.DOF3Event}.
 * <p>
 * Generic events attach an {@link #action()} to the non-generic version of the event. The idea being that an
 * {@link remixlab.tersehandling.core.Agent} will at some point automatically attach the action to the event (
 * {@link remixlab.tersehandling.core.Agent#handle(remixlab.tersehandling.event.TerseEvent)}).
 * 
 * @param <A>
 *          user-defined action
 */
public class GenericDOF3Event<A extends Actionable<?>> extends DOF3Event implements Duoable<A> {
	Actionable<?> action;

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF3Event} one.
	 */
	public GenericDOF3Event(float x, float y, float z, int modifiers, int button) {
		super(x, y, z, modifiers, button);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF3Event} one.
	 */
	public GenericDOF3Event(GenericDOF3Event<A> prevEvent, float x, float y, float z, int modifiers, int button) {
		super(prevEvent, x, y, z, modifiers, button);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF3Event} one.
	 */
	public GenericDOF3Event(float x, float y, float z) {
		super(x, y, z);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF3Event} one.
	 */
	public GenericDOF3Event(GenericDOF3Event<A> prevEvent, float x, float y, float z) {
		super(prevEvent, x, y, z);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF3Event} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericDOF3Event(float x, float y, float z, int modifiers, int button, Actionable<?> a) {
		super(x, y, z, modifiers, button);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF3Event} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericDOF3Event(GenericDOF3Event<A> prevEvent, float x, float y, float z, int modifiers, int button,
					Actionable<?> a) {
		super(prevEvent, x, y, z, modifiers, button);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF3Event} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericDOF3Event(float x, float y, float z, Actionable<?> a) {
		super(x, y, z);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.DOF3Event} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericDOF3Event(GenericDOF3Event<A> prevEvent, float x, float y, float z, Actionable<?> a) {
		super(prevEvent, x, y, z);
		action = a;
	}

	protected GenericDOF3Event(GenericDOF3Event<A> other) {
		super(other);
		action = other.action;
	}

	@Override
	public Actionable<?> action() {
		return action;
	}

	@Override
	public void setAction(Actionable<?> a) {
		if (a instanceof Actionable<?>)
			action = a;
	}

	@Override
	public GenericDOF3Event<A> get() {
		return new GenericDOF3Event<A>(this);
	}
}
