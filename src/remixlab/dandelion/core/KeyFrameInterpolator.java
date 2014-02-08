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

import java.util.*;

import remixlab.dandelion.geom.*;
import remixlab.fpstiming.AbstractTimerJob;
import remixlab.fpstiming.TimingHandler;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;
import remixlab.util.Util;

/**
 * A keyFrame Catmull-Rom Frame interpolator.
 * <p>
 * A KeyFrameInterpolator holds keyFrames (that define a path) and, optionally,
 * a reference to a Frame of your application (which will be interpolated). In
 * this case, when the user {@link #startInterpolation()}, the
 * KeyFrameInterpolator regularly updates the {@link #frame()} position and
 * orientation along the path.
 * <p>
 * Here is a typical utilization example (see also the examples FrameInterpolation
 * and CameraInterpolation):
 * <p>
 * {@code //PApplet.setup() should look like:}<br>
 * {@code size(640, 360, P3D);}<br>
 * {@code // The KeyFrameInterpolator kfi is given the Frame that it will drive
 * over time.}<br>
 * {@code myFrame = new Frame());}
 * {@code kfi = new KeyFrameInterpolator( myFrame, this );} 
 * //or an anonymous Frame may also be given: {@code kfi = new KeyFrameInterpolator( this );}<br>
 * {@code //By default the Frame is provided as a reference to the
 * KeyFrameInterpolator}} (see {@link #addKeyFrame(Frame)} methods):<br>
 * {@code kfi.addKeyFrame( new Frame( new Vector3D(1,0,0), new Quaternion() ) );}<br>
 * {@code kfi.addKeyFrame( new Frame( new Vector3D(2,1,0), new Quaternion() ) );}<br>
 * {@code // ...and so on for all the keyFrames.}<br>
 * {@code kfi.startInterpolation();}<br>
 * <p>
 * {@code //PApplet.draw() should look like:}<br>
 * {@code background(0);}<br>
 * {@code scene.beginDraw();}<br>
 * {@code pushMatrix();}<br>
 * {@code kfi.frame().applyTransformation(this);}<br>
 * {@code
 * // Draw your object here. Its position and orientation are interpolated.}<br>
 * {@code popMatrix();}<br>
 * {@code scene.endDraw();}<br>
 * <p>
 * The keyFrames are defined by a Frame and a time, expressed in seconds.
 * Optionally, the Frame can be provided as a reference (see the
 * {@link #addKeyFrame(Frame)} methods). In this case, the path will
 * automatically be updated when the Frame is modified.
 * <p>
 * The time has to be monotonously increasing over keyFrames. When
 * {@link #interpolationSpeed()} equals 1.0 (default value), these times
 * correspond to actual user's seconds during interpolation (provided that your
 * main loop is fast enough). The interpolation is then real-time: the keyFrames
 * will be reached at their {@link #keyFrameTime(int)}.
 * <p>
 * <h3>Interpolation details</h3>
 * <p>
 * When the user {@link #startInterpolation()}, a timer is started which will
 * update the {@link #frame()}'s position and orientation every
 * {@link #interpolationPeriod()} milliseconds. This update increases the
 * {@link #interpolationTime()} by {@link #interpolationPeriod()} *
 * {@link #interpolationSpeed()} milliseconds.
 * <p>
 * Note that this mechanism ensures that the number of interpolation steps is
 * constant and equal to the total path {@link #duration()} divided by the
 * {@link #interpolationPeriod()} * {@link #interpolationSpeed()}. This is
 * especially useful for benchmarking or movie creation (constant number of
 * snapshots).
 * <p>
 * The interpolation is stopped when {@link #interpolationTime()} is greater
 * than the {@link #lastTime()} (unless loopInterpolation() is {@code true}).
 * <p>
 * Note that a Camera has
 * {@link remixlab.dandelion.core.Camera#keyFrameInterpolator(int)}, that can be used
 * to drive the Camera along a path.
 * <p>
 * <b>Attention:</b> If a Constraint is attached to the {@link #frame()} (see
 * {@link remixlab.dandelion.core.Frame#constraint()}), it should be deactivated
 * before {@link #interpolationIsStarted()}, otherwise the interpolated motion
 * (computed as if there was no constraint) will probably be erroneous.
 */
public class KeyFrameInterpolator implements Copyable {
	@Override
	public int hashCode() {
    return new HashCodeBuilder(17, 37).
		append(currentFrmValid).
		append(mainFrame).
		append(interpolationSpd).
		append(interpolationStrt).
		append(interpolationTm).
		append(keyFrameList).
		append(lpInterpolation).
		append(path).
		append(pathIsValid).
		append(period).
		append(valuesAreValid).
    toHashCode();
	}

	@Override
	public boolean equals(Object obj) {		
		if (obj == null) return false;
		if (obj == this) return true;		
		if (obj.getClass() != getClass()) return false;
		
		KeyFrameInterpolator other = (KeyFrameInterpolator) obj;
	   return new EqualsBuilder()		
		.append(currentFrmValid, other.currentFrmValid)
		.append(mainFrame, other.mainFrame)
		.append(interpolationSpd, other.interpolationSpd)
		.append(interpolationStrt, other.interpolationStrt)
		.append(interpolationTm, other.interpolationTm)
		.append(keyFrameList, other.keyFrameList)
		.append(lpInterpolation, other.lpInterpolation)
		.append(path, other.path )
		.append(pathIsValid, other.pathIsValid)
		.append(period, other.period)
		.append(valuesAreValid, other.valuesAreValid)
		.isEquals();
	}

