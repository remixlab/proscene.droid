/*********************************************************************************
 * bogusinput_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.bogusinput.generic.agent;

import remixlab.bogusinput.core.*;
import remixlab.bogusinput.event.*;
import remixlab.bogusinput.generic.event.*;
import remixlab.bogusinput.generic.profile.*;

/**
 * A {@link remixlab.bogusinput.generic.agent.ActionMotionAgent} with an extra
 * {@link remixlab.bogusinput.generic.profile.MotionProfile} defining
 * {@link remixlab.bogusinput.event.shortcut.ButtonShortcut} -> {@link remixlab.bogusinput.core.Action} mappings.
 * <p>
 * The Agent thus is defined by three profiles: the {@link #motionProfile()} (alias for {@link #profile()} provided for
 * convenience), the {@link #clickProfile()} and the extra {@link #wheelProfile()}.
 * 
 * @param <W>
 *          {@link remixlab.bogusinput.generic.profile.MotionProfile} to parameterize the Agent with.
 * @param <M>
 *          {@link remixlab.bogusinput.generic.profile.MotionProfile} to parameterize the Agent with.
 * @param <C>
 *          {@link remixlab.bogusinput.generic.profile.ClickProfile} to parameterize the Agent with.
 */
public class ActionWheeledMotionAgent<W extends MotionProfile<?>, M extends MotionProfile<?>, C extends ClickProfile<?>>
				extends ActionMotionAgent<M, C> {

	protected W wheelProfile;

	/**
	 * @param w
	 *          {@link remixlab.bogusinput.generic.profile.MotionProfile} instance
	 * @param p
	 *          {@link remixlab.bogusinput.generic.profile.MotionProfile} second instance
	 * @param c
	 *          {@link remixlab.bogusinput.generic.profile.ClickProfile} instance
	 * @param tHandler
	 *          {@link remixlab.bogusinput.core.InputHandler} to register this Agent to
	 * @param n
	 *          Agent name
	 */
	public ActionWheeledMotionAgent(W w, M p, C c, InputHandler tHandler, String n) {
		super(p, c, tHandler, n);
		wheelProfile = w;
	}

	/**
	 * @return the agents second {@link remixlab.bogusinput.generic.profile.MotionProfile} instance.
	 */
	public W wheelProfile() {
		return wheelProfile;
	}

	/**
	 * Sets the {@link remixlab.bogusinput.generic.profile.MotionProfile} second instance.
	 */
	public void setWheelProfile(W profile) {
		wheelProfile = profile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bogusinput.generic.agent.ActionMotionAgent#info()
	 */
	@Override
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if (clickProfile().description().length() != 0) {
			description += "Click shortcuts\n";
			description += clickProfile().description();
		}
		if (motionProfile().description().length() != 0) {
			description += "Motion shortcuts\n";
			description += motionProfile().description();
		}
		if (wheelProfile().description().length() != 0) {
			description += "Wheel shortcuts\n";
			description += wheelProfile().description();
		}
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bogusinput.generic.agent.ActionMotionAgent#handle(remixlab.bogusinput.event.BogusEvent)
	 */
	@Override
	public void handle(BogusEvent event) {
		// overkill but feels safer ;)
		if (event == null || !handler.isAgentRegistered(this) || grabber() == null)
			return;
		if (event instanceof ActionBogusEvent<?>) {
			if (event instanceof ClickEvent)
				if (foreignGrabber())
					handler.enqueueEventTuple(new EventGrabberTuple(event, grabber()));
				else
					handler.enqueueEventTuple(new ActionEventGrabberTuple(event, clickProfile().handle(
									(ActionBogusEvent<?>) event),
									grabber()));
			else if (event instanceof MotionEvent) {
				((MotionEvent) event).modulate(sens);
				if (foreignGrabber())
					handler.enqueueEventTuple(new EventGrabberTuple(event, grabber()));
				else if (event instanceof ActionDOF1Event)
					handler.enqueueEventTuple(new ActionEventGrabberTuple(event, wheelProfile().handle(
									(ActionBogusEvent<?>) event),
									grabber()));
				else
					handler.enqueueEventTuple(new ActionEventGrabberTuple(event,
									motionProfile().handle((ActionBogusEvent<?>) event),
									grabber()));
			}
		}
	}
}
