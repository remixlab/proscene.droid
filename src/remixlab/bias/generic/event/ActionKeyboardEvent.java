/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.generic.event;

import remixlab.bias.core.Action;
import remixlab.bias.event.KeyboardEvent;

/**
 * Generic version of {@link remixlab.bias.event.KeyboardEvent}.
 * <p>
 * Action events attach an {@link #action()} to the non-generic version of the event. The idea being that an
 * {@link remixlab.bias.core.Agent} will at some point automatically attach the action to the
 * {@link remixlab.bias.generic.event.ActionBogusEvent}.
 * 
 * @param <A>
 *          user-defined action
 */
public class ActionKeyboardEvent<A extends Action<?>> extends KeyboardEvent implements ActionBogusEvent<A> {
	Action<?>	action;

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.KeyboardEvent} one.
	 */
	public ActionKeyboardEvent(Integer modifiers, Integer vk) {
		super(modifiers, vk);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.KeyboardEvent} one.
	 */
	public ActionKeyboardEvent(Character c) {
		super(c);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.KeyboardEvent} one and then attaches
	 * to it the given user-defined action.
	 */
	public ActionKeyboardEvent(Integer modifiers, Integer vk, Action<?> a) {
		super(modifiers, vk);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.KeyboardEvent} one and then attaches
	 * to it the given user-defined action.
	 */
	public ActionKeyboardEvent(Character c, Action<?> a) {
		super(c);
		action = a;
	}

	protected ActionKeyboardEvent(ActionKeyboardEvent<A> other) {
		super(other);
		action = other.action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bias.core.ActionEvent#action()
	 */
	@Override
	public Action<?> action() {
		return action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bias.core.ActionEvent#setAction(remixlab.bias.core.Action)
	 */
	@Override
	public void setAction(Action<?> a) {
		if (a instanceof Action<?>)
			action = a;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bias.event.KeyboardEvent#get()
	 */
	@Override
	public ActionKeyboardEvent<A> get() {
		return new ActionKeyboardEvent<A>(this);
	}
}