	protected abstract class AbstractKeyFrame implements Copyable {
		@Override
		public int hashCode() {
	    return new HashCodeBuilder(17, 37).
			append(frm).
	    toHashCode();
		}
		
		@Override
		public boolean equals(Object obj) {		
			if (obj == null) return false;
			if (obj == this) return true;		
			if (obj.getClass() != getClass()) return false;
			
			AbstractKeyFrame other = (AbstractKeyFrame) obj;
		   return new EqualsBuilder()		
			.append(frm, other.frm)
			.isEquals();
		}
		
		protected Vec tgPVec;	
		//Option 2 (interpolate scaling using a spline)
		protected Vec tgSVec;
		protected float tm;
		protected Frame frm;

		AbstractKeyFrame(Frame fr, float t) {
			tm = t;
			frm = fr;
		}
		
		protected AbstractKeyFrame(AbstractKeyFrame otherKF) {
			this.tm = otherKF.tm;
			this.frm = otherKF.frm.get();
		}		

		Vec position() {
			return frame().position();
		}

		Orientable orientation() {
			return frame().orientation();
		}
		
		Vec magnitude() {
			return frame().magnitude();
		}

		float time() {
			return tm;
		}

		Frame frame() {
			return frm;
		}
		
		Vec tgP() {
			return tgPVec;
		}
		
		// /**
	  //Option 2 (interpolate scaling using a spline)
		Vec tgS() {
			return tgSVec;
		}
		// */
		
		abstract void computeTangent(AbstractKeyFrame prev, AbstractKeyFrame next);
	}
	
	protected class KeyFrame3D extends AbstractKeyFrame {
		protected Quat tgQuat;
		
		KeyFrame3D(Frame fr, float t) {
			super(fr, t);
		}
		
		protected KeyFrame3D(KeyFrame3D other) {
			super(other);
		}
		
		@Override
		public KeyFrame3D get() {
			return new KeyFrame3D(this);
		}	

		Quat tgQ() {
			return tgQuat;
		}
		
		@Override
		void computeTangent(AbstractKeyFrame prev, AbstractKeyFrame next) {
			tgPVec = Vec.multiply(Vec.subtract(next.position(), prev.position()), 0.5f);
			tgQuat = Quat.squadTangent((Quat)prev.orientation(), (Quat)orientation(), (Quat)next.orientation());
			////Option 2 (interpolate scaling using a spline)
			tgSVec = Vec.multiply(Vec.subtract(next.magnitude(), prev.magnitude()), 0.5f);			
		}
	}
	
	protected class KeyFrame2D extends AbstractKeyFrame {		
		KeyFrame2D(Frame fr, float t) {
			super(fr, t);			
		}
		
		protected KeyFrame2D(KeyFrame2D other) {
			super(other);
		}
		
		@Override
		public KeyFrame2D get() {
			return new KeyFrame2D(this);
		}		
		
		@Override
		void computeTangent(AbstractKeyFrame prev, AbstractKeyFrame next) {
			tgPVec = Vec.multiply(Vec.subtract(next.position(), prev.position()), 0.5f);			
		  //Option 2 (interpolate scaling using a spline)
			tgSVec = Vec.multiply(Vec.subtract(next.magnitude(), prev.magnitude()), 0.5f);			
		}
	}

	private long lUpdate;
	protected List<AbstractKeyFrame> keyFrameList;
	private ListIterator<AbstractKeyFrame> currentFrame0;
	private ListIterator<AbstractKeyFrame> currentFrame1;
	private ListIterator<AbstractKeyFrame> currentFrame2;
	private ListIterator<AbstractKeyFrame> currentFrame3;
	protected List<Frame> path;
	// A s s o c i a t e d f r a m e
	private Frame mainFrame;

	// R h y t h m
	private AbstractTimerJob interpolationTimerJob;
	private int period;
	private float interpolationTm;
	private float interpolationSpd;
	private boolean interpolationStrt;

	// M i s c
	private boolean lpInterpolation;

	// C a c h e d v a l u e s a n d f l a g s
	private boolean pathIsValid;
	private boolean valuesAreValid;
	private boolean currentFrmValid;
	private boolean splineCacheIsValid;
	private Vec pv1, pv2;
  //Option 2 (interpolate scaling using a spline)
	private Vec sv1, sv2;

  //S C E N E
  public AbstractScene scene;
  
  /**
   * Convenience constructor that simply calls {@code this(scn, new Frame())}.
   * <p>
   * Creates an anonymous {@link #frame()} to be interpolated by this
   * KeyFrameInterpolator.
   * 
   * @see #KeyFrameInterpolator(AbstractScene, Frame)
   */
  public KeyFrameInterpolator(AbstractScene scn) {
  	this(scn, new Frame());
  }

