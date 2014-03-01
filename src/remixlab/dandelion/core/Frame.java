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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import remixlab.dandelion.constraint.*;
import remixlab.dandelion.geom.Mat;
import remixlab.dandelion.geom.Orientable;
import remixlab.dandelion.geom.Quat;
import remixlab.dandelion.geom.Rot;
import remixlab.dandelion.geom.Vec;
import remixlab.fpstiming.TimingHandler;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;
import remixlab.util.Util;

/**
 * A Frame is a 3D coordinate system, represented by a {@link #position()} and an {@link #orientation()}. The order of
 * these transformations is important: the Frame is first translated and then rotated around the new translated origin.
 * <p>
 * In rare situations a frame can be {@link #linkTo(Frame)}, meaning that it will share its {@link #translation()},
 * {@link #rotation()}, {@link #referenceFrame()}, and {@link #constraint()} with the other frame, which can useful for
 * some off-screen scenes.
 */
public class Frame implements Copyable, Constants {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
						append(krnl).
						// append(list).
						append(linkedFramesList).
						append(srcFrame).
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

		Frame other = (Frame) obj;
		return new EqualsBuilder()
						.append(krnl, other.krnl)
						// .append(list, other.list)
						.append(linkedFramesList, other.linkedFramesList)
						.append(srcFrame, other.srcFrame)
						.isEquals();
	}

	protected abstract class AbstractFrameKernel implements Copyable {
		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37).
							append(trans).
							append(rot).
							append(scl).
							append(refFrame).
							append(constr).
							append(lastUpdate).
							toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			FrameKernel3D other = (FrameKernel3D) obj;
			return new EqualsBuilder()
							.appendSuper(super.equals(obj))
							.append(trans, other.trans)
							.append(scl, other.scl)
							.append(rot, other.rot)
							.append(refFrame, other.refFrame)
							.append(constr, other.constr)
							.isEquals();
		}

		protected Vec trans;
		protected Vec scl;
		protected Orientable rot;
		protected Frame refFrame;
		protected Constraint constr;
		protected long lastUpdate;

		public AbstractFrameKernel() {
			trans = new Vec(0, 0, 0);
			scl = new Vec(1, 1, 1);
			rot = null;
			refFrame = null;
			constr = null;
			lastUpdate = 0;
		}

		public AbstractFrameKernel(Orientable r, Vec p, Vec s) {
			trans = new Vec(p.x(), p.y(), p.z());
			scl = new Vec(1, 1, 1);
			setScaling(s);
			rot = r.get();
			refFrame = null;
			constr = null;
			lastUpdate = 0;
		}

		public AbstractFrameKernel(Orientable r, Vec p) {
			trans = new Vec(p.x(), p.y(), p.z());
			scl = new Vec(1, 1, 1);
			rot = r.get();
			refFrame = null;
			constr = null;
			lastUpdate = 0;
		}

		protected AbstractFrameKernel(AbstractFrameKernel other) {
			trans = new Vec(other.translation().vec[0], other.translation().vec[1], other.translation().vec[2]);
			rot = other.rotation().get();
			scl = other.scaling().get();
			refFrame = other.referenceFrame();
			constr = other.constraint();
			lastUpdate = other.lastUpdate();
		}

		public final Vec translation() {
			return trans;
		}

		public final void setTranslation(Vec t) {
			trans = t;
			modified();
		}

		public final Vec scaling() {
			return scl;
		}

		public final Vec inverseScaling() {
			return new Vec(1 / scl.x(), 1 / scl.y(), 1 / scl.z());
		}

		public final void setScaling(Vec s) {
			if (Util.zero(s.x())) {
				System.out.println("Setting x scale value to zero is not allowed");
				s.setX(scl.x());
			}
			if (Util.zero(s.y())) {
				System.out.println("Setting y scale value to zero is not allowed");
				s.setY(scl.y());
			}
			if (Util.zero(s.z())) {
				System.out.println("Setting z scale value to zero is not allowed");
				s.setZ(scl.z());
			}
			scl = s;
			modified();
		}

		public final Orientable rotation() {
			return rot;
		}

		public final Orientable inverseRotation() {
			return rot.inverse();
		}

		public final void setRotation(Orientable r) {
			rot = r;
			modified();
		}

		public Constraint constraint() {
			return constr;
		}

		public final Frame referenceFrame() {
			return refFrame;
		}

		public void setConstraint(Constraint c) {
			constr = c;
		}

		public void translate(Vec t) {
			translation().add(t);
			modified();
		}

		public void rotate(Orientable q) {
			rotation().compose(q);
			if (this instanceof FrameKernel3D)
				((Quat) rotation()).normalize(); // Prevents numerical drift
			modified();
		}

		public void scale(Vec s) {
			setScaling(Vec.multiply(scaling(), s));
		}

		public boolean isInverted() {
			boolean inverted = false;

			if (referenceFrame() != null) {
				if (this instanceof FrameKernel2D)
					inverted = referenceFrame().magnitude().x() * referenceFrame().magnitude().y() < 0;
				else
					inverted = referenceFrame().magnitude().x() * referenceFrame().magnitude().y()
									* referenceFrame().magnitude().z() < 0;
			}

			return inverted;
		}

		protected void modified() {
			lastUpdate = TimingHandler.frameCount;
		}

		public long lastUpdate() {
			return lastUpdate;
		}

		public final void setReferenceFrame(Frame rFrame) {
			if (settingAsReferenceFrameWillCreateALoop(rFrame))
				System.out.println("Frame.setReferenceFrame would create a loop in Frame hierarchy");
			else {
				boolean identical = (referenceFrame() == rFrame);
				refFrame = rFrame;
				if (!identical)
					modified();
			}
		}

		public final boolean settingAsReferenceFrameWillCreateALoop(Frame frame) {
			Frame f = frame;
			while (f != null) {
				if (f == Frame.this)
					return true;
				f = f.referenceFrame();
			}
			return false;
		}

		public void fromRotatedBasis(Vec x, Vec y, Vec z) {
			rotation().fromRotatedBasis(x, y, z);
			modified();
		}
	}

	/**
	 * Internal class that holds the main frame attributes. This class is useful to linking frames (i.e., to share these
	 * attributes).
	 */
	public class FrameKernel3D extends AbstractFrameKernel {
		public FrameKernel3D() {
			rot = new Quat();
		}

		public FrameKernel3D(Quat r, Vec p, Vec s) {
			super(r, p, s);
		}

		public FrameKernel3D(Quat r, Vec p) {
			super(r, p);
		}

		protected FrameKernel3D(FrameKernel3D other) {
			super(other);
		}

		@Override
		public FrameKernel3D get() {
			return new FrameKernel3D(this);
		}
	}

	public class FrameKernel2D extends AbstractFrameKernel {
		public FrameKernel2D() {
			rot = new Rot();
		}

		public FrameKernel2D(Rot r, Vec p, Vec s) {
			super(r, p, s);
		}

		public FrameKernel2D(Rot r, Vec p) {
			super(r, p);
		}

		protected FrameKernel2D(FrameKernel2D other) {
			super(other);
		}

		@Override
		public FrameKernel2D get() {
			return new FrameKernel2D(this);
		}
	}

	protected AbstractFrameKernel krnl;
	protected List<Frame> linkedFramesList;
	protected Frame srcFrame;

	public Frame() {
		this(true);
	}

	/**
	 * Creates a default Frame.
	 * <p>
	 * Its {@link #position()} is (0,0,0) and it has an identity {@link #orientation()} Orientable. The
	 * {@link #referenceFrame()} and the {@link #constraint()} are {@code null}.
	 */
	public Frame(boolean three_d) {
		if (three_d)
			krnl = new FrameKernel3D();
		else
			krnl = new FrameKernel2D();
		linkedFramesList = new ArrayList<Frame>();
		srcFrame = null;
	}

	public Frame(Orientable r, Vec p, Vec s) {
		if (r instanceof Quat)
			krnl = new FrameKernel3D((Quat) r, p, s);
		else if (r instanceof Rot)
			krnl = new FrameKernel2D((Rot) r, p, s);

		linkedFramesList = new ArrayList<Frame>();
		srcFrame = null;
	}

	/**
	 * Creates a Frame with a {@link #position()} and an {@link #orientation()}.
	 * <p>
	 * See the Vector3D and Orientable documentations for convenient constructors and methods.
	 * <p>
	 * The Frame is defined in the world coordinate system (its {@link #referenceFrame()} is {@code null}). It has a
	 * {@code null} associated {@link #constraint()}.
	 */
	public Frame(Orientable r, Vec p) {
		if (r instanceof Quat)
			krnl = new FrameKernel3D((Quat) r, p);
		else if (r instanceof Rot)
			krnl = new FrameKernel2D((Rot) r, p);

		linkedFramesList = new ArrayList<Frame>();
		srcFrame = null;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 *          the Frame containing the object to be copied
	 */
	protected Frame(Frame other) {
		if (other.is3D())
			krnl = new FrameKernel3D((FrameKernel3D) other.kernel());
		else
			krnl = new FrameKernel2D((FrameKernel2D) other.kernel());
		linkedFramesList = new ArrayList<Frame>();
		Iterator<Frame> iterator = other.linkedFramesList.iterator();
		while (iterator.hasNext())
			linkedFramesList.add(iterator.next());
		srcFrame = other.srcFrame;
	}

	/**
	 * Calls {@code Frame(Frame)} (which is protected) and returns a copy of {@code this} object.
	 * 
	 * @see #Frame(Frame)
	 */
	public Frame get() {
		return new Frame(this);
	}

	public AbstractFrameKernel kernel() {
		return krnl;
	}

	public void setKernel(AbstractFrameKernel k) {
		krnl = k;
	}

	public boolean is2D() {
		return !is3D();
	}

	public boolean is3D() {
		return kernel().rot instanceof Quat;
	}

	public boolean isInverted() {
		return kernel().isInverted();
	}

	public final Vec scaling() {
		return kernel().scaling();
	}

	public long lastUpdate() {
		return kernel().lastUpdate();
	}

	/**
	 * Returns the Frame translation, defined with respect to the {@link #referenceFrame()}.
	 * <p>
	 * Use {@link #position()} to get the result in the world coordinates. These two values are identical when the
	 * {@link #referenceFrame()} is {@code null} (default).
	 * 
	 * @see #setTranslation(Vec)
	 * @see #setTranslationWithConstraint(Vec)
	 */
	public final Vec translation() {
		return kernel().translation();
	}

	/**
	 * Returns the Frame rotation, defined with respect to the {@link #referenceFrame()} (i.e, the current Orientable
	 * orientation).
	 * <p>
	 * Use {@link #orientation()} to get the result in the world coordinates. These two values are identical when the
	 * {@link #referenceFrame()} is {@code null} (default).
	 * 
	 * @see #setRotation(Orientable)
	 * @see #setRotationWithConstraint(Orientable)
	 */
	public final Orientable rotation() {
		return kernel().rotation();
	}

	/**
	 * Returns the reference Frame, in which coordinates system the Frame is defined.
	 * <p>
	 * The {@link #translation()} {@link #rotation()} of the Frame are defined with respect to the reference Frame
	 * coordinate system. A {@code null} reference Frame (default value) means that the Frame is defined in the world
	 * coordinate system.
	 * <p>
	 * Use {@link #position()} and {@link #orientation()} to recursively convert values along the reference Frame chain
	 * and to get values expressed in the world coordinate system. The values match when the reference Frame {@code null}.
	 * <p>
	 * Use {@link #setReferenceFrame(Frame)} to set this value and create a Frame hierarchy. Convenient functions allow
	 * you to convert 3D coordinates from one Frame to another: see {@link #coordinatesOf(Vec)},
	 * {@link #localCoordinatesOf(Vec)} , {@link #coordinatesOfIn(Vec, Frame)} and their inverse functions.
	 * <p>
	 * Vectors can also be converted using {@link #transformOf(Vec)}, {@link #transformOfIn(Vec, Frame)},
	 * {@link #localTransformOf(Vec)} and their inverse functions.
	 */
	public final Frame referenceFrame() {
		return kernel().referenceFrame();
	}

	/**
	 * Returns the current constraint applied to the Frame.
	 * <p>
	 * A {@code null} value (default) means that no Constraint is used to filter the Frame translation and rotation.
	 * <p>
	 * See the Constraint class documentation for details.
	 */
	public Constraint constraint() {
		return kernel().constraint();
	}

	/**
	 * Links this frame (referred to as the requested frame) to {@code sourceFrame}, meaning that this frame will take
	 * (and share by reference) the {@link #translation()}, {@link #rotation()}, {@link #referenceFrame()}, and
	 * {@link #constraint()} from the {@code sourceFrame}. This can useful for some off-screen scenes, e.g., to link a
	 * frame defined in one scene to the camera frame defined in other scene (see the CameraCrane example).
	 * <p>
	 * <b>Note:</b> Linking frames has the following properties:
	 * <ol>
	 * <li>A frame can be linked only to another frame (referred to as the source frame).</li>
	 * <li>A source frame can be linked by from many (requested) frames.</li>
	 * <li>A source frame can't be linked to another (source) frame, i.e., it can only receive links form other frames.</li>
	 * </ol>
	 * 
	 * @param sourceFrame
	 *          the frame to link this frame with.
	 * @return true if this frame can successfully being linked to the frame. False otherwise.
	 * 
	 * @see #linkFrom(Frame)
	 * @see #unlink()
	 * @see #unlinkFrom(Frame)
	 * @see #isLinked()
	 * @see #areLinkedTogether(Frame)
	 */
	public boolean linkTo(Frame sourceFrame) {
		// avoid loops
		if ((!linkedFramesList.isEmpty()) || sourceFrame.linkedFramesList.contains(this) || (sourceFrame == this))
			return false;

		if (sourceFrame.linkedFramesList.add(this)) {
			srcFrame = sourceFrame;
			setKernel(srcFrame.kernel());
			return true;
		}

		return false;
	}

	/**
	 * Attempts to link the {@code requestedFrame} to this frame.
	 * <p>
	 * See {@link #linkTo(Frame)} for the rules and terminology applying to the linking process.
	 * 
	 * @param requestedFrame
	 *          the frame that is requesting a link to this frame.
	 * @return true if the requested frame can successfully being linked to this frame. False otherwise.
	 * 
	 * @see #linkTo(Frame)
	 * @see #unlink()
	 * @see #unlinkFrom(Frame)
	 * @see #isLinked()
	 * @see #areLinkedTogether(Frame)
	 */
	public boolean linkFrom(Frame requestedFrame) {
		// avoid loops
		if ((!requestedFrame.linkedFramesList.isEmpty()) || linkedFramesList.contains(this) || (requestedFrame == this))
			return false;

		if (linkedFramesList.add(requestedFrame)) {
			requestedFrame.srcFrame = this;
			requestedFrame.setKernel(kernel());
			return true;
		}

		return false;
	}

	/**
	 * Unlinks this frame from its source frame. Does nothing if this frame is not linked to another frame.
	 * <p>
	 * See {@link #linkTo(Frame)} for the rules and terminology applying to the linking process.
	 * 
	 * @return true if succeeded otherwise returns false.
	 * 
	 * @see #linkTo(Frame)
	 * @see #linkFrom(Frame)
	 * @see #unlinkFrom(Frame)
	 * @see #isLinked()
	 * @see #areLinkedTogether(Frame)
	 */
	public boolean unlink() {
		boolean result = false;
		if (srcFrame != null) {
			result = srcFrame.linkedFramesList.remove(this);
			if (result) {
				if (is3D())
					setKernel(new FrameKernel3D((Quat) srcFrame.rotation(), srcFrame.translation(), srcFrame.scaling()));
				else
					setKernel(new FrameKernel2D((Rot) srcFrame.rotation(), srcFrame.translation(), srcFrame.scaling()));
				srcFrame = null;
			}
		}
		return result;
	}

	/**
	 * Unlinks the requested frame from this frame. Does nothing if the frames are not linked together (
	 * {@link #areLinkedTogether(Frame)}).
	 * <p>
	 * See {@link #linkTo(Frame)} for the rules and terminology applying to the linking process.
	 * 
	 * @return true if succeeded otherwise returns false.
	 * 
	 * @see #linkTo(Frame)
	 * @see #linkFrom(Frame)
	 * @see #unlink()
	 * @see #isLinked()
	 * @see #areLinkedTogether(Frame)
	 */
	public boolean unlinkFrom(Frame requestedFrame) {
		boolean result = false;
		if ((srcFrame == null) && (requestedFrame != this)) {
			result = linkedFramesList.remove(requestedFrame);
			if (result) {
				if (is3D())
					requestedFrame.setKernel(new FrameKernel3D((Quat) rotation(), translation(), scaling()));
				else
					requestedFrame.setKernel(new FrameKernel2D((Rot) rotation(), translation(), scaling()));
				requestedFrame.srcFrame = null;
			}
		}
		return result;
	}

	/**
	 * Returns true if this frame is linked to a source frame or if this frame acts as the source frame of other frames.
	 * Otherwise returns false.
	 * <p>
	 * See {@link #linkTo(Frame)} for the rules and terminology applying to the linking process.
	 * 
	 * @see #linkTo(Frame)
	 * @see #linkFrom(Frame)
	 * @see #unlink()
	 * @see #unlinkFrom(Frame)
	 * @see #areLinkedTogether(Frame)
	 */
	public boolean isLinked() {
		if ((srcFrame != null) || (!linkedFramesList.isEmpty()))
			return true;
		return false;
	}

	/**
	 * Returns true if this frame is linked with {@code sourceFrame}. Otherwise returns false.
	 * <p>
	 * See {@link #linkTo(Frame)} for the rules and terminology applying to the linking process.
	 * 
	 * @see #linkTo(Frame)
	 * @see #linkFrom(Frame)
	 * @see #unlink()
	 * @see #unlinkFrom(Frame)
	 * @see #isLinked()
	 */
	public boolean areLinkedTogether(Frame sourceFrame) {
		if (sourceFrame == srcFrame)
			return true;
		if (linkedFramesList.contains(sourceFrame))
			return true;
		return false;
	}

	/**
	 * Sets the {@link #translation()} of the frame, locally defined with respect to the {@link #referenceFrame()}.
	 * <p>
	 * Use {@link #setPosition(Vec)} to define the world coordinates {@link #position()}. Use
	 * {@link #setTranslationWithConstraint(Vec)} to take into account the potential {@link #constraint()} of the Frame.
	 */
	public final void setTranslation(Vec t) {
		kernel().setTranslation(t);
	}

	/**
	 * Same as {@link #setTranslation(Vec)}, but with {@code float} parameters.
	 */
	public final void setTranslation(float x, float y, float z) {
		setTranslation(new Vec(x, y, z));
	}

	public final void setScaling(Vec s) {
		kernel().setScaling(s);
	}

	public final void setScaling(float x, float y, float z) {
		setScaling(new Vec(x, y, z));
	}

	public final void setScaling(float x, float y) {
		setScaling(new Vec(x, y, 1));
	}

	public final void setScaling(float s) {
		setScaling(new Vec(s, s, s));
	}

	public final void setScalingWithConstraint(Vec s) {
		Vec deltaS = Vec.divide(s, this.scaling());
		if (constraint() != null)
			deltaS = constraint().constrainScaling(deltaS, this);

		kernel().scale(deltaS);
	}

	/**
	 * Same as {@link #setTranslation(Vec)}, but if there's a {@link #constraint()} it is satisfied (without modifying
	 * {@code translation}).
	 * 
	 * @see #setRotationWithConstraint(Orientable)
	 * @see #setPositionWithConstraint(Vec)
	 */
	public final void setTranslationWithConstraint(Vec translation) {
		Vec deltaT = Vec.subtract(translation, this.translation());
		if (constraint() != null)
			deltaT = constraint().constrainTranslation(deltaT, this);

		kernel().translate(deltaT);

		/**
		 * translation.x = this.translation().x; translation.y = this.translation().y; translation.z = this.translation().z;
		 */
	}

	/**
	 * Set the current rotation. See the different Orientable constructors.
	 * <p>
	 * Sets the {@link #rotation()} of the Frame, locally defined with respect to the {@link #referenceFrame()}.
	 * <p>
	 * Use {@link #setOrientation(Orientable)} to define the world coordinates {@link #orientation()}. The potential
	 * {@link #constraint()} of the Frame is not taken into account, use {@link #setRotationWithConstraint(Orientable)}
	 * instead.
	 * 
	 * @see #setRotationWithConstraint(Orientable)
	 * @see #rotation()
	 * @see #setTranslation(Vec)
	 */
	public final void setRotation(Orientable r) {
		kernel().setRotation(r);
	}

	/**
	 * Same as {@link #setRotation(Orientable)} but with {@code float} Orientable parameters.
	 */
	public final void setRotation(float x, float y, float z, float w) {
		setRotation(new Quat(x, y, z, w));
	}

	public final void setRotation(float a) {
		if (is3D())
			throw new RuntimeException("Scene should be in 2d for this method to work");
		setRotation(new Rot(a));
	}

	/**
	 * Same as {@link #setRotation(Orientable)}, but if there's a {@link #constraint()} it's satisfied (without modifying
	 * {@code rotation}).
	 * 
	 * @see #setTranslationWithConstraint(Vec)
	 * @see #setOrientationWithConstraint(Orientable)
	 */
	public final void setRotationWithConstraint(Orientable rotation) {
		Orientable deltaQ;

		if (is3D())
			deltaQ = Quat.compose(rotation().inverse(), rotation);
		else
			deltaQ = Rot.compose(rotation().inverse(), rotation);

		if (constraint() != null)
			deltaQ = constraint().constrainRotation(deltaQ, this);

		deltaQ.normalize(); // Prevent numerical drift

		kernel().rotate(deltaQ);
		// rotation.x = this.rotation().x;
		// rotation.y = this.rotation().y;
		// rotation.z = this.rotation().z;
		// rotation.w = this.rotation().w;
	}

	/**
	 * Sets the {@link #referenceFrame()} of the Frame.
	 * <p>
	 * The Frame {@link #translation()} and {@link #rotation()} are then defined in the {@link #referenceFrame()}
	 * coordinate system.
	 * <p>
	 * Use {@link #position()} and {@link #orientation()} to express these in the world coordinate system.
	 * <p>
	 * Using this method, you can create a hierarchy of Frames. This hierarchy needs to be a tree, which root is the world
	 * coordinate system (i.e., {@code null} {@link #referenceFrame()}). No action is performed if setting
	 * {@code refFrame} as the {@link #referenceFrame()} would create a loop in the Frame hierarchy.
	 */
	public final void setReferenceFrame(Frame rFrame) {
		kernel().setReferenceFrame(rFrame);
	}

	/**
	 * Sets the {@link #constraint()} attached to the Frame.
	 * <p>
	 * A {@code null} value means no constraint.
	 */
	public void setConstraint(Constraint c) {
		kernel().setConstraint(c);
	}

	/**
	 * Returns the orientation of the Frame, defined in the world coordinate system.
	 * 
	 * @see #position()
	 * @see #setOrientation(Orientable)
	 * @see #rotation()
	 */
	public final Orientable orientation() {
		Orientable res = rotation().get();
		Frame fr = referenceFrame();
		while (fr != null) {
			if (is3D())
				res = Quat.compose(fr.rotation(), res);
			else
				res = Rot.compose(fr.rotation(), res);
			fr = fr.referenceFrame();
		}
		return res;
	}

	/**
	 * Sets the {@link #position()} of the Frame, defined in the world coordinate system.
	 * <p>
	 * Use {@link #setTranslation(Vec)} to define the local Frame translation (with respect to the
	 * {@link #referenceFrame()}). The potential {@link #constraint()} of the Frame is not taken into account, use
	 * {@link #setPositionWithConstraint(Vec)} instead.
	 */
	public final void setPosition(Vec p) {
		if (referenceFrame() != null)
			setTranslation(referenceFrame().coordinatesOf(p));
		else
			setTranslation(p);
	}

	/**
	 * Same as {@link #setPosition(float, float, float)}, but with {@code float} parameters.
	 */
	public final void setPosition(float x, float y, float z) {
		setPosition(new Vec(x, y, z));
	}

	public final void setMagnitude(Vec s) {
		Frame refFrame = referenceFrame();
		if (refFrame != null)
			setScaling(s.x() / refFrame.magnitude().x(), s.y() / refFrame.magnitude().y(), s.z() / refFrame.magnitude().z());
		else
			setScaling(s.x(), s.y(), s.z());
	}

	public final void setMagnitudeWithConstraint(Vec mag) {
		if (referenceFrame() != null)
			mag = Vec.divide(mag, referenceFrame().magnitude());

		setScalingWithConstraint(mag);
	}

	public final void setMagnitude(float sx, float sy, float sz) {
		setMagnitude(new Vec(sx, sy, sz));
	}

	public final void setMagnitude(float sx, float sy) {
		setMagnitude(new Vec(sx, sy, 1));
	}

	public final void setMagnitude(float s) {
		setMagnitude(new Vec(s, s, s));
	}

	public Vec magnitude() {
		if (referenceFrame() != null)
			return Vec.multiply(referenceFrame().magnitude(), scaling());
		else
			return scaling().get();
	}

	public Vec inverseMagnitude() {
		Vec vec = magnitude();
		return new Vec(1 / vec.x(), 1 / vec.y(), 1 / vec.z());
	}

	/**
	 * Same as {@link #setPosition(float, float, float)}, but with {@code float} parameters.
	 */
	public final void setPosition(float x, float y) {
		setPosition(new Vec(x, y));
	}

	/**
	 * Same as {@link #setPosition(Vec)}, but if there's a {@link #constraint()} it is satisfied (without modifying
	 * {@code position}).
	 * 
	 * @see #setOrientationWithConstraint(Orientable)
	 * @see #setTranslationWithConstraint(Vec)
	 */
	public final void setPositionWithConstraint(Vec position) {
		if (referenceFrame() != null)
			position = referenceFrame().coordinatesOf(position);

		setTranslationWithConstraint(position);
	}

	/**
	 * Sets the {@link #orientation()} of the Frame, defined in the world coordinate system.
	 * <p>
	 * Use {@link #setRotation(Orientable)} to define the local frame rotation (with respect to the
	 * {@link #referenceFrame()}). The potential {@link #constraint()} of the Frame is not taken into account, use
	 * {@link #setOrientationWithConstraint(Orientable)} instead.
	 */
	public final void setOrientation(Orientable q) {
		if (referenceFrame() != null) {
			if (is3D())
				setRotation(Quat.compose(referenceFrame().orientation().inverse(), q));
			else
				setRotation(Rot.compose(referenceFrame().orientation().inverse(), q));
		}
		else
			setRotation(q);
	}

	/**
	 * Same as {@link #setOrientation(Orientable)}, but with {@code float} parameters.
	 */
	public final void setOrientation(float x, float y, float z, float w) {
		setOrientation(new Quat(x, y, z, w));
	}

	/**
	 * Same as {@link #setOrientation(Orientable)}, but if there's a {@link #constraint()} it is satisfied (without
	 * modifying {@code orientation}).
	 * 
	 * @see #setPositionWithConstraint(Vec)
	 * @see #setRotationWithConstraint(Orientable)
	 */
	public final void setOrientationWithConstraint(Orientable orientation) {
		if (referenceFrame() != null) {
			if (is3D())
				orientation = Quat.compose(referenceFrame().orientation().inverse(), orientation);
			else
				orientation = Rot.compose(referenceFrame().orientation().inverse(), orientation);
		}

		setRotationWithConstraint(orientation);
	}

	/**
	 * Returns the position of the Frame, defined in the world coordinate system.
	 * 
	 * @see #orientation()
	 * @see #setPosition(Vec)
	 * @see #translation()
	 */
	public final Vec position() {
		return inverseCoordinatesOf(new Vec(0, 0, 0));
	}

	/**
	 * Translates the Frame according to {@code t}, locally defined with respect to the {@link #referenceFrame()}.
	 * <p>
	 * If there's a {@link #constraint()} it is satisfied. Hence the translation actually applied to the Frame may differ
	 * from {@code t} (since it can be filtered by the {@link #constraint()}). Use {@link #setTranslation(Vec)} to
	 * directly translate the Frame without taking the {@link #constraint()} into account.
	 * 
	 * @see #rotate(Orientable)
	 */
	public final void translate(Vec t) {
		if (constraint() != null)
			kernel().translate(constraint().constrainTranslation(t, this));
		else
			kernel().translate(t);
	}

	/**
	 * Translates the Frame according to {@code t}, locally defined with respect to the {@link #referenceFrame()}. Calls
	 * {@link #modified()}.
	 * <p>
	 * If there's a {@link #constraint()} it is satisfied. Hence the translation actually applied to the Frame may differ
	 * from {@code t} (since it can be filtered by the {@link #constraint()}). Use {@code translate(t, false)} to retrieve
	 * the filtered translation value and {@code translate(t, true)} to keep the original value of {@code t}. Use
	 * {@link #setTranslation(Vec)} to directly translate the Frame without taking the {@link #constraint()} into account.
	 * 
	 * @see #rotate(Orientable)
	 */
	/*
	 * public final Vec filteredTranslate(Vec t) { if (constraint() != null) t = constraint().constrainTranslation(t,
	 * this); kernel().translate(t); return t.get(); }
	 */

	/**
	 * public final void translate(Vector3D t, boolean keepArg) { Vector3D o = new Vector3D(t.vec[0], t.vec[1], t.vec[2]);
	 * if (constraint() != null) { o = constraint().constrainTranslation(t, this); if (!keepArg) { t.vec[0] = o.vec[0];
	 * t.vec[1] = o.vec[1]; t.vec[2] = o.vec[2]; } } kernel().translate(o); }
	 */

	/**
	 * Same as {@link #translate(Vec)} but with {@code float} parameters.
	 */
	public final void translate(float x, float y, float z) {
		translate(new Vec(x, y, z));
	}

	/**
	 * Same as {@link #translate(Vec)} but with {@code float} parameters.
	 */
	public final void translate(float x, float y) {
		translate(new Vec(x, y));
	}

	public void scale(Vec s) {
		if (constraint() != null)
			kernel().scale(constraint().constrainScaling(s, this));
		else
			kernel().scale(s);
	}

	/*
	 * public Vec filteredScale(Vec s) { if( constraint() != null ) s = constraint().constrainScaling(s, this);
	 * kernel().scale(s); return s.get(); }
	 */

	public void scale(float x, float y, float z) {
		scale(new Vec(x, y, z));
	}

	public void scale(float x, float y) {
		scale(new Vec(x, y, 1));
	}

	public void scale(float s) {
		scale(new Vec(s, s, s));
	}

	/**
	 * Rotates the Frame by {@code q} (defined in the Frame coordinate system): {@code R = R*q}.
	 * <p>
	 * If there's a {@link #constraint()} it is satisfied. Hence the rotation actually applied to the Frame may differ
	 * from {@code q} (since it can be filtered by the {@link #constraint()}). Use {@link #setRotation(Orientable)} to
	 * directly rotate the Frame without taking the {@link #constraint()} into account.
	 * 
	 * @see #translate(Vec)
	 */
	public final void rotate(Orientable q) {
		if (constraint() != null)
			kernel().rotate(constraint().constrainRotation(q, this));
		else
			kernel().rotate(q);
	}

	/**
	 * Rotates the Frame by {@code q} (defined in the Frame coordinate system): {@code R = R*q}. Calls {@link #modified()}
	 * .
	 * <p>
	 * If there's a {@link #constraint()} it is satisfied. Hence the rotation actually applied to the Frame may differ
	 * from {@code q} (since it can be filtered by the {@link #constraint()}). Use {@code rotate(q, false)} to retrieve
	 * the filtered rotation value and {@code rotate(q, true)} to keep the original value of {@code q}. Use
	 * {@link #setRotation(Orientable)} to directly rotate the Frame without taking the {@link #constraint()} into
	 * account.
	 * 
	 * @see #translate(Vec)
	 */
	/*
	 * public final Orientable filteredRotate(Orientable q) { if (constraint() != null) q =
	 * constraint().constrainRotation(q, this); kernel().rotate(q); return q.get(); }
	 */

	/**
	 * Rotates the Frame by {@code q} (defined in the Frame coordinate system): {@code R = R*q}. Calls {@link #modified()}
	 * .
	 * <p>
	 * If there's a {@link #constraint()} it is satisfied. Hence the rotation actually applied to the Frame may differ
	 * from {@code q} (since it can be filtered by the {@link #constraint()}). Use {@code rotate(q, false)} to retrieve
	 * the filtered rotation value and {@code rotate(q, true)} to keep the original value of {@code q}. Use
	 * {@link #setRotation(Orientable)} to directly rotate the Frame without taking the {@link #constraint()} into
	 * account.
	 * 
	 * @see #translate(Vector3D)
	 */
	/**
	 * public final void rotate(Orientable q, boolean keepArg) { Orientable o = q.get(); if (constraint() != null) { o =
	 * constraint().constrainRotation(q, this); if (!keepArg) { if(is3D()) { ((Quaternion)q).quat[0] =
	 * ((Quaternion)o).quat[0]; ((Quaternion)q).quat[1] = ((Quaternion)o).quat[1]; ((Quaternion)q).quat[2] =
	 * ((Quaternion)o).quat[2]; ((Quaternion)q).quat[3] = ((Quaternion)o).quat[3]; } else { ((Rotation)q).angle =
	 * ((Rotation)o).angle; } } } kernel().rotate(o); }
	 */

	/**
	 * Same as {@link #rotate(Orientable)} but with {@code float} Orientable parameters.
	 */
	public final void rotate(float x, float y, float z, float w) {
		rotate(new Quat(x, y, z, w));
	}

	/**
	 * Makes the Frame {@link #rotate(Orientable)} by {@code rotation} around {@code point}.
	 * <p>
	 * {@code point} is defined in the world coordinate system, while the {@code rotation} axis is defined in the Frame
	 * coordinate system.
	 * <p>
	 * If the Frame has a {@link #constraint()}, {@code rotation} is first constrained using
	 * {@link remixlab.dandelion.constraint.Constraint#constrainRotation(Orientable, Frame)}. Hence the rotation actually
	 * applied to the Frame may differ from {@code rotation} (since it can be filtered by the {@link #constraint()}).
	 * <p>
	 * The translation which results from the filtered rotation around {@code point} is then computed and filtered using
	 * {@link remixlab.dandelion.constraint.Constraint#constrainTranslation(Vec, Frame)}.
	 */
	public void rotateAroundPoint(Orientable rotation, Vec point) {
		if (constraint() != null)
			rotation = constraint().constrainRotation(rotation, this);

		this.kernel().rotation().compose(rotation);
		if (is3D())
			this.kernel().rotation().normalize(); // Prevents numerical drift

		Orientable q;
		if (is3D())
			// TODO needs further testing
			// q = new Quaternion(inverseTransformOf(((Quaternion)rotation).axis()), rotation.angle());//orig
			q = new Quat(inverseTransformOf(((Quat) rotation).axis(), false), rotation.angle());
		// q = new Quaternion(orientation().rotate(((Quaternion)rotation).axis()), rotation.angle());
		else
			q = new Rot(rotation.angle());
		Vec t = Vec.add(point, q.rotate(Vec.subtract(position(), point)));
		t.subtract(kernel().translation());
		if (constraint() != null)
			kernel().translate(constraint().constrainTranslation(t, this));
		else
			kernel().translate(t);
	}

	/**
	 * Makes the Frame {@link #rotate(Orientable)} by {@code rotation} around {@code point}. Calls {@link #modified()}.
	 * <p>
	 * {@code point} is defined in the world coordinate system, while the {@code rotation} axis is defined in the Frame
	 * coordinate system.
	 * <p>
	 * If the Frame has a {@link #constraint()}, {@code rotation} is first constrained using
	 * {@link remixlab.dandelion.constraint.Constraint#constrainRotation(Orientable, Frame)}. Hence the rotation actually
	 * applied to the Frame may differ from {@code rotation} (since it can be filtered by the {@link #constraint()}). Use
	 * {@code rotateAroundPoint(rotation, point, false)} to retrieve the filtered rotation value and
	 * {@code rotateAroundPoint(rotation, point, true)} to keep the original value of {@code rotation}.
	 * <p>
	 * The translation which results from the filtered rotation around {@code point} is then computed and filtered using
	 * {@link remixlab.dandelion.constraint.Constraint#constrainTranslation(Vec, Frame)}.
	 */
	/*
	 * public final Orientable filteredRotateAroundPoint(Orientable rotation, Vec point) { if (constraint() != null)
	 * rotation = constraint().constrainRotation(rotation, this);
	 * 
	 * this.kernel().rotation().compose(rotation); if(is3D()) this.kernel().rotation().normalize(); // Prevents numerical
	 * drift
	 * 
	 * Orientable q; if(is3D()) q = new Quat(inverseTransformOf(((Quat)rotation).axis()), rotation.angle()); else q = new
	 * Rot(rotation.angle());
	 * 
	 * Vec t = Vec.add(point, q.rotate(Vec.sub(position(), point))); t.sub(kernel().translation());
	 * 
	 * if (constraint() != null) kernel().translate(constraint().constrainTranslation(t, this)); else
	 * kernel().translate(t); return rotation.get(); }
	 */

	/**
	 * Makes the Frame {@link #rotate(Orientable)} by {@code rotation} around {@code point}. Calls {@link #modified()}.
	 * <p>
	 * {@code point} is defined in the world coordinate system, while the {@code rotation} axis is defined in the Frame
	 * coordinate system.
	 * <p>
	 * If the Frame has a {@link #constraint()}, {@code rotation} is first constrained using
	 * {@link remixlab.remixcam.constraints.Constraint#constrainRotation(Orientable, VFrame)}. Hence the rotation actually
	 * applied to the Frame may differ from {@code rotation} (since it can be filtered by the {@link #constraint()}). Use
	 * {@code rotateAroundPoint(rotation, point, false)} to retrieve the filtered rotation value and
	 * {@code rotateAroundPoint(rotation, point, true)} to keep the original value of {@code rotation}.
	 * <p>
	 * The translation which results from the filtered rotation around {@code point} is then computed and filtered using
	 * {@link remixlab.remixcam.constraints.Constraint#constrainTranslation(Vector3D, VFrame)}.
	 */
	/**
	 * public final void rotateAroundPoint(Orientable rotation, Vector3D point, boolean keepArg) { Orientable q =
	 * rotation.get(); if (constraint() != null) { q = constraint().constrainRotation(rotation, this); if (!keepArg) {
	 * if(is3D()) { ((Quaternion)rotation).quat[0] = ((Quaternion)q).quat[0]; ((Quaternion)rotation).quat[1] =
	 * ((Quaternion)q).quat[1]; ((Quaternion)rotation).quat[2] = ((Quaternion)q).quat[2]; ((Quaternion)rotation).quat[3] =
	 * ((Quaternion)q).quat[3]; } else { ((Rotation)rotation).angle = ((Rotation)q).angle; } } }
	 * this.kernel().rotation().compose(q); if(is3D()) this.kernel().rotation().normalize(); // Prevents numerical drift
	 * 
	 * if(is3D()) q = new Quaternion(inverseTransformOf(((Quaternion)rotation).axis()), rotation.angle()); else q = new
	 * Rotation(rotation.angle());
	 * 
	 * Vector3D t = Vector3D.add(point, q.rotate(Vector3D.sub(position(), point))); t.sub(kernel().translation());
	 * 
	 * if (constraint() != null) kernel().translate(constraint().constrainTranslation(t, this)); else
	 * kernel().translate(t); }
	 */

	/**
	 * Convenience function that simply calls {@code alignWithFrame(frame, false,
	 * 0.85f)}
	 */
	public final void alignWithFrame(Frame frame) {
		alignWithFrame(frame, false, 0.85f);
	}

	/**
	 * Convenience function that simply calls {@code alignWithFrame(frame, move,
	 * 0.85f)}
	 */
	public final void alignWithFrame(Frame frame, boolean move) {
		alignWithFrame(frame, move, 0.85f);
	}

	/**
	 * Convenience function that simply calls {@code alignWithFrame(frame, false,
	 * threshold)}
	 */
	public final void alignWithFrame(Frame frame, float threshold) {
		alignWithFrame(frame, false, threshold);
	}

	/**
	 * Aligns the Frame with {@code frame}, so that two of their axis are parallel.
	 * <p>
	 * If one of the X, Y and Z axis of the Frame is almost parallel to any of the X, Y, or Z axis of {@code frame}, the
	 * Frame is rotated so that these two axis actually become parallel.
	 * <p>
	 * If, after this first rotation, two other axis are also almost parallel, a second alignment is performed. The two
	 * frames then have identical orientations, up to 90 degrees rotations.
	 * <p>
	 * {@code threshold} measures how close two axis must be to be considered parallel. It is compared with the absolute
	 * values of the dot product of the normalized axis.
	 * <p>
	 * When {@code move} is set to {@code true}, the Frame {@link #position()} is also affected by the alignment. The new
	 * Frame {@link #position()} is such that the {@code frame} frame position (computed with {@link #coordinatesOf(Vec)},
	 * in the Frame coordinates system) does not change.
	 * <p>
	 * {@code frame} may be {@code null} and then represents the world coordinate system (same convention than for the
	 * {@link #referenceFrame()}).
	 */
	public final void alignWithFrame(Frame frame, boolean move, float threshold) {
		if (is3D()) {
			Vec[][] directions = new Vec[2][3];

			for (int d = 0; d < 3; ++d) {
				Vec dir = new Vec((d == 0) ? 1.0f : 0.0f, (d == 1) ? 1.0f : 0.0f, (d == 2) ? 1.0f : 0.0f);
				if (frame != null)
					directions[0][d] = frame.inverseTransformOf(dir, false);
				else
					directions[0][d] = dir;
				directions[1][d] = inverseTransformOf(dir, false);
			}

			float maxProj = 0.0f;
			float proj;
			short[] index = new short[2];
			index[0] = index[1] = 0;

			Vec vec = new Vec(0.0f, 0.0f, 0.0f);
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 3; ++j) {
					vec.set(directions[0][i]);
					proj = Math.abs(vec.dot(directions[1][j]));
					if ((proj) >= maxProj) {
						index[0] = (short) i;
						index[1] = (short) j;
						maxProj = proj;
					}
				}
			}
			Frame old = new Frame(this); // correct line
			// VFrame old = this.get();// this call the get overloaded method and hence add the frame to the mouse grabber

			vec.set(directions[0][index[0]]);
			float coef = vec.dot(directions[1][index[1]]);

			if (Math.abs(coef) >= threshold) {
				vec.set(directions[0][index[0]]);
				Vec axis = vec.cross(directions[1][index[1]]);
				float angle = (float) Math.asin(axis.magnitude());
				if (coef >= 0.0)
					angle = -angle;
				// setOrientation(Quaternion(axis, angle) * orientation());
				Quat q = new Quat(axis, angle);
				q = Quat.multiply(((Quat) rotation()).inverse(), q);
				q = Quat.multiply(q, (Quat) orientation());
				rotate(q);

				// Try to align an other axis direction
				short d = (short) ((index[1] + 1) % 3);
				Vec dir = new Vec((d == 0) ? 1.0f : 0.0f, (d == 1) ? 1.0f : 0.0f, (d == 2) ? 1.0f : 0.0f);
				dir = inverseTransformOf(dir, false);

				float max = 0.0f;
				for (int i = 0; i < 3; ++i) {
					vec.set(directions[0][i]);
					proj = Math.abs(vec.dot(dir));
					if (proj > max) {
						index[0] = (short) i;
						max = proj;
					}
				}

				if (max >= threshold) {
					vec.set(directions[0][index[0]]);
					axis = vec.cross(dir);
					angle = (float) Math.asin(axis.magnitude());
					vec.set(directions[0][index[0]]);
					if (vec.dot(dir) >= 0.0)
						angle = -angle;
					// setOrientation(Quaternion(axis, angle) * orientation());
					q.fromAxisAngle(axis, angle);
					q = Quat.multiply(((Quat) rotation()).inverse(), q);
					q = Quat.multiply(q, (Quat) orientation());
					rotate(q);
				}
			}
			if (move) {
				Vec center = new Vec(0.0f, 0.0f, 0.0f);
				if (frame != null)
					center = frame.position();

				vec = Vec.subtract(center, orientation().rotate(old.coordinatesOf(center, false)));
				vec.subtract(translation());
				translate(vec);
			}
		}
		else {
			Rot o;
			if (frame != null)
				o = (Rot) frame.orientation();
			else
				o = new Rot();
			o.normalize(true);
			((Rot) orientation()).normalize(true);

			float angle = 0; // if( (-QUARTER_PI <= delta) && (delta < QUARTER_PI) )
			float delta = Math.abs(o.angle() - orientation().angle());

			if ((QUARTER_PI <= delta) && (delta < (HALF_PI + QUARTER_PI)))
				angle = HALF_PI;
			else if (((HALF_PI + QUARTER_PI) <= delta) && (delta < (PI + QUARTER_PI)))
				angle = PI;
			else if (((PI + QUARTER_PI) <= delta) && (delta < (TWO_PI - QUARTER_PI)))
				angle = PI + HALF_PI;

			angle += o.angle();
			Rot other = new Rot(angle);
			other.normalize();
			setOrientation(other);
		}
	}

	/**
	 * Translates the Frame so that its {@link #position()} lies on the line defined by {@code origin} and
	 * {@code direction} (defined in the world coordinate system).
	 * <p>
	 * Simply uses an orthogonal projection. {@code direction} does not need to be normalized.
	 */
	public final void projectOnLine(Vec origin, Vec direction) {
		Vec shift = Vec.subtract(origin, position());
		Vec proj = shift;
		// float directionSquaredNorm = (direction.x * direction.x) + (direction.y *
		// direction.y) + (direction.z * direction.z);
		// float modulation = proj.dot(direction) / directionSquaredNorm;
		// proj = Vector3D.mult(direction, modulation);
		proj = Vec.projectVectorOnAxis(proj, direction);
		translate(Vec.subtract(shift, proj));
	}

	/**
	 * Returns the Frame coordinates of a point {@code src} defined in the world coordinate system (converts from world to
	 * Frame).
	 * <p>
	 * {@link #inverseCoordinatesOf(Vec)} performs the inverse conversion. {@link #transformOf(Vec)} converts 3D vectors
	 * instead of 3D coordinates.
	 */
	public final Vec coordinatesOf(Vec src) {
		return coordinatesOf(src, true);
	}

	// TODO get rid of boolean sclng methods? I'd love to
	protected final Vec coordinatesOf(Vec src, boolean sclng) {
		if (referenceFrame() != null)
			return localCoordinatesOf(referenceFrame().coordinatesOf(src), sclng);
		else
			return localCoordinatesOf(src, sclng);
	}

	/**
	 * Returns the world coordinates of the point whose position in the Frame coordinate system is {@code src} (converts
	 * from Frame to world).
	 * <p>
	 * {@link #coordinatesOf(Vec)} performs the inverse conversion. Use {@link #inverseTransformOf(Vec)} to transform 3D
	 * vectors instead of 3D coordinates.
	 */
	public final Vec inverseCoordinatesOf(Vec src) {
		return inverseCoordinatesOf(src, true);
	}

	// TODO get rid of boolean sclng methods? I'd love to
	protected final Vec inverseCoordinatesOf(Vec src, boolean sclng) {
		Frame fr = this;
		Vec res = src;
		while (fr != null) {
			res = fr.localInverseCoordinatesOf(res, sclng);
			fr = fr.referenceFrame();
		}
		return res;
	}

	/**
	 * Returns the Frame coordinates of a point {@code src} defined in the {@link #referenceFrame()} coordinate system
	 * (converts from {@link #referenceFrame()} to Frame).
	 * <p>
	 * {@link #localInverseCoordinatesOf(Vec)} performs the inverse conversion.
	 * 
	 * @see #localTransformOf(Vec)
	 */
	public final Vec localCoordinatesOf(Vec src) {
		return localCoordinatesOf(src, true);
	}

	protected final Vec localCoordinatesOf(Vec src, boolean sclng) {
		if (sclng)
			return Vec.divide(rotation().inverseRotate(Vec.subtract(src, translation())), scaling());
		else
			return rotation().inverseRotate(Vec.subtract(src, translation()));
	}

	/**
	 * Returns the {@link #referenceFrame()} coordinates of a point {@code src} defined in the Frame coordinate system
	 * (converts from Frame to {@link #referenceFrame()}).
	 * <p>
	 * {@link #localCoordinatesOf(Vec)} performs the inverse conversion.
	 * 
	 * @see #localInverseTransformOf(Vec)
	 */
	public final Vec localInverseCoordinatesOf(Vec src) {
		return localInverseCoordinatesOf(src, true);
	}

	protected final Vec localInverseCoordinatesOf(Vec src, boolean sclng) {
		if (sclng)
			return Vec.add(rotation().rotate(Vec.multiply(src, scaling())), translation());
		else
			return Vec.add(rotation().rotate(src), translation());
	}

	/**
	 * Returns the Frame coordinates of the point whose position in the {@code from} coordinate system is {@code src}
	 * (converts from {@code from} to Frame).
	 * <p>
	 * {@link #coordinatesOfIn(Vec, Frame)} performs the inverse transformation.
	 */
	public final Vec coordinatesOfFrom(Vec src, Frame from) {
		if (this == from)
			return src;
		else if (referenceFrame() != null)
			return localCoordinatesOf(referenceFrame().coordinatesOfFrom(src, from));
		else
			return localCoordinatesOf(from.inverseCoordinatesOf(src));
	}

	/**
	 * Returns the {@code in} coordinates of the point whose position in the Frame coordinate system is {@code src}
	 * (converts from Frame to {@code in}).
	 * <p>
	 * {@link #coordinatesOfFrom(Vec, Frame)} performs the inverse transformation.
	 */
	public final Vec coordinatesOfIn(Vec src, Frame in) {
		Frame fr = this;
		Vec res = src;
		while ((fr != null) && (fr != in)) {
			res = fr.localInverseCoordinatesOf(res);
			fr = fr.referenceFrame();
		}

		if (fr != in)
			// in was not found in the branch of this, res is now expressed in
			// the world
			// coordinate system. Simply convert to in coordinate system.
			res = in.coordinatesOf(res);

		return res;
	}

	/**
	 * Returns the Frame transform of a vector {@code src} defined in the world coordinate system (converts vectors from
	 * world to Frame).
	 * <p>
	 * {@link #inverseTransformOf(Vec)} performs the inverse transformation. {@link #coordinatesOf(Vec)} converts 3D
	 * coordinates instead of 3D vectors (here only the rotational part of the transformation is taken into account).
	 */
	public final Vec transformOf(Vec src) {
		return transformOf(src, true);
	}

	protected final Vec transformOf(Vec src, boolean sclng) {
		if (referenceFrame() != null)
			return localTransformOf(referenceFrame().transformOf(src), sclng);
		else
			return localTransformOf(src, sclng);
	}

	/**
	 * Returns the world transform of the vector whose coordinates in the Frame coordinate system is {@code src} (converts
	 * vectors from Frame to world).
	 * <p>
	 * {@link #transformOf(Vec)} performs the inverse transformation. Use {@link #inverseCoordinatesOf(Vec)} to transform
	 * 3D coordinates instead of 3D vectors.
	 */
	public final Vec inverseTransformOf(Vec src) {
		return inverseTransformOf(src, true);
	}

	protected final Vec inverseTransformOf(Vec src, boolean sclng) {
		Frame fr = this;
		Vec res = src;
		while (fr != null) {
			res = fr.localInverseTransformOf(res, sclng);
			fr = fr.referenceFrame();
		}
		return res;
	}

	/**
	 * Rotates the frame so that its {@link #xAxis()} becomes {@code axis} defined in the world coordinate system.
	 * <p>
	 * <b>Attention:</b> this rotation is not uniquely defined. See {@link remixlab.dandelion.geom.Quat#fromTo(Vec, Vec)}.
	 * 
	 * @see #xAxis()
	 * @see #setYAxis(Vec)
	 * @see #setZAxis(Vec)
	 */
	public void setXAxis(Vec axis) {
		if (is3D())
			rotate(new Quat(new Vec(1.0f, 0.0f, 0.0f), transformOf(axis)));
		else
			rotate(new Rot(new Vec(1.0f, 0.0f, 0.0f), transformOf(axis)));
	}

	/**
	 * Rotates the frame so that its {@link #yAxis()} becomes {@code axis} defined in the world coordinate system.
	 * <p>
	 * <b>Attention:</b> this rotation is not uniquely defined. See {@link remixlab.dandelion.geom.Quat#fromTo(Vec, Vec)}.
	 * 
	 * @see #yAxis()
	 * @see #setYAxis(Vec)
	 * @see #setZAxis(Vec)
	 */
	public void setYAxis(Vec axis) {
		if (is3D())
			rotate(new Quat(new Vec(0.0f, 1.0f, 0.0f), transformOf(axis)));
		else
			rotate(new Rot(new Vec(0.0f, 1.0f, 0.0f), transformOf(axis)));
	}

	/**
	 * Rotates the frame so that its {@link #zAxis()} becomes {@code axis} defined in the world coordinate system.
	 * <p>
	 * <b>Attention:</b> this rotation is not uniquely defined. See {@link remixlab.dandelion.geom.Quat#fromTo(Vec, Vec)}.
	 * 
	 * @see #zAxis()
	 * @see #setYAxis(Vec)
	 * @see #setZAxis(Vec)
	 */
	public void setZAxis(Vec axis) {
		if (is3D())
			rotate(new Quat(new Vec(0.0f, 0.0f, 1.0f), transformOf(axis)));
		else
			System.out.println("There's no point in setting the Z axis in 2D");
	}

	public Vec xAxis() {
		return xAxis(true);
	}

	/**
	 * Returns the x-axis of the frame, represented as a normalized vector defined in the world coordinate system.
	 * 
	 * @see #setXAxis(Vec)
	 * @see #yAxis()
	 * @see #zAxis()
	 */
	public Vec xAxis(boolean positive) {
		Vec res;
		if (is3D()) {
			res = inverseTransformOf(new Vec(positive ? 1.0f : -1.0f, 0.0f, 0.0f));
			if (Util.diff(magnitude().x(), 1) || Util.diff(magnitude().y(), 1) || Util.diff(magnitude().z(), 1))
				res.normalize();
		}
		else {
			res = inverseTransformOf(new Vec(positive ? 1.0f : -1.0f, 0.0f));
			if (Util.diff(magnitude().x(), 1) || Util.diff(magnitude().y(), 1))
				res.normalize();
		}
		return res;
	}

	public Vec yAxis() {
		return yAxis(true);
	}

	/**
	 * Returns the y-axis of the frame, represented as a normalized vector defined in the world coordinate system.
	 * 
	 * @see #setYAxis(Vec)
	 * @see #xAxis()
	 * @see #zAxis()
	 */
	public Vec yAxis(boolean positive) {
		Vec res;
		if (is3D()) {
			res = inverseTransformOf(new Vec(0.0f, positive ? 1.0f : -1.0f, 0.0f));
			if (Util.diff(magnitude().x(), 1) || Util.diff(magnitude().y(), 1) || Util.diff(magnitude().z(), 1))
				res.normalize();
		}
		else {
			res = inverseTransformOf(new Vec(0.0f, positive ? 1.0f : -1.0f));
			if (Util.diff(magnitude().x(), 1) || Util.diff(magnitude().y(), 1))
				res.normalize();
		}
		return res;
	}

	public Vec zAxis() {
		return zAxis(true);
	}

	/**
	 * Returns the z-axis of the frame, represented as a normalized vector defined in the world coordinate system.
	 * 
	 * @see #setZAxis(Vec)
	 * @see #xAxis()
	 * @see #yAxis()
	 */
	public Vec zAxis(boolean positive) {
		Vec res = new Vec();
		if (is3D()) {
			res = inverseTransformOf(new Vec(0.0f, 0.0f, positive ? 1.0f : -1.0f));
			if (Util.diff(magnitude().x(), 1) || Util.diff(magnitude().y(), 1) || Util.diff(magnitude().z(), 1))
				res.normalize();
		}
		else
			System.out.println("There's no point in setting the Z axis in 2D");
		return res;
	}

	/**
	 * Returns the Frame transform of a vector {@code src} defined in the {@link #referenceFrame()} coordinate system
	 * (converts vectors from {@link #referenceFrame()} to Frame).
	 * <p>
	 * {@link #localInverseTransformOf(Vec)} performs the inverse transformation.
	 * 
	 * @see #localCoordinatesOf(Vec)
	 */
	public final Vec localTransformOf(Vec src) {
		return localTransformOf(src, true);
	}

	protected final Vec localTransformOf(Vec src, boolean sclng) {
		if (sclng)
			return Vec.divide(rotation().inverseRotate(src), scaling());
		else
			return rotation().inverseRotate(src);
	}

	/**
	 * Returns the {@link #referenceFrame()} transform of a vector {@code src} defined in the Frame coordinate system
	 * (converts vectors from Frame to {@link #referenceFrame()}).
	 * <p>
	 * {@link #localTransformOf(Vec)} performs the inverse transformation.
	 * 
	 * @see #localInverseCoordinatesOf(Vec)
	 */
	public final Vec localInverseTransformOf(Vec src) {
		return localInverseTransformOf(src, true);
	}

	protected final Vec localInverseTransformOf(Vec src, boolean sclng) {
		if (sclng)
			return rotation().rotate(Vec.multiply(src, scaling()));
		else
			return rotation().rotate(src);
	}

	/**
	 * Returns the Frame transform of the vector whose coordinates in the {@code from} coordinate system is {@code src}
	 * (converts vectors from {@code from} to Frame).
	 * <p>
	 * {@link #transformOfIn(Vec, Frame)} performs the inverse transformation.
	 */
	public final Vec transformOfFrom(Vec src, Frame from) {
		if (this == from)
			return src;
		else if (referenceFrame() != null)
			return localTransformOf(referenceFrame().transformOfFrom(src, from));
		else
			return localTransformOf(from.inverseTransformOf(src));
	}

	/**
	 * Returns the {@code in} transform of the vector whose coordinates in the Frame coordinate system is {@code src}
	 * (converts vectors from Frame to {@code in}).
	 * <p>
	 * {@link #transformOfFrom(Vec, Frame)} performs the inverse transformation.
	 */
	public final Vec transformOfIn(Vec src, Frame in) {
		Frame fr = this;
		Vec res = src;
		while ((fr != null) && (fr != in)) {
			res = fr.localInverseTransformOf(res);
			fr = fr.referenceFrame();
		}

		if (fr != in)
			// in was not found in the branch of this, res is now expressed in
			// the world
			// coordinate system. Simply convert to in coordinate system.
			res = in.transformOf(res);

		return res;
	}

	/**
	 * Returns the Matrix3D associated with this Frame.
	 * <p>
	 * This method could be used in conjunction with {@code applyMatrix()} to modify the processing modelview matrix from
	 * a Frame hierarchy. For example, with this Frame hierarchy:
	 * <p>
	 * {@code Frame body = new Frame();} <br>
	 * {@code Frame leftArm = new Frame();} <br>
	 * {@code Frame rightArm = new Frame();} <br>
	 * {@code leftArm.setReferenceFrame(body);} <br>
	 * {@code rightArm.setReferenceFrame(body);} <br>
	 * <p>
	 * The associated processing drawing code should look like:
	 * <p>
	 * {@code p.pushMatrix();}//Supposing p is the PApplet instance <br>
	 * {@code p.applyMatrix(body.matrix());} <br>
	 * {@code drawBody();} <br>
	 * {@code p.pushMatrix();} <br>
	 * {@code p.applyMatrix(leftArm.matrix());} <br>
	 * {@code drawArm();} <br>
	 * {@code p.popMatrix();} <br>
	 * {@code p.pushMatrix();} <br>
	 * {@code p.applyMatrix(rightArm.matrix());} <br>
	 * {@code drawArm();} <br>
	 * {@code p.popMatrix();} <br>
	 * {@code p.popMatrix();} <br>
	 * <p>
	 * Note the use of nested {@code pushMatrix()} and {@code popMatrix()} blocks to represent the frame hierarchy:
	 * {@code leftArm} and {@code rightArm} are both correctly drawn with respect to the {@code body} coordinate system.
	 * <p>
	 * <b>Attention:</b> This technique is inefficient because {@code p.applyMatrix} will try to calculate the inverse of
	 * the transform. Avoid it whenever possible and instead use {@link #applyTransformation(AbstractScene)} which is very
	 * efficient.
	 * <p>
	 * This matrix only represents the local Frame transformation (i.e., with respect to the {@link #referenceFrame()}).
	 * Use {@link #worldMatrix()} to get the full Frame transformation matrix (i.e., from the world to the Frame
	 * coordinate system). These two match when the {@link #referenceFrame()} is {@code null}.
	 * <p>
	 * The result is only valid until the next call to {@code matrix()} or {@link #worldMatrix()}. Use it immediately (as
	 * above).
	 * <p>
	 * <b>Note:</b> The scaling factor of the 4x4 matrix is 1.0.
	 * 
	 * @see #applyTransformation(AbstractScene)
	 */
	public final Mat matrix() {
		Mat pM = new Mat();

		pM = kernel().rotation().matrix();

		pM.mat[12] = kernel().translation().vec[0];
		pM.mat[13] = kernel().translation().vec[1];
		pM.mat[14] = kernel().translation().vec[2];

		Vec s = scaling();
		if (s.x() != 1) {
			pM.setM00(pM.m00() * s.x());
			pM.setM10(pM.m10() * s.x());
			pM.setM20(pM.m20() * s.x());
		}
		if (s.y() != 1) {
			pM.setM01(pM.m01() * s.y());
			pM.setM11(pM.m11() * s.y());
			pM.setM21(pM.m21() * s.y());
		}
		if (s.z() != 1) {
			pM.setM02(pM.m02() * s.z());
			pM.setM12(pM.m12() * s.z());
			pM.setM22(pM.m22() * s.z());
		}

		return pM;
	}

	/**
	 * Convenience function that simply calls {@code scn.applyTransformation(this)}.
	 * 
	 * @see #matrix()
	 * @see remixlab.dandelion.core.AbstractScene#applyTransformation(Frame)
	 */
	public void applyTransformation(AbstractScene scn) {
		scn.applyTransformation(this);
	}

	/**
	 * Convenience function that simply calls {@code scn.applyWorldTransformation(this)}.
	 * 
	 * @see #worldMatrix()
	 * @see remixlab.dandelion.core.AbstractScene#applyWorldTransformation(Frame)
	 */
	public void applyWorldTransformation(AbstractScene scn) {
		scn.applyWorldTransformation(this);
	}

	/**
	 * Returns the transformation matrix represented by the Frame.
	 * <p>
	 * This method should be used in conjunction with {@code applyMatrix()} to modify the processing modelview matrix from
	 * a Frame:
	 * <p>
	 * {@code // Here the modelview matrix corresponds to the world coordinate system.} <br>
	 * {@code Frame fr = new Frame(pos, Orientable(from, to));} <br>
	 * {@code pushMatrix();} <br>
	 * {@code applyMatrix(worldMatrix());} <br>
	 * {@code // draw object in the fr coordinate system.} <br>
	 * {@code popMatrix();} <br>
	 * <p>
	 * This matrix represents the global Frame transformation: the entire {@link #referenceFrame()} hierarchy is taken
	 * into account to define the Frame transformation from the world coordinate system. Use {@link #matrix()} to get the
	 * local Frame transformation matrix (i.e. defined with respect to the {@link #referenceFrame()}). These two match
	 * when the {@link #referenceFrame()} is {@code null}.
	 * <p>
	 * <b>Attention:</b> The result is only valid until the next call to {@link #matrix()} or {@code worldMatrix()}. Use
	 * it immediately (as above).
	 * <p>
	 * <b>Note:</b> The scaling factor of the 4x4 matrix is 1.0.
	 */
	public final Mat worldMatrix() {
		if (referenceFrame() != null) {
			final Frame fr = new Frame();
			fr.setTranslation(position());
			fr.setRotation(orientation());
			fr.setScaling(scaling());
			return fr.matrix();
		} else
			return matrix();
	}

	public final void fromMatrix(Mat pM) {
		fromMatrix(pM, new Vec(1, 1, 1));
	}

	/**
	 * Sets the Frame from a Matrix3D representation (rotation in the upper left 3x3 matrix and translation on the last
	 * column).
	 * <p>
	 * Hence, if a code fragment looks like:
	 * <p>
	 * {@code float [] m = new float [16]; m[0]=...;} <br>
	 * {@code gl.glMultMatrixf(m);} <br>
	 * <p>
	 * It is equivalent to write:
	 * <p>
	 * {@code Frame fr = new Frame();} <br>
	 * {@code fr.fromMatrix(m);} <br>
	 * {@code applyMatrix(fr.matrix());} <br>
	 * <p>
	 * Using this conversion, you can benefit from the powerful Frame transformation methods to translate points and
	 * vectors to and from the Frame coordinate system to any other Frame coordinate system (including the world
	 * coordinate system). See {@link #coordinatesOf(Vec)} and {@link #transformOf(Vec)}.
	 * <p>
	 * <b>Attention:</b> A Frame does not contain a scale factor. The possible scaling in {@code m} will not be converted
	 * into the Frame by this method.
	 */
	public final void fromMatrix(Mat pM, Vec scl) {
		if (Util.zero(pM.mat[15])) {
			System.out.println("Doing nothing: pM.mat[15] should be non-zero!");
			return;
		}

		kernel().translation().vec[0] = pM.mat[12] / pM.mat[15];
		kernel().translation().vec[1] = pM.mat[13] / pM.mat[15];
		kernel().translation().vec[2] = pM.mat[14] / pM.mat[15];

		float[][] r = new float[3][3];

		r[0][0] = pM.mat[0] / pM.mat[15];
		r[0][1] = pM.mat[4] / pM.mat[15];
		r[0][2] = pM.mat[8] / pM.mat[15];
		r[1][0] = pM.mat[1] / pM.mat[15];
		r[1][1] = pM.mat[5] / pM.mat[15];
		r[1][2] = pM.mat[9] / pM.mat[15];
		r[2][0] = pM.mat[2] / pM.mat[15];
		r[2][1] = pM.mat[6] / pM.mat[15];
		r[2][2] = pM.mat[10] / pM.mat[15];

		setScaling(scl.x(), scl.y(), scl.z());
		Vec s = scaling();

		if (s.x() != 1 || s.y() != 1 || s.z() != 1) {
			r[0][0] = r[0][0] / s.x();
			r[1][0] = r[1][0] / s.x();
			r[2][0] = r[2][0] / s.x();

			r[0][1] = r[0][1] / s.y();
			r[1][1] = r[1][1] / s.y();
			r[2][1] = r[2][1] / s.y();

			if (this.is3D()) {
				r[0][2] = r[0][2] / s.z();
				r[1][2] = r[1][2] / s.z();
				r[2][2] = r[2][2] / s.z();
			}
		}

		Vec x = new Vec(r[0][0], r[1][0], r[2][0]);
		Vec y = new Vec(r[0][1], r[1][1], r[2][1]);
		Vec z = new Vec(r[0][2], r[1][2], r[2][2]);

		kernel().fromRotatedBasis(x, y, z);
	}

	/**
	 * Returns a Frame representing the inverse of the Frame space transformation.
	 * <p>
	 * The {@link #rotation()} the new Frame is the {@link remixlab.dandelion.geom.Quat#inverse()} of the original
	 * rotation. Its {@link #translation()} is the negated inverse rotated image of the original translation.
	 * <p>
	 * If a Frame is considered as a space rigid transformation (translation and rotation), the inverse() Frame performs
	 * the inverse transformation.
	 * <p>
	 * Only the local Frame transformation (i.e., defined with respect to the {@link #referenceFrame()}) is inverted. Use
	 * {@link #worldInverse()} for a global inverse.
	 * <p>
	 * The resulting Frame has the same {@link #referenceFrame()} as the Frame and a {@code null} {@link #constraint()}.
	 * <p>
	 * <b>Note:</b> The scaling factor of the 4x4 matrix is 1.0.
	 */
	public final Frame inverse() {
		Frame fr = new Frame(kernel().rotation().inverse(), Vec.multiply(
						kernel().rotation().inverseRotate(kernel().translation()), -1), kernel().inverseScaling());
		fr.setReferenceFrame(referenceFrame());
		return fr;
	}

	/**
	 * 
	 * Returns the {@link #inverse()} of the Frame world transformation.
	 * <p>
	 * The {@link #orientation()} of the new Frame is the {@link remixlab.dandelion.geom.Quat#inverse()} of the original
	 * orientation. Its {@link #position()} is the negated and inverse rotated image of the original position.
	 * <p>
	 * The result Frame has a {@code null} {@link #referenceFrame()} and a {@code null} {@link #constraint()}.
	 * <p>
	 * Use {@link #inverse()} for a local (i.e., with respect to {@link #referenceFrame()}) transformation inverse.
	 */
	public final Frame worldInverse() {
		return (new Frame(orientation().inverse(), Vec.multiply(orientation().inverseRotate(position()), -1),
						inverseMagnitude()));
	}

	// TODO experimental

	/**
	 * public final Vector3D coordinatesOfNoScl(Vector3D src) { if (referenceFrame() != null) return
	 * localCoordinatesOfNoScl(referenceFrame().coordinatesOfNoScl(src)); else return localCoordinatesOfNoScl(src); }
	 * 
	 * public final Vector3D localCoordinatesOfNoScl(Vector3D src) { return rotation().inverseRotate(Vector3D.sub(src,
	 * translation())); }
	 * 
	 * public final Vector3D inverseCoordinatesOfNoScl(Vector3D src) { GeomFrame fr = this; Vector3D res = src; while (fr
	 * != null) { res = fr.localInverseCoordinatesOfNoScl(res); fr = fr.referenceFrame(); } return res; }
	 * 
	 * public final Vector3D localInverseCoordinatesOfNoScl(Vector3D src) { return Vector3D.add(rotation().rotate(src),
	 * translation()); }
	 * 
	 * public final Vector3D transformOfNoScl(Vector3D src) { if (referenceFrame() != null) return
	 * localTransformOfNoScl(referenceFrame().transformOfNoScl(src)); else return localTransformOfNoScl(src); }
	 * 
	 * public final Vector3D localTransformOfNoScl(Vector3D src) { return rotation().inverseRotate(src); }
	 * 
	 * public final Vector3D inverseTransformOfNoScl(Vector3D src) { GeomFrame fr = this; Vector3D res = src; while (fr !=
	 * null) { res = fr.localInverseTransformOfNoScl(res); fr = fr.referenceFrame(); } return res; }
	 * 
	 * public final Vector3D localInverseTransformOfNoScl(Vector3D src) { return rotation().rotate(src); } //
	 */
}
