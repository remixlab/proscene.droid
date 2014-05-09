/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.agent;

import remixlab.bias.agent.profile.*;
import remixlab.bias.event.DOF2Event;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * An {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent} representing a Wheeled mouse and thus only holds 2
 * Degrees-Of-Freedom (e.g., two translations or two rotations), such as most mice.
 */
public class MouseAgent extends ActionWheeledBiMotionAgent<MotionProfile<DOF2Action>> {
	/**
	 * Constructs a MouseAgent and defined bindings as {@link #setAsArcball()}.
	 * 
	 * @param scn
	 *          AbstractScene
	 * @param n
	 *          Agents name
	 */
	public MouseAgent(AbstractScene scn, String n) {
		super(new MotionProfile<WheelAction>(),
				new MotionProfile<WheelAction>(),
				new MotionProfile<DOF2Action>(),
				new MotionProfile<DOF2Action>(),
				new ClickProfile<ClickAction>(),
				new ClickProfile<ClickAction>(), scn, n);

		setAsArcball();
	}

	/**
	 * Set the default InteractiveEye mouse bindings for the camera in first person mode. Only meaningful for 3D Scenes.
	 * Default bindings are defined as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * Left button -> ROTATE<br>
	 * Center button -> SCALE<br>
	 * Right button -> TRANSLATE<br>
	 * Shift + Center button -> SCREEN_TRANSLATE<br>
	 * Shift + Right button -> SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveEyeFrame bindings</b><br>
	 * Left button -> MOVE_FORWARD<br>
	 * Center button -> LOOK_AROUND<br>
	 * Right button -> MOVE_BACKWARD<br>
	 * Shift + Left button -> ROLL<br>
	 * Shift + Center button -> DRIVE<br>
	 * Ctrl + Wheel -> ROLL<br>
	 * Shift + Wheel -> DRIVE<br>
	 * <p>
	 * Then calls {@link #setCommonBindings()}.
	 */
	public void setAsFirstPerson() {
		resetAllProfiles();
		eyeProfile().setBinding(B_NOMODIFIER_MASK, B_LEFT, DOF2Action.MOVE_FORWARD);
		eyeProfile().setBinding(B_NOMODIFIER_MASK, B_RIGHT, DOF2Action.MOVE_BACKWARD);
		eyeProfile().setBinding(B_SHIFT, B_LEFT, DOF2Action.ROTATE_Z);
		eyeWheelProfile().setBinding(B_CTRL, B_NOBUTTON, WheelAction.ROTATE_Z);
		if (scene.is2D()) {
			eyeProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, DOF2Action.LOOK_AROUND);
			eyeProfile().setBinding(B_SHIFT, B_CENTER, DOF2Action.DRIVE);
		}
		// TODO PITCH, YAW?
		// eyeWheelProfile().setBinding(B_SHIFT, B_NOBUTTON, WheelAction.DRIVE);
		frameProfile().setBinding(B_NOMODIFIER_MASK, B_LEFT, DOF2Action.ROTATE);
		frameProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, DOF2Action.SCALE);
		frameProfile().setBinding(B_NOMODIFIER_MASK, B_RIGHT, DOF2Action.TRANSLATE);
		frameProfile().setBinding(B_SHIFT, B_CENTER, DOF2Action.SCREEN_TRANSLATE);
		frameProfile().setBinding(B_SHIFT, B_RIGHT, DOF2Action.SCREEN_ROTATE);
		setCommonBindings();
	}

	/**
	 * Set the default InteractiveFrame mouse bindings for the camera in third person mode. Only meaningful for 3D Scenes.
	 * Default bindings are defined as follows:
	 * <p>
	 * Left button -> MOVE_FORWARD<br>
	 * Center button -> LOOK_AROUND<br>
	 * Right button -> MOVE_BACKWARD<br>
	 * Shift + Left button -> ROLL<br>
	 * Shift + Center button -> DRIVE<br>
	 * <p>
	 * Then calls {@link #setCommonBindings()}.
	 */
	public void setAsThirdPerson() {
		resetAllProfiles();
		frameProfile().setBinding(B_NOMODIFIER_MASK, B_LEFT, DOF2Action.MOVE_FORWARD);
		frameProfile().setBinding(B_NOMODIFIER_MASK, B_RIGHT, DOF2Action.MOVE_BACKWARD);
		frameProfile().setBinding(B_SHIFT, B_LEFT, DOF2Action.ROTATE_Z);
		if (scene.is3D()) {
			frameProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, DOF2Action.LOOK_AROUND);
			frameProfile().setBinding(B_SHIFT, B_CENTER, DOF2Action.DRIVE);
		}
		setCommonBindings();
	}

	/**
	 * Set the default mouse bindings for the Eye to rotate around a point (typically scene center) as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * Left button -> ROTATE<br>
	 * Center button -> SCALE<br>
	 * Right button -> TRANSLATE<br>
	 * Shift + Center button -> SCREEN_TRANSLATE<br>
	 * Shift + Right button -> SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveEyeFrame bindings</b><br>
	 * Left button -> ROTATE<br>
	 * Center button -> ZOOM<br>
	 * Right button -> TRANSLATE<br>
	 * Shift + Left button -> ZOOM_ON_REGION<br>
	 * Shift + Center button -> SCREEN_TRANSLATE<br>
	 * Shift + Right button -> SCREEN_ROTATE.
	 * <p>
	 * Then calls {@link #setCommonBindings()}.
	 */
	public void setAsArcball() {
		resetAllProfiles();
		eyeProfile().setBinding(B_NOMODIFIER_MASK, B_LEFT, DOF2Action.ROTATE);
		eyeProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, scene.is3D() ? DOF2Action.ZOOM : DOF2Action.SCALE);
		eyeProfile().setBinding(B_NOMODIFIER_MASK, B_RIGHT, DOF2Action.TRANSLATE);
		eyeProfile().setBinding(B_SHIFT, B_LEFT, DOF2Action.ZOOM_ON_REGION);
		eyeProfile().setBinding(B_SHIFT, B_CENTER, DOF2Action.SCREEN_TRANSLATE);
		eyeProfile().setBinding(B_SHIFT, B_RIGHT, DOF2Action.SCREEN_ROTATE);
		frameProfile().setBinding(B_NOMODIFIER_MASK, B_LEFT, DOF2Action.ROTATE);
		frameProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, DOF2Action.SCALE);
		frameProfile().setBinding(B_NOMODIFIER_MASK, B_RIGHT, DOF2Action.TRANSLATE);
		frameProfile().setBinding(B_SHIFT, B_CENTER, DOF2Action.SCREEN_TRANSLATE);
		frameProfile().setBinding(B_SHIFT, B_RIGHT, DOF2Action.SCREEN_ROTATE);
		setCommonBindings();
	}

	/**
	 * Common bindings are: 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel -> SCALE for both, InteractiveFrame and InteractiveEyeFrame.
	 */
	protected void setCommonBindings() {
		eyeClickProfile().setClickBinding(B_NOMODIFIER_MASK, B_LEFT, 2, ClickAction.ALIGN_FRAME);
		eyeClickProfile().setClickBinding(B_NOMODIFIER_MASK, B_RIGHT, 2, ClickAction.CENTER_FRAME);
		frameClickProfile().setClickBinding(B_NOMODIFIER_MASK, B_LEFT, 2, ClickAction.ALIGN_FRAME);
		frameClickProfile().setClickBinding(B_NOMODIFIER_MASK, B_RIGHT, 2, ClickAction.CENTER_FRAME);
		eyeWheelProfile().setBinding(B_NOMODIFIER_MASK, B_NOBUTTON, scene.is3D() ? WheelAction.ZOOM : WheelAction.SCALE);
		frameWheelProfile().setBinding(B_NOMODIFIER_MASK, B_NOBUTTON, WheelAction.SCALE);
	}

	@Override
	public DOF2Event feed() {
		return null;
	}

	@Override
	public MotionProfile<DOF2Action> eyeProfile() {
		return camProfile;
	}

	@Override
	public MotionProfile<DOF2Action> frameProfile() {
		return profile;
	}

	/**
	 * Sets the mouse translation sensitivity along X.
	 */
	public void setXTranslationSensitivity(float s) {
		sens[0] = s;
	}

	/**
	 * Sets the mouse translation sensitivity along Y.
	 */
	public void setYTranslationSensitivity(float s) {
		sens[1] = s;
	}
}
