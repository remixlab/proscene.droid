/*******************************************************************************
 * dandelion_tree (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package remixlab.dandelion.core;

import remixlab.bias.event.DOF2Event;
import remixlab.bias.generic.event.*;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.TimerJob;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;
import remixlab.util.Util;

/**
 * The InteractiveEyeFrame class represents an InteractiveFrame with Camera specific mouse bindings.
 * <p>
 * An InteractiveEyeFrame is a specialization of an InteractiveFrame (hence it can "fly" in the Scene), designed to be
 * set as the {@link Eye#frame()}. Mouse motions are basically interpreted in a negated way: when the mouse goes to the
 * right, the InteractiveFrame (and also the InteractiveDrivableFrame and the InteractiveAvatarFrame) translation goes
 * to the right, while the InteractiveEyeFrame has to go to the <i>left</i>, so that the <i>scene</i> seems to move to
 * the right.
 * <p>
 * An InteractiveEyeFrame rotates around its {@link #anchor()} , which corresponds to the associated
 * {@link Camera#anchor()}.
 * <p>
 * <b>Note:</b> The InteractiveEyeFrame is not added to the {@link remixlab.dandelion.core.AbstractScene#inputHandler()}
 * {@link remixlab.bias.core.InputHandler#agents()} pool upon creation.
 */
