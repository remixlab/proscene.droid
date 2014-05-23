package remixlab.proscene;

import processing.core.PApplet;
import processing.core.PGraphics;
import remixlab.bias.event.ClickEvent;
import remixlab.bias.event.DOF2Event;
import remixlab.bias.event.KeyboardEvent;
import remixlab.dandelion.agent.KeyboardAgent;
import remixlab.dandelion.agent.MouseAgent;
import remixlab.dandelion.core.InteractiveFrame;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class DroidScene extends Scene {
	ProsceneTouch me;
	
	public class ProsceneTouch extends MouseAgent  implements OnGestureListener, OnDoubleTapListener {
		Scene							scene;
		DOF2Event					event, prevEvent;
		InteractiveFrame	iFrame;
		private GestureDetector mDetector; 
		
		public ProsceneTouch (Scene scn, String n) {
			super(scn, n);
			inputHandler().unregisterAgent(this);
			scene = scn;
			eyeProfile().setBinding(DOF2Action.ROTATE);
			frameProfile().setBinding(DOF2Action.TRANSLATE);
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
			
			if (action == android.view.MotionEvent.ACTION_DOWN){
				PApplet.println("DOWN");
				event = new DOF2Event(prevEvent, 
					      e.getX(), 
					      e.getY(), 
					      B_NOMODIFIER_MASK, 
					      B_NOBUTTON);
				updateTrackedGrabber(event);
				prevEvent = event.get();
			}
			if (action == android.view.MotionEvent.ACTION_UP){
				PApplet.println("UP");
				event = new DOF2Event(prevEvent, 
					      e.getX(), 
					      e.getY(),  
					      B_NOMODIFIER_MASK, 
					      B_NOBUTTON);
				prevEvent = event.get();
		        disableTracking();
		        enableTracking();
			}
			if (action == android.view.MotionEvent.ACTION_MOVE){
				PApplet.println("MOVE");
				if (e.getPointerCount() == 1){
					event = new DOF2Event(prevEvent, 
						      e.getX(), 
						      e.getY(), 
						      B_NOMODIFIER_MASK, 
						      B_NOBUTTON);
				    handle(event);
				    prevEvent = event.get();
			    }else{
			    	event = new DOF2Event(prevEvent, 
			    		      e.getX(), 
			    		      e.getY(), 
			    		      B_NOMODIFIER_MASK, 
			    		      B_CENTER);
	    		    handle(event);
	    		    prevEvent = event.get();
			    }
			}
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
	public MouseAgent disableMotionAgent() {
		if (inputHandler().isAgentRegistered(motionAgent())) {
			//parent.unregisterMethod("touchEvent", motionAgent());
			return (MouseAgent) inputHandler().unregisterAgent(motionAgent());
		}
		return mouseAgent();
	}

	public MouseAgent disableTouchAgent() {
		return disableMotionAgent();
	}

}