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
import remixlab.tersehandling.generic.event.GenericDOF6Event;
import remixlab.tersehandling.generic.profile.GenericClickProfile;
import remixlab.tersehandling.generic.profile.GenericMotionProfile;

/**
 * A {@link remixlab.dandelion.agent.GenericWheeledBiMotionAgent} representing a Human Interface Device with 6
 * Degrees-Of-Freedom (three translations and three rotations), such as the Space Navigator or any MultiTouch device.
 */
public class HIDAgent extends GenericWheeledBiMotionAgent<GenericMotionProfile<Constants.DOF6Action>> {
	/**
	 * Constructs an HIDAgent with the following bindings:
	 * <p>
	 * {@code eyeProfile().setBinding(TH_NOMODIFIER_MASK, TH_NOBUTTON, DOF6Action.TRANSLATE_ROTATE);}<br>
	 * {@code frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_NOBUTTON, DOF6Action.TRANSLATE_ROTATE)}<br>
	 * 
	 * @param scn
	 *          AbstractScene
	 * @param n
	 *          name
	 */
	public HIDAgent(AbstractScene scn, String n) {
		super(new GenericMotionProfile<WheelAction>(),
						new GenericMotionProfile<WheelAction>(),
						new GenericMotionProfile<DOF6Action>(),
						new GenericMotionProfile<DOF6Action>(),
						new GenericClickProfile<ClickAction>(),
						new GenericClickProfile<ClickAction>(), scn, n);
		eyeProfile().setBinding(TH_NOMODIFIER_MASK, TH_NOBUTTON, DOF6Action.TRANSLATE_ROTATE);
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_NOBUTTON, DOF6Action.TRANSLATE_ROTATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.core.Agent#feed()
	 */
	@Override
	public GenericDOF6Event<Constants.DOF6Action> feed() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.dandelion.agent.GenericWheeledBiMotionAgent#eyeProfile()
	 */
	@Override
	public GenericMotionProfile<Constants.DOF6Action> eyeProfile() {
		return camProfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.dandelion.agent.GenericWheeledBiMotionAgent#frameProfile()
	 */
	@Override
	public GenericMotionProfile<Constants.DOF6Action> frameProfile() {
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

	/*
	 * Sets the rotation sensitivity along X.
	 */
	public void setXRotationSensitivity(float s) {
		sens[3] = s;
	}

	/*
	 * Sets the rotation sensitivity along Y.
	 */
	public void setYRotationSensitivity(float s) {
		sens[4] = s;
	}

	/*
	 * Sets the rotation sensitivity along Z.
	 */
	public void setZRotationSensitivity(float s) {
		sens[5] = s;
	}
}