	/**
	 * Creates a KeyFrameInterpolator, with {@code frame} as associated
	 * {@link #frame()}.
	 * <p>
	 * The {@link #frame()} can be set or changed using {@link #setFrame(Frame)}.
	 * <p>
	 * {@link #interpolationTime()}, {@link #interpolationSpeed()} and
	 * {@link #interpolationPeriod()} are set to their default values.
	 */
	public KeyFrameInterpolator(AbstractScene scn, Frame frame) {
		scene = scn;
		keyFrameList = new ArrayList<AbstractKeyFrame>();
		path = new ArrayList<Frame>();
		mainFrame = null;
		period = 40;
		interpolationTm = 0.0f;
		interpolationSpd = 1.0f;
		interpolationStrt = false;
		lpInterpolation = false;
		pathIsValid = false;
		valuesAreValid = true;
		currentFrmValid = false;
		setFrame(frame);

		currentFrame0 = keyFrameList.listIterator();
		currentFrame1 = keyFrameList.listIterator();
		currentFrame2 = keyFrameList.listIterator();
		currentFrame3 = keyFrameList.listIterator();
		
		interpolationTimerJob = new AbstractTimerJob() {
			public void execute() {
				update();
			}
		};		
		scene.registerJob(interpolationTimerJob);
	}
	
	protected KeyFrameInterpolator(KeyFrameInterpolator otherKFI) {
		this.scene = otherKFI.scene;		
		this.path = new ArrayList<Frame>();
		ListIterator<Frame> frameIt = otherKFI.path.listIterator();
		while (frameIt.hasNext()) {
			this.path.add(frameIt.next().get());
		}
		
		this.setFrame(otherKFI.frame());
		
		this.period = otherKFI.period;
		this.interpolationTm = otherKFI.interpolationTm;
		this.interpolationSpd = otherKFI.interpolationSpd;
		this.interpolationStrt = otherKFI.interpolationStrt;
		this.lpInterpolation = otherKFI.lpInterpolation;
		this.pathIsValid = otherKFI.pathIsValid;
		this.valuesAreValid = otherKFI.valuesAreValid;
		this.currentFrmValid = otherKFI.currentFrmValid;		
		
		this.keyFrameList = new ArrayList<AbstractKeyFrame>();		
		
		for (AbstractKeyFrame element : otherKFI.keyFrameList) {
			AbstractKeyFrame kf = (AbstractKeyFrame)element.get();
			this.keyFrameList.add(kf);
			if (kf.frame() instanceof InteractiveFrame)
				this.scene.terseHandler().removeFromAllAgentPools((InteractiveFrame)kf.frame());
		}
		
		this.currentFrame0 = keyFrameList.listIterator(otherKFI.currentFrame0.nextIndex());
		this.currentFrame1 = keyFrameList.listIterator(otherKFI.currentFrame1.nextIndex());
		this.currentFrame2 = keyFrameList.listIterator(otherKFI.currentFrame2.nextIndex());
		this.currentFrame3 = keyFrameList.listIterator(otherKFI.currentFrame3.nextIndex());
		
		this.interpolationTimerJob = new AbstractTimerJob() {
			public void execute() {
				update();
			}
		};		
		scene.registerJob(interpolationTimerJob);		
		
		this.invalidateValues();
	}
	
	public KeyFrameInterpolator get() {
		return new KeyFrameInterpolator(this);
	}
	
	public void checked() {
		lUpdate = TimingHandler.frameCount;
	}
	
	public long lastUpdate() {
		return lUpdate;
	}

	/**
	 * Sets the {@link #frame()} associated to the KeyFrameInterpolator.
	 */
	public void setFrame(Frame f) {
		mainFrame = f;
	}

	/**
	 * Returns the associated Frame that is interpolated by the
	 * KeyFrameInterpolator.
	 * <p>
	 * When {@link #interpolationIsStarted()}, this Frame's position and
	 * orientation will regularly be updated by a timer, so that they follow the
	 * KeyFrameInterpolator path.
	 * <p>
	 * Set using {@link #setFrame(Frame)} or with the KeyFrameInterpolator
	 * constructor.
	 */
	public Frame frame() {
		return mainFrame;
	}

	/**
	 * Returns the number of keyFrames used by the interpolation. Use
	 * {@link #addKeyFrame(Frame)} to add new keyFrames.
	 */
	public int numberOfKeyFrames() {
		return keyFrameList.size();
	}

	/**
	 * Returns the current interpolation time (in seconds) along the
	 * KeyFrameInterpolator path.
	 * <p>
	 * This time is regularly updated when {@link #interpolationIsStarted()}. Can
	 * be set directly with {@link #setInterpolationTime(float)} or
	 * {@link #interpolateAtTime(float)}.
	 */
	public float interpolationTime() {
		return interpolationTm;
	}

	/**
	 * Returns the current interpolation speed.
	 * <p>
	 * Default value is 1.0f, which means {@link #keyFrameTime(int)} will be
	 * matched during the interpolation (provided that your main loop is fast
	 * enough).
	 * <p>
	 * A negative value will result in a reverse interpolation of the keyFrames.
	 * 
	 * @see #interpolationPeriod()
	 */
	public float interpolationSpeed() {
		return interpolationSpd;
	}

