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

import remixlab.tersehandling.event.KeyboardEvent;
import remixlab.tersehandling.generic.profile.Actionable;
import remixlab.tersehandling.generic.profile.Duoable;

/**
 * Generic version of {@link remixlab.tersehandling.event.KeyboardEvent}.
 * <p>
 * Generic events attach an {@link #action()} to the non-generic version of the event. The idea being that an
 * {@link remixlab.tersehandling.core.Agent} will at some point automatically attach the action to the event (
 * {@link remixlab.tersehandling.core.Agent#handle(remixlab.tersehandling.event.TerseEvent)}).
 * 
 * @param <A>
 *          user-defined action
 */
public class GenericKeyboardEvent<A extends Actionable<?>> extends KeyboardEvent implements Duoable<A> {
	Actionable<?> action;

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.KeyboardEvent} one.
	 */
	public GenericKeyboardEvent(Integer modifiers, Integer vk) {
		super(modifiers, vk);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.KeyboardEvent} one.
	 */
	public GenericKeyboardEvent(Character c) {
		super(c);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.KeyboardEvent} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericKeyboardEvent(Integer modifiers, Integer vk, Actionable<?> a) {
		super(modifiers, vk);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.tersehandling.event.KeyboardEvent} one and then
	 * attaches to it the given user-defined action.
	 */
	public GenericKeyboardEvent(Character c, Actionable<?> a) {
		super(c);
		action = a;
	}

	protected GenericKeyboardEvent(GenericKeyboardEvent<A> other) {
		super(other);
		action = other.action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.generic.profile.Duoable#action()
	 */
	@Override
	public Actionable<?> action() {
		return action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.generic.profile.Duoable#setAction(remixlab.tersehandling.generic.profile.Actionable)
	 */
	@Override
	public void setAction(Actionable<?> a) {
		if (a instanceof Actionable<?>)
			action = a;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.event.KeyboardEvent#get()
	 */
	@Override
	public GenericKeyboardEvent<A> get() {
		return new GenericKeyboardEvent<A>(this);
	}
}
