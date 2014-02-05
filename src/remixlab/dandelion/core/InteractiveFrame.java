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

//import remixlab.remixcam.constraint.Constraint;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.AbstractTimerJob;
import remixlab.tersehandling.core.Agent;
import remixlab.tersehandling.core.Grabbable;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.Duoable;
import remixlab.tersehandling.event.*;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;
import remixlab.util.Util;

/**
 * A InteractiveFrame is a Frame that can be rotated and translated using the
 * mouse.
 * <p>
 * It converts the mouse motion into a translation and an orientation updates. A
 * InteractiveFrame is used to move an object in the scene. Combined with object
 * selection, its Grabbable properties and a dynamic update of the scene, the
 * InteractiveFrame introduces a great reactivity in your processing
 * applications.
 * <p>
 * <b>Note:</b> Once created, the InteractiveFrame is automatically added to
 * the {@link remixlab.tersehandling.core.TerseHandler#agents()} pool.
 */

public class InteractiveFrame extends Frame implements Grabbable, Copyable {
	@Override
	public int hashCode() {
    return new HashCodeBuilder(17, 37).
    appendSuper(super.hashCode()).
		append(grabsInputThreshold).
		append(isInCamPath).
		append(isSpng).
		append(rotSensitivity).
		append(spngQuat).
		append(spngSensitivity).
		append(dampFriction).
		append(sFriction).
		append(transSensitivity).
		append(wheelSensitivity).
		append(drvSpd).
		append(flyDisp).
		append(flySpd).
		append(flyUpVec).
    toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;		
		if (obj.getClass() != getClass()) return false;
		
		InteractiveFrame other = (InteractiveFrame) obj;
		return new EqualsBuilder()
    .appendSuper(super.equals(obj))
		.append(grabsInputThreshold, other.grabsInputThreshold)
		.append(isInCamPath, other.isInCamPath)
		.append(isSpng, other.isSpng)
		.append(dampFriction, other.dampFriction)
		.append(sFriction, other.sFriction)
		.append(rotSensitivity, other.rotSensitivity)
		.append(spngQuat,other.spngQuat)
		.append(spngSensitivity,other.spngSensitivity)
		.append(transSensitivity, other.transSensitivity)
		.append(wheelSensitivity, other.wheelSensitivity)	
		.append(drvSpd,other.drvSpd)
		.append(flyDisp,other.flyDisp)
		.append(flySpd,other.flySpd)
		.append(flyUpVec,other.flyUpVec)
		.isEquals();
	}
	
	private int grabsInputThreshold;
	private float rotSensitivity;
	private float transSensitivity;
	private float wheelSensitivity;
	
	// spinning stuff:
	protected float eventSpeed;
	private float spngSensitivity;
	
	//TODO: remove this flag?:
	private boolean isSpng;
	private AbstractTimerJob spinningTimerJob;
	private Orientable spngQuat;
	protected float dampFriction; //new
	//TODO decide whether or not toss should have its own damp var
	// currently its share among the two -> test behavior
	private float sFriction; //new

	// Whether the SCREEN_TRANS direction (horizontal or vertical) is fixed or not.
	public boolean dirIsFixed;
  private boolean horiz = true;// Two simultaneous InteractiveFrame require two mice!

	protected boolean isInCamPath;
	
	// " D R I V A B L E "   S T U F F :
	protected Vec tDir;
	protected float flySpd;
	protected float drvSpd;
	protected AbstractTimerJob flyTimerJob;
	protected Vec flyUpVec;
	protected Vec flyDisp;
	protected static final long FLY_UPDATE_PERDIOD = 10;

	// P R O S C E N E A N D P R O C E S S I N G A P P L E T A N D O B J E C T S
	public AbstractScene scene;