	/**
	 * Returns the current interpolation period, expressed in milliseconds. The
	 * update of the {@link #frame()} state will be done by a timer at this period
	 * when {@link #interpolationIsStarted()}.
	 * <p>
	 * This period (multiplied by {@link #interpolationSpeed()}) is added to the
	 * {@link #interpolationTime()} at each update, and the {@link #frame()} state
	 * is modified accordingly (see {@link #interpolateAtTime(float)}). Default
	 * value is 40 milliseconds.
	 * 
	 * @see #setInterpolationPeriod(int)
	 */
	public int interpolationPeriod() {
		return period;
	}

	/**
	 * Returns {@code true} when the interpolation is played in an infinite loop.
	 * <p>
	 * When {@code false} (default), the interpolation stops when
	 * {@link #interpolationTime()} reaches {@link #firstTime()} (with negative
	 * {@link #interpolationSpeed()}) or {@link #lastTime()}.
	 * <p>
	 * {@link #interpolationTime()} is otherwise reset to {@link #firstTime()} (+
	 * {@link #interpolationTime()} - {@link #lastTime()}) (and inversely for
	 * negative {@link #interpolationSpeed()}) and interpolation continues.
	 */
	public boolean loopInterpolation() {
		return lpInterpolation;
	}

	/**
	 * Sets the {@link #interpolationTime()}.
	 * <p>
	 * <b>Attention:</b> The {@link #frame()} state is not affected by this
	 * method. Use this function to define the starting time of a future
	 * interpolation (see {@link #startInterpolation()}). Use
	 * {@link #interpolateAtTime(float)} to actually interpolate at a given time.
	 */
	public void setInterpolationTime(float time) {
		interpolationTm = time;
	};

	/**
	 * Sets the {@link #interpolationSpeed()}. Negative or null values are
	 * allowed.
	 */
	public void setInterpolationSpeed(float speed) {
		interpolationSpd = speed;
	}

	/**
	 * Sets the {@link #interpolationPeriod()}. Should positive.
	 */
	public void setInterpolationPeriod(int myPeriod) {
		if(myPeriod > 0)
			period = myPeriod;
	}

	/**
	 * Convenience function that simply calls  {@code setLoopInterpolation(true)}. 
	 */
	public void setLoopInterpolation() {
		setLoopInterpolation(true);
	}

	/**
	 * Sets the {@link #loopInterpolation()} value.
	 */
	public void setLoopInterpolation(boolean loop) {
		lpInterpolation = loop;
	}

	/**
	 * Returns {@code true} when the interpolation is being performed. Use
	 * {@link #startInterpolation()}, {@link #stopInterpolation()} or
	 * {@link #toggleInterpolation()} to modify this state.
	 */
	public boolean interpolationIsStarted() {
		return interpolationStrt;
	}

	/**
	 * Calls {@link #startInterpolation()} or {@link #stopInterpolation()},
	 * depending on {@link #interpolationIsStarted()}.
	 */
	public void toggleInterpolation() {
		if (interpolationIsStarted())
			stopInterpolation();
		else
			startInterpolation();
	}

	/**
	 * Updates {@link #frame()} state according to current
	 * {@link #interpolationTime()}. Then adds {@link #interpolationPeriod()}*
	 * {@link #interpolationSpeed()} to {@link #interpolationTime()}.
	 * <p>
	 * This internal method is called by a timer when
	 * {@link #interpolationIsStarted()}. It can be used for debugging purpose.
	 * {@link #stopInterpolation()} is called when {@link #interpolationTime()}
	 * reaches {@link #firstTime()} or {@link #lastTime()}, unless
	 * {@link #loopInterpolation()} is {@code true}.
	 */
	protected void update() {
		interpolateAtTime(interpolationTime());

		interpolationTm += interpolationSpeed() * interpolationPeriod() / 1000.0f;

		if (interpolationTime() > keyFrameList.get(keyFrameList.size() - 1).time()) {
			if (loopInterpolation())
				setInterpolationTime(keyFrameList.get(0).time() + interpolationTm	- keyFrameList.get(keyFrameList.size() - 1).time());
			else {
				// Make sure last KeyFrame is reached and displayed
				interpolateAtTime(keyFrameList.get(keyFrameList.size() - 1).time());
				stopInterpolation();
			}
			// emit endReached();
		} else if (interpolationTime() < keyFrameList.get(0).time()) {
			if (loopInterpolation())
				setInterpolationTime(keyFrameList.get(keyFrameList.size() - 1).time()	- keyFrameList.get(0).time() + interpolationTm);
			else {
				// Make sure first KeyFrame is reached and displayed
				interpolateAtTime(keyFrameList.get(0).time());
				stopInterpolation();
			}
			// emit endReached();
		}
	}

	public void invalidateValues() {
		valuesAreValid = false;
		pathIsValid = false;
		splineCacheIsValid = false;
	}

	/**
	 * Convenience function that simply calls {@code startInterpolation(-1)}.
	 * 
	 * @see #startInterpolation(int)
	 */
	public void startInterpolation() {
		startInterpolation(-1);
	}

