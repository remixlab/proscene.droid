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
	
	public P cameraProfile() {
		return camProfile;
	}
	
	public P frameProfile() {
		return profile();
	}
	
	public void setCameraProfile(P profile) {
		camProfile = profile;
	}
	
	public void setFrameProfile(P profile) {
		setProfile(profile);
	}
	
	public GenericClickProfile<ClickAction> cameraClickProfile() {
		return camClickProfile;
	}
	
	public GenericClickProfile<ClickAction> frameClickProfile() {
		return clickProfile;
	}
	
	public void setCameraClickProfile(GenericClickProfile<ClickAction> profile) {
		camClickProfile = profile;
	}
	
	public void setFrameClickProfile(GenericClickProfile<ClickAction> profile) {
		setClickProfile(profile);
	}
	
	public GenericMotionProfile<WheelAction> cameraWheelProfile() {
		return camWheelProfile;
	}
	
	public GenericMotionProfile<WheelAction> frameWheelProfile() {
		return wheelProfile;
	}
	
	public void setCameraWheelProfile(GenericMotionProfile<WheelAction> profile) {
		camWheelProfile = profile;
	}
	
	public void setFrameWheelProfile(GenericMotionProfile<WheelAction> profile) {
		setWheelProfile(profile);
	}
	
	@Override
	public P motionProfile() {
		if( grabber() instanceof InteractiveEyeFrame )
			return cameraProfile();
		if( grabber() instanceof InteractiveFrame )
			return frameProfile();					
		return null;
	}
	
	@Override
	public GenericClickProfile<ClickAction> clickProfile() {
		if( grabber() instanceof InteractiveEyeFrame )
			return cameraClickProfile();
		if( grabber() instanceof InteractiveFrame )
			return frameClickProfile();					
		return null;
	}
	
	@Override
	public GenericMotionProfile<WheelAction> wheelProfile() {
		if( grabber() instanceof InteractiveEyeFrame )
			return cameraWheelProfile();
		if( grabber() instanceof InteractiveFrame )
			return frameWheelProfile();					
		return null;
	}
	
	@Override
	protected boolean foreignGrabber() {
		return !( grabber() instanceof InteractiveFrame ) && !( grabber() instanceof AbstractScene);
	}
	
	@Override
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if( cameraClickProfile().description().length() != 0 ) {
			description += "Camera click shortcuts\n";
			description += cameraClickProfile().description();
		}
		if( frameClickProfile().description().length() != 0 ) {
			description += "Frame click shortcuts\n";
			description += frameClickProfile().description();
		}		
		if( cameraProfile().description().length() != 0 ) {
			description += "Camera shortcuts\n";
			description += cameraProfile().description();
		}
		if( frameProfile().description().length() != 0 ) {
			description += "Frame shortcuts\n";
			description += frameProfile().description();
		}
		if( cameraWheelProfile().description().length() != 0 ) {
			description += "Camera wheel shortcuts\n";
			description += cameraWheelProfile().description();
		}
		if( frameWheelProfile().description().length() != 0 ) {
			description += "Frame wheel shortcuts\n";
			description += frameWheelProfile().description();
		}		
		return description;
	}
}
