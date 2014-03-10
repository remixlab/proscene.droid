/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.generic.agent;

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.generic.event.ActionBogusEvent;
import remixlab.bias.generic.profile.*;

/**
 * An {@link remixlab.bias.generic.agent.ActionAgent} with an extra {@link remixlab.bias.generic.profile.ClickProfile}
 * defining {@link remixlab.bias.event.shortcut.ClickShortcut} -> {@link remixlab.bias.core.Action} mappings.
 * <p>
 * The Agent thus is defined by two profiles: the {@link #motionProfile()} (alias for {@link #profile()} provided for
 * convenience) and the (extra) {@link #clickProfile()}.
 * 
 * @param <M>
 *          {@link remixlab.bias.generic.profile.MotionProfile} to parameterize the Agent with.
 * @param <C>
 *          {@link remixlab.bias.generic.profile.ClickProfile} to parameterize the Agent with.
 */
public class ActionMotionAgent<M extends MotionProfile<?>, C extends ClickProfile<?>> extends
		ActionAgent<M> {
	protected C				clickProfile;
	protected float[]	sens;

	/**
	 * @param p
	 *          {@link remixlab.bias.generic.profile.MotionProfile} instance
	 * @param c
	 *          {@link remixlab.bias.generic.profile.ClickProfile} instance
	 * @param tHandler
	 *          {@link remixlab.bias.core.InputHandler} to register this Agent to
	 * @param n
	 *          Agent name
	 */
	public ActionMotionAgent(M p, C c, InputHandler tHandler, String n) {
		super(p, tHandler, n);
		clickProfile = c;
		sens = new float[] { 1f, 1f, 1f, 1f, 1f, 1f };
	}

	/**
	 * Alias for {@link #profile()}.
	 */
	public M motionProfile() {
		return profile();
	}

	/**
	 * Sets the {@link remixlab.bias.generic.profile.MotionProfile}
	 */
	public void setMotionProfile(M profile) {
		setProfile(profile);
	}

	/**
	 * Returns the {@link remixlab.bias.generic.profile.ClickProfile} instance.
	 */
	public C clickProfile() {
		return clickProfile;
	}

	/**
	 * Sets the {@link remixlab.bias.generic.profile.ClickProfile}
	 */
	public void setClickProfile(C profile) {
		clickProfile = profile;
	}

	/**
	 * Set dof1 sensitivity value which is needed by {@link remixlab.bias.event.MotionEvent#modulate(float[])}.
	 */
	public void setSensitivities(float x) {
		setSensitivities(x, 0, 0, 0, 0, 0);
	}

	/**
	 * Set dof1 and dof2 sensitivities value which are needed by {@link remixlab.bias.event.MotionEvent#modulate(float[])}
	 * .
	 */
	public void setSensitivities(float x, float y) {
		setSensitivities(x, y, 0, 0, 0, 0);
	}

	/**
	 * Set dof1, dof2 and dof3 sensitivities values which are needed by
	 * {@link remixlab.bias.event.MotionEvent#modulate(float[])}.
	 */
	public void setSensitivities(float x, float y, float z) {
		setSensitivities(x, y, z, 0, 0, 0);
	}

	/**
	 * Set dof1, dof2, dof3, dof4, dof5 and dof6 sensitivities values which are needed by
	 * {@link remixlab.bias.event.MotionEvent#modulate(float[])}.
	 */
	public void setSensitivities(float x, float y, float z, float rx, float ry, float rz) {
		sens[0] = x;
		sens[1] = y;
		sens[2] = z;
		sens[3] = rx;
		sens[4] = ry;
		sens[5] = rz;
	}

	/**
	 * Returns the sensitivities that modulate the {@link remixlab.bias.event.MotionEvent}
	 */
	public float[] sensitivities() {
		return sens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bias.generic.agent.ActionAgent#info()
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
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bias.generic.agent.ActionAgent#handle(remixlab.bias.event.BogusEvent)
	 */
	@Override
	public void handle(BogusEvent event) {
		// overkill but feels safer ;)
		if (event == null || !handler.isAgentRegistered(this) || grabber() == null)
			return;
		if (event instanceof ActionBogusEvent<?>) {
			if (event instanceof ClickEvent)
				if (foreignGrabber())
					enqueueEventTuple(new EventGrabberTuple(event, grabber()));
				else
					enqueueEventTuple(new ActionEventGrabberTuple(event, clickProfile().handle((ActionBogusEvent<?>) event),
							grabber()));
			else if (event instanceof MotionEvent) {
				((MotionEvent) event).modulate(sens);
				if (foreignGrabber())
					enqueueEventTuple(new EventGrabberTuple(event, grabber()));
				else
					enqueueEventTuple(new ActionEventGrabberTuple(event, motionProfile().handle((ActionBogusEvent<?>) event),
							grabber()));
			}
		}
	}
}
