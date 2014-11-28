/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/, Victor Forero
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.agent;

import remixlab.bias.agent.profile.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;

/**
 * An {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent} representing a Wheeled mouse and thus only holds 2
 * Degrees-Of-Freedom (e.g., two translations or two rotations), such as most mice.
 */
public class MultiTouchAgent extends HIDAgent {
	DOF6Event					event, pressEvent;
	TouchParser		touch;

	/**
	 * Multi-Touch Gesture
	 */
	public enum Gestures {
		TAP("", 1),
		DRAG_ONE("", 2),
		DRAG_TWO("", 3),
		DRAG_THREE("", 4),
		TURN_TWO("", 5),
		TURN_THREE("", 6),
		PINCH_TWO("", 7),
		PINCH_THREE("", 8);

		String	description;
		int			id;

		Gestures(String description, int id) {
			this.description = description;
			this.id = id;
		}

		/**
		 * Returns a description of the gesture.
		 */
		public String description() {
			return description;
		}

		/**
		 * Returns gesture id.
		 */
		public int id() {
			return id;
		}
	}

	public MultiTouchAgent(AbstractScene scn, String n) {
		super(scn, n);
		touch = new TouchParser();
	}

	// HIGH-LEVEL

	/**
	 * Binds the gesture shortcut to the (DOF6) dandelion action to be performed by the given {@code target} (EYE or
	 * FRAME).
	 */
	public void setGestureBinding(Target target, Gestures gesture, DOF6Action action) {
		MotionProfile<DOF6Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(gesture.id, action);
	}

	/**
	 * Removes the gesture shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeGestureBinding(Target target, Gestures gesture) {
		MotionProfile<DOF6Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBinding(gesture.id);
	}

	/**
	 * Returns {@code true} if the gesture shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasGestureBinding(Target target, Gestures gesture) {
		MotionProfile<DOF6Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.hasBinding(gesture.id);
	}

	/**
	 * Returns {@code true} if the gesture action is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isGestureActionBound(Target target, DOF6Action action) {
		MotionProfile<DOF6Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.isActionBound(action);
	}

	/**
	 * Returns the (DOF6) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound
	 * Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF6Action gestureAction(Target target, Gestures gesture) {
		MotionProfile<DOF6Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return (DOF6Action) profile.action(gesture.id);
	}

	/**
	 * Binds the tap gesture shortcut to the (click) dandelion action to be performed by the given {@code target} (EYE or
	 * FRAME).
	 */
	public void setTapBinding(Target target, Gestures gesture, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? clickProfile() : frameClickProfile();
		profile.setBinding(gesture.id, 1, action);
	}

	/**
	 * Removes the gesture click-shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeTapBinding(Target target, Gestures gesture) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? clickProfile() : frameClickProfile();
		profile.removeBinding(gesture.id);
	}

	/**
	 * Returns {@code true} if the tap gesture shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasTapBinding(Target target, Gestures gesture) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? clickProfile() : frameClickProfile();
		return profile.hasBinding(gesture.id);
	}

	/**
	 * Returns {@code true} if the tap action is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isTapActionBound(Target target, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? clickProfile() : frameClickProfile();
		return profile.isActionBound(action);
	}

	/**
	 * Returns the (click) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to
	 * the given tap gesture shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public ClickAction tapAction(Target target, Gestures gesture) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? clickProfile() : frameClickProfile();
		return (ClickAction) profile.action(gesture.id);
	}
	
	// touch parsing
	//TODO complete wrappers
	
	protected float getCx() {
		return touch.getCx();
	}

	protected float getCy() {
		return touch.getCy();
	}
	
	protected float getZ() {
		return touch.getZ();
	}

	protected float getR() {
		return touch.getR();
	}
	
	protected void pointUp(int id) {
		touch.pointUp(id);
	}
	
	protected void pointMoved(float x, float y, int id) {
		touch.pointMoved(x, y, id);
	}
	
	protected void pointDown(float x, float y, int id) {
		touch.pointDown(x, y, id);
	}
	
	protected void parse() {
		touch.parse();
	}

	protected Gestures parseTap() {
		return touch.parseTap();
	}

	protected Gestures parseGesture() { 
		return touch.parseGesture();
	}
}