	/**
	 * Starts the interpolation process.
	 * <p>
	 * A timer is started with an {@link #interpolationPeriod()} period that
	 * updates the {@link #frame()}'s position and orientation.
	 * {@link #interpolationIsStarted()} will return {@code true} until
	 * {@link #stopInterpolation()} or {@link #toggleInterpolation()} is called.
	 * <p>
	 * If {@code period} is positive, it is set as the new
	 * {@link #interpolationPeriod()}. The previous {@link #interpolationPeriod()}
	 * is used otherwise (default).
	 * <p>
	 * If {@link #interpolationTime()} is larger than {@link #lastTime()},
	 * {@link #interpolationTime()} is reset to {@link #firstTime()} before
	 * interpolation starts (and inversely for negative
	 * {@link #interpolationSpeed()}.
	 * <p>
	 * Use {@link #setInterpolationTime(float)} before calling this method to
	 * change the starting {@link #interpolationTime()}.
	 * <p>
	 * <b>Attention:</b> The keyFrames must be defined (see
	 * {@link #addKeyFrame(Frame, float)}) before you startInterpolation(), or
	 * else the interpolation will naturally immediately stop.
	 */
	public void startInterpolation(int myPeriod) {
		if (myPeriod >= 0)
			setInterpolationPeriod(myPeriod);

		if (!keyFrameList.isEmpty()) {
			if ((interpolationSpeed() > 0.0) && (interpolationTime() >= keyFrameList.get(keyFrameList.size() - 1).time()))
				setInterpolationTime(keyFrameList.get(0).time());
			if ((interpolationSpeed() < 0.0) && (interpolationTime() <= keyFrameList.get(0).time()))
				setInterpolationTime(keyFrameList.get(keyFrameList.size() - 1).time());
			interpolationTimerJob.run(interpolationPeriod());
			interpolationStrt = true;
			update();
		}
	}

	/**
	 * Stops an interpolation started with {@link #startInterpolation()}. See
	 * {@link #interpolationIsStarted()} and {@link #toggleInterpolation()}.
	 */
	public void stopInterpolation() {
		interpolationTimerJob.stop();
		interpolationStrt = false;
	}

	/**
	 * Stops the interpolation and resets {@link #interpolationTime()} to the
	 * {@link #firstTime()}.
	 * <p>
	 * If desired, call {@link #interpolateAtTime(float)} after this method to
	 * actually move the {@link #frame()} to {@link #firstTime()}.
	 */
	public void resetInterpolation() {
		stopInterpolation();
		setInterpolationTime(firstTime());
	}

	/**
	 * Appends a new keyFrame to the path.
	 * <p>
	 * Same as {@link #addKeyFrame(Frame, float)}, except that the
	 * {@link #keyFrameTime(int)} is set to the previous
	 * {@link #keyFrameTime(int)} plus one second (or 0.0 if there is no previous
	 * keyFrame).
	 */
	public void addKeyFrame(Frame frame) {
		float time;

		if (keyFrameList.isEmpty())
			time = 0.0f;
		else
			time = keyFrameList.get(keyFrameList.size() - 1).time() + 1.0f;

		addKeyFrame(frame, time);
	}

	/**
	 * Appends a new keyFrame to the path, with its associated {@code time} (in
	 * seconds).
	 * <p>
	 * When {@code setRef} is {@code false} the keyFrame is added by value, meaning
	 * that the path will use the current {@code frame} state.
	 * <p>
	 * When {@code setRef} is {@code true} the keyFrame is given as a reference to
	 * a Frame, which will be connected to the KeyFrameInterpolator: when {@code
	 * frame} is modified, the KeyFrameInterpolator path is updated accordingly.
	 * This allows for dynamic paths, where keyFrame can be edited, even during
	 * the interpolation. {@code null} frame references are silently ignored. The
	 * {@link #keyFrameTime(int)} has to be monotonously increasing over
	 * keyFrames.
	 */
	public void addKeyFrame(Frame frame, float time) {
		if (frame == null)
			return;

		if (keyFrameList.isEmpty())
			interpolationTm = time;

		///**
		if ((!keyFrameList.isEmpty()) && (keyFrameList.get(keyFrameList.size() - 1).time() > time))
			System.out.println("Error in KeyFrameInterpolator.addKeyFrame: time is not monotone");
		else {
			if(scene.is3D())
				keyFrameList.add(new KeyFrame3D(frame, time));
			else
				keyFrameList.add(new KeyFrame2D(frame, time));
		}
		// */

		valuesAreValid = false;
		pathIsValid = false;
		currentFrmValid = false;
		resetInterpolation();
	}
	
	/**
	 * Remove KeyFrame according to {@code index} in the list and {@link #stopInterpolation()}
	 * if {@link #interpolationIsStarted()}. If {@code index < 0 || index >= keyFr.size()}
	 * the call is silently ignored. 
	 */
	//TODO testing
	public void removeKeyFrame(int index) {
		if (index < 0 || index >= keyFrameList.size())
			return;
		valuesAreValid = false;
		pathIsValid = false;
		currentFrmValid = false;
		if( interpolationIsStarted() )
			stopInterpolation();
		AbstractKeyFrame kf = keyFrameList.remove(index);
		if (kf.frm  instanceof InteractiveFrame)
			scene.terseHandler().removeFromAllAgentPools( (InteractiveFrame) kf.frm );
		  //before:
			//if (((InteractiveFrame) kf.frm).isInDeviceGrabberPool())
				//((InteractiveFrame) kf.frm).removeFromDeviceGrabberPool();
		setInterpolationTime(firstTime());
	}

