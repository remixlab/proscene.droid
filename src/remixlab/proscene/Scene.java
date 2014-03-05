/**************************************************************************************
 * ProScene (version 2.0.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *************************************************************************************/
package remixlab.proscene;

import processing.core.*;
import processing.opengl.*;
import remixlab.bias.core.*;
import remixlab.bias.generic.event.*;
import remixlab.bias.generic.profile.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.helper.*;
import remixlab.fpstiming.*;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A 2D or 3D interactive Processing scene.
 * <p>
 * A Scene has a full reach Eye, it can be used for on-screen or off-screen rendering purposes (see the different
 * constructors).
 * <h3>Usage</h3>
 * To use a Scene you have three choices:
 * <ol>
 * <li><b>Direct instantiation</b>. In this case you should instantiate your own Scene object at the
 * {@code PApplet.setup()} function. See the example <i>BasicUse</i>.
 * <li><b>Inheritance</b>. In this case, once you declare a Scene derived class, you should implement
 * {@link #proscenium()} which defines the objects in your scene. Just make sure to define the {@code PApplet.draw()}
 * method, even if it's empty. See the example <i>AlternativeUse</i>.
 * <li><b>External draw handler registration</b>. You can even declare an external drawing method and then register it
 * at the Scene with {@link #addDrawHandler(Object, String)}. That method should return {@code void} and have one single
 * {@code Scene} parameter. This strategy may be useful when there are multiple viewers sharing the same drawing code.
 * See the example <i>StandardCamera</i>.
 * </ol>
 * <h3>Interactivity mechanisms</h3>
 * Thanks to its event back-end, proscene provides powerful interactivity mechanisms allowing a wide range of scene
 * setups ranging from very simple to complex ones. For convenience, two interaction mechanisms are provided by default:
 * {@link #defaultKeyboardAgent()}, and {@link #defaultMouseAgent()}.
 * <ol>
 * <li><b>Default keyboard agent</b> provides global configuration options such as {@link #drawGrid()} or
 * {@link #drawAxis()} that are common among the different registered eye profiles. To define a keyboard shortcut
 * retrieve the agent's {@link remixlab.dandelion.agent.KeyboardAgent#keyboardProfile()} and call one of the provided
 * {@code setShortcut()} convenience methods.
 * <li><b>Default mouse agent</b> provides high-level methods to manage camera and frame motion actions. To configure
 * the mouse retrieve one of the mouse agent's profiles (such as
 * {@link remixlab.dandelion.agent.MouseAgent#eyeProfile()} or
 * {@link remixlab.dandelion.agent.MouseAgent#frameProfile()}) and then call one of the provided {@code setBinding()}
 * convenience methods.
 * </ol>
 * <h3>Animation mechanisms</h3>
 * Proscene provides three animation mechanisms to define how your scene evolves over time:
 * <ol>
 * <li><b>Overriding the {@link #animate()} method.</b> In this case, once you declare a Scene derived class, you should
 * implement {@link #animate()} which defines how your scene objects evolve over time. See the example <i>Animation</i>.
 * <li><b>External animation handler registration.</b> You can also declare an external animation method and then
 * register it at the Scene with {@link #addAnimationHandler(Object, String)}. That method should return {@code void}
 * and have one single {@code Scene} parameter. See the example <i>AnimationHandler</i>.
 * <li><b>By checking if the scene's {@link #timer()} was triggered within the frame.</b> See the example <i>Flock</i>.
 */
public class Scene extends AbstractScene implements PConstants {
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

	public void vertex(float x, float y, float z) {
		if (this.is2D())
			pg().vertex(x, y);
		else
			pg().vertex(x, y, z);
	}

	public void vertex(float x, float y) {
		pg().vertex(x, y);
	}

	public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
		if (this.is2D())
			pg().line(x1, y1, x2, y2);
		else
			pg().line(x1, y1, z1, x2, y2, z2);
	}

	public void line(float x1, float y1, float x2, float y2) {
		pg().line(x1, y1, x2, y2);
	}

	public static Vec toVec(PVector v) {
		return new Vec(v.x, v.y, v.z);
	}

	public static PVector toPVector(Vec v) {
		return new PVector(v.x(), v.y(), v.z());
	}

	public static Mat toMat(PMatrix3D m) {
		return new Mat(m.get(new float[16]), true);
	}

	public static PMatrix3D toPMatrix(Mat m) {
		float[] a = m.getTransposed(new float[16]);
		return new PMatrix3D(a[0], a[1], a[2], a[3],
						a[4], a[5], a[6], a[7],
						a[8], a[9], a[10], a[11],
						a[12], a[13], a[14], a[15]);
	}

	public class ProsceneKeyboard extends KeyboardAgent {
		public ProsceneKeyboard(Scene scn, String n) {
			super(scn, n);
			inputHandler().unregisterAgent(this);
		}

		@Override
		public void setDefaultShortcuts() {
			super.setDefaultShortcuts();
			keyboardProfile().setShortcut(B_CTRL, java.awt.event.KeyEvent.VK_1, KeyboardAction.ADD_KEYFRAME_TO_PATH_1);
			keyboardProfile().setShortcut(B_ALT, java.awt.event.KeyEvent.VK_1, KeyboardAction.DELETE_PATH_1);
			keyboardProfile().setShortcut(B_CTRL, java.awt.event.KeyEvent.VK_2, KeyboardAction.ADD_KEYFRAME_TO_PATH_2);
			keyboardProfile().setShortcut(B_ALT, java.awt.event.KeyEvent.VK_2, KeyboardAction.DELETE_PATH_2);
			keyboardProfile().setShortcut(B_CTRL, java.awt.event.KeyEvent.VK_3, KeyboardAction.ADD_KEYFRAME_TO_PATH_3);
			keyboardProfile().setShortcut(B_ALT, java.awt.event.KeyEvent.VK_3, KeyboardAction.DELETE_PATH_3);
			setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_1, 1);
			setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_2, 2);
			setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_3, 3);
		}

		public void keyEvent(processing.event.KeyEvent e) {
			if (e.getAction() == processing.event.KeyEvent.TYPE)
				handle(new ActionKeyboardEvent<KeyboardAction>(e.getKey()));
			else if (e.getAction() == processing.event.KeyEvent.RELEASE)
				handle(new ActionKeyboardEvent<KeyboardAction>(e.getModifiers(), e.getKeyCode()));
		}
	}

	public class ProsceneMouse extends MouseAgent {
		Scene scene;
		boolean bypassNullEvent, need4Spin;
		Point fCorner = new Point();
		Point lCorner = new Point();
		ActionDOF2Event<DOF2Action> event, prevEvent;
		float dFriction = eye().frame().dampingFriction();
		InteractiveFrame iFrame;

		public ProsceneMouse(Scene scn, String n) {
			super(scn, n);
			inputHandler().unregisterAgent(this);
			scene = scn;
		}

		public void mouseEvent(processing.event.MouseEvent e) {
			if (e.getAction() == processing.event.MouseEvent.MOVE) {
				event = new ActionDOF2Event<DOF2Action>(prevEvent, e.getX() - scene.upperLeftCorner.x(), e.getY()
								- scene.upperLeftCorner.y());
				updateGrabber(event);
				prevEvent = event.get();
			}
			if (e.getAction() == processing.event.MouseEvent.PRESS) {
				event = new ActionDOF2Event<DOF2Action>(prevEvent, e.getX() - scene.upperLeftCorner.x(), e.getY()
								- scene.upperLeftCorner.y(), e.getModifiers(), e.getButton());
				if (grabber() instanceof InteractiveFrame) {
					if (need4Spin)
						((InteractiveFrame) grabber()).stopSpinning();
					iFrame = (InteractiveFrame) grabber();
					Action<?> a = (grabber() instanceof InteractiveEyeFrame) ? eyeProfile().handle((ActionBogusEvent<?>) event)
									: frameProfile().handle((ActionBogusEvent<?>) event);
					if (a == null)
						return;
					DandelionAction dA = (DandelionAction) a.referenceAction();
					if (dA == DandelionAction.SCREEN_TRANSLATE)
						((InteractiveFrame) grabber()).dirIsFixed = false;
					need4Spin = (((dA == DandelionAction.ROTATE) || (dA == DandelionAction.ROTATE3)
									|| (dA == DandelionAction.SCREEN_ROTATE) || (dA == DandelionAction.TRANSLATE_ROTATE)) && (((InteractiveFrame) grabber())
									.dampingFriction() == 0));
					bypassNullEvent = (dA == DandelionAction.MOVE_FORWARD) || (dA == DandelionAction.MOVE_BACKWARD)
									|| (dA == DandelionAction.DRIVE) && scene.inputHandler().isAgentRegistered(this);
					setZoomVisualHint(dA == DandelionAction.ZOOM_ON_REGION && (grabber() instanceof InteractiveEyeFrame)
									&& scene.inputHandler().isAgentRegistered(this));
					setRotateVisualHint(dA == DandelionAction.SCREEN_ROTATE && (grabber() instanceof InteractiveEyeFrame)
									&& scene.inputHandler().isAgentRegistered(this));
					if (bypassNullEvent || zoomVisualHint() || rotateVisualHint()) {
						if (bypassNullEvent) {
							// TODO: experimental, this is needed for first person:
							((InteractiveFrame) grabber()).updateFlyUpVector();
							dFriction = ((InteractiveFrame) grabber()).dampingFriction();
							((InteractiveFrame) grabber()).setDampingFriction(0);
							handler.eventTupleQueue().add(new ActionEventGrabberTuple(event, a, grabber()));
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
				// if( e.getAction() == processing.event.MouseEvent.MOVE ) {//e.g., rotate without dragging any button also
				// possible :P
				if (zoomVisualHint() || rotateVisualHint())
					lCorner.set(e.getX() - scene.upperLeftCorner.x(), e.getY() - scene.upperLeftCorner.y());
				if (!zoomVisualHint()) { // bypass zoom_on_region, may be different when using a touch device :P
					event = new ActionDOF2Event<DOF2Action>(prevEvent, e.getX() - scene.upperLeftCorner.x(), e.getY()
									- scene.upperLeftCorner.y(), e.getModifiers(), e.getButton());
					handle(event);
					prevEvent = event.get();
				}
			}
			if (e.getAction() == processing.event.MouseEvent.RELEASE) {
				if (grabber() instanceof InteractiveFrame)
					if (need4Spin && (prevEvent.speed() >= ((InteractiveFrame) grabber()).spinningSensitivity()))
						((InteractiveFrame) grabber()).startSpinning(prevEvent);
				event = new ActionDOF2Event<DOF2Action>(prevEvent, e.getX() - scene.upperLeftCorner.x(), e.getY()
								- scene.upperLeftCorner.y(), e.getModifiers(), e.getButton());
				if (zoomVisualHint()) {
					// at first glance this should work
					// handle(event);
					// but the problem is that depending on the order the button and the modifiers are released,
					// different actions maybe triggered, so we go for sure ;) :
					enqueueEventTuple(new ActionEventGrabberTuple(event, DOF2Action.ZOOM_ON_REGION, grabber()));
					setZoomVisualHint(false);
				}
				if (rotateVisualHint())
					setRotateVisualHint(false);
				updateGrabber(event);
				prevEvent = event.get();
				if (bypassNullEvent) {
					iFrame.setDampingFriction(dFriction);
					bypassNullEvent = !bypassNullEvent;
				}
			}
			if (e.getAction() == processing.event.MouseEvent.WHEEL) {
				handle(new ActionDOF1Event<WheelAction>(e.getCount(), e.getModifiers(), B_NOBUTTON));
			}
			if (e.getAction() == processing.event.MouseEvent.CLICK) {
				handle(new ActionClickEvent<ClickAction>(e.getX() - scene.upperLeftCorner.x(), e.getY()
								- scene.upperLeftCorner.y(), e.getModifiers(), e.getButton(), e.getCount()));
			}
		}

		// hack to deal with this: https://github.com/processing/processing/issues/1693
		// is to override all the following so that:
		// 1. Whenever B_CENTER appears B_ALT should be present
		// 2. Whenever B_RIGHT appears B_META should be present
		@Override
		public void setAsFirstPerson() {
			if (is2D()) {
				AbstractScene.showDepthWarning("setAsFirstPerson");
				return;
			}
			resetAllProfiles();
			eyeProfile().setBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, DOF2Action.MOVE_FORWARD);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_CENTER), B_CENTER, DOF2Action.LOOK_AROUND);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_RIGHT), B_RIGHT, DOF2Action.MOVE_BACKWARD);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_LEFT), B_LEFT, DOF2Action.ROLL);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_CENTER), B_CENTER, DOF2Action.DRIVE);
			eyeWheelProfile().setBinding(B_CTRL, B_NOBUTTON, WheelAction.ROLL);
			eyeWheelProfile().setBinding(B_SHIFT, B_NOBUTTON, WheelAction.DRIVE);
			setCommonBindings();
		}

		@Override
		public void setAsThirdPerson() {
			if (is2D()) {
				AbstractScene.showDepthWarning("setAsThirdPerson");
				return;
			}
			resetAllProfiles();
			frameProfile().setBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, DOF2Action.MOVE_FORWARD);
			frameProfile().setBinding(p5ButtonModifiersFix(B_CENTER), B_CENTER, DOF2Action.LOOK_AROUND);
			frameProfile().setBinding(p5ButtonModifiersFix(B_RIGHT), B_RIGHT, DOF2Action.MOVE_BACKWARD);
			frameProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_LEFT), B_LEFT, DOF2Action.ROLL);
			frameProfile().setBinding(p5ButtonModifiersFix(B_SHIFT, B_CENTER), B_CENTER, DOF2Action.DRIVE);
			setCommonBindings();
		}

		@Override
		public void setAsArcball() {
			resetAllProfiles();
			eyeProfile().setBinding(p5ButtonModifiersFix(B_LEFT), B_LEFT, DOF2Action.ROTATE);
			eyeProfile().setBinding(p5ButtonModifiersFix(B_CENTER), B_CENTER, DOF2Action.ZOOM);
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

	protected class TimerWrap implements Timable {
		Scene scene;
		Timer timer;
		TimerTask timerTask;
		Taskable caller;
		boolean runOnlyOnce;
		long prd;

		public TimerWrap(Scene scn, Taskable o) {
			this(scn, o, false);
		}

		public TimerWrap(Scene scn, Taskable o, boolean singleShot) {
			scene = scn;
			runOnlyOnce = singleShot;
			caller = o;
		}

		public Taskable timerJob() {
			return caller;
		}

		@Override
		public void create() {
			stop();
			timer = new Timer();
			timerTask = new TimerTask() {
				public void run() {
					caller.execute();
				}
			};
		}

		@Override
		public void run(long period) {
			prd = period;
			run();
		}

		@Override
		public void run() {
			create();
			if (isSingleShot())
				timer.schedule(timerTask, prd);
			else
				timer.scheduleAtFixedRate(timerTask, 0, prd);
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
		}

		@Override
		public boolean isActive() {
			return timer != null;
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

	protected class P5Java2DMatrixHelper extends AbstractMatrixHelper {
		PGraphics pg;
		Mat proj, mv;

		public P5Java2DMatrixHelper(Scene scn, PGraphics renderer) {
			super(scn);
			pg = renderer;
			proj = new Mat();
			mv = new Mat();
		}

		public PGraphics pg() {
			return pg;
		}

		public PGraphicsJava2D pgj2d() {
			return (PGraphicsJava2D) pg();
		}

		@Override
		public void bind() {
			scene.eye().getProjection(proj, true);
			scene.eye().getView(mv, true);
			cacheProjectionViewInverse();

			Vec pos = scene.eye().position();
			Rotation o = scene.eye().frame().orientation();

			translate(scene.width() / 2, scene.height() / 2);
			if (scene.isRightHanded())
				scale(1, -1);
			// TODO experimental
			// scale(scene.viewpoint().frame().inverseMagnitude().x(), scene.viewpoint().frame().inverseMagnitude().y());
			scale(1 / scene.eye().frame().scaling().x(), 1 / scene.eye().frame().scaling().y());
			rotate(-o.angle());
			translate(-pos.x(), -pos.y());
		}

		@Override
		public void cacheProjectionViewInverse() {
			Mat.multiply(proj, mv, projectionViewMat);
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
			// TODO experimental
			// scale(scene.window().frame().magnitude().x(), scene.window().frame().magnitude().y());
			scale(scene.window().frame().scaling().x(), scene.window().frame().scaling().y());
			if (scene.isRightHanded())
				scale(1, -1);
			translate(-scene.width() / 2, -scene.height() / 2);
		}

		@Override
		public void endScreenDrawing() {
			popModelView();
		}

		// matrix stuff

		@Override
		public Mat projection() {
			return scene.eye().getProjection(false);
		}

		@Override
		public Mat getProjection(Mat target) {
			if (target == null)
				target = new Mat();
			target.set(scene.eye().getProjection(false));
			return target;
		}

		// --

		@Override
		public void pushModelView() {
			pgj2d().pushMatrix();
		}

		@Override
		public void popModelView() {
			pgj2d().popMatrix();
		}

		@Override
		public void resetModelView() {
			pgj2d().resetMatrix();
		}

		// TODO seems getModelView is not working in java2d
		@Override
		public Mat modelView() {
			return Scene.toMat(new PMatrix3D(pgj2d().getMatrix()));
		}

		@Override
		public Mat getModelView(Mat target) {
			if (target == null)
				target = new Mat(Scene.toMat((PMatrix3D) pgj2d().getMatrix()));
			else
				target.set(Scene.toMat((PMatrix3D) pgj2d().getMatrix()));
			return target;
		}

		@Override
		public void setModelView(Mat source) {
			resetModelView();
			applyModelView(source);
		}

		@Override
		public void printModelView() {
			pgj2d().printMatrix();
		}

		@Override
		public void printProjection() {
			pgj2d().printProjection();
		}

		@Override
		public void applyModelView(Mat source) {
			pgj2d().applyMatrix(Scene.toPMatrix(source));
		}

		@Override
		public void applyModelViewRowMajorOrder(float n00, float n01, float n02, float n03,
						float n10, float n11, float n12, float n13,
						float n20, float n21, float n22, float n23,
						float n30, float n31, float n32, float n33) {
			pgj2d().applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
		}

		//

		@Override
		public void translate(float tx, float ty) {
			pgj2d().translate(tx, ty);
		}

		@Override
		public void translate(float tx, float ty, float tz) {
			pgj2d().translate(tx, ty, tz);
		}

		@Override
		public void rotate(float angle) {
			pgj2d().rotate(angle);
		}

		@Override
		public void rotateX(float angle) {
			pgj2d().rotateX(angle);
		}

		@Override
		public void rotateY(float angle) {
			pgj2d().rotateY(angle);
		}

		@Override
		public void rotateZ(float angle) {
			pgj2d().rotateZ(angle);
		}

		@Override
		public void rotate(float angle, float vx, float vy, float vz) {
			pgj2d().rotate(angle, vx, vy, vz);
		}

		@Override
		public void scale(float s) {
			pgj2d().scale(s);
		}

		@Override
		public void scale(float sx, float sy) {
			pgj2d().scale(sx, sy);
		}

		@Override
		public void scale(float x, float y, float z) {
			pgj2d().scale(x, y, z);
		}

		@Override
		public void pushProjection() {
			AbstractScene.showMissingImplementationWarning("pushProjection", getClass().getName());
		}

		@Override
		public void popProjection() {
			AbstractScene.showMissingImplementationWarning("popProjection", getClass().getName());
		}

		@Override
		public void resetProjection() {
			AbstractScene.showMissingImplementationWarning("resetProjection", getClass().getName());
		}

		@Override
		public void applyProjection(Mat source) {
			AbstractScene.showMissingImplementationWarning("resetProjection", getClass().getName());
		}

		@Override
		public void applyProjectionRowMajorOrder(float n00, float n01, float n02,
						float n03, float n10, float n11, float n12, float n13, float n20,
						float n21, float n22, float n23, float n30, float n31, float n32,
						float n33) {
			AbstractScene.showMissingImplementationWarning("applyProjectionRowMajorOrder", getClass().getName());
		}

		@Override
		public void setProjection(Mat source) {
			AbstractScene.showMissingImplementationWarning("setProjection", getClass().getName());
		}

		@Override
		public void loadProjection() {
			AbstractScene.showMissingImplementationWarning("loadProjection", getClass().getName());
		}

		@Override
		public void loadModelView() {
			AbstractScene.showMissingImplementationWarning("loadModelView", getClass().getName());
		}
	}

	protected class P5GLMatrixHelper extends AbstractMatrixHelper {
		PGraphicsOpenGL pg;

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
				target = new Mat(Scene.toMat(pggl().projection.get()));
			else
				target.set(Scene.toMat(pggl().projection.get()));
			return target;
		}

		@Override
		public void applyProjection(Mat source) {
			pggl().applyProjection(Scene.toPMatrix(source));
		}

		@Override
		public void applyProjectionRowMajorOrder(float n00, float n01, float n02,
						float n03, float n10, float n11, float n12, float n13, float n20,
						float n21, float n22, float n23, float n30, float n31, float n32,
						float n33) {
			pggl().applyProjection(
							new PMatrix3D(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33));
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
				target = new Mat(Scene.toMat((PMatrix3D) pggl().getMatrix()));
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
		public void applyModelViewRowMajorOrder(float n00, float n01, float n02, float n03,
						float n10, float n11, float n12, float n13,
						float n20, float n21, float n22, float n23,
						float n30, float n31, float n32, float n33) {
			pggl().applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
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

	// proscene version
	public static final String prettyVersion = "2.0.0";

	public static final String version = "16";

	// P R O C E S S I N G A P P L E T A N D O B J E C T S
	protected PApplet parent;
	protected PGraphics pgraphics;

	// E X C E P T I O N H A N D L I N G
	protected int beginOffScreenDrawingCalls;

	// R E G I S T E R D R A W A N D A N I M A T I O N M E T H O D S
	// Draw
	/** The object to handle the draw event */
	protected Object drawHandlerObject;
	/** The method in drawHandlerObject to execute */
	protected Method drawHandlerMethod;
	/** the name of the method to handle the event */
	protected String drawHandlerMethodName;
	// Animation
	/** The object to handle the animation */
	protected Object animateHandlerObject;
	/** The method in animateHandlerObject to execute */
	protected Method animateHandlerMethod;
	/** the name of the method to handle the animation */
	protected String animateHandlerMethodName;

	protected boolean javaTiming;

	// Eventhandling agents
	protected MouseAgent defMouseAgent;
	protected KeyboardAgent defKeyboardAgent;

	/**
	 * Constructor that defines an on-screen Scene (the one that most likely would just fulfill all of your needs). All
	 * viewer parameters (display flags, scene parameters, associated objects...) are set to their default values. See the
	 * associated documentation. This is actually just a convenience function that simply calls
	 * {@code this(p, (PGraphicsOpenGL) p.g)}. Call any other constructor by yourself to possibly define an off-screen
	 * Scene.
	 * 
	 * @see #Scene(PApplet, PGraphics)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */
	public Scene(PApplet p) {
		this(p, p.g);
	}

	/**
	 * This constructor is typically used to define an off-screen Scene. This is accomplished simply by specifying a
	 * custom {@code renderer}, different from the PApplet's renderer. All viewer parameters (display flags, scene
	 * parameters, associated objects...) are set to their default values. This is actually just a convenience function
	 * that simply calls {@code this(p, renderer, 0, 0)}. If you plan to define an on-screen Scene, call
	 * {@link #Scene(PApplet)} instead.
	 * 
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */
	public Scene(PApplet p, PGraphics renderer) {
		this(p, renderer, 0, 0);
	}

	/**
	 * This constructor is typically used to define an off-screen Scene. This is accomplished simply by specifying a
	 * custom {@code renderer}, different from the PApplet's renderer. All viewer parameters (display flags, scene
	 * parameters, associated objects...) are set to their default values. The {@code x} and {@code y} parameters define
	 * the position of the upper-left corner where the off-screen Scene is expected to be displayed, e.g., for instance
	 * with a call to the Processing built-in {@code image(img, x, y)} function. If {@link #isOffscreen()} returns
	 * {@code false} (i.e., {@link #matrixHelper()} equals the PApplet's renderer), the values of x and y are meaningless
	 * (both are set to 0 to be taken as dummy values). If you plan to define an on-screen Scene, call
	 * {@link #Scene(PApplet)} instead.
	 * 
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics)
	 */
	public Scene(PApplet p, PGraphics pg, int x, int y) {
		parent = p;
		pgraphics = pg;

		if (pg instanceof PGraphicsJava2D)
			setMatrixHelper(new P5Java2DMatrixHelper(this, (PGraphicsJava2D) pg));
		else if (pg instanceof PGraphics2D)
			setMatrixHelper(new P5GLMatrixHelper(this, (PGraphics2D) pg));
		else if (pg instanceof PGraphics3D)
			setMatrixHelper(new P5GLMatrixHelper(this, (PGraphics3D) pg));

		width = pg.width;
		height = pg.height;

		if (is2D())
			this.setDottedGrid(false);

		// setJavaTimers();
		this.parent.frameRate(100);
		setLeftHanded();

		// 1 ->
		avatarIsInteractiveFrame = false;// also init in setAvatar, but we
		// need it here to properly init the camera
		avatarIsInteractiveAvatarFrame = false;// also init in setAvatar, but we
		// need it here to properly init the camera

		if (is3D())
			eye = new Camera(this);
		else
			eye = new Window(this);
		setEye(eye());// calls showAll();

		setAvatar(null);

		// This scene is offscreen if the provided renderer is
		// different from the main PApplet renderer.
		offscreen = pg != p.g;
		if (offscreen)
			upperLeftCorner = new Point(x, y);
		else
			upperLeftCorner = new Point(0, 0);
		beginOffScreenDrawingCalls = 0;
		// setDeviceTracking(true);
		// setDeviceGrabber(null);

		// deviceGrabberIsAnIFrame = false;

		// withConstraint = true;

		this.setVisualHints(AXIS | GRID);

		disableBoundaryEquations();

		setDefaultKeyboardAgent(new ProsceneKeyboard(this, "proscene_keyboard"));
		setDefaultMouseAgent(new ProsceneMouse(this, "proscene_mouse"));

		parent.registerMethod("pre", this);
		parent.registerMethod("draw", this);

		// register draw method
		removeDrawHandler();
		// register animation method
		removeAnimationHandler();

		// called only once
		init();
	}

	// firstly, of course, dirty things that I love:

	public void setMouseAsArcball() {
		defaultMouseAgent().setAsArcball();
	}

	public void setMouseAsFirstPerson() {
		defaultMouseAgent().setAsFirstPerson();
	}

	public void setMouseAsThirdPerson() {
		defaultMouseAgent().setAsThirdPerson();
	}

	// mouse wrappers that fix <a href="https://github.com/processing/processing/issues/1693">issue</a>
	public void setMouseButtonBinding(boolean eye, int mask, int button, DOF2Action action) {
		MotionProfile<DOF2Action> profile = eye ? defaultMouseAgent().eyeProfile() : defaultMouseAgent()
						.frameProfile();
		if (profile != null)
			profile.setBinding(p5ButtonModifiersFix(mask, button), button, action);
	}

	public void setMouseButtonBinding(boolean eye, int button, DOF2Action action) {
		MotionProfile<DOF2Action> profile = eye ? defaultMouseAgent().eyeProfile() : defaultMouseAgent()
						.frameProfile();
		profile.setBinding(p5ButtonModifiersFix(button), button, action);
	}

	public void removeMouseButtonBinding(boolean eye, int mask, int button) {
		MotionProfile<DOF2Action> profile = eye ? defaultMouseAgent().eyeProfile() : defaultMouseAgent()
						.frameProfile();
		if (profile != null)
			profile.removeBinding(p5ButtonModifiersFix(mask, button), button);
	}

	public void removeMouseButtonBinding(boolean eye, int button) {
		MotionProfile<DOF2Action> profile = eye ? defaultMouseAgent().eyeProfile() : defaultMouseAgent()
						.frameProfile();
		if (profile != null)
			profile.removeBinding(p5ButtonModifiersFix(button), button);
	}

	public boolean isMouseButtonBindingInUse(boolean eye, int mask, int button) {
		MotionProfile<DOF2Action> profile = eye ? defaultMouseAgent().eyeProfile() : defaultMouseAgent()
						.frameProfile();
		return profile.isBindingInUse(p5ButtonModifiersFix(mask, button), button);
	}

	public boolean isMouseButtonBindingInUse(boolean eye, int button) {
		MotionProfile<DOF2Action> profile = eye ? defaultMouseAgent().eyeProfile() : defaultMouseAgent()
						.frameProfile();
		return profile.isBindingInUse(p5ButtonModifiersFix(button), button);
	}

	// wheel here

	public void setMouseWheelBinding(boolean eye, int mask, WheelAction action) {
		MotionProfile<WheelAction> profile = eye ? defaultMouseAgent().wheelProfile()
						: defaultMouseAgent().frameWheelProfile();
		if (profile != null)
			profile.setBinding(mask, B_NOBUTTON, action);
	}

	public void setMouseWheelBinding(boolean eye, WheelAction action) {
		MotionProfile<WheelAction> profile = eye ? defaultMouseAgent().wheelProfile()
						: defaultMouseAgent().frameWheelProfile();
		if (profile != null)
			profile.setBinding(action);
	}

	public void removeMouseWheelBinding(boolean eye, int mask) {
		MotionProfile<WheelAction> profile = eye ? defaultMouseAgent().wheelProfile()
						: defaultMouseAgent().frameWheelProfile();
		if (profile != null)
			profile.removeBinding(mask, B_NOBUTTON);
	}

	public void removeMouseWheelBinding(boolean eye) {
		MotionProfile<WheelAction> profile = eye ? defaultMouseAgent().wheelProfile()
						: defaultMouseAgent().frameWheelProfile();
		if (profile != null)
			profile.removeBinding();
	}

	public boolean isMouseWheelBindingInUse(boolean eye, int mask) {
		MotionProfile<WheelAction> profile = eye ? defaultMouseAgent().wheelProfile()
						: defaultMouseAgent().frameWheelProfile();
		return profile.isBindingInUse(mask, B_NOBUTTON);
	}

	public boolean isMouseWheelBindingInUse(boolean eye) {
		MotionProfile<WheelAction> profile = eye ? defaultMouseAgent().wheelProfile()
						: defaultMouseAgent().frameWheelProfile();
		return profile.isBindingInUse();
	}

	// mouse click

	public void setMouseClickBinding(boolean eye, int mask, int button, int ncs, ClickAction action) {
		ClickProfile<ClickAction> profile = eye ? defaultMouseAgent().clickProfile() : defaultMouseAgent()
						.frameClickProfile();
		if (profile != null)
			profile.setClickBinding(p5ButtonModifiersFix(mask, button), button, ncs, action);
	}

	public void setMouseClickBinding(boolean eye, int button, int ncs, ClickAction action) {
		ClickProfile<ClickAction> profile = eye ? defaultMouseAgent().clickProfile() : defaultMouseAgent()
						.frameClickProfile();
		if (profile != null)
			profile.setClickBinding(p5ButtonModifiersFix(button), button, ncs, action);
	}

	public void setMouseClickBinding(boolean eye, int button, ClickAction action) {
		ClickProfile<ClickAction> profile = eye ? defaultMouseAgent().clickProfile() : defaultMouseAgent()
						.frameClickProfile();
		if (profile != null)
			profile.setClickBinding(p5ButtonModifiersFix(button), button, 1, action);
	}

	public void removeMouseClickBinding(boolean eye, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = eye ? defaultMouseAgent().clickProfile() : defaultMouseAgent()
						.frameClickProfile();
		if (profile != null)
			profile.removeClickBinding(p5ButtonModifiersFix(mask, button), button, ncs);
	}

	public void removeMouseClickBinding(boolean eye, int button, int ncs) {
		ClickProfile<ClickAction> profile = eye ? defaultMouseAgent().clickProfile() : defaultMouseAgent()
						.frameClickProfile();
		if (profile != null)
			profile.removeClickBinding(p5ButtonModifiersFix(button), button, ncs);
	}

	public void removeMouseClickBinding(boolean eye, int button) {
		ClickProfile<ClickAction> profile = eye ? defaultMouseAgent().clickProfile() : defaultMouseAgent()
						.frameClickProfile();
		if (profile != null)
			profile.removeClickBinding(p5ButtonModifiersFix(button), button, 1);
	}

	public boolean isMouseClickBindingInUse(boolean eye, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = eye ? defaultMouseAgent().clickProfile() : defaultMouseAgent()
						.frameClickProfile();
		return profile.isClickBindingInUse(p5ButtonModifiersFix(mask, button), button, ncs);
	}

	public boolean isMouseClickBindingInUse(boolean eye, int button, int ncs) {
		ClickProfile<ClickAction> profile = eye ? defaultMouseAgent().clickProfile() : defaultMouseAgent()
						.frameClickProfile();
		return profile.isClickBindingInUse(p5ButtonModifiersFix(button), button, ncs);
	}

	public boolean isMouseClickBindingInUse(boolean eye, int button) {
		ClickProfile<ClickAction> profile = eye ? defaultMouseAgent().clickProfile() : defaultMouseAgent()
						.frameClickProfile();
		return profile.isClickBindingInUse(p5ButtonModifiersFix(button), button, 1);
	}

	// keyboard here

	public void setDefaultKeyboardShortcuts() {
		defaultKeyboardAgent().setDefaultShortcuts();
	}

	public void setKeyCodeToPlayPath(int code, int path) {
		defaultKeyboardAgent().setKeyCodeToPlayPath(code, path);
	}

	public void setKeyboardShortcut(Character key, KeyboardAction action) {
		defaultKeyboardAgent().keyboardProfile().setShortcut(key, action);
	}

	public void setKeyboardShortcut(int mask, int vKey, KeyboardAction action) {
		defaultKeyboardAgent().keyboardProfile().setShortcut(mask, vKey, action);
	}

	public void removeKeyboardShortcut(Character key) {
		defaultKeyboardAgent().keyboardProfile().removeShortcut(key);
	}

	public void removeKeyboardShortcut(int mask, int vKey) {
		defaultKeyboardAgent().keyboardProfile().removeShortcut(mask, vKey);
	}

	public boolean isKeyboardShortcutInUse(Character key) {
		return defaultKeyboardAgent().keyboardProfile().isShortcutInUse(key);
	}

	public boolean isKeyboardShortcutInUse(int mask, int vKey) {
		return defaultKeyboardAgent().keyboardProfile().isShortcutInUse(mask, vKey);
	}

	// TODO add new high-level example
	// setMouseClickBinding(defaultMouseClickEyeProfile(), B_SHIFT, B_RIGHT, 2, ClickAction.DRAW_AXIS);

	@Override
	public boolean is3D() {
		return (pgraphics instanceof PGraphics3D);
	}

	public KeyboardAgent defaultKeyboardAgent() {
		return defKeyboardAgent;
	}

	public void setDefaultKeyboardAgent(KeyboardAgent keyboard) {
		if (keyboard == null)
			return;
		if (defaultKeyboardAgent() != null)
			disableDefaultKeyboardAgent();
		defKeyboardAgent = keyboard;
		enableDefaultKeyboardAgent();
	}

	/**
	 * Enables Proscene keyboard handling.
	 * 
	 * @see #isDefaultKeyboardAgentEnabled()
	 * @see #enableDefaultMouseAgent()
	 * @see #disableDefaultKeyboardAgent()
	 */
	public void enableDefaultKeyboardAgent() {
		if (!inputHandler().isAgentRegistered(defaultKeyboardAgent())) {
			inputHandler().registerAgent(defaultKeyboardAgent());
			parent.registerMethod("keyEvent", defaultKeyboardAgent());
		}
	}

	/**
	 * Disables the default keyboard agent and returns it.
	 * 
	 * @see #isDefaultKeyboardAgentEnabled()
	 */
	public KeyboardAgent disableDefaultKeyboardAgent() {
		if (inputHandler().isAgentRegistered(defaultKeyboardAgent())) {
			parent.unregisterMethod("keyEvent", defaultKeyboardAgent());
			return (KeyboardAgent) inputHandler().unregisterAgent(defaultKeyboardAgent());
		}
		return defaultKeyboardAgent();
	}

	public MouseAgent defaultMouseAgent() {
		return defMouseAgent;
	}

	public void setDefaultMouseAgent(MouseAgent agent) {
		if (agent == null)
			return;
		if (defaultMouseAgent() != null)
			disableDefaultMouseAgent();
		defMouseAgent = agent;
		enableDefaultMouseAgent();
	}

	public boolean isDefaultMouseAgentEnabled() {
		return inputHandler().isAgentRegistered(defMouseAgent);
	}

	public boolean isDefaultKeyboardAgentEnabled() {
		return inputHandler().isAgentRegistered(defKeyboardAgent);
	}

	/**
	 * Enables Proscene mouse handling.
	 * 
	 * @see #isDefaultMouseAgentEnabled()
	 * @see #disableDefaultMouseAgent()
	 * @see #enableDefaultKeyboardAgent()
	 */
	public void enableDefaultMouseAgent() {
		if (!inputHandler().isAgentRegistered(defaultMouseAgent())) {
			inputHandler().registerAgent(defaultMouseAgent());
			parent.registerMethod("mouseEvent", defaultMouseAgent());
		}
	}

	/**
	 * Disables Proscene mouse handling.
	 * 
	 * @see #isDefaultMouseAgentEnabled()
	 */
	public MouseAgent disableDefaultMouseAgent() {
		if (inputHandler().isAgentRegistered(defaultMouseAgent())) {
			parent.unregisterMethod("mouseEvent", defaultMouseAgent());
			return (MouseAgent) inputHandler().unregisterAgent(defaultMouseAgent());
		}
		return defaultMouseAgent();
	}

	// 2. Associated objects

	@Override
	public void registerJob(AbstractTimerJob job) {
		if (isTimingSingleThreaded())
			timerHandler().registerJob(job);
		else
			timerHandler().registerJob(job, new TimerWrap(this, job));
	}

	public void setJavaTimers() {
		if (!isTimingSingleThreaded())
			return;

		boolean isActive;

		for (AbstractTimerJob job : timerHandler().timerPool()) {
			long period = 0;
			boolean rOnce = false;
			isActive = job.isActive();
			if (isActive) {
				period = job.period();
				rOnce = job.timer().isSingleShot();
			}
			job.stop();
			job.setTimer(new TimerWrap(this, job));
			if (isActive) {
				if (rOnce)
					job.runOnce(period);
				else
					job.run(period);
			}
		}

		javaTiming = true;
		PApplet.println("java util timers set");
	}

	public boolean isTimingSingleThreaded() {
		return !javaTiming;
	}

	public void switchTimers() {
		if (isTimingSingleThreaded())
			setJavaTimers();
		else
			setSingleThreadedTimers();
	}

	public void setSingleThreadedTimers() {
		javaTiming = false;
		timerHandler().restoreTimers();
	}

	// 5. Drawing methods

	/**
	 * Paint method which is called just before your {@code PApplet.draw()} method. This method is registered at the
	 * PApplet and hence you don't need to call it.
	 * <p>
	 * Sets the processing camera parameters from {@link #eye()} and updates the frustum planes equations if
	 * {@link #enableBoundaryEquations(boolean)} has been set to {@code true}.
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
	 * Paint method which is called just after your {@code PApplet.draw()} method. This method is registered at the
	 * PApplet and hence you don't need to call it. Calls {@link #postDraw()}.
	 * 
	 * @see #postDraw()
	 */
	public void draw() {
		if (isOffscreen())
			return;
		postDraw();
	}

	@Override
	protected void invokeDrawHandler() {
		// 3. Draw external registered method
		if (drawHandlerObject != null) {
			try {
				drawHandlerMethod.invoke(drawHandlerObject, new Object[] { this });
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + drawHandlerMethodName + " method");
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method should be called when using offscreen rendering right after renderer.beginDraw().
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
	 * This method should be called when using offscreen rendering right before renderer.endDraw(). Calls
	 * {@link #postDraw()}.
	 * 
	 * @see #postDraw()
	 */
	public void endDraw() {
		beginOffScreenDrawingCalls--;

		if (beginOffScreenDrawingCalls != 0)
			throw new RuntimeException(
							"There should be exactly one beginDraw() call followed by a "
											+ "endDraw() and they cannot be nested. Check your implementation!");

		postDraw();
	}

	public PGraphics pg() {
		return pgraphics;
	}

	public PGraphicsJava2D pgj2d() {
		if (pg() instanceof PGraphicsJava2D)
			return (PGraphicsJava2D) pg();
		else
			throw new RuntimeException("pGraphics is not instance of PGraphicsJava2D");
	}

	public PGraphicsOpenGL pggl() {
		if (pg() instanceof PGraphicsOpenGL)
			return (PGraphicsOpenGL) pg();
		else
			throw new RuntimeException("pGraphics is not instance of PGraphicsOpenGL");
	}

	public PGraphics2D pg2d() {
		if (pg() instanceof PGraphics2D)
			return (PGraphics2D) ((P5GLMatrixHelper) matrixHelper()).pggl();
		else
			throw new RuntimeException("pGraphics is not instance of PGraphics2D");
	}

	public PGraphics3D pg3d() {
		if (pg() instanceof PGraphics3D)
			return (PGraphics3D) ((P5GLMatrixHelper) matrixHelper()).pggl();
		else
			throw new RuntimeException("pGraphics is not instance of PGraphics3D");
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

	/**
	 * Displays the {@link #info()} bindings.
	 * 
	 * @param onConsole
	 *          if this flag is true displays the help on console. Otherwise displays it on the applet
	 * 
	 * @see #info()
	 */
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

	public PApplet pApplet() {
		return parent;
	}

	// 10. Draw method registration

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
	 */
	public void removeDrawHandler() {
		drawHandlerMethod = null;
		drawHandlerObject = null;
		drawHandlerMethodName = null;
	}

	/**
	 * Returns {@code true} if the user has registered a 'draw' handler method to the Scene and {@code false} otherwise.
	 */
	public boolean hasDrawHandler() {
		if (drawHandlerMethodName == null)
			return false;
		return true;
	}

	// 11. Animation

	/**
	 * Internal use.
	 * <p>
	 * Calls the animation handler.
	 * 
	 * @see #animationPeriod()
	 * @see #startAnimation()
	 */
	@Override
	public boolean invokeAnimationHandler() {
		if (animateHandlerObject != null) {
			try {
				animateHandlerMethod.invoke(animateHandlerObject, new Object[] { this });
				return true;
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + animateHandlerMethodName + " method");
				e.printStackTrace();
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
	 */
	public boolean hasAnimationHandler() {
		if (animateHandlerMethodName == null)
			return false;
		return true;
	}

	//

	/**
	 * Returns the coordinates of the 3D point located at {@code pixel} (x,y) on screen.
	 */
	@Override
	protected Camera.WorldPoint pointUnderPixel(Point pixel) {
		float[] depth = new float[1];
		PGL pgl = pggl().beginPGL();
		pgl.readPixels(pixel.x(), (camera().screenHeight() - pixel.y()), 1, 1, PGL.DEPTH_COMPONENT, PGL.FLOAT,
						FloatBuffer.wrap(depth));
		pggl().endPGL();
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
	public void drawAxis(float length) {
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
							drawAxis(scale / 10.0f);

						popModelView();
					}
			}
			kfi.addFramesToAllAgentPools();
			pg().strokeWeight(pg().strokeWeight / 2f);
			drawFrameSelectionTargets(true);
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
	public void drawFrameSelectionTargets(boolean keyFrame) {
		pg().pushStyle();
		for (Grabbable mg : inputHandler().globalGrabberList()) {
			if (mg instanceof InteractiveFrame) {
				InteractiveFrame iF = (InteractiveFrame) mg;// downcast needed
				// frames
				if (!(iF.isInCameraPath() ^ keyFrame)) {
					Vec center = projectedCoordinatesOf(iF.position());
					if (grabsAnAgent(mg)) {
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

	// Code contributed by Jacques Maire (http://www.alcys.com/) See also:
	// http://www.mathcurve.com/courbes3d/solenoidtoric/solenoidtoric.shtml
	// http://crazybiocomputing.blogspot.fr/2011/12/3d-curves-toric-solenoids.html
	@Override
	public void drawTorusSolenoid(int faces, int detail, float insideRadius, float outsideRadius) {
		pg().pushStyle();
		pg().noStroke();
		Vec v1, v2;
		int b, ii, jj, a;
		int c1, c2, c;
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
				pg().colorMode(PApplet.RGB, 255);
				float alfa = pg().alpha(pg().fillColor);
				c1 = pg().color(200 + 55 * PApplet.cos(jj * eps), 130 + 125 * PApplet.sin(jj * eps), 0, alfa);
				c2 = pg().color(130 + 125 * PApplet.sin(jj * eps), 0, 200 + 55 * PApplet.cos(jj * eps), alfa);
				c = (a % 3 == 0) ? c1 : c2;
				pg().fill(c);
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
	protected void drawAxisHint() {
		pg().pushStyle();
		pg().strokeWeight(2);
		drawAxis(eye().sceneRadius());
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
		drawAllEyePaths();
		pg().popStyle();
	}

	@Override
	/*
	 * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
	 */
	protected void drawFramesHint() {
		pg().pushStyle();
		pg().colorMode(PApplet.RGB, 255);
		pg().strokeWeight(1);
		pg().stroke(220, 220, 220);
		drawFrameSelectionTargets();
		pg().popStyle();
	}

	@Override
	protected void drawArcballReferencePointHint() {
		pg().pushStyle();
		Vec p = eye().projectedCoordinatesOf(arcballReferencePoint());
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
		if (!(defaultMouseAgent() instanceof ProsceneMouse))
			return;
		float p1x = (float) ((ProsceneMouse) defaultMouseAgent()).lCorner.x();
		float p1y = (float) ((ProsceneMouse) defaultMouseAgent()).lCorner.y();
		Vec p2 = eye().projectedCoordinatesOf(arcballReferencePoint());
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
		if (!(defaultMouseAgent() instanceof ProsceneMouse))
			return;
		pg().pushStyle();
		float p1x = (float) ((ProsceneMouse) defaultMouseAgent()).fCorner.x();
		float p1y = (float) ((ProsceneMouse) defaultMouseAgent()).fCorner.y();
		float p2x = (float) ((ProsceneMouse) defaultMouseAgent()).lCorner.x();
		float p2y = (float) ((ProsceneMouse) defaultMouseAgent()).lCorner.y();
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
