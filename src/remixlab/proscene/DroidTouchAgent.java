/**************************************************************************************
 * ProScene (version 2.1.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos http://otrolado.info/, Victor Manuel Forero
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import remixlab.bias.core.Action;
import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.*;
import remixlab.dandelion.agent.WheeledMultiTouchAgent;
import remixlab.dandelion.core.InteractiveEyeFrame;
import remixlab.dandelion.core.InteractiveFrame;
import remixlab.gesture.TouchProcessor;

/**
 * Proscene {@link remixlab.dandelion.agent.WheeledMultiTouchAgent}.
 */
public class DroidTouchAgent extends WheeledMultiTouchAgent{
	Scene				scene;
	DOF6Event			event, prevEvent;
	InteractiveFrame	iFrame;
	boolean 			firstPerson = false;
	TouchProcessor 		touch;

	public DroidTouchAgent(Scene scn, String n) {
		super(scn, n);
		touch = new TouchProcessor();
		//inputHandler().unregisterAgent(this);
		scene = scn;
		eyeProfile().setBinding(DOF6Event.NOMODIFIER_MASK, Gestures.DRAG_ONE.id() ,DOF6Action.ROTATE);
		frameProfile().setBinding(DOF6Event.NOMODIFIER_MASK, Gestures.DRAG_ONE.id() ,DOF6Action.ROTATE);
		eyeProfile().setBinding(DOF6Event.NOMODIFIER_MASK, Gestures.DRAG_TWO.id() ,DOF6Action.TRANSLATE);
		frameProfile().setBinding(DOF6Event.NOMODIFIER_MASK, Gestures.DRAG_TWO.id() ,DOF6Action.TRANSLATE);
		eyeProfile().setBinding(DOF6Event.NOMODIFIER_MASK, Gestures.PINCH_TWO.id(), scene.is3D() ? DOF6Action.ZOOM : DOF6Action.SCALE);
		frameProfile().setBinding(DOF6Event.NOMODIFIER_MASK, Gestures.PINCH_TWO.id(), scene.is3D() ? DOF6Action.ZOOM : DOF6Action.SCALE);
		eyeProfile().setBinding(DOF6Event.NOMODIFIER_MASK, Gestures.TURN_TWO.id() ,DOF6Action.ROTATE);
		frameProfile().setBinding(DOF6Event.NOMODIFIER_MASK, Gestures.TURN_TWO.id() ,DOF6Action.ROTATE);
	}

	public void touchEvent(android.view.MotionEvent e) {
		int action = e.getAction();
		int code   = action & android.view.MotionEvent.ACTION_MASK;
		int index  = action >>android.view.MotionEvent.ACTION_POINTER_ID_SHIFT;
		
		float x = e.getX(index);
		float y = e.getY(index);
		int id  = e.getPointerId(index);
		Gestures gesture;
		
		// pass the events to the TouchProcessor
		if ( code == android.view.MotionEvent.ACTION_DOWN || code == android.view.MotionEvent.ACTION_POINTER_DOWN) {
			//touch(new DOF6Event(x, y, 0, 0, 0, 0));
			touch.pointDown(x, y, id);
			touch.analyse();
			event = new DOF6Event(null, 
					touch.getCx(), 
					touch.getCy(),
					0,
					0,
					0,
					0,
					DOF6Event.NOMODIFIER_MASK,
					e.getPointerCount());
			if( e.getPointerCount() == 1) updateTrackedGrabber(event);
			prevEvent = event.get();
		}
		else if (code == android.view.MotionEvent.ACTION_UP || code == android.view.MotionEvent.ACTION_POINTER_UP) {
			touch.pointUp(id);
			if( e.getPointerCount() == 1){
				gesture = touch.analyseTap();
				if (gesture == Gestures.TAP){
					handle(new ClickEvent(e.getX() - scene.originCorner().x(), e.getY() - scene.originCorner().y(), gesture.id()));
				}
				this.disableTracking();
				this.enableTracking();
			}
		
		}
		else if ( code == android.view.MotionEvent.ACTION_MOVE) {
			int numPointers = e.getPointerCount();
			for (int i=0; i < numPointers; i++) {
				id = e.getPointerId(i);
				x = e.getX(i);
				y = e.getY(i);
				touch.pointMoved(x, y, id);
			}			
			gesture = touch.analyseGesture();
			if (gesture != null){

				event = new DOF6Event(prevEvent, touch.getCx(), touch.getCy(), 0, 0, 0, 0, DOF6Event.NOMODIFIER_MASK, gesture.id());

				Action<?> a = (inputGrabber() instanceof InteractiveEyeFrame) ? eyeProfile().handle((BogusEvent) event)
						: frameProfile().handle((BogusEvent) event);
				if (a == null)
					return;
				DandelionAction dA = (DandelionAction) a.referenceAction();
				if (dA == DandelionAction.TRANSLATE_XYZ){

				}else if (dA == DandelionAction.TRANSLATE_XYZ_ROTATE_XYZ){

				}else{
					if(prevEvent.button() != gesture.id()){
						prevEvent = null;
					}
					switch (gesture) {
					case DRAG_ONE: case DRAG_TWO: case DRAG_THREE:			//Drag  
						event = new DOF6Event(prevEvent, 
								touch.getCx(), 
								touch.getCy(),
								0,
								0,
								0,
								0,
								DOF6Event.NOMODIFIER_MASK,
								gesture.id()
								);
						break;
					case PINCH_TWO: case PINCH_THREE:					//Pinch
						event = new DOF6Event(prevEvent, 
								0, 
								touch.getZ(),
								0,
								0,
								0,
								0,
								DOF6Event.NOMODIFIER_MASK,
								gesture.id()
								);	
						break;
					case TURN_TWO: case TURN_THREE:					//Rotate
						event = new DOF6Event(prevEvent, 
								0, 
								touch.getR(),
								0,
								0,
								0,
								0,
								DOF6Event.NOMODIFIER_MASK,
								gesture.id()
								);	
						break;
					default:
						break;

					}
				}
				if (gesture != null){
					if (prevEvent != null) handle(event);
					prevEvent = event.get();
				}
			}
		}
	}

	public boolean isAsFirstPerson() {
		return firstPerson;
	}

	public void setAsFirstPerson(boolean cameraFirstPerson) {
		firstPerson = cameraFirstPerson;
	}
}