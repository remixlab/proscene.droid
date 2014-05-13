/**************************************************************************************
 * ProScene (version 2.0.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import processing.core.*;
import processing.opengl.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.*;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * A 2D or 3D interactive Processing Scene. The Scene is a specialization of the
 * {@link remixlab.dandelion.core.AbstractScene}, providing an interface between Dandelion and Processing.
 * <p>
 * <h3>Usage</h3>
 * To use a Scene you have three choices:
 * <ol>
 * <li><b>Direct instantiation</b>. In this case you should instantiate your own Scene object at the
 * {@code PApplet.setup()} function. See the example <i>BasicUse</i>.
 * <li><b>Inheritance</b>. In this case, once you declare a Scene derived class, you should implement
 * {@link #proscenium()} which defines the objects in your scene. Just make sure to define the {@code PApplet.draw()}
 * method, even if it's empty. See the example <i>AlternativeUse</i>.
 * <li><b>External draw handler registration</b>. In addition (not being part of Dandelion), you can even declare an
 * external drawing method and then register it at the Scene with {@link #addDrawHandler(Object, String)}. That method
 * should return {@code void} and have one single {@code Scene} parameter. This strategy may be useful when there are
 * multiple viewers sharing the same drawing code. See the example <i>StandardCamera</i>.
 * </ol>
 * <h3>Interactivity mechanisms</h3>
 * Through Dandelion, Proscene provides powerful interactivity mechanisms allowing a wide range of scene setups ranging
 * from very simple to complex ones. For convenience, two interaction mechanisms are provided by default:
 * {@link #keyboardAgent()}, and {@link #mouseAgent()}:
 * <ol>
 * <li><b>The default keyboard agent</b> provides shortcuts to general keyboard dandelion actions such as
 * {@link #drawGrid()} or {@link #drawAxes()}. See
 * {@link #setKeyboardShortcut(Character, remixlab.dandelion.core.Constants.KeyboardAction)} and
 * {@link #setKeyboardShortcut(int, int, remixlab.dandelion.core.Constants.KeyboardAction)}.
 * <li><b>The default mouse agent</b> provides high-level methods to manage camera and frame motion actions. Please
 * refer to the different {@code setMouseButtonBinding()}, {@code setMouseClickBinding()},
 * {@code setMouseWheelBinding()} methods.
 * </ol>
 * <h3>Animation mechanisms</h3>
 * Proscene provides three animation mechanisms to define how your scene evolves over time:
 * <ol>
 * <li><b>Overriding the Dandelion {@link #animate()} method.</b> In this case, once you declare a Scene derived class,
 * you should implement {@link #animate()} which defines how your scene objects evolve over time. See the example
 * <i>Animation</i>.
 * <li><b>By checking if the Dandelion AbstractScene's {@link #timer()} was triggered within the frame.</b> See the
 * example <i>Flock</i>.
 * <li><b>External animation handler registration.</b> In addition (not being part of Dandelion), you can also declare
 * an external animation method and then register it at the Scene with {@link #addAnimationHandler(Object, String)}.
 * That method should return {@code void} and have one single {@code Scene} parameter. See the example
 * <i>AnimationHandler</i>.
 */
public class Scene extends AbstractScene implements PConstants {
	/**
	 * Convenience function that simply returns {@code p5ButtonModifiersFix(B_NOMODIFIER_MASK, button)}.
	 * 
	 * @see #p5ButtonModifiersFix(int, int)
	 */
	public static int p5ButtonModifiersFix(int button) {
		return p5ButtonModifiersFix(B_NOMODIFIER_MASK, button);
	}

	/**
	 * Hack to fix <a href="https://github.com/processing/processing/issues/1693">Processing MouseEvent.getModifiers()
	 * issue</a>
	 * 
	 * @param m
	 *          original Processing modifiers mask
	 * @param button
	 *          original Processing button
	 * @return fixed mask
	 */
	public static int p5ButtonModifiersFix(int m, int button) {
		int mask = m;
		// ALT
		if (button == B_CENTER)
			mask = (B_ALT | m);
		// META
		else if (button == B_RIGHT)
			mask = (B_META | m);
		return mask;
	}

	/**
	 * Wrapper for PGraphics.vertex(x,y,z)
	 */
	public void vertex(float x, float y, float z) {
		if (this.is2D())
			pg().vertex(x, y);
		else
			pg().vertex(x, y, z);
	}

	/**
	 * Wrapper for PGraphics.vertex(x,y)
	 */
	public void vertex(float x, float y) {
		pg().vertex(x, y);
	}

