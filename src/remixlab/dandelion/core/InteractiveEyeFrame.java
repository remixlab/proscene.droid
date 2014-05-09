/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.core;

import remixlab.dandelion.geom.*;
import remixlab.fpstiming.TimingTask;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;
import remixlab.util.Util;

/**
 * The InteractiveEyeFrame class represents an InteractiveFrame with Eye specific gesture bindings.
 * <p>
 * An InteractiveEyeFrame is a specialization of an InteractiveFrame which can "fly" in the Scene and that is designed
 * to be set as the {@link Eye#frame()}. User gestures are basically interpreted in a negated way respect to those
 * defined for the InteractiveFrame (and also the InteractiveAvatarFrame). For instance, with a move-to-the-right user
 * gesture the InteractiveEyeFrame has to go to the <i>left</i>, so that the <i>scene</i> seems to move to the right.
 * <p>
 * Depending on the Dandelion action an InteractiveEyeFrame rotates either around its {@link #anchor()} (e.g., ROTATE)
 * which is a wrapper to {@link Eye#anchor()}), or its {@link #sceneUpVector()} (e.g., ROTATE_CAD). In the latter case
 * the {@link #sceneUpVector()} defines a 'vertical' direction around which the camera rotates. The camera can rotate
 * left or right, around this axis. It can also be moved up or down to show the 'top' and 'bottom' views of the scene.
 * As a result, the {@link #sceneUpVector()} will always appear vertical in the scene, and the horizon is preserved and
 * stays projected along the camera's horizontal axis. Use {@link remixlab.dandelion.core.Camera#setUpVector(Vec)} to
 * define the {@link #sceneUpVector()} and align the camera before starting a ROTATE_CAD action to ensure these
 * invariants are preserved.
 * <p>
 * <b>Observation: </b> The InteractiveEyeFrame is not added to the
 * {@link remixlab.dandelion.core.AbstractScene#inputHandler()} {@link remixlab.bias.core.InputHandler#agents()} pool
 * upon creation.
 */
