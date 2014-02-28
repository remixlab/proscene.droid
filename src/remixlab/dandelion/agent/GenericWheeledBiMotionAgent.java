/*********************************************************************************
 * dandelion
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.dandelion.agent;

import remixlab.dandelion.core.*;
import remixlab.tersehandling.generic.agent.*;
import remixlab.tersehandling.generic.profile.*;

/**
 * A {@link remixlab.tersehandling.generic.agent.GenericWheeledMotionAgent} that handles Dandelion motion actions (i.e.,
 * actions triggered from a {@link remixlab.tersehandling.event.MotionEvent}). You should not instantiate this class but
 * one of its derived ones: {@link remixlab.dandelion.agent.HIDAgent}, {@link remixlab.dandelion.agent.JoystickAgent} or
 * {@link remixlab.dandelion.agent.MouseAgent}.
 * <p>
 * Dandelion actions can be handled by an {@link remixlab.dandelion.core.AbstractScene}, an
 * {@link remixlab.dandelion.core.InteractiveFrame} or by an {@link remixlab.dandelion.core.InteractiveEyeFrame}. This
 * class implements a generic Agent that represents any Human Interface Device (such as a mouse or a Joystick) and that
 * handles actions to be executed only by an {@link remixlab.dandelion.core.InteractiveFrame} or an
 * {@link remixlab.dandelion.core.InteractiveEyeFrame} (hence the name "bimotion").
 * {@link remixlab.dandelion.core.AbstractScene} actions are handled exclusively by a
 * {@link remixlab.dandelion.agent.KeyboardAgent}.
 * <p>
 * The agent uses its {@link remixlab.tersehandling.generic.profile.Profile}s (see below) to parse the generic
 * {@link remixlab.tersehandling.event.TerseEvent} to obtain a dandelion action, which is then sent to the proper (
 * {@link #grabber()}) Frame (InteractiveFrame or InteractiveEyeFrame) for its final execution. In case the grabber is
 * not an instance of a Frame, but a different object which behavior you implemented (retrieved as with
 * {@link #foreignGrabber()}), the agent sends the raw TerseEvent to it (please refer to the mouse grabbers example).
 * <p>
 * This agent holds the following InteractiveFrame {@link remixlab.tersehandling.generic.profile.Profile}s: a
 * {@link #frameProfile()}, a {@link #frameClickProfile()}, and a {@link #frameWheelProfile()}; together with its
 * InteractiveEyeFrame counterparts: a {@link #eyeProfile()}, a {@link #eyeClickProfile()}, and a
 * {@link #eyeWheelProfile()}. Simply retrieve a specific profile to bind an action to a shortcut, to remove it, or to
 * check your current bindings.
 * <p>
 * <b>Note</b> that the {@link remixlab.tersehandling.generic.agent.GenericWheeledMotionAgent} holds only three
 * profiles: {@link remixlab.tersehandling.generic.agent.GenericWheeledMotionAgent#wheelProfile()},
 * {@link remixlab.tersehandling.generic.agent.GenericWheeledMotionAgent#clickProfile()} and
 * {@link remixlab.tersehandling.generic.agent.GenericWheeledMotionAgent#motionProfile()}. The
 * GenericWheeledBiMotionAgent renames this three profiles for the InteractiveFrame and add those of the
 * InteractiveEyeFrame.
 * 
 * @param <P>
 *          GenericMotionProfile parameterised with a Dandelion action
 */