public class InteractiveEyeFrame extends InteractiveFrame implements Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(anchorPnt).
				append(worldAxis).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		InteractiveEyeFrame other = (InteractiveEyeFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(anchorPnt, other.anchorPnt)
				.append(worldAxis, other.worldAxis)
				.isEquals();
	}

	protected Eye								viewport;
	protected Vec								anchorPnt;
	protected Vec								worldAxis;

	// L O C A L T I M E R
	public boolean							anchorFlag;
	public boolean							pupFlag;
	public Vec									pupVec;
	protected TimerJob	timerFx;

	/**
	 * Default constructor.
	 * <p>
	 * {@link #flySpeed()} is set to 0.0 and {@link #flyUpVector()} is (0,1,0). The {@link #anchor()} is set to (0,0,0).
	 * <p>
	 * <b>Attention:</b> Created object is removed form the {@link remixlab.dandelion.core.AbstractScene#inputHandler()}
	 * {@link remixlab.bias.core.InputHandler#agents()} pool.
	 */
	public InteractiveEyeFrame(Eye vp) {
		super(vp.scene);
		viewport = vp;
		scene.inputHandler().removeFromAllAgentPools(this);
		anchorPnt = new Vec(0.0f, 0.0f, 0.0f);
		worldAxis = new Vec(0, 0, 1);

		timerFx = new TimerJob() {
			public void execute() {
				unSetTimerFlag();
			}
		};
		scene.registerJob(timerFx);
	}

	/**
	 * Copy constructor
	 * 
	 * @param otherFrame
	 *          the other interactive camera frame
	 */
	protected InteractiveEyeFrame(InteractiveEyeFrame otherFrame) {
		super(otherFrame);
		this.viewport = otherFrame.viewport;
		this.anchorPnt = new Vec();
		this.anchorPnt.set(otherFrame.anchorPnt);
		this.worldAxis = new Vec();
		this.worldAxis.set(otherFrame.worldAxis);
		this.scene.inputHandler().removeFromAllAgentPools(this);
		this.timerFx = new TimerJob() {
			public void execute() {
				unSetTimerFlag();
			}
		};
		this.scene.registerJob(timerFx);
	}

	/**
	 * Calls {@link #InteractiveEyeFrame(InteractiveEyeFrame)} (which is protected) and returns a copy of {@code this}
	 * object.
	 * 
	 * @see #InteractiveEyeFrame(InteractiveEyeFrame)
	 */
	public InteractiveEyeFrame get() {
		return new InteractiveEyeFrame(this);
	}

	public Eye pinhole() {
		return viewport;
	}

	// 2. Local timer
	/**
	 * Called from the timer to stop displaying the point under pixel and anchor visual hints.
	 */
	protected void unSetTimerFlag() {
		anchorFlag = false;
		pupFlag = false;
	}

	/**
	 * Updates the {@link remixlab.remixcam.core.Camera#lastFrameUpdate} variable when the frame changes and then calls
	 * {@code super.modified()}.
	 */
	/**
	 * @Override protected void modified() { viewport.lastFrameUpdate = scene.frameCount(); super.modified(); }
	 */

	/**
	 * Overloading of {@link remixlab.dandelion.core.InteractiveFrame#spin()}.
	 * <p>
	 * Rotates the InteractiveEyeFrame around its {@link #anchor()} instead of its origin.
	 */
	@Override
	public void spin() {
		if (dampFriction > 0) {
			if (eventSpeed == 0) {
				stopSpinning();
				return;
			}
			rotateAroundPoint(spinningOrientation(), anchor());
			recomputeSpinningQuaternion();
		}
		else
			rotateAroundPoint(spinningOrientation(), anchor());
	}

	/**
	 * Returns the point the InteractiveEyeFrame revolves around when rotated.
	 * <p>
	 * It is defined in the world coordinate system. Default value is (0,0,0).
	 * <p>
	 * When the InteractiveEyeFrame is associated to a Camera, {@link remixlab.dandelion.core.Camera#anchor()} also
	 * returns this value.
	 */
	public Vec anchor() {
		return anchorPnt;
	}

	/**
	 * Sets the {@link #anchor()}, defined in the world coordinate system.
	 */
	public void setAnchor(Vec refP) {
		anchorPnt = refP;
		if (scene.is2D())
			anchorPnt.setZ(0);
	}

	@Override
	public void execAction2D(DandelionAction a) {
		if (a == null)
			return;
		Window viewWindow = (Window) viewport;
		Vec trans;
		float deltaX, deltaY;
		Rotation rot;
		switch (a) {
		// better handled these by default (see below)
		/*
		 * case CUSTOM: case ROLL: super.execAction2D(a); break;
		 */
		case ROTATE:
		case SCREEN_ROTATE:
			trans = viewWindow.projectedCoordinatesOf(anchor());
			if (e2.isRelative()) {
				Point prevPos = new Point(e2.prevX(), e2.prevY());
				Point curPos = new Point(e2.x(), e2.y());
				rot = new Rot(new Point(trans.x(), trans.y()), prevPos, curPos);
				rot = new Rot(rot.angle() * rotationSensitivity());
			}
			else
				rot = new Rot(e2.x() * rotationSensitivity());
			if (!isFlipped())
				rot.negate();
			// but its not enough to cover all different cases, so:
			if (scene.window().frame().magnitude().x() * scene.window().frame().magnitude().y() < 0)
				rot.negate();
			if (e2.isRelative()) {
				setSpinningOrientation(rot);
				if (Util.nonZero(dampingFriction()))
					startSpinning(e2);
				else
					spin();
			} else
				// absolute needs testing
				rotate(rot);
			break;
		case SCREEN_TRANSLATE:
			trans = new Vec();
			int dir = originalDirection(e2);
			deltaX = (e2.isRelative()) ? e2.dx() : e2.x();
			if (e2.isRelative())
				deltaY = scene.isRightHanded() ? e2.dy() : -e2.dy();
			else
				deltaY = scene.isRightHanded() ? e2.y() : -e2.y();
			if (dir == 1)
				trans.set(-deltaX, 0.0f, 0.0f);
			else if (dir == -1)
				trans.set(0.0f, deltaY, 0.0f);

			float[] wh = viewWindow.getBoundaryWidthHeight();
			trans.vec[0] *= 2.0f * wh[0] / viewWindow.screenWidth();
			trans.vec[1] *= 2.0f * wh[1] / viewWindow.screenHeight();
			translate(inverseTransformOf(Vec.multiply(trans, translationSensitivity())));
			break;
		case TRANSLATE:
			deltaX = (e2.isRelative()) ? e2.dx() : e2.x();
			if (e2.isRelative())
				deltaY = scene.isRightHanded() ? -e2.dy() : e2.dy();
			else
				deltaY = scene.isRightHanded() ? -e2.y() : e2.y();
			trans = new Vec(-deltaX, -deltaY, 0.0f);
			trans = viewWindow.frame().inverseTransformOf(Vec.multiply(trans, translationSensitivity()));
			// And then down to frame
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			break;
		case TRANSLATE_ROTATE:
			// translate:
			deltaX = (e6.isRelative()) ? e6.dx() : e6.x();
			if (e6.isRelative())
				deltaY = scene.isRightHanded() ? -e6.dy() : e6.dy();
			else
				deltaY = scene.isRightHanded() ? -e6.y() : e6.y();
			trans = new Vec(-deltaX, -deltaY, 0.0f);
			trans = viewWindow.frame().inverseTransformOf(Vec.multiply(trans, translationSensitivity()));
			// And then down to frame
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			// rotate:
			trans = viewWindow.projectedCoordinatesOf(anchor());
			// TODO "relative" is experimental here.
			// Hard to think of a DOF6 relative device in the first place.
			if (e6.isRelative())
				rot = new Rot(e6.drx() * rotationSensitivity());
			else
				rot = new Rot(e6.rx() * rotationSensitivity());
			if (!isFlipped())
				rot.negate();
			// but its not enough to cover all different cases, so:
			if (scene.window().frame().magnitude().x() * scene.window().frame().magnitude().y() < 0)
				rot.negate();
			if (e6.isRelative()) {
				setSpinningOrientation(rot);
				if (Util.nonZero(dampingFriction()))
					startSpinning(e6);
				else
					spin();
			} else
				// absolute needs testing
				rotate(rot);
			break;
		case SCALE:
			float delta;
			if (e1 instanceof ActionDOF1Event) // its a wheel wheel :P
				delta = e1.x() * wheelSensitivity();
			else if (e1.isAbsolute())
				delta = e1.x();
			else
				delta = e1.dx();
			float s = 1 + Math.abs(delta) / (float) -scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case ZOOM_ON_REGION:
			if (e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			int w = (int) Math.abs(e2.dx());
			int tlX = (int) e2.prevX() < (int) e2.x() ? (int) e2.prevX() : (int) e2.x();
			int h = (int) Math.abs(e2.dy());
			int tlY = (int) e2.prevY() < (int) e2.y() ? (int) e2.prevY() : (int) e2.y();
			// viewWindow.fitScreenRegion( new Rectangle (tlX, tlY, w, h) );
			viewWindow.interpolateToZoomOnRegion(new Rect(tlX, tlY, w, h));
			break;
		case CENTER_FRAME:
			viewWindow.centerScene();
			break;
		case ALIGN_FRAME:
			viewWindow.frame().alignWithFrame(null, true);
			break;
		// TODO these timer actions need testing
		case ZOOM_ON_PIXEL:
			viewWindow.interpolateToZoomOnPixel(new Point(cEvent.x(), cEvent.y()));
			pupVec = viewWindow.unprojectedCoordinatesOf(new Vec(cEvent.x(), cEvent.y(), 0.5f));
			pupFlag = true;
			timerFx.runOnce(1000);
			break;
		case ANCHOR_FROM_PIXEL:
			if (viewWindow.setAnchorFromPixel(new Point(cEvent.x(), cEvent.y()))) {
				anchorFlag = true;
				timerFx.runOnce(1000);
			}
			break;
		default:
			super.execAction2D(a);
			break;
		}
	}

	@Override
	public void execAction3D(DandelionAction a) {
		if (a == null)
			return;
		Camera camera = (Camera) viewport;
		Vec trans;
		Quat q;
		Camera.WorldPoint wP;
		switch (a) {
		// better handled these by default (see below)
		/*
		 * case CUSTOM: case DRIVE: case LOOK_AROUND: case MOVE_BACKWARD: case MOVE_FORWARD: case ROLL:
		 * super.execAction3D(a); break;
		 */
		case ROTATE:
			if (e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = camera.projectedCoordinatesOf(anchor());
			setSpinningOrientation(deformedBallQuaternion(e2, trans.vec[0], trans.vec[1], camera));
			if (Util.nonZero(dampingFriction()))
				startSpinning(e2);
			else
				spin();
			break;
		case CAD_ROTATE:
			if (e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = camera.projectedCoordinatesOf(anchor());
			setSpinningOrientation(cadQuaternion(e2, trans.vec[0], trans.vec[1], camera));
			if (Util.nonZero(dampingFriction()))
				startSpinning(e2);
			else
				spin();
			break;
		case ROTATE3:
			q = new Quat();
			if (e3.isAbsolute())
				q.fromEulerAngles(-e3.x(), -e3.y(), e3.z());
			else
				q.fromEulerAngles(-e3.dx(), -e3.dy(), e3.dz());
			rotate(q);
			break;
		case SCREEN_ROTATE:
			if (e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = camera.projectedCoordinatesOf(anchor());
			float angle = (float) Math.atan2(e2.y() - trans.vec[1], e2.x() - trans.vec[0])
					- (float) Math.atan2(e2.prevY() - trans.vec[1], e2.prevX() - trans.vec[0]);
			// lef-handed coordinate system correction
			// if( scene.isLeftHanded() )
			if (!isFlipped())
				angle = -angle;
			Rotation rot = new Quat(new Vec(0.0f, 0.0f, 1.0f), angle);
			setSpinningOrientation(rot);
			if (Util.nonZero(dampingFriction()))
				startSpinning(e2);
			else
				spin();
			updateFlyUpVector();
			break;
		case SCREEN_TRANSLATE:
			trans = new Vec();
			int dir = originalDirection(e2);
			if (dir == 1)
				if (e2.isAbsolute())
					trans.set(-e2.x(), 0.0f, 0.0f);
				else
					trans.set(-e2.dx(), 0.0f, 0.0f);
			else if (dir == -1)
				if (e2.isAbsolute())
					trans.set(0.0f, -e2.y(), 0.0f);
				else
					trans.set(0.0f, -e2.dy(), 0.0f);
			switch (camera.type()) {
			case PERSPECTIVE:
				trans.multiply(2.0f
						* (float) Math.tan(camera.fieldOfView() / 2.0f)
						* Math.abs(coordinatesOf(anchor()).vec[2] * magnitude().z())
						// * Math.abs((camera.frame().coordinatesOf(arcballReferencePoint())).vec[2])
						// * Math.abs((camera.frame().coordinatesOfNoScl(arcballReferencePoint())).vec[2])
						/ camera.screenHeight());
				break;
			case ORTHOGRAPHIC:
				float[] wh = camera.getBoundaryWidthHeight();
				trans.vec[0] *= 2.0f * wh[0] / camera.screenWidth();
				trans.vec[1] *= 2.0f * wh[1] / camera.screenHeight();
				break;
			}
			trans = Vec.multiply(trans, translationSensitivity());
			trans.divide(magnitude());
			translate(inverseTransformOf(trans));
			// translate(inverseTransformOf(trans, false));
			break;
		case TRANSLATE:
			if (e2.isRelative())
				trans = new Vec(-e2.dx(), scene.isRightHanded() ? e2.dy() : -e2.dy(), 0.0f);
			else
				trans = new Vec(-e2.x(), scene.isRightHanded() ? e2.y() : -e2.y(), 0.0f);
			// Scale to fit the screen mouse displacement
			switch (camera.type()) {
			case PERSPECTIVE:
				trans.multiply(2.0f * (float) Math.tan(camera.fieldOfView() / 2.0f)
						* Math.abs(coordinatesOf(anchor()).vec[2] * magnitude().z())
						/ camera.screenHeight());
				break;
			case ORTHOGRAPHIC:
				float[] wh = camera.getBoundaryWidthHeight();
				trans.vec[0] *= 2.0f * wh[0] / camera.screenWidth();
				trans.vec[1] *= 2.0f * wh[1] / camera.screenHeight();
				break;
			}
			translate(inverseTransformOf(Vec.multiply(trans, translationSensitivity()), false));
			break;
		case TRANSLATE3:
			if (e3.isRelative())
				trans = new Vec(-e3.dx(), scene.isRightHanded() ? e3.dy() : -e3.dy(), -e3.dz());
			else
				trans = new Vec(-e3.x(), scene.isRightHanded() ? e3.y() : -e3.y(), -e3.z());
			// Scale to fit the screen mouse displacement
			switch (camera.type()) {
			case PERSPECTIVE:
				trans.multiply(2.0f * (float) Math.tan(camera.fieldOfView() / 2.0f)
						* Math.abs(coordinatesOf(anchor()).vec[2] * magnitude().z())
						/ camera.screenHeight());
				break;
			case ORTHOGRAPHIC:
				float[] wh = camera.getBoundaryWidthHeight();
				trans.vec[0] *= 2.0f * wh[0] / camera.screenWidth();
				trans.vec[1] *= 2.0f * wh[1] / camera.screenHeight();
				break;
			}
			translate(inverseTransformOf(Vec.multiply(trans, translationSensitivity()), false));
			break;
		case TRANSLATE_ROTATE:
			// translate
			if (e6.isRelative())
				trans = new Vec(-e6.dx(), scene.isRightHanded() ? e6.dy() : -e6.dy(), -e6.dz());
			else
				trans = new Vec(-e6.x(), scene.isRightHanded() ? e6.y() : -e6.y(), -e6.z());
			// Scale to fit the screen mouse displacement
			switch (camera.type()) {
			case PERSPECTIVE:
				trans.multiply(2.0f * (float) Math.tan(camera.fieldOfView() / 2.0f)
						* Math.abs(coordinatesOf(anchor()).vec[2] * magnitude().z())
						/ camera.screenHeight());
				break;
			case ORTHOGRAPHIC:
				float[] wh = camera.getBoundaryWidthHeight();
				trans.vec[0] *= 2.0f * wh[0] / camera.screenWidth();
				trans.vec[1] *= 2.0f * wh[1] / camera.screenHeight();
				break;
			}
			translate(inverseTransformOf(Vec.multiply(trans, translationSensitivity()), false));
			// Rotate
			q = new Quat();
			if (e6.isAbsolute())
				q.fromEulerAngles(-e6.roll(), -e6.pitch(), e6.yaw());
			else
				q.fromEulerAngles(-e6.drx(), -e6.dry(), e6.drz());
			rotate(q);
			break;
		case SCALE:
			float delta;
			if (e1 instanceof ActionDOF1Event) // its a wheel wheel :P
				delta = e1.x() * wheelSensitivity();
			else if (e1.isAbsolute())
				delta = e1.x();
			else
				delta = e1.dx();
			float s = 1 + Math.abs(delta) / (float) -scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case ZOOM:
			float wheelSensitivityCoef = 8E-4f;
			float coef = Math.max(Math.abs((coordinatesOf(camera.anchor())).vec[2] * magnitude().z()),
					0.2f * camera.sceneRadius());
			if (e1 instanceof ActionDOF1Event) // its a wheel wheel :P
				delta = coef * e1.x() * -wheelSensitivity() * wheelSensitivityCoef;
			else if (e1.isAbsolute())
				delta = -coef * e1.x() / camera.screenHeight();
			else
				delta = -coef * e1.dx() / camera.screenHeight();
			trans = new Vec(0.0f, 0.0f, delta);
			// No Scl
			Vec mag = magnitude();
			trans.divide(mag);
			translate(inverseTransformOf(trans));
			break;
		case ZOOM_ON_REGION:
			if (e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			int w = (int) Math.abs(e2.dx());
			int tlX = (int) e2.prevX() < (int) e2.x() ? (int) e2.prevX() : (int) e2.x();
			int h = (int) Math.abs(e2.dy());
			int tlY = (int) e2.prevY() < (int) e2.y() ? (int) e2.prevY() : (int) e2.y();
			// camera.fitScreenRegion( new Rectangle (tlX, tlY, w, h) );
			camera.interpolateToZoomOnRegion(new Rect(tlX, tlY, w, h));
			break;
		case CENTER_FRAME:
			camera.centerScene();
			break;
		case ALIGN_FRAME:
			camera.frame().alignWithFrame(null, true);
			break;
		case ZOOM_ON_PIXEL:
			wP = camera.interpolateToZoomOnPixel(camera.pointUnderPixel(new Point(cEvent.x(), cEvent.y())));
			if (wP.found) {
				pupVec = wP.point;
				pupFlag = true;
				timerFx.runOnce(1000);
			}
			break;
		case ANCHOR_FROM_PIXEL:
			if (camera.setAnchorFromPixel(new Point(cEvent.x(), cEvent.y()))) {
				anchorFlag = true;
				timerFx.runOnce(1000);
			}
			break;
		default:
			// Dummie value:
			// AbstractScene.showMissingImplementationWarning(a, this.getClass().getName());
			super.execAction3D(a);
			break;
		}

		/**
		 * //TODO implement me as an example case GOOGLE_EARTH: Vec t = new Vec(); Quat q = new Quat();
		 * 
		 * event6 = (GenericDOF6Event<?>)e; float magic = 0.01f; // rotSens/transSens?
		 * 
		 * //t = DLVector.mult(position(), -event6.getZ() * ( rotSens.z/transSens.z ) ); t = Vec.mult(position(),
		 * -event6.getZ() * (magic) ); translate(t);
		 * 
		 * //q.fromEulerAngles(-event6.getY() * ( rotSens.y/transSens.y ), event6.getX() * ( rotSens.x/transSens.x ), 0);
		 * q.fromEulerAngles(-event6.getY() * (magic), event6.getX() * (magic), 0); rotateAroundPoint(q,
		 * scene.camera().arcballReferencePoint());
		 * 
		 * q.fromEulerAngles(0, 0, event6.yaw()); rotateAroundPoint(q, scene.camera().arcballReferencePoint());
		 * 
		 * q.fromEulerAngles(-event6.roll(), 0, 0); rotate(q); break; //
		 */
	}

	/**
	 * Returns a Quaternion computed according to mouse motion. The Quaternion is computed as composition of two rotations
	 * (quaternions): 1. Mouse motion along the screen X Axis rotates the camera along the {@link #getCADAxis()}. 2. Mouse
	 * motion along the screen Y axis rotates the camera along its X axis.
	 * 
	 * @see #getCADAxis()
	 */
	protected Quat cadQuaternion(DOF2Event event, float cx, float cy, Eye camera) {
		if (!(camera instanceof Camera))
			throw new RuntimeException("CAD cam is oly available in 3D");

		float x = event.x();
		float y = event.y();
		float prevX = event.prevX();
		float prevY = event.prevY();

		// Points on the deformed ball
		float px = rotationSensitivity() * ((int) prevX - cx) / camera.screenWidth();
		float py = rotationSensitivity() * (scene.isLeftHanded() ? ((int) prevY - cy) : ((cy - (int) prevY)))
				/ camera.screenHeight();
		float dx = rotationSensitivity() * (x - cx) / camera.screenWidth();
		float dy = rotationSensitivity() * (scene.isLeftHanded() ? (y - cy) : (cy - y)) / camera.screenHeight();

		// 1,0,0 is given in the camera frame
		Vec axisX = new Vec(1, 0, 0);
		// 0,0,1 is given in the world and then transform to the camera frame

		// TODO broken when cam frame has scaling, maybe should go like this:?
		// Vec world2camAxis = camera.frame().transformOf(worldAxis, false);
		Vec world2camAxis = camera.frame().transformOf(worldAxis);
		// Vector3D world2camAxis = camera.frame().transformOfNoScl(worldAxis);

		float angleWorldAxis = rotationSensitivity() * (scene.isLeftHanded() ? (dx - px) : (px - dx));
		float angleX = rotationSensitivity() * (dy - py);

		Quat quatWorld = new Quat(world2camAxis, angleWorldAxis);
		Quat quatX = new Quat(axisX, angleX);

		return Quat.multiply(quatWorld, quatX);
	}

	/**
	 * Set axis (defined in the world coordinate system) as the main rotation axis used in CAD rotation.
	 */
	public void setCADAxis(Vec axis) {
		// non-zero
		if (Util.zero(axis.magnitude()))
			return;
		else
			worldAxis = axis.get();
		worldAxis.normalize();
	}

	/**
	 * Returns the main CAD rotation axis ((defined in the world coordinate system).
	 */
	public Vec getCADAxis() {
		return worldAxis;
	}
}
