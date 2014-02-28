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

import remixlab.dandelion.core.AbstractScene;
import remixlab.dandelion.core.Constants;
import remixlab.tersehandling.generic.profile.ClickProfile;
import remixlab.tersehandling.generic.profile.MotionProfile;

/**
 * A {@link remixlab.dandelion.agent.GenericWheeledBiMotionAgent} representing a Human Interface Device with 3
 * Degrees-Of-Freedom (e.g., three translations or three rotations), such as some Joysticks.
 */
public class JoystickAgent extends GenericWheeledBiMotionAgent<MotionProfile<Constants.DOF3Action>> {
	public JoystickAgent(AbstractScene scn, String n) {
		super(new MotionProfile<WheelAction>(),
						new MotionProfile<WheelAction>(),
						new MotionProfile<DOF3Action>(),
						new MotionProfile<DOF3Action>(),
						new ClickProfile<ClickAction>(),
						new ClickProfile<ClickAction>(), scn, n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.dandelion.agent.GenericWheeledBiMotionAgent#eyeProfile()
	 */
	@Override
	public MotionProfile<Constants.DOF3Action> eyeProfile() {
		return camProfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.dandelion.agent.GenericWheeledBiMotionAgent#frameProfile()
	 */
	@Override
	public MotionProfile<Constants.DOF3Action> frameProfile() {
		return profile;
	}

	/*
	 * Sets the translation sensitivity along X.
	 */
	public void setXTranslationSensitivity(float s) {
		sens[0] = s;
	}

	/*
	 * Sets the translation sensitivity along Y.
	 */
	public void setYTranslationSensitivity(float s) {
		sens[1] = s;
	}

	/*
	 * Sets the translation sensitivity along Z.
	 */
	public void setZTranslationSensitivity(float s) {
		sens[2] = s;
	}
}
