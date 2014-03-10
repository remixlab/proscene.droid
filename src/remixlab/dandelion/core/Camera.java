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
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;
import remixlab.util.Util;

import java.util.ArrayList;

/**
 * 3D implementation of the {@link remixlab.dandelion.core.Eye} abstract class, defining a perspective or orthographic
 * Camera.
 * <p>
 * The Camera dynamically sets up the {@link #zNear()} and {@link #zFar()} values, in order to provide optimal precision
 * of the z-buffer.
 */
public class Camera extends Eye implements Constants, Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(zClippingCoef).
				append(IODist).
				append(focusDist).
				append(physicalDist2Scrn).
				append(physicalScrnWidth).
				append(tp).
				append(zClippingCoef).
				append(zNearCoef).
				append(rapK).
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

		Camera other = (Camera) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(zClippingCoef, other.zClippingCoef)
				.append(IODist, other.IODist)
				.append(focusDist, other.focusDist)
				.append(physicalDist2Scrn, other.physicalDist2Scrn)
				.append(physicalScrnWidth, other.physicalScrnWidth)
				.append(tp, other.tp)
				.append(zClippingCoef, other.zClippingCoef)
				.append(zNearCoef, other.zNearCoef)
				.append(rapK, other.rapK)
				.isEquals();
	}

	/**
	 * Internal class that represents/holds a cone of normals. Typically needed to perform bfc.
	 */
	public class Cone {
		Vec		axis;
		float	angle;

		public Cone() {
			reset();
		}

		public Cone(Vec vec, float a) {
			set(vec, a);
		}

		public Cone(ArrayList<Vec> normals) {
			set(normals);
		}

		public Cone(Vec[] normals) {
			set(normals);
		}

		public Vec axis() {
			return axis;
		}

		public float angle() {
			return angle;
		}

		public void reset() {
			axis = new Vec(0, 0, 1);
			angle = 0;
		}

		public void set(Vec vec, float a) {
			axis = vec;
			angle = a;
		}

		public void set(ArrayList<Vec> normals) {
			set(normals.toArray(new Vec[normals.size()]));
		}

		public void set(Vec[] normals) {
			axis = new Vec(0, 0, 0);
			if (normals.length == 0) {
				reset();
				return;
			}

			Vec[] n = new Vec[normals.length];
			for (int i = 0; i < normals.length; i++) {
				n[i] = new Vec();
				n[i].set(normals[i]);
				n[i].normalize();
				axis = Vec.add(axis, n[i]);
			}

			if (Util.nonZero(axis.magnitude())) {
				axis.normalize();
			}
			else {
				axis.set(0, 0, 1);
			}

			angle = 0;
			for (int i = 0; i < normals.length; i++)
				angle = Math.max(angle, (float) Math.acos(Vec.dot(n[i], axis)));
		}
	}

	/**
	 * Internal class provided to catch the output of {@link remixlab.dandelion.core.Camera#pointUnderPixel(Point)} (which
	 * should be implemented by an openGL based derived class Camera).
	 */
	public class WorldPoint {
		public WorldPoint(Vec p, boolean f) {
			point = p;
			found = f;
		}

		public Vec			point;
		public boolean	found;
	}

	/**
	 * Enumerates the two possible types of Camera.
	 * <p>
	 * This type mainly defines different camera projection matrix. Many other methods take this Type into account.
	 */
	public enum Type {
		PERSPECTIVE, ORTHOGRAPHIC
	};

	// C a m e r a p a r a m e t e r s
	private float	zNearCoef;
	private float	zClippingCoef;
	private Type	tp;								// PERSPECTIVE or ORTHOGRAPHIC

	// S t e r e o p a r a m e t e r s
	private float	IODist;						// inter-ocular distance, in meters
	private float	focusDist;					// in scene units
	private float	physicalDist2Scrn;	// in meters
	private float	physicalScrnWidth;	// in meters

	// rescale ortho when rap changes
	private float	rapK	= 1;

	/**
	 * Main constructor.
	 * <p>
	 * {@link #sceneCenter()} is set to (0,0,0) and {@link #sceneRadius()} is set to 100. {@link #type()}
	 * Camera.PERSPECTIVE, with a {@code PI/3} {@link #fieldOfView()} (same value used in P5 by default).
	 * <p>
	 * Camera matrices (projection and view) are created and computed according to remaining default Camera parameters.
	 * <p>
	 * See {@link #IODistance()}, {@link #physicalDistanceToScreen()}, {@link #physicalScreenWidth()} and
	 * {@link #focusDistance()} documentations for default stereo parameter values.
	 */
	public Camera(AbstractScene scn) {
		super(scn);

		if (scene.is2D())
			throw new RuntimeException("Use Camera only for a 3D Scene");

		// dist = new float[6];
		// normal = new Vec[6];
		// for (int i = 0; i < normal.length; i++) normal[i] = new Vec();

		// fldOfView = (float) Math.PI / 3.0f; //in Proscene 1.x it was Pi/4
		// setFieldOfView((float) Math.PI / 2.0f);//fov yScaling -> 1
		setFieldOfView((float) Math.PI / 3.0f);
		// Initial value (only scaled after this)
		// orthoCoef = (float)Math.tan(fieldOfView() / 2.0f);

		// fpCoefficients = new float[6][4];

		// Initial value (only scaled after this)
		// orthoCoef = (float) Math.tan(fieldOfView() / 2.0f);

		// Requires fieldOfView() when called with ORTHOGRAPHIC. Attention to projectionMat below.
		setType(Camera.Type.PERSPECTIVE);
		setZNearCoefficient(0.005f);
		setZClippingCoefficient((float) Math.sqrt(3.0f));

		// Stereo parameters
		setIODistance(0.062f);
		setPhysicalDistanceToScreen(0.5f);
		setPhysicalScreenWidth(0.4f);
		// focusDistance is set from setFieldOfView()

		computeProjection();
	}

	protected Camera(Camera oCam) {
		super(oCam);
		this.setType(oCam.type());
		this.setZNearCoefficient(oCam.zNearCoefficient());
		this.setZClippingCoefficient(oCam.zClippingCoefficient());
		this.setIODistance(oCam.IODistance());
		this.setPhysicalDistanceToScreen(oCam.physicalDistanceToScreen());
		this.setPhysicalScreenWidth(oCam.physicalScreenWidth());
		this.rapK = oCam.rapK;
	}

	@Override
	public Camera get() {
		return new Camera(this);
	}

	// 2. POSITION AND ORIENTATION

	@Override
	public Vec rightVector() {
		// if there were not validateScaling this should do it:
		// return frame().magnitude().x() > 0 ? frame().xAxis() : frame().xAxis(false);
		return frame().xAxis();
	}

	@Override
	public Vec upVector() {
		// if there were not validateScaling this should do it:
		// return frame().magnitude().y() > 0 ? frame().yAxis() : frame().yAxis(false);
		return frame().yAxis();
	}

	@Override
	public void setUpVector(Vec up, boolean noMove) {
		// if there were not validateScaling this should do it:
		// Quat q = new Quat(new Vec(0.0f, frame().magnitude().y() > 0 ? 1.0f : -1.0f, 0.0f), frame().transformOf(up));
		Quat q = new Quat(new Vec(0.0f, 1.0f, 0.0f), frame().transformOf(up));

		if (!noMove && scene.is3D())
			frame().setPosition(Vec.subtract(anchor(),
					(Quat.multiply((Quat) frame().orientation(), q)).rotate(frame().coordinatesOf(anchor()))));

		frame().rotate(q);

		// Useful in fly mode to keep the horizontal direction.
		frame().updateFlyUpVector();
	}

	@Override
	public Vec viewDirection() {
		// if there were not validateScaling this should do it:
		// return frame().magnitude().z() > 0 ? frame().zAxis(false) : frame().zAxis();
		return frame().zAxis(false);
	}

	/**
	 * Rotates the Camera so that its {@link #viewDirection()} is {@code direction} (defined in the world coordinate
	 * system).
	 * <p>
	 * The Camera {@link #position()} is not modified. The Camera is rotated so that the horizon (defined by its
	 * {@link #upVector()}) is preserved.
	 * 
	 * @see #lookAt(Vec)
	 * @see #setUpVector(Vec)
	 */
	public void setViewDirection(Vec direction) {
		if (Util.zero(direction.squaredNorm()))
			return;

		Vec xAxis = direction.cross(upVector());
		if (Util.zero(xAxis.squaredNorm())) {
			// target is aligned with upVector, this means a rotation around X axis
			// X axis is then unchanged, let's keep it !
			xAxis = frame().xAxis();
		}

		Quat q = new Quat();
		q.fromRotatedBasis(xAxis, xAxis.cross(direction), Vec.multiply(direction, -1));
		frame().setOrientationWithConstraint(q);
	}

	/**
	 * Sets the {@link #orientation()} of the Camera using polar coordinates.
	 * <p>
	 * {@code theta} rotates the Camera around its Y axis, and then {@code phi} rotates it around its X axis.
	 * <p>
	 * The polar coordinates are defined in the world coordinates system: {@code theta = phi = 0} means that the Camera is
	 * directed towards the world Z axis. Both angles are expressed in radians.
	 * <p>
	 * The {@link #position()} of the Camera is unchanged, you may want to call {@link #showEntireScene()} after this
	 * method to move the Camera.
	 * 
	 * @see #setUpVector(Vec)
	 */
	public void setOrientation(float theta, float phi) {
		// TODO: need check.
		Vec axis = new Vec(0.0f, 1.0f, 0.0f);
		Quat rot1 = new Quat(axis, theta);
		axis.set(-(float) Math.cos(theta), 0.0f, (float) Math.sin(theta));
		Quat rot2 = new Quat(axis, phi);
		setOrientation(Quat.multiply(rot1, rot2));
	}

	@Override
	public void setOrientation(Rotation q) {
		frame().setOrientation(q);
		frame().updateFlyUpVector();
	}

	// 3. FRUSTUM

	/**
	 * Returns the Camera.Type.
	 * <p>
	 * Set by {@link #setType(Type)}.
	 * <p>
	 * A {@link remixlab.dandelion.core.Camera.Type#PERSPECTIVE} Camera uses a classical projection mainly defined by its
	 * {@link #fieldOfView()}.
	 * <p>
	 * With a {@link remixlab.dandelion.core.Camera.Type#ORTHOGRAPHIC} {@link #type()}, the {@link #fieldOfView()} is
	 * meaningless and the width and height of the Camera frustum are inferred from the distance to the {@link #anchor()}
	 * using {@link #getBoundaryWidthHeight()}.
	 * <p>
	 * Both types use {@link #zNear()} and {@link #zFar()} (to define their clipping planes) and {@link #aspectRatio()}
	 * (for frustum shape).
	 */
	public final Type type() {
		return tp;
	}

	/**
	 * Defines the Camera {@link #type()}.
	 * <p>
	 * Changing the Camera Type alters the viewport and the objects' size can be changed. This method guarantees that the
	 * two frustum match in a plane normal to {@link #viewDirection()}, passing through the arcball reference point.
	 */
	public final void setType(Type type) {
		if (type != type()) {
			modified();
			this.tp = type;
		}
	}

	/**
	 * Returns the vertical field of view of the Camera (in radians).
	 * <p>
	 * Value is set using {@link #setFieldOfView(float)}. Default value is pi/3 radians. This value is meaningless if the
	 * Camera {@link #type()} is {@link remixlab.dandelion.core.Camera.Type#ORTHOGRAPHIC}.
	 * <p>
	 * The field of view corresponds the one used in {@code gluPerspective} (see manual). It sets the Y (vertical)
	 * aperture of the Camera. The X (horizontal) angle is inferred from the window aspect ratio (see
	 * {@link #aspectRatio()} and {@link #horizontalFieldOfView()}).
	 * <p>
	 * Use {@link #setFOVToFitScene()} to adapt the {@link #fieldOfView()} to a given scene.
	 */
	public float fieldOfView() {
		return 2.0f * (float) Math.atan(frame().scaling().y());
	}

	/**
	 * Sets the vertical {@link #fieldOfView()} of the Camera (in radians).
	 * <p>
	 * Note that {@link #focusDistance()} is set to {@link #sceneRadius()} / tan( {@link #fieldOfView()}/2) by this
	 * method.
	 */
	public void setFieldOfView(float fov) {
		// fldOfView = fov;
		frame().setScaling((float) Math.tan(fov / 2.0f),
				(float) Math.tan(fov / 2.0f),
				(float) Math.tan(fov / 2.0f));
		setFocusDistance(sceneRadius() / frame().scaling().y());
	}

	/**
	 * Changes the Camera {@link #fieldOfView()} so that the entire scene (defined by
	 * {@link remixlab.dandelion.core.AbstractScene#center()} and {@link remixlab.dandelion.core.AbstractScene#radius()}
	 * is visible from the Camera {@link #position()}.
	 * <p>
	 * The {@link #position()} and {@link #orientation()} of the Camera are not modified and you first have to orientate
	 * the Camera in order to actually see the scene (see {@link #lookAt(Vec)}, {@link #showEntireScene()} or
	 * {@link #fitBall(Vec, float)}).
	 * <p>
	 * This method is especially useful for <i>shadow maps</i> computation. Use the Camera positioning tools (
	 * {@link #setPosition(Vec)}, {@link #lookAt(Vec)}) to position a Camera at the light position. Then use this method
	 * to define the {@link #fieldOfView()} so that the shadow map resolution is optimally used:
	 * <p>
	 * {@code // The light camera needs size hints in order to optimize its
	 * fieldOfView} <br>
	 * {@code lightCamera.setSceneRadius(sceneRadius());} <br>
	 * {@code lightCamera.setSceneCenter(sceneCenter());} <br>
	 * {@code // Place the light camera} <br>
	 * {@code lightCamera.setPosition(lightFrame.position());} <br>
	 * {@code lightCamera.lookAt(sceneCenter());} <br>
	 * {@code lightCamera.setFOVToFitScene();} <br>
	 * <p>
	 * <b>Attention:</b> The {@link #fieldOfView()} is clamped to M_PI/2.0. This happens when the Camera is at a distance
	 * lower than sqrt(2.0) * sceneRadius() from the sceneCenter(). It optimizes the shadow map resolution, although it
	 * may miss some parts of the scene.
	 */
	public void setFOVToFitScene() {
		if (distanceToSceneCenter() > (float) Math.sqrt(2.0f) * sceneRadius())
			setFieldOfView(2.0f * (float) Math.asin(sceneRadius() / distanceToSceneCenter()));
		else
			setFieldOfView((float) Math.PI / 2.0f);
	}

	/**
	 * Returns the horizontal field of view of the Camera (in radians).
	 * <p>
	 * Value is set using {@link #setHorizontalFieldOfView(float)} or {@link #setFieldOfView(float)}. These values are
	 * always linked by: {@code
	 * horizontalFieldOfView() = 2.0 * atan ( tan(fieldOfView()/2.0) *
	 * aspectRatio() )}.
	 */
	public float horizontalFieldOfView() {
		// return 2.0f * (float) Math.atan((float) Math.tan(fieldOfView() / 2.0f) * aspectRatio());
		return 2.0f * (float) Math.atan(frame().scaling().x() * aspectRatio());
	}

	/**
	 * Sets the {@link #horizontalFieldOfView()} of the Camera (in radians).
	 * <p>
	 * {@link #horizontalFieldOfView()} and {@link #fieldOfView()} are linked by the {@link #aspectRatio()}. This method
	 * actually calls {@code setFieldOfView(( 2.0 * atan (tan(hfov / 2.0) / aspectRatio()) ))} so that a call to
	 * {@link #horizontalFieldOfView()} returns the expected value.
	 */
	public void setHorizontalFieldOfView(float hfov) {
		setFieldOfView(2.0f * (float) Math.atan((float) Math.tan(hfov / 2.0f) / aspectRatio()));
	}

	/**
	 * Returns the near clipping plane distance used by the Camera projection matrix.
	 * <p>
	 * The clipping planes' positions depend on the {@link #sceneRadius()} and {@link #sceneCenter()} rather than being
	 * fixed small-enough and large-enough values. A good scene dimension approximation will hence result in an optimal
	 * precision of the z-buffer.
	 * <p>
	 * The near clipping plane is positioned at a distance equal to {@link #zClippingCoefficient()} *
	 * {@link #sceneRadius()} in front of the {@link #sceneCenter()}: {@code distanceToSceneCenter() -
	 * zClippingCoefficient() * sceneRadius()}
	 * <p>
	 * In order to prevent negative or too small {@link #zNear()} values (which would degrade the z precision),
	 * {@link #zNearCoefficient()} is used when the Camera is inside the {@link #sceneRadius()} sphere:
	 * <p>
	 * {@code zMin = zNearCoefficient() * zClippingCoefficient() * sceneRadius();} <br>
	 * {@code zNear = zMin;}<br>
	 * {@code // With an ORTHOGRAPHIC type, the value is simply clamped to 0.0} <br>
	 * <p>
	 * See also the {@link #zFar()}, {@link #zClippingCoefficient()} and {@link #zNearCoefficient()} documentations.
	 * <p>
	 * If you need a completely different zNear computation, overload the {@link #zNear()} and {@link #zFar()} methods in
	 * a new class that publicly inherits from Camera and use {@link remixlab.dandelion.core.AbstractScene#setEye(Eye)}.
	 * <p>
	 * <b>Attention:</b> The value is always positive although the clipping plane is positioned at a negative z value in
	 * the Camera coordinate system. This follows the {@code gluPerspective} standard.
	 * 
	 * @see #zFar()
	 */
	public float zNear() {
		float z = distanceToSceneCenter() - zClippingCoefficient() * sceneRadius();

		// Prevents negative or null zNear values.
		final float zMin = zNearCoefficient() * zClippingCoefficient() * sceneRadius();
		if (z < zMin)
			switch (type()) {
			case PERSPECTIVE:
				z = zMin;
				break;
			case ORTHOGRAPHIC:
				z = 0.0f;
				break;
			}
		return z;
	}

	/**
	 * Returns the far clipping plane distance used by the Camera projection matrix.
	 * <p>
	 * The far clipping plane is positioned at a distance equal to {@code zClippingCoefficient() * sceneRadius()} behind
	 * the {@link #sceneCenter()}:
	 * <p>
	 * {@code zFar = distanceToSceneCenter() + zClippingCoefficient()*sceneRadius()}
	 * 
	 * @see #zNear()
	 */
	public float zFar() {
		return distanceToSceneCenter() + zClippingCoefficient() * sceneRadius();
	}

	/**
	 * Returns the coefficient which is used to set {@link #zNear()} when the Camera is inside the sphere defined by
	 * {@link #sceneCenter()} and {@link #zClippingCoefficient()} * {@link #sceneRadius()}.
	 * <p>
	 * In that case, the {@link #zNear()} value is set to
	 * {@code zNearCoefficient() * zClippingCoefficient() * sceneRadius()}. See the {@code zNear()} documentation for
	 * details.
	 * <p>
	 * Default value is 0.005, which is appropriate for most applications. In case you need a high dynamic ZBuffer
	 * precision, you can increase this value (~0.1). A lower value will prevent clipping of very close objects at the
	 * expense of a worst Z precision.
	 * <p>
	 * Only meaningful when Camera type is PERSPECTIVE.
	 */
	public float zNearCoefficient() {
		return zNearCoef;
	}

	/**
	 * Sets the {@link #zNearCoefficient()} value.
	 */
	public void setZNearCoefficient(float coef) {
		if (coef != zNearCoef)
			modified();
		zNearCoef = coef;
	}

	/**
	 * Returns the coefficient used to position the near and far clipping planes.
	 * <p>
	 * The near (resp. far) clipping plane is positioned at a distance equal to
	 * {@code zClippingCoefficient() * sceneRadius()} in front of (resp. behind) the {@link #sceneCenter()}. This
	 * guarantees an optimal use of the z-buffer range and minimizes aliasing. See the {@link #zNear()} and
	 * {@link #zFar()} documentations.
	 * <p>
	 * Default value is square root of 3.0 (so that a cube of size {@link #sceneRadius()} is not clipped).
	 * <p>
	 * However, since the {@link #sceneRadius()} is used for other purposes (see showEntireScene(), flySpeed(), ...) and
	 * you may want to change this value to define more precisely the location of the clipping planes. See also
	 * {@link #zNearCoefficient()}.
	 */
	public float zClippingCoefficient() {
		return zClippingCoef;
	}

	/**
	 * Sets the {@link #zClippingCoefficient()} value.
	 */
	public void setZClippingCoefficient(float coef) {
		if (coef != zClippingCoef)
			modified();
		zClippingCoef = coef;
	}

	@Override
	public float pixelSceneRatio(Vec position) {
		switch (type()) {
		case PERSPECTIVE:
			return 2.0f * Math.abs((frame().coordinatesOf(position, false)).vec[2])
					* (float) Math.tan(fieldOfView() / 2.0f) / screenHeight();
		case ORTHOGRAPHIC: {
			float[] wh = getBoundaryWidthHeight();
			return 2.0f * wh[1] / screenHeight();
		}
		}
		return 1.0f;
	}

	@Override
	public boolean pointIsVisible(Vec point) {
		if (!scene.areBoundaryEquationsEnabled())
			System.out.println("The camera frustum plane equations (needed by pointIsVisible) may be outdated. Please "
					+ "enable automatic updates of the equations in your PApplet.setup "
					+ "with Scene.enableBoundaryEquations()");
		for (int i = 0; i < 6; ++i)
			if (distanceToBoundary(i, point) > 0)
				return false;
		return true;
	}

	@Override
	public Visibility ballIsVisible(Vec center, float radius) {
		if (!scene.areBoundaryEquationsEnabled())
			System.out.println("The camera frustum plane equations (needed by sphereIsVisible) may be outdated. Please "
					+ "enable automatic updates of the equations in your PApplet.setup "
					+ "with Scene.enableBoundaryEquations()");
		boolean allInForAllPlanes = true;
		for (int i = 0; i < 6; ++i) {
			float d = distanceToBoundary(i, center);
			if (d > radius)
				return Camera.Visibility.INVISIBLE;
			if ((d > 0) || (-d < radius))
				allInForAllPlanes = false;
		}
		if (allInForAllPlanes)
			return Camera.Visibility.VISIBLE;
		return Camera.Visibility.SEMIVISIBLE;
	}

	@Override
	public Visibility boxIsVisible(Vec p1, Vec p2) {
		if (!scene.areBoundaryEquationsEnabled())
			System.out.println("The camera frustum plane equations (needed by aaBoxIsVisible) may be outdated. Please "
					+ "enable automatic updates of the equations in your PApplet.setup "
					+ "with Scene.enableBoundaryEquations()");
		boolean allInForAllPlanes = true;
		for (int i = 0; i < 6; ++i) {
			boolean allOut = true;
			for (int c = 0; c < 8; ++c) {
				Vec pos = new Vec(((c & 4) != 0) ? p1.vec[0] : p2.vec[0],
						((c & 2) != 0) ? p1.vec[1] : p2.vec[1],
						((c & 1) != 0) ? p1.vec[2] : p2.vec[2]);
				if (distanceToBoundary(i, pos) > 0.0)
					allInForAllPlanes = false;
				else
					allOut = false;
			}
			// The eight points are on the outside side of this plane
			if (allOut)
				return Camera.Visibility.INVISIBLE;
		}

		if (allInForAllPlanes)
			return Camera.Visibility.VISIBLE;

		// Too conservative, but tangent cases are too expensive to detect
		return Camera.Visibility.SEMIVISIBLE;
	}

	@Override
	public float[][] computeBoundaryEquations() {
		return computeBoundaryEquations(new float[6][4]);
	}

	@Override
	public float[][] computeBoundaryEquations(float[][] coef) {
		// soft check:
		if (coef == null || (coef.length == 0))
			coef = new float[6][4];
		else if ((coef.length != 6) || (coef[0].length != 4))
			coef = new float[6][4];

		// Computed once and for all
		Vec pos = position();
		Vec viewDir = viewDirection();
		Vec up = upVector();
		Vec right = rightVector();

		float posViewDir = Vec.dot(pos, viewDir);

		switch (type()) {
		case PERSPECTIVE: {
			float hhfov = horizontalFieldOfView() / 2.0f;
			float chhfov = (float) Math.cos(hhfov);
			float shhfov = (float) Math.sin(hhfov);
			normal[0] = Vec.multiply(viewDir, -shhfov);
			normal[1] = Vec.add(normal[0], Vec.multiply(right, chhfov));
			normal[0] = Vec.add(normal[0], Vec.multiply(right, -chhfov));
			normal[2] = Vec.multiply(viewDir, -1);
			normal[3] = viewDir;

			float hfov = fieldOfView() / 2.0f;
			float chfov = (float) Math.cos(hfov);
			float shfov = (float) Math.sin(hfov);
			normal[4] = Vec.multiply(viewDir, -shfov);
			normal[5] = Vec.add(normal[4], Vec.multiply(up, -chfov));
			normal[4] = Vec.add(normal[4], Vec.multiply(up, chfov));

			for (int i = 0; i < 2; ++i)
				dist[i] = Vec.dot(pos, normal[i]);
			for (int j = 4; j < 6; ++j)
				dist[j] = Vec.dot(pos, normal[j]);

			// Natural equations are:
			// dist[0,1,4,5] = pos * normal[0,1,4,5];
			// dist[2] = (pos + zNear() * viewDir) * normal[2];
			// dist[3] = (pos + zFar() * viewDir) * normal[3];

			// 2 times less computations using expanded/merged equations. Dir vectors
			// are normalized.
			float posRightCosHH = chhfov * Vec.dot(pos, right);
			dist[0] = -shhfov * posViewDir;
			dist[1] = dist[0] + posRightCosHH;
			dist[0] = dist[0] - posRightCosHH;
			float posUpCosH = chfov * Vec.dot(pos, up);
			dist[4] = -shfov * posViewDir;
			dist[5] = dist[4] - posUpCosH;
			dist[4] = dist[4] + posUpCosH;
			break;
		}
		case ORTHOGRAPHIC:
			normal[0] = Vec.multiply(right, -1);
			normal[1] = right;
			normal[4] = up;
			normal[5] = Vec.multiply(up, -1);

			float[] wh = getBoundaryWidthHeight();
			dist[0] = Vec.dot(Vec.subtract(pos, Vec.multiply(right, wh[0])), normal[0]);
			dist[1] = Vec.dot(Vec.add(pos, Vec.multiply(right, wh[0])), normal[1]);
			dist[4] = Vec.dot(Vec.add(pos, Vec.multiply(up, wh[1])), normal[4]);
			dist[5] = Vec.dot(Vec.subtract(pos, Vec.multiply(up, wh[1])), normal[5]);
			break;
		}

		// Front and far planes are identical for both camera types.
		normal[2] = Vec.multiply(viewDir, -1);
		normal[3] = viewDir;
		dist[2] = -posViewDir - zNear();
		dist[3] = posViewDir + zFar();

		for (int i = 0; i < 6; ++i) {
			coef[i][0] = normal[i].vec[0];
			coef[i][1] = normal[i].vec[1];
			coef[i][2] = normal[i].vec[2];
			coef[i][3] = dist[i];
		}

		return coef;
	}

	/**
	 * Convenience function that simply calls {@code coneIsBackFacing(new Cone(normals))}.
	 * 
	 * @see #coneIsBackFacing(Cone)
	 * @see #coneIsBackFacing(Vec[])
	 */
	public boolean coneIsBackFacing(ArrayList<Vec> normals) {
		return coneIsBackFacing(new Cone(normals));
	}

	/**
	 * Convenience function that simply calls {coneIsBackFacing(viewDirection, new Cone(normals))}.
	 * 
	 * @param viewDirection
	 *          Cached camera view direction.
	 * @param normals
	 *          cone of normals.
	 */
	public boolean coneIsBackFacing(Vec viewDirection, ArrayList<Vec> normals) {
		return coneIsBackFacing(viewDirection, new Cone(normals));
	}

	/**
	 * Convenience function that simply calls {@code coneIsBackFacing(new Cone(normals))}.
	 * 
	 * @see #coneIsBackFacing(Cone)
	 * @see #coneIsBackFacing(ArrayList)
	 */
	public boolean coneIsBackFacing(Vec[] normals) {
		return coneIsBackFacing(new Cone(normals));
	}

	/**
	 * Convenience function that simply returns {@code coneIsBackFacing(viewDirection, new Cone(normals))}.
	 * 
	 * @param viewDirection
	 *          Cached camera view direction.
	 * @param normals
	 *          cone of normals.
	 */
	public boolean coneIsBackFacing(Vec viewDirection, Vec[] normals) {
		return coneIsBackFacing(viewDirection, new Cone(normals));
	}

	/**
	 * Convenience function that simply returns {@code coneIsBackFacing(cone.axis(), cone.angle())}.
	 * 
	 * @see #coneIsBackFacing(Vec, float)
	 * @see #faceIsBackFacing(Vec, Vec, Vec)
	 */
	public boolean coneIsBackFacing(Cone cone) {
		return coneIsBackFacing(cone.axis(), cone.angle());
	}

	/**
	 * Convenience function that simply returns {@code coneIsBackFacing(viewDirection, cone.axis(), cone.angle())}.
	 * 
	 * @param viewDirection
	 *          cached camera view direction.
	 * @param cone
	 *          cone of normals
	 */
	public boolean coneIsBackFacing(Vec viewDirection, Cone cone) {
		return coneIsBackFacing(viewDirection, cone.axis(), cone.angle());
	}

	/**
	 * Convinience funtion that simply returns {@code coneIsBackFacing(viewDirection(), axis, angle)}.
	 * <p>
	 * Non-cached version of {@link #coneIsBackFacing(Vec, Vec, float)}
	 */
	public boolean coneIsBackFacing(Vec axis, float angle) {
		return coneIsBackFacing(viewDirection(), axis, angle);
	}

	/**
	 * Returns {@code true} if the given cone is back facing the camera. Otherwise returns {@code false}.
	 * 
	 * @param viewDirection
	 *          cached view direction
	 * @param axis
	 *          normalized cone axis
	 * @param angle
	 *          cone angle
	 * 
	 * @see #coneIsBackFacing(Cone)
	 * @see #faceIsBackFacing(Vec, Vec, Vec)
	 */
	public boolean coneIsBackFacing(Vec viewDirection, Vec axis, float angle) {
		if (angle < HALF_PI) {
			float phi = (float) Math.acos(Vec.dot(axis, viewDirection));
			if (phi >= HALF_PI)
				return false;
			if ((phi + angle) >= HALF_PI)
				return false;
			return true;
		}
		return false;
	}

	/**
	 * Returns {@code true} if the given face is back facing the camera. Otherwise returns {@code false}.
	 * <p>
	 * <b>Attention:</b> This method is not computationally optimized. If you call it several times with no change in the
	 * matrices, you should buffer the matrices (modelview, projection and then viewport) to speed-up the queries.
	 * 
	 * @param a
	 *          first face vertex
	 * @param b
	 *          second face vertex
	 * @param c
	 *          third face vertex
	 */
	public boolean faceIsBackFacing(Vec a, Vec b, Vec c) {
		Vec v1 = Vec.subtract(projectedCoordinatesOf(a), projectedCoordinatesOf(b));
		Vec v2 = Vec.subtract(projectedCoordinatesOf(b), projectedCoordinatesOf(c));
		return v1.cross(v2).vec[2] <= 0;
	}

	// 4. SCENE RADIUS AND CENTER

	@Override
	public void setSceneRadius(float radius) {
		super.setSceneRadius(radius);
		setFocusDistance(sceneRadius() / (float) Math.tan(fieldOfView() / 2.0f));
	}

	@Override
	public float distanceToSceneCenter() {
		// return Math.abs((frame().coordinatesOf(sceneCenter())).vec[2]);//before scln
		// if there were not validateScaling this should do it:
		// Vec zCam = frame().magnitude().z() > 0 ? frame().zAxis() : frame().zAxis(false);
		Vec zCam = frame().zAxis();
		Vec cam2SceneCenter = Vec.subtract(position(), sceneCenter());
		return Math.abs(Vec.dot(cam2SceneCenter, zCam));
	}

	@Override
	public float distanceToAnchor() {
		// return Math.abs(cameraCoordinatesOf(arcballReferencePoint()).vec[2]);//before scln
		// if there were not validateScaling this should do it:
		// Vec zCam = frame().magnitude().z() > 0 ? frame().zAxis() : frame().zAxis(false);
		Vec zCam = frame().zAxis();
		Vec cam2anchor = Vec.subtract(position(), anchor());
		return Math.abs(Vec.dot(cam2anchor, zCam));
	}

	@Override
	public void setSceneBoundingBox(Vec min, Vec max) {
		setSceneCenter(Vec.multiply(Vec.add(min, max), 1 / 2.0f));
		setSceneRadius(0.5f * (Vec.subtract(max, min)).magnitude());
	}

	// 5. ANCHOR REFERENCE POINT

	@Override
	public boolean setAnchorFromPixel(Point pixel) {
		WorldPoint wP = pointUnderPixel(pixel);
		if (wP.found)
			setAnchor(wP.point);
		return wP.found;
	}

	@Override
	public boolean setSceneCenterFromPixel(Point pixel) {
		WorldPoint wP = pointUnderPixel(pixel);
		if (wP.found)
			setSceneCenter(wP.point);
		return wP.found;
	}

	/**
	 * Returns the coordinates of the 3D point located at {@code pixel} (x,y) on screen.
	 * <p>
	 * Override this method in your jogl-based camera class.
	 * <p>
	 * Current implementation always returns {@code WorlPoint.found = false} (dummy value), meaning that no point was
	 * found under pixel.
	 */
	public WorldPoint pointUnderPixel(Point pixel) {
		return scene.pointUnderPixel(pixel);
	}

	// 8. PROCESSING MATRICES

	@Override
	public void computeView() {
		Quat q = (Quat) frame().orientation();

		float q00 = 2.0f * q.quat[0] * q.quat[0];
		float q11 = 2.0f * q.quat[1] * q.quat[1];
		float q22 = 2.0f * q.quat[2] * q.quat[2];

		float q01 = 2.0f * q.quat[0] * q.quat[1];
		float q02 = 2.0f * q.quat[0] * q.quat[2];
		float q03 = 2.0f * q.quat[0] * q.quat[3];

		float q12 = 2.0f * q.quat[1] * q.quat[2];
		float q13 = 2.0f * q.quat[1] * q.quat[3];
		float q23 = 2.0f * q.quat[2] * q.quat[3];

		viewMat.mat[0] = 1.0f - q11 - q22;
		viewMat.mat[1] = q01 - q23;
		viewMat.mat[2] = q02 + q13;
		viewMat.mat[3] = 0.0f;

		viewMat.mat[4] = q01 + q23;
		viewMat.mat[5] = 1.0f - q22 - q00;
		viewMat.mat[6] = q12 - q03;
		viewMat.mat[7] = 0.0f;

		viewMat.mat[8] = q02 - q13;
		viewMat.mat[9] = q12 + q03;
		viewMat.mat[10] = 1.0f - q11 - q00;
		viewMat.mat[11] = 0.0f;

		Vec t = q.inverseRotate(frame().position());

		viewMat.mat[12] = -t.vec[0];
		viewMat.mat[13] = -t.vec[1];
		viewMat.mat[14] = -t.vec[2];
		viewMat.mat[15] = 1.0f;
	}

	@Override
	protected float rescalingOrthoFactor() {
		float toAnchor = this.distanceToAnchor();
		return (2 * (Util.zero(toAnchor) ? Util.FLOAT_EPS : toAnchor) * rapK / screenHeight());
	}

	@Override
	public void setAnchor(Vec rap) {
		float prevDist = distanceToAnchor();
		frame().setAnchor(rap);
		float newDist = distanceToAnchor();
		if ((Util.nonZero(prevDist)) && (Util.nonZero(newDist)))
			rapK *= prevDist / newDist;
	}

	@Override
	public void computeProjection() {
		float ZNear = zNear();
		float ZFar = zFar();

		switch (type()) {
		case PERSPECTIVE: {
			// #CONNECTION# all non null coefficients were set to 0.0 in
			// constructor.
			// float f = 1.0f / (float) Math.tan(fieldOfView() / 2.0f);
			// projectionMat.mat[0] = f / aspectRatio();
			// projectionMat.mat[5] = scene.isLeftHanded() ? -f : f;
			projectionMat.mat[0] = 1 / (frame().scaling().x() * this.aspectRatio());
			projectionMat.mat[5] = 1 / (scene.isLeftHanded() ? -frame().scaling().y() : frame().scaling().y());
			projectionMat.mat[10] = (ZNear + ZFar) / (ZNear - ZFar);
			projectionMat.mat[11] = -1.0f;
			projectionMat.mat[14] = 2.0f * ZNear * ZFar / (ZNear - ZFar);
			projectionMat.mat[15] = 0.0f;
			// same as gluPerspective( 180.0*fieldOfView()/M_PI, aspectRatio(), zNear(), zFar() );
			break;
		}
		case ORTHOGRAPHIC: {
			float[] wh = getBoundaryWidthHeight();
			projectionMat.mat[0] = 1.0f / wh[0];
			projectionMat.mat[5] = (scene.isLeftHanded() ? -1.0f : 1.0f) / wh[1];
			projectionMat.mat[10] = -2.0f / (ZFar - ZNear);
			projectionMat.mat[11] = 0.0f;
			projectionMat.mat[14] = -(ZFar + ZNear) / (ZFar - ZNear);
			projectionMat.mat[15] = 1.0f;
			// same as glOrtho( -w, w, -h, h, zNear(), zFar() );
			break;
		}
		}
	}

	@Override
	public void fromView(Mat mv, boolean recompute) {
		Quat q = new Quat();
		q.fromMatrix(mv);
		setOrientation(q);
		setPosition(Vec.multiply(q.rotate(new Vec(mv.mat[12], mv.mat[13], mv.mat[14])), -1));
		if (recompute)
			this.computeView();
	}

	// 9. WORLD -> CAMERA

	// 10. 2D -> 3D

	/**
	 * Gives the coefficients of a 3D half-line passing through the Camera eye and pixel (x,y).
	 * <p>
	 * The origin of the half line (eye position) is stored in {@code orig}, while {@code dir} contains the properly
	 * oriented and normalized direction of the half line.
	 * <p>
	 * {@code x} and {@code y} are expressed in Processing format (origin in the upper left corner). Use
	 * {@link #screenHeight()} - y to convert to processing scene units.
	 * <p>
	 * This method is useful for analytical intersection in a selection method.
	 */
	public void convertClickToLine(final Point pixelInput, Vec orig, Vec dir) {
		Point pixel = new Point(pixelInput.x(), pixelInput.y());

		// lef-handed coordinate system correction
		if (scene.isLeftHanded())
			pixel.setY(screenHeight() - pixelInput.y());

		switch (type()) {
		case PERSPECTIVE:
			orig.set(position());
			dir.set(new Vec(((2.0f * pixel.x() / screenWidth()) - 1.0f) * (float) Math.tan(fieldOfView() / 2.0f)
					* aspectRatio(),
					((2.0f * (screenHeight() - pixel.y()) / screenHeight()) - 1.0f)
							* (float) Math.tan(fieldOfView() / 2.0f),
					-1.0f));
			dir.set(Vec.subtract(frame().inverseCoordinatesOf(dir, false), orig));
			dir.normalize();
			break;

		case ORTHOGRAPHIC: {
			float[] wh = getBoundaryWidthHeight();
			orig.set(new Vec((2.0f * pixel.x() / screenWidth() - 1.0f) * wh[0],
					-(2.0f * pixel.y() / screenHeight() - 1.0f) * wh[1], 0.0f));
			orig.set(frame().inverseCoordinatesOf(orig, false));
			dir.set(viewDirection());
			break;
		}
		}
	}

	// 12. POSITION TOOLS

	@Override
	public void lookAt(Vec target) {
		setViewDirection(Vec.subtract(target, position()));
	}

	@Override
	public Vec at() {
		return Vec.add(position(), viewDirection());
	}

	@Override
	public void fitBall(Vec center, float radius) {
		float distance = 0.0f;
		switch (type()) {
		case PERSPECTIVE: {
			float yview = radius / (float) Math.sin(fieldOfView() / 2.0f);
			float xview = radius / (float) Math.sin(horizontalFieldOfView() / 2.0f);
			distance = Math.max(xview, yview);
			break;
		}
		case ORTHOGRAPHIC: {
			// distance = Vec.dot(Vec.subtract(center, arcballReferencePoint()), viewDirection()) + (radius / orthoCoef);
			distance = Vec.dot(Vec.subtract(center, anchor()), viewDirection())
					+ (radius / Math.max(frame().scaling().x(), frame().scaling().y()));
			break;
		}
		}

		Vec newPos = Vec.subtract(center, Vec.multiply(viewDirection(), distance));
		frame().setPositionWithConstraint(newPos);
	}

	@Override
	public void fitBoundingBox(Vec min, Vec max) {
		float diameter = Math.max(Math.abs(max.vec[1] - min.vec[1]), Math.abs(max.vec[0] - min.vec[0]));
		diameter = Math.max(Math.abs(max.vec[2] - min.vec[2]), diameter);
		fitBall(Vec.multiply(Vec.add(min, max), 0.5f), 0.5f * diameter);
	}

	@Override
	public void fitScreenRegion(Rect rectangle) {
		Vec vd = viewDirection();
		float distToPlane = distanceToSceneCenter();

		Point center = new Point((int) rectangle.centerX(), (int) rectangle.centerY());

		Vec orig = new Vec();
		Vec dir = new Vec();
		convertClickToLine(center, orig, dir);
		Vec newCenter = Vec.add(orig, Vec.multiply(dir, (distToPlane / Vec.dot(dir, vd))));

		convertClickToLine(new Point(rectangle.x(), center.y()), orig, dir);
		final Vec pointX = Vec.add(orig, Vec.multiply(dir, (distToPlane / Vec.dot(dir, vd))));

		convertClickToLine(new Point(center.x(), rectangle.y()), orig, dir);
		final Vec pointY = Vec.add(orig, Vec.multiply(dir, (distToPlane / Vec.dot(dir, vd))));

		float distance = 0.0f;
		switch (type()) {
		case PERSPECTIVE: {
			final float distX = Vec.distance(pointX, newCenter) / (float) Math.sin(horizontalFieldOfView() / 2.0f);
			final float distY = Vec.distance(pointY, newCenter) / (float) Math.sin(fieldOfView() / 2.0f);

			distance = Math.max(distX, distY);
			break;
		}
		case ORTHOGRAPHIC: {
			final float dist = Vec.dot(Vec.subtract(newCenter, anchor()), vd);
			// final float distX = Vec.distance(pointX, newCenter) / frame().scaling().x() / ((aspectRatio() < 1.0) ? 1.0f :
			// aspectRatio());
			// final float distY = Vec.distance(pointY, newCenter) / frame().scaling().y() / ((aspectRatio() < 1.0) ? 1.0f /
			// aspectRatio() : 1.0f);
			// final float distX = Vec.distance(pointX, newCenter) / Math.max(frame().scaling().x(),frame().scaling().y()) /
			// ((aspectRatio() < 1.0) ? 1.0f : aspectRatio());
			// final float distY = Vec.distance(pointY, newCenter) / Math.max(frame().scaling().x(),frame().scaling().y()) /
			// ((aspectRatio() < 1.0) ? 1.0f / aspectRatio() : 1.0f);
			final float distX = Vec.distance(pointX, newCenter) / Math.max(frame().scaling().x(), frame().scaling().y())
					/ aspectRatio();
			final float distY = Vec.distance(pointY, newCenter) / Math.max(frame().scaling().x(), frame().scaling().y())
					/ 1.0f;

			distance = dist + Math.max(distX, distY);

			break;
		}
		}

		frame().setPositionWithConstraint(Vec.subtract(newCenter, Vec.multiply(vd, distance)));
	}

	@Override
	public void showEntireScene() {
		fitBall(sceneCenter(), sceneRadius());
	}

	@Override
	public void interpolateToZoomOnPixel(Point pixel) {
		WorldPoint target = pointUnderPixel(pixel);

		if (!target.found) {
			System.out.println("No object under pixel was found");
			// return target;
			return;
		}

		interpolateToZoomOnPixel(target);
	}

	protected WorldPoint interpolateToZoomOnPixel(WorldPoint target) {
		float coef = 0.1f;

		if (!target.found)
			return target;

		// if (interpolationKfi.interpolationIsStarted())
		// interpolationKfi.stopInterpolation();
		if (anyInterpolationIsStarted())
			stopAllInterpolations();

		interpolationKfi.deletePath();
		interpolationKfi.addKeyFrame(new InteractiveFrame(scene, frame()));

		interpolationKfi.addKeyFrame(new Frame(frame().orientation(),
				Vec.add(Vec.multiply(frame().position(),
						0.3f), Vec.multiply(target.point, 0.7f))), 0.4f);

		// Small hack: attach a temporary frame to take advantage of lookAt without
		// modifying frame
		tempFrame = new InteractiveEyeFrame(this);
		InteractiveEyeFrame originalFrame = frame();
		tempFrame.setPosition(Vec.add(Vec.multiply(frame().position(), coef), Vec.multiply(target.point, (1.0f - coef))));
		tempFrame.setOrientation(frame().orientation().get());
		setFrame(tempFrame);
		lookAt(target.point);
		setFrame(originalFrame);

		interpolationKfi.addKeyFrame(tempFrame, 1.0f);
		interpolationKfi.startInterpolation();

		return target;
	}

	// 13. STEREO PARAMETERS

	/**
	 * Returns the user's inter-ocular distance (in meters). Default value is 0.062m, which fits most people.
	 * 
	 * @see #setIODistance(float)
	 */
	public float IODistance() {
		return IODist;
	}

	/**
	 * Sets the {@link #IODistance()}.
	 */
	public void setIODistance(float distance) {
		IODist = distance;
	}

	/**
	 * Returns the physical distance between the user's eyes and the screen (in meters).
	 * <p>
	 * Default value is 0.5m.
	 * <p>
	 * Value is set using {@link #setPhysicalDistanceToScreen(float)}.
	 * <p>
	 * physicalDistanceToScreen() and {@link #focusDistance()} represent the same distance. The first one is expressed in
	 * physical real world units, while the latter is expressed in processing virtual world units. Use their ratio to
	 * convert distances between these worlds.
	 */
	public float physicalDistanceToScreen() {
		return physicalDist2Scrn;
	}

	/**
	 * Sets the {@link #physicalDistanceToScreen()}.
	 */
	public void setPhysicalDistanceToScreen(float distance) {
		physicalDist2Scrn = distance;
	}

	/**
	 * Returns the physical screen width, in meters. Default value is 0.4m (average monitor).
	 * <p>
	 * Used for stereo display only. Set using {@link #setPhysicalScreenWidth(float)}.
	 * <p>
	 * See {@link #physicalDistanceToScreen()} for reality center automatic configuration.
	 */
	public float physicalScreenWidth() {
		return physicalScrnWidth;
	}

	/**
	 * Sets the physical screen (monitor or projected wall) width (in meters).
	 */
	public void setPhysicalScreenWidth(float width) {
		physicalScrnWidth = width;
	}

	/**
	 * Returns the focus distance used by stereo display, expressed in processing units.
	 * <p>
	 * This is the distance in the virtual world between the Camera and the plane where the horizontal stereo parallax is
	 * null (the stereo left and right images are superimposed).
	 * <p>
	 * This distance is the virtual world equivalent of the real-world {@link #physicalDistanceToScreen()}.
	 * <p>
	 * <b>attention:</b> This value is modified by Scene.setSceneRadius(), setSceneRadius() and
	 * {@link #setFieldOfView(float)}. When one of these values is modified, {@link #focusDistance()} is set to
	 * {@link #sceneRadius()} / tan({@link #fieldOfView()}/2), which provides good results.
	 */
	public float focusDistance() {
		return focusDist;
	}

	/**
	 * Sets the focusDistance(), in processing scene units.
	 */
	public void setFocusDistance(float distance) {
		if (distance != focusDist)
			modified();
		focusDist = distance;
	}
}
