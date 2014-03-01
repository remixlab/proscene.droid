/*********************************************************************************
 * bogusinput_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.bogusinput.generic.event;

import remixlab.bogusinput.core.Action;
import remixlab.bogusinput.event.ClickEvent;

/**
 * Generic version of {@link remixlab.bogusinput.event.ClickEvent}.
 * <p>
 * Action events attach an {@link #action()} to the non-generic version of the event. The idea being that an
 * {@link remixlab.bogusinput.core.Agent} will at some point automatically attach the action to the
 * {@link remixlab.bogusinput.generic.event.ActionBogusEvent}.
 * 
 * @param <A>
 *          user defined action
 */
public class ActionClickEvent<A extends Action<?>> extends ClickEvent implements ActionBogusEvent<A> {
	Action<?> action;

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bogusinput.event.ClickEvent} one.
	 */
	public ActionClickEvent(float x, float y, int b) {
		super(x, y, b);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bogusinput.event.ClickEvent} one.
	 */
	public ActionClickEvent(float x, float y, int b, int clicks) {
		super(x, y, b, clicks);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bogusinput.event.ClickEvent} one.
	 */
	public ActionClickEvent(float x, float y, Integer modifiers, int b, int clicks) {
		super(x, y, modifiers, b, clicks);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bogusinput.event.ClickEvent} one and then
	 * attaches to it the given user-defined action.
	 */
	public ActionClickEvent(float x, float y, int b, Action<?> a) {
		super(x, y, b);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bogusinput.event.ClickEvent} one and then
	 * attaches to it the given user-defined action.
	 */
	public ActionClickEvent(float x, float y, int b, int clicks, Action<?> a) {
		super(x, y, b, clicks);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bogusinput.event.ClickEvent} one and then
	 * attaches to it the given user-defined action.
	 */
	public ActionClickEvent(float x, float y, Integer modifiers, int b, int clicks, Action<?> a) {
		super(x, y, modifiers, b, clicks);
		action = a;
	}

	protected ActionClickEvent(ActionClickEvent<A> other) {
		super(other);
		action = other.action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bogusinput.core.ActionEvent#action()
	 */
	@Override
	public Action<?> action() {
		return action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bogusinput.core.ActionEvent#setAction(remixlab.bogusinput.core.Action)
	 */
	@Override
	public void setAction(Action<?> a) {
		if (a instanceof Action<?>)
			action = a;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bogusinput.event.ClickEvent#get()
	 */
	@Override
	public ActionClickEvent<A> get() {
		return new ActionClickEvent<A>(this);
	}
}
