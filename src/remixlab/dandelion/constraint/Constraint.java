/*******************************************************************************
 * dandelion (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.dandelion.constraint;

import remixlab.dandelion.core.Frame;
import remixlab.dandelion.geom.*;

/**
 * An interface class for Frame constraints.
 * <p>
 * This class defines the interface for the constraint that can be applied to a
 * Frame to limit its motion. Use
 * {@link remixlab.dandelion.core.Frame#setConstraint(Constraint)} to associate a
 * Constraint to a Frame (default is a {@code null}
 * {@link remixlab.dandelion.core.Frame#constraint()}.
 */
public abstract class Constraint {
	protected boolean [] sclConstr = new boolean[3];
	
	/**
	 * Filters the translation applied to the Frame. This default implementation
	 * is empty (no filtering).
	 * <p>
	 * Overload this method in your own Constraint class to define a new
	 * translation constraint. {@code frame} is the Frame to which is applied the
	 * translation. You should refrain from directly changing its value in the
	 * constraint. Use its {@link remixlab.dandelion.core.Frame#position()} and update
	 * the translation accordingly instead.
	 * <p>
	 * {@code translation} is expressed in the local Frame coordinate system. Use
	 * {@link remixlab.dandelion.core.Frame#inverseTransformOf(Vec)} to express it
	 * in the world coordinate system if needed.
	 */
	public Vec constrainTranslation(Vec translation, Frame frame) {
		return translation.get();
	}

	/**
	 * Filters the rotation applied to the {@code frame}. This default
	 * implementation is empty (no filtering).
	 * <p>
	 * Overload this method in your own Constraint class to define a new rotation
	 * constraint. See {@link #constrainTranslation(Vec, Frame)} for details.
	 * <p>
	 * Use {@link remixlab.dandelion.core.Frame#inverseTransformOf(Vec)} on the
	 * {@code rotation} {@link remixlab.dandelion.geom.Quat#axis()} to express
	 * {@code rotation} in the world coordinate system if needed.
	 */
	public Orientable constrainRotation(Orientable rotation, Frame frame) {
		return rotation.get();
	}
	
	public boolean[] scalingConstraint() {
		return sclConstr;
	}

	public void setScalingConstraint(boolean [] c) {
		if( c.length == 2 || c.length == 3) for(int i=0; i< c.length; i++) sclConstr[i] = c[i];
	}
	
	public void setScalingConstraint(boolean a, boolean b) {
		sclConstr[0] = a; sclConstr[1] = b;;
	}
	
	public void setScalingConstraint(boolean a, boolean b, boolean c) {
		sclConstr[0] = a; sclConstr[1] = b; sclConstr[2] = c;
	}
	
	/**
	 * Filters the scaling applied to the Frame.
	 */	
	public Vec constrainScaling(Vec scaling, Frame frame) {
		if(frame.is2D())
			return new Vec(sclConstr[0] ? 1 : scaling.x(),
							       sclConstr[1] ? 1 : scaling.y());
		else
			return new Vec(sclConstr[0] ? 1 : scaling.x(),
				             sclConstr[1] ? 1 : scaling.y(),
				             sclConstr[2] ? 1 : scaling.z());
	}
}
