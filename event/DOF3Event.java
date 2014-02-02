/*******************************************************************************
 * TerseHandling (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.tersehandling.event;

import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;
import remixlab.util.Util;

public class DOF3Event extends MotionEvent {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
				.append(x)
				.append(dx)
				.append(y)
				.append(dy)
				.append(z)
				.append(dz)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		DOF3Event other = (DOF3Event) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(x, other.x)
				.append(dx, other.dx)
				.append(y, other.y)
				.append(dy, other.dy)
				.append(z, other.z)
				.append(dz, other.dz)
				.isEquals();
	}

	protected Float x, dx;
	protected Float y, dy;
	protected Float z, dz;

	public DOF3Event(float x, float y, float z, int modifiers, int button) {
		super(modifiers, button);
		this.x = x;
		this.dx = 0f;
		this.y = y;
		this.dy = 0f;
		this.z = z;
		this.dz = 0f;
	}

	public DOF3Event(DOF3Event prevEvent, float x, float y, float z, int modifiers, int button) {
		this(x, y, z, modifiers, button);
		setPreviousEvent(prevEvent);
		/**
		 * if(prevEvent!=null) { distance = Util.distance(x, y, z,
		 * prevEvent.getX(), prevEvent.getY(), prevEvent.getZ()); if(
		 * sameSequence(prevEvent) ) { this.dx = this.getX() - prevEvent.getX();
		 * this.dy = this.getY() - prevEvent.getY(); this.dz = this.getZ() -
		 * prevEvent.getZ(); this.action = prevEvent.getAction(); } }
		 */
	}

	// ready to be enqueued
	public DOF3Event(float x, float y, float z) {
		super();
		this.x = x;
		this.dx = 0f;
		this.y = y;
		this.dy = 0f;
		this.z = z;
		this.dz = 0f;
		this.button = TH_NOBUTTON;
	}

	// idem
	public DOF3Event(DOF3Event prevEvent, float x, float y, float z) {
		super();
		this.x = x;
		this.dx = 0f;
		this.y = y;
		this.dy = 0f;
		this.z = z;
		this.dz = 0f;
		this.button = TH_NOBUTTON;
		setPreviousEvent(prevEvent);
	}

	protected DOF3Event(DOF3Event other) {
		super(other);
		this.x = new Float(other.x);
		this.dx = new Float(other.dx);
		this.y = new Float(other.y);
		this.dy = new Float(other.dy);
		this.z = new Float(other.z);
		this.dz = new Float(other.z);
	}

	@Override
	public DOF3Event get() {
		return new DOF3Event(this);
	}

	@Override
	public void setPreviousEvent(MotionEvent prevEvent) {
		super.setPreviousEvent(prevEvent);
		if (prevEvent != null)
			if (prevEvent instanceof DOF3Event) {
				rel = true;
				this.dx = this.x() - ((DOF3Event) prevEvent).x();
				this.dy = this.y() - ((DOF3Event) prevEvent).y();
				this.dz = this.z() - ((DOF3Event) prevEvent).z();
				distance = Util.distance(x, y, z,
						((DOF3Event) prevEvent).x(),
						((DOF3Event) prevEvent).y(),
						((DOF3Event) prevEvent).z());
				delay = this.timestamp() - prevEvent.timestamp();
				if (delay == 0)
					speed = distance;
				else
					speed = distance / (float) delay;
			} else {
				this.dx = 0f;
				this.dy = 0f;
				this.dz = 0f;
				delay = 0;
				speed = 0;
				distance = 0;
			}
	}

	public float x() {
		return x;
	}

	public float dx() {
		return dx;
	}

	public float prevX() {
		return x() - dx();
	}

	public float y() {
		return y;
	}

	public float dy() {
		return dy;
	}

	public float prevY() {
		return y() - dy();
	}

	public float z() {
		return z;
	}

	public float dz() {
		return dz;
	}

	public float prevZ() {
		return z() - dz();
	}

	@Override
	public void modulate(float[] sens) {
		if (sens != null)
			if (sens.length >= 3 && this.isAbsolute()) {
				x = x * sens[0];
				y = y * sens[1];
				z = z * sens[2];
			}
	}

	@Override
	public boolean isNull() {
		if (isRelative() && Util.zero(dx()) && Util.zero(dy()) && Util.zero(dz()))
			return true;
		if (isAbsolute() && Util.zero(x()) && Util.zero(y()) && Util.zero(z()))
			return true;
		return false;
	}

	public DOF2Event dof2Event() {
		DOF2Event pe2;
		DOF2Event e2;
		if (isRelative()) {
			pe2 = new DOF2Event(prevX(), prevY(), modifiers(),	button());
			e2 = new DOF2Event(pe2, x(), y(), modifiers(), button());
		} else {
			e2 = new DOF2Event(x(), y(), modifiers(), button());
		}
		e2.timestamp = this.timestamp();
		e2.delay = this.delay();
		e2.speed = this.speed();
		e2.distance = this.distance();
		return e2;
	}
}
