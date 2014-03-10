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
import remixlab.bias.event.DOF1Event;

/**
 * Generic version of {@link remixlab.bias.event.DOF1Event}.
 * <p>
 * Action events attach an {@link #action()} to the non-generic version of the event. The idea being that an
 * {@link remixlab.bias.core.Agent} will at some point automatically attach the action to the
 * {@link remixlab.bias.generic.event.ActionBogusEvent}.
 * 
 * @param <A>
 *          user-defined action
 */
public class ActionDOF1Event<A extends Action<?>> extends DOF1Event implements ActionBogusEvent<A> {
	Action<?>	action;

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF1Event} one.
	 */
	public ActionDOF1Event(float x, int modifiers, int button) {
		super(x, modifiers, button);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF1Event} one.
	 */
	public ActionDOF1Event(ActionDOF1Event<A> prevEvent, float x, int modifiers, int button) {
		super(prevEvent, x, modifiers, button);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF1Event} one.
	 */
	public ActionDOF1Event(float x) {
		super(x);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF1Event} one.
	 */
	public ActionDOF1Event(ActionDOF1Event<A> prevEvent, float x) {
		super(prevEvent, x);
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF1Event} one and then attaches to it
	 * the given user-defined action.
	 */
	public ActionDOF1Event(float x, int modifiers, int button, Action<?> a) {
		super(x, modifiers, button);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF1Event} one and then attaches to it
	 * the given user-defined action.
	 */
	public ActionDOF1Event(ActionDOF1Event<A> prevEvent, float x, int modifiers, int button, Action<?> a) {
		super(prevEvent, x, modifiers, button);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF1Event} one and then attaches to it
	 * the given user-defined action.
	 */
	public ActionDOF1Event(float x, Action<?> a) {
		super(x);
		action = a;
	}

	/**
	 * Convenience constructor that calls the equivalent {@link remixlab.bias.event.DOF1Event} one and then attaches to it
	 * the given user-defined action.
	 */
	public ActionDOF1Event(ActionDOF1Event<A> prevEvent, float x, Action<?> a) {
		super(prevEvent, x);
		action = a;
	}

	protected ActionDOF1Event(ActionDOF1Event<A> other) {
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
	public ActionDOF1Event<A> get() {
		return new ActionDOF1Event<A>(this);
	}
}
