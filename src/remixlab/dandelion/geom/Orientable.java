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

import remixlab.util.Copyable;

public interface Orientable extends Copyable {
	@Override
	public Orientable get();

	public float angle();

	public void negate();

	public void compose(Orientable o);

	public Orientable inverse();

	public Vec rotate(Vec v);

	public Vec inverseRotate(Vec v);

	public Mat matrix();

	public Mat inverseMatrix();

	public void fromMatrix(Mat glMatrix);

	public void fromRotatedBasis(Vec X, Vec Y, Vec Z);

	public float normalize();

	public void fromTo(Vec from, Vec to);

	public void print();
}
