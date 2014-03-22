/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

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

	/**
	 * Dandelion global action enum. All enum sub-groups point-out to this one.
	 */
	public enum DandelionAction {
		// KEYfRAMES
		/**
		 * Add keyframe to path 1
		 */
		ADD_KEYFRAME_TO_PATH_1("Add keyframe to path 1", true, 0),
		/**
		 * Play path 1
		 */
		PLAY_PATH_1("Play path 1", true, 0),
		/**
		 * Delete path 1
		 */
		DELETE_PATH_1("Delete path 1", true, 0),
		/**
		 * Add keyframe to path 2
		 */
		ADD_KEYFRAME_TO_PATH_2("Add keyframe to path 2", true, 0),
		/**
		 * Play path 2
		 */
		PLAY_PATH_2("Play path 2", true, 0),
		/**
		 * Delete path 2
		 */
		DELETE_PATH_2("Delete path 2", true, 0),
		/**
		 * Add keyframe to path 3
		 */
		ADD_KEYFRAME_TO_PATH_3("Add keyframe to path 3", true, 0),
		/**
		 * Play path 3
		 */
		PLAY_PATH_3("Play path 3", true, 0),
		/**
		 * Delete path 3
		 */
		DELETE_PATH_3("Delete path 3", true, 0),

		// CLICk ACTIONs
		/**
		 * Center frame
		 */
		CENTER_FRAME("Center frame", true, 0),
		/**
		 * Align frame with world
		 */
		ALIGN_FRAME("Align frame with world", true, 0),

		// Click actions require cursor pos:
		/**
		 * Interpolate the eye to zoom on pixel
		 */
		ZOOM_ON_PIXEL("Interpolate the eye to zoom on pixel", true, 0),
		/**
		 * Set the anchor from the pixel under the pointer
		 */
		ANCHOR_FROM_PIXEL("Set the anchor from the pixel under the pointer", true, 0),

		// GENERAL KEYBOARD ACTIONs
		/**
		 * Toggles axis visual hint
		 */
		TOGGLE_AXIS_VISUAL_HINT("Toggles axis visual hint", true, 0),
		/**
		 * Toggles grid visual hint
		 */
		TOGGLE_GRID_VISUAL_HINT("Toggles grid visual hint", true, 0),
		/**
		 * Toggles paths visual hint
		 */
		TOGGLE_PATHS_VISUAL_HINT("Toggles paths visual hint", true, 0),
		/**
		 * Toggles frame visual hint
		 */
		TOGGLE_FRAME_VISUAL_HINT("Toggles frame visual hint", true, 0),
		/**
		 * Toggles animation
		 */
		TOGGLE_ANIMATION("Toggles animation", true, 0),
		/**
		 * Toggles camera type
		 */
		TOGGLE_CAMERA_TYPE("Toggles camera type", false, 0),
		/**
		 * Displays the global help
		 */
		DISPLAY_INFO("Displays the global help", true, 0),
		/**
		 * Zoom to fit the scene
		 */
		INTERPOLATE_TO_FIT("Zoom to fit the scene", true, 0),
		/**
		 * Reset the anchor to the world origin
		 */
		RESET_ANCHOR("Reset the anchor to the world origin", true, 0),
		/**
		 * Show the whole scene
		 */
		SHOW_ALL("Show the whole scene", true, 0),

		// CAMERA KEYBOARD ACTIONs // TODO all of these could be dof_1
		/**
		 * Move eye to the left
		 */
		MOVE_EYE_LEFT("Move eye to the left", true, 0),
		/**
		 * Move eye to the right
		 */
		MOVE_EYE_RIGHT("Move eye to the right", true, 0),
		/**
		 * Move eye up
		 */
		MOVE_EYE_UP("Move eye up", true, 0),
		/**
		 * Move eye down
		 */
		MOVE_EYE_DOWN("Move eye down", true, 0),
		/**
		 * Increase frame rotation sensitivity
		 */
		INCREASE_ROTATION_SENSITIVITY("Increase frame rotation sensitivity", true, 0),
		/**
		 * Decrease frame rotation sensitivity
		 */
		DECREASE_ROTATION_SENSITIVITY("Decrease frame rotation sensitivity", true, 0),
		/**
		 * Increase camera fly speed (only meaningful in first-person mode)
		 */
		INCREASE_CAMERA_FLY_SPEED("Increase camera fly speed (only meaningful in first-person mode)", false, 0),
		/**
		 * Decrease camera fly speed (only meaningful in first-person mode)
		 */
		DECREASE_CAMERA_FLY_SPEED("Decrease camera fly speed (only meaningful in first-person mode)", false, 0),
		/**
		 * Increase avatar fly speed (only meaningful in third-person mode)
		 */
		INCREASE_AVATAR_FLY_SPEED("Increase avatar fly speed (only meaningful in third-person mode)", false, 0),
		/**
		 * Decrease avatar fly speed (only meaningful in third-person mode)
		 */
		DECREASE_AVATAR_FLY_SPEED("Decrease avatar fly speed (only meaningful in third-person mode)", false, 0),
		/**
		 * Increase camera azymuth respect to the avatar (only meaningful in third-person mode)
		 */
		INCREASE_AZYMUTH("Increase camera azymuth respect to the avatar (only meaningful in third-person mode)", false, 0),
		/**
		 * Decrease camera azymuth respect to the avatar (only meaningful in third-person mode)
		 */
		DECREASE_AZYMUTH("Decrease camera azymuth respect to the avatar (only meaningful in third-person mode)", false, 0),
		/**
		 * Increase camera inclination respect to the avatar (only meaningful in third-person mode)
		 */
		INCREASE_INCLINATION("Increase camera inclination respect to the avatar (only meaningful in third-person mode)", false, 0),
		/**
		 * Decrease camera inclination respect to the avatar (only meaningful in third-person mode)
		 */
		DECREASE_INCLINATION("Decrease camera inclination respect to the avatar (only meaningful in third-person mode)", false, 0),
		/**
		 * Increase camera tracking distance respect to the avatar (only meaningful in third-person mode
		 */
		INCREASE_TRACKING_DISTANCE("Increase camera tracking distance respect to the avatar (only meaningful in third-person mode)", false, 0),
		/**
		 * Decrease camera tracking distance respect to the avatar (only meaningful in third-person mode)
		 */
		DECREASE_TRACKING_DISTANCE("Decrease camera tracking distance respect to the avatar (only meaningful in third-person mode)", false, 0),

		// Wheel
		/**
		 * Scale frame
		 */
		SCALE("Scale frame", true, 1),
		/**
		 * Zoom eye
		 */
		ZOOM("Zoom eye", false, 1),
		/**
		 * Roll frame (camera or interactive frame)
		 */
		ROLL("Roll frame (eye or interactive frame)", true, 1),
		/**
		 * Drive (camera or interactive frame)
		 */
		DRIVE("Drive (camera or interactive frame)", false, 1),

		// DEVICE ACTIONs
		/**
		 * Rotate frame (eye or interactive frame)
		 */
		ROTATE("Rotate frame (eye or interactive frame)", true, 2),
		/**
		 * Rotate camera frame as in CAD applications
		 */
		CAD_ROTATE("Rotate camera frame as in CAD applications", false, 2),
		/**
		 * Translate frame (eye or interactive frame)
		 */
		TRANSLATE("Translate frame (eye or interactive frame)", true, 2),
		/**
		 * Move forward frame (camera or interactive frame)
		 */
		MOVE_FORWARD("Move forward frame (camera or interactive frame)", false, 2),
		/**
		 * Move backward frame (camera or interactive frame)
		 */
		MOVE_BACKWARD("Move backward frame (camera or interactive frame)", false, 2),
		/**
		 * Look around with frame (camera or interactive frame)
		 */
		LOOK_AROUND("Look around with frame (camera or interactive frame)", false, 2),
		/**
		 * Screen rotate (eye or interactive frame)
		 */
		SCREEN_ROTATE("Screen rotate (eye or interactive frame)", true, 2),
		/**
		 * Screen translate frame (eye or interactive frame)
		 */
		SCREEN_TRANSLATE("Screen translate frame (eye or interactive frame)", true, 2),
		/**
		 * Zoom on region (eye or interactive frame)
		 */
		ZOOM_ON_REGION("Zoom on region (eye or interactive frame)", true, 2),
		/**
		 * Translate frame (camera or interactive frame) from dx, dy, dz simultaneously
		 */
		TRANSLATE3("Translate frame (camera or interactive frame) from dx, dy, dz simultaneously", false, 3),
		/**
		 * Rotate frame (camera or interactive frame) from Euler angles
		 */
		ROTATE3("Rotate frame (camera or interactive frame) from Euler angles", false, 3),

		// GOOGLE_EARTH("Google earth emulation", false, 6),
		/**
		 * Natural (camera or interactive frame)
		 */
		TRANSLATE_ROTATE("Natural (camera or interactive frame)", false, 6),

		// CUSTOM ACTIONs
		/**
		 * User defined action
		 */
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

		/**
		 * Returns a description of the action item.
		 */
		public String description() {
			return description;
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return twoD;
		}

		/**
		 * Returns the degrees-of-freedom needed to perform the action item.
		 */
		public int dofs() {
			return dofs;
		}
	}

	/**
	 * Click action sub-group.
	 */
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
		TOGGLE_AXIS_VISUAL_HINT(DandelionAction.TOGGLE_AXIS_VISUAL_HINT),
		TOGGLE_GRID_VISUAL_HINT(DandelionAction.TOGGLE_GRID_VISUAL_HINT),
		// CAMERA_PROFILE(DandelionAction.CAMERA_PROFILE),
		TOGGLE_CAMERA_TYPE(DandelionAction.TOGGLE_CAMERA_TYPE),
		TOGGLE_ANIMATION(DandelionAction.TOGGLE_ANIMATION),
		RESET_ANCHOR(DandelionAction.RESET_ANCHOR),
		DISPLAY_INFO(DandelionAction.DISPLAY_INFO),
		// CURRENT_CAMERA_PROFILE_HELP(DandelionAction.CURRENT_CAMERA_PROFILE_HELP),
		TOGGLE_PATHS_VISUAL_HINT(DandelionAction.TOGGLE_PATHS_VISUAL_HINT),
		// FOCUS_INTERACTIVE_FRAME(DandelionAction.FOCUS_INTERACTIVE_FRAME),
		TOGGLE_FRAME_VISUAL_HINT(DandelionAction.TOGGLE_FRAME_VISUAL_HINT),
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

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		ClickAction(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * Keyboard action sub-group.
	 */
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
		TOGGLE_AXIS_VISUAL_HINT(DandelionAction.TOGGLE_AXIS_VISUAL_HINT),
		TOGGLE_GRID_VISUAL_HINT(DandelionAction.TOGGLE_GRID_VISUAL_HINT),
		// CAMERA_PROFILE(DandelionAction.CAMERA_PROFILE),
		TOGGLE_CAMERA_TYPE(DandelionAction.TOGGLE_CAMERA_TYPE),
		TOGGLE_ANIMATION(DandelionAction.TOGGLE_ANIMATION),
		RESET_ANCHOR(DandelionAction.RESET_ANCHOR),
		DISPLAY_INFO(DandelionAction.DISPLAY_INFO),
		// CURRENT_CAMERA_PROFILE_HELP(DandelionAction.CURRENT_CAMERA_PROFILE_HELP),
		TOGGLE_PATHS_VISUAL_HINT(DandelionAction.TOGGLE_PATHS_VISUAL_HINT),
		// FOCUS_INTERACTIVE_FRAME(DandelionAction.FOCUS_INTERACTIVE_FRAME),
		TOGGLE_FRAME_VISUAL_HINT(DandelionAction.TOGGLE_FRAME_VISUAL_HINT),
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

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		KeyboardAction(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * Wheel action sub-group.
	 */
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

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		WheelAction(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * DOF2 action sub-group.
	 */
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

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF2Action(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * DOF3 action sub-group.
	 */
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

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF3Action(DandelionAction a) {
			act = a;
		}
	}

	/**
	 * DOF6 action sub-group.
	 */
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

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		DandelionAction	act;

		DOF6Action(DandelionAction a) {
			act = a;
		}
	}
}
