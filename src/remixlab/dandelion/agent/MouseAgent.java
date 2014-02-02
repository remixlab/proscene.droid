/*******************************************************************************
 * dandelion (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.dandelion.agent;

import remixlab.dandelion.core.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;

public class MouseAgent extends GenericWheeledBiMotionAgent<GenericMotionProfile<Constants.DOF2Action>> {
	public MouseAgent(AbstractScene scn, String n) {
		super(new GenericMotionProfile<WheelAction>(),
				  new GenericMotionProfile<WheelAction>(),
				  new GenericMotionProfile<DOF2Action>(),
				  new GenericMotionProfile<DOF2Action>(),
				  new GenericClickProfile<ClickAction>(),
				  new GenericClickProfile<ClickAction>(), scn, n);
		
		setAsArcball();
		
		cameraClickProfile().setClickBinding(TH_NOMODIFIER_MASK, TH_LEFT, 2, ClickAction.ALIGN_FRAME);
		cameraClickProfile().setClickBinding(TH_NOMODIFIER_MASK, TH_RIGHT, 2, ClickAction.CENTER_FRAME);		
		
		frameClickProfile().setClickBinding(TH_NOMODIFIER_MASK, TH_LEFT, 2, ClickAction.ALIGN_FRAME);
		frameClickProfile().setClickBinding(TH_NOMODIFIER_MASK, TH_RIGHT, 2, ClickAction.CENTER_FRAME);
		
		cameraWheelProfile().setBinding(TH_NOMODIFIER_MASK, TH_NOBUTTON, scn.is3D() ? WheelAction.ZOOM : WheelAction.SCALE);		
		frameWheelProfile().setBinding(TH_NOMODIFIER_MASK, TH_NOBUTTON, WheelAction.SCALE);
	}
	
	public void setAsFirstPerson() {		
		cameraProfile().setBinding(TH_NOMODIFIER_MASK, TH_LEFT, DOF2Action.MOVE_FORWARD);
		cameraProfile().setBinding(TH_NOMODIFIER_MASK, TH_CENTER, DOF2Action.LOOK_AROUND);
		cameraProfile().setBinding(TH_NOMODIFIER_MASK, TH_RIGHT, DOF2Action.MOVE_BACKWARD);
		cameraProfile().setBinding(TH_SHIFT, TH_LEFT, DOF2Action.ROLL);
		cameraProfile().setBinding(TH_SHIFT, TH_CENTER, DOF2Action.DRIVE);
		cameraWheelProfile().setBinding(TH_CTRL, TH_NOBUTTON, WheelAction.ROLL);
		cameraWheelProfile().setBinding(TH_SHIFT, TH_NOBUTTON, WheelAction.DRIVE);
	}
	
	public void setAsThirdPerson() {
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_LEFT, DOF2Action.MOVE_FORWARD);
    frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_CENTER, DOF2Action.LOOK_AROUND);
    frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_RIGHT, DOF2Action.MOVE_BACKWARD);
    frameProfile().setBinding(TH_SHIFT, TH_LEFT, DOF2Action.ROLL);
		frameProfile().setBinding(TH_SHIFT, TH_CENTER, DOF2Action.DRIVE);
	}
	
	public void setAsArcball() {
		cameraProfile().setBinding(TH_NOMODIFIER_MASK, TH_LEFT, DOF2Action.ROTATE);
		cameraProfile().setBinding(TH_NOMODIFIER_MASK, TH_CENTER, DOF2Action.ZOOM);
		cameraProfile().setBinding(TH_NOMODIFIER_MASK, TH_RIGHT, DOF2Action.TRANSLATE);		
		cameraProfile().setBinding(TH_SHIFT, TH_LEFT, DOF2Action.ZOOM_ON_REGION);
		cameraProfile().setBinding(TH_SHIFT, TH_CENTER, DOF2Action.SCREEN_TRANSLATE);
		cameraProfile().setBinding(TH_SHIFT, TH_RIGHT, DOF2Action.SCREEN_ROTATE);
			
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_LEFT, DOF2Action.ROTATE);
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_CENTER, DOF2Action.SCALE);
		frameProfile().setBinding(TH_NOMODIFIER_MASK, TH_RIGHT, DOF2Action.TRANSLATE);
		frameProfile().setBinding(TH_SHIFT, TH_CENTER, DOF2Action.SCREEN_TRANSLATE);
		frameProfile().setBinding(TH_SHIFT, TH_RIGHT, DOF2Action.SCREEN_ROTATE);
	}
	
	@Override
	public GenericDOF2Event<Constants.DOF2Action> feed() {
		return null;
	}
	
	@Override
	public GenericMotionProfile<Constants.DOF2Action> cameraProfile() {
		return camProfile;
	}
	
	@Override
	public GenericMotionProfile<Constants.DOF2Action> frameProfile() {
		return profile;
	}
	
	public void setXTranslationSensitivity(float s) {
		sens[0] = s;
	}
	
	public void setYTranslationSensitivity(float s) {
		sens[1] = s;
	}
}
