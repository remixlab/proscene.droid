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
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;

/**
 * A {@link remixlab.dandelion.agent.GenericWheeledBiMotionAgent} representing a Wheeled mouse and thus only holds 2
 * Degrees-Of-Freedom (e.g., two translations or two rotations), such as some Joysticks.
 */
public class MouseAgent extends GenericWheeledBiMotionAgent<GenericMotionProfile<Constants.DOF2Action>> {
	/**
	 * Constructs a MouseAgent and defined bindings as {@link #setAsArcball()}.
	 * 
	 * @param scn
	 *          AbstractScene
	 * @param n
	 *          Agents name
	 */
	public MouseAgent(AbstractScene scn, String n) {
		super(new GenericMotionProfile<WheelAction>(),
						new GenericMotionProfile<WheelAction>(),
						new GenericMotionProfile<DOF2Action>(),
						new GenericMotionProfile<DOF2Action>(),
						new GenericClickProfile<ClickAction>(),
						new GenericClickProfile<ClickAction>(), scn, n);

		setAsArcball();

		eyeClickProfile().setClickBinding(TH_NOMODIFIER_MASK, TH_LEFT, 2, ClickAction.ALIGN_FRAME);
		eyeClickProfile().setClickBinding(TH_NOMODIFIER_MASK, TH_RIGHT, 2, ClickAction.CENTER_FRAME);

		frameClickProfile().setClickBinding(TH_NOMODIFIER_MASK, TH_LEFT, 2, ClickAction.ALIGN_FRAME);
		frameClickProfile().setClickBinding(TH_NOMODIFIER_MASK, TH_RIGHT, 2, ClickAction.CENTER_FRAME);

		eyeWheelProfile().setBinding(TH_NOMODIFIER_MASK, TH_NOBUTTON, scn.is3D() ? WheelAction.ZOOM : WheelAction.SCALE);
		frameWheelProfile().setBinding(TH_NOMODIFIER_MASK, TH_NOBUTTON, WheelAction.SCALE);
	}

	/*
	 * Set the default InteractiveEye mouse bindings for the camera in first person mode. Only meaningful for 3D Scenes.
	 * Default bindings are defined as follows: <p> Left button -> MOVE_FORWARD<br> Center button -> LOOK_AROUND<br> Right
	 * button -> MOVE_BACKWARD<br> Shift + Left button -> ROLL<br> Shift + Center button -> DRIVE<br> Ctrl + Wheel ->
	 * ROLL<br> Shift + Wheel -> DRIVE<br>
	 */
	public void setAsFirstPerson() {
		if (scene.is2D()) {
			AbstractScene.showDepthWarning("setAsFirstPerson");
			return;
		}
		eyeProfile().setBinding(TH_NOMODIFIER_MASK, TH_LEFT, DOF2Action.MOVE_FORWARD);
		eyeProfile().setBinding(TH_NOMODIFIER_MASK, TH_CENTER, DOF2Action.LOOK_AROUND);
		eyeProfile().setBinding(TH_NOMODIFIER_MASK, TH_RIGHT, DOF2Action.MOVE_BACKWARD);
		eyeProfile().setBinding(TH_SHIFT, TH_LEFT, DOF2Action.ROLL);
		eyeProfile().setBinding(TH_SHIFT, TH_CENTER, DOF2Action.DRIVE);
		eyeWheelProfile().setBinding(TH_CTRL, TH_NOBUTTON, WheelAction.ROLL);
		eyeWheelProfile().setBinding(TH_SHIFT, TH_NOBUTTON, WheelAction.DRIVE);
	}

	/*
	 * Set the default InteractiveFrame mouse bindings for the camera in third person mode. Only meaningful for 3D Scenes.
	 * Default bindings are defined as follows: <p> Left button -> MOVE_FORWARD<br> Center button -> LOOK_AROUND<br> Right
	 * button -> MOVE_BACKWARD<br> Shift + Left button -> ROLL<br> Shift + Center button -> DRIVE<br>
	 */
	public void setAsThirdPerson() {
		if (scene.is2D()) {
			AbstractScene.showDepthWarning("setAsThirdPerson");
			return;
		}
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_LEFT, DOF2Action.MOVE_FORWARD);
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_CENTER, DOF2Action.LOOK_AROUND);
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_RIGHT, DOF2Action.MOVE_BACKWARD);
		frameProfile().setBinding(TH_SHIFT, TH_LEFT, DOF2Action.ROLL);
		frameProfile().setBinding(TH_SHIFT, TH_CENTER, DOF2Action.DRIVE);
	}

	/*
	 * Set the default mouse bindings for the Eye to rotate around a point (typically scene center) as follows: <p> 1.
	 * <b>InteractiveFrame bindings</b><br> Left button -> ROTATE<br> Center button -> SCALE<br> Right button ->
	 * TRANSLATE<br> Shift + Center button -> SCREEN_TRANSLATE<br> Shift + Right button -> SCREEN_ROTATE<br> <p> 2.
	 * <b>InteractiveEyeFrame bindings</b><br> Left button -> ROTATE<br> Center button -> ZOOM<br> Right button ->
	 * TRANSLATE<br> Shift + Left button -> ZOOM_ON_REGION<br> Shift + Center button -> SCREEN_TRANSLATE<br> Shift + Right
	 * button -> SCREEN_ROTATE
	 */
	public void setAsArcball() {
		eyeProfile().setBinding(TH_NOMODIFIER_MASK, TH_LEFT, DOF2Action.ROTATE);
		eyeProfile().setBinding(TH_NOMODIFIER_MASK, TH_CENTER, DOF2Action.ZOOM);
		eyeProfile().setBinding(TH_NOMODIFIER_MASK, TH_RIGHT, DOF2Action.TRANSLATE);
		eyeProfile().setBinding(TH_SHIFT, TH_LEFT, DOF2Action.ZOOM_ON_REGION);
		eyeProfile().setBinding(TH_SHIFT, TH_CENTER, DOF2Action.SCREEN_TRANSLATE);
		eyeProfile().setBinding(TH_SHIFT, TH_RIGHT, DOF2Action.SCREEN_ROTATE);

		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_LEFT, DOF2Action.ROTATE);
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_CENTER, DOF2Action.SCALE);
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_RIGHT, DOF2Action.TRANSLATE);
		frameProfile().setBinding(TH_SHIFT, TH_CENTER, DOF2Action.SCREEN_TRANSLATE);
		frameProfile().setBinding(TH_SHIFT, TH_RIGHT, DOF2Action.SCREEN_ROTATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.core.Agent#feed()
	 */
	@Override
	public GenericDOF2Event<Constants.DOF2Action> feed() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.dandelion.agent.GenericWheeledBiMotionAgent#eyeProfile()
	 */
	@Override
	public GenericMotionProfile<Constants.DOF2Action> eyeProfile() {
		return camProfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.dandelion.agent.GenericWheeledBiMotionAgent#frameProfile()
	 */
	@Override
	public GenericMotionProfile<Constants.DOF2Action> frameProfile() {
		return profile;
	}

	/*
	 * Sets the mouse translation sensitivity along X.
	 */
	public void setXTranslationSensitivity(float s) {
		sens[0] = s;
	}

	/*
	 * Sets the mouse translation sensitivity along Y.
	 */
	public void setYTranslationSensitivity(float s) {
		sens[1] = s;
	}
}
