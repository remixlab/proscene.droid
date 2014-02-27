/*******************************************************************************
 * dandelion (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.dandelion.geom;

import remixlab.dandelion.core.Constants;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;
import remixlab.util.Util;

public class Rot implements Constants, Orientable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
						append(this.angle).
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

		Rot other = (Rot) obj;
		return new EqualsBuilder()
						.append(this.angle, other.angle)
						.isEquals();
	}

	protected float angle;

	public Rot() {
		angle = 0;
	}

	public Rot(float a) {
		angle = a;
		normalize();
	}

	public Rot(Vec from, Vec to) {
		fromTo(from, to);
	}

	public Rot(Point center, Point prev, Point curr) {
		Vec from = new Vec(prev.x - center.x, prev.y - center.y);
		Vec to = new Vec(curr.x - center.x, curr.y - center.y);
		fromTo(from, to);
	}

	protected Rot(Rot a1) {
		this.angle = a1.angle();
		normalize();
	}

	@Override
	public Rot get() {
		return new Rot(this);
	}

	@Override
	public float angle() {
		return angle;
	}

	@Override
	public void negate() {
		angle = -angle;
	}

	@Override
	public Orientable inverse() {
		return new Rot(-angle());
	}

	@Override
	public Vec rotate(Vec v) {
		float cosB = (float) Math.cos(angle());
		float sinB = (float) Math.sin(angle());
		return new Vec(((v.x() * cosB) - (v.y() * sinB)), ((v.x() * sinB) + (v.y() * cosB)));
	}

	@Override
	public Vec inverseRotate(Vec v) {
		float cosB = (float) Math.cos(-angle());
		float sinB = (float) Math.sin(-angle());
		return new Vec(((v.x() * cosB) - (v.y() * sinB)), ((v.x() * sinB) + (v.y() * cosB)));
	}

	@Override
	public Mat matrix() {
		float cosB = (float) Math.cos((double) angle());
		float sinB = (float) Math.sin((double) angle());

		return new Mat(cosB, sinB, 0, 0,
						-sinB, cosB, 0, 0,
						0, 0, 1, 0,
						0, 0, 0, 1);
	}

	@Override
	public Mat inverseMatrix() {
		float cosB = (float) Math.cos(-angle());
		float sinB = (float) Math.sin(-angle());

		return new Mat(cosB, sinB, 0, 0,
						-sinB, cosB, 0, 0,
						0, 0, 1, 0,
						0, 0, 0, 1);
	}

	@Override
	public void fromMatrix(Mat glMatrix) {
		// "If both sine and cosine of the angle are already known, ATAN2(sin, cos) gives the angle"
		// http://www.firebirdsql.org/refdocs/langrefupd21-intfunc-atan2.html
		angle = (float) Math.atan2(glMatrix.m10(), glMatrix.m00());
	}

	@Override
	// TODO needs testing
	public void fromRotatedBasis(Vec X, Vec Y, Vec Z) {
		// "If both sine and cosine of the angle are already known, ATAN2(sin, cos) gives the angle"
		// http://www.firebirdsql.org/refdocs/langrefupd21-intfunc-atan2.html
		angle = (float) Math.atan2(X.vec[1], X.vec[0]);
	}

	@Override
	public final void compose(Orientable r) {
		float res = angle + r.angle();
		angle = angle + r.angle();
		angle = res;
		this.normalize();
	}

	public final static Orientable compose(Orientable r1, Orientable r2) {
		return new Rot(r1.angle() + r2.angle());
	}

	public float normalize(boolean onlypos) {
		if (onlypos) {// 0 <-> two_pi
			if (Math.abs(angle) > TWO_PI) {
				angle = angle % TWO_PI;
			}
			if (angle < 0)
				angle = TWO_PI + angle;
		}
		else {// -pi <-> pi
			if (Math.abs(angle) > PI)
				if (angle >= 0)
					angle = (angle % PI) - PI;
				else
					angle = PI - (angle % PI);
		}
		return angle;
	}

	@Override
	public float normalize() {
		// return normalize(false);
		return angle; // dummy
	}

	@Override
	public void fromTo(Vec from, Vec to) {
		// perp dot product. See:
		// 1. http://stackoverflow.com/questions/2150050/finding-signed-angle-between-vectors
		// 2. http://mathworld.wolfram.com/PerpDotProduct.html
		float fromNorm = from.magnitude();
		float toNorm = to.magnitude();
		if ((Util.zero(fromNorm)) || (Util.zero(toNorm)))
			angle = 0;
		else
			// angle =(float) Math.acos( (double)Vector3D.dot(from, to) / ( fromNorm * toNorm ));
			angle = (float) Math.atan2(from.x() * to.y() - from.y() * to.x(), from.x() * to.x() + from.y() * to.y());
	}

	@Override
	public void print() {
		System.out.println(angle());
	}
}
