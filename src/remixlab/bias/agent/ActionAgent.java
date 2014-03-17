/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.agent;

import remixlab.bias.core.Agent;
import remixlab.bias.core.BogusEvent;
import remixlab.bias.core.EventGrabberTuple;
import remixlab.bias.core.InputHandler;
import remixlab.bias.profile.Profile;

/**
 * An ActionAgent is just an {@link remixlab.bias.core.Agent} holding some {@link remixlab.bias.profile.Profile} s. The
 * Agent uses the {@link remixlab.bias.event.shortcut.Shortcut} -> {@link remixlab.bias.core.Action} mappings defined by
 * each of its Profiles to parse the {@link remixlab.bias.core.BogusEvent} into an user-defined
 * {@link remixlab.bias.core.Action} (see {@link #handle(BogusEvent)}).
 * <p>
 * The default implementation here holds only a single {@link remixlab.bias.profile.Profile} (see {@link #profile()})
 * attribute (note that we use the type of the Profile to parameterize the GenericAgent). Different profile groups are
 * provided by the {@link remixlab.bias.agent.ActionMotionAgent}, the
 * {@link remixlab.bias.agent.ActionWheeledMotionAgent} and the {@link remixlab.bias.agent.ActionKeyboardAgent}
 * specializations, which roughly represent an HIDevice (like a kinect), a wheeled HIDevice (like a mouse) and a generic
 * keyboard, respectively.
 * <p>
 * Third-parties implementations should "simply": 1. Derive from the GenericAgent above that best fits their needs; 2.
 * Supply a routine to reduce application-specific input data into BogusEvents (given them thier name); and, 3. Properly
 * call {@link #updateTrackedGrabber(BogusEvent)} and {@link #handle(BogusEvent)} on them. The
 * <b>remixlab.proscene.Scene.ProsceneMouse</b> and <b>remixlab.proscene.Scene.ProsceneKeyboard</b> classes provide good
 * example implementations. Note that the GenericAgent methods defined in this package (bias) should rarely be in need
 * to be overridden, not even {@link #handle(BogusEvent)}.
 * 
 * @param <P>
 *          {@link remixlab.bias.profile.Profile} to parameterize the Agent with.
 */
public class ActionAgent<P extends Profile<?, ?>> extends Agent {
	protected P	profile;

	/**
	 * @param p
	 *          {@link remixlab.bias.profile.Profile}
	 * @param tHandler
	 *          {@link remixlab.bias.core.InputHandler} to register this Agent to
	 * @param n
	 *          Agent name
	 */
	public ActionAgent(P p, InputHandler tHandler, String n) {
		super(tHandler, n);
		profile = p;
	}

	/**
	 * @return the agents {@link remixlab.bias.profile.Profile} instance.
	 */
	public P profile() {
		return profile;
	}

	/**
	 * Sets the {@link remixlab.bias.profile.Profile}
	 * 
	 * @param p
	 */
	public void setProfile(P p) {
		profile = p;
	}

	/**
	 * Returns false. More interesting things may happen in derived classes.
	 */
	protected boolean foreignGrabber() {
		return false;
	}

	@Override
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if (profile().description().length() != 0) {
			description += "Shortcuts\n";
			description += profile().description();
		}
		return description;
	}

	/**
	 * Overriding of the {@link remixlab.bias.core.Agent} main method. The {@link #profile()} is used to parse the event
	 * into an user-defined action which is then enqueued as an event-grabber tuple (
	 * {@link #enqueueEventTuple(EventGrabberTuple)}), used to instruct the {@link #inputGrabber()} the user-defined
	 * action to perform.
	 * <p>
	 * <b>Note 1:</b> {@link #foreignGrabber()}s always make the tuple to be enqueued even if the action in non-null (see
	 * {@link #enqueueEventTuple(EventGrabberTuple, boolean)}).
	 * <p>
	 * <b>Note 2:</b> This method should only be overridden in the rare case a custom set of rules is needed to select
	 * which Profile should parse the given bogus event.
	 */
	@Override
	public void handle(BogusEvent event) {
		// overkill but feels safer ;)
		if (event == null || !handler.isAgentRegistered(this) || inputGrabber() == null)
			return;
		if (foreignGrabber())
			enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()), false);
		else
			enqueueEventTuple(new EventGrabberTuple(event, profile().handle(event), inputGrabber()));
	}
}
