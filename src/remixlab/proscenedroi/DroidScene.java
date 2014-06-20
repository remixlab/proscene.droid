package remixlab.proscenedroi;

import processing.core.PApplet;
import processing.core.PGraphics;
import remixlab.bias.event.ClickEvent;
import remixlab.bias.event.DOF3Event;
import remixlab.bias.event.KeyboardEvent;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.InteractiveEyeFrame;
import remixlab.dandelion.core.InteractiveFrame;
import remixlab.proscene.Scene;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;

public class DroidScene extends Scene {
	ProsceneTouch me;
		
	public class ProsceneTouch extends JoystickAgent implements OnGestureListener, OnDoubleTapListener {
		Scene				scene;
		DOF3Event			event, prevEvent;
		InteractiveFrame	iFrame;
		GestureDetector 	mDetector;
		float 				histDistance;
		boolean 			CameraFirstPerson = false;
		
		public ProsceneTouch (Scene scn, String n) {
			super(scn, n);
			inputHandler().unregisterAgent(this);
			scene = scn;
	        eyeProfile().setBinding(DOF3Action.ROTATE);
	        frameProfile().setBinding(DOF3Action.ROTATE);
	        eyeProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, DOF3Action.TRANSLATE_XYZ);
	        frameProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, DOF3Action.TRANSLATE_XYZ);
	        eyeProfile().setBinding(B_NOMODIFIER_MASK, B_LEFT, DOF3Action.ROTATE_Z);
	        frameProfile().setBinding(B_NOMODIFIER_MASK, B_LEFT, DOF3Action.ROTATE_Z);
			me = this;
			
