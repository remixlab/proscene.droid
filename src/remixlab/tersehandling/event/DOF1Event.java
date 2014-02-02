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

public class DOF1Event extends MotionEvent {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
				.append(x)
				.append(dx)
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

		DOF1Event other = (DOF1Event) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(x, other.x)
				.append(dx, other.dx)
				.isEquals();
	}

	protected Float x, dx;

	public DOF1Event(float x, int modifiers, int button) {
		super(modifiers, button);
		this.x = x;
		this.dx = 0f;
	}

	public DOF1Event(DOF1Event prevEvent, float x, int modifiers, int button) {
		this(x, modifiers, button);
		setPreviousEvent(prevEvent);
		/**
		 * if(prevEvent!=null) { distance = this.getX() - prevEvent.getX(); if
		 * (sameSequence(prevEvent)) { this.action = prevEvent.getAction();
		 * this.dx = this.getX() - prevEvent.getX(); } }
		 */
	}

	// ready to be enqueued
	public DOF1Event(float x) {
		super();
		this.x = x;
		this.dx = 0f;
		this.button = TH_NOBUTTON;
	}

	// idem
	public DOF1Event(DOF1Event prevEvent, float x) {
		super();
		this.x = x;
		this.dx = 0f;
		this.button = TH_NOBUTTON;
		setPreviousEvent(prevEvent);
	}

	// ---

	protected DOF1Event(DOF1Event other) {
		super(other);
		this.x = new Float(other.x);
		this.dx = new Float(other.dx);
	}

	@Override
	public DOF1Event get() {
		return new DOF1Event(this);
	}

	@Override
	public void setPreviousEvent(MotionEvent prevEvent) {
		if (prevEvent != null)
			if (prevEvent instanceof DOF1Event) {
				rel = true;
				this.dx = this.x() - ((DOF1Event) prevEvent).x();
				distance = this.x() - ((DOF1Event) prevEvent).x();
				delay = this.timestamp() - prevEvent.timestamp();
				if (delay == 0)
					speed = distance;
				else
					speed = distance / (float) delay;
			} else {
				this.dx = 0f;
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

	@Override
	public void modulate(float[] sens) {
		if (sens != null)
			if (sens.length >= 1 && this.isAbsolute())
				x = x * sens[0];
	}

	@Override
	public boolean isNull() {
		if (isRelative() && Util.zero(dx()))
			return true;
		if (isAbsolute() && Util.zero(x()))
			return true;
		return false;
	}
}
