/*******************************************************************************
 * dandelion_tree (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package remixlab.dandelion.core;

import remixlab.dandelion.geom.*;

/**
 * Various matrix operations dandelion should support either through a third-party implementation or the
 */
public interface MatrixHelper {
	// public void setScene(AbstractScene scn);

	public AbstractScene scene();

	public void bind();

	/**
	 * Computes the world coordinates of an screen object so that drawing can be done directly with 2D screen coordinates.
	 * <p>
	 * All screen drawing should be enclosed between {@link #beginScreenDrawing()} and {@link #endScreenDrawing()}. Then
	 * you can just begin drawing your screen shapes (defined between {@code PApplet.beginShape()} and
	 * {@code PApplet.endShape()}).
	 * <p>
	 * <b>Note:</b> To specify a {@code (x,y)} vertex screen coordinate you should first call
	 * {@code Vector3D p = coords(new Point(x, y))} then do your drawing as {@code vertex(p.x, p.y, p.z)}.
	 * <p>
	 * <b>Attention:</b> If you want your screen drawing to appear on top of your 3d scene then draw first all your 3d
	 * before doing any call to a {@link #beginScreenDrawing()} and {@link #endScreenDrawing()} pair.
	 * 
	 * @see #endScreenDrawing()
	 */
	public void beginScreenDrawing();

	/**
	 * Ends screen drawing. See {@link #beginScreenDrawing()} for details.
	 * 
	 * @see #beginScreenDrawing()
	 */
	public void endScreenDrawing();

	// matrix
	/**
	 * Push a copy of the modelview matrix onto the stack.
	 */
	public void pushModelView();

	/**
	 * Replace the current modelview matrix with the top of the stack.
	 */
	public void popModelView();

	/**
	 * Push a copy of the projection matrix onto the stack.
	 */
	public void pushProjection();

	/**
	 * Replace the current projection matrix with the top of the stack.
	 */
	public void popProjection();

	/**
	 * Translate in X and Y.
	 */
	public void translate(float tx, float ty);

	/**
	 * Translate in X, Y, and Z.
	 */
	public void translate(float tx, float ty, float tz);

	/**
	 * Two dimensional rotation.
	 * 
	 * Same as rotateZ (this is identical to a 3D rotation along the z-axis) but included for clarity. It'd be weird for
	 * people drawing 2D graphics to be using rotateZ. And they might kick our a-- for the confusion.
	 * 
	 * <A HREF="http://www.xkcd.com/c184.html">Additional background</A>.
	 */
	public void rotate(float angle);

	public void rotateX(float angle);

	public void rotateY(float angle);

	/**
	 * Rotate around the Z axis.
	 * 
	 * The functions rotate() and rotateZ() are identical, it's just that it make sense to have rotate() and then
	 * rotateX() and rotateY() when using 3D; nor does it make sense to use a function called rotateZ() if you're only
	 * doing things in 2D. so we just decided to have them both be the same.
	 */
	public void rotateZ(float angle);

	/**
	 * Rotate about a vector in space. Same as the glRotatef() function.
	 */
	public void rotate(float angle, float vx, float vy, float vz);

	/**
	 * Scale in all dimensions.
	 */
	public void scale(float s);

	/**
	 * Scale in X and Y. Equivalent to scale(sx, sy, 1).
	 * 
	 * Not recommended for use in 3D, because the z-dimension is just scaled by 1, since there's no way to know what else
	 * to scale it by.
	 */
	public void scale(float sx, float sy);

	/**
	 * Scale in X, Y, and Z.
	 */
	public void scale(float x, float y, float z);

	/**
	 * Set the current modelview matrix to identity.
	 */
	public void resetModelView();

	/**
	 * Set the current projection matrix to identity.
	 */
	public void resetProjection();

	// public void loadMatrix(Matrix3D source);
	// public void loadProjection(Matrix3D source);
	// public void multiplyMatrix(Matrix3D source);
	// public void multiplyProjection(Matrix3D source);
	public void applyModelView(Mat source);

	public void applyProjection(Mat source);

	/**
	 * Apply a 4x4 modelview matrix.
	 */
	public void applyModelViewRowMajorOrder(float n00, float n01, float n02, float n03,
			float n10, float n11, float n12, float n13,
			float n20, float n21, float n22, float n23,
			float n30, float n31, float n32, float n33);

	/**
	 * Apply a 4x4 projection matrix.
	 */
	public void applyProjectionRowMajorOrder(float n00, float n01, float n02, float n03,
			float n10, float n11, float n12, float n13,
			float n20, float n21, float n22, float n23,
			float n30, float n31, float n32, float n33);

	// public void frustum(float left, float right, float bottom, float top, float znear, float zfar);

	public Mat modelView();

	/**
	 * Copy the current modelview matrix into the specified target. Pass in null to create a new matrix.
	 */
	public Mat getModelView(Mat target);

	public Mat projection();

	/**
	 * Copy the current projection matrix into the specified target. Pass in null to create a new matrix.
	 */
	public Mat getProjection(Mat target);

	/**
	 * Set the current modelview matrix to the contents of another.
	 */
	public void setModelView(Mat source);

	/**
	 * Print the current modelview matrix.
	 */
	public void printModelView();

	/**
	 * Set the current projection matrix to the contents of another.
	 */
	public void setProjection(Mat source);

	/**
	 * Print the current projection matrix.
	 */
	public void printProjection();

	public void loadProjection();

	public void loadModelView();

	Mat projectionView();

	Mat projectionViewInverse();

	/**
	 * Returns {@code true} if {@code P x M} and {@code inv (P x M)} are being cached, and {@code false} otherwise.
	 * 
	 * @see #cacheProjectionViewInverse()
	 * @see #optimizeUnprojectCache(boolean)
	 */
	boolean unprojectCacheIsOptimized();

	void cacheProjectionViewInverse();

	/**
	 * Cache {@code inv (P x M)} (and also {@code (P x M)} ) so that
	 * {@code project(float, float, float, Matrx3D, Matrx3D, int[], float[])} (and also
	 * {@code unproject(float, float, float, Matrx3D, Matrx3D, int[], float[])}) is optimised.
	 * 
	 * @see #unprojectCacheIsOptimized()
	 * @see #cacheProjectionViewInverse()
	 */
	void optimizeUnprojectCache(boolean optimise);
}
