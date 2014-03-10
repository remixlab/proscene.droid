/*******************************************************************************
 * dandelion_tree (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package remixlab.dandelion.core;

import remixlab.bias.core.Action;
import remixlab.bias.core.EventConstants;

public interface Constants extends EventConstants {
	/**
	 * Visual hints as "the last shall be first"
	 */
	final static int		AXIS				= 1 << 0;
	final static int		GRID				= 1 << 1;
	final static int		FRAME				= 1 << 2;
	final static int		PATHS				= 1 << 3;
	final static int		ZOOM				= 1 << 4;					// prosceneMouse.zoomOnRegion
	final static int		ROTATE			= 1 << 5;					// prosceneMouse.screenRotate
	// final static int PUP = 1 << 6;
	// final static int ARP = 1 << 7;

	/**
	 * PI is a mathematical constant with the value 3.14159265358979323846. It is the ratio of the circumference of a
	 * circle to its diameter. It is useful in combination with the trigonometric functions <b>sin()</b> and <b>cos()</b>.
	 * 
	 * @see #HALF_PI
	 * @see #TWO_PI
	 * @see #QUARTER_PI
	 * 
	 */
	static final float	PI					= (float) Math.PI;
	/**
	 * HALF_PI is a mathematical constant with the value 1.57079632679489661923. It is half the ratio of the circumference
	 * of a circle to its diameter. It is useful in combination with the trigonometric functions <b>sin()</b> and
	 * <b>cos()</b>.
	 * 
	 * @see #PI
	 * @see #TWO_PI
	 * @see #QUARTER_PI
	 */
	static final float	HALF_PI			= PI / 2.0f;
	static final float	THIRD_PI		= PI / 3.0f;
	/**
	 * QUARTER_PI is a mathematical constant with the value 0.7853982. It is one quarter the ratio of the circumference of
	 * a circle to its diameter. It is useful in combination with the trigonometric functions <b>sin()</b> and
	 * <b>cos()</b>.
	 * 
	 * @see #PI
	 * @see #TWO_PI
	 * @see #HALF_PI
	 */
	static final float	QUARTER_PI	= PI / 4.0f;
	/**
	 * TWO_PI is a mathematical constant with the value 6.28318530717958647693. It is twice the ratio of the circumference
	 * of a circle to its diameter. It is useful in combination with the trigonometric functions <b>sin()</b> and
	 * <b>cos()</b>.
	 * 
	 * @see #PI
	 * @see #HALF_PI
	 * @see #QUARTER_PI
	 */
	static final float	TWO_PI			= PI * 2.0f;

	// Actions
	public enum DandelionAction {
		// KEYfRAMES
		ADD_KEYFRAME_TO_PATH_1("Add keyframe to path 1", true, 0),
		PLAY_PATH_1("Play keyframe path 1", true, 0),
		DELETE_PATH_1("Delete keyframepath 1", true, 0),
		ADD_KEYFRAME_TO_PATH_2("Add keyframe to path 2", true, 0),
		PLAY_PATH_2("Play keyframe path 2", true, 0),
		DELETE_PATH_2("Delete keyframepath 2", true, 0),
		ADD_KEYFRAME_TO_PATH_3("Add keyframe to path 3", true, 0),
		PLAY_PATH_3("Play keyframe path 3", true, 0),
		DELETE_PATH_3("Delete keyframepath 3", true, 0),

		// CLICk ACTIONs
		CENTER_FRAME("Center frame", true, 0),
		ALIGN_FRAME("Align frame with world", true, 0),

		// Click actions require cursor pos:
		ZOOM_ON_PIXEL("Interpolate the camera to zoom on pixel", true, 0),
		ANCHOR_FROM_PIXEL("Set the anchor from the pixel under the mouse", true, 0),

		// GENERAL KEYBOARD ACTIONs
		// TODO reconsider renaming -> for toggle, see: AbstractScene.execAction
		DRAW_AXIS("Toggles the display of the world axis", true, 0),
		DRAW_GRID("Toggles the display of the XY grid", true, 0),
		CAMERA_TYPE("Toggles camera type (orthographic or perspective)", false, 0),
		ANIMATION("Toggles animation", true, 0),
		INTERPOLATE_TO_FIT("Zoom to fit the scene", true, 0),
		RESET_ANCHOR("Reset the anchor to the 3d frame world origin", true, 0),
		GLOBAL_HELP("Displays the global help", true, 0),
		EDIT_EYE_PATH("Toggles the key frame camera paths (if any) for edition", true, 0),
		DRAW_FRAME_SELECTION_HINT("Toggle interactive frame selection region drawing", true, 0),
		SHOW_ALL("Show the whole scene", true, 0),

		// CAMERA KEYBOARD ACTIONs // TODO all of these could be dof_1
		MOVE_EYE_LEFT("Move camera to the left", true, 0),
		MOVE_EYE_RIGHT("Move camera to the right", true, 0),
		MOVE_EYE_UP("Move camera up", true, 0),
		MOVE_EYE_DOWN("Move camera down", true, 0),
		INCREASE_ROTATION_SENSITIVITY("Increase camera rotation sensitivity (only meaningful in arcball mode)", true, 0),
		DECREASE_ROTATION_SENSITIVITY("Decrease camera rotation sensitivity (only meaningful in arcball mode)", true, 0),
		INCREASE_CAMERA_FLY_SPEED("Increase camera fly speed (only meaningful in first-person mode)", false, 0),
		DECREASE_CAMERA_FLY_SPEED("Decrease camera fly speed (only meaningful in first-person mode)", false, 0),
		INCREASE_AVATAR_FLY_SPEED("Increase avatar fly speed (only meaningful in third-person mode)", false, 0),
		DECREASE_AVATAR_FLY_SPEED("Decrease avatar fly speed (only meaningful in third-person mode)", false, 0),
		INCREASE_AZYMUTH("Increase camera azymuth respect to the avatar (only meaningful in third-person mode)", false, 0),
		DECREASE_AZYMUTH("Decrease camera azymuth respect to the avatar (only meaningful in third-person mode)", false, 0),
		INCREASE_INCLINATION("Increase camera inclination respect to the avatar (only meaningful in third-person mode)",
				false, 0),
		DECREASE_INCLINATION("Decrease camera inclination respect to the avatar (only meaningful in third-person mode)",
				false, 0),
		INCREASE_TRACKING_DISTANCE(
				"Increase camera tracking distance respect to the avatar (only meaningful in third-person mode)", false, 0),
		DECREASE_TRACKING_DISTANCE(
				"Decrease camera tracking distance respect to the avatar (only meaningful in third-person mode)", false, 0),

		// Wheel
		SCALE("Scale", true, 1),
		ZOOM("Zoom", false, 1),
		ROLL("Roll frame (camera or interactive frame)", true, 1),
		DRIVE("Drive (camera or interactive frame)", false, 1),

		// DEVICE ACTIONs
		ROTATE("Rotate frame (camera or interactive frame)", true, 2),
		CAD_ROTATE("Rotate camera frame as in CAD applications", false, 2),
		TRANSLATE("Translate frame (camera or interactive frame)", true, 2),
		MOVE_FORWARD("Move forward frame (camera or interactive frame)", false, 2),
		MOVE_BACKWARD("move backward frame (camera or interactive frame)", false, 2),
		LOOK_AROUND("Look around with frame (camera or interactive frame)", false, 2),
		SCREEN_ROTATE("Screen rotate (camera or interactive frame)", true, 2),
		SCREEN_TRANSLATE("Screen translate frame (camera or interactive frame)", true, 2),
		ZOOM_ON_REGION("Zoom on region (camera or interactive frame)", true, 2),

		TRANSLATE3("Translate frame (camera or interactive frame) from dx, dy, dz simultaneously", false, 3),
		ROTATE3("Rotate frame (camera or interactive frame) from Euler angles", false, 3),

		// GOOGLE_EARTH("Google earth emulation", false, 6),
		TRANSLATE_ROTATE("Natural (camera or interactive frame)", false, 6),

		// CUSTOM ACTIONs
		CUSTOM("User defined action");

		String	description;
		boolean	twoD;
		int			dofs;

		DandelionAction(String description, boolean td, int ds) {
			this.description = description;
			this.twoD = td;
			this.dofs = ds;
		}

		DandelionAction(String description, int ds) {
			this.description = description;
			this.twoD = true;
			this.dofs = ds;
		}

		DandelionAction(String description, boolean td) {
			this.description = description;
			this.twoD = td;
			this.dofs = 2;
		}

		DandelionAction(String description) {
			this.description = description;
			this.twoD = true;
			this.dofs = 0;
		}

		public String description() {
			return description;
		}

		public boolean is2D() {
			return twoD;
		}

		public int dofs() {
			return dofs;
		}
	}

	public enum ClickAction implements Action<DandelionAction> {
		// KEYfRAMES
		ADD_KEYFRAME_TO_PATH_1(DandelionAction.ADD_KEYFRAME_TO_PATH_1),
		PLAY_PATH_1(DandelionAction.PLAY_PATH_1),
		DELETE_PATH_1(DandelionAction.DELETE_PATH_1),
		ADD_KEYFRAME_TO_PATH_2(DandelionAction.ADD_KEYFRAME_TO_PATH_2),
		PLAY_PATH_2(DandelionAction.PLAY_PATH_2),
		DELETE_PATH_2(DandelionAction.DELETE_PATH_2),
		ADD_KEYFRAME_TO_PATH_3(DandelionAction.ADD_KEYFRAME_TO_PATH_3),
		PLAY_PATH_3(DandelionAction.PLAY_PATH_3),
		DELETE_PATH_3(DandelionAction.DELETE_PATH_3),

		// CLICk ACTIONs
		INTERPOLATE_TO_FIT(DandelionAction.INTERPOLATE_TO_FIT),
		CENTER_FRAME(DandelionAction.CENTER_FRAME),
		ALIGN_FRAME(DandelionAction.ALIGN_FRAME),

		// Click actions require cursor pos:
		ZOOM_ON_PIXEL(DandelionAction.ZOOM_ON_PIXEL),
		ANCHOR_FROM_PIXEL(DandelionAction.ANCHOR_FROM_PIXEL),

		// GENERAL KEYBOARD ACTIONs
		DRAW_AXIS(DandelionAction.DRAW_AXIS),
		DRAW_GRID(DandelionAction.DRAW_GRID),
		// CAMERA_PROFILE(DandelionAction.CAMERA_PROFILE),
		CAMERA_TYPE(DandelionAction.CAMERA_TYPE),
		ANIMATION(DandelionAction.ANIMATION),
		RESET_ANCHOR(DandelionAction.RESET_ANCHOR),
		GLOBAL_HELP(DandelionAction.GLOBAL_HELP),
		// CURRENT_CAMERA_PROFILE_HELP(DandelionAction.CURRENT_CAMERA_PROFILE_HELP),
		EDIT_EYE_PATH(DandelionAction.EDIT_EYE_PATH),
		// FOCUS_INTERACTIVE_FRAME(DandelionAction.FOCUS_INTERACTIVE_FRAME),
		DRAW_FRAME_SELECTION_HINT(DandelionAction.DRAW_FRAME_SELECTION_HINT),
		// CONSTRAIN_FRAME(DandelionAction.CONSTRAIN_FRAME),
		SHOW_ALL(DandelionAction.SHOW_ALL),

		// CAMERA KEYBOARD ACTIONs
		MOVE_EYE_LEFT(DandelionAction.MOVE_EYE_LEFT),
		MOVE_EYE_RIGHT(DandelionAction.MOVE_EYE_RIGHT),
		MOVE_EYE_UP(DandelionAction.MOVE_EYE_UP),
		MOVE_EYE_DOWN(DandelionAction.MOVE_EYE_DOWN),
		INCREASE_ROTATION_SENSITIVITY(DandelionAction.INCREASE_ROTATION_SENSITIVITY),
		DECREASE_ROTATION_SENSITIVITY(DandelionAction.DECREASE_ROTATION_SENSITIVITY),
		INCREASE_CAMERA_FLY_SPEED(DandelionAction.INCREASE_CAMERA_FLY_SPEED),
		DECREASE_CAMERA_FLY_SPEED(DandelionAction.DECREASE_CAMERA_FLY_SPEED),
		INCREASE_AVATAR_FLY_SPEED(DandelionAction.INCREASE_AVATAR_FLY_SPEED),
		DECREASE_AVATAR_FLY_SPEED(DandelionAction.DECREASE_AVATAR_FLY_SPEED),
		INCREASE_AZYMUTH(DandelionAction.INCREASE_AZYMUTH),
		DECREASE_AZYMUTH(DandelionAction.DECREASE_AZYMUTH),
		INCREASE_INCLINATION(DandelionAction.INCREASE_INCLINATION),
		DECREASE_INCLINATION(DandelionAction.DECREASE_INCLINATION),
		INCREASE_TRACKING_DISTANCE(DandelionAction.INCREASE_TRACKING_DISTANCE),
		DECREASE_TRACKING_DISTANCE(DandelionAction.DECREASE_TRACKING_DISTANCE),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		ClickAction(DandelionAction a) {
			act = a;
		}
	}

	public enum KeyboardAction implements Action<DandelionAction> {
		// KEYfRAMES
		ADD_KEYFRAME_TO_PATH_1(DandelionAction.ADD_KEYFRAME_TO_PATH_1),
		PLAY_PATH_1(DandelionAction.PLAY_PATH_1),
		DELETE_PATH_1(DandelionAction.DELETE_PATH_1),
		ADD_KEYFRAME_TO_PATH_2(DandelionAction.ADD_KEYFRAME_TO_PATH_2),
		PLAY_PATH_2(DandelionAction.PLAY_PATH_2),
		DELETE_PATH_2(DandelionAction.DELETE_PATH_2),
		ADD_KEYFRAME_TO_PATH_3(DandelionAction.ADD_KEYFRAME_TO_PATH_3),
		PLAY_PATH_3(DandelionAction.PLAY_PATH_3),
		DELETE_PATH_3(DandelionAction.DELETE_PATH_3),

		// CLICk ACTIONs
		INTERPOLATE_TO_FIT(DandelionAction.INTERPOLATE_TO_FIT),

		// GENERAL KEYBOARD ACTIONs
		DRAW_AXIS(DandelionAction.DRAW_AXIS),
		DRAW_GRID(DandelionAction.DRAW_GRID),
		// CAMERA_PROFILE(DandelionAction.CAMERA_PROFILE),
		CAMERA_TYPE(DandelionAction.CAMERA_TYPE),
		ANIMATION(DandelionAction.ANIMATION),
		RESET_ANCHOR(DandelionAction.RESET_ANCHOR),
		GLOBAL_HELP(DandelionAction.GLOBAL_HELP),
		// CURRENT_CAMERA_PROFILE_HELP(DandelionAction.CURRENT_CAMERA_PROFILE_HELP),
		EDIT_EYE_PATH(DandelionAction.EDIT_EYE_PATH),
		// FOCUS_INTERACTIVE_FRAME(DandelionAction.FOCUS_INTERACTIVE_FRAME),
		DRAW_FRAME_SELECTION_HINT(DandelionAction.DRAW_FRAME_SELECTION_HINT),
		// CONSTRAIN_FRAME(DandelionAction.CONSTRAIN_FRAME),
		SHOW_ALL(DandelionAction.SHOW_ALL),

		// CAMERA KEYBOARD ACTIONs
		MOVE_EYE_LEFT(DandelionAction.MOVE_EYE_LEFT),
		MOVE_EYE_RIGHT(DandelionAction.MOVE_EYE_RIGHT),
		MOVE_EYE_UP(DandelionAction.MOVE_EYE_UP),
		MOVE_EYE_DOWN(DandelionAction.MOVE_EYE_DOWN),
		INCREASE_ROTATION_SENSITIVITY(DandelionAction.INCREASE_ROTATION_SENSITIVITY),
		DECREASE_ROTATION_SENSITIVITY(DandelionAction.DECREASE_ROTATION_SENSITIVITY),
		INCREASE_CAMERA_FLY_SPEED(DandelionAction.INCREASE_CAMERA_FLY_SPEED),
		DECREASE_CAMERA_FLY_SPEED(DandelionAction.DECREASE_CAMERA_FLY_SPEED),
		INCREASE_AVATAR_FLY_SPEED(DandelionAction.INCREASE_AVATAR_FLY_SPEED),
		DECREASE_AVATAR_FLY_SPEED(DandelionAction.DECREASE_AVATAR_FLY_SPEED),
		INCREASE_AZYMUTH(DandelionAction.INCREASE_AZYMUTH),
		DECREASE_AZYMUTH(DandelionAction.DECREASE_AZYMUTH),
		INCREASE_INCLINATION(DandelionAction.INCREASE_INCLINATION),
		DECREASE_INCLINATION(DandelionAction.DECREASE_INCLINATION),
		INCREASE_TRACKING_DISTANCE(DandelionAction.INCREASE_TRACKING_DISTANCE),
		DECREASE_TRACKING_DISTANCE(DandelionAction.DECREASE_TRACKING_DISTANCE),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		KeyboardAction(DandelionAction a) {
			act = a;
		}
	}

	public enum WheelAction implements Action<DandelionAction> {
		// DOF_1
		SCALE(DandelionAction.SCALE),
		ZOOM(DandelionAction.ZOOM),
		ROLL(DandelionAction.ROLL),
		DRIVE(DandelionAction.DRIVE),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		WheelAction(DandelionAction a) {
			act = a;
		}
	}

	public enum DOF2Action implements Action<DandelionAction> {
		// DOF_1
		SCALE(DandelionAction.SCALE),
		ZOOM(DandelionAction.ZOOM),
		ROLL(DandelionAction.ROLL),
		DRIVE(DandelionAction.DRIVE),

		// DOF_2
		ROTATE(DandelionAction.ROTATE),
		CAD_ROTATE(DandelionAction.CAD_ROTATE),
		TRANSLATE(DandelionAction.TRANSLATE),
		MOVE_FORWARD(DandelionAction.MOVE_FORWARD),
		MOVE_BACKWARD(DandelionAction.MOVE_BACKWARD),
		LOOK_AROUND(DandelionAction.LOOK_AROUND),
		SCREEN_ROTATE(DandelionAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(DandelionAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(DandelionAction.ZOOM_ON_REGION),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF2Action(DandelionAction a) {
			act = a;
		}
	}

	public enum DOF3Action implements Action<DandelionAction> {
		// DOF_1
		SCALE(DandelionAction.SCALE),
		ZOOM(DandelionAction.ZOOM),
		ROLL(DandelionAction.ROLL),
		DRIVE(DandelionAction.DRIVE),

		// DOF_2
		ROTATE(DandelionAction.ROTATE),
		CAD_ROTATE(DandelionAction.CAD_ROTATE),
		TRANSLATE(DandelionAction.TRANSLATE),
		MOVE_FORWARD(DandelionAction.MOVE_FORWARD),
		MOVE_BACKWARD(DandelionAction.MOVE_BACKWARD),
		LOOK_AROUND(DandelionAction.LOOK_AROUND),
		SCREEN_ROTATE(DandelionAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(DandelionAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(DandelionAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE3(DandelionAction.TRANSLATE3),
		ROTATE3(DandelionAction.ROTATE3),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF3Action(DandelionAction a) {
			act = a;
		}
	}

	public enum DOF6Action implements Action<DandelionAction> {
		// DOF_1
		SCALE(DandelionAction.SCALE),
		ZOOM(DandelionAction.ZOOM),
		ROLL(DandelionAction.ROLL),
		DRIVE(DandelionAction.DRIVE),

		// DOF_2
		ROTATE(DandelionAction.ROTATE),
		CAD_ROTATE(DandelionAction.CAD_ROTATE),
		TRANSLATE(DandelionAction.TRANSLATE),
		MOVE_FORWARD(DandelionAction.MOVE_FORWARD),
		MOVE_BACKWARD(DandelionAction.MOVE_BACKWARD),
		LOOK_AROUND(DandelionAction.LOOK_AROUND),
		SCREEN_ROTATE(DandelionAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(DandelionAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(DandelionAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE3(DandelionAction.TRANSLATE3),
		ROTATE3(DandelionAction.ROTATE3),

		// DOF_6
		TRANSLATE_ROTATE(DandelionAction.TRANSLATE_ROTATE),

		CUSTOM(DandelionAction.CUSTOM);

		@Override
		public DandelionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF6Action(DandelionAction a) {
			act = a;
		}
	}
}