	/**
	 * Wrapper for PGraphics.line(x1, y1, z1, x2, y2, z2)
	 */
	public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
		if (this.is2D())
			pg().line(x1, y1, x2, y2);
		else
			pg().line(x1, y1, z1, x2, y2, z2);
	}

	/**
	 * Wrapper for PGraphics.line(x1, y1, x2, y2)
	 */
	public void line(float x1, float y1, float x2, float y2) {
		pg().line(x1, y1, x2, y2);
	}

	/**
	 * Converts a {@link remixlab.dandelion.geom.Vec} to a PVec.
	 */
	public static PVector toPVector(Vec v) {
		return new PVector(v.x(), v.y(), v.z());
	}

	/**
	 * Converts a PVec to a {@link remixlab.dandelion.geom.Vec}.
	 */
	public static Vec toVec(PVector v) {
		return new Vec(v.x, v.y, v.z);
	}

	/**
	 * Converts a {@link remixlab.dandelion.geom.Mat} to a PMatrix3D.
	 */
	public static PMatrix3D toPMatrix(Mat m) {
		float[] a = m.getTransposed(new float[16]);
		return new PMatrix3D(a[0], a[1], a[2], a[3],
				a[4], a[5], a[6], a[7],
				a[8], a[9], a[10], a[11],
				a[12], a[13], a[14], a[15]);
	}

	/**
	 * Converts a PMatrix3D to a {@link remixlab.dandelion.geom.Mat}.
	 */
	public static Mat toMat(PMatrix3D m) {
		return new Mat(m.get(new float[16]), true);
	}

	/**
	 * Converts a PMatrix2D to a {@link remixlab.dandelion.geom.Mat}.
	 */
	public static Mat toMat(PMatrix2D m) {
		return toMat(new PMatrix3D(m));
	}

	/**
	 * Converts a {@link remixlab.dandelion.geom.Mat} to a PMatrix2D.
	 */
	public static PMatrix2D toPMatrix2D(Mat m) {
		float[] a = m.getTransposed(new float[16]);
		return new PMatrix2D(a[0], a[1], a[3],
				a[4], a[5], a[7]);
	}

	/**
	 * Proscene {@link remixlab.dandelion.agent.KeyboardAgent}.
	 */
	public class ProsceneKeyboard extends KeyboardAgent {
		public ProsceneKeyboard(Scene scn, String n) {
			super(scn, n);
			inputHandler().unregisterAgent(this);
		}

		/**
		 * Calls {@link remixlab.dandelion.agent.KeyboardAgent#setDefaultShortcuts()} and then adds the following:
		 * <p>
		 * {@code B_CTRL + java.awt.event.KeyEvent.VK_1 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_1}<br>
		 * {@code B_ALT + java.awt.event.KeyEvent.VK_1 -> KeyboardAction.DELETE_PATH_1}<br>
		 * {@code B_CTRL + java.awt.event.KeyEvent.VK_2 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_2}<br>
		 * {@code B_ALT + java.awt.event.KeyEvent.VK_2 -> KeyboardAction.DELETE_PATH_2}<br>
		 * {@code B_CTRL + java.awt.event.KeyEvent.VK_3 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_3}<br>
		 * {@code B_ALT + java.awt.event.KeyEvent.VK_3 -> KeyboardAction.DELETE_PATH_3}<br>
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
			super.setDefaultShortcuts();
			// keyboardProfile().setShortcut(B_CTRL, java.awt.event.KeyEvent.VK_1, KeyboardAction.ADD_KEYFRAME_TO_PATH_1);
			keyboardProfile().setShortcut(B_CTRL, 49, KeyboardAction.ADD_KEYFRAME_TO_PATH_1);
			// keyboardProfile().setShortcut(B_ALT, java.awt.event.KeyEvent.VK_1, KeyboardAction.DELETE_PATH_1);
			keyboardProfile().setShortcut(B_ALT, 49, KeyboardAction.DELETE_PATH_1);
			// keyboardProfile().setShortcut(B_CTRL, java.awt.event.KeyEvent.VK_2, KeyboardAction.ADD_KEYFRAME_TO_PATH_2);
			keyboardProfile().setShortcut(B_CTRL, 50, KeyboardAction.ADD_KEYFRAME_TO_PATH_2);
			// keyboardProfile().setShortcut(B_ALT, java.awt.event.KeyEvent.VK_2, KeyboardAction.DELETE_PATH_2);
			keyboardProfile().setShortcut(B_ALT, 50, KeyboardAction.DELETE_PATH_2);
			// keyboardProfile().setShortcut(B_CTRL, java.awt.event.KeyEvent.VK_3, KeyboardAction.ADD_KEYFRAME_TO_PATH_3);
			keyboardProfile().setShortcut(B_CTRL, 51, KeyboardAction.ADD_KEYFRAME_TO_PATH_3);
			// keyboardProfile().setShortcut(B_ALT, java.awt.event.KeyEvent.VK_3, KeyboardAction.DELETE_PATH_3);
			keyboardProfile().setShortcut(B_ALT, 51, KeyboardAction.DELETE_PATH_3);
			// setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_1, 1);
			setKeyCodeToPlayPath(49, 1);
			// setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_2, 2);
			setKeyCodeToPlayPath(50, 2);
			// setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_3, 3);
			setKeyCodeToPlayPath(51, 3);
		}

		public void keyEvent(processing.event.KeyEvent e) {
			if (e.getAction() == processing.event.KeyEvent.TYPE)
				handle(new KeyboardEvent(e.getKey()));
			else if (e.getAction() == processing.event.KeyEvent.RELEASE)
				handle(new KeyboardEvent(e.getModifiers(), e.getKeyCode()));
		}
	}
	
	/**
	 * Proscene {@link remixlab.dandelion.agent.MouseAgent}.
	 */
	public class ProsceneTouch extends MouseAgent {
		Scene							scene;
		DOF2Event					event, prevEvent;
		InteractiveFrame	iFrame;
		
		public ProsceneTouch (Scene scn, String n) {
			super(scn, n);
			this.enableTracking();
			scene = scn;
			eyeProfile().setBinding(DOF2Action.ROTATE);
			frameProfile().setBinding(DOF2Action.TRANSLATE);
		}

		public void touchEvent(android.view.MotionEvent e) {
			int action = e.getActionMasked();          // get code for action
			
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
	}

	/**
	 * Proscene {@link remixlab.dandelion.agent.MouseAgent}.
	 */
	public class ProsceneMouse extends MouseAgent {
		Scene							scene;
		boolean						bypassNullEvent, need4Spin, drive, rotateMode;
		Point							fCorner		= new Point();
		Point							lCorner		= new Point();
		DOF2Event					event, prevEvent, pressEvent;
		float							dFriction	= eye().frame().dampingFriction();
		InteractiveFrame	iFrame;

		public ProsceneMouse(Scene scn, String n) {
			super(scn, n);
			inputHandler().unregisterAgent(this);
			scene = scn;
		}

		public void mouseEvent(processing.event.MouseEvent e) {
			if (e.getAction() == processing.event.MouseEvent.MOVE) {
				event = new DOF2Event(prevEvent, e.getX() - scene.upperLeftCorner.x(), e.getY()
						- scene.upperLeftCorner.y());
				updateTrackedGrabber(event);
				prevEvent = event.get();
			}
			if (e.getAction() == processing.event.MouseEvent.PRESS) {
				event = new DOF2Event(prevEvent, e.getX() - scene.upperLeftCorner.x(), e.getY()
						- scene.upperLeftCorner.y(), e.getModifiers(), e.getButton());
				pressEvent = event.get();
				if (inputGrabber() instanceof InteractiveFrame) {
					if (need4Spin)
						((InteractiveFrame) inputGrabber()).stopSpinning();
					iFrame = (InteractiveFrame) inputGrabber();
					Action<?> a = (inputGrabber() instanceof InteractiveEyeFrame) ? eyeProfile().handle((BogusEvent) event)
							: frameProfile().handle((BogusEvent) event);
					if (a == null)
						return;
					DandelionAction dA = (DandelionAction) a.referenceAction();
					if (dA == DandelionAction.SCREEN_TRANSLATE)
						((InteractiveFrame) inputGrabber()).dirIsFixed = false;
					rotateMode = ((dA == DandelionAction.ROTATE) || (dA == DandelionAction.ROTATE_XYZ)
							|| (dA == DandelionAction.ROTATE_CAD)
							|| (dA == DandelionAction.SCREEN_ROTATE) || (dA == DandelionAction.TRANSLATE_XYZ_ROTATE_XYZ));
					if (rotateMode && scene.is3D())
						scene.camera().frame().cadRotationIsReversed = scene.camera().frame()
								.transformOf(scene.camera().frame().sceneUpVector()).y() < 0.0f;
					need4Spin = (rotateMode && (((InteractiveFrame) inputGrabber()).dampingFriction() == 0));
					drive = (dA == DandelionAction.DRIVE);
					bypassNullEvent = (dA == DandelionAction.MOVE_FORWARD) || (dA == DandelionAction.MOVE_BACKWARD)
							|| (drive) && scene.inputHandler().isAgentRegistered(this);
					setZoomVisualHint(dA == DandelionAction.ZOOM_ON_REGION && (inputGrabber() instanceof InteractiveEyeFrame)
							&& scene.inputHandler().isAgentRegistered(this));
					setRotateVisualHint(dA == DandelionAction.SCREEN_ROTATE && (inputGrabber() instanceof InteractiveEyeFrame)
							&& scene.inputHandler().isAgentRegistered(this));
					if (bypassNullEvent || zoomVisualHint() || rotateVisualHint()) {
						if (bypassNullEvent) {
							// This is needed for first person:
							((InteractiveFrame) inputGrabber()).updateSceneUpVector();
							dFriction = ((InteractiveFrame) inputGrabber()).dampingFriction();
							((InteractiveFrame) inputGrabber()).setDampingFriction(0);
							handler.eventTupleQueue().add(new EventGrabberTuple(event, a, inputGrabber()));
						}
						if (zoomVisualHint() || rotateVisualHint()) {
							lCorner.set(e.getX() - scene.upperLeftCorner.x(), e.getY() - scene.upperLeftCorner.y());
							if (zoomVisualHint())
								fCorner.set(e.getX() - scene.upperLeftCorner.x(), e.getY() - scene.upperLeftCorner.y());
						}
					}
					else
						handle(event);
				} else
					handle(event);
				prevEvent = event.get();
			}
			if (e.getAction() == processing.event.MouseEvent.DRAG) {
				if (zoomVisualHint() || rotateVisualHint())
					lCorner.set(e.getX() - scene.upperLeftCorner.x(), e.getY() - scene.upperLeftCorner.y());
				if (!zoomVisualHint()) { // bypass zoom_on_region, may be different when using a touch device :P
					event = new DOF2Event(prevEvent, e.getX() - scene.upperLeftCorner.x(), e.getY()
							- scene.upperLeftCorner.y(), e.getModifiers(), e.getButton());
					if (drive && inputGrabber() instanceof InteractiveFrame)
						((InteractiveFrame) inputGrabber()).setFlySpeed(0.01f * radius() * 0.01f * (event.y() - pressEvent.y()));
					handle(event);
					prevEvent = event.get();
				}
			}
			if (e.getAction() == processing.event.MouseEvent.RELEASE) {
				if (inputGrabber() instanceof InteractiveFrame)
					if (need4Spin && (prevEvent.speed() >= ((InteractiveFrame) inputGrabber()).spinningSensitivity()))
						((InteractiveFrame) inputGrabber()).startSpinning(prevEvent);
				event = new DOF2Event(prevEvent, e.getX() - scene.upperLeftCorner.x(), e.getY()
						- scene.upperLeftCorner.y(), e.getModifiers(), e.getButton());
				if (zoomVisualHint()) {
					// at first glance this should work
					// handle(event);
					// but the problem is that depending on the order the button and the modifiers are released,
					// different actions maybe triggered, so we go for sure ;) :
					enqueueEventTuple(new EventGrabberTuple(event, DOF2Action.ZOOM_ON_REGION, inputGrabber()));
					setZoomVisualHint(false);
				}
				if (rotateVisualHint())
					setRotateVisualHint(false);
				updateTrackedGrabber(event);
				prevEvent = event.get();
				if (bypassNullEvent) {
					iFrame.setDampingFriction(dFriction);
					bypassNullEvent = !bypassNullEvent;
				}
				// restore speed after drive action terminates:
				if (drive && inputGrabber() instanceof InteractiveFrame)
					((InteractiveFrame) inputGrabber()).setFlySpeed(0.01f * radius());
			}
			if (e.getAction() == processing.event.MouseEvent.WHEEL) {
				handle(new DOF1Event(e.getCount(), e.getModifiers(), B_NOBUTTON));
			}
			if (e.getAction() == processing.event.MouseEvent.CLICK) {
				handle(new ClickEvent(e.getX() - scene.upperLeftCorner.x(), e.getY()
						- scene.upperLeftCorner.y(), e.getModifiers(), e.getButton(), e.getCount()));
			}
		}

		/**
		 * Hack to deal with this: https://github.com/processing/processing/issues/1693 is to override all the following so
		 * that:
		 * <p>
		 * <ol>
		 * <li>Whenever B_CENTER appears B_ALT should be present.</li>
		 * <li>Whenever B_RIGHT appears B_META should be present.</li>
		 * </ol>
		 */
		@Override
		public void setAsFirstPerson() {
			resetAllProfiles();
			eyeProfile().setBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, DOF2Action.MOVE_FORWARD);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_RIGHT), B_RIGHT, DOF2Action.MOVE_BACKWARD);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_LEFT), B_LEFT, DOF2Action.ROTATE_Z);
			eyeWheelProfile().setBinding(B_CTRL, B_NOBUTTON, WheelAction.ROTATE_Z);
			if (is3D()) {
				eyeProfile().setBinding(p5ButtonModifiersFix(B_CENTER), B_CENTER, DOF2Action.LOOK_AROUND);
				eyeProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_CENTER), B_CENTER, DOF2Action.DRIVE);
			}
			frameProfile().setBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, DOF2Action.ROTATE);
			frameProfile().setBinding(p5ButtonModifiersFix(B_CENTER), B_CENTER, DOF2Action.SCALE);
			frameProfile().setBinding(p5ButtonModifiersFix(B_RIGHT), B_RIGHT, DOF2Action.TRANSLATE);
			frameProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_CENTER), B_CENTER, DOF2Action.SCREEN_TRANSLATE);
			frameProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_RIGHT), B_RIGHT, DOF2Action.SCREEN_ROTATE);
			setCommonBindings();
		}

		@Override
		public void setAsThirdPerson() {
			resetAllProfiles();
			frameProfile().setBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, DOF2Action.MOVE_FORWARD);
			frameProfile().setBinding(p5ButtonModifiersFix(B_RIGHT), B_RIGHT, DOF2Action.MOVE_BACKWARD);
			frameProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_LEFT), B_LEFT, DOF2Action.ROTATE_Z);
			if (is3D()) {
				frameProfile().setBinding(p5ButtonModifiersFix(B_CENTER), B_CENTER, DOF2Action.LOOK_AROUND);
				frameProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_CENTER), B_CENTER, DOF2Action.DRIVE);
			}
			setCommonBindings();
		}

		@Override
		public void setAsArcball() {
			resetAllProfiles();
			eyeProfile().setBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, DOF2Action.ROTATE);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_CENTER), B_CENTER, is3D() ? DOF2Action.ZOOM : DOF2Action.SCALE);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_RIGHT), B_RIGHT, DOF2Action.TRANSLATE);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_LEFT), B_LEFT, DOF2Action.ZOOM_ON_REGION);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_CENTER), B_CENTER, DOF2Action.SCREEN_TRANSLATE);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_RIGHT), B_RIGHT, DOF2Action.SCREEN_ROTATE);
			frameProfile().setBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, DOF2Action.ROTATE);
			frameProfile().setBinding(p5ButtonModifiersFix(B_CENTER), B_CENTER, DOF2Action.SCALE);
			frameProfile().setBinding(p5ButtonModifiersFix(B_RIGHT), B_RIGHT, DOF2Action.TRANSLATE);
			frameProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_CENTER), B_CENTER, DOF2Action.SCREEN_TRANSLATE);
			frameProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_RIGHT), B_RIGHT, DOF2Action.SCREEN_ROTATE);
			setCommonBindings();
		}

		@Override
		protected void setCommonBindings() {
			eyeClickProfile().setClickBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, 2, ClickAction.ALIGN_FRAME);
			eyeClickProfile().setClickBinding(p5ButtonModifiersFix(B_RIGHT), B_RIGHT, 2, ClickAction.CENTER_FRAME);
			frameClickProfile().setClickBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, 2, ClickAction.ALIGN_FRAME);
			frameClickProfile().setClickBinding(p5ButtonModifiersFix(B_RIGHT), B_RIGHT, 2, ClickAction.CENTER_FRAME);
			eyeWheelProfile().setBinding(B_NOMODIFIER_MASK, B_NOBUTTON, is3D() ? WheelAction.ZOOM : WheelAction.SCALE);
			frameWheelProfile().setBinding(B_NOMODIFIER_MASK, B_NOBUTTON, WheelAction.SCALE);
		}
	}

	/**
	 * Non-seq timer based on java.util.Timer and java.util.TimerTask.
	 */
	protected class NonSeqTimer implements Timer {
		Scene								scene;
		java.util.Timer			timer;
		java.util.TimerTask	timerTask;
		Taskable						tmnTask;
		boolean							runOnlyOnce;
		boolean							active;
		long								prd;

		public NonSeqTimer(Scene scn, Taskable o) {
			this(scn, o, false);
		}

		public NonSeqTimer(Scene scn, Taskable o, boolean singleShot) {
			scene = scn;
			runOnlyOnce = singleShot;
			tmnTask = o;
		}

		@Override
		public Taskable timingTask() {
			return tmnTask;
		}

		@Override
		public void create() {
			stop();
			timer = new java.util.Timer();
			timerTask = new java.util.TimerTask() {
				public void run() {
					tmnTask.execute();
				}
			};
		}

		@Override
		public void run(long period) {
			setPeriod(period);
			run();
		}

		@Override
		public void run() {
			create();
			if (isSingleShot())
				timer.schedule(timerTask, prd);
			else
				timer.scheduleAtFixedRate(timerTask, 0, prd);
			active = true;
		}

		@Override
		public void cancel() {
			stop();
		}

		@Override
		public void stop() {
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
			active = false;
		}

		@Override
		public boolean isActive() {
			return timer != null && active;
		}

		@Override
		public long period() {
			return prd;
		}

		@Override
		public void setPeriod(long period) {
			prd = period;
		}

		@Override
		public boolean isSingleShot() {
			return runOnlyOnce;
		}

		@Override
		public void setSingleShot(boolean singleShot) {
			runOnlyOnce = singleShot;
		}
	}

	/**
	 * Internal {@link remixlab.dandelion.core.MatrixHelper} based on PGraphicsJava2D graphics transformations.
	 */
	protected class P5Java2DMatrixHelper extends MatrixHelper {
		protected PGraphics	pg;

		public P5Java2DMatrixHelper(Scene scn, PGraphics renderer) {
			super(scn);
			pg = renderer;
		}

		public PGraphics pg() {
			return pg;
		}

		// Comment the above line and uncomment this one to develop the driver:
		// public PGraphicsJava2D pg() { return (PGraphicsJava2D) pg; }

		@Override
		public void bind() {
			scene.eye().computeProjection();
			scene.eye().computeView();
			cacheProjectionViewInverse();

			Vec pos = scene.eye().position();
			Rotation o = scene.eye().frame().orientation();
			translate(scene.width() / 2, scene.height() / 2);

			if (scene.isRightHanded())
				scale(1, -1);
			scale(1 / scene.eye().frame().scaling(), 1 / scene.eye().frame().scaling());
			rotate(-o.angle());
			translate(-pos.x(), -pos.y());
		}

		@Override
		protected void cacheProjectionViewInverse() {
			Mat.multiply(scene.eye().getProjection(), scene.eye().getView(), projectionViewMat);
			if (unprojectCacheIsOptimized()) {
				if (projectionViewInverseMat == null)
					projectionViewInverseMat = new Mat();
				projectionViewMatHasInverse = projectionViewMat.invert(projectionViewInverseMat);
			}
		}

		@Override
		public void beginScreenDrawing() {
			Vec pos = scene.eye().position();
			Rotation quat = scene.eye().frame().orientation();

			pushModelView();
			translate(pos.x(), pos.y());
			rotate(quat.angle());
			scale(scene.window().frame().scaling(), scene.window().frame().scaling());
			if (scene.isRightHanded())
				scale(1, -1);
			translate(-scene.width() / 2, -scene.height() / 2);
		}

		@Override
		public void endScreenDrawing() {
			popModelView();
		}

		@Override
		public void pushModelView() {
			pg().pushMatrix();
		}

		@Override
		public void popModelView() {
			pg().popMatrix();
		}

		@Override
		public void resetModelView() {
			pg().resetMatrix();
		}

		@Override
		public Mat modelView() {
			return Scene.toMat(new PMatrix2D(pg().getMatrix()));
		}

		@Override
		public Mat getModelView(Mat target) {
			if (target == null)
				target = Scene.toMat((PMatrix2D) pg().getMatrix()).get();
			else
				target.set(Scene.toMat((PMatrix2D) pg().getMatrix()));
			return target;
		}

		@Override
		public void setModelView(Mat source) {
			pg().setMatrix(Scene.toPMatrix2D(source));
		}

		@Override
		public void printModelView() {
			pg().printMatrix();
		}

		@Override
		public void printProjection() {
			pg().printProjection();
		}

		@Override
		public void applyModelView(Mat source) {
			pg().applyMatrix(Scene.toPMatrix2D(source));
		}

		@Override
		public void translate(float tx, float ty) {
			pg().translate(tx, ty);
		}

		@Override
		public void translate(float tx, float ty, float tz) {
			pg().translate(tx, ty, tz);
		}

		@Override
		public void rotate(float angle) {
			pg().rotate(angle);
		}

		@Override
		public void rotateX(float angle) {
			pg().rotateX(angle);
		}

		@Override
		public void rotateY(float angle) {
			pg().rotateY(angle);
		}

		@Override
		public void rotateZ(float angle) {
			pg().rotateZ(angle);
		}

		@Override
		public void rotate(float angle, float vx, float vy, float vz) {
			pg().rotate(angle, vx, vy, vz);
		}

		@Override
		public void scale(float s) {
			pg().scale(s);
		}

		@Override
		public void scale(float sx, float sy) {
			pg().scale(sx, sy);
		}

		@Override
		public void scale(float x, float y, float z) {
			pg().scale(x, y, z);
		}
	}

	/**
	 * Internal {@link remixlab.dandelion.core.MatrixHelper} based on PGraphicsOpenGL graphics transformation.
	 */
	protected class P5GLMatrixHelper extends MatrixHelper {
		PGraphicsOpenGL	pg;

		public P5GLMatrixHelper(Scene scn, PGraphicsOpenGL renderer) {
			super(scn);
			pg = renderer;
		}

		public PGraphicsOpenGL pggl() {
			return pg;
		}

		@Override
		public void pushProjection() {
			pggl().pushProjection();
		}

		@Override
		public void popProjection() {
			pggl().popProjection();
		}

		@Override
		public void resetProjection() {
			pggl().resetProjection();
		}

		@Override
		public void printProjection() {
			pggl().printProjection();
		}

		@Override
		public Mat projection() {
			return Scene.toMat(pggl().projection.get());
		}

		@Override
		public Mat getProjection(Mat target) {
			if (target == null)
				target = Scene.toMat(pggl().projection.get()).get();
			else
				target.set(Scene.toMat(pggl().projection.get()));
			return target;
		}

		@Override
		public void applyProjection(Mat source) {
			pggl().applyProjection(Scene.toPMatrix(source));
		}

		@Override
		public void pushModelView() {
			pggl().pushMatrix();
		}

		@Override
		public void popModelView() {
			pggl().popMatrix();
		}

		@Override
		public void resetModelView() {
			pggl().resetMatrix();
		}

		@Override
		public Mat modelView() {
			return Scene.toMat((PMatrix3D) pggl().getMatrix());
		}

		@Override
		public Mat getModelView(Mat target) {
			if (target == null)
				target = Scene.toMat((PMatrix3D) pggl().getMatrix()).get();
			else
				target.set(Scene.toMat((PMatrix3D) pggl().getMatrix()));
			return target;
		}

		@Override
		public void printModelView() {
			pggl().printMatrix();
		}

		@Override
		public void applyModelView(Mat source) {
			pggl().applyMatrix(Scene.toPMatrix(source));
		}

		@Override
		public void translate(float tx, float ty) {
			pggl().translate(tx, ty);
		}

		@Override
		public void translate(float tx, float ty, float tz) {
			pggl().translate(tx, ty, tz);
		}

		@Override
		public void rotate(float angle) {
			pggl().rotate(angle);
		}

		@Override
		public void rotateX(float angle) {
			pggl().rotateX(angle);
		}

		@Override
		public void rotateY(float angle) {
			pggl().rotateY(angle);
		}

		@Override
		public void rotateZ(float angle) {
			pggl().rotateZ(angle);
		}

		@Override
		public void rotate(float angle, float vx, float vy, float vz) {
			pggl().rotate(angle, vx, vy, vz);
		}

		@Override
		public void scale(float s) {
			pggl().scale(s);
		}

		@Override
		public void scale(float sx, float sy) {
			pggl().scale(sx, sy);
		}

		@Override
		public void scale(float x, float y, float z) {
			pggl().scale(x, y, z);
		}

		@Override
		public void setProjection(Mat source) {
			pggl().setProjection(Scene.toPMatrix(source));
		}

		@Override
		public void setModelView(Mat source) {
			if (is3D())
				pggl().setMatrix(Scene.toPMatrix(source));// in P5 this caches projmodelview
			else {
				pggl().modelview.set(Scene.toPMatrix(source));
				pggl().projmodelview.set(Mat.multiply(scene.eye().getProjection(false), scene.eye().getView(false))
						.getTransposed(new float[16]));
			}
		}
	}

	public static final String	prettyVersion	= "2.0.0";

	public static final String	version				= "16";

	// P R O C E S S I N G A P P L E T A N D O B J E C T S
	protected PApplet						parent;
	protected PGraphics					pgraphics;

	// E X C E P T I O N H A N D L I N G
	protected int								beginOffScreenDrawingCalls;

	// R E G I S T E R D R A W A N D A N I M A T I O N M E T H O D S
	// Draw
	/** The object to handle the draw event */
	protected Object						drawHandlerObject;
	/** The method in drawHandlerObject to execute */
	protected Method						drawHandlerMethod;
	/** the name of the method to handle the event */
	protected String						drawHandlerMethodName;
	// Animation
	/** The object to handle the animation */
	protected Object						animateHandlerObject;
	/** The method in animateHandlerObject to execute */
	protected Method						animateHandlerMethod;
	/** the name of the method to handle the animation */
	protected String						animateHandlerMethodName;

	protected boolean						javaTiming;

	/**
	 * Constructor that defines an on-screen Processing Scene. Same as {@code this(p, p.g}.
	 * 
	 * @see #Scene(PApplet, PGraphics)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */
	public Scene(PApplet p) {
		this(p, p.g);
	}

	/**
	 * Same as {@code this(p, renderer, 0, 0)}.
	 * 
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */
	public Scene(PApplet p, PGraphics renderer) {
		this(p, renderer, 0, 0);
	}

	/**
	 * Main constructor defining a left-handed Processing compatible Scene. Calls {@link #setMatrixHelper(MatrixHelper)}
	 * using a customized {@link remixlab.dandelion.core.MatrixHelper} depending on the {@code pg} type (see
	 * {@link remixlab.proscene.Scene.P5Java2DMatrixHelper} and {@link remixlab.proscene.Scene.P5GLMatrixHelper}). The
	 * constructor instantiates the {@link #inputHandler()} and the {@link #timingHandler()}, sets the AXIS and GRID
	 * visual hint flags, instantiates the {@link #eye()} (a {@link remixlab.dandelion.core.Camera} if the Scene
	 * {@link #is3D()} or a {@link remixlab.dandelion.core.Window} if the Scene {@link #is2D()}). It also instantiates the
	 * {@link #keyboardAgent()} and the {@link #mouseAgent()}, and finally calls {@link #init()}.
	 * <p>
	 * An off-screen Processing Scene is defined if {@code pg != p.g}. In this case the {@code x} and {@code y} parameters
	 * define the position of the upper-left corner where the off-screen Scene is expected to be displayed, e.g., for
	 * instance with a call to Processing the {@code image(img, x, y)} function. If {@code pg == p.g}) (which defines an
	 * on-screen Scene, see also {@link #isOffscreen()}), the values of x and y are meaningless (both are set to 0 to be
	 * taken as dummy values).
	 * 
	 * @see remixlab.dandelion.core.AbstractScene#AbstractScene()
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics)
	 */
	public Scene(PApplet p, PGraphics pg, int x, int y) {
		// 1. P5 objects
		parent = p;
		pgraphics = pg;

		// 2. Matrix helper
		if (pg instanceof PGraphics3D)
			setMatrixHelper(new P5GLMatrixHelper(this, (PGraphics3D) pg));
		else if (pg instanceof PGraphics2D)
			setMatrixHelper(new P5GLMatrixHelper(this, (PGraphics2D) pg));
		else
			setMatrixHelper(new P5Java2DMatrixHelper(this, pg));

		// 3. Eye
		setLeftHanded();
		width = pg.width;
		height = pg.height;
		if (is3D()) {
			eye = new Camera(this);
			setDottedGrid(true);
		}
		else {
			eye = new Window(this);
			setDottedGrid(false);
		}
		setEye(eye());// calls showAll();

		// 4. Off-screen?
		offscreen = pg != p.g;
		if (offscreen)
			upperLeftCorner = new Point(x, y);
		else
			upperLeftCorner = new Point(0, 0);

		// 5. Create agents and register P5 methods
		defKeyboardAgent = new ProsceneKeyboard(this, "proscene_keyboard");
		enableKeyboardAgent();
		//defMotionAgent = new ProsceneMouse(this, "proscene_mouse");
		//enableMouseAgent();
		defMotionAgent = new ProsceneTouch(this, "proscene_mouse");
		
		pApplet().registerMethod("pre", this);
		pApplet().registerMethod("draw", this);
		// Misc stuff:
		this.setNonSeqTimers();
		// pApplet().frameRate(100);

		// 6. Init should be called only once
		init();
	}

	// firstly, of course, dirty things that I love :P

	/**
	 * Set mouse bindings as arcball. Same as {@code defaultMouseAgent().setAsArcball()}.
	 * 
	 * @see remixlab.dandelion.agent.MouseAgent#setAsArcball()
	 * @see #setMouseAsFirstPerson()
	 * @see #setMouseAsThirdPerson()
	 */
	public void setMouseAsArcball() {
		mouseAgent().setAsArcball();
	}

	/**
	 * Set mouse bindings as first-person. Same as {@code defaultMouseAgent().setAsFirstPerson()}.
	 * 
	 * @see remixlab.dandelion.agent.MouseAgent#setAsFirstPerson()
	 * @see #setMouseAsArcball()
	 * @see #setMouseAsThirdPerson()
	 */
	public void setMouseAsFirstPerson() {
		mouseAgent().setAsFirstPerson();
	}

	/**
	 * Set mouse bindings as third-person. Same as {@code defaultMouseAgent().setAsThirdPerson()}.
	 * 
	 * @see remixlab.dandelion.agent.MouseAgent#setAsThirdPerson()
	 * @see #setMouseAsArcball()
	 * @see #setMouseAsFirstPerson()
	 */
	public void setMouseAsThirdPerson() {
		mouseAgent().setAsThirdPerson();
	}

	/**
	 * Binds the mask-button mouse shortcut to the (DOF2) dandelion action to be performed by the given {@code target}
	 * (EYE or FRAME).
	 */
	public void setMouseButtonBinding(Target target, int mask, int button, DOF2Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? mouseAgent().eyeProfile() : mouseAgent().frameProfile();
		if (profile != null)
			profile.setBinding(p5ButtonModifiersFix(mask, button), button, action);
	}

	/**
	 * Binds the button mouse shortcut to the (DOF2) dandelion action to be performed by the given {@code target} (EYE or
	 * FRAME).
	 */
	public void setMouseButtonBinding(Target target, int button, DOF2Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? mouseAgent().eyeProfile() : mouseAgent().frameProfile();
		profile.setBinding(p5ButtonModifiersFix(button), button, action);
	}

	/**
	 * Removes the mask-button mouse shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeMouseButtonBinding(Target target, int mask, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? mouseAgent().eyeProfile() : mouseAgent().frameProfile();
		if (profile != null)
			profile.removeBinding(p5ButtonModifiersFix(mask, button), button);
	}

	/**
	 * Removes the button mouse shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeMouseButtonBinding(Target target, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? mouseAgent().eyeProfile() : mouseAgent().frameProfile();
		if (profile != null)
			profile.removeBinding(p5ButtonModifiersFix(button), button);
	}

	/**
	 * Returns {@code true} if the mask-button mouse shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isMouseButtonBindingInUse(Target target, int mask, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? mouseAgent().eyeProfile() : mouseAgent().frameProfile();
		return profile.isBindingInUse(p5ButtonModifiersFix(mask, button), button);
	}

	/**
	 * Returns {@code true} if the button mouse shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isMouseButtonBindingInUse(Target target, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? mouseAgent().eyeProfile() : mouseAgent().frameProfile();
		return profile.isBindingInUse(p5ButtonModifiersFix(button), button);
	}

	// wheel here

	/**
	 * Binds the mask-wheel shortcut to the (wheel) dandelion action to be performed by the given {@code target} (EYE or
	 * FRAME).
	 */
	public void setMouseWheelBinding(Target target, int mask, WheelAction action) {
		MotionProfile<WheelAction> profile = target == Target.EYE ? mouseAgent().wheelProfile() : mouseAgent()
				.frameWheelProfile();
		if (profile != null)
			profile.setBinding(mask, B_NOBUTTON, action);
	}

	/**
	 * Binds the wheel to the (wheel) dandelion action to be performed by the given {@code target} (EYE or FRAME).
	 */
	public void setMouseWheelBinding(Target target, WheelAction action) {
		MotionProfile<WheelAction> profile = target == Target.EYE ? mouseAgent().wheelProfile() : mouseAgent()
				.frameWheelProfile();
		if (profile != null)
			profile.setBinding(action);
	}

	/**
	 * Removes the mask-wheel shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeMouseWheelBinding(Target target, int mask) {
		MotionProfile<WheelAction> profile = target == Target.EYE ? mouseAgent().wheelProfile() : mouseAgent()
				.frameWheelProfile();
		if (profile != null)
			profile.removeBinding(mask, B_NOBUTTON);
	}

	/**
	 * Removes the wheel binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeMouseWheelBinding(Target target) {
		MotionProfile<WheelAction> profile = target == Target.EYE ? mouseAgent().wheelProfile() : mouseAgent()
				.frameWheelProfile();
		if (profile != null)
			profile.removeBinding();
	}

	/**
	 * Returns {@code true} if the mask-wheel shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isMouseWheelBindingInUse(Target target, int mask) {
		MotionProfile<WheelAction> profile = target == Target.EYE ? mouseAgent().wheelProfile() : mouseAgent()
				.frameWheelProfile();
		return profile.isBindingInUse(mask, B_NOBUTTON);
	}

	/**
	 * Returns {@code true} if the wheel is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isMouseWheelBindingInUse(Target target) {
		MotionProfile<WheelAction> profile = target == Target.EYE ? mouseAgent().wheelProfile() : mouseAgent()
				.frameWheelProfile();
		return profile.isBindingInUse();
	}

	// mouse click

	/**
	 * Binds the mask-button-ncs (number-of-clicks) click-shortcut to the (click) dandelion action to be performed by the
	 * given {@code target} (EYE or FRAME).
	 */
	public void setMouseClickBinding(Target target, int mask, int button, int ncs, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? mouseAgent().clickProfile() : mouseAgent()
				.frameClickProfile();
		if (profile != null)
			profile.setClickBinding(p5ButtonModifiersFix(mask, button), button, ncs, action);
	}

	/**
	 * Binds the button-ncs (number-of-clicks) click-shortcut to the (click) dandelion action to be performed by the given
	 * {@code target} (EYE or FRAME).
	 */
	public void setMouseClickBinding(Target target, int button, int ncs, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? mouseAgent().clickProfile() : mouseAgent()
				.frameClickProfile();
		if (profile != null)
			profile.setClickBinding(p5ButtonModifiersFix(button), button, ncs, action);
	}

	/**
	 * Binds the single-clicked button shortcut to the (click) dandelion action to be performed by the given
	 * {@code target} (EYE or FRAME).
	 */
	public void setMouseClickBinding(Target target, int button, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? mouseAgent().clickProfile() : mouseAgent()
				.frameClickProfile();
		if (profile != null)
			profile.setClickBinding(p5ButtonModifiersFix(button), button, 1, action);
	}

	/**
	 * Removes the mask-button-ncs (number-of-clicks) click-shortcut binding from the
	 * {@link remixlab.dandelion.core.InteractiveEyeFrame} (if {@code eye} is {@code true}) or from the
	 * {@link remixlab.dandelion.core.InteractiveFrame} (if {@code eye} is {@code false}).
	 */
	public void removeMouseClickBinding(Target target, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? mouseAgent().clickProfile() : mouseAgent()
				.frameClickProfile();
		if (profile != null)
			profile.removeClickBinding(p5ButtonModifiersFix(mask, button), button, ncs);
	}

	/**
	 * Removes the button-ncs (number-of-clicks) click-shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeMouseClickBinding(Target target, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? mouseAgent().clickProfile() : mouseAgent()
				.frameClickProfile();
		if (profile != null)
			profile.removeClickBinding(p5ButtonModifiersFix(button), button, ncs);
	}

	/**
	 * Removes the single-clicked button shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeMouseClickBinding(Target target, int button) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? mouseAgent().clickProfile() : mouseAgent()
				.frameClickProfile();
		if (profile != null)
			profile.removeClickBinding(p5ButtonModifiersFix(button), button, 1);
	}

	/**
	 * Returns {@code true} if the mask-button-ncs (number-of-clicks) click-shortcut is bound to the given {@code target}
	 * (EYE or FRAME).
	 */
	public boolean isMouseClickBindingInUse(Target target, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? mouseAgent().clickProfile() : mouseAgent()
				.frameClickProfile();
		return profile.isClickBindingInUse(p5ButtonModifiersFix(mask, button), button, ncs);
	}

	/**
	 * Returns {@code true} if the button-ncs (number-of-clicks) click-shortcut is bound to the given {@code target} (EYE
	 * or FRAME).
	 */
	public boolean isMouseClickBindingInUse(Target target, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? mouseAgent().clickProfile() : mouseAgent()
				.frameClickProfile();
		return profile.isClickBindingInUse(p5ButtonModifiersFix(button), button, ncs);
	}

	/**
	 * Returns {@code true} if the single-clicked button shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isMouseClickBindingInUse(Target target, int button) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? mouseAgent().clickProfile() : mouseAgent()
				.frameClickProfile();
		return profile.isClickBindingInUse(p5ButtonModifiersFix(button), button, 1);
	}
	
	// touch here
	
	public void surfaceTouchEvent(android.view.MotionEvent event) {
		((ProsceneTouch)defMotionAgent).touchEvent(event);
	}

	// keyboard here

	/**
	 * Restores the default keyboard shortcuts.
	 * 
	 * @see remixlab.dandelion.agent.KeyboardAgent#setDefaultShortcuts()
	 * @see #setMouseAsArcball()
	 * @see #setMouseAsFirstPerson()
	 * @see #setMouseAsThirdPerson()
	 */
	public void setDefaultKeyboardShortcuts() {
		keyboardAgent().setDefaultShortcuts();
	}

	/**
	 * Set the virtual-key to play path. Defaults are java.awt.event.KeyEvent.VK_1, java.awt.event.KeyEvent.VK_2 and
	 * java.awt.event.KeyEvent.VK_3 which will play paths 1, 2, 3, resp.
	 */
	public void setKeyCodeToPlayPath(int code, int path) {
		keyboardAgent().setKeyCodeToPlayPath(code, path);
	}

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	public void setKeyboardShortcut(Character key, KeyboardAction action) {
		keyboardAgent().keyboardProfile().setShortcut(key, action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setKeyboardShortcut(int mask, int vKey, KeyboardAction action) {
		keyboardAgent().keyboardProfile().setShortcut(mask, vKey, action);
	}

	/**
	 * Removes key shortcut binding (if present).
	 */
	public void removeKeyboardShortcut(Character key) {
		keyboardAgent().keyboardProfile().removeShortcut(key);
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeKeyboardShortcut(int mask, int vKey) {
		keyboardAgent().keyboardProfile().removeShortcut(mask, vKey);
	}

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean isKeyboardShortcutInUse(Character key) {
		return keyboardAgent().keyboardProfile().isShortcutInUse(key);
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean isKeyboardShortcutInUse(int mask, int vKey) {
		return keyboardAgent().keyboardProfile().isShortcutInUse(mask, vKey);
	}

	@Override
	public boolean is3D() {
		return (pgraphics instanceof PGraphics3D);
	}

	/**
	 * Enables Proscene keyboard handling through the {@link #keyboardAgent()}.
	 * 
	 * @see #isKeyboardAgentEnabled()
	 * @see #disableKeyboardAgent()
	 * @see #enableMouseAgent()
	 */
	@Override
	public void enableKeyboardAgent() {
		if (!inputHandler().isAgentRegistered(keyboardAgent())) {
			inputHandler().registerAgent(keyboardAgent());
			parent.registerMethod("keyEvent", keyboardAgent());
		}
	}

	/**
	 * Disables the default keyboard agent and returns it.
	 * 
	 * @see #isKeyboardAgentEnabled()
	 * @see #enableKeyboardAgent()
	 * @see #disableMouseAgent()
	 */
	@Override
	public KeyboardAgent disableKeyboardAgent() {
		if (inputHandler().isAgentRegistered(keyboardAgent())) {
			parent.unregisterMethod("keyEvent", keyboardAgent());
			return (KeyboardAgent) inputHandler().unregisterAgent(keyboardAgent());
		}
		return keyboardAgent();
	}

	/**
	 * Returns the default mouse agent handling Processing mouse events.
	 * 
	 * @see #keyboardAgent()
	 */
	public MouseAgent mouseAgent() {
		return (MouseAgent) motionAgent();
	}

	/**
	 * Returns {@code true} if the {@link #mouseAgent()} is enabled and {@code false} otherwise.
	 * 
	 * @see #enableMouseAgent()
	 * @see #disableMouseAgent()
	 * @see #isKeyboardAgentEnabled()
	 */
	public boolean isMouseAgentEnabled() {
		return isMotionAgentEnabled();
	}

	/**
	 * Enables Proscene mouse handling through the {@link #mouseAgent()}.
	 * 
	 * @see #isMouseAgentEnabled()
	 * @see #disableMouseAgent()
	 * @see #enableKeyboardAgent()
	 */
	@Override
	public void enableMotionAgent() {
		if (!inputHandler().isAgentRegistered(motionAgent())) {
			inputHandler().registerAgent(motionAgent());
			parent.registerMethod("mouseEvent", motionAgent());
		}
	}

	public void enableMouseAgent() {
		enableMotionAgent();
	}

	/**
	 * Disables the default mouse agent and returns it.
	 * 
	 * @see #isMouseAgentEnabled()
	 * @see #enableMouseAgent()
	 * @see #enableKeyboardAgent()
	 */
	@Override
	public MouseAgent disableMotionAgent() {
		if (inputHandler().isAgentRegistered(motionAgent())) {
			parent.unregisterMethod("mouseEvent", motionAgent());
			return (MouseAgent) inputHandler().unregisterAgent(motionAgent());
		}
		return mouseAgent();
	}

	public MouseAgent disableMouseAgent() {
		return disableMotionAgent();
	}

	// 2. Associated objects

	@Override
	public void registerTimingTask(TimingTask task) {
		if (areTimersSeq())
			timingHandler().registerTask(task);
		else
			timingHandler().registerTask(task, new NonSeqTimer(this, task));
	}

	/**
	 * Sets all {@link #timingHandler()} timers as (single-threaded) {@link remixlab.fpstiming.SeqTimer}(s).
	 * 
	 * @see #setNonSeqTimers()
	 * @see #switchTimers()
	 * @see #areTimersSeq()
	 */
	public void setSeqTimers() {
		if (areTimersSeq())
			return;

		javaTiming = false;
		timingHandler().restoreTimers();
	}

	/**
	 * Sets all {@link #timingHandler()} timers as (multi-threaded) java.util.Timer(s).
	 * 
	 * @see #setSeqTimers()
	 * @see #switchTimers()
	 * @see #areTimersSeq()
	 */
	public void setNonSeqTimers() {
		if (!areTimersSeq())
			return;

		boolean isActive;

		for (TimingTask task : timingHandler().timerPool()) {
			long period = 0;
			boolean rOnce = false;
			isActive = task.isActive();
			if (isActive) {
				period = task.period();
				rOnce = task.timer().isSingleShot();
			}
			task.stop();
			task.setTimer(new NonSeqTimer(this, task));
			if (isActive) {
				if (rOnce)
					task.runOnce(period);
				else
					task.run(period);
			}
		}

		javaTiming = true;
		PApplet.println("java util timers set");
	}

	/**
	 * @return true, if timing is handling sequentially (i.e., all {@link #timingHandler()} timers are (single-threaded)
	 *         {@link remixlab.fpstiming.SeqTimer}(s)).
	 * 
	 * @see #setSeqTimers()
	 * @see #setNonSeqTimers()
	 * @see #switchTimers()
	 */
	public boolean areTimersSeq() {
		return !javaTiming;
	}

	/**
	 * If {@link #areTimersSeq()} calls {@link #setNonSeqTimers()}, otherwise call {@link #setSeqTimers()}.
	 */
	public void switchTimers() {
		if (areTimersSeq())
			setNonSeqTimers();
		else
			setSeqTimers();
	}

	// 5. Drawing methods

	/**
	 * Paint method which is called just before your {@code PApplet.draw()} method. Simply calls {@link #preDraw()}. This
	 * method is registered at the PApplet and hence you don't need to call it.
	 * <p>
	 * If {@link #isOffscreen()} does nothing.
	 * <p>
	 * If {@link #pg()} is resized then (re)sets the scene {@link #width()} and {@link #height()}, and calls
	 * {@link remixlab.dandelion.core.Eye#setScreenWidthAndHeight(int, int)}.
	 * 
	 * @see #draw()
	 * @see #preDraw()
	 * @see #postDraw()
	 * @see #beginDraw()
	 * @see #endDraw()
	 * @see #isOffscreen()
	 */
	public void pre() {
		if (isOffscreen())
			return;

		if ((width != pg().width) || (height != pg().height)) {
			width = pg().width;
			height = pg().height;
			eye().setScreenWidthAndHeight(width, height);
		}

		preDraw();
	}

	/**
	 * Paint method which is called just after your {@code PApplet.draw()} method. Simply calls {@link #postDraw()}. This
	 * method is registered at the PApplet and hence you don't need to call it.
	 * <p>
	 * If {@link #isOffscreen()} does nothing.
	 * 
	 * @see #pre()
	 * @see #preDraw()
	 * @see #postDraw()
	 * @see #beginDraw()
	 * @see #endDraw()
	 * @see #isOffscreen()
	 */
	public void draw() {
		if (isOffscreen())
			return;
		postDraw();
	}

	/**
	 * Only if the Scene {@link #isOffscreen()}. This method should be called just after the {@link #pg()} beginDraw()
	 * method. Simply calls {@link #preDraw()}.
	 * <p>
	 * If {@link #pg()} is resized then (re)sets the scene {@link #width()} and {@link #height()}, and calls
	 * {@link remixlab.dandelion.core.Eye#setScreenWidthAndHeight(int, int)}.
	 * 
	 * @see #draw()
	 * @see #preDraw()
	 * @see #postDraw()
	 * @see #pre()
	 * @see #endDraw()
	 * @see #isOffscreen()
	 */
	public void beginDraw() {
		if (isOffscreen()) {
			if (beginOffScreenDrawingCalls != 0)
				throw new RuntimeException("There should be exactly one beginDraw() call followed by a "
						+ "endDraw() and they cannot be nested. Check your implementation!");
			beginOffScreenDrawingCalls++;

			if ((width != pg().width) || (height != pg().height)) {
				width = pg().width;
				height = pg().height;
				eye().setScreenWidthAndHeight(width, height);
			}

			preDraw();
		}
	}

	/**
	 * Only if the Scene {@link #isOffscreen()}. This method should be called just before {@link #pg()} endDraw() method.
	 * Simply calls {@link #postDraw()}.
	 * 
	 * @see #draw()
	 * @see #preDraw()
	 * @see #postDraw()
	 * @see #beginDraw()
	 * @see #pre()
	 * @see #isOffscreen()
	 */
	public void endDraw() {
		beginOffScreenDrawingCalls--;

		if (beginOffScreenDrawingCalls != 0)
			throw new RuntimeException(
					"There should be exactly one beginDraw() call followed by a "
							+ "endDraw() and they cannot be nested. Check your implementation!");

		postDraw();
	}

	/**
	 * Returns the PGraphics instance this Scene is related to. It may be the PApplets one, if the Scene is on-screen or
	 * an user-defined if the Scene {@link #isOffscreen()}.
	 */
	public PGraphics pg() {
		return pgraphics;
	}

	@Override
	public void disableDepthTest() {
		pg().hint(PApplet.DISABLE_DEPTH_TEST);
	}

	@Override
	public void enableDepthTest() {
		pg().hint(PApplet.ENABLE_DEPTH_TEST);
	}

	@Override
	public int width() {
		return pg().width;
	}

	@Override
	public int height() {
		return pg().height;
	}

	// 8. Keyboard customization

	@Override
	public void displayInfo(boolean onConsole) {
		if (onConsole)
			System.out.println(info());
		else { // on applet
			pg().textFont(parent.createFont("Arial", 12));
			beginScreenDrawing();
			pg().fill(0, 255, 0);
			pg().textLeading(20);
			pg().text(info(), 10, 10, (pg().width - 20), (pg().height - 20));
			endScreenDrawing();
		}
	}

	/**
	 * Returns the PApplet instance this Scene is related to.
	 */
	public PApplet pApplet() {
		return parent;
	}

	// 10. Draw method registration

	@Override
	protected boolean invokeDrawHandler() {
		// 3. Draw external registered method
		if (drawHandlerObject != null) {
			try {
				drawHandlerMethod.invoke(drawHandlerObject, new Object[] { this });
				return true;
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + drawHandlerMethodName + " method");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * Attempt to add a 'draw' handler method to the Scene. The default event handler is a method that returns void and
	 * has one single Scene parameter.
	 * 
	 * @param obj
	 *          the object to handle the event
	 * @param methodName
	 *          the method to execute in the object handler class
	 * 
	 * @see #removeDrawHandler()
	 * @see #invokeDrawHandler()
	 */
	public void addDrawHandler(Object obj, String methodName) {
		try {
			drawHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { Scene.class });
			drawHandlerObject = obj;
			drawHandlerMethodName = methodName;
		} catch (Exception e) {
			PApplet.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters the 'draw' handler method (if any has previously been added to the Scene).
	 * 
	 * @see #addDrawHandler(Object, String)
	 * @see #invokeDrawHandler()
	 */
	public void removeDrawHandler() {
		drawHandlerMethod = null;
		drawHandlerObject = null;
		drawHandlerMethodName = null;
	}

	/**
	 * Returns {@code true} if the user has registered a 'draw' handler method to the Scene and {@code false} otherwise.
	 * 
	 * @see #addDrawHandler(Object, String)
	 * @see #invokeDrawHandler()
	 */
	public boolean hasDrawHandler() {
		if (drawHandlerMethodName == null)
			return false;
		return true;
	}

	// 11. Animation

	@Override
	public boolean invokeAnimationHandler() {
		if (animateHandlerObject != null) {
			try {
				animateHandlerMethod.invoke(animateHandlerObject, new Object[] { this });
				return true;
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + animateHandlerMethodName + " method");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * Attempt to add an 'animation' handler method to the Scene. The default event handler is a method that returns void
	 * and has one single Scene parameter.
	 * 
	 * @param obj
	 *          the object to handle the event
	 * @param methodName
	 *          the method to execute in the object handler class
	 * 
	 * @see #animate()
	 * @see #removeAnimationHandler()
	 */
	public void addAnimationHandler(Object obj, String methodName) {
		try {
			animateHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { Scene.class });
			animateHandlerObject = obj;
			animateHandlerMethodName = methodName;
		} catch (Exception e) {
			PApplet.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters the 'animation' handler method (if any has previously been added to the Scene).
	 * 
	 * @see #addAnimationHandler(Object, String)
	 */
	public void removeAnimationHandler() {
		animateHandlerMethod = null;
		animateHandlerObject = null;
		animateHandlerMethodName = null;
	}

	/**
	 * Returns {@code true} if the user has registered an 'animation' handler method to the Scene and {@code false}
	 * otherwise.
	 * 
	 * @see #addAnimationHandler(Object, String)
	 * @see #removeAnimationHandler()
	 */
	public boolean hasAnimationHandler() {
		if (animateHandlerMethodName == null)
			return false;
		return true;
	}

	@Override
	protected Camera.WorldPoint pointUnderPixel(Point pixel) {
		PGraphicsOpenGL pggl;
		if (pg() instanceof PGraphicsOpenGL)
			pggl = (PGraphicsOpenGL) pg();
		else
			throw new RuntimeException("pg() is not instance of PGraphicsOpenGL");
		float[] depth = new float[1];
		PGL pgl = pggl.beginPGL();
		pgl.readPixels(pixel.x(), (camera().screenHeight() - pixel.y()), 1, 1, PGL.DEPTH_COMPONENT, PGL.FLOAT,
				FloatBuffer.wrap(depth));
		pggl.endPGL();
		Vec point = new Vec(pixel.x(), pixel.y(), depth[0]);
		point = camera().unprojectedCoordinatesOf(point);
		return camera().new WorldPoint(point, (depth[0] < 1.0f));
	}

	// implementation of abstract drawing methods

	@Override
	public void drawCylinder(float w, float h) {
		if (is2D()) {
			AbstractScene.showDepthWarning("drawCylinder");
			return;
		}

		pg().pushStyle();
		float px, py;

		pg().beginShape(PApplet.QUAD_STRIP);
		for (float i = 0; i < 13; i++) {
			px = (float) Math.cos(PApplet.radians(i * 30)) * w;
			py = (float) Math.sin(PApplet.radians(i * 30)) * w;
			vertex(px, py, 0);
			vertex(px, py, h);
		}
		pg().endShape();

		pg().beginShape(PApplet.TRIANGLE_FAN);
		vertex(0, 0, 0);
		for (float i = 12; i > -1; i--) {
			px = (float) Math.cos(PApplet.radians(i * 30)) * w;
			py = (float) Math.sin(PApplet.radians(i * 30)) * w;
			vertex(px, py, 0);
		}
		pg().endShape();

		pg().beginShape(PApplet.TRIANGLE_FAN);
		vertex(0, 0, h);
		for (float i = 0; i < 13; i++) {
			px = (float) Math.cos(PApplet.radians(i * 30)) * w;
			py = (float) Math.sin(PApplet.radians(i * 30)) * w;
			vertex(px, py, h);
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawHollowCylinder(int detail, float w, float h, Vec m, Vec n) {
		if (is2D()) {
			AbstractScene.showDepthWarning("drawCylinder");
			return;
		}

		pg().pushStyle();
		// eqs taken from: http://en.wikipedia.org/wiki/Line-plane_intersection
		Vec pm0 = new Vec(0, 0, 0);
		Vec pn0 = new Vec(0, 0, h);
		Vec l0 = new Vec();
		Vec l = new Vec(0, 0, 1);
		Vec p = new Vec();
		float x, y, d;

		pg().noStroke();
		pg().beginShape(PApplet.QUAD_STRIP);

		for (float t = 0; t <= detail; t++) {
			x = w * PApplet.cos(t * PApplet.TWO_PI / detail);
			y = w * PApplet.sin(t * PApplet.TWO_PI / detail);
			l0.set(x, y, 0);

			d = (m.dot(Vec.subtract(pm0, l0))) / (l.dot(m));
			p = Vec.add(Vec.multiply(l, d), l0);
			vertex(p.x(), p.y(), p.z());

			l0.setZ(h);
			d = (n.dot(Vec.subtract(pn0, l0))) / (l.dot(n));
			p = Vec.add(Vec.multiply(l, d), l0);
			vertex(p.x(), p.y(), p.z());
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawCone(int detail, float x, float y, float r, float h) {
		if (is2D()) {
			AbstractScene.showDepthWarning("drawCylinder");
			return;
		}
		pg().pushStyle();
		float unitConeX[] = new float[detail + 1];
		float unitConeY[] = new float[detail + 1];

		for (int i = 0; i <= detail; i++) {
			float a1 = PApplet.TWO_PI * i / detail;
			unitConeX[i] = r * (float) Math.cos(a1);
			unitConeY[i] = r * (float) Math.sin(a1);
		}

		pushModelView();
		translate(x, y);
		pg().beginShape(PApplet.TRIANGLE_FAN);
		vertex(0, 0, h);
		for (int i = 0; i <= detail; i++) {
			vertex(unitConeX[i], unitConeY[i], 0.0f);
		}
		pg().endShape();
		popModelView();
		pg().popStyle();
	}

	@Override
	public void drawCone(int detail, float x, float y, float r1, float r2, float h) {
		if (is2D()) {
			AbstractScene.showDepthWarning("drawCylinder");
			return;
		}
		pg().pushStyle();
		float firstCircleX[] = new float[detail + 1];
		float firstCircleY[] = new float[detail + 1];
		float secondCircleX[] = new float[detail + 1];
		float secondCircleY[] = new float[detail + 1];

		for (int i = 0; i <= detail; i++) {
			float a1 = PApplet.TWO_PI * i / detail;
			firstCircleX[i] = r1 * (float) Math.cos(a1);
			firstCircleY[i] = r1 * (float) Math.sin(a1);
			secondCircleX[i] = r2 * (float) Math.cos(a1);
			secondCircleY[i] = r2 * (float) Math.sin(a1);
		}

		pushModelView();
		translate(x, y);
		pg().beginShape(PApplet.QUAD_STRIP);
		for (int i = 0; i <= detail; i++) {
			vertex(firstCircleX[i], firstCircleY[i], 0);
			vertex(secondCircleX[i], secondCircleY[i], h);
		}
		pg().endShape();
		popModelView();
		pg().popStyle();
	}

	@Override
	public void drawAxes(float length) {
		pg().pushStyle();
		pg().colorMode(PApplet.RGB, 255);
		final float charWidth = length / 40.0f;
		final float charHeight = length / 30.0f;
		final float charShift = 1.04f * length;

		pg().beginShape(PApplet.LINES);

		if (is2D()) {
			// The X
			pg().stroke(200, 0, 0);
			vertex(charShift + charWidth, -charHeight);
			vertex(charShift - charWidth, charHeight);
			vertex(charShift - charWidth, -charHeight);
			vertex(charShift + charWidth, charHeight);

			// The Y
			pg().stroke(0, 200, 0);
			vertex(charWidth, charShift + charHeight);
			vertex(0.0f, charShift + 0.0f);
			vertex(-charWidth, charShift + charHeight);
			vertex(0.0f, charShift + 0.0f);
			vertex(0.0f, charShift + 0.0f);
			vertex(0.0f, charShift + -charHeight);
		}
		else {
			// The X
			pg().stroke(200, 0, 0);
			vertex(charShift, charWidth, -charHeight);
			vertex(charShift, -charWidth, charHeight);
			vertex(charShift, -charWidth, -charHeight);
			vertex(charShift, charWidth, charHeight);
			// The Y
			pg().stroke(0, 200, 0);
			vertex(charWidth, charShift, charHeight);
			vertex(0.0f, charShift, 0.0f);
			vertex(-charWidth, charShift, charHeight);
			vertex(0.0f, charShift, 0.0f);
			vertex(0.0f, charShift, 0.0f);
			vertex(0.0f, charShift, -charHeight);
			// The Z
			pg().stroke(0, 100, 200);
			// left_handed
			if (isLeftHanded()) {
				vertex(-charWidth, -charHeight, charShift);
				vertex(charWidth, -charHeight, charShift);
				vertex(charWidth, -charHeight, charShift);
				vertex(-charWidth, charHeight, charShift);
				vertex(-charWidth, charHeight, charShift);
				vertex(charWidth, charHeight, charShift);
			}
			else {
				vertex(-charWidth, charHeight, charShift);
				vertex(charWidth, charHeight, charShift);
				vertex(charWidth, charHeight, charShift);
				vertex(-charWidth, -charHeight, charShift);
				vertex(-charWidth, -charHeight, charShift);
				vertex(charWidth, -charHeight, charShift);
			}
		}

		pg().endShape();

		// X Axis
		pg().stroke(200, 0, 0);
		line(0, 0, 0, length, 0, 0);
		// Y Axis
		pg().stroke(0, 200, 0);
		line(0, 0, 0, 0, length, 0);

		// Z Axis
		if (is3D()) {
			pg().stroke(0, 100, 200);
			line(0, 0, 0, 0, 0, length);
		}
		pg().popStyle();
	}

	@Override
	public void drawGrid(float size, int nbSubdivisions) {
		pg().pushStyle();
		pg().beginShape(LINES);
		for (int i = 0; i <= nbSubdivisions; ++i) {
			final float pos = size * (2.0f * i / nbSubdivisions - 1.0f);
			vertex(pos, -size);
			vertex(pos, +size);
			vertex(-size, pos);
			vertex(size, pos);
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawDottedGrid(float size, int nbSubdivisions) {
		pg().pushStyle();
		float posi, posj;
		pg().beginShape(POINTS);
		for (int i = 0; i <= nbSubdivisions; ++i) {
			posi = size * (2.0f * i / nbSubdivisions - 1.0f);
			for (int j = 0; j <= nbSubdivisions; ++j) {
				posj = size * (2.0f * j / nbSubdivisions - 1.0f);
				vertex(posi, posj);
			}
		}
		pg().endShape();
		int internalSub = 5;
		int subSubdivisions = nbSubdivisions * internalSub;
		float currentWeight = pg().strokeWeight;
		pg().colorMode(HSB, 255);
		float hue = pg().hue(pg().strokeColor);
		float saturation = pg().saturation(pg().strokeColor);
		float brightness = pg().brightness(pg().strokeColor);
		pg().stroke(hue, saturation, brightness * 10f / 17f);
		pg().strokeWeight(currentWeight / 2);
		pg().beginShape(POINTS);
		for (int i = 0; i <= subSubdivisions; ++i) {
			posi = size * (2.0f * i / subSubdivisions - 1.0f);
			for (int j = 0; j <= subSubdivisions; ++j) {
				posj = size * (2.0f * j / subSubdivisions - 1.0f);
				if (((i % internalSub) != 0) || ((j % internalSub) != 0))
					vertex(posi, posj);
			}
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawEye(Eye eye, float scale) {
		pg().pushStyle();
		// boolean drawFarPlane = true;
		// int farIndex = drawFarPlane ? 1 : 0;
		int farIndex = is3D() ? 1 : 0;
		boolean ortho = false;
		if (is3D())
			if (((Camera) eye).type() == Camera.Type.ORTHOGRAPHIC)
				ortho = true;
		pushModelView();
		// applyMatrix(camera.frame().worldMatrix());
		// same as the previous line, but maybe more efficient

		// Frame tmpFrame = new Frame(is3D());
		// tmpFrame.fromMatrix(eye.frame().worldMatrix());
		// applyTransformation(tmpFrame);
		// same as above but easier
		// scene().applyTransformation(camera.frame());

		// fails due to scaling!

		// take into account the whole hierarchy:
		if (is2D()) {
			// applyWorldTransformation(eye.frame());
			translate(eye.frame().position().vec[0], eye.frame().position().vec[1]);
			rotate(eye.frame().orientation().angle());
		} else {
			translate(eye.frame().position().vec[0], eye.frame().position().vec[1], eye.frame().position().vec[2]);
			rotate(eye.frame().orientation().angle(), ((Quat) eye.frame().orientation()).axis().vec[0], ((Quat) eye.frame()
					.orientation()).axis().vec[1], ((Quat) eye.frame().orientation()).axis().vec[2]);
		}

		// 0 is the upper left coordinates of the near corner, 1 for the far one
		Vec[] points = new Vec[2];
		points[0] = new Vec();
		points[1] = new Vec();

		if (is2D() || ortho) {
			float[] wh = eye.getBoundaryWidthHeight();
			points[0].setX(scale * wh[0]);
			points[1].setX(scale * wh[0]);
			points[0].setY(scale * wh[1]);
			points[1].setY(scale * wh[1]);
		}

		if (is3D()) {
			points[0].setZ(scale * ((Camera) eye).zNear());
			points[1].setZ(scale * ((Camera) eye).zFar());

			if (((Camera) eye).type() == Camera.Type.PERSPECTIVE) {
				points[0].setY(points[0].z() * PApplet.tan(((Camera) eye).fieldOfView() / 2.0f));
				points[0].setX(points[0].y() * ((Camera) eye).aspectRatio());
				float ratio = points[1].z() / points[0].z();
				points[1].setY(ratio * points[0].y());
				points[1].setX(ratio * points[0].x());
			}

			// Frustum lines
			switch (((Camera) eye).type()) {
			case PERSPECTIVE: {
				pg().beginShape(PApplet.LINES);
				vertex(0.0f, 0.0f, 0.0f);
				vertex(points[farIndex].x(), points[farIndex].y(), -points[farIndex].z());
				vertex(0.0f, 0.0f, 0.0f);
				vertex(-points[farIndex].x(), points[farIndex].y(), -points[farIndex].z());
				vertex(0.0f, 0.0f, 0.0f);
				vertex(-points[farIndex].x(), -points[farIndex].y(), -points[farIndex].z());
				vertex(0.0f, 0.0f, 0.0f);
				vertex(points[farIndex].x(), -points[farIndex].y(), -points[farIndex].z());
				pg().endShape();
				break;
			}
			case ORTHOGRAPHIC: {
				// if (drawFarPlane) {
				pg().beginShape(PApplet.LINES);
				vertex(points[0].x(), points[0].y(), -points[0].z());
				vertex(points[1].x(), points[1].y(), -points[1].z());
				vertex(-points[0].x(), points[0].y(), -points[0].z());
				vertex(-points[1].x(), points[1].y(), -points[1].z());
				vertex(-points[0].x(), -points[0].y(), -points[0].z());
				vertex(-points[1].x(), -points[1].y(), -points[1].z());
				vertex(points[0].x(), -points[0].y(), -points[0].z());
				vertex(points[1].x(), -points[1].y(), -points[1].z());
				pg().endShape();
				// }
				break;
			}
			}
		}

		// Near and (optionally) far plane(s)
		pg().noStroke();
		pg().beginShape(PApplet.QUADS);
		for (int i = farIndex; i >= 0; --i) {
			pg().normal(0.0f, 0.0f, (i == 0) ? 1.0f : -1.0f);
			vertex(points[i].x(), points[i].y(), -points[i].z());
			vertex(-points[i].x(), points[i].y(), -points[i].z());
			vertex(-points[i].x(), -points[i].y(), -points[i].z());
			vertex(points[i].x(), -points[i].y(), -points[i].z());
		}
		pg().endShape();

		// Up arrow
		float arrowHeight = 1.5f * points[0].y();
		float baseHeight = 1.2f * points[0].y();
		float arrowHalfWidth = 0.5f * points[0].x();
		float baseHalfWidth = 0.3f * points[0].x();

		// pg3d().noStroke();
		// Arrow base
		pg().beginShape(PApplet.QUADS);
		if (isLeftHanded()) {
			vertex(-baseHalfWidth, -points[0].y(), -points[0].z());
			vertex(baseHalfWidth, -points[0].y(), -points[0].z());
			vertex(baseHalfWidth, -baseHeight, -points[0].z());
			vertex(-baseHalfWidth, -baseHeight, -points[0].z());
		} else {
			vertex(-baseHalfWidth, points[0].y(), -points[0].z());
			vertex(baseHalfWidth, points[0].y(), -points[0].z());
			vertex(baseHalfWidth, baseHeight, -points[0].z());
			vertex(-baseHalfWidth, baseHeight, -points[0].z());
		}
		pg().endShape();

		// Arrow
		pg().beginShape(PApplet.TRIANGLES);
		if (isLeftHanded()) {
			vertex(0.0f, -arrowHeight, -points[0].z());
			vertex(-arrowHalfWidth, -baseHeight, -points[0].z());
			vertex(arrowHalfWidth, -baseHeight, -points[0].z());
		} else {
			vertex(0.0f, arrowHeight, -points[0].z());
			vertex(-arrowHalfWidth, baseHeight, -points[0].z());
			vertex(arrowHalfWidth, baseHeight, -points[0].z());
		}
		pg().endShape();
		popModelView();
		pg().popStyle();
	}

	@Override
	public void drawPath(KeyFrameInterpolator kfi, int mask, int nbFrames, float scale) {
		pg().pushStyle();
		if (mask != 0) {
			int nbSteps = 30;
			pg().strokeWeight(2 * pg().strokeWeight);
			pg().noFill();

			List<Frame> path = kfi.path();
			if (((mask & 1) != 0) && path.size() > 1) {
				pg().beginShape();
				for (Frame myFr : path)
					vertex(myFr.position().x(), myFr.position().y(), myFr.position().z());
				pg().endShape();
			}
			if ((mask & 6) != 0) {
				int count = 0;
				if (nbFrames > nbSteps)
					nbFrames = nbSteps;
				float goal = 0.0f;

				for (Frame myFr : path)
					if ((count++) >= goal) {
						goal += nbSteps / (float) nbFrames;
						pushModelView();

						applyTransformation(myFr);

						if ((mask & 2) != 0)
							drawKFIEye(scale);
						if ((mask & 4) != 0)
							drawAxes(scale / 10.0f);

						popModelView();
					}
			}
			kfi.addFramesToAllAgentPools();
			pg().strokeWeight(pg().strokeWeight / 2f);
			drawPickingTargets(true);
		}
		pg().popStyle();
	}

	@Override
	protected void drawKFIEye(float scale) {
		pg().pushStyle();
		float halfHeight = scale * (is2D() ? 1.2f : 0.07f);
		float halfWidth = halfHeight * 1.3f;
		float dist = halfHeight / (float) Math.tan(PApplet.PI / 8.0f);

		float arrowHeight = 1.5f * halfHeight;
		float baseHeight = 1.2f * halfHeight;
		float arrowHalfWidth = 0.5f * halfWidth;
		float baseHalfWidth = 0.3f * halfWidth;

		// Frustum outline
		pg().noFill();
		pg().beginShape();
		vertex(-halfWidth, halfHeight, -dist);
		vertex(-halfWidth, -halfHeight, -dist);
		vertex(0.0f, 0.0f, 0.0f);
		vertex(halfWidth, -halfHeight, -dist);
		vertex(-halfWidth, -halfHeight, -dist);
		pg().endShape();
		pg().noFill();
		pg().beginShape();
		vertex(halfWidth, -halfHeight, -dist);
		vertex(halfWidth, halfHeight, -dist);
		vertex(0.0f, 0.0f, 0.0f);
		vertex(-halfWidth, halfHeight, -dist);
		vertex(halfWidth, halfHeight, -dist);
		pg().endShape();

		// Up arrow
		pg().noStroke();
		pg().fill(pg().strokeColor);
		// Base
		pg().beginShape(PApplet.QUADS);

		if (isLeftHanded()) {
			vertex(baseHalfWidth, -halfHeight, -dist);
			vertex(-baseHalfWidth, -halfHeight, -dist);
			vertex(-baseHalfWidth, -baseHeight, -dist);
			vertex(baseHalfWidth, -baseHeight, -dist);
		}
		else {
			vertex(-baseHalfWidth, halfHeight, -dist);
			vertex(baseHalfWidth, halfHeight, -dist);
			vertex(baseHalfWidth, baseHeight, -dist);
			vertex(-baseHalfWidth, baseHeight, -dist);
		}

		pg().endShape();
		// Arrow
		pg().beginShape(PApplet.TRIANGLES);

		if (isLeftHanded()) {
			vertex(0.0f, -arrowHeight, -dist);
			vertex(arrowHalfWidth, -baseHeight, -dist);
			vertex(-arrowHalfWidth, -baseHeight, -dist);
		}
		else {
			vertex(0.0f, arrowHeight, -dist);
			vertex(-arrowHalfWidth, baseHeight, -dist);
			vertex(arrowHalfWidth, baseHeight, -dist);
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawCross(float px, float py, float size) {
		pg().pushStyle();
		beginScreenDrawing();
		pg().noFill();
		pg().beginShape(LINES);
		vertex(px - size, py);
		vertex(px + size, py);
		vertex(px, py - size);
		vertex(px, py + size);
		pg().endShape();
		endScreenDrawing();
		pg().popStyle();
	}

	@Override
	public void drawFilledCircle(int subdivisions, Vec center, float radius) {
		pg().pushStyle();
		float precision = PApplet.TWO_PI / subdivisions;
		float x = center.x();
		float y = center.y();
		float angle, x2, y2;
		beginScreenDrawing();
		pg().noStroke();
		pg().beginShape(TRIANGLE_FAN);
		vertex(x, y);
		for (angle = 0.0f; angle <= PApplet.TWO_PI + 1.1 * precision; angle += precision) {
			x2 = x + PApplet.sin(angle) * radius;
			y2 = y + PApplet.cos(angle) * radius;
			vertex(x2, y2);
		}
		pg().endShape();
		endScreenDrawing();
		pg().popStyle();
	}

	@Override
	public void drawFilledSquare(Vec center, float edge) {
		pg().pushStyle();
		float x = center.x();
		float y = center.y();
		beginScreenDrawing();
		pg().noStroke();
		pg().beginShape(QUADS);
		vertex(x - edge, y + edge);
		vertex(x + edge, y + edge);
		vertex(x + edge, y - edge);
		vertex(x - edge, y - edge);
		pg().endShape();
		endScreenDrawing();
		pg().popStyle();
	}

	@Override
	public void drawShooterTarget(Vec center, float length) {
		pg().pushStyle();
		float x = center.x();
		float y = center.y();
		beginScreenDrawing();
		pg().noFill();

		pg().beginShape();
		vertex((x - length), (y - length) + (0.6f * length));
		vertex((x - length), (y - length));
		vertex((x - length) + (0.6f * length), (y - length));
		pg().endShape();

		pg().beginShape();
		vertex((x + length) - (0.6f * length), (y - length));
		vertex((x + length), (y - length));
		vertex((x + length), ((y - length) + (0.6f * length)));
		pg().endShape();

		pg().beginShape();
		vertex((x + length), ((y + length) - (0.6f * length)));
		vertex((x + length), (y + length));
		vertex(((x + length) - (0.6f * length)), (y + length));
		pg().endShape();

		pg().beginShape();
		vertex((x - length) + (0.6f * length), (y + length));
		vertex((x - length), (y + length));
		vertex((x - length), ((y + length) - (0.6f * length)));
		pg().endShape();
		endScreenDrawing();
		drawCross(center.x(), center.y(), 0.6f * length);
		pg().popStyle();
	}

	@Override
	public void drawPickingTargets(boolean keyFrame) {
		pg().pushStyle();
		for (Grabber mg : inputHandler().globalGrabberList()) {
			if (mg instanceof InteractiveFrame) {
				InteractiveFrame iF = (InteractiveFrame) mg;// downcast needed
				// frames
				if (!(iF.isInEyePath() ^ keyFrame)) {
					Vec center = projectedCoordinatesOf(iF.position());
					if (grabsAnyAgentInput(mg)) {
						pg().pushStyle();
						pg().strokeWeight(2 * pg().strokeWeight);
						pg().colorMode(HSB, 255);
						float hue = pg().hue(pg().strokeColor);
						float saturation = pg().saturation(pg().strokeColor);
						float brightness = pg().brightness(pg().strokeColor);
						pg().stroke(hue, saturation * 1.4f, brightness * 1.4f);
						drawShooterTarget(center, (iF.grabsInputThreshold() + 1));
						pg().popStyle();
					}
					else {
						pg().pushStyle();
						pg().colorMode(HSB, 255);
						float hue = pg().hue(pg().strokeColor);
						float saturation = pg().saturation(pg().strokeColor);
						float brightness = pg().brightness(pg().strokeColor);
						pg().stroke(hue, saturation * 1.4f, brightness);
						drawShooterTarget(center, iF.grabsInputThreshold());
						pg().popStyle();
					}
				}
			}
		}
		pg().popStyle();
	}

	/**
	 * Code contributed by Jacques Maire (http://www.alcys.com/) See also:
	 * http://www.mathcurve.com/courbes3d/solenoidtoric/solenoidtoric.shtml
	 * http://crazybiocomputing.blogspot.fr/2011/12/3d-curves-toric-solenoids.html
	 */
	@Override
	public void drawTorusSolenoid(int faces, int detail, float insideRadius, float outsideRadius) {
		pg().pushStyle();
		pg().noStroke();
		Vec v1, v2;
		int b, ii, jj, a;
		float eps = PApplet.TWO_PI / detail;
		for (a = 0; a < faces; a += 2) {
			pg().beginShape(PApplet.TRIANGLE_STRIP);
			b = (a <= (faces - 1)) ? a + 1 : 0;
			for (int i = 0; i < (detail + 1); i++) {
				ii = (i < detail) ? i : 0;
				jj = ii + 1;
				float ai = eps * jj;
				float alpha = a * PApplet.TWO_PI / faces + ai;
				v1 = new Vec((outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.cos(ai),
						(outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.sin(ai), insideRadius
								* PApplet.sin(alpha));
				alpha = b * PApplet.TWO_PI / faces + ai;
				v2 = new Vec((outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.cos(ai),
						(outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.sin(ai), insideRadius
								* PApplet.sin(alpha));
				vertex(v1.x(), v1.y(), v1.z());
				vertex(v2.x(), v2.y(), v2.z());
			}
			pg().endShape();
		}
		pg().popStyle();
	}

	/*
	 * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
	 */
	@Override
	protected void drawAxesHint() {
		pg().pushStyle();
		pg().strokeWeight(2);
		drawAxes(eye().sceneRadius());
		pg().popStyle();
	}

	/*
	 * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
	 */
	@Override
	protected void drawGridHint() {
		pg().pushStyle();
		pg().stroke(170);
		if (gridIsDotted()) {
			pg().strokeWeight(2);
			drawDottedGrid(eye().sceneRadius());
		}
		else {
			pg().strokeWeight(1);
			drawGrid(eye().sceneRadius());
		}
		pg().popStyle();
	}

	/*
	 * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
	 */
	@Override
	protected void drawPathsHint() {
		pg().pushStyle();
		pg().colorMode(PApplet.RGB, 255);
		pg().strokeWeight(1);
		pg().stroke(0, 220, 220);
		drawEyePaths();
		pg().popStyle();
	}

	@Override
	/*
	 * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
	 */
	protected void drawPickingHint() {
		pg().pushStyle();
		pg().colorMode(PApplet.RGB, 255);
		pg().strokeWeight(1);
		pg().stroke(220, 220, 220);
		drawPickingTargets();
		pg().popStyle();
	}

	@Override
	protected void drawAnchorHint() {
		pg().pushStyle();
		Vec p = eye().projectedCoordinatesOf(anchor());
		pg().stroke(255);
		pg().strokeWeight(3);
		drawCross(p.vec[0], p.vec[1]);
		pg().popStyle();
	}

	@Override
	protected void drawPointUnderPixelHint() {
		pg().pushStyle();
		Vec v = eye().projectedCoordinatesOf(eye().frame().pupVec);
		pg().stroke(255);
		pg().strokeWeight(3);
		drawCross(v.vec[0], v.vec[1], 15);
		pg().popStyle();
	}

	@Override
	protected void drawScreenRotateHint() {
		pg().pushStyle();
		if (!(mouseAgent() instanceof ProsceneMouse))
			return;
		float p1x = (float) ((ProsceneMouse) mouseAgent()).lCorner.x();
		float p1y = (float) ((ProsceneMouse) mouseAgent()).lCorner.y();
		Vec p2 = eye().projectedCoordinatesOf(anchor());
		beginScreenDrawing();
		pg().stroke(255, 255, 255);
		pg().strokeWeight(2);
		pg().noFill();
		line(p2.x(), p2.y(), p1x, p1y);
		endScreenDrawing();
		pg().popStyle();
	}

	@Override
	protected void drawZoomWindowHint() {
		if (!(mouseAgent() instanceof ProsceneMouse))
			return;
		pg().pushStyle();
		float p1x = (float) ((ProsceneMouse) mouseAgent()).fCorner.x();
		float p1y = (float) ((ProsceneMouse) mouseAgent()).fCorner.y();
		float p2x = (float) ((ProsceneMouse) mouseAgent()).lCorner.x();
		float p2y = (float) ((ProsceneMouse) mouseAgent()).lCorner.y();
		beginScreenDrawing();
		pg().stroke(255, 255, 255);
		pg().strokeWeight(2);
		pg().noFill();
		pg().beginShape();
		vertex(p1x, p1y);
		vertex(p2x, p1y);
		vertex(p2x, p2y);
		vertex(p1x, p2y);
		pg().endShape(CLOSE);
		endScreenDrawing();
		pg().popStyle();
	}
}
