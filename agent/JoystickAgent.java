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

import remixlab.dandelion.core.AbstractScene;
import remixlab.dandelion.core.Constants;
//import remixlab.dandelion.core.InteractiveCameraFrame;
//import remixlab.dandelion.core.InteractiveFrame;
//import remixlab.tersehandling.core.Grabbable;
import remixlab.tersehandling.generic.profile.GenericClickProfile;
import remixlab.tersehandling.generic.profile.GenericMotionProfile;

public class JoystickAgent extends GenericWheeledBiMotionAgent<GenericMotionProfile<Constants.DOF3Action>> {
	public JoystickAgent(AbstractScene scn, String n) {
		super(new GenericMotionProfile<WheelAction>(),
			    new GenericMotionProfile<WheelAction>(),
	        new GenericMotionProfile<DOF3Action>(),
			    new GenericMotionProfile<DOF3Action>(),
			    new GenericClickProfile<ClickAction>(),
			    new GenericClickProfile<ClickAction>(), scn, n);
	}
	
	@Override
	public GenericMotionProfile<Constants.DOF3Action> cameraProfile() {
		return camProfile;
	}
	
	@Override
	public GenericMotionProfile<Constants.DOF3Action> frameProfile() {
		return profile;
	}
	
	public void setXTranslationSensitivity(float s) {
		sens[0] = s;
	}
	
	public void setYTranslationSensitivity(float s) {
		sens[1] = s;
	}
	
	public void setZTranslationSensitivity(float s) {
		sens[2] = s;
	}
}
