/**************************************************************************************
 * ProScene (version 2.1.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.KeyboardEvent;
import remixlab.dandelion.agent.KeyboardAgent;

/**
 * Proscene {@link remixlab.dandelion.agent.KeyboardAgent}.
 */
public class KeyAgent extends KeyboardAgent {
	public KeyAgent(Scene scn, String n) {
		super(scn, n);
		// registration requires a call to PApplet.registerMethod("keyEvent", keyboardAgent());
		// which is done in Scene.enableKeyboardAgent(), which also register the agent at the inputHandler
		inputHandler().unregisterAgent(this);
	}

	/**
	 * Calls {@link remixlab.dandelion.agent.KeyboardAgent#setDefaultShortcuts()} and then adds the following:
	 * <p>
	 * {@code left_arrow -> KeyboardAction.MOVE_LEFT}<br>
	 * {@code right_arrow -> KeyboardAction.MOVE_RIGHT}<br>
	 * {@code up_arrow -> KeyboardAction.MOVE_UP}<br>
	 * {@code down_arrow -> KeyboardAction.MOVE_DOWN	}<br>
	 * {@code CTRL + java.awt.event.KeyEvent.VK_1 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_1}<br>
	 * {@code ALT + java.awt.event.KeyEvent.VK_1 -> KeyboardAction.DELETE_PATH_1}<br>
	 * {@code CTRL + java.awt.event.KeyEvent.VK_2 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_2}<br>
	 * {@code ALT + java.awt.event.KeyEvent.VK_2 -> KeyboardAction.DELETE_PATH_2}<br>
	 * {@code CTRL + java.awt.event.KeyEvent.VK_3 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_3}<br>
	 * {@code ALT + java.awt.event.KeyEvent.VK_3 -> KeyboardAction.DELETE_PATH_3}<br>
	 * <p>
	 * Finally, it calls: {@code setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_1, 1)},
	 * {@code setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_2, 2)} and
	 * {@code setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_3, 3)} to play the paths.
	 * 
	 * @see remixlab.dandelion.agent.KeyboardAgent#setDefaultShortcuts()
	 * @see remixlab.dandelion.agent.KeyboardAgent#setKeyCodeToPlayPath(int, int)
	 */
	@Override
	public void setDefaultShortcuts() {
		// VK values here: http://docs.oracle.com/javase/7/docs/api/constant-values.html
		super.setDefaultShortcuts();
		// VK_LEFT : 37
		keyboardProfile().setBinding(BogusEvent.NOMODIFIER_MASK, 37, KeyboardAction.MOVE_LEFT);
		// VK_UP : 38
		keyboardProfile().setBinding(BogusEvent.NOMODIFIER_MASK, 38, KeyboardAction.MOVE_UP);
		// VK_RIGHT : 39
		keyboardProfile().setBinding(BogusEvent.NOMODIFIER_MASK, 39, KeyboardAction.MOVE_RIGHT);
		// VK_DOWN : 40
		keyboardProfile().setBinding(BogusEvent.NOMODIFIER_MASK, 40, KeyboardAction.MOVE_DOWN);

		// VK_1 : 49
		keyboardProfile().setBinding(BogusEvent.CTRL, 49, KeyboardAction.ADD_KEYFRAME_TO_PATH_1);
		keyboardProfile().setBinding(BogusEvent.ALT, 49, KeyboardAction.DELETE_PATH_1);
		setKeyCodeToPlayPath(49, 1);
		// VK_2 : 50
		keyboardProfile().setBinding(BogusEvent.CTRL, 50, KeyboardAction.ADD_KEYFRAME_TO_PATH_2);
		keyboardProfile().setBinding(BogusEvent.ALT, 50, KeyboardAction.DELETE_PATH_2);
		setKeyCodeToPlayPath(50, 2);
		// VK_3 : 51
		keyboardProfile().setBinding(BogusEvent.CTRL, 51, KeyboardAction.ADD_KEYFRAME_TO_PATH_3);
		keyboardProfile().setBinding(BogusEvent.ALT, 51, KeyboardAction.DELETE_PATH_3);
		setKeyCodeToPlayPath(51, 3);
	}

	/**
	 * Processing keyEvent method to be registered at the PApplet's instance.
	 */
	public void keyEvent(processing.event.KeyEvent e) {
		if (e.getAction() == processing.event.KeyEvent.TYPE)
			handle(new KeyboardEvent(e.getKey()));
		else if (e.getAction() == processing.event.KeyEvent.RELEASE)
			handle(new KeyboardEvent(e.getModifiers(), e.getKeyCode()));
	}
}