	/**
	 * Removes all keyFrames from the path. Calls
	 * {@link #removeFramesFromAllAgentPools()}. The
	 * {@link #numberOfKeyFrames()} is set to 0.
	 * 
	 * @see #removeFramesFromAllAgentPools()
	 */
	public void deletePath() {
		stopInterpolation();
		removeFramesFromAllAgentPools();
		keyFrameList.clear();
		pathIsValid = false;
		valuesAreValid = false;
		currentFrmValid = false;
	}

	/**
	 * Removes all the Frames from the mouse grabber pool (if they were provided
	 * as references).
	 * 
	 * @see #addFramesToAllAgentPools()
	 */
	public void removeFramesFromAllAgentPools() {
		for (int i = 0; i < keyFrameList.size(); ++i)
			if(keyFrameList.get(i).frame() instanceof InteractiveFrame)
				scene.terseHandler().removeFromAllAgentPools((InteractiveFrame) keyFrameList.get(i).frame());
	}

	/**
	 * Re-adds all the Frames to the mouse grabber pool (if they were provided as
	 * references).
	 * 
	 * @see #removeFramesFromAllAgentPools()
	 */
	public void addFramesToAllAgentPools() {
		for (int i = 0; i < keyFrameList.size(); ++i)
			if(keyFrameList.get(i).frame() instanceof InteractiveFrame)
				scene.terseHandler().addInAllAgentPools((InteractiveFrame) keyFrameList.get(i).frame());			  
	}

	protected void updateModifiedFrameValues() {
		AbstractKeyFrame kf;
		AbstractKeyFrame prev = keyFrameList.get(0);
		kf = keyFrameList.get(0);

		int index = 1;
		while (kf != null) {
			AbstractKeyFrame next = (index < keyFrameList.size()) ? keyFrameList.get(index) : null;
			index++;
			if (next != null)
				kf.computeTangent(prev, next);
			else
				kf.computeTangent(prev, kf);
			prev = kf;
			kf = next;
		}
		valuesAreValid = true;
	}
	
	public List<Frame> path() {
		updatePath();
		return path;
	}
	
	protected void updatePath() {
		checkValidity();
		if (!pathIsValid) {
			path.clear();
			int nbSteps = 30;

			if (keyFrameList.isEmpty())
				return;

			if (!valuesAreValid)
				updateModifiedFrameValues();

			if (keyFrameList.get(0) == keyFrameList.get(keyFrameList.size() - 1))
				path.add(new Frame(keyFrameList.get(0).orientation(), keyFrameList.get(0).position(), keyFrameList.get(0).magnitude()));
			else {
				AbstractKeyFrame[] kf = new AbstractKeyFrame[4];
				kf[0] = keyFrameList.get(0);
				kf[1] = kf[0];

				int index = 1;
				kf[2] = (index < keyFrameList.size()) ? keyFrameList.get(index) : null;
				index++;
				kf[3] = (index < keyFrameList.size()) ? keyFrameList.get(index) : null;

				while (kf[2] != null) {
					Vec pdiff = Vec.subtract(kf[2].position(), kf[1].position());
					Vec pvec1 = Vec.add(Vec.multiply(pdiff, 3.0f), Vec.multiply(kf[1].tgP(), (-2.0f)));
					pvec1 = Vec.subtract(pvec1, kf[2].tgP());
					Vec pvec2 = Vec.add(Vec.multiply(pdiff, (-2.0f)), kf[1].tgP());
					pvec2 = Vec.add(pvec2, kf[2].tgP());
					
					// /**
					//Option 2 (interpolate scaling using a spline)
					Vec sdiff = Vec.subtract(kf[2].magnitude(), kf[1].magnitude());
					Vec svec1 = Vec.add(Vec.multiply(sdiff, 3.0f), Vec.multiply(kf[1].tgS(), (-2.0f)));
					svec1 = Vec.subtract(svec1, kf[2].tgS());
					Vec svec2 = Vec.add(Vec.multiply(sdiff, (-2.0f)), kf[1].tgS());
					svec2 = Vec.add(svec2, kf[2].tgS());
					// */
					
					for (int step = 0; step < nbSteps; ++step) {
						Frame frame = new Frame(scene.is3D());
						float alpha = step / (float) nbSteps;
						frame.setPosition(Vec.add(kf[1].position(), Vec.multiply(Vec.add(kf[1].tgP(), Vec.multiply(Vec.add(pvec1, Vec.multiply(pvec2, alpha)), alpha)), alpha)));
					  if( scene.is3D()) {
						  frame.setOrientation(Quat.squad((Quat)kf[1].orientation(), ((KeyFrame3D)kf[1]).tgQ(), ((KeyFrame3D)kf[2]).tgQ(), (Quat)kf[2].orientation(), alpha));
					  }
					  else {
					    //linear interpolation
							float start = kf[1].orientation().angle();
							float stop = kf[2].orientation().angle();
							frame.setOrientation(new Rot(start + (stop-start) * alpha) );
					  }
					  //myFrame.setMagnitude(magnitudeLerp(kf[1], kf[2], alpha));
					  //Option 2 (interpolate scaling using a spline)
					  frame.setMagnitude(Vec.add(kf[1].magnitude(), Vec.multiply(Vec.add(kf[1].tgS(), Vec.multiply(Vec.add(svec1, Vec.multiply(svec2, alpha)), alpha)), alpha)));					  
						path.add(frame.get());
					}

					// Shift
					kf[0] = kf[1];
					kf[1] = kf[2];
					kf[2] = kf[3];

					index++;
					kf[3] = (index < keyFrameList.size()) ? keyFrameList.get(index) : null;
				}
				// Add last KeyFrame
				path.add(new Frame(kf[1].orientation(), kf[1].position(), kf[1].magnitude()));
			}
			pathIsValid = true;
		}
	}
	