			parent.runOnUiThread(new Runnable() {
                public void run() {
                	mDetector = new GestureDetector(parent, me);
                }
			});
			
		}

		public void touchEvent(android.view.MotionEvent e) {
			int action = e.getActionMasked();          // get code for action
			
			mDetector.onTouchEvent(e);
			
			if (action == android.view.MotionEvent.ACTION_DOWN || action == android.view.MotionEvent.ACTION_POINTER_1_DOWN){
				PApplet.println((inputGrabber() instanceof InteractiveFrame && !(inputGrabber() instanceof InteractiveEyeFrame)));
				if( e.getPointerCount() == 1 || CameraFirstPerson
					|| (inputGrabber() instanceof InteractiveFrame && !(inputGrabber() instanceof InteractiveEyeFrame)) ){
					PApplet.println("DOWN");
					if (e.getX()< 10){
				        InputMethodManager imm = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
				        imm.toggleSoftInput(0, 0);  
				    }
					event = new DOF3Event(prevEvent, 
						      e.getX(), 
						      e.getY(),
						      0,
						      B_NOMODIFIER_MASK, 
						      B_NOBUTTON);
				}else{
					event = new DOF3Event(prevEvent, 
						      e.getX()*-1, 
						      e.getY()*-1,
						      0,
						      B_NOMODIFIER_MASK, 
						      B_NOBUTTON);
				}
				
				if( e.getPointerCount() == 1) updateTrackedGrabber(event);
				prevEvent = event.get();
			}
			if (action == android.view.MotionEvent.ACTION_UP || action == android.view.MotionEvent.ACTION_POINTER_1_UP){
				PApplet.println("UP");
				event = new DOF3Event(prevEvent, 
					      e.getX(), 
					      e.getY(),
					      0,
					      B_NOMODIFIER_MASK, 
					      B_NOBUTTON);
				prevEvent = event.get();
		        disableTracking();
		        enableTracking();
		        histDistance = 0f;
			}
			if (action == android.view.MotionEvent.ACTION_MOVE){
				PApplet.println("MOVE");
				if (e.getPointerCount() == 1){
					event = new DOF3Event(prevEvent, 
						      e.getX(), 
						      e.getY(),
						      0,
						      B_NOMODIFIER_MASK, 
						      B_NOBUTTON);
				    handle(event);
				    prevEvent = event.get();
			    }else{
			    	float distance;
			        if (histDistance == 0){
			          distance = 0;
			        }else{
			          distance = histDistance - (float) Math.sqrt((e.getX(0) - e.getX(1)) * (e.getX(0) - e.getX(1)) +
			        		  					 				  (e.getY(0) - e.getY(1)) * (e.getY(0) - e.getY(1)));
			        }
			        if ((inputGrabber() instanceof InteractiveFrame && !(inputGrabber() instanceof InteractiveEyeFrame))
			        	|| CameraFirstPerson) {
				    	event = new DOF3Event(prevEvent, 
				    		      e.getX(), 
				    		      e.getY(),
				    		      distance*-1, 
				    		      B_NOMODIFIER_MASK, 
				    		      B_CENTER);
			        }else{
			        	event = new DOF3Event(prevEvent, 
				    		      e.getX()*-1, 
				    		      e.getY()*-1,
				    		      distance, 
				    		      B_NOMODIFIER_MASK, 
				    		      B_CENTER);
			        }
	    		    handle(event);
	    		    prevEvent = event.get();
	    		    histDistance =  (float) Math.sqrt((e.getX(0) - e.getX(1)) * (e.getX(0) - e.getX(1)) + 
	    		    								  (e.getY(0) - e.getY(1)) * (e.getY(0) - e.getY(1)));
			    }
			}
		}
		
		public boolean isCameraFirstPerson() {
			return CameraFirstPerson;
		}

		public void setCameraFirstPerson(boolean cameraFirstPerson) {
			CameraFirstPerson = cameraFirstPerson;
		}
	
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			PApplet.println("SINGLETAP");
			updateTrackedGrabber(event);
			handle(new ClickEvent(e.getX() - scene.upperLeftCorner.x(), e.getY()
					- scene.upperLeftCorner.y(), 1));
		    disableTracking();
	        enableTracking();
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float arg0,
				float arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onScroll(MotionEvent e, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	public class ProsceneDroidKeyboard extends KeyboardAgent {
		public ProsceneDroidKeyboard(Scene scn, String n) {
			super(scn, n);
			inputHandler().unregisterAgent(this);
		}

		@Override
		public void setDefaultShortcuts() {
			super.setDefaultShortcuts();
			keyboardProfile().setShortcut('1', KeyboardAction.ADD_KEYFRAME_TO_PATH_1);
			keyboardProfile().setShortcut('2', KeyboardAction.DELETE_PATH_1);
			keyboardProfile().setShortcut('3', KeyboardAction.PLAY_PATH_1);

		}

		public void keyEvent(processing.event.KeyEvent e) {
			PApplet.println(e.getKeyCode());
		  
			if (e.getAction() == processing.event.KeyEvent.PRESS)
				handle(new KeyboardEvent(e.getKey()));
		}
	}
	
	public DroidScene(PApplet p) {
		this(p, p.g);
	}

	public DroidScene(PApplet p, PGraphics renderer) {
		this(p, renderer, 0, 0);
	}

	public DroidScene(PApplet p, PGraphics pg, int x, int y) {
		super(p,pg,x,y);
		p.orientation(PORTRAIT);
		defMotionAgent = new ProsceneTouch(this, "proscene_touch");
		enableTouchAgent();
		disableKeyboardAgent();
		defKeyboardAgent =new ProsceneDroidKeyboard(this, "proscene_keyboard");
		enableKeyboardAgent();
	}
	
	public void surfaceTouchEvent(android.view.MotionEvent event) {
		((ProsceneTouch)defMotionAgent).touchEvent(event);
	}
	
	
	@Override
	public void enableMotionAgent() {
		if (!inputHandler().isAgentRegistered(motionAgent())) {
			inputHandler().registerAgent(motionAgent());
			//parent.registerMethod("touchEvent", motionAgent());
		}
	}
	
	public void enableTouchAgent() {
		enableMotionAgent();
	}
	
	@Override
	public ActionWheeledBiMotionAgent<?> disableMotionAgent() {
		if (inputHandler().isAgentRegistered(motionAgent())) {
			//parent.unregisterMethod("touchEvent", motionAgent());
			return (ActionWheeledBiMotionAgent<?>) inputHandler().unregisterAgent(motionAgent());
		}
		return motionAgent();
	}

	public JoystickAgent disableTouchAgent() {
		return (JoystickAgent)disableMotionAgent();
	}
	
	public ProsceneTouch TouchAgent() {
		return (ProsceneTouch)motionAgent();
	}
	
	
}