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

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import remixlab.bias.event.KeyboardEvent;
import remixlab.dandelion.agent.KeyboardAgent;

/**
 * Proscene {@link remixlab.dandelion.agent.KeyboardAgent}.
 */
public class DroidKeyAgent extends KeyboardAgent {
	Scene scene; 
	
	public DroidKeyAgent(Scene scn, String n) {
		super(scn, n);
		scene = scn;
		// registration requires a call to PApplet.registerMethod("keyEvent", keyboardAgent());
		// which is done in Scene.enableKeyboardAgent(), which also register the agent at the inputHandler
		inputHandler().unregisterAgent(this);
	}


	@Override
	public void setDefaultShortcuts() {
		// VK values here: http://docs.oracle.com/javase/7/docs/api/constant-values.html
		super.setDefaultShortcuts();
		
		keyboardProfile().setBinding('1', KeyboardAction.ADD_KEYFRAME_TO_PATH_1);
	    keyboardProfile().setBinding('2', KeyboardAction.DELETE_PATH_1);
	    keyboardProfile().setBinding('3', KeyboardAction.PLAY_PATH_1);
	}

	/**
	 * Processing keyEvent method to be registered at the PApplet's instance.
	 */
	public void keyEvent(processing.event.KeyEvent e) {
		 if (e.getAction() == processing.event.KeyEvent.PRESS){
			 if (e.getKeyCode() == android.view.KeyEvent.KEYCODE_MENU){
					Object context = scene.pApplet();
					InputMethodManager imm = (InputMethodManager) ((Context) context).getSystemService(Context.INPUT_METHOD_SERVICE);
				    imm.toggleSoftInput(0, 0);
			 }else
				 handle(new KeyboardEvent(e.getKey()));			
		 }
	}
}