	protected void checkValidity() {
		boolean flag = false;
		for (AbstractKeyFrame element : keyFrameList) {
	    if(element.frame().lastUpdate() > lastUpdate()) {
	    	flag = true;
	    	break;
	    }
	  }
		if(flag) {
			this.invalidateValues();
			this.checked();
		}
	}

	/**
	 * Returns the Frame associated with the keyFrame at index {@code index}.
	 * <p>
	 * See also {@link #keyFrameTime(int)}. {@code index} has to be in the range
	 * 0..{@link #numberOfKeyFrames()}-1.
	 * <p>
	 * <b>Note:</b> If this keyFrame was defined using a reference to a Frame (see
	 * {@link #addKeyFrame(Frame, float)} the current referenced Frame
	 * state is returned.
	 */
	public Frame keyFrame(int index) {
		/**
		AbstractKeyFrame kf = keyFr.get(index);
		return new RefFrame(kf.orientation(), kf.position(), kf.magnitude());
		*/
		return keyFrameList.get(index).frame();
	}

	/**
	 * Returns the time corresponding to the {@code index} keyFrame. index has to
	 * be in the range 0..{@link #numberOfKeyFrames()}-1.
	 * 
	 * @see #keyFrame(int)
	 */
	public float keyFrameTime(int index) {
		return keyFrameList.get(index).time();
	}

	/**
	 * Returns the duration of the KeyFrameInterpolator path, expressed in
	 * seconds.
	 * <p>
	 * Simply corresponds to {@link #lastTime()} - {@link #firstTime()}. Returns
	 * 0.0 if the path has less than 2 keyFrames.
	 * 
	 * @see #keyFrameTime(int)
	 */
	public float duration() {
		return lastTime() - firstTime();
	}

	/**
	 * Returns the time corresponding to the first keyFrame, expressed in seconds.
	 * <p>
	 * Returns 0.0 if the path is empty.
	 * 
	 * @see #lastTime()
	 * @see #duration()
	 * @see #keyFrameTime(int)
	 */
	public float firstTime() {
		if (keyFrameList.isEmpty())
			return 0.0f;
		else
			return keyFrameList.get(0).time();
	}

	/**
	 * Returns the time corresponding to the last keyFrame, expressed in seconds.
	 * <p>
	 * 
	 * @see #firstTime()
	 * @see #duration()
	 * @see #keyFrameTime(int)
	 */
	public float lastTime() {
		if (keyFrameList.isEmpty())
			return 0.0f;
		else
			return keyFrameList.get(keyFrameList.size() - 1).time();
	}

	protected void updateCurrentKeyFrameForTime(float time) {
		// Assertion: times are sorted in monotone order.
		// Assertion: keyFrame_ is not empty

		// TODO: Special case for loops when closed path is implemented !!
		if (!currentFrmValid)
			// Recompute everything from scratch
			currentFrame1 = keyFrameList.listIterator();

		// currentFrame_[1]->peekNext() <---> keyFr.get(currentFrame1.nextIndex());
		while (keyFrameList.get(currentFrame1.nextIndex()).time() > time) {
			currentFrmValid = false;
			if (!currentFrame1.hasPrevious())
				break;
			currentFrame1.previous();
		}

		if (!currentFrmValid)
			// *currentFrame_[2] = *currentFrame_[1]; <---> currentFrame2 =
			// keyFr.listIterator( currentFrame1.nextIndex() );
			currentFrame2 = keyFrameList.listIterator(currentFrame1.nextIndex());

		while (keyFrameList.get(currentFrame2.nextIndex()).time() < time) {
			currentFrmValid = false;

			if (!currentFrame2.hasNext())
				break;

			currentFrame2.next();
		}

		if (!currentFrmValid) {
			currentFrame1 = keyFrameList.listIterator(currentFrame2.nextIndex());

			if ((currentFrame1.hasPrevious())
					&& (time < keyFrameList.get(currentFrame2.nextIndex()).time()))
				currentFrame1.previous();

			currentFrame0 = keyFrameList.listIterator(currentFrame1.nextIndex());

			if (currentFrame0.hasPrevious())
				currentFrame0.previous();

			currentFrame3 = keyFrameList.listIterator(currentFrame2.nextIndex());

			if (currentFrame3.hasNext())
				currentFrame3.next();

			currentFrmValid = true;
			splineCacheIsValid = false;
		}
	}