public class InteractiveEyeFrame extends InteractiveFrame implements Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(anchorPnt).
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
				.isEquals();
	}

	protected Eye					eye;
	protected Vec					anchorPnt;

	// L O C A L T I M E R
	public boolean				anchorFlag;
	public boolean				pupFlag;
	public Vec						pupVec;
	protected TimingTask	timerFx;

	// Inverse the direction of an horizontal mouse motion. Depends on the projected
	// screen orientation of the vertical axis when the mouse button is pressed.
	public boolean				cadRotationIsReversed;

	/**
	 * Default constructor.
	 * <p>
	 * {@link #flySpeed()} is set to 0.0 and {@link #sceneUpVector()} is set to the Y-axis. The {@link #anchor()} is set
	 * to 0.
	 * <p>
	 * <b>Attention:</b> Created object is removed from the {@link remixlab.dandelion.core.AbstractScene#inputHandler()}
	 * {@link remixlab.bias.core.InputHandler#agents()} pool.
	 */
	public InteractiveEyeFrame(Eye theEye) {
		super(theEye.scene);
		eye = theEye;
		scene.inputHandler().removeFromAllAgentPools(this);
		anchorPnt = new Vec(0.0f, 0.0f, 0.0f);

		timerFx = new TimingTask() {
			public void execute() {
				unSetTimerFlag();
			}
		};
		scene.registerTimingTask(timerFx);
	}

	protected InteractiveEyeFrame(InteractiveEyeFrame otherFrame) {
		super(otherFrame);
		this.eye = otherFrame.eye;
		this.anchorPnt = new Vec();
		this.anchorPnt.set(otherFrame.anchorPnt);
		this.scene.inputHandler().removeFromAllAgentPools(this);
		this.timerFx = new TimingTask() {
			public void execute() {
				unSetTimerFlag();
			}
		};
		this.scene.registerTimingTask(timerFx);
	}

	@Override
	public InteractiveEyeFrame get() {
		return new InteractiveEyeFrame(this);
	}

	public Eye eye() {
		return eye;
	}

	// 2. Local timer

	/**
	 * Internal use. Called from the timer to stop displaying the point under pixel and anchor visual hints.
	 */
	protected void unSetTimerFlag() {
		anchorFlag = false;
		pupFlag = false;
	}

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
			rotateAroundPoint(spinningRotation(), anchor());
			recomputeSpinningRotation();
		}
		else
			rotateAroundPoint(spinningRotation(), anchor());
	}

	/**
	 * Returns the point the InteractiveEyeFrame revolves around when rotated.
	 * <p>
	 * It is defined in the world coordinate system. Default value is 0.
	 * <p>
	 * When the InteractiveEyeFrame is associated to an Eye, {@link remixlab.dandelion.core.Eye#anchor()} also returns
	 * this value.
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
	protected void execAction2D(DandelionAction a) {
		if (a == null)
			return;
		Window window = (Window) eye;
		Vec trans;
		float deltaX, deltaY;
		Rotation rot;
		switch (a) {
		// better handled these by default (see below)
		// case CUSTOM: case ROLL: super.execAction2D(a); break;
		case TRANSLATE_X:
			translateFromEye(new Vec(-delta1(), 0));
			break;
		case TRANSLATE_Y:
			translateFromEye(new Vec(0, scene.isRightHanded() ? delta1() : -delta1()));
			break;
		case ROTATE_Z:
			rot = new Rot(scene.isRightHanded() ? -computeAngle() : computeAngle());
			rotate(rot);
			setSpinningRotation(rot);
		case ROTATE:
		case SCREEN_ROTATE:
			rot = computeRot(window.projectedCoordinatesOf(anchor()));
			if (e2.isRelative()) {
				setSpinningRotation(rot);
				if (Util.nonZero(dampingFriction()))
					startSpinning(e2);
				else
					spin();
			} else
				// absolute needs testing
				rotate(rot);
			break;
		case MOVE_FORWARD:
			rotate(computeRot(scene.window().projectedCoordinatesOf(position())));
			flyDisp.set(-flySpeed(), 0.0f, 0.0f);
			translate(flyDisp);
			setTossingDirection(flyDisp);
			startTossing(e2);
			break;
		case MOVE_BACKWARD:
			rotate(computeRot(scene.window().projectedCoordinatesOf(position())));
			flyDisp.set(flySpeed(), 0.0f, 0.0f);
			translate(flyDisp);
			setTossingDirection(flyDisp);
			startTossing(e2);
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

			float[] wh = window.getBoundaryWidthHeight();
			trans.vec[0] *= 2.0f * wh[0] / window.screenWidth();
			trans.vec[1] *= 2.0f * wh[1] / window.screenHeight();
			translate(inverseTransformOf(Vec.multiply(trans, translationSensitivity())));
			// not the same as (because invTransfOf takes into account scaling):
			// translate(orientation().rotate(Vec.multiply(trans, translationSensitivity())));
			break;
		case TRANSLATE:
			deltaX = (e2.isRelative()) ? e2.dx() : e2.x();
			if (e2.isRelative())
				deltaY = scene.isRightHanded() ? -e2.dy() : e2.dy();
			else
				deltaY = scene.isRightHanded() ? -e2.y() : e2.y();
			translateFromEye(new Vec(-deltaX, -deltaY, 0.0f));
			break;
		case TRANSLATE_XYZ_ROTATE_XYZ:
			// translate:
			deltaX = (e6.isRelative()) ? e6.dx() : e6.x();
			if (e6.isRelative())
				deltaY = scene.isRightHanded() ? -e6.dy() : e6.dy();
			else
				deltaY = scene.isRightHanded() ? -e6.y() : e6.y();
			translateFromEye(new Vec(-deltaX, -deltaY, 0.0f));
			// rotate:
			// TODO commented because trans is not used
			// trans = window.projectedCoordinatesOf(anchor());
			// TODO "relative" is experimental here.
			// Hard to think of a DOF6 relative device in the first place.
			if (e6.isRelative())
				rot = new Rot(e6.drx() * rotationSensitivity());
			else
				rot = new Rot(e6.rx() * rotationSensitivity());
			if (scene.isLeftHanded())
				rot.negate();
			if (e6.isRelative()) {
				setSpinningRotation(rot);
				if (Util.nonZero(dampingFriction()))
					startSpinning(e6);
				else
					spin();
			} else
				// absolute needs testing
				rotate(rot);
			break;
		case SCALE:
			float delta = delta1();
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
			window.interpolateToZoomOnRegion(new Rect(tlX, tlY, w, h));
			break;
		case CENTER_FRAME:
			window.centerScene();
			break;
		case ALIGN_FRAME:
			window.frame().alignWithFrame(null, true);
			break;
		// TODO these timer actions need testing
		case ZOOM_ON_PIXEL:
			window.interpolateToZoomOnPixel(new Point(cEvent.x(), cEvent.y()));
			pupVec = window.unprojectedCoordinatesOf(new Vec(cEvent.x(), cEvent.y(), 0.5f));
			pupFlag = true;
			timerFx.runOnce(1000);
			break;
		case ANCHOR_FROM_PIXEL:
			if (window.setAnchorFromPixel(new Point(cEvent.x(), cEvent.y()))) {
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
	protected void execAction3D(DandelionAction a) {
		if (a == null)
			return;
		Camera camera = (Camera) eye;
		Vec trans;
		Quat q;
		Camera.WorldPoint wP;
		float delta;
		float wheelSensitivityCoef = 8E-4f;
		switch (a) {
		// better handled these by default (see below)
		// case CUSTOM: case DRIVE: case LOOK_AROUND: case MOVE_BACKWARD: case MOVE_FORWARD: case ROLL:
		// super.execAction3D(a); break;
		case TRANSLATE_X:
			trans = new Vec(-delta1(), 0.0f, 0.0f);
			scale2Fit(trans);
			translate(orientation().rotate(Vec.multiply(trans, translationSensitivity())));
			break;
		case TRANSLATE_Y:
			trans = new Vec(0.0f, scene.isRightHanded() ? delta1() : -delta1(), 0.0f);
			scale2Fit(trans);
			translate(orientation().rotate(Vec.multiply(trans, translationSensitivity())));
			break;
		case TRANSLATE_Z:
			trans = new Vec(0.0f, 0.0f, -delta1());
			scale2Fit(trans);
			translate(orientation().rotate(Vec.multiply(trans, translationSensitivity())));
			break;
		case ROTATE_X:
			rotateAroundAxis(new Vec(scene.isLeftHanded() ? 1 : -1, 0, 0), computeAngle());
			break;
		case ROTATE_Y:
			rotateAroundAxis(new Vec(0, -1, 0), computeAngle());
			break;
		case ROTATE_Z:
			rotateAroundAxis(new Vec(0, 0, scene.isLeftHanded() ? -1 : 0), computeAngle());
			break;
		case ROTATE_XYZ:
			q = new Quat();
			if (e3.isAbsolute())
				q.fromEulerAngles(scene.isLeftHanded() ? -e3.x() : e3.x(), -e3.y(), scene.isLeftHanded() ? e3.z() : -e3.z());
			else
				q.fromEulerAngles(scene.isLeftHanded() ? -e3.dx() : e3.dx(), -e3.dy(),
						scene.isLeftHanded() ? e3.dz() : -e3.dz());
			rotate(q);
			break;
		case ROTATE:
			if (e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = camera.projectedCoordinatesOf(anchor());
			setSpinningRotation(deformedBallQuaternion(e2, trans.vec[0], trans.vec[1], camera));
			if (Util.nonZero(dampingFriction()))
				startSpinning(e2);
			else
				spin();
			break;
		case ROTATE_CAD:
			if (e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			// Multiply by 2.0 to get on average about the same speed as with the deformed ball
			float dx = -2.0f * rotationSensitivity() * e2.dx() / scene.camera().screenWidth();
			float dy = 2.0f * rotationSensitivity() * e2.dy() / scene.camera().screenHeight();
			if (cadRotationIsReversed)
				dx = -dx;
			if (scene.isRightHanded())
				dy = -dy;
			Vec verticalAxis = transformOf(sceneUpVector());
			setSpinningRotation(Quat.multiply(new Quat(verticalAxis, dx), new Quat(new Vec(1.0f, 0.0f, 0.0f), dy)));
			if (Util.nonZero(dampingFriction()))
				startSpinning(e2);
			else
				spin();
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
			if (scene.isLeftHanded())
				angle = -angle;
			Rotation rot = new Quat(new Vec(0.0f, 0.0f, 1.0f), angle);
			setSpinningRotation(rot);
			if (Util.nonZero(dampingFriction()))
				startSpinning(e2);
			else
				spin();
			updateSceneUpVector();
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
					trans.set(0.0f, scene.isLeftHanded() ? -e2.y() : e2.y(), 0.0f);
				else
					trans.set(0.0f, scene.isLeftHanded() ? -e2.dy() : e2.dy(), 0.0f);
			scale2Fit(trans);
			trans = Vec.multiply(trans, translationSensitivity());
			translate(orientation().rotate(trans));
			break;
		case TRANSLATE:
			if (e2.isRelative())
				trans = new Vec(-e2.dx(), scene.isRightHanded() ? e2.dy() : -e2.dy(), 0.0f);
			else
				trans = new Vec(-e2.x(), scene.isRightHanded() ? e2.y() : -e2.y(), 0.0f);
			scale2Fit(trans);
			translate(orientation().rotate(Vec.multiply(trans, translationSensitivity())));
			break;
		case TRANSLATE_XYZ:
			if (e3.isRelative())
				trans = new Vec(-e3.dx(), scene.isRightHanded() ? -e3.dy() : e3.dy(), -e3.dz());
			else
				trans = new Vec(-e3.x(), scene.isRightHanded() ? -e3.y() : e3.y(), -e3.z());
			scale2Fit(trans);
			translate(orientation().rotate(Vec.multiply(trans, translationSensitivity())));
			break;
		case TRANSLATE_XYZ_ROTATE_XYZ:
			// translate
			if (e6.isRelative())
				trans = new Vec(-e6.dx(), scene.isRightHanded() ? -e6.dy() : e6.dy(), -e6.dz());
			else
				trans = new Vec(-e6.x(), scene.isRightHanded() ? -e6.y() : e6.y(), -e6.z());
			// Scale to fit the screen mouse displacement
			scale2Fit(trans);
			translate(orientation().rotate(Vec.multiply(trans, translationSensitivity())));
			// Rotate
			q = new Quat();
			if (e6.isAbsolute())
				q.fromEulerAngles(scene.isLeftHanded() ? -e6.roll() : e6.roll(), -e6.pitch(), scene.isLeftHanded() ? e6.yaw()
						: -e6.yaw());
			else
				q.fromEulerAngles(scene.isLeftHanded() ? -e6.drx() : e6.drx(), -e6.dry(),
						scene.isLeftHanded() ? e6.drz() : -e6.drz());
			rotate(q);
			break;
		case SCALE:
			delta = delta1();
			float s = 1 + Math.abs(delta) / (float) -scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case ZOOM:
			float coef = Math.max(Math.abs((coordinatesOf(camera.anchor())).vec[2] * magnitude()),
					0.2f * camera.sceneRadius());
			if (e1.action() != null) // its a wheel wheel :P
				delta = coef * e1.x() * -wheelSensitivity() * wheelSensitivityCoef;
			// TODO should absolute be divided by camera.screenHeight()?
			else if (e1.isAbsolute())
				delta = -coef * e1.x() / camera.screenHeight();
			else
				delta = -coef * e1.dx() / camera.screenHeight();
			trans = new Vec(0.0f, 0.0f, delta);
			translate(orientation().rotate(trans));
			break;
		case ZOOM_ON_ANCHOR:
			if (e1.action() != null) // its a wheel wheel :P
				delta = e1.x() * -wheelSensitivity() * wheelSensitivityCoef;
			// TODO should absolute be divided by camera.screenHeight()?
			else if (e1.isAbsolute())
				delta = -e1.x() / camera.screenHeight();
			else
				delta = -e1.dx() / camera.screenHeight();
			trans = Vec.subtract(position(), scene.camera().anchor());
			if (trans.magnitude() > 0.02f * scene.radius() || delta > 0.0f)
				translate(Vec.multiply(trans, delta));
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

	@Override
	protected void scale2Fit(Vec trans) {
		// Scale to fit the screen mouse displacement
		switch (scene.camera().type()) {
		case PERSPECTIVE:
			trans.multiply(2.0f * (float) Math.tan(scene.camera().fieldOfView() / 2.0f)
					* Math.abs(coordinatesOf(anchor()).vec[2] * magnitude())
					/ scene.camera().screenHeight());
			break;
		case ORTHOGRAPHIC:
			float[] wh = scene.camera().getBoundaryWidthHeight();
			trans.vec[0] *= 2.0f * wh[0] / scene.camera().screenWidth();
			trans.vec[1] *= 2.0f * wh[1] / scene.camera().screenHeight();
			break;
		}
	}

	@Override
	protected Rot computeRot(Vec trans) {
		Rot rot;
		if (e2.isRelative()) {
			Point prevPos = new Point(e2.prevX(), e2.prevY());
			Point curPos = new Point(e2.x(), e2.y());
			rot = new Rot(new Point(trans.x(), trans.y()), prevPos, curPos);
			rot = new Rot(rot.angle() * rotationSensitivity());
		}
		else
			rot = new Rot(e2.x() * rotationSensitivity());
		if (scene.isLeftHanded())
			rot.negate();
		return rot;
	}
}
