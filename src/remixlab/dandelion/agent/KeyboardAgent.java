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

import remixlab.bias.agent.*;
import remixlab.bias.event.KeyboardEvent;
import remixlab.bias.profile.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * An {@link remixlab.bias.agent.ActionKeyboardAgent} that handles Dandelion keyboard actions.
 * <p>
 * Dandelion actions can be handled by an {@link remixlab.dandelion.core.AbstractScene}, an
 * {@link remixlab.dandelion.core.InteractiveFrame} or by an {@link remixlab.dandelion.core.InteractiveEyeFrame}. This
 * class implements a generic Keyboard Agent that represents a keyboard device that handles actions to be executed only
 * by an AbstractScene (InteractiveFrame and InteractiveEyeFrame actions are handled exclusively by an
 * {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent}).
 * <p>
 * The agent uses its {@link #keyboardProfile()} to parse the {@link remixlab.bias.core.BogusEvent} to obtain a
 * dandelion action, which is then sent to the proper AbstractScene ({@link #inputGrabber()}) for its final execution.
 * In case the grabber is not an instance of an AbstractScenee, but a different object which behavior you implemented (
 * {@link #alienGrabber()}), the agent sends the raw BogusEvent to it.
 * <p>
 * Simply retrieve the {@link #keyboardProfile()} to bind an action to a shortcut, to remove it, or to check your
 * current bindings. Default bindings are provided for convenience ({@link #setDefaultShortcuts()}).
 * <p>
 * Note that {@link #keyboardProfile()} shortcuts are {@link remixlab.bias.event.shortcut.KeyboardShortcut}s.
 */
public class KeyboardAgent extends ActionKeyboardAgent<KeyboardProfile<KeyboardAction>> implements
		Constants {
	AbstractScene	scene;

	/**
	 * Default constructor. Calls {@link #setDefaultShortcuts()}.
	 */
	public KeyboardAgent(AbstractScene scn, String n) {
		super(new KeyboardProfile<KeyboardAction>(), scn.inputHandler(), n);
		setDefaultGrabber(scn);
		scene = scn;

		// D e f a u l t s h o r t c u t s
		setDefaultShortcuts();
	}

	/**
	 * Set the default keyboard shortcuts as follows:
	 * <p>
	 * {@code 'a' -> KeyboardAction.TOGGLE_AXIS_VISUAL_HINT}<br>
	 * {@code 'f' -> KeyboardAction.TOGGLE_FRAME_VISUAL_HINT}<br>
	 * {@code 'g' -> KeyboardAction.TOGGLE_GRID_VISUAL_HINT}<br>
	 * {@code 'm' -> KeyboardAction.TOGGLE_ANIMATION}<br>
	 * {@code 'e' -> KeyboardAction.TOGGLE_CAMERA_TYPE}<br>
	 * {@code 'h' -> KeyboardAction.DISPLAY_INFO}<br>
	 * {@code 'r' -> KeyboardAction.TOGGLE_PATHS_VISUAL_HINT}<br>
	 * {@code 's' -> KeyboardAction.INTERPOLATE_TO_FIT}<br>
	 * {@code 'S' -> KeyboardAction.SHOW_ALL}<br>
	 * {@code left_arrow -> KeyboardAction.MOVE_EYE_RIGHT}<br>
	 * {@code right_arrow -> KeyboardAction.MOVE_EYE_LEFT}<br>
	 * {@code up_arrow -> KeyboardAction.MOVE_EYE_UP}<br>
	 * {@code down_arrow -> KeyboardAction.MOVE_EYE_DOWN}<br>
	 */
	public void setDefaultShortcuts() {
		keyboardProfile().removeAllBindings();
		keyboardProfile().setShortcut('a', KeyboardAction.TOGGLE_AXIS_VISUAL_HINT);
		keyboardProfile().setShortcut('f', KeyboardAction.TOGGLE_FRAME_VISUAL_HINT);
		keyboardProfile().setShortcut('g', KeyboardAction.TOGGLE_GRID_VISUAL_HINT);
		keyboardProfile().setShortcut('m', KeyboardAction.TOGGLE_ANIMATION);

		keyboardProfile().setShortcut('e', KeyboardAction.TOGGLE_CAMERA_TYPE);
		keyboardProfile().setShortcut('h', KeyboardAction.DISPLAY_INFO);
		keyboardProfile().setShortcut('r', KeyboardAction.TOGGLE_PATHS_VISUAL_HINT);

		keyboardProfile().setShortcut('s', KeyboardAction.INTERPOLATE_TO_FIT);
		keyboardProfile().setShortcut('S', KeyboardAction.SHOW_ALL);

		keyboardProfile().setShortcut(B_NOMODIFIER_MASK, B_RIGHT, KeyboardAction.MOVE_EYE_RIGHT);
		keyboardProfile().setShortcut(B_NOMODIFIER_MASK, B_LEFT, KeyboardAction.MOVE_EYE_LEFT);
		keyboardProfile().setShortcut(B_NOMODIFIER_MASK, B_UP, KeyboardAction.MOVE_EYE_UP);
		keyboardProfile().setShortcut(B_NOMODIFIER_MASK, B_DOWN, KeyboardAction.MOVE_EYE_DOWN);
	}

	/**
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

	@Override
	public KeyboardEvent feed() {
		return null;
	}

	@Override
	public KeyboardProfile<KeyboardAction> keyboardProfile() {
		return profile;
	}
}