	public void updateSplineCache() {		
		Vec deltaP = Vec.subtract(keyFrameList.get(currentFrame2.nextIndex()).position(), keyFrameList.get(currentFrame1.nextIndex()).position());
		pv1 = Vec.add(Vec.multiply(deltaP, 3.0f), Vec.multiply(keyFrameList.get(currentFrame1.nextIndex()).tgP(), (-2.0f)));
		pv1 = Vec.subtract(pv1, keyFrameList.get(currentFrame2.nextIndex()).tgP());
		pv2 = Vec.add(Vec.multiply(deltaP, (-2.0f)), keyFrameList.get(currentFrame1.nextIndex()).tgP());
		pv2 = Vec.add(pv2, keyFrameList.get(currentFrame2.nextIndex()).tgP());
	
		// /**
		//Option 2 (interpolate scaling using a spline)
		Vec deltaS = Vec.subtract(keyFrameList.get(currentFrame2.nextIndex()).magnitude(), keyFrameList.get(currentFrame1.nextIndex()).magnitude());
		sv1 = Vec.add(Vec.multiply(deltaS, 3.0f), Vec.multiply(keyFrameList.get(currentFrame1.nextIndex()).tgS(), (-2.0f)));
		sv1 = Vec.subtract(sv1, keyFrameList.get(currentFrame2.nextIndex()).tgS());
		sv2 = Vec.add(Vec.multiply(deltaS, (-2.0f)), keyFrameList.get(currentFrame1.nextIndex()).tgS());
		sv2 = Vec.add(sv2, keyFrameList.get(currentFrame2.nextIndex()).tgS());
		// */
		
		splineCacheIsValid = true;
	}

	/**
	 * Interpolate {@link #frame()} at time {@code time} (expressed in seconds).
	 * {@link #interpolationTime()} is set to {@code time} and {@link #frame()} is
	 * set accordingly.
	 * <p>
	 * If you simply want to change {@link #interpolationTime()} but not the
	 * {@link #frame()} state, use {@link #setInterpolationTime(float)} instead.
	 */
	public void interpolateAtTime(float time) {
		this.checkValidity();
		setInterpolationTime(time);

		if ((keyFrameList.isEmpty()) || (frame() == null))
			return;

		if (!valuesAreValid)
			updateModifiedFrameValues();

		updateCurrentKeyFrameForTime(time);

		if (!splineCacheIsValid)
			updateSplineCache();

		float alpha;
		float dt = keyFrameList.get(currentFrame2.nextIndex()).time() - keyFrameList.get(currentFrame1.nextIndex()).time();
		if (Util.zero(dt))
			alpha = 0.0f;
		else
			alpha = (time - keyFrameList.get(currentFrame1.nextIndex()).time()) / dt;		

		// Linear interpolation - debug
		// Vec pos = alpha*(currentFrame2->peekNext()->position()) +
		// (1.0-alpha)*(currentFrame1->peekNext()->position());
		// Vec pos = currentFrame_[1]->peekNext()->position() + alpha *
		// (currentFrame_[1]->peekNext()->tgP() + alpha * (v1+alpha*v2));
		Vec pos = Vec.add(keyFrameList.get(currentFrame1.nextIndex()).position(),
				                        Vec.multiply(Vec.add(keyFrameList.get(currentFrame1.nextIndex()).tgP(),
						                    Vec.multiply(Vec.add(pv1, Vec.multiply(pv2, alpha)), alpha)), alpha));
		
		/**
		//Option 1
		Vector3D mag = magnitudeLerp((keyFr.get(currentFrame1.nextIndex())),
                                 (keyFr.get(currentFrame2.nextIndex())),
                                  (alpha));
		// */
		
		// /**
		//Option 2 (interpolate scaling using a spline)
		Vec mag = Vec.add(keyFrameList.get(currentFrame1.nextIndex()).magnitude(),
                                Vec.multiply(Vec.add(keyFrameList.get(currentFrame1.nextIndex()).tgS(),
                                Vec.multiply(Vec.add(sv1, Vec.multiply(sv2, alpha)), alpha)), alpha));
    // */		

		Orientable q;
		if(scene.is3D()) {
		  q = Quat.squad((Quat)keyFrameList.get(currentFrame1.nextIndex()).orientation(), 
			                    ((KeyFrame3D)keyFrameList.get(currentFrame1.nextIndex())).tgQ(),
			                    ((KeyFrame3D)keyFrameList.get(currentFrame2.nextIndex())).tgQ(),
			                     (Quat)keyFrameList.get(currentFrame2.nextIndex()).orientation(), alpha);
		} else {
			q =  new Rot( rotationLerp(keyFrameList.get(currentFrame1.nextIndex()),
					                                 keyFrameList.get(currentFrame2.nextIndex()),
					                                 ( alpha)));
		}
		
		frame().setPositionWithConstraint(pos);
		frame().setRotationWithConstraint(q);
		frame().setMagnitudeWithConstraint(mag);
	}
	
	protected float rotationLerp(AbstractKeyFrame kf1, AbstractKeyFrame kf2, float alpha) {
		float start = kf1.orientation().angle();
		float stop = kf2.orientation().angle();
		return lerp(start, stop, alpha);
	}
	
	protected Vec magnitudeLerp(AbstractKeyFrame kf1, AbstractKeyFrame kf2, float alpha) {
		return vectorLerp(kf1.magnitude(), kf2.magnitude(), alpha);
	}
	
	protected Vec vectorLerp(Vec start, Vec stop, float alpha) {
		return new Vec( lerp(start.x(), stop.x(), alpha), lerp(start.y(), stop.y(), alpha), lerp(start.z(), stop.z(), alpha) );
	}
	
	protected float lerp(float start, float stop, float alpha) {
		return start + (stop-start) * alpha;
	}
}
