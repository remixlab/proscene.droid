/*******************************************************************************
 * dandelion (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.dandelion.agent;

import remixlab.dandelion.core.*;
import remixlab.tersehandling.generic.agent.*;
import remixlab.tersehandling.generic.profile.*;

/**
 * A GenericWheeledMotionAgent that handles Dandelion motion actions.
 * <p>
 * Dandelion actions can be handled by an AbstractScene, an InteractiveFrame or 
 * by an InteractiveEyeFrame. This class implements a generic Agent that represents
 * any Human Interface Device (such as a mouse or a Joystick) and that handles actions
 * to be executed only by an InteractiveFrame or an InteractiveEyeFrame
 * (AbstractScene actions are handled exclusively by a KeyboardAgent).
 * <p>
 * The agent uses its profiles (see below) to parse the TerseEvent to obtain a
 * dandelion action, which is then sent to the proper ({@link #grabber()}) Frame
 * (InteractiveFrame or InteractiveEyeFrame) for its final execution. In case the
 * grabber is not an instance of a Frame, but a different object which behavior you
 * implemented ({@link #foreignGrabber()}), the agent sends the raw TerseEvent to it
 * (please refer to the mouse grabber example).
 * <p>
 * This agent holds the following InteractiveFrame Profiles: a {@link #frameProfile()},
 * a {@link #frameClickProfile()}, and a {@link #frameWheelProfile()}; together with its
 * InteractiveEyeFrame counterparts: a {@link #eyeProfile()},
 * a {@link #eyeClickProfile()}, and a {@link #eyeWheelProfile()}. Simply retrieve
 * a specific profile to bind an action to a shortcut, to remove it, or to check your
 * current bindings. Default bindings are provided for convenience.
 * <p>
 * Note that shortcuts are also specialized and tied to a specific profile:
 * ButtonShortcuts which are tied to the {@link #frameProfile()},  the {@link #eyeProfile()},
 * the {@link #frameWheelProfile()} and the {@link #eyeWheelProfile()}; and, ClickShortcuts which
 * are tied to the {@link #frameClickProfile()} and the {@link #eyeClickProfile()}.
 * 
 * @author pierre
 *
 * @param <P> GenericMotionProfile parameterised with a Dandelion action 
 */
public class GenericWheeledBiMotionAgent<P extends GenericMotionProfile<?>> extends GenericWheeledMotionAgent<GenericMotionProfile<Constants.WheelAction>,
                                                                           																		P,
                                                                           																		GenericClickProfile<Constants.ClickAction>> implements Constants {
	protected P camProfile;
	protected GenericMotionProfile<WheelAction> camWheelProfile;
	protected GenericClickProfile<ClickAction> camClickProfile;
	
	public GenericWheeledBiMotionAgent(GenericMotionProfile<WheelAction> fWProfile,
			                               GenericMotionProfile<WheelAction> cWProfile,
			                               P fProfile,
			                               P cProfile,
			                               GenericClickProfile<ClickAction> c,
																		 GenericClickProfile<ClickAction> d,
																		 AbstractScene scn, String n) {
		super(fWProfile, fProfile, c, scn.terseHandler(), n);
		setDefaultGrabber(scn.eye().frame());
		camProfile = cProfile;
		camWheelProfile = cWProfile;
		camClickProfile = d;
	}
	
	/*
	 * Profile defining InteractiveEyeFrame action bindings from ButtonShortcuts.
	 */
	public P eyeProfile() {
		return camProfile;
	}
	
	/*
	 * Profile defining InteractiveFrame action bindings from ButtonShortcuts.
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
	 * Profile defining InteractiveEyeFrame action bindings from ClickShortcuts.
	 */
	public GenericClickProfile<ClickAction> eyeClickProfile() {
		return camClickProfile;
	}
	
	/*
	 * Profile defining InteractiveFrame action bindings from ClickShortcuts.
	 */
	public GenericClickProfile<ClickAction> frameClickProfile() {
		return clickProfile;
	}
	
	/*
	 * Sets the {@link #eyeClickProfile()}.
	 */
	public void setEyeClickProfile(GenericClickProfile<ClickAction> profile) {
		camClickProfile = profile;
	}
	
	/*
	 * Sets the {@link #frameClickProfile()}.
	 */
	public void setFrameClickProfile(GenericClickProfile<ClickAction> profile) {
		setClickProfile(profile);
	}
	
	/*
	 * Profile defining InteractiveEyeFrame action bindings from (wheel) ButtonShortcuts.
	 */
	public GenericMotionProfile<WheelAction> eyeWheelProfile() {
		return camWheelProfile;
	}
	
	/*
	 * Profile defining InteractiveFrame action bindings from (wheel) ButtonShortcuts.
	 */
	public GenericMotionProfile<WheelAction> frameWheelProfile() {
		return wheelProfile;
	}
	
	/*
	 * Sets the {@link #eyeWheelProfile()}.
	 */
	public void setEyeWheelProfile(GenericMotionProfile<WheelAction> profile) {
		camWheelProfile = profile;
	}
	
	/*
	 * Sets the {@link #frameWheelProfile()}.
	 */
	public void setFrameWheelProfile(GenericMotionProfile<WheelAction> profile) {
		setWheelProfile(profile);
	}
	
	/**
	 * (non-Javadoc)
	 * @see remixlab.tersehandling.generic.agent.GenericMotionAgent#motionProfile()
	 */
	@Override
	public P motionProfile() {
		if( grabber() instanceof InteractiveEyeFrame )
			return eyeProfile();
		if( grabber() instanceof InteractiveFrame )
			return frameProfile();					
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see remixlab.tersehandling.generic.agent.GenericMotionAgent#clickProfile()
	 */
	@Override
	public GenericClickProfile<ClickAction> clickProfile() {
		if( grabber() instanceof InteractiveEyeFrame )
			return eyeClickProfile();
		if( grabber() instanceof InteractiveFrame )
			return frameClickProfile();					
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see remixlab.tersehandling.generic.agent.GenericWheeledMotionAgent#wheelProfile()
	 */
	@Override
	public GenericMotionProfile<WheelAction> wheelProfile() {
		if( grabber() instanceof InteractiveEyeFrame )
			return eyeWheelProfile();
		if( grabber() instanceof InteractiveFrame )
			return frameWheelProfile();					
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see remixlab.tersehandling.generic.agent.GenericActionableAgent#foreignGrabber()
	 */
	@Override
	protected boolean foreignGrabber() {
		return !( grabber() instanceof InteractiveFrame ) && !( grabber() instanceof AbstractScene);
	}
	
	/*
	 * (non-Javadoc)
	 * @see remixlab.tersehandling.generic.agent.GenericWheeledMotionAgent#info()
	 */
	@Override
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if( eyeClickProfile().description().length() != 0 ) {
			description += "Eye click shortcuts\n";
			description += eyeClickProfile().description();
		}
		if( frameClickProfile().description().length() != 0 ) {
			description += "Frame click shortcuts\n";
			description += frameClickProfile().description();
		}		
		if( eyeProfile().description().length() != 0 ) {
			description += "Eye shortcuts\n";
			description += eyeProfile().description();
		}
		if( frameProfile().description().length() != 0 ) {
			description += "Frame shortcuts\n";
			description += frameProfile().description();
		}
		if( eyeWheelProfile().description().length() != 0 ) {
			description += "Eye wheel shortcuts\n";
			description += eyeWheelProfile().description();
		}
		if( frameWheelProfile().description().length() != 0 ) {
			description += "Frame wheel shortcuts\n";
			description += frameWheelProfile().description();
		}		
		return description;
	}
}