public class GenericWheeledBiMotionAgent<P extends MotionProfile<?>> extends
				GenericWheeledMotionAgent<MotionProfile<Constants.WheelAction>,
				P,
				ClickProfile<Constants.ClickAction>> implements Constants {
	protected P camProfile;
	protected MotionProfile<WheelAction> camWheelProfile;
	protected ClickProfile<ClickAction> camClickProfile;
	protected AbstractScene scene;

	public GenericWheeledBiMotionAgent(MotionProfile<WheelAction> fWProfile,
					MotionProfile<WheelAction> cWProfile,
					P fProfile,
					P cProfile,
					ClickProfile<ClickAction> c,
					ClickProfile<ClickAction> d,
					AbstractScene scn, String n) {
		super(fWProfile, fProfile, c, scn.eventHandler(), n);
		scene = scn;
		setDefaultGrabber(scn.eye().frame());
		camProfile = cProfile;
		camWheelProfile = cWProfile;
		camClickProfile = d;
	}

	/*
	 * Profile defining InteractiveEyeFrame action bindings from {@link
	 * remixlab.tersehandling.event.shortcut.ButtonShortcut}s.
	 */
	public P eyeProfile() {
		return camProfile;
	}

	/*
	 * Profile defining InteractiveFrame action bindings from {@link
	 * remixlab.tersehandling.event.shortcut.ButtonShortcut}s.
	 */
	public P frameProfile() {
		return profile();
	}

	/*
	 * Sets the {@link #eyeProfile()}.
	 */
	public void setEyeProfile(P profile) {
		camProfile = profile;
	}

	/*
	 * Sets the {@link #frameProfile()}.
	 */
	public void setFrameProfile(P profile) {
		setProfile(profile);
	}

	/*
	 * Profile defining InteractiveEyeFrame action bindings from {@link
	 * remixlab.tersehandling.event.shortcut.ClickShortcut}s.
	 */
	public ClickProfile<ClickAction> eyeClickProfile() {
		return camClickProfile;
	}

	/*
	 * Profile defining InteractiveFrame action bindings from {@link
	 * remixlab.tersehandling.event.shortcut.ClickShortcut}s.
	 */
	public ClickProfile<ClickAction> frameClickProfile() {
		return clickProfile;
	}

	/*
	 * Sets the {@link #eyeClickProfile()}.
	 */
	public void setEyeClickProfile(ClickProfile<ClickAction> profile) {
		camClickProfile = profile;
	}

	/*
	 * Sets the {@link #frameClickProfile()}.
	 */
	public void setFrameClickProfile(ClickProfile<ClickAction> profile) {
		setClickProfile(profile);
	}

	/*
	 * Profile defining InteractiveEyeFrame action bindings from (wheel) {@link
	 * remixlab.tersehandling.event.shortcut.ButtonShortcut}s.
	 */
	public MotionProfile<WheelAction> eyeWheelProfile() {
		return camWheelProfile;
	}

	/*
	 * Profile defining InteractiveFrame action bindings from (wheel) {@link
	 * remixlab.tersehandling.event.shortcut.ButtonShortcut}s.
	 */
	public MotionProfile<WheelAction> frameWheelProfile() {
		return wheelProfile;
	}

	/*
	 * Sets the {@link #eyeWheelProfile()}.
	 */
	public void setEyeWheelProfile(MotionProfile<WheelAction> profile) {
		camWheelProfile = profile;
	}

	/*
	 * Sets the {@link #frameWheelProfile()}.
	 */
	public void setFrameWheelProfile(MotionProfile<WheelAction> profile) {
		setWheelProfile(profile);
	}

	/**
	 * Calls {@link remixlab.tersehandling.generic.profile.Profile#removeAllBindings()} on all agent profiles.
	 */
	public void resetAllProfiles() {
		eyeClickProfile().removeAllBindings();
		eyeProfile().removeAllBindings();
		eyeWheelProfile().removeAllBindings();
		frameClickProfile().removeAllBindings();
		frameProfile().removeAllBindings();
		frameWheelProfile().removeAllBindings();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.generic.agent.GenericMotionAgent#motionProfile()
	 */
	@Override
	public P motionProfile() {
		if (grabber() instanceof InteractiveEyeFrame)
			return eyeProfile();
		if (grabber() instanceof InteractiveFrame)
			return frameProfile();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.generic.agent.GenericMotionAgent#clickProfile()
	 */
	@Override
	public ClickProfile<ClickAction> clickProfile() {
		if (grabber() instanceof InteractiveEyeFrame)
			return eyeClickProfile();
		if (grabber() instanceof InteractiveFrame)
			return frameClickProfile();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.generic.agent.GenericWheeledMotionAgent#wheelProfile()
	 */
	@Override
	public MotionProfile<WheelAction> wheelProfile() {
		if (grabber() instanceof InteractiveEyeFrame)
			return eyeWheelProfile();
		if (grabber() instanceof InteractiveFrame)
			return frameWheelProfile();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.generic.agent.GenericAgent#foreignGrabber()
	 */
	@Override
	protected boolean foreignGrabber() {
		return !(grabber() instanceof InteractiveFrame) && !(grabber() instanceof AbstractScene);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.generic.agent.GenericWheeledMotionAgent#info()
	 */
	@Override
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if (eyeClickProfile().description().length() != 0) {
			description += "Eye click shortcuts\n";
			description += eyeClickProfile().description();
		}
		if (frameClickProfile().description().length() != 0) {
			description += "Frame click shortcuts\n";
			description += frameClickProfile().description();
		}
		if (eyeProfile().description().length() != 0) {
			description += "Eye shortcuts\n";
			description += eyeProfile().description();
		}
		if (frameProfile().description().length() != 0) {
			description += "Frame shortcuts\n";
			description += frameProfile().description();
		}
		if (eyeWheelProfile().description().length() != 0) {
			description += "Eye wheel shortcuts\n";
			description += eyeWheelProfile().description();
		}
		if (frameWheelProfile().description().length() != 0) {
			description += "Frame wheel shortcuts\n";
			description += frameWheelProfile().description();
		}
		return description;
	}
}
