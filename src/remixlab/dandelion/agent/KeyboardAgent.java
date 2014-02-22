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
import remixlab.tersehandling.generic.agent.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;

/**
 * A {@link remixlab.tersehandling.generic.agent.GenericKeyboardAgent} that handles Dandelion keyboard actions.
 * <p>
 * Dandelion actions can be handled by an {@link remixlab.dandelion.core.AbstractScene},
 * an {@link remixlab.dandelion.core.InteractiveFrame} or by an
 * {@link remixlab.dandelion.core.InteractiveEyeFrame}. This class implements a
 * Generic Keyboard Agent that represents a keyboard device that handles actions
 * to be executed only by AbstractScene (InteractiveFrame and InteractiveEyeFrame
 * actions are handled exclusively by a {@link remixlab.dandelion.agent.GenericWheeledBiMotionAgent}).
 * <p>
 * The agent uses its {@link #keyboardProfile()} to parse the
 * {@link remixlab.tersehandling.event.TerseEvent} to obtain a
 * dandelion action, which is then sent to the proper
 * AbstractScene ({@link #grabber()}) for its final execution. In case the
 * grabber is not an instance of an AbstractScenee, but a different object which behavior you
 * implemented ({@link #foreignGrabber()}), the agent sends the raw TerseEvent to it
 * (please refer to the mouse grabber example).
 * <p>
 * Simply retrieve the {@link #keyboardProfile()} to bind an action to a shortcut,
 * to remove it, or to check your current bindings. Default bindings are provided for convenience.
 * <p>
 * Note that {@link #keyboardProfile()} shortcuts are
 * {@link remixlab.tersehandling.event.shortcut.KeyboardShortcut}s.
 * 
 * @author pierre
 */
public class KeyboardAgent extends GenericKeyboardAgent<GenericKeyboardProfile<Constants.KeyboardAction>> implements Constants {
	AbstractScene scene;
	public KeyboardAgent(AbstractScene scn, String n) {
		super(new GenericKeyboardProfile<KeyboardAction>(), scn.terseHandler(), n);
		setDefaultGrabber(scn);
		scene = scn;

		// D e f a u l t s h o r t c u t s
		keyboardProfile().setShortcut('a', KeyboardAction.DRAW_AXIS);
		keyboardProfile().setShortcut('f', KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		keyboardProfile().setShortcut('g', KeyboardAction.DRAW_GRID);
		keyboardProfile().setShortcut('m', KeyboardAction.ANIMATION);
		
		keyboardProfile().setShortcut('e', KeyboardAction.CAMERA_TYPE);
		keyboardProfile().setShortcut('h', KeyboardAction.GLOBAL_HELP);
		keyboardProfile().setShortcut('r', KeyboardAction.EDIT_EYE_PATH);

		keyboardProfile().setShortcut('s', KeyboardAction.INTERPOLATE_TO_FIT);
		keyboardProfile().setShortcut('S', KeyboardAction.SHOW_ALL);

		keyboardProfile().setShortcut(TH_NOMODIFIER_MASK, TH_RIGHT, KeyboardAction.MOVE_EYE_RIGHT);
		keyboardProfile().setShortcut(TH_NOMODIFIER_MASK, TH_LEFT, KeyboardAction.MOVE_EYE_LEFT);
		keyboardProfile().setShortcut(TH_NOMODIFIER_MASK, TH_UP, KeyboardAction.MOVE_EYE_UP);
		keyboardProfile().setShortcut(TH_NOMODIFIER_MASK, TH_DOWN, KeyboardAction.MOVE_EYE_DOWN);

		keyboardProfile().setShortcut((TH_ALT | GenericKeyboardEvent.TH_SHIFT), 'l',	KeyboardAction.MOVE_EYE_LEFT);
		
		//only one not working but horrible: 
		//keyboardProfile().setShortcut('1', KeyboardAction.PLAY_PATH);
		
		//keyboardProfile().setShortcut(49, KeyboardAction.PLAY_PATH);
		//keyboardProfile().setShortcut(TH_CTRL, 49, KeyboardAction.ADD_KEYFRAME_TO_PATH);
		//keyboardProfile().setShortcut(TH_ALT, 49, KeyboardAction.DELETE_PATH);
		//keyboardProfile().setShortcut(TH_NOMODIFIER_MASK, '1', KeyboardAction.PLAY_PATH_1);
		
		keyboardProfile().setShortcut(TH_CTRL, '1', KeyboardAction.ADD_KEYFRAME_TO_PATH_1);
		keyboardProfile().setShortcut(TH_ALT, '1', KeyboardAction.DELETE_PATH_1);
		keyboardProfile().setShortcut(TH_CTRL, '2', KeyboardAction.ADD_KEYFRAME_TO_PATH_2);
		keyboardProfile().setShortcut(TH_ALT, '2', KeyboardAction.DELETE_PATH_2);
		keyboardProfile().setShortcut(TH_CTRL, '3', KeyboardAction.ADD_KEYFRAME_TO_PATH_3);
		keyboardProfile().setShortcut(TH_ALT, '3', KeyboardAction.DELETE_PATH_3);
		
		setKeyToPlayPath('1', 1);
		setKeyToPlayPath('2', 2);
		setKeyToPlayPath('3', 3);
		
	  //testing:
		//keyboardProfile().setShortcut('z', KeyboardAction.RESET_ARP);
	}
	
	/*
	 * Sets the default key to play eye paths.
	 */
	public void setKeyToPlayPath(char key, int path) {			
		switch (path) {
		case 1 :
			keyboardProfile().setShortcut(TH_NOMODIFIER_MASK, key, KeyboardAction.PLAY_PATH_1);
			break;
		case 2 :
			keyboardProfile().setShortcut(TH_NOMODIFIER_MASK, key, KeyboardAction.PLAY_PATH_2);
			break;
		case 3 :
			keyboardProfile().setShortcut(TH_NOMODIFIER_MASK, key, KeyboardAction.PLAY_PATH_3);
			break;
		default :
			break;
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see remixlab.tersehandling.core.Agent#feed()
	 */
	@Override
	public GenericKeyboardEvent<KeyboardAction> feed() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see remixlab.tersehandling.generic.agent.GenericKeyboardAgent#keyboardProfile()
	 */
	@Override
	public GenericKeyboardProfile<KeyboardAction> keyboardProfile() {
		return profile;
	}
}
