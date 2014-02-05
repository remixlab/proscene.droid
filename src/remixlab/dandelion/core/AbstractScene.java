/*******************************************************************************
 * dandelion (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.dandelion.core;

import java.util.HashMap;

import remixlab.dandelion.agent.GenericWheeledBiMotionAgent;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.helper.MatrixStackHelper;
import remixlab.fpstiming.AbstractTimerJob;
import remixlab.fpstiming.Animatable;
import remixlab.fpstiming.AnimatedObject;
import remixlab.fpstiming.TimingHandler;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;
import remixlab.tersehandling.event.*;

public abstract class AbstractScene extends AnimatedObject implements Constants, Grabbable {	
	protected boolean dottedGrid;
	
  //O B J E C T S
	protected MatrixHelpable matrixHelper;
	protected VisualHintable drawingHelpler;
	
	protected Eye eye;
	protected Trackable trck;
	public boolean avatarIsInteractiveFrame;
	protected boolean avatarIsInteractiveAvatarFrame;
	
  //E X C E P T I O N H A N D L I N G
	protected int startCoordCalls;
	
  // T i m e r P o o l
  //T I M E R S
  //protected boolean singleThreadedTaskableTimers;
	protected TimingHandler timerHandler;
	
	//TerseHandler
	protected TerseHandler terseHandler;

	// D I S P L A Y F L A G S
	protected int visualHintFlag;
	
	// LEFT vs RIGHT_HAND
	protected boolean rightHanded;
	
	// S I Z E
	protected int width, height;
	
	//offscreen
	public Point upperLeftCorner;
	protected boolean offscreen;
	
	/**
   * The system variables <b>cursorX</b> and <b>cursorY</b> always contains the current horizontal
   * and vertical coordinates of the mouse.
   */ 
  public int cursorX, cursorY, pcursorX, pcursorY;
  
  public static final String prettyVersion = "1.0.0";

	public static final String version = "1";
	
	public AbstractScene() {		
	  // E X C E P T I O N H A N D L I N G
	  startCoordCalls = 0;
	  timerHandler = new TimingHandler(this);		
		terseHandler = new TerseHandler();
		setMatrixHelper(new MatrixStackHelper(this));
		setDottedGrid(true);
		setRightHanded();
	}
	
	protected void setDrawingHelper(VisualHintable d) {
		drawingHelpler = d;
	}
		
	public VisualHintable drawingHelpler() {
		return drawingHelpler;
	}
	
	public void setMatrixHelper(MatrixHelpable r) {
		matrixHelper = r;
	}
		
	public MatrixHelpable matrixHelper() {
		return matrixHelper;
	}
	
	public boolean gridIsDotted() {
		return dottedGrid;
	}
	
	public void setDottedGrid(boolean dotted) {
		dottedGrid = dotted;
	}
	
	//FPSTiming wrappers
	
	public TimingHandler timerHandler() {
		return timerHandler;
	}
	
	public void registerJob(AbstractTimerJob job) {
		timerHandler().registerJob(job);
	}
	
	public void unregisterJob(AbstractTimerJob job) {
		timerHandler().unregisterJob(job);
	}
	
	public boolean isJobRegistered(AbstractTimerJob job) {
		return timerHandler().isJobRegistered(job);
	}
	
	public void registerAnimation(Animatable object) {
		timerHandler().registerAnimation(object);
	}	
	
	public void unregisterAnimation(Animatable object) {
		timerHandler().unregisterAnimation(object);
	}
	
	public boolean isAnimationRegistered(Animatable object) {
		return timerHandler().isAnimationRegistered(object);
	}
	
	/**
	 * Returns {@code true} if this Scene is associated to an offscreen 
	 * renderer and {@code false} otherwise.
	 */	
	public boolean isOffscreen() {
		return offscreen;
	}	
	
	// E V E N T   HA N D L I N G
	
  //TerseHandling
	
	public TerseHandler terseHandler() {
		return terseHandler;
	}
	
	public boolean grabsAnAgent(Grabbable g) {
		for( Agent agent : terseHandler().agents() ) {
			if (g.grabsAgent(agent))
					return true;
		}
		return false;
	}
	
	@Override
	public boolean grabsAgent(Agent agent) {
		return agent.grabber() == this;
	}
	
	@Override
	public boolean checkIfGrabsInput(TerseEvent event) {		
		return (event instanceof GenericKeyboardEvent || event instanceof GenericClickEvent);
	}
	
	/**
	 * Convenience function that simply calls {@code displayCurrentCameraProfileHelp(true)}.
	 * 
	 * @see #displayInfo(boolean)
	 */	
	public String info() {
		String description = new String();
		description += "Agents' info\n";
		int index = 1;
		for( Agent agent : terseHandler().agents() ) {
			description += index;
			description += ". ";
			description += agent.info();
			index++;
		}
		return description;
	}
	
	public void displayInfo() {
		displayInfo(true);
	}
	
	public void displayInfo(boolean onConsole) {
		if (onConsole)
			System.out.println(info());
		else
			AbstractScene.showMissingImplementationWarning("displayInfo", getClass().getName());
	}
	
	/**
	 * Internal method. Handles the different global keyboard actions.
	 */
	@Override
	public void performInteraction(TerseEvent event) {
		if( !(event instanceof GenericClickEvent) && ! (event instanceof GenericKeyboardEvent))
			return;
		
		Actionable<DandelionAction> a=null;
		
		if(event instanceof GenericClickEvent<?>)
		  a = (ClickAction) ((GenericClickEvent<?>) event).action();
		if(event instanceof GenericKeyboardEvent<?>)
			a =  (KeyboardAction) ((GenericKeyboardEvent<?>) event).action();		
		if(a == null) return;
		DandelionAction id = a.referenceAction();
		
		if( !id.is2D() && this.is2D() )
			return;
		
		execAction(id);
	}
	
	public void execAction(DandelionAction id) {
		Vec trans;
		switch (id) {
		case ADD_KEYFRAME_TO_PATH_1:
			eye().addKeyFrameToPath(1);
			break;
		case DELETE_PATH_1:
			eye().deletePath(1);
			break;
		case PLAY_PATH_1:
			eye().playPath(1);
			break;
		case ADD_KEYFRAME_TO_PATH_2:
			eye().addKeyFrameToPath(2);
			break;
		case DELETE_PATH_2:
			eye().deletePath(2);
			break;
		case PLAY_PATH_2:
			eye().playPath(2);
			break;
		case ADD_KEYFRAME_TO_PATH_3:
			eye().addKeyFrameToPath(3);
			break;
		case DELETE_PATH_3:
			eye().deletePath(3);
			break;
		case PLAY_PATH_3:
			eye().playPath(3);
			break;
		case DRAW_AXIS:
			toggleAxisVisualHint();
			break;
		case DRAW_GRID:
			toggleGridVisualHint();
			break;
		case CAMERA_TYPE:
			toggleCameraType();
			break;
		case ANIMATION:
			toggleAnimation();
			break;
		case GLOBAL_HELP:
			displayInfo();
			break;
		case EDIT_EYE_PATH:
			togglePathsVisualHint();
			break;
		case DRAW_FRAME_SELECTION_HINT:
			toggleFrameVisualhint();
			break;
		case SHOW_ALL:
			showAll();
			break;
		case MOVE_EYE_LEFT:
			trans = new Vec(-10.0f * eye().flySpeed(), 0.0f, 0.0f);
			if(this.is3D())
				trans.divide(camera().frame().magnitude());
			eye().frame().translate(eye().frame().inverseTransformOf(trans));			
			break;
		case MOVE_EYE_RIGHT:
			trans = new Vec(10.0f * eye().flySpeed(), 0.0f, 0.0f);
			if(this.is3D())
				trans.divide(camera().frame().magnitude());
			eye().frame().translate(eye().frame().inverseTransformOf(trans));			
			break;
		case MOVE_EYE_UP:
			trans = eye().frame().inverseTransformOf(new Vec(0.0f, isRightHanded() ? 10.0f : -10.0f * eye().flySpeed(), 0.0f));
			if(this.is3D())
				trans.divide(camera().frame().magnitude());
			eye().frame().translate(trans);					  
			break;
		case MOVE_EYE_DOWN:
			trans = eye().frame().inverseTransformOf(new Vec(0.0f, isRightHanded() ? -10.0f : 10.0f * eye().flySpeed(), 0.0f));
			if(this.is3D())
				trans.divide(camera().frame().magnitude());
			eye().frame().translate(trans);			
			break;
		case INCREASE_ROTATION_SENSITIVITY:
			eye().setRotationSensitivity(eye().rotationSensitivity() * 1.2f);
			break;
		case DECREASE_ROTATION_SENSITIVITY:
			eye().setRotationSensitivity(eye().rotationSensitivity() / 1.2f);
			break;
		case INCREASE_CAMERA_FLY_SPEED:
			((Camera) eye()).setFlySpeed(((Camera) eye()).flySpeed() * 1.2f);
			break;
		case DECREASE_CAMERA_FLY_SPEED:
			((Camera) eye()).setFlySpeed(((Camera) eye()).flySpeed() / 1.2f);
			break;
		case INCREASE_AVATAR_FLY_SPEED:
			if (avatar() != null)
				if (avatarIsInteractiveFrame)
					((InteractiveFrame) avatar()).setFlySpeed(((InteractiveFrame) avatar()).flySpeed() * 1.2f);
			break;
		case DECREASE_AVATAR_FLY_SPEED:
			if (avatar() != null)
				if (avatarIsInteractiveFrame)
					((InteractiveFrame) avatar()).setFlySpeed(((InteractiveFrame) avatar()).flySpeed() / 1.2f);
			break;
		case INCREASE_AZYMUTH:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar()).setAzimuth(((InteractiveAvatarFrame) avatar()).azimuth() + PI / 64);
			break;
		case DECREASE_AZYMUTH:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar()).setAzimuth(((InteractiveAvatarFrame) avatar()).azimuth() - PI / 64);
			break;
		case INCREASE_INCLINATION:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar()).setInclination(((InteractiveAvatarFrame) avatar()).inclination() + PI / 64);
			break;
		case DECREASE_INCLINATION:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar()).setInclination(((InteractiveAvatarFrame) avatar()).inclination() - PI / 64);
			break;
		case INCREASE_TRACKING_DISTANCE:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar()).setTrackingDistance(((InteractiveAvatarFrame) avatar()).trackingDistance() + radius() / 50);
			break;
		case DECREASE_TRACKING_DISTANCE:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar()).setTrackingDistance(((InteractiveAvatarFrame) avatar()).trackingDistance() - radius() / 50);
			break;
		case INTERPOLATE_TO_FIT:
			eye().interpolateToFitScene();
			break;
		case RESET_ARP:		  
			eye().setArcballReferencePoint(new Vec(0, 0, 0));
			//looks horrible, but works ;)
			eye().frame().arpFlag = true;
			eye().frame().timerFx.runOnce(1000);				
			break;
		case CUSTOM:
			AbstractScene.showMissingImplementationWarning(id, getClass().getName());
			break;
		default: 
			System.out.println("Action cannot be handled here!");
    break;
		}
	}
	
	/**
	 * Returns the {@link #width()} to {@link #height()} aspect ratio of
	 * the display window.
	 */
	public float aspectRatio() {
		return (float) width() / (float) height();
	}
	
	// D R A W I N G   M E T H O D S
	
	public void preDraw() {
		eye().validateScaling();
		if ( avatar() != null	&& (!eye().anyInterpolationIsStarted() ) ) {
			eye().setPosition(avatar().eyePosition());
			eye().setUpVector(avatar().upVector());
			eye().lookAt(avatar().target());
		}		
		bind();
		if (areBoundaryEquationsEnabled())
			eye().updateBoundaryEquations();
	}
	
	/**
	 * Internal method. Called by {@link remixlab.proscene.Scene#draw()}
	 * and {@link remixlab.proscene.Scene#endDraw()}.
	 * <p>
	 * First handles timing and parses events, then calls {@link #proscenium()}
	 * which is the main drawing method that could be overloaded. Then, handles
	 * external drawing methods (if any) and finally calls {@link #displayVisualHints()}.
	 * 
	 * @see #proscenium()
	 * @see #invokeDrawHandler()
	 * @see #gridVisualHint()
	 * @see #visualHints()
	 */
	public void postDraw() {			
		// 1
		//updateCursor();//TODO test if this is necessary
		// 2. timers
		timerHandler().handle();		
		// 3. Agents
		terseHandler().handle();		
	  // 4. Alternative use only
		proscenium();		
		// 6. Draw external registered method (only in java sub-classes)
		invokeDrawHandler(); // abstract
		// 7. Display visual hints
 		displayVisualHints(); // abstract
	}
	
	//protected abstract void updateCursor();
	
	protected abstract void invokeDrawHandler();
	
	/**
	 * Internal use. Display various on-screen visual hints to be called from {@link #pre()}
	 * or {@link #draw()}.
	 */
	
	protected void displayVisualHints() {
		if (gridVisualHint()) {
 			if(gridIsDotted())
 				drawDottedGrid(eye().sceneRadius());
 			else
 				drawGrid(eye().sceneRadius());
 		}
 		if (axisVisualHint())
 			drawAxis(eye().sceneRadius());
 		
		if (frameVisualHint())
			drawSelectionHints();
		if (pathsVisualHint()) {
			eye().drawAllPaths();
			drawEyePathsSelectionHints();
		} else {
			eye().hideAllPaths();
		}
		
		if (zoomVisualHint())
			drawZoomWindowHint();
		if (rotateVisualHint())
			drawScreenRotateLineHint();		
		
		if (eye().frame().arpFlag) 
			drawArcballReferencePointHint();		
		if (eye().frame().pupFlag) {
			drawPointUnderPixelHint();
		}
	}
	
  //1. Scene overloaded
	
	/**
	 * This method is called before the first drawing happen and should be overloaded to
	 * initialize stuff. The default implementation is empty.
	 * <p>
	 * Typical usage include {@link #eye()} initialization ({@link #showAll()})
	 * and Scene state setup ({@link #setAxisVisualHint(boolean)} and
	 * {@link #setGridVisualHint(boolean)}.
	 */
	public void init() {}
	
	/**
	 * The method that actually defines the scene.
	 * <p>
	 * If you build a class that inherits from Scene, this is the method you
	 * should overload, but no if you instantiate your own Scene object (for instance,
	 * in Processing you should just overload {@code PApplet.draw()} to define your scene).
	 * <p>
	 * The eye matrices set in {@link #bind()} converts from the world to the
	 * camera coordinate systems. Thus vertices given here can then be considered as
	 * being given in the world coordinate system. The eye is
	 * moved in this world using the mouse. This representation is much more
	 * intuitive than a camera-centric system (which for instance is the standard
	 * in OpenGL).
	 */
	public void proscenium() {}
	
	public boolean is2D() {
		return !is3D();
	}
	
	public abstract boolean is3D();
		
	// WRAPPERS
	
	/**
	 * Bind processing matrices to proscene matrices.
	 */	
	protected void bind() {
		matrixHelper.bind();
	}
	
	/**
	 * Push a copy of the modelview matrix onto the stack.
   */
	public void pushModelView() {
		matrixHelper.pushModelView();
	}
	
	/**
	 * Replace the current modelview matrix with the top of the stack.
	 */
	public void popModelView() {
		matrixHelper.popModelView();
	}
	
	/**
	 * Push a copy of the projection matrix onto the stack.
   */
	public void pushProjection() {
		matrixHelper.pushProjection();
	}
	
	/**
	 * Replace the current projection matrix with the top of the stack.
	 */
	public void popProjection() {
		matrixHelper.popProjection();
	}
	
  /**
   * Translate in X and Y.
   */
  public void translate(float tx, float ty) {    
  	matrixHelper.translate(tx, ty);
  }

  /**
   * Translate in X, Y, and Z.
   */
  public void translate(float tx, float ty, float tz) {
  	matrixHelper.translate(tx, ty, tz);
  }

  /**
   * Two dimensional rotation.
   *
   * Same as rotateZ (this is identical to a 3D rotation along the z-axis)
   * but included for clarity. It'd be weird for people drawing 2D graphics
   * to be using rotateZ. And they might kick our a-- for the confusion.
   *
   * <A HREF="http://www.xkcd.com/c184.html">Additional background</A>.
   */
  public void rotate(float angle) {
  	matrixHelper.rotate(angle);
  }

  /**
   * Rotate around the X axis.
   */
  public void rotateX(float angle) { 
  	matrixHelper.rotateX(angle);
  }

  /**
   * Rotate around the Y axis.
   */
  public void rotateY(float angle) {
  	matrixHelper.rotateY(angle);
  }

  /**
   * Rotate around the Z axis.
   *
   * The functions rotate() and rotateZ() are identical, it's just that it make
   * sense to have rotate() and then rotateX() and rotateY() when using 3D;
   * nor does it make sense to use a function called rotateZ() if you're only
   * doing things in 2D. so we just decided to have them both be the same.
   */
  public void rotateZ(float angle) {
  	matrixHelper.rotateZ(angle);
  }

  /**
   * Rotate about a vector in space. Same as the glRotatef() function.
   */
  public void rotate(float angle, float vx, float vy, float vz) {
  	matrixHelper.rotate(angle, vx, vy, vz);
  }

  /**
   * Scale in all dimensions.
   */
  public void scale(float s) {
  	matrixHelper.scale(s);
  }

  /**
   * Scale in X and Y. Equivalent to scale(sx, sy, 1).
   *
   * Not recommended for use in 3D, because the z-dimension is just
   * scaled by 1, since there's no way to know what else to scale it by.
   */
  public void scale(float sx, float sy) {
  	matrixHelper.scale(sx, sy);
  }

  /**
   * Scale in X, Y, and Z.
   */
  public void scale(float x, float y, float z) {
  	matrixHelper.scale(x, y, z);
  }  
  
  /**
   * Set the current modelview matrix to identity.
   */
  public void resetModelView() {
  	matrixHelper.resetModelView();
  }
  
  /**
   * Set the current projection matrix to identity.
   */
  public void resetProjection() {
  	matrixHelper.resetProjection();
  }  
  
  public void applyModelView(Mat source) {
  	matrixHelper.applyModelView(source);
  }
  
  public void applyProjection(Mat source) {
  	matrixHelper.applyProjection(source);
  }

  /**
   * Apply a 4x4 modelview matrix.
   */
  public void applyModelViewRowMajorOrder(float n00, float n01, float n02, float n03,
                                       float n10, float n11, float n12, float n13,
                                       float n20, float n21, float n22, float n23,
                                       float n30, float n31, float n32, float n33) {    
  	matrixHelper.applyModelViewRowMajorOrder(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
  }
  
  /**
   * Apply a 4x4 projection matrix.
   */
  public void applyProjectionRowMajorOrder(float n00, float n01, float n02, float n03,
                                       float n10, float n11, float n12, float n13,
                                       float n20, float n21, float n22, float n23,
                                       float n30, float n31, float n32, float n33) {    
  	matrixHelper.applyProjectionRowMajorOrder(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
  }
  
  /**
  public void frustum(float left, float right, float bottom, float top, float znear, float zfar) {
  	renderer.frustum(left, right, bottom, top, znear, zfar);
  }
  */

  public Mat modelView() {
  	return matrixHelper.modelView();
  }
  
  public Mat projection() {
  	return matrixHelper.projection();
  }

  /**
   * Copy the current modelview matrix into the specified target.
   * Pass in null to create a new matrix.
   */
  public Mat getModelView(Mat target) {
  	return matrixHelper.getModelView(target);
  }
  
  /**
   * Copy the current projection matrix into the specified target.
   * Pass in null to create a new matrix.
   */
  public Mat getProjection(Mat target) {
  	return matrixHelper.getProjection(target);
  }

  /**
   * Set the current modelview matrix to the contents of another.
   */
  public void setModelView(Mat source) {
  	matrixHelper.setModelView(source);
  }
  
  /**
   * Set the current projection matrix to the contents of another.
   */
  public void setProjection(Mat source) {
  	matrixHelper.setProjection(source);
  }

  /**
   * Print the current modelview matrix.
   */
  public void printModelView() {
  	matrixHelper.printModelView();
  }
  
  /**
   * Print the current projection matrix.
   */
  public void printProjection() {
  	matrixHelper.printProjection();
  }
  
  /**
	 * Draws a cylinder of width {@code w} and height {@code h}, along the 
	 * positive {@code z} axis. 
	 */
  public void cylinder(float w, float h) {
  	drawingHelpler().cylinder(w, h);
  }
  
  public void cone(int detail, float x, float y, float r, float h) {
  	drawingHelpler().cone(detail, x, y, r, h);
  }
  
  public void cone(int detail, float x, float y, float r1, float r2, float h) {
  	drawingHelpler().cone(detail, x, y, r1, r2, h);
  }
  
  public void drawAxis(float length) {
  	drawingHelpler().drawAxis(length);
  }
  
  public void drawGrid(float size, int nbSubdivisions) {
  	drawingHelpler().drawGrid(size, nbSubdivisions);
  }
  
  public void drawDottedGrid(float size, int nbSubdivisions) {
  	drawingHelpler().drawDottedGrid(size, nbSubdivisions);
  }
  
  public void drawEye(Eye eye, float scale) {
  	drawingHelpler().drawEye(eye, scale);
  }
  
  /* not needed
  public void drawKeyFrame(float scale) {
  	drawingHelpler().drawKeyFrame(scale);
  }
  */
  
  public void drawZoomWindowHint() {
  	drawingHelpler().drawZoomWindowHint();
  }
  
  public void drawScreenRotateLineHint() {
  	drawingHelpler().drawScreenRotateLineHint();
  }
  
  public void drawArcballReferencePointHint() {
  	drawingHelpler().drawArcballReferencePointHint();
  }
  
  public void drawPointUnderPixelHint() {
  	drawingHelpler().drawPointUnderPixelHint();
  }
  
  public void drawCross(float px, float py, float size) {
  	drawingHelpler().drawCross(px, py, size);
  }
  
  public void drawFilledCircle(int subdivisions, Vec center, float radius) {
  	drawingHelpler().drawFilledCircle(subdivisions, center, radius);
  }
  
  public void drawFilledSquare(Vec center, float edge) {
  	drawingHelpler().drawFilledSquare(center, edge);
  }
  
  public void drawShooterTarget(Vec center, float length) {
  	drawingHelpler().drawShooterTarget(center, length);
	}
	
	public void drawPath(KeyFrameInterpolator kfi, int mask, int nbFrames, float scale) {
		drawingHelpler().drawPath(kfi, mask, nbFrames, scale);
	}
	
//Abstract drawing methods
	
	/**
	 * Same as {@code cone(det, 0, 0, r, h);}
	 * 
	 * @see #cone(int, float, float, float, float)
	 */
	public void cone(int det, float r, float h) {
		cone(det, 0, 0, r, h);
	}		
	
	/**
	 * Same as {@code cone(12, 0, 0, r, h);}
	 * 
	 * @see #cone(int, float, float, float, float)
	 */
	public void cone(float r, float h) {
		cone(12, 0, 0, r, h);
	}	
	
	/**
	 * Same as {@code cone(det, 0, 0, r1, r2, h);}
	 * 
	 * @see #cone(int, float, float, float, float, float)
	 */
	public void cone(int det, float r1, float r2, float h) {
		cone(det, 0, 0, r1, r2, h);
	}	
	
	/**
	 * Same as {@code cone(18, 0, 0, r1, r2, h);}
	 * 
	 * @see #cone(int, float, float, float, float, float)
	 */
	public void cone(float r1, float r2, float h) {
		cone(18, 0, 0, r1, r2, h);
	}	
	
	/**
	 * Convenience function that simply calls {@code drawAxis(100)}.
	 */
	public void drawAxis() {
		drawAxis(100);
	}	
	
	/**
	 * Simply calls {@code drawArrow(length, 0.05f * length)}
	 * 
	 * @see #drawArrow(float, float)
	 */
	public void drawArrow(float length) {
		drawArrow(length, 0.05f * length);
	}
	
	/**
	 * Draws a 3D arrow along the positive Z axis.
	 * <p>
	 * {@code length} and {@code radius} define its geometry.
	 * <p>
	 * Use {@link #drawArrow(Vec, Vec, float)} to place the arrow
	 * in 3D.
	 */
	public void drawArrow(float length, float radius) {
		float head = 2.5f * (radius / length) + 0.1f;
		float coneRadiusCoef = 4.0f - 5.0f * head;

		cylinder(radius, length * (1.0f - head / coneRadiusCoef));
		translate(0.0f, 0.0f, length * (1.0f - head));
		cone(coneRadiusCoef * radius, head * length);
		translate(0.0f, 0.0f, -length * (1.0f - head));
	}
	
	/**
	 * Draws a 3D arrow between the 3D point {@code from} and the 3D point {@code
	 * to}, both defined in the current world coordinate system.
	 * 
	 * @see #drawArrow(float, float)
	 */
	public void drawArrow(Vec from, Vec to,	float radius) {
		pushModelView();
		translate(from.x(), from.y(), from.z());
		applyModelView(new Quat(new Vec(0, 0, 1), Vec.subtract(to,	from)).matrix());
		drawArrow(Vec.subtract(to, from).magnitude(), radius);
		popModelView();
	}
	
	public void drawDottedGrid() {
		drawDottedGrid(100, 10);
	}
	
	/**
	 * Convenience function that simply calls {@code drawGrid(100, 10)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public void drawGrid() {
		drawGrid(100, 10);
	}
	
	public void drawDottedGrid(float size) {
		drawDottedGrid(size, 10);
	}
		
	/**
	 * Convenience function that simply calls {@code drawGrid(size, 10)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public void drawGrid(float size) {
		drawGrid(size, 10);
	}
	
	public void drawDottedGrid(int nbSubdivisions) {
		drawDottedGrid(100, nbSubdivisions);
	}
	
	/**
	 * Convenience function that simply calls {@code drawGrid(100, nbSubdivisions)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public void drawGrid(int nbSubdivisions) {
		drawGrid(100, nbSubdivisions);
	}
	
	/**
	 * Convenience function that simply calls {@code drawEye(eye, 1)}.
	 */
	public void drawEye(Eye eye) {
		drawEye(eye, 1);
	}
		
	/**
	 * Draws all InteractiveFrames' selection regions: a shooter target
	 * visual hint of {@link remixlab.dandelion.core.InteractiveFrame#grabsInputThreshold()} pixels size.
	 * 
	 * <b>Attention:</b> If the InteractiveFrame is part of a Camera path draws
	 * nothing.
	 * 
	 * @see #drawEyePathsSelectionHints()
	 */
	protected abstract void drawSelectionHints();
	
	/**
	 * Draws the selection regions (a shooter target visual hint of
	 * {@link remixlab.dandelion.core.InteractiveFrame#grabsInputThreshold()} pixels size) of all
	 * InteractiveFrames forming part of the Camera paths.
	 * 
	 * @see #drawSelectionHints()
	 */
	protected abstract void drawEyePathsSelectionHints();
		
	/**
	 * Convenience function that simply calls
	 * {@code drawCross(pg3d.color(255, 255, 255), px, py, 15, 3)}.
	 */
	public void drawCross(float px, float py) {
		drawCross(px, py, 15);
	}
		
	/**
	 * Convenience function that simply calls
	 * {@code drawFilledCircle(40, center, radius)}.
	 * 
	 * @see #drawFilledCircle(int, Vec, float)
	 */
	public void drawFilledCircle(Vec center, float radius) {
		drawFilledCircle(40, center, radius);
	}
	
  public void beginScreenDrawing() {
  	if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
							                 + "endScreenDrawing() and they cannot be nested. Check your implementation!");
		
		startCoordCalls++;
		
		disableDepthTest();
		matrixHelper.beginScreenDrawing();
  }
	
	public void endScreenDrawing() {
		startCoordCalls--;
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
							                 + "endScreenDrawing() and they cannot be nested. Check your implementation!");
		
		matrixHelper.endScreenDrawing();
		enableDepthTest();
	}
	
	public abstract void disableDepthTest();
	
	public abstract void enableDepthTest();
	
	// end wrapper
	
	public boolean isLeftHanded() {
		return !rightHanded;
	}
	
	public boolean isRightHanded() {
		return rightHanded;
	}
	
	public void setRightHanded() {
		rightHanded = true;
	}
	
	public void setLeftHanded() {
		rightHanded = false;
	}	
	
	// 0. Optimization stuff
	
	// TODO fix documentation
	/**
	 * Apply the transformation defined by {@code frame}.
	 * The Frame is first translated and then rotated around the new translated origin.
	 * <p>
	 * Same as:
	 * <p>
	 * {@code renderer().translate(translation().x, translation().y, translation().z);} <br>
	 * {@code renderer().rotate(rotation().angle(), rotation().axis().x,
	 * rotation().axis().y, rotation().axis().z);} <br>
	 * <p>
	 * This method may be used to modify the modelview matrix from a Frame hierarchy.
	 * For example, with this Frame hierarchy:
	 * <p>
	 * {@code Frame body = new Frame();} <br>
	 * {@code Frame leftArm = new Frame();} <br>
	 * {@code Frame rightArm = new Frame();} <br>
	 * {@code leftArm.setReferenceFrame(body);} <br>
	 * {@code rightArm.setReferenceFrame(body);} <br>
	 * <p>
	 * The associated processing drawing code should look like:
	 * <p>
	 * {@code pushMatrix();} <br>
	 * {@code applyTransformation(body);} <br>
	 * {@code drawBody();} <br>
	 * {@code pushMatrix();} <br>
	 * {@code applyTransformation(leftArm);} <br>
	 * {@code drawArm();} <br>
	 * {@code popMatrix();} <br>
	 * {@code pushMatrix();} <br>
	 * {@code applyTransformation(rightArm);} <br>
	 * {@code drawArm();} <br>
	 * {@code popMatrix();} <br>
	 * {@code popMatrix();} <br>
	 * <p>
	 * If the frame hierarchy to be drawn should be applied to a different renderer
	 * context than main one (e.g., an off-screen rendering context), you may
	 * call {@code renderer().pushMatrix();} and {@code renderer().popMatrix();} above.
	 * <p> 
	 * Note the use of nested {@code pushMatrix()} and {@code popMatrix()} blocks
	 * to represent the frame hierarchy: {@code leftArm} and {@code rightArm} are
	 * both correctly drawn with respect to the {@code body} coordinate system.
	 * <p>
	 * <b>Attention:</b> When drawing a frame hierarchy as above, this method
	 * should be used whenever possible.
	 */
	public void applyTransformation(Frame frame) {
		if( is2D() ) {
			translate(frame.translation().x(), frame.translation().y());
			rotate(frame.rotation().angle());
			scale(frame.scaling().x(), frame.scaling().y());
		}
		else {
			translate( frame.translation().vec[0], frame.translation().vec[1], frame.translation().vec[2] );
			rotate( frame.rotation().angle(), ((Quat)frame.rotation()).axis().vec[0], ((Quat)frame.rotation()).axis().vec[1], ((Quat)frame.rotation()).axis().vec[2]);
		  scale(frame.scaling().x(), frame.scaling().y(), frame.scaling().z());
		}
	}
	
	public void applyWorldTransformation(Frame frame) {
		Frame refFrame = frame.referenceFrame();
		if(refFrame != null) {
			applyWorldTransformation(refFrame);
			applyTransformation(frame);
		}
		else {
			applyTransformation(frame);
		}
	}	

	//public abstract long frameCount();
	
	// 1. Associated objects
	
	/**
	 * Returns the associated Eye, never {@code null}.
	 */
	public Eye eye() {
		return eye;
	}
	
	/**
	 * Returns the associated Camera, never {@code null}.
	 */
	public Camera camera() {
		if (this.is3D())
			return (Camera) eye;
		else 
			throw new RuntimeException("Camera type is only available in 3D");
	}
	
	public void camera(Camera cam) {
		if( this.is2D() ) {
			System.out.println("Warning: Camera Type is only available in 3D");			
		}
		else
			setEye(cam);		
	}
	
	public Window window() {
		if (this.is2D())
			return (Window) eye;
		else 
			throw new RuntimeException("Window type is only available in 2D");
	}
	
	public void window(Window win) {
		if( this.is3D() ) {
			System.out.println("Warning: Window Type is only available in 2D");			
		}
		else
			setEye(win);
	}

	/**
	 * Replaces the current {@link #eye()} with {@code vp}
	 */
	public void setEye(Eye vp)  {
		if (vp == null)
			return;

		vp.setSceneRadius(radius());		
		vp.setSceneCenter(center());

		vp.setScreenWidthAndHeight(width(), height());
    
		eye = vp;
		
		for( Agent agent : terseHandler().agents() ) {
			if( agent instanceof GenericWheeledBiMotionAgent )
				agent.setDefaultGrabber(eye.frame());
		}		

		showAll();
	}
	
	/**
	 * Returns {@code true} if automatic update of the camera frustum plane
	 * equations is enabled and {@code false} otherwise. Computation of the
	 * equations is expensive and hence is disabled by default.
	 * 
	 * @see #toggleBoundaryEquations()
	 * @see #disableBoundaryEquations()
	 * @see #enableBoundaryEquations()
	 * @see #enableBoundaryEquations(boolean)
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public boolean areBoundaryEquationsEnabled() {
		return eye().areBoundaryEquationsEnabled();
	}

	/**
	 * Toggles automatic update of the camera frustum plane equations every frame.
	 * Computation of the equations is expensive and hence is disabled by default.
	 * 
	 * @see #areBoundaryEquationsEnabled()
	 * @see #disableBoundaryEquations()
	 * @see #enableBoundaryEquations()
	 * @see #enableBoundaryEquations(boolean)
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public void toggleBoundaryEquations() {
		if ( areBoundaryEquationsEnabled() )
			disableBoundaryEquations();
		else
			enableBoundaryEquations();
	}

	/**
	 * Disables automatic update of the camera frustum plane equations every
	 * frame. Computation of the equations is expensive and hence is disabled by
	 * default.
	 * 
	 * @see #areBoundaryEquationsEnabled()
	 * @see #toggleBoundaryEquations()
	 * @see #enableBoundaryEquations()
	 * @see #enableBoundaryEquations(boolean)
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public void disableBoundaryEquations() {
		enableBoundaryEquations(false);
	}

	/**
	 * Enables automatic update of the camera frustum plane equations every frame.
	 * Computation of the equations is expensive and hence is disabled by default.
	 * 
	 * @see #areBoundaryEquationsEnabled()
	 * @see #toggleBoundaryEquations()
	 * @see #disableBoundaryEquations()
	 * @see #enableBoundaryEquations(boolean)
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public void enableBoundaryEquations() {
		enableBoundaryEquations(true);
	}

	/**
	 * Enables or disables automatic update of the camera frustum plane equations
	 * every frame according to {@code flag}. Computation of the equations is
	 * expensive and hence is disabled by default.
	 * 
	 * @see #areBoundaryEquationsEnabled()
	 * @see #toggleBoundaryEquations()
	 * @see #disableBoundaryEquations()
	 * @see #enableBoundaryEquations()
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public void enableBoundaryEquations(boolean flag) {
		eye().enableBoundaryEquations(flag);
	}
	
	/**
	 * Toggles the {@link #eye()} type between PERSPECTIVE and ORTHOGRAPHIC.
	 */
	public void toggleCameraType() {
		if( this.is2D() ) {
			System.out.println("Warning: Camera Type is only available in 3D");
		}
		else {
		if (((Camera) eye()).type() == Camera.Type.PERSPECTIVE)
			setCameraType(Camera.Type.ORTHOGRAPHIC);
		else
			setCameraType(Camera.Type.PERSPECTIVE);
		}
	}
	
	/**
	 * Returns the current {@link #eye()} type.
	 */
	public final Camera.Type cameraType() {
		if( this.is2D() ) {
			System.out.println("Warning: Camera Type is only available in 3D");
			return null;
		}
		else
			return ((Camera) eye()).type();		
	}

	/**
	 * Sets the {@link #eye()} type.
	 */
	public void setCameraType(Camera.Type type) {
		if( this.is2D() ) {
			System.out.println("Warning: Camera Type is only available in 3D");			
		}
		else
			if (type != ((Camera) eye()).type())
				((Camera) eye()).setType(type);
	}
	
	/**
	 * Returns {@code true} if a mouse moved event  is called even when no mouse button is pressed.
	 * <p>
	 * You need to {@link #setDeviceTracking(boolean)} to {@code true} in order to use MouseGrabber
	 * (see {@link #deviceGrabber()}).
	 */
	/**
	public boolean isTrackingDevice() {
		return deviceTrckn;
	}
	*/
	
	/**
	 * Sets the {@link #isTrackingDevice()} value.
	 */
	/**
	public void setDeviceTracking(boolean enable) {		
		if(!enable) {
			if( deviceGrabber() != null )
				deviceGrabber().setGrabsInput(false);
			setDeviceGrabber(null);
		}
		deviceTrckn = enable;
	}
	*/
	
	/**
	 * Calls {@link #setDeviceTracking(boolean)} to toggle the {@link #isTrackingDevice()} value.
	 */
	/**
	public void toggleDeviceTracking() {
		setDeviceTracking(!isTrackingDevice());
	}
	*/
	
  //2. Associated objects
	
	/**
	 * Returns the avatar object to be tracked by the Camera when it
	 * is in Third Person mode.
	 * <p>
	 * Simply returns {@code null} if no avatar has been set.
	 */
	public Trackable avatar() {
		return trck;
	}

	/**
	 * Sets the avatar object to be tracked by the Camera when it is in Third
	 * Person mode.
	 * 
	 * @see #unsetAvatar()
	 */
	public void setAvatar(Trackable t) {
		trck = t;
		avatarIsInteractiveAvatarFrame = false;
		avatarIsInteractiveFrame = false;
		if (avatar() == null)
			return;			
		if (avatar() instanceof InteractiveFrame) {
			avatarIsInteractiveFrame = true;
			if (avatar() instanceof InteractiveAvatarFrame)
				avatarIsInteractiveAvatarFrame = true;
			eye().frame().updateFlyUpVector();// ?
			eye().frame().stopSpinning();
			if( this.avatarIsInteractiveFrame ) {
				((InteractiveFrame) (avatar())).updateFlyUpVector();
				((InteractiveFrame) (avatar())).stopSpinning();
			}
			// perform small animation ;)
			if (eye().anyInterpolationIsStarted())
				eye().stopAllInterpolations();
			Eye cm = eye().get();
			cm.setPosition(avatar().eyePosition());
			cm.setUpVector(avatar().upVector());
			cm.lookAt(avatar().target());
			eye().interpolateTo(cm.frame());
		}
	}

	/**
	 * If there's a avatar unset it.
	 * 
	 * @see #setAvatar(Trackable)
	 */
	public void unsetAvatar() {
		trck = null;
		avatarIsInteractiveAvatarFrame = false;
		avatarIsInteractiveFrame = false;
	}
	
	// 3. Scene dimensions

	/**
	 * Returns the scene radius.
	 * <p>
	 * Convenience wrapper function that simply calls {@code
	 * camera().sceneRadius()}
	 * 
	 * @see #setRadius(float)
	 * @see #center()
	 */
	public float radius() {
		return eye().sceneRadius();
	}

	/**
	 * Returns the scene center.
	 * <p>
	 * Convenience wrapper function that simply returns {@code
	 * camera().sceneCenter()}
	 * 
	 * @see #setCenter(Vec) {@link #radius()}
	 */
	public Vec center() {
		return eye().sceneCenter();
	}

	/**
	 * Returns the arcball reference point.
	 * <p>
	 * Convenience wrapper function that simply returns {@code
	 * camera().arcballReferencePoint()}
	 * 
	 * @see #setCenter(Vec) {@link #radius()}
	 */
	public Vec arcballReferencePoint() {
		return eye().arcballReferencePoint();
	}

	/**
	 * Sets the {@link #radius()} of the Scene.
	 * <p>
	 * Convenience wrapper function that simply returns {@code
	 * camera().setSceneRadius(radius)}
	 * 
	 * @see #setCenter(Vec)
	 */
	public void setRadius(float radius) {
		eye().setSceneRadius(radius);
	}

	/**
	 * Sets the {@link #center()} of the Scene.
	 * <p>
	 * Convenience wrapper function that simply calls {@code }
	 * 
	 * @see #setRadius(float)
	 */
	public void setCenter(Vec center) {
		eye().setSceneCenter(center);
	}

	/**
	 * Sets the {@link #center()} and {@link #radius()} of the Scene from the
	 * {@code min} and {@code max} vectors.
	 * <p>
	 * Convenience wrapper function that simply calls {@code
	 * camera().setSceneBoundingBox(min,max)}
	 * 
	 * @see #setRadius(float)
	 * @see #setCenter(Vec)
	 */
	public void setBoundingBox(Vec min, Vec max) {
		if( this.is2D() )
			System.out.println("setBoundingBox is available only in 3D. Use setBoundingRect instead");
		else
			((Camera) eye()).setSceneBoundingBox(min, max);
	}
	
	public void setBoundingRect(Vec min, Vec max) {
		if( this.is3D() )
			System.out.println("setBoundingRect is available only in 2D. Use setBoundingBox instead");
		else
			((Window) eye()).setSceneBoundingBox(min, max);
	}

	/**
	 * Convenience wrapper function that simply calls {@code
	 * camera().showEntireScene()}
	 * 
	 * @see remixlab.dandelion.core.Camera#showEntireScene()
	 */
	public void showAll() {
		eye().showEntireScene();
	}

	/**
	 * Convenience wrapper function that simply returns {@code
	 * camera().setArcballReferencePointFromPixel(pixel)}.
	 * <p>
	 * Current implementation set no
	 * {@link remixlab.dandelion.core.Camera#arcballReferencePoint()}. Override
	 * {@link remixlab.dandelion.core.Camera#pointUnderPixel(Point)} in your openGL
	 * based camera for this to work.
	 * 
	 * @see remixlab.dandelion.core.Camera#setArcballReferencePointFromPixel(Point)
	 * @see remixlab.dandelion.core.Camera#pointUnderPixel(Point)
	 */
	public boolean setArcballReferencePointFromPixel(Point pixel) {
		return eye().setArcballReferencePointFromPixel(pixel);
	}
	
	/**
	 * Convenience wrapper function that simply returns {@code
	 * camera().setSceneCenterFromPixel(pixel)}
	 * <p>
	 * Current implementation set no
	 * {@link remixlab.dandelion.core.Camera#sceneCenter()}. Override
	 * {@link remixlab.dandelion.core.Camera#pointUnderPixel(Point)} in your openGL
	 * based camera for this to work.
	 * 
	 * @see remixlab.dandelion.core.Camera#setSceneCenterFromPixel(Point)
	 * @see remixlab.dandelion.core.Camera#pointUnderPixel(Point)
	 */
	public boolean setCenterFromPixel(Point pixel) {
		return eye().setSceneCenterFromPixel(pixel);
	}
	
	// Control what is drawing
	
	/**
	 * Returns the visual hints flag.
	 */
	public int visualHints() {
		return this.visualHintFlag;
	}
	
	/**
	 * Low level setting of visual flags. You'd prefer {@link #setAxisVisualHint(boolean)},
	 * {@link #setGridVisualHint(boolean)}, {@link #setPathsVisualHint(boolean)} and
	 * {@link #setFrameVisualHint(boolean)}, unless you want to set them all at once,
	 * e.g., {@code setVisualHints(Constants.AXIS | Constants.GRID | Constants.PATHS | Constants.FRAME)}.
	 */
	public void setVisualHints(int flag) {
		visualHintFlag = flag;
	}
	
	/**
	 * Toggles the state of {@link #axisVisualHint()}.
	 * 
	 * @see #axisVisualHint()
	 * @see #setAxisVisualHint(boolean)
	 */
	public void toggleAxisVisualHint() {
		setAxisVisualHint(!axisVisualHint());
	}

	/**
	 * Toggles the state of {@link #gridVisualHint()}.
	 * 
	 * @see #setGridVisualHint(boolean)
	 */
	public void toggleGridVisualHint() {
		setGridVisualHint(!gridVisualHint());
	}

	/**
	 * Toggles the state of {@link #frameVisualHint()}.
	 * 
	 * @see #setFrameVisualHint(boolean)
	 */
	public void toggleFrameVisualhint() {
		setFrameVisualHint(!frameVisualHint());
	}

	/**
	 * Toggles the state of {@link #pathsVisualHint()}.
	 * 
	 * @see #setPathsVisualHint(boolean)
	 */
	public void togglePathsVisualHint() {
		setPathsVisualHint(!pathsVisualHint());
	}
	
	/**
	 * Internal :p
	 */
	public void toggleZoomVisualHint() {
		setZoomVisualHint(!zoomVisualHint());
	}
	
	/**
	 * Internal :p
	 */
	public void toggleRotateVisualHint() {
		setRotateVisualHint(!rotateVisualHint());
	}
	
	/**
	 * Returns {@code true} if axis is currently being drawn and {@code false}
	 * otherwise.
	 */
	public boolean axisVisualHint() {
		return ((visualHintFlag & AXIS) != 0);
	}

	/**
	 * Returns {@code true} if grid is currently being drawn and {@code false}
	 * otherwise.
	 */
	public boolean gridVisualHint() {
		return ((visualHintFlag & GRID) != 0);
	}

	/**
	 * Returns {@code true} if the frames selection visual hints are currently
	 * being drawn and {@code false} otherwise.
	 */
	public boolean frameVisualHint() {
		return ((visualHintFlag & FRAME) != 0);
	}

	/**
	 * Returns {@code true} if the eye pads visual hints are currently
	 * being drawn and {@code false} otherwise.
	 */
	public boolean pathsVisualHint() {
		return ((visualHintFlag & PATHS) != 0);
	}
	
	/**
	 * Internal :p
	 */
	protected boolean zoomVisualHint() {
		return ((visualHintFlag & ZOOM) != 0);
	}
	
	/**
	 * Internal :p
	 */
	protected boolean rotateVisualHint() {
		return ((visualHintFlag & ROTATE) != 0);
	}

	/**
	 * Sets the display of the axis according to {@code draw}
	 */
	public void setAxisVisualHint(boolean draw) {
		if(draw) visualHintFlag |= AXIS; else visualHintFlag &= ~AXIS;
	}

	/**
	 * Sets the display of the grid according to {@code draw}
	 */
	public void setGridVisualHint(boolean draw) {
		if(draw) visualHintFlag |= GRID; else visualHintFlag &= ~GRID;
	}

	/**
	 * Sets the display of the interactive frames' selection hints according to
	 * {@code draw}
	 */
	public void setFrameVisualHint(boolean draw) {
		if(draw) visualHintFlag |= FRAME; else visualHintFlag &= ~FRAME;
	}

	/**
	 * Sets the display of the camera key frame paths according to {@code draw}
	 */
	public void setPathsVisualHint(boolean draw) {
		if(draw) visualHintFlag |= PATHS; else visualHintFlag &= ~PATHS;
	}
	
	/**
	 * Internal :p
	 */
	protected void setZoomVisualHint(boolean draw) {
		if(draw) visualHintFlag |= ZOOM; else visualHintFlag &= ~ZOOM;
	}
	
	/**
	 * Internal :p
	 */
	protected void setRotateVisualHint(boolean draw) {
		if(draw) visualHintFlag |= ROTATE; else visualHintFlag &= ~ROTATE;
	}
	
	protected abstract Camera.WorldPoint pointUnderPixel(Point pixel);
	
  //dimensions
  public abstract int width();
  
  public abstract int height();
  
  public Vec projectedCoordinatesOf(Vec src) {
  	return eye().projectedCoordinatesOf(this.matrixHelper().projectionView(), src);
  }
  
  public Vec unprojectedCoordinatesOf(Vec src) {
  	if( this.matrixHelper().unprojectCacheIsOptimized() )
  		return eye().unprojectedCoordinatesOf(this.matrixHelper().projectionViewInverse(), src);
  	else
  		return eye().unprojectedCoordinatesOf(src);
  }

  // WARNINGS and EXCEPTIONS
     
  static protected HashMap<String, Object> warnings;

  /**
   * Show warning, and keep track of it so that it's only shown once.
   * @param msg the error message (which will be stored for later comparison)
   */
  static public void showWarning(String msg) {  // ignore
    if (warnings == null) {
      warnings = new HashMap<String, Object>();
    }
    if (!warnings.containsKey(msg)) {
      System.err.println(msg);
      warnings.put(msg, new Object());
    }
  }
  
  /**
   * Display a warning that the specified method is only available with 3D.
   * @param method The method name (no parentheses)
   */
  static public void showDepthWarning(String method) {
    showWarning(method + "() is not available in 2D.");
  }
  
  static public void showDepthWarning(DandelionAction action) {
    showWarning(action.name() + " is not available in 2D.");
  }

  /**
   * Display a warning that the specified method lacks implementation.
   */
  static public void showMissingImplementationWarning(String method, String theclass) {
    showWarning(method + "(), should be implemented by your " + theclass + " derived class.");
  }
  
  static public void showMissingImplementationWarning(DandelionAction action, String theclass) {
    showWarning(action.name() + " should be implemented by your " + theclass + " derived class.");
  }
  
  static public void showEventVariationWarning(DandelionAction action) {
    showWarning(action.name() + " can only be performed using a relative event.");
  }
  
  static public void showOnlyEyeWarning(DandelionAction action) {
    showWarning(action.name() + " can only be performed by the eye (frame).");
  }
}
