/**************************************************************************************
 * ProScene.droid (version 0.1)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Victor Forero, https://sites.google.com/site/proscenedroi/home and
 *         and Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscenedroid;

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

/**
 * A 2D or 3D interactive Processing Android Scene. The Scene is a specialization of the
 * {@link remixlab.proscene.Scene} that can handle Android events and translate them into
 * dandelion actions.
 */
public class DroidScene extends Scene {
	ProsceneTouch me;
		
	public class ProsceneTouch extends JoystickAgent implements OnGestureListener, OnDoubleTapListener {
		Scene				scene;
		DOF3Event			event, prevEvent;
		InteractiveFrame	iFrame;
		GestureDetector 	mDetector;
		float 				histDistance;
		boolean 			firstPerson = false;
		
		public ProsceneTouch (Scene scn, String n) {
			super(scn, n);
			inputHandler().unregisterAgent(this);
			scene = scn;
	        eyeProfile().setBinding(DOF3Action.ROTATE);
	        frameProfile().setBinding(DOF3Action.ROTATE);
	        eyeProfile().setBinding(DOF3Event.NOMODIFIER_MASK, CENTER,DOF3Action.TRANSLATE_XYZ);
	        frameProfile().setBinding(DOF3Event.NOMODIFIER_MASK, CENTER, DOF3Action.TRANSLATE_XYZ);
	        eyeProfile().setBinding(DOF3Event.NOMODIFIER_MASK, LEFT, DOF3Action.ROTATE_Z);
	        frameProfile().setBinding(DOF3Event.NOMODIFIER_MASK, LEFT, DOF3Action.ROTATE_Z);
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
				if( e.getPointerCount() == 1 || firstPerson
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
						      DOF3Event.NOMODIFIER_MASK, 
						      DOF3Event.NOBUTTON);
				}else{
					event = new DOF3Event(prevEvent, 
						      e.getX()*-1, 
						      e.getY()*-1,
						      0,
						      DOF3Event.NOMODIFIER_MASK, 
						      DOF3Event.NOBUTTON);
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
					      DOF3Event.NOMODIFIER_MASK, 
					      DOF3Event.NOBUTTON);
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
						      DOF3Event.NOMODIFIER_MASK, 
						      DOF3Event.NOBUTTON);
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
			        	|| firstPerson) {
				    	event = new DOF3Event(prevEvent, 
				    		      e.getX(), 
				    		      e.getY(),
				    		      distance*-1, 
				    		      DOF3Event.NOMODIFIER_MASK, 
							      CENTER);
			        }else{
			        	event = new DOF3Event(prevEvent, 
				    		      e.getX()*-1, 
				    		      e.getY()*-1,
				    		      distance, 
				    		      DOF3Event.NOMODIFIER_MASK, 
							      CENTER);
			        }
	    		    handle(event);
	    		    prevEvent = event.get();
	    		    histDistance =  (float) Math.sqrt((e.getX(0) - e.getX(1)) * (e.getX(0) - e.getX(1)) + 
	    		    								  (e.getY(0) - e.getY(1)) * (e.getY(0) - e.getY(1)));
			    }
			}
		}
		
		public boolean isAsFirstPerson() {
			return firstPerson;
		}

		public void setAsFirstPerson(boolean cameraFirstPerson) {
			firstPerson = cameraFirstPerson;
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
	
	/**
	 * Same as {@code this(p, p.g)}.
	 * 
	 * @see #DroidScene(Scene, String)
	 */
	public DroidScene(PApplet p) {
		this(p, p.g);
	}

	/**
	 * Same as {@code this(p, renderer, 0, 0)}.
	 * 
	 * @see #DroidScene(PApplet, PGraphics, int, int)
	 */
	public DroidScene(PApplet p, PGraphics renderer) {
		this(p, renderer, 0, 0);
	}

	/**
	 * Calls {@code super(p,pg,x,y)} and sets a fixed Android PORTRAIT Scene. Finally initializes the
	 * default agents: {@link #touchAgent()} and the keyboard agent.
	 */
	public DroidScene(PApplet p, PGraphics pg, int x, int y) {
		super(p,pg,x,y);
		p.orientation(PORTRAIT);
		defMotionAgent = new ProsceneTouch(this, "proscene_touch");
		enableTouchAgent();
		disableKeyboardAgent();
		defKeyboardAgent =new ProsceneDroidKeyboard(this, "proscene_keyboard");
		enableKeyboardAgent();
	}
	
	/**
	 * Called by {@link android.app.Activity#dispatchTouchEvent(MotionEvent)} or
	 * {@link processing.core.PApplet#surfaceTouchEvent(MotionEvent)} on the Android {@link android.view.MotionEvent}.
	 * <p>
	 * The Android motion event is then processed by the {@link #touchAgent()}.
	 */
	public void touchEvent(android.view.MotionEvent event) {
		((ProsceneTouch)defMotionAgent).touchEvent(event);
	}
	
	@Override
	public void enableMotionAgent() {
		if (!inputHandler().isAgentRegistered(motionAgent())) {
			inputHandler().registerAgent(motionAgent());
			//parent.registerMethod("touchEvent", motionAgent());
		}
	}
	
	/**
	 * Calls {@code touchAgent().setAsFirstPerson(fp)}.
	 */
	public void setTouchAsFirstPerson(boolean fp) {
		touchAgent().setAsFirstPerson(fp);
	}
	
	/**
	 * Same as {@code touchAgent().isAsFirstPerson()}.
	 */
	public boolean isTouchAsFirstPerson() {
		return touchAgent().isAsFirstPerson();
	}
	
	/**
	 * Calls {@link #enableMotionAgent()}.
	 */
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

	/**
	 * Returns {@link #disableMotionAgent()}.
	 */
	public JoystickAgent disableTouchAgent() {
		return (JoystickAgent)disableMotionAgent();
	}
	
	/**
	 * Returns {@link #motionAgent()}.
	 */
	public ProsceneTouch touchAgent() {
		return (ProsceneTouch)motionAgent();
	}
	
	
}