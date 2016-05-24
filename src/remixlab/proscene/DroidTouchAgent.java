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

import processing.core.PApplet;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.util.TouchProcessor;
import remixlab.util.TouchProcessor.Gestures;

public class DroidTouchAgent extends Agent {
	Scene	scene;
	protected DOF6Event			event, prevEvent;
	protected static TouchProcessor	touchProcessor = new TouchProcessor();
	public static int TAP_ID, DRAG_ONE_ID, DRAG_TWO_ID,	DRAG_THREE_ID, TURN_TWO_ID,	TURN_THREE_ID, PINCH_TWO_ID, PINCH_THREE_ID, OPPOSABLE_THREE_ID;
	
	public DroidTouchAgent(Scene scn) {
		super(scn.inputHandler());
		scene = scn;
		DRAG_ONE_ID = scene().registerMotionID(2, this, 6);
		DRAG_TWO_ID = scene().registerMotionID(3, this, 6);
		DRAG_THREE_ID = scene().registerMotionID(4, this, 6);
		TURN_TWO_ID = scene().registerMotionID(5, this, 6);
		TURN_THREE_ID = scene().registerMotionID(6, this, 6);
		PINCH_TWO_ID = scene().registerMotionID(7, this, 6); 
		PINCH_THREE_ID = scene().registerMotionID(8, this, 6);
		OPPOSABLE_THREE_ID = scene().registerMotionID(9, this, 6);
		 
	}

	public void setDefaultBindings(InteractiveFrame frame) {
		frame.removeMotionBindings(this);
		frame.removeClickBindings(this);
		
		frame.setMotionBinding(DRAG_ONE_ID, "rotate");
	    frame.setMotionBinding(TURN_TWO_ID, frame.isEyeFrame() ? "zoomOnRegion" : "screenRotate");
	    frame.setMotionBinding(DRAG_TWO_ID, "translate");
	    frame.setMotionBinding(PINCH_TWO_ID, scene().is3D() ? frame.isEyeFrame() ? "translateZ" : "scale" : "scale");
	}

    /**
    * Returns the scene this object belongs to.
    */
	public Scene scene() {
	   return scene;
	}
	
	public void touchEvent(android.view.MotionEvent e) {
		int action = e.getAction();
		int code = action & android.view.MotionEvent.ACTION_MASK;
		int index = action >> android.view.MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		int turnOrientation;
		float x = e.getX(index);
		float y = e.getY(index);
		int id = e.getPointerId(index); 
		Gestures gesture;
		PApplet.println("touch");
		PApplet.print(x + " " + y + " " + id);
		// pass the events to the TouchProcessor
		if (code == android.view.MotionEvent.ACTION_DOWN || code == android.view.MotionEvent.ACTION_POINTER_DOWN) {
			// touch(new DOF6Event(x, y, 0, 0, 0, 0));
			PApplet.print("down");
			
			touchProcessor.pointDown(x, y, id);
			touchProcessor.parse();
			event = new DOF6Event(null,
					touchProcessor.getCx(),
					touchProcessor.getCy(),
					0,
					0,
					0,
					0,
					MotionEvent.NO_MODIFIER_MASK,
					e.getPointerCount());
			
			prevEvent = event.get();
			event = new DOF6Event(prevEvent,
					touchProcessor.getCx(),
					touchProcessor.getCy(),
					0,
					0,
					0,
					0,
					MotionEvent.NO_MODIFIER_MASK,
					e.getPointerCount());
			
			if (e.getPointerCount() == 1){
				updateTrackedGrabber(event);
			}
			
		}
		else if (code == android.view.MotionEvent.ACTION_UP || code == android.view.MotionEvent.ACTION_POINTER_UP) {
			PApplet.print("up");
			touchProcessor.pointUp(id);
			if (e.getPointerCount() == 1) {
				gesture = touchProcessor.parseTap();
				if (gesture == Gestures.TAP_ID) {
					handle(new ClickEvent(e.getX() - scene.originCorner().x(), e.getY() - scene.originCorner().y(), gesture.id()));
				}
				this.disableTracking();
				this.enableTracking();
			}

		}
		else if (code == android.view.MotionEvent.ACTION_MOVE) {
			PApplet.print("move");
			int numPointers = e.getPointerCount();
			for (int i = 0; i < numPointers; i++) {
				id = e.getPointerId(i);
				x = e.getX(i);
				y = e.getY(i);
				touchProcessor.pointMoved(x, y, id);
			}
			gesture = touchProcessor.parseGesture();
			if (gesture != null) {
				PApplet.print("Gesto " + gesture.id());
				/*event = new DOF6Event(prevEvent, touchProcessor.getCx(), touchProcessor.getCy(), 0, 0, 0, 0,
						MotionEvent.NO_MODIFIER_MASK,
						gesture.id());

				Action<?> a = (inputGrabber() instanceof InteractiveFrame) ? eyeProfile().handle((BogusEvent) event)
						: frameProfile().handle((BogusEvent) event);
				if (a == null)
					return;
				MotionAction dA = (MotionAction) a.referenceAction();
				if (dA == MotionAction.TRANSLATE_XYZ) {

				} else if (dA == MotionAction.TRANSLATE_XYZ_ROTATE_XYZ) {

				} else {*/
					if (prevEvent.id() != gesture.id()) {
						prevEvent = null;
					}
					switch (gesture) {
					case DRAG_ONE_ID:
					case DRAG_TWO_ID:
					case DRAG_THREE_ID:// Drag
						event = new DOF6Event(prevEvent,
								touchProcessor.getCx(),
								touchProcessor.getCy(),
								0,
								0,
								0,
								0,
								MotionEvent.NO_MODIFIER_MASK,
								gesture.id()
								);
						PApplet.print("drag");
						break;
					case OPPOSABLE_THREE_ID:
						event = new DOF6Event(prevEvent,
								x,
								y,
								0,
								0,
								0,
								0,
								MotionEvent.NO_MODIFIER_MASK,
								gesture.id()
								);
						PApplet.print("opposable");
						break;
					case PINCH_TWO_ID:
					case PINCH_THREE_ID: // Pinch
						event = new DOF6Event(prevEvent,
								touchProcessor.getZ(),
								0,
								0,
								0,
								0,
								0,
								MotionEvent.NO_MODIFIER_MASK,
								gesture.id()
								);
						PApplet.print("pinch");
						break;
					case TURN_TWO_ID:
					case TURN_THREE_ID: // Rotate
						turnOrientation = 1;
						// TODO needs testing
						if (inputGrabber() instanceof InteractiveFrame)
							turnOrientation = ((InteractiveFrame) inputGrabber()).isEyeFrame() ? -1 : 1;
						event = new DOF6Event(prevEvent,
								touchProcessor.getR() * turnOrientation,
								0,
								0,
								0,
								0,
								0,
								MotionEvent.NO_MODIFIER_MASK,
								gesture.id()
								);
						PApplet.print("rotate");
						break;
					default:
						break;

					}
				//}
				if (gesture != null) {
					if (prevEvent != null){
						handle(event);
					}
					prevEvent = event.get();
				}
			}
		}
	}
}