	/**
	 * Default constructor.
	 * <p>
	 * The {@link #translation()} is set to (0,0,0), with an identity
	 * {@link #rotation()} (0,0,0,1) (see Frame constructor for details). The
	 * different sensitivities are set to their default values (see
	 * {@link #rotationSensitivity()} , {@link #translationSensitivity()},
	 * {@link #spinningSensitivity()} and {@link #wheelSensitivity()}).
	 * <p>
	 * <b>Note:</b> the InteractiveFrame is automatically added to
	 * the {@link remixlab.tersehandling.core.TerseHandler#agents()} pool.
	 */
	public InteractiveFrame(AbstractScene scn) {
		super(scn.is3D());		
		scene = scn;		

		scene.terseHandler().addInAllAgentPools(this);
		isInCamPath = false;

		setGrabsInputThreshold(10);
		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setWheelSensitivity(20.0f);
		
		isSpng = false;
		setSpinningSensitivity(0.3f);
		setDampingFriction(0.5f);
		
		spinningTimerJob = new AbstractTimerJob() {
			public void execute() {
				spin();
			}
		};	
		scene.registerJob(spinningTimerJob);
		
		// Drivable stuff:
		drvSpd = 0.0f;
		flyUpVec = new Vec(0.0f, 1.0f, 0.0f);

		flyDisp = new Vec(0.0f, 0.0f, 0.0f);

		if(! (this instanceof InteractiveEyeFrame) )
			setFlySpeed(0.01f * scene.radius());

		flyTimerJob = new AbstractTimerJob() {
			public void execute() {
				toss();
			}
		};		
		scene.registerJob(flyTimerJob);
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param otherFrame the other interactive frame
	 */
	protected InteractiveFrame(InteractiveFrame otherFrame) {
		super(otherFrame);
		this.scene = otherFrame.scene;
		
		//this.scene.terseHandler().addInAllAgentPools(this);
		for (Agent element : this.scene.terseHandler().agents() ) {
			if( this.scene.terseHandler().isInAgentPool(otherFrame, element) )
				this.scene.terseHandler().addInAgentPool(this, element);
		}
		
		this.isInCamPath = otherFrame.isInCamPath;

		this.setGrabsInputThreshold( otherFrame.grabsInputThreshold()  );
		this.setRotationSensitivity( otherFrame.rotationSensitivity() );
		this.setTranslationSensitivity( otherFrame.translationSensitivity() );
		this.setWheelSensitivity( otherFrame.wheelSensitivity() );

		this.isSpng = otherFrame.isSpng;
		this.setSpinningSensitivity( otherFrame.spinningSensitivity() );
		this.setDampingFriction( otherFrame.dampingFriction() );
		
		this.spinningTimerJob = new AbstractTimerJob() {
			public void execute() {
				spin();
			}
		};		
		this.scene.registerJob(spinningTimerJob);
		
		// Drivable stuff:
		this.drvSpd = otherFrame.drvSpd;
		this.flyUpVec = new Vec();
		this.flyUpVec.set(otherFrame.flyUpVector());
		this.flyDisp = new Vec();
		this.flyDisp.set(otherFrame.flyDisp);
		this.setFlySpeed( otherFrame.flySpeed() );
		
		this.flyTimerJob = new AbstractTimerJob() {
			public void execute() {
				toss();
			}
		};		
		this.scene.registerJob(flyTimerJob);
	}
  
	/**
	 * Calls {@link #InteractiveFrame(InteractiveFrame)} (which is protected) and returns a copy of
	 * {@code this} object.
	 * 
	 * @see #InteractiveFrame(InteractiveFrame)
	 */
	public InteractiveFrame get() {
		return new InteractiveFrame(this);
	}

	/**
	 * Ad-hoc constructor needed to make editable a Camera path defined by
	 * KeyFrameInterpolator.
	 * <p>
	 * Constructs a Frame from the the {@code iFrame} {@link #translation()} and
	 * {@link #orientation()} and immediately adds it to the
	 * {@link remixlab.tersehandling.core.TerseHandler#agents()} pool.
	 * <p>
	 * A call on {@link #isInCameraPath()} on this Frame will return {@code true}.
	 * 
	 * <b>Attention:</b> Internal use. You should not call this constructor in your
	 * own applications.
	 * 
	 * @see remixlab.dandelion.core.Camera#addKeyFrameToPath(int)
	 */
	public InteractiveFrame(AbstractScene scn, InteractiveEyeFrame iFrame) {
		super(iFrame.rotation(), iFrame.translation(), iFrame.scaling());
		scene = scn;

		isInCamPath = true;
		
		setGrabsInputThreshold(10);
		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setWheelSensitivity(20.0f);
		
		
		isSpng = false;
		setSpinningSensitivity(0.3f);
		setDampingFriction(0.5f);
				
		spinningTimerJob = new AbstractTimerJob() {
			public void execute() {
				spin();
			}
		};		
		scene.registerJob(spinningTimerJob);
		
		// Drivable stuff:
		drvSpd = 0.0f;
		flyUpVec = new Vec(0.0f, 1.0f, 0.0f);
		flyDisp = new Vec(0.0f, 0.0f, 0.0f);
		setFlySpeed(0.0f);
		flyTimerJob = new AbstractTimerJob() {
			public void execute() {
				toss();
			}
		};
		scene.registerJob(flyTimerJob);
	}	

	/**
	 * Convenience function that simply calls {@code applyTransformation(AbstractScene)}.
	 * 
	 * @see remixlab.dandelion.core.Frame#applyTransformation(AbstractScene)
	 */
	public void applyTransformation() {
		applyTransformation(scene);
	}
	
	/**
	 * Convenience function that simply calls {@code applyWorldTransformation(Abstractscene)}
	 * 
	 * @see remixlab.dandelion.core.Frame#applyWorldTransformation(AbstractScene)
	 */
	public void applyWorldTransformation() {
		applyWorldTransformation(scene);
	}

	/**
	 * Returns {@code true} if the InteractiveFrame forms part of a Camera path
	 * and {@code false} otherwise.
	 * 
	 */
	public boolean isInCameraPath() {
		return isInCamPath;
	}
	
	/**
	 * Returns the grabs mouse threshold which is used by this interactive frame to
	 * {@link #checkIfGrabsInput(TerseEvent)}.
	 * 
	 * @see #setGrabsInputThreshold(int)
	 */
	public int grabsInputThreshold() {
		return grabsInputThreshold;
	}
	
	/**
	 * Sets the number of pixels that defined the {@link #checkIfGrabsInput(TerseEvent)}
	 * condition.
	 * 
	 * @param threshold number of pixels that defined the {@link #checkIfGrabsInput(TerseEvent)}
	 * condition. Default value is 10 pixels (which is set in the constructor). Negative values are
	 * silently ignored.
	 * 
	 * @see #grabsInputThreshold()
	 * @see #checkIfGrabsInput(TerseEvent)
	 */
	public void setGrabsInputThreshold( int threshold ) {
		if(threshold >= 0)
			grabsInputThreshold = threshold; 
	}

	/**
	 * Implementation of the Grabbable main method.
	 * <p>
	 * The InteractiveFrame {@link #grabsAgent(Agent)} when the mouse is within a
	 * {@link #grabsInputThreshold()} pixels region around its
	 * {@link remixlab.dandelion.core.Camera#projectedCoordinatesOf(Vec)}
	 * {@link #position()}.
	 */
	@Override
	public boolean checkIfGrabsInput(TerseEvent event) {
		DOF2Event event2 = null;
		
		if ( ( ! (event instanceof MotionEvent) ) || (event instanceof DOF1Event) )  {
			throw new RuntimeException("Gravving an interactive frame requires at least a DOF2 event");
		}
		
		if( event instanceof DOF2Event )
			event2 = ((DOF2Event)event).get();
		else if( event instanceof DOF3Event )
			event2 = ((DOF3Event)event).dof2Event();
		else if( event instanceof DOF6Event )
			event2 = ((DOF6Event)event).dof3Event().dof2Event();
		
		Vec proj = scene.eye().projectedCoordinatesOf(position());
		
		return ((Math.abs(event2.x() - proj.vec[0]) < grabsInputThreshold()) &&
		        (Math.abs(event2.y() - proj.vec[1]) < grabsInputThreshold()));
	}

	/**
	 * Returns {@code true} when this frame grabs the Scene's {@code agent}.
	 * 
	 * @see #checkIfGrabsInput(TerseEvent)
	 */
	@Override
	public boolean grabsAgent(Agent agent) {
		return agent.grabber() == this;
	}

	/**
	 * Returns {@code agent.isInPool(this)}.
	 * 
	 * @see remixlab.tersehandling.core.Agent#isInPool(Grabbable)
	 */
	public boolean isInAgentPool(Agent agent) {
		return agent.isInPool(this);
	}

	/**
	 * Convenience wrapper function that simply calls {agent.addInPool(this)}.
	 * 
	 * @see remixlab.tersehandling.core.Agent#addInPool(Grabbable)
	 */
	public void addInAgentPool(Agent agent) {
		agent.addInPool(this);
	}

	/**
	 * Convenience wrapper function that simply calls {@code agent.removeFromPool(this)}.
	 * 
	 * @see remixlab.tersehandling.core.Agent#removeFromPool(Grabbable)
	 */
	public void removeFromAgentPool(Agent agent) {
		agent.removeFromPool(this);
	}

	/**
	 * Defines the {@link #rotationSensitivity()}.
	 */
	public final void setRotationSensitivity(float sensitivity) {
		rotSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #translationSensitivity()}.
	 */
	public final void setTranslationSensitivity(float sensitivity) {
		transSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #spinningSensitivity()}.
	 */
	public final void setSpinningSensitivity(float sensitivity) {
		spngSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #wheelSensitivity()}.
	 */
	public final void setWheelSensitivity(float sensitivity) {
		wheelSensitivity = sensitivity;
	}

	/**
	 * Returns the influence of a mouse displacement on the InteractiveFrame
	 * rotation.
	 * <p>
	 * Default value is 1.0. With an identical mouse displacement, a higher value
	 * will generate a larger rotation (and inversely for lower values). A 0.0
	 * value will forbid InteractiveFrame mouse rotation (see also
	 * {@link #constraint()}).
	 * 
	 * @see #setRotationSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float rotationSensitivity() {
		return rotSensitivity;
	}

	/**
	 * Returns the influence of a mouse displacement on the InteractiveFrame
	 * translation.
	 * <p>
	 * Default value is 1.0. You should not have to modify this value, since with
	 * 1.0 the InteractiveFrame precisely stays under the mouse cursor.
	 * <p>
	 * With an identical mouse displacement, a higher value will generate a larger
	 * translation (and inversely for lower values). A 0.0 value will forbid
	 * InteractiveFrame mouse translation (see also {@link #constraint()}).
	 * <p>
	 * <b>Note:</b> When the InteractiveFrame is used to move a <i>Camera</i> (see
	 * the InteractiveCameraFrame class documentation), after zooming on a small
	 * region of your scene, the camera may translate too fast. For a camera, it
	 * is the Camera.arcballReferencePoint() that exactly matches the mouse
	 * displacement. Hence, instead of changing the
	 * {@link #translationSensitivity()}, solve the problem by (temporarily)
	 * setting the {@link remixlab.dandelion.core.Camera#arcballReferencePoint()} to a
	 * point on the zoomed region).
	 * 
	 * @see #setTranslationSensitivity(float)
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float translationSensitivity() {
		return transSensitivity;
	}

	/**
	 * Returns the minimum mouse speed required (at button release) to make the
	 * InteractiveFrame {@link #spin()}.
	 * <p>
	 * See {@link #spin()}, {@link #spinningOrientation()} and
	 * {@link #startSpinning(MotionEvent)} for details.
	 * <p>
	 * Mouse speed is expressed in pixels per milliseconds. Default value is 0.3
	 * (300 pixels per second). Use {@link #setSpinningSensitivity(float)} to tune
	 * this value. A higher value will make spinning more difficult (a value of
	 * 100.0 forbids spinning in practice).
	 * 
	 * @see #setSpinningSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float spinningSensitivity() {
		return spngSensitivity;
	}

	/**
	 * Returns the mouse wheel sensitivity.
	 * <p>
	 * Default value is 20.0. A higher value will make the wheel action more
	 * efficient (usually meaning a faster zoom). Use a negative value to invert
	 * the zoom in and out directions.
	 * 
	 * @see #setWheelSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 */
	public float wheelSensitivity() {
		return wheelSensitivity;
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is spinning.
	 * <p>
	 * During spinning, {@link #spin()} rotates the InteractiveFrame by its
	 * {@link #spinningOrientation()} at a frequency defined when the
	 * InteractiveFrame {@link #startSpinning(MotionEvent)}.
	 * <p>
	 * Use {@link #startSpinning(MotionEvent)} and {@link #stopSpinning()} to change this
	 * state. Default value is {@code false}.
	 */
	public final boolean isSpinning() {
		return isSpng;
	}

	/**
	 * Returns the incremental rotation that is applied by {@link #spin()} to the
	 * InteractiveFrame orientation when it {@link #isSpinning()}.
	 * <p>
	 * Default value is a {@code null} rotation (identity Quaternion). Use
	 * {@link #setSpinningOrientation(Orientable)} to change this value.
	 * <p>
	 * The {@link #spinningOrientation()} axis is defined in the InteractiveFrame
	 * coordinate system. You can use
	 * {@link remixlab.dandelion.core.Frame#transformOfFrom(Vec, Frame)}
	 * to convert this axis from another Frame coordinate system.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #dampingFriction()}
	 * till it stops completely.
	 * 
	 * @see #tossingDirection()
	 */
	public final Orientable spinningOrientation() {
		return spngQuat;
	}
	
	public final Vec tossingDirection() {
		return tDir;
	}
	
	public final void setTossingDirection(Vec dir) {
		tDir = dir;
	}

	/**
	 * Defines the {@link #spinningOrientation()}. Its axis is defined in the
	 * InteractiveFrame coordinate system.
	 * 
	 * @see #setTossingDirection(Vec)
	 */
	public final void setSpinningOrientation(Orientable spinningQuaternion) {
		spngQuat = spinningQuaternion;
	}
	
	/**
   * Returns {@code true} when the InteractiveFrame is being manipulated with
   * an agent. Can be used to change the display of the manipulated object
   * during manipulation.
   */
  public boolean isInInteraction() {
  	return currentAction != null;
  }
	
	public final void stopTossing() {
		this.flyTimerJob.stop();
	}
	
	/**
	 * Stops the spinning motion started using {@link #startSpinning(MotionEvent)}.
	 * {@link #isSpinning()} will return {@code false} after this call.
	 * <p>
	 * <b>Attention: </b>This method may be called by {@link #spin()}, since spinning may
	 * be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #toss()
	 */
	public final void stopSpinning() {
		spinningTimerJob.stop();
		isSpng = false;
	}

	/**
	 * Starts the spinning of the InteractiveFrame.
	 * <p>
	 * This method starts a timer that will call {@link #spin()} every {@code
	 * updateInterval} milliseconds. The InteractiveFrame {@link #isSpinning()}
	 * until you call {@link #stopSpinning()}.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #dampingFriction()}
	 * till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #toss()
	 */
	public void startSpinning(MotionEvent e) {
		eventSpeed = e.speed();	
		isSpng = true;
		int updateInterval = (int) e.delay();
		if(updateInterval>0)
			spinningTimerJob.run(updateInterval);
	}
	
	public void startTossing(MotionEvent e) {
		eventSpeed = e.speed();
		flyTimerJob.run(FLY_UPDATE_PERDIOD);
	}
	
	/**
	 * Rotates the InteractiveFrame by its {@link #spinningOrientation()}. Called
	 * by a timer when the InteractiveFrame {@link #isSpinning()}. 
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to
	 * {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #toss()
	 */
	public void spin() {		
		if(Util.nonZero(dampingFriction())) {
			if (eventSpeed == 0) {
				stopSpinning();
				return;
			}
			rotate(spinningOrientation());
			recomputeSpinningQuaternion();						
		}
		else
			rotate(spinningOrientation());
	}
	
	public void toss() {		
		if(Util.nonZero(dampingFriction())) {
			if (eventSpeed == 0) {
				stopTossing();
				return;
			}
			translate(tossingDirection());
			recomputeTossingDirection();						
		}
		else
			translate(tossingDirection());
	}
	
	/**
	 * Defines the {@link #dampingFriction()}. Values must be
	 * in the range [0..1].
	 */
	public void setDampingFriction(float f) {
		if(f < 0 || f > 1)
			return;
		dampFriction = f;
		setDampingFrictionFx(dampFriction);
	} 
	
	/**
	 * Defines the spinning deceleration.
	 * <p>
	 * Default value is 0.5, i.e., no spinning deceleration. Use
	 * {@link #setDampingFriction(float)} to tune this value.
	 * A higher value will make spinning more difficult (a value of
	 * 1.0 forbids spinning).
	 */
	public float dampingFriction() {
		return dampFriction;
	}
	
	/**
	 * Internal use.
	 * <p>
	 * Computes and caches the value of the spinning friction used in
	 * {@link #recomputeSpinningQuaternion()}.
	 */
	protected void setDampingFrictionFx(float spinningFriction) {
		sFriction = spinningFriction*spinningFriction*spinningFriction;
	}
	
	/**
	 * Internal use.
	 * <p>
	 * Returns the cached value of the spinning friction used in
	 * {@link #recomputeSpinningQuaternion()}.
	 */
	protected float dampingFrictionFx() {
		return sFriction;
	}
	
	/**
	 * Internal method. Recomputes the {@link #spinningOrientation()}
	 * according to {@link #dampingFriction()}.
	 * 
	 * @see #recomputeTossingDirection()
	 */
	protected void recomputeSpinningQuaternion() {
		float prevSpeed = eventSpeed;
		float damping = 1.0f - dampingFrictionFx();
		eventSpeed *= damping;
		if (Math.abs(eventSpeed) < .001f)
			eventSpeed = 0;
		//float currSpeed = eventSpeed;
		if( scene.is3D() )
			((Quat)spinningOrientation()).fromAxisAngle(((Quat)spinningOrientation()).axis(), spinningOrientation().angle() * (eventSpeed / prevSpeed) );
		else
			this.setSpinningOrientation(new Rot(spinningOrientation().angle() * (eventSpeed / prevSpeed)));
	}
	
	protected void recomputeTossingDirection() {
		float prevSpeed = eventSpeed;
		float damping = 1.0f - dampingFrictionFx();
		eventSpeed *= damping;
		if (Math.abs(eventSpeed) < .001f)
			eventSpeed = 0;
		
		flyDisp.setZ(flyDisp.z() * (eventSpeed / prevSpeed));
		
		if(scene.is2D())
			setTossingDirection(localInverseTransformOf(flyDisp));
		else
			setTossingDirection(rotation().rotate(flyDisp));
	}
	
	@Override
	public void performInteraction(TerseEvent e) {
		//TODO following line prevents spinning when frameRate is low (as P5 default)
		//if( isSpinning() && Util.nonZero(dampingFriction()) ) stopSpinning();
		stopTossing();	
		if(e == null) return;		
		if(e instanceof KeyboardEvent) {
			scene.performInteraction(e);
			return;
		}
		//new
		if(e instanceof GenericClickEvent) {
			GenericClickEvent<?> genericClickEvent = (GenericClickEvent<?>)e;
			if( genericClickEvent.action() == null ) return;		
			if( genericClickEvent.action() != ClickAction.CENTER_FRAME && 
					genericClickEvent.action() != ClickAction.ALIGN_FRAME &&
					genericClickEvent.action() != ClickAction.ZOOM_ON_PIXEL &&
					genericClickEvent.action() != ClickAction.ARP_FROM_PIXEL &&
					genericClickEvent.action() != ClickAction.CUSTOM ) {
				scene.performInteraction(e); // ;)
				return;
			}
			if( ( scene.is2D() ) && ( ((DandelionAction)genericClickEvent.action().referenceAction()).is2D() ) ) {
				cEvent = (ClickEvent) e.get();
				execAction2D( ((DandelionAction)genericClickEvent.action().referenceAction() ) );
				return;
			}
			else
				if(scene.is3D()) {
					cEvent = (ClickEvent) e.get();
					execAction3D( ((DandelionAction)genericClickEvent.action().referenceAction() ) );
					return;
				}
		}
		//end
		// then it's a MotionEvent		
		Duoable<?> event;		
		if(e instanceof Duoable)
			event = (Duoable<?>)e;
		else 
			return;	
		// same as no action
		if( event.action() == null )
			return;
		if( scene.is2D() ) 
			if(( ((DandelionAction)event.action().referenceAction()).is2D() ) )
				execAction2D( reduceEvent( (MotionEvent)e ));
			else
				AbstractScene.showDepthWarning((DandelionAction) event.action().referenceAction());
		else
			if(scene.is3D())
				execAction3D( reduceEvent( (MotionEvent)e ));
	}
	
	//MotionEvent currentEvent;
	ClickEvent cEvent;
	DOF1Event e1;
	DOF2Event e2;
	DOF3Event e3;
	DOF6Event e6;
	DandelionAction currentAction;
	
	protected DandelionAction reduceEvent(MotionEvent e) {
		//currentEvent = e;
		if( !(e instanceof Duoable) )	return null;
		
		currentAction = (DandelionAction) ((Duoable<?>) e).action().referenceAction();		
		if( currentAction == null ) return null;

		int dofs = currentAction.dofs();
		
		switch(dofs) {
		case 1:
			if( e instanceof DOF1Event )
				e1 = (DOF1Event) e.get();
			else if( e instanceof DOF2Event )
				e1 = currentAction == DandelionAction.ROLL || currentAction == DandelionAction.DRIVE ? ((DOF2Event)e).dof1Event() : ((DOF2Event)e).dof1Event(false);
			else if( e instanceof DOF3Event )
				e1 = currentAction == DandelionAction.ROLL || currentAction == DandelionAction.DRIVE ? ((DOF3Event)e).dof2Event().dof1Event() : ((DOF3Event)e).dof2Event().dof1Event(false);
			else if( e instanceof DOF6Event )
				e1 = currentAction == DandelionAction.ROLL || currentAction == DandelionAction.DRIVE ? ((DOF6Event)e).dof3Event().dof2Event().dof1Event() : ((DOF6Event)e).dof3Event().dof2Event().dof1Event(false);
			break;
		case 2:
			if( e instanceof DOF2Event )
				e2 = ((DOF2Event)e).get();
			else if( e instanceof DOF3Event )
				e2 = ((DOF3Event)e).dof2Event();
			else if( e instanceof DOF6Event )
				e2 = ((DOF6Event)e).dof3Event().dof2Event();
			break;
		case 3:
			if( e instanceof DOF3Event )
				e3 = ((DOF3Event)e).get();
			else if( e instanceof DOF6Event )
				e3 = ((DOF6Event)e).dof3Event();
			if(scene.is2D())
				e2 = e3.dof2Event();
			break;
		case 6:
			if( e instanceof DOF6Event )
			  e6 = ((DOF6Event)e).get();
			break;
		default:
		  break;
		}
		return currentAction;		
	}
	
	public void execAction2D(DandelionAction a) {
		if(a==null) return;
		Vec trans;
		float deltaX, deltaY;
		Orientable rot;
		float angle;
		switch(a) {
		case CUSTOM:
			AbstractScene.showMissingImplementationWarning(a, this.getClass().getName());
			break;
		//better handled these by default (see below)
		/*
		case ZOOM_ON_REGION:
		case ARP_FROM_PIXEL:
		case ZOOM_ON_PIXEL:
			AbstractScene.showOnlyEyeWarning(a);
			break;
		*/
		case ROLL:
			//TODO needs testing
			if( e1 instanceof GenericDOF1Event ) //its a wheel wheel :P
				angle = (float) Math.PI * e1.x()	* wheelSensitivity() / scene.camera().screenWidth();
			else
				if( e1.isAbsolute() )
					angle = (float) Math.PI * e1.x()	/ scene.camera().screenWidth();
				else
					angle = (float) Math.PI * e1.dx()/ scene.camera().screenWidth();			
		  //lef-handed coordinate system correction
			if ( scene.isLeftHanded() )
				angle = -angle;			
			rot = new Rot(angle);
			rotate(rot);
			setSpinningOrientation(rot);
			//TODO needs this:?
			updateFlyUpVector();
			break;
		case ROTATE:
		case SCREEN_ROTATE:
			trans = scene.window().projectedCoordinatesOf(position());
			if(e2.isRelative()) {
				Point prevPos = new Point(e2.prevX(), e2.prevY());
				Point curPos= new Point(e2.x(), e2.y());
				rot = new Rot(new Point(trans.x(), trans.y()), prevPos, curPos);
				rot = new Rot(rot.angle() * rotationSensitivity());
			}
			else 
				rot = new Rot(e2.x() * rotationSensitivity());			
			if ( isFlipped() ) rot.negate();	
			if (scene.window().frame().magnitude().x() * scene.window().frame().magnitude().y() < 0 ) rot.negate();
			if(e2.isRelative()) {
				setSpinningOrientation(rot);
				if( Util.nonZero(dampingFriction()) ) startSpinning(e2); else spin();
			} else //absolute needs testing
				rotate(rot);
			break;
		case SCREEN_TRANSLATE:
			deltaX = (e2.isRelative()) ? e2.dx() : e2.x();
			if(e2.isRelative())
				deltaY = scene.isRightHanded() ? e2.dy() : -e2.dy();
			else
				deltaY = scene.isRightHanded() ? e2.y() : -e2.y();
			trans = new Vec();
			int dir = originalDirection(e2);
			if (dir == 1)
				trans.set(deltaX, 0.0f, 0.0f);
			else if (dir == -1)
				trans.set(0.0f, -deltaY, 0.0f);				
			trans = scene.window().frame().inverseTransformOf(Vec.multiply(trans, translationSensitivity()));				
			// And then down to frame
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			break;
		case TRANSLATE:
			deltaX = (e2.isRelative()) ? e2.dx() : e2.x();
			if(e2.isRelative())
				deltaY = scene.isRightHanded() ? e2.dy() : -e2.dy();
			else
				deltaY = scene.isRightHanded() ? e2.y() : -e2.y();
			trans = new Vec(deltaX, -deltaY, 0.0f);
			trans = scene.window().frame().inverseTransformOf(Vec.multiply(trans, translationSensitivity()));				
			// And then down to frame
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			break;
		//TODO needs testing with space navigator
		case TRANSLATE_ROTATE:
			//translate
			deltaX = (e6.isRelative()) ? e6.dx() : e6.x();
			if(e6.isRelative())
				deltaY = scene.isRightHanded() ? e6.dy() : -e6.dy();
			else
				deltaY = scene.isRightHanded() ? e6.y() : -e6.y();
			trans = new Vec(deltaX, -deltaY, 0.0f);
			trans = scene.window().frame().inverseTransformOf(Vec.multiply(trans, translationSensitivity()));				
			// And then down to frame
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			//rotate
			trans = scene.window().projectedCoordinatesOf(position());
			//TODO "relative" is experimental here.
			//Hard to think of a DOF6 relative device in the first place.
			if(e6.isRelative())
				rot = new Rot(e6.drx() * rotationSensitivity());
			else 
				rot = new Rot(e6.rx() * rotationSensitivity());			
			if ( isFlipped() ) rot.negate();	
			if (scene.window().frame().magnitude().x() * scene.window().frame().magnitude().y() < 0 ) rot.negate();			
			if(e6.isRelative()) {
				setSpinningOrientation(rot);
				if( Util.nonZero(dampingFriction()) ) startSpinning(e6);	else spin();
			} else //absolute needs testing
			  // absolute should simply go (only relative has speed which is needed by start spinning):
				rotate(rot);
			break;
		case SCALE:
			float delta;
			if( e1 instanceof GenericDOF1Event ) //its a wheel wheel :P
				delta = e1.x() * wheelSensitivity();
			else
				if( e1.isAbsolute() )
					delta = e1.x();
				else
					delta = e1.dx();
			if(delta >= 0)
				scale(1 + Math.abs(delta) / (float) scene.height());
			else {
				//inverseScale(1 + Math.abs(delta) / (float) scene.height());
				float s = 1 + Math.abs(delta) / (float) scene.height();
				setScaling(Vec.divide(scaling(), new Vec(s,s)));
			}
			break;
		case CENTER_FRAME:
			projectOnLine(scene.window().position(), scene.window().viewDirection());
			break;
		case ALIGN_FRAME:
			alignWithFrame(scene.window().frame());
			break;
		default:
			AbstractScene.showOnlyEyeWarning(a);
			break;
		}
	}
	
	public void execAction3D(DandelionAction a) {
		if(a==null) return;
		Quat q, rot;
		Vec trans;
		//Vec t;
		float angle;
		switch(a) {
		case CUSTOM:
			AbstractScene.showMissingImplementationWarning(a, getClass().getName());
			break;
		case DRIVE:
			rotate(turnQuaternion(e1, scene.camera()));
			if( e1 instanceof GenericDOF1Event ) //its a wheel wheel :P
				drvSpd = 0.01f * -e1.x() * wheelSensitivity();
			else
				if( e1.isAbsolute() )
					drvSpd = 0.01f * -e1.x();
				else
					drvSpd = 0.01f * -e1.dx();
			flyDisp.set(0.0f, 0.0f, flySpeed() * drvSpd);
			if(scene.is2D())
				trans = localInverseTransformOf(flyDisp);
			else
				trans = rotation().rotate(flyDisp);			
			setTossingDirection(trans);
			startTossing(e1);
			break;
		case LOOK_AROUND:
			rotate(pitchYawQuaternion(e2, scene.camera()));
			break;
		case MOVE_BACKWARD:
			rotate(pitchYawQuaternion(e2, scene.camera()));
			flyDisp.set(0.0f, 0.0f, flySpeed());
			if(scene.is2D())
				trans = localInverseTransformOf(flyDisp);
			else
				trans = rotation().rotate(flyDisp);			
			setTossingDirection(trans);
			startTossing(e2);
			break;
		case MOVE_FORWARD:
			rotate(pitchYawQuaternion(e2, scene.camera()));
			flyDisp.set(0.0f, 0.0f, -flySpeed());
			if(scene.is2D())
				trans = localInverseTransformOf(flyDisp);
			else
				trans = rotation().rotate(flyDisp);			
			setTossingDirection(trans);
			startTossing(e2);
			break;
		case ROLL:
			if( e1 instanceof GenericDOF1Event ) //its a wheel wheel :P
				angle = (float) Math.PI * e1.x()	* wheelSensitivity() / scene.camera().screenWidth();
			else
				if( e1.isAbsolute() )
					angle = (float) Math.PI * e1.x()	/ scene.camera().screenWidth();
				else
					angle = (float) Math.PI * e1.dx()/ scene.camera().screenWidth();			
		  //lef-handed coordinate system correction
			if ( scene.isLeftHanded() )
				angle = -angle;			
			q = new Quat(new Vec(0.0f, 0.0f, 1.0f), angle);
			rotate(q);
			setSpinningOrientation(q);
			updateFlyUpVector();
			break;
		case ROTATE:
			if(e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = scene.camera().projectedCoordinatesOf(position());
			rot = deformedBallQuaternion(e2, trans.x(), trans.y(), scene.camera());
			rot = iFrameQuaternion(rot, scene.camera());			
			setSpinningOrientation(rot);
			if( Util.nonZero(dampingFriction()) ) startSpinning(e2); else spin();
			break;		
		case ROTATE3:
			q = new Quat();
			trans = scene.camera().projectedCoordinatesOf(position());
	    if(e3.isAbsolute())
	    	q.fromEulerAngles(e3.x(), e3.y(), -e3.z());
	    else
	    	q.fromEulerAngles(e3.dx(), e3.dy(), -e3.dz());
	    trans.set(-q.x(), -q.y(), -q.z());
	    trans = scene.camera().frame().orientation().rotate(trans);
	    trans = transformOf(trans, false);
	    q.setX(trans.x());
	    q.setY(trans.y());
	    q.setZ(trans.z());
	    rotate(q);
			break;
		case SCREEN_ROTATE:
			if(e2.isAbsolute()) {
				AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = scene.camera().projectedCoordinatesOf(position());
			float prev_angle = (float) Math.atan2(e2.prevY() - trans.vec[1], e2.prevX() - trans.vec[0]);
			angle = (float) Math.atan2(e2.y() - trans.vec[1], e2.x() - trans.vec[0]);			
			Vec axis = transformOf(scene.camera().frame().inverseTransformOf(new Vec(0.0f, 0.0f, -1.0f)));			
			//TODO testing handed
			if( scene.isRightHanded() )
				rot = new Quat(axis, angle - prev_angle);
			else
				rot = new Quat(axis, prev_angle - angle);
			setSpinningOrientation(rot);
			if( Util.nonZero(dampingFriction()) ) startSpinning(e2);	else spin();
			break;
		case SCREEN_TRANSLATE:
		  // TODO: needs testing to see if it works correctly when left-handed is set
			int dir = originalDirection(e2);
			trans = new Vec();
			if (dir == 1)
				if( e2.isAbsolute() )
					trans.set(e2.x(), 0.0f, 0.0f);
				else
					trans.set(e2.dx(), 0.0f, 0.0f);
			else if (dir == -1)
				if( e2.isAbsolute() )
					trans.set(0.0f, e2.y(), 0.0f);
				else
					trans.set(0.0f, e2.dy(), 0.0f);	
			switch ( scene.camera().type() ) {
			case PERSPECTIVE:
				trans.multiply(2.0f * (float) Math.tan(scene.camera().fieldOfView() / 2.0f)
						            * Math.abs((scene.camera().frame().coordinatesOf(position())).vec[2] * scene.camera().frame().magnitude().z())
						            //* Math.abs((camera.frame().coordinatesOf(position())).vec[2])						            
						            / scene.camera().screenHeight());
				break;
			case ORTHOGRAPHIC:
				float[] wh = scene.camera().getBoundaryWidthHeight();
				trans.vec[0] *= 2.0 * wh[0] / scene.camera().screenWidth();
				trans.vec[1] *= 2.0 * wh[1] / scene.camera().screenHeight();
				break;
			}
			trans = scene.camera().frame().orientation().rotate(Vec.multiply(trans, translationSensitivity()));
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			break;
		case TRANSLATE:
			if(e2.isRelative())
			  trans = new Vec(e2.dx(), scene.isRightHanded() ? -e2.dy() : e2.dy(), 0.0f);
			else
				trans = new Vec(e2.x(), scene.isRightHanded() ? -e2.y() : e2.y(), 0.0f);
		  // Scale to fit the screen mouse displacement
			switch ( scene.camera().type() ) {
			case PERSPECTIVE:
				trans.multiply(2.0f * (float) Math.tan(scene.camera().fieldOfView() / 2.0f)
						            * Math.abs((scene.camera().frame().coordinatesOf(position())).vec[2] * scene.camera().frame().magnitude().z())
								        //* Math.abs((scene.camera().frame().coordinatesOf(position())).vec[2])						            
								        / scene.camera().screenHeight());
				break;
				case ORTHOGRAPHIC: {
					float[] wh = scene.camera().getBoundaryWidthHeight();
					trans.vec[0] *= 2.0 * wh[0] / scene.camera().screenWidth();
					trans.vec[1] *= 2.0 * wh[1] / scene.camera().screenHeight();
					break;
				}
			}
			// same as:
			trans = scene.camera().frame().orientation().rotate(Vec.multiply(trans, translationSensitivity()));
			// but takes into account scaling
			//trans = scene.camera().frame().inverseTransformOf(Vector3D.mult(trans, translationSensitivity()));
			// And then down to frame
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			break;
		case TRANSLATE3:
			if(e3.isRelative())
			  trans = new Vec(e3.dx(), scene.isRightHanded() ? -e3.dy() : e3.dy(), e3.dz());
			else
				trans = new Vec(e3.x(), scene.isRightHanded() ? -e3.y() : e3.y(), e3.z());
		  // Scale to fit the screen mouse displacement
			switch ( scene.camera().type() ) {
			case PERSPECTIVE:
				trans.multiply(2.0f * (float) Math.tan(scene.camera().fieldOfView() / 2.0f)
						            * Math.abs((scene.camera().frame().coordinatesOf(position())).vec[2] * scene.camera().frame().magnitude().z())
								        //* Math.abs((scene.camera().frame().coordinatesOf(position())).vec[2])						            
								        / scene.camera().screenHeight());
				break;
				case ORTHOGRAPHIC: {
					float[] wh = scene.camera().getBoundaryWidthHeight();
					trans.vec[0] *= 2.0 * wh[0] / scene.camera().screenWidth();
					trans.vec[1] *= 2.0 * wh[1] / scene.camera().screenHeight();
					break;
				}
			}
		  // same as:
			trans = scene.camera().frame().orientation().rotate(Vec.multiply(trans, translationSensitivity()));
			// but takes into account scaling
			//trans = scene.camera().frame().inverseTransformOf(Vector3D.mult(trans, translationSensitivity()));
			// And then down to frame
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			break;
		case TRANSLATE_ROTATE:
		  // A. Translate the iFrame
			if(e6.isRelative())
			  trans = new Vec(e6.dx(), scene.isRightHanded() ? -e6.dy() : e6.dy(), e6.dz());
			else
				trans = new Vec(e6.x(), scene.isRightHanded() ? -e6.y() : e6.y(), e6.z());
		  // Scale to fit the screen mouse displacement
			switch ( scene.camera().type() ) {
			case PERSPECTIVE:
				trans.multiply(2.0f * (float) Math.tan(scene.camera().fieldOfView() / 2.0f)
						            * Math.abs((scene.camera().frame().coordinatesOf(position())).vec[2] * scene.camera().frame().magnitude().z())
								        //* Math.abs((scene.camera().frame().coordinatesOf(position())).vec[2])						            
								        / scene.camera().screenHeight());
				break;
				case ORTHOGRAPHIC: {
					float[] wh = scene.camera().getBoundaryWidthHeight();
					trans.vec[0] *= 2.0 * wh[0] / scene.camera().screenWidth();
					trans.vec[1] *= 2.0 * wh[1] / scene.camera().screenHeight();
					break;
				}
			}
		  // same as:
			trans = scene.camera().frame().orientation().rotate(Vec.multiply(trans, translationSensitivity()));
			// but takes into account scaling
			//trans = scene.camera().frame().inverseTransformOf(Vector3D.mult(trans, translationSensitivity()));
			// And then down to frame
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
	    // B. Rotate the iFrame
	    q = new Quat();
	    trans = scene.camera().projectedCoordinatesOf(position());	    
	    if(e6.isAbsolute())
	    	q.fromEulerAngles(e6.roll(), e6.pitch(), -e6.yaw());
	    else
	    	q.fromEulerAngles(e6.drx(), e6.dry(), -e6.drz());
	    trans.set(-q.x(), -q.y(), -q.z());
	    trans = scene.camera().frame().orientation().rotate(trans);
	    trans = transformOf(trans, false);
	    q.setX(trans.x());
	    q.setY(trans.y());
	    q.setZ(trans.z());
	    rotate(q);
			break;
		case SCALE:
			float delta;
			if( e1 instanceof GenericDOF1Event ) //its a wheel wheel :P
				delta = e1.x() * wheelSensitivity();
			else
				if( e1.isAbsolute() )
				  delta = e1.x();
				else
					delta = e1.dx();	
			if(delta >= 0)
				scale(1 + Math.abs(delta) / (float) scene.height());
			else {
				//inverseScale(1 + Math.abs(delta) / (float) scene.height());
				float s = 1 + Math.abs(delta) / (float) scene.height();
				setScaling(Vec.divide(scaling(), new Vec(s,s,s)));
				}
			break;
		case CENTER_FRAME:
			projectOnLine(scene.camera().position(), scene.camera().viewDirection());
			break;
		case ALIGN_FRAME:
			alignWithFrame(scene.camera().frame());
			break;
		default:
			AbstractScene.showOnlyEyeWarning(a);
			break;
		}
	}

	public boolean isFlipped() {
		return ( scene.isRightHanded() && !isInverted() ) || ( scene.isLeftHanded() && isInverted() );
	}
	
	/**
	 * Returns a Quaternion computed according to the mouse motion. Mouse positions
	 * are projected on a deformed ball, centered on ({@code cx}, {@code cy}).
	 */
	protected Quat deformedBallQuaternion(DOF2Event event, float cx, float cy, Camera camera) {
		//TODO absolute events!?
		float x = event.x();
		float y = event.y();
		float prevX = event.prevX();
		float prevY = event.prevY();
		// Points on the deformed ball		
    float px = rotationSensitivity() *                         ((int)prevX - cx)                       / camera.screenWidth();
    float py = rotationSensitivity() * (scene.isLeftHanded() ? ((int)prevY - cy) : ( cy - (int)prevY)) / camera.screenHeight();
    float dx = rotationSensitivity() *                         (x - cx)             / camera.screenWidth();
    float dy = rotationSensitivity() * (scene.isLeftHanded() ? (y - cy) : (cy - y)) / camera.screenHeight();    

		Vec p1 = new Vec(px, py, projectOnBall(px, py));
		Vec p2 = new Vec(dx, dy, projectOnBall(dx, dy));
		// Approximation of rotation angle Should be divided by the projectOnBall size, but it is 1.0
		Vec axis = p2.cross(p1);
		float angle = 2.0f * (float) Math.asin((float) Math.sqrt(axis.squaredNorm() / p1.squaredNorm() / p2.squaredNorm()));			
		return new Quat(axis, angle);
	}
	
	protected final Quat iFrameQuaternion(Quat rot, Camera camera) {
		Vec trans = new Vec();		
		trans = rot.axis();
		trans = camera.frame().orientation().rotate(trans);
		trans = transformOf(trans);
		//trans = transformOfFrom(trans, camera.frame());
		
		Vec res = new Vec(trans);			
		// perform conversion			
		if (scaling().x() < 0 )	res.setX(-trans.x());
		if (scaling().y() < 0 )	res.setY(-trans.y());
		if (scaling().z() < 0 )	res.setZ(-trans.z());
		
		return new Quat(res, isInverted() ? rot.angle() : -rot.angle());						
	}	

	/**
	 * Returns "pseudo-distance" from (x,y) to ball of radius size. For a point
	 * inside the ball, it is proportional to the euclidean distance to the ball.
	 * For a point outside the ball, it is proportional to the inverse of this
	 * distance (tends to zero) on the ball, the function is continuous.
	 */
	protected static float projectOnBall(float x, float y) {
		// If you change the size value, change angle computation in deformedBallQuaternion().
		float size = 1.0f;
		float size2 = size * size;
		float size_limit = size2 * 0.5f;

		float d = x * x + y * y;
		return d < size_limit ? (float) Math.sqrt(size2 - d) : size_limit	/ (float) Math.sqrt(d);
	}
	
	/**
	 * Returns the fly speed, expressed in processing scene units.
	 * <p>
	 * It corresponds to the incremental displacement that is periodically applied
	 * to the InteractiveDrivableFrame position when a
	 * MOVE_FORWARD or MOVE_BACKWARD action is proceeded.
	 * <p>
	 * <b>Attention:</b> When the InteractiveFrame is set as the
	 * {@link remixlab.dandelion.core.Eye#frame()} or when it is set as the
	 * {@link remixlab.dandelion.core.AbstractScene#avatar()} (which indeed is an instance of
	 * the InteractiveAvatarFrame class), this value is set according to the
	 * {@link remixlab.dandelion.core.AbstractScene#radius()} by
	 * {@link remixlab.dandelion.core.AbstractScene#setRadius(float)}.
	 */
	public float flySpeed() {
		return flySpd;
	}

	/**
	 * Sets the {@link #flySpeed()}, defined in processing scene units.
	 * <p>
	 * Default value is 0.0, but it is modified according to the
	 * {@link remixlab.dandelion.core.AbstractScene#radius()} when the InteractiveDrivableFrame
	 * is set as the {@link remixlab.dandelion.core.Camera#frame()} (which indeed is an
	 * instance of the InteractiveCameraFrame class) or when the
	 * InteractiveDrivableFrame is set as the
	 * {@link remixlab.dandelion.core.AbstractScene#avatar()} (which indeed is an instance of
	 * the InteractiveAvatarFrame class).
	 */
	public void setFlySpeed(float speed) {
		flySpd = speed;
	}

	/**
	 * Returns the up vector used in fly mode, expressed in the world coordinate
	 * system.
	 * <p>
	 * Fly mode corresponds to the MOVE_FORWARD and MOVE_BACKWARD action
	 * bindings. In these modes, horizontal displacements of the mouse rotate the
	 * InteractiveDrivableFrame around this vector. Vertical displacements rotate
	 * always around the frame {@code X} axis.
	 * <p>
	 * Default value is (0,1,0), but it is updated by the Camera when set as its
	 * {@link remixlab.dandelion.core.Camera#frame()}.
	 * {@link remixlab.dandelion.core.Camera#setOrientation(Orientable)} and
	 * {@link remixlab.dandelion.core.Camera#setUpVector(Vec)} modify this value and
	 * should be used instead.
	 */
	public Vec flyUpVector() {
		return flyUpVec;
	}

	/**
	 * Sets the {@link #flyUpVector()}, defined in the world coordinate system.
	 * <p>
	 * Default value is (0,1,0), but it is updated by the Camera when set as its
	 * {@link remixlab.dandelion.core.Camera#frame()}. Use
	 * {@link remixlab.dandelion.core.Camera#setUpVector(Vec)} instead in that case.
	 */
	//TODO: decide if this should go or not
	public void setFlyUpVector(Vec up) {
		flyUpVec = up;
	}

	/**
	 * This method will be called by the Camera when its orientation is changed,
	 * so that the {@link #flyUpVector()} (private) is changed accordingly. You
	 * should not need to call this method.
	 */
	public final void updateFlyUpVector() {
		//flyUpVec = inverseTransformOf(new Vector3D(0.0f, 1.0f, 0.0f));
		flyUpVec = inverseTransformOf(new Vec(0.0f, 1.0f, 0.0f), false);
	}

	/**
	 * Returns a Quaternion that is a rotation around current camera Y,
	 * proportional to the horizontal mouse position.
	 */
	protected final Quat turnQuaternion(DOF1Event event, Camera camera) {
		float deltaX;
		if( event instanceof GenericDOF1Event ) //it's a wheel then :P
			deltaX = event.x() * wheelSensitivity();
		else
			deltaX = event.isAbsolute() ? event.x() : event.dx();
		return new Quat(new Vec(0.0f, 1.0f, 0.0f), rotationSensitivity()	* (-deltaX) / camera.screenWidth());
	}

	/**
	 * Returns a Quaternion that is the composition of two rotations, inferred
	 * from the mouse pitch (X axis) and yaw ({@link #flyUpVector()} axis).
	 */
	protected final Quat pitchYawQuaternion(DOF2Event event, Camera camera) {
		float deltaX = event.isAbsolute() ? event.x() : event.dx();
		float deltaY = event.isAbsolute() ? event.y() : event.dy();
			
		if( scene.isRightHanded() )
			deltaY = -deltaY;
		
		Quat rotX = new Quat(new Vec(1.0f, 0.0f, 0.0f), rotationSensitivity() * deltaY / camera.screenHeight());
		//Quaternion rotY = new Quaternion(transformOf(flyUpVector()), rotationSensitivity() * ((int)prevPos.x - x) / camera.screenWidth());	
		Quat rotY = new Quat(transformOf(flyUpVector(), false), rotationSensitivity() * (-deltaX) / camera.screenWidth());
		return Quat.multiply(rotY, rotX);
	}
	
	/**
	 * Return 1 if mouse motion was started horizontally and -1 if it was more
	 * vertical. Returns 0 if this could not be determined yet (perfect diagonal
	 * motion, rare).
	 */
	protected int originalDirection(DOF2Event event) {
		if (!dirIsFixed) {
			Point delta;
			if( event.isAbsolute() )
				delta = new Point(event.x(), event.y());
			else
				delta = new Point(event.dx(), event.dy());
			dirIsFixed = Math.abs(delta.x()) != Math.abs(delta.y());
			horiz = Math.abs(delta.x()) > Math.abs(delta.y());
		}

		if (dirIsFixed)
			if (horiz)
				return 1;
			else
				return -1;
		else
			return 0;
	}
}
