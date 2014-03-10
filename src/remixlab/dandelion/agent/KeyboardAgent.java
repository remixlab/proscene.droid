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

import remixlab.bias.generic.agent.*;
import remixlab.bias.generic.event.*;
import remixlab.bias.generic.profile.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * A {@link remixlab.bias.generic.agent.ActionKeyboardAgent} that handles Dandelion keyboard actions.
 * <p>
 * Dandelion actions can be handled by an {@link remixlab.dandelion.core.AbstractScene}, an
 * {@link remixlab.dandelion.core.InteractiveFrame} or by an {@link remixlab.dandelion.core.InteractiveEyeFrame}. This
 * class implements a generic Keyboard Agent that represents a keyboard device that handles actions to be executed only
 * by AbstractScene (InteractiveFrame and InteractiveEyeFrame actions are handled exclusively by a
 * {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent}).
 * <p>
 * The agent uses its {@link #keyboardProfile()} to parse the {@link remixlab.bias.event.BogusEvent} to obtain a
 * dandelion action, which is then sent to the proper AbstractScene ({@link #grabber()}) for its final execution. In
 * case the grabber is not an instance of an AbstractScenee, but a different object which behavior you implemented (
 * {@link #foreignGrabber()}), the agent sends the raw BogusEvent to it (please refer to the mouse grabber example).
 * <p>
 * Simply retrieve the {@link #keyboardProfile()} to bind an action to a shortcut, to remove it, or to check your
 * current bindings. Default bindings are provided for convenience.
 * <p>
 * Note that {@link #keyboardProfile()} shortcuts are {@link remixlab.bias.event.shortcut.KeyboardShortcut}s.
 */
public class KeyboardAgent extends ActionKeyboardAgent<KeyboardProfile<KeyboardAction>> implements
		Constants {
	AbstractScene	scene;

	public KeyboardAgent(AbstractScene scn, String n) {
		super(new KeyboardProfile<KeyboardAction>(), scn.inputHandler(), n);
		setDefaultGrabber(scn);
		scene = scn;

		// D e f a u l t s h o r t c u t s
		setDefaultShortcuts();
	}

	public void setDefaultShortcuts() {
		keyboardProfile().removeAllBindings();
		keyboardProfile().setShortcut('a', KeyboardAction.DRAW_AXIS);
		keyboardProfile().setShortcut('f', KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		keyboardProfile().setShortcut('g', KeyboardAction.DRAW_GRID);
		keyboardProfile().setShortcut('m', KeyboardAction.ANIMATION);

		keyboardProfile().setShortcut('e', KeyboardAction.CAMERA_TYPE);
		keyboardProfile().setShortcut('h', KeyboardAction.GLOBAL_HELP);
		keyboardProfile().setShortcut('r', KeyboardAction.EDIT_EYE_PATH);

		keyboardProfile().setShortcut('s', KeyboardAction.INTERPOLATE_TO_FIT);
		keyboardProfile().setShortcut('S', KeyboardAction.SHOW_ALL);

		keyboardProfile().setShortcut(B_NOMODIFIER_MASK, B_RIGHT, KeyboardAction.MOVE_EYE_RIGHT);
		keyboardProfile().setShortcut(B_NOMODIFIER_MASK, B_LEFT, KeyboardAction.MOVE_EYE_LEFT);
		keyboardProfile().setShortcut(B_NOMODIFIER_MASK, B_UP, KeyboardAction.MOVE_EYE_UP);
		keyboardProfile().setShortcut(B_NOMODIFIER_MASK, B_DOWN, KeyboardAction.MOVE_EYE_DOWN);
	}

	/*
	 * Sets the default (virtual) key to play eye paths.
	 */
	public void setKeyCodeToPlayPath(int vkey, int path) {
		switch (path) {
		case 1:
			keyboardProfile().setShortcut(B_NOMODIFIER_MASK, vkey, KeyboardAction.PLAY_PATH_1);
			break;
		case 2:
			keyboardProfile().setShortcut(B_NOMODIFIER_MASK, vkey, KeyboardAction.PLAY_PATH_2);
			break;
		case 3:
			keyboardProfile().setShortcut(B_NOMODIFIER_MASK, vkey, KeyboardAction.PLAY_PATH_3);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bias.core.Agent#feed()
	 */
	@Override
	public ActionKeyboardEvent<KeyboardAction> feed() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bias.generic.agent.ActionKeyboardAgent#keyboardProfile()
	 */
	@Override
	public KeyboardProfile<KeyboardAction> keyboardProfile() {
		return profile;
	}
}
