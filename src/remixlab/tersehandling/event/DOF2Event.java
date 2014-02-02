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

public class DOF2Event extends MotionEvent {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
				.appendSuper(super.hashCode())
				.append(x)
				.append(dx)
				.append(y)
				.append(dy)
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

		DOF2Event other = (DOF2Event) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(x, other.x)
				.append(dx, other.dx)
				.append(y, other.y)
				.append(dy, other.dy)
				.isEquals();
	}

	protected Float x, dx;
	protected Float y, dy;

	public DOF2Event(float x, float y, int modifiers, int button) {
		super(modifiers, button);
		this.x = x;
		this.dx = 0f;
		this.y = y;
		this.dy = 0f;
	}

	public DOF2Event(DOF2Event prevEvent, float x, float y, int modifiers, int button) {
		this(x, y, modifiers, button);
		setPreviousEvent(prevEvent);

		/**
		 * if(prevEvent!=null) { distance = Util.distance(x, y,
		 * prevEvent.getX(), prevEvent.getY()); if( sameSequence(prevEvent) ) {
		 * this.dx = this.getX() - prevEvent.getX(); this.dy = this.getY() -
		 * prevEvent.getY(); this.action = prevEvent.getAction(); } } //
		 */

		/**
		 * //TODO debug if( sameSequence(prevEvent) ) { this.dx = this.getX() -
		 * prevEvent.getX(); this.dy = this.getY() - prevEvent.getY();
		 * this.action = prevEvent.getAction();
		 * System.out.println("Current event: x: " + getX() + " y: " + getY());
		 * System.out.println("Prev event: x: " + getPrevX() + " y: " +
		 * getPrevY()); //System.out.println("Distance: " + distance());
		 * //System.out.println("Delay: " + delay());
		 * //System.out.println("Speed: " + speed()); } else {
		 * System.out.println("different sequence!"); } //
		 */
	}

	// ready to be enqueued
	public DOF2Event(float x, float y) {
		super();
		this.x = x;
		this.dx = 0f;
		this.y = y;
		this.dy = 0f;
		this.button = TH_NOBUTTON;
	}

	// idem
	public DOF2Event(DOF2Event prevEvent, float x, float y) {
		super();
		this.x = x;
		this.dx = 0f;
		this.y = y;
		this.dy = 0f;
		this.button = TH_NOBUTTON;
		setPreviousEvent(prevEvent);
	}

	protected DOF2Event(DOF2Event other) {
		super(other);
		this.x = new Float(other.x);
		this.dx = new Float(other.dx);
		this.y = new Float(other.y);
		this.dy = new Float(other.dy);
	}

	@Override
	public DOF2Event get() {
		return new DOF2Event(this);
	}

	@Override
	public void setPreviousEvent(MotionEvent prevEvent) {
		if (prevEvent != null)
			if (prevEvent instanceof DOF2Event) {
				rel = true;
				this.dx = this.x() - ((DOF2Event) prevEvent).x();
				this.dy = this.y() - ((DOF2Event) prevEvent).y();
				distance = Util.distance(x, y, ((DOF2Event) prevEvent).x(), ((DOF2Event) prevEvent).y());
				delay = this.timestamp() - prevEvent.timestamp();
				if (delay == 0)
					speed = distance;
				else
					speed = distance / (float) delay;
			} else {
				this.dx = 0f;
				this.dy = 0f;
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

	@Override
	public void modulate(float[] sens) {
		if (sens != null)
			if (sens.length >= 2 && this.isAbsolute()) {
				x = x * sens[0];
				y = y * sens[1];
			}
	}

	@Override
	public boolean isNull() {
		if (isRelative() && Util.zero(dx()) && Util.zero(dy()))
			return true;
		if (isAbsolute() && Util.zero(x()) && Util.zero(y()))
			return true;
		return false;
	}

	public DOF1Event dof1Event() {
		return dof1Event(true);
	}

	public DOF1Event dof1Event(boolean fromX) {
		DOF1Event pe1;
		DOF1Event e1;
		if (fromX) {
			if (isRelative()) {
				pe1 = new DOF1Event(prevX(), modifiers(), button());				
				e1 = new DOF1Event(pe1, x(), modifiers(), button());
			} else {
				e1 = new DOF1Event(x(), modifiers(), button());
			}
		} else {
			if (isRelative()) {
				pe1 = new DOF1Event(prevY(), modifiers(), button());
				e1 = new DOF1Event(pe1, y(), modifiers(), button());
			} else {
				e1 = new DOF1Event(y(), modifiers(), button());
			}
		}
		e1.timestamp = this.timestamp();
		e1.delay = this.delay();
		e1.speed = this.speed();
		e1.distance = this.distance();
		return e1;
	}
}
