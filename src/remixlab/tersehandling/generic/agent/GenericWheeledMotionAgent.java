/*********************************************************************************
 * TerseHandling
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.tersehandling.generic.agent;

import remixlab.tersehandling.core.*;
import remixlab.tersehandling.event.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;

/**
 * A {@link remixlab.tersehandling.generic.agent.GenericMotionAgent} with an extra
 * {@link remixlab.tersehandling.generic.profile.MotionProfile} defining
 * {@link remixlab.tersehandling.event.shortcut.ButtonShortcut} -> {@link remixlab.tersehandling.core.Action} mappings.
 * <p>
 * The Agent thus is defined by three profiles: the {@link #motionProfile()} (alias for {@link #profile()} provided for
 * convenience), the {@link #clickProfile()} and the extra {@link #wheelProfile()}.
 * 
 * @param <W>
 *          {@link remixlab.tersehandling.generic.profile.MotionProfile} to parameterize the Agent with.
 * @param <M>
 *          {@link remixlab.tersehandling.generic.profile.MotionProfile} to parameterize the Agent with.
 * @param <C>
 *          {@link remixlab.tersehandling.generic.profile.ClickProfile} to parameterize the Agent with.
 */
public class GenericWheeledMotionAgent<W extends MotionProfile<?>, M extends MotionProfile<?>, C extends ClickProfile<?>>
				extends GenericMotionAgent<M, C> {

	protected W wheelProfile;

	/**
	 * @param w
	 *          {@link remixlab.tersehandling.generic.profile.MotionProfile} instance
	 * @param p
	 *          {@link remixlab.tersehandling.generic.profile.MotionProfile} second instance
	 * @param c
	 *          {@link remixlab.tersehandling.generic.profile.ClickProfile} instance
	 * @param tHandler
	 *          {@link remixlab.tersehandling.core.EventHandler} to register this Agent to
	 * @param n
	 *          Agent name
	 */
	public GenericWheeledMotionAgent(W w, M p, C c, EventHandler tHandler, String n) {
		super(p, c, tHandler, n);
		wheelProfile = w;
	}

	/**
	 * @return the agents second {@link remixlab.tersehandling.generic.profile.MotionProfile} instance.
	 */
	public W wheelProfile() {
		return wheelProfile;
	}

	/**
	 * Sets the {@link remixlab.tersehandling.generic.profile.MotionProfile} second instance.
	 */
	public void setWheelProfile(W profile) {
		wheelProfile = profile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.generic.agent.GenericMotionAgent#info()
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
	 * @see remixlab.tersehandling.generic.agent.GenericMotionAgent#handle(remixlab.tersehandling.event.TerseEvent)
	 */
	@Override
	public void handle(TerseEvent event) {
		// overkill but feels safer ;)
		if (event == null || !handler.isAgentRegistered(this) || grabber() == null)
			return;
		if (event instanceof GenericEvent<?>) {
			if (event instanceof ClickEvent)
				if (foreignGrabber())
					handler.enqueueEventTuple(new EventGrabberTuple(event, grabber()));
				else
					handler.enqueueEventTuple(new GenericEventGrabberTuple(event, clickProfile().handle((GenericEvent<?>) event),
									grabber()));
			else if (event instanceof MotionEvent) {
				((MotionEvent) event).modulate(sens);
				if (foreignGrabber())
					handler.enqueueEventTuple(new EventGrabberTuple(event, grabber()));
				else if (event instanceof GenericDOF1Event)
					handler.enqueueEventTuple(new GenericEventGrabberTuple(event, wheelProfile().handle((GenericEvent<?>) event),
									grabber()));
				else
					handler.enqueueEventTuple(new GenericEventGrabberTuple(event,
									motionProfile().handle((GenericEvent<?>) event),
									grabber()));
			}
		}
	}
}
