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

import remixlab.bias.generic.event.ActionDOF6Event;
import remixlab.bias.generic.profile.ClickProfile;
import remixlab.bias.generic.profile.MotionProfile;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * A {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent} representing a Human Interface Device with 6
 * Degrees-Of-Freedom (three translations and three rotations), such as the Space Navigator or any MultiTouch device.
 */
public class HIDAgent extends ActionWheeledBiMotionAgent<MotionProfile<DOF6Action>> {
	/**
	 * Constructs an HIDAgent with the following bindings:
	 * <p>
	 * {@code eyeProfile().setBinding(B_NOMODIFIER_MASK, B_NOBUTTON, DOF6Action.TRANSLATE_ROTATE);}<br>
	 * {@code frameProfile().setBinding(B_NOMODIFIER_MASK, B_NOBUTTON, DOF6Action.TRANSLATE_ROTATE)}<br>
	 * 
	 * @param scn
	 *          AbstractScene
	 * @param n
	 *          name
	 */
	public HIDAgent(AbstractScene scn, String n) {
		super(new MotionProfile<WheelAction>(),
						new MotionProfile<WheelAction>(),
						new MotionProfile<DOF6Action>(),
						new MotionProfile<DOF6Action>(),
						new ClickProfile<ClickAction>(),
						new ClickProfile<ClickAction>(), scn, n);
		eyeProfile().setBinding(B_NOMODIFIER_MASK, B_NOBUTTON, DOF6Action.TRANSLATE_ROTATE);
		frameProfile().setBinding(B_NOMODIFIER_MASK, B_NOBUTTON, DOF6Action.TRANSLATE_ROTATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bias.core.Agent#feed()
	 */
	@Override
	public ActionDOF6Event<DOF6Action> feed() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.dandelion.agent.ActionWheeledBiMotionAgent#eyeProfile()
	 */
	@Override
	public MotionProfile<DOF6Action> eyeProfile() {
		return camProfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.dandelion.agent.ActionWheeledBiMotionAgent#frameProfile()
	 */
	@Override
	public MotionProfile<DOF6Action> frameProfile() {
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
