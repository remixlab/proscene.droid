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
import remixlab.bias.event.DOF3Event;

/**
 * Generic version of {@link remixlab.bias.event.DOF3Event}.
 * <p>
 * Action events attach an {@link #action()} to the non-generic version of the event. The idea being that an
 * {@link remixlab.bias.core.Agent} will at some point automatically attach the action to the
 * {@link remixlab.bias.generic.event.ActionBogusEvent}.
 * 
 * @param <A>
 *          user-defined action
 */
public class ActionDOF3Event<A extends Action<?>> extends DOF3Event implements ActionBogusEvent<A> {
	Action<?>	action;

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF3Event} one.
	 */
	public ActionDOF3Event(float x, float y, float z, int modifiers, int button) {
		super(x, y, z, modifiers, button);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF3Event} one.
	 */
	public ActionDOF3Event(ActionDOF3Event<A> prevEvent, float x, float y, float z, int modifiers, int button) {
		super(prevEvent, x, y, z, modifiers, button);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF3Event} one.
	 */
	public ActionDOF3Event(float x, float y, float z) {
		super(x, y, z);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF3Event} one.
	 */
	public ActionDOF3Event(ActionDOF3Event<A> prevEvent, float x, float y, float z) {
		super(prevEvent, x, y, z);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF3Event} one and then attaches to it
	 * the given user-defined action.
	 */
	public ActionDOF3Event(float x, float y, float z, int modifiers, int button, Action<?> a) {
		super(x, y, z, modifiers, button);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF3Event} one and then attaches to it
	 * the given user-defined action.
	 */
	public ActionDOF3Event(ActionDOF3Event<A> prevEvent, float x, float y, float z, int modifiers, int button,
			Action<?> a) {
		super(prevEvent, x, y, z, modifiers, button);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF3Event} one and then attaches to it
	 * the given user-defined action.
	 */
	public ActionDOF3Event(float x, float y, float z, Action<?> a) {
		super(x, y, z);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF3Event} one and then attaches to it
	 * the given user-defined action.
	 */
	public ActionDOF3Event(ActionDOF3Event<A> prevEvent, float x, float y, float z, Action<?> a) {
		super(prevEvent, x, y, z);
		action = a;
	}

	protected ActionDOF3Event(ActionDOF3Event<A> other) {
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
	public ActionDOF3Event<A> get() {
		return new ActionDOF3Event<A>(this);
	}
}
