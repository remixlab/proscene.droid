/*******************************************************************************
 * dandelion (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.dandelion.helper;

import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

public class MatrixStackHelper extends MatrixHelper implements Constants {
	private static final int MATRIX_STACK_DEPTH = 32;

	private static final String ERROR_PUSHMATRIX_OVERFLOW = "Too many calls to pushModelView().";
	private static final String ERROR_PUSHMATRIX_UNDERFLOW = "Too many calls to popModelView(), and not enough to pushModelView().";

	float[][] matrixStack = new float[MATRIX_STACK_DEPTH][16];
	int matrixStackDepth;

	float[][] pmatrixStack = new float[MATRIX_STACK_DEPTH][16];
	int pmatrixStackDepth;

	Mat projection, modelview;

	public MatrixStackHelper(AbstractScene scn) {
		super(scn);
		modelview = new Mat();
		projection = new Mat();
	}
	
	@Override
	public void pushModelView() {
		if (matrixStackDepth == MATRIX_STACK_DEPTH) {
			throw new RuntimeException(ERROR_PUSHMATRIX_OVERFLOW);
		}
		modelview.get(matrixStack[matrixStackDepth]);
		matrixStackDepth++;
	}

	@Override
	public void popModelView() {
		if (matrixStackDepth == 0) {
			throw new RuntimeException(ERROR_PUSHMATRIX_UNDERFLOW);
		}
		matrixStackDepth--;
		modelview.set(matrixStack[matrixStackDepth]);
	}

	@Override
	public void resetModelView() {
		modelview.reset();
	}

	@Override
	public Mat modelView() {
		return modelview.get();
	}

	@Override
	public Mat getModelView(Mat target) {
		if (target == null)
			target = new Mat();
		target.set(modelview);
		return target;
	}

	@Override
	public void printModelView() {
		modelview.print();
	}
	
	@Override
	public void setModelView(Mat source) {
		resetModelView();
		applyModelView(source);
	}
	
	/**
	 * Apply a 4x4 transformation matrix. Same as glMultMatrix().
	 */
	@Override
	public void applyModelView(Mat source) {
		modelview.apply(source);
	}

	/**
	 * 16 consecutive values that are used as the elements of a 4 x 4 column-major
	 * matrix.
	 */
	public void applyModelView(float[] source) {
		modelview.apply(source);
	}
	
	/**
	 * 16 consecutive values that are used as the elements of a 4 x 4 column-major
	 * matrix.
	 */
	public void applyModelView(float m0, float m1, float m2, float m3,
													float m4,	float m5, float m6, float m7,
													float m8, float m9, float m10, float m11,
													float m12, float m13, float m14, float m15) {
		modelview.apply(m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13,	m14, m15);
	}

	// TODO check this
	@Override
	public void applyModelViewRowMajorOrder(float n00, float n01, float n02, float n03,
			                                 float n10, float n11, float n12, float n13,
			                                 float n20, float n21, float n22, float n23,
			                                 float n30, float n31, float n32,	float n33) {
		modelview.applyTransposed(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21,	n22, n23, n30, n31, n32, n33);
	}
	
	/**
	 * first "index" is row, second is column, e.g., n20 corresponds to the
	 * element located at the third row and first column of the matrix.
	 */
	public void applyModelViewTransposed(float n00, float n01, float n02, float n03,
																		float n10, float n11, float n12, float n13,
																		float n20, float n21,	float n22, float n23,
																		float n30, float n31, float n32, float n33) {

		modelview.applyTransposed(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21,	n22, n23, n30, n31, n32, n33);
	}

	@Override
	public void translate(float tx, float ty) {
		translate(tx, ty, 0);
	}

	@Override
	public void translate(float tx, float ty, float tz) {
		modelview.translate(tx, ty, tz);
	}

	/**
	 * Two dimensional rotation. Same as rotateZ (this is identical to a 3D
	 * rotation along the z-axis) but included for clarity -- it'd be weird for
	 * people drawing 2D graphics to be using rotateZ. And they might kick our a--
	 * for the confusion.
	 */
	@Override
	public void rotate(float angle) {
		rotateZ(angle);
	}

	@Override
	public void rotateX(float angle) {
		modelview.rotateX(angle);
	}

	@Override
	public void rotateY(float angle) {
		modelview.rotateY(angle);
	}

	@Override
	public void rotateZ(float angle) {
		modelview.rotateZ(angle);
	}

	/**
	 * Rotate around an arbitrary vector, similar to glRotate(), except that it
	 * takes radians (instead of degrees).
	 */
	@Override
	public void rotate(float angle, float v0, float v1, float v2) {
		modelview.rotate(angle, v0, v1, v2);
	}

	/**
	 * Same as scale(s, s, s).
	 */
	@Override
	public void scale(float s) {
		scale(s, s, s);
	}

	/**
	 * Same as scale(sx, sy, 1).
	 */
	@Override
	public void scale(float sx, float sy) {
		scale(sx, sy, 1);
	}

	/**
	 * Scale in three dimensions.
	 */
	@Override
	public void scale(float x, float y, float z) {
		modelview.scale(x, y, z);
	}
	
	@Override
	public void pushProjection() {
		if (pmatrixStackDepth == MATRIX_STACK_DEPTH) {
			throw new RuntimeException(ERROR_PUSHMATRIX_OVERFLOW);
		}
		projection.get(pmatrixStack[pmatrixStackDepth]);
		pmatrixStackDepth++;
	}

	@Override
	public void popProjection() {
		if (pmatrixStackDepth == 0) {
			throw new RuntimeException(ERROR_PUSHMATRIX_UNDERFLOW);
		}
		pmatrixStackDepth--;
		projection.set(pmatrixStack[pmatrixStackDepth]);
	}
	
	@Override
	public void setProjection(Mat source) {
	  resetProjection();
		applyProjection(source);
	}

	@Override
	public void resetProjection() {
		projection.reset();
	}
	
	@Override
	public void applyProjection(Mat source) {
		projection.apply(source);
	}

	/**
	 * 16 consecutive values that are used as the elements of a 4 x 4 column-major
	 * matrix.
	 */
	public void applyProjection(float[] source) {
		projection.apply(source);
	}

	public void applyProjection(float m0, float m1, float m2, float m3, float m4,
			float m5, float m6, float m7, float m8, float m9, float m10, float m11,
			float m12, float m13, float m14, float m15) {

		projection.apply(m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12,
				m13, m14, m15);
	}

	@Override
	// TODO check me
	public void applyProjectionRowMajorOrder(float n00, float n01, float n02,
			float n03, float n10, float n11, float n12, float n13, float n20,
			float n21, float n22, float n23, float n30, float n31, float n32,
			float n33) {

		projection.applyTransposed(n00, n01, n02, n03, n10, n11, n12, n13, n20,
				n21, n22, n23, n30, n31, n32, n33);
	}

	public void applyProjectionTransposed(float n00, float n01, float n02,
			float n03, float n10, float n11, float n12, float n13, float n20,
			float n21, float n22, float n23, float n30, float n31, float n32,
			float n33) {

		projection.applyTransposed(n00, n01, n02, n03, n10, n11, n12, n13, n20,
				n21, n22, n23, n30, n31, n32, n33);
	}

	@Override
	public Mat projection() {
		return projection.get();
	}

	@Override
	public Mat getProjection(Mat target) {
		if (target == null)
			target = new Mat();
		target.set(projection);
		return target;
	}
	
	/**
	 * Print the current model (or "transformation") matrix.
	 */
	@Override
	public void printProjection() {
		projection.print();
	}	

	//TODO: this actually requires testing in stand alone mode. Ideally this should be handled
	//by MatrixHelper
	// neeed for screen drawing in stand alone mode (jogl webgl)
	// TODO maybe protected? (camera matrix handling is done through the camera class)
	/*
	public void ortho(float left, float right, float bottom, float top, float near, float far) {
		float x = +2.0f / (right - left);
		float y = +2.0f / (top - bottom);
		float z = -2.0f / (far - near);

		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = -(far + near) / (far - near);

		if (scene.isLeftHanded())
			// The minus sign is needed to invert the Y axis.
			projection.setTransposed(x, 0, 0, tx, 0, -y, 0, ty, 0, 0, z, tz, 0, 0, 0,	1);
		else
			projection.setTransposed(x, 0, 0, tx, 0, y, 0, ty, 0, 0, z, tz, 0, 0, 0, 1);
	}
	
	//TODO study if this should go at all (camera matrix handling is done through the camera class)
	public void perspective(float fov, float aspect, float zNear, float zFar) {
		float ymax = zNear * (float) Math.tan(fov / 2);
		float ymin = -ymax;
		float xmin = ymin * aspect;
		float xmax = ymax * aspect;
		frustum(xmin, xmax, ymin, ymax, zNear, zFar);
	}
	
  //TODO study if this should go at all (camera matrix handling is done through the camera class)
	public void frustum(float left, float right, float bottom, float top,	float znear, float zfar) {
		// new approach: applies it, as in P5
		float n2 = 2 * znear;
		float w = right - left;
		float h = top - bottom;
		float d = zfar - znear;

		if (scene.isLeftHanded())
			// The minus sign is needed to invert the Y axis.
			projection.setTransposed(n2 / w, 0, (right + left) / w, 0, 0, -n2 / h,
					(top + bottom) / h, 0, 0, 0, -(zfar + znear) / d, -(n2 * zfar) / d,
					0, 0, -1, 0);
		else
			projection.setTransposed(n2 / w, 0, (right + left) / w, 0, 0, n2 / h,
					(top + bottom) / h, 0, 0, 0, -(zfar + znear) / d, -(n2 * zfar) / d,
					0, 0, -1, 0);
	}
	*/

	/*
	@Override
	public void beginScreenDrawing() {
		pushProjection();
		resetProjection();
		// next two same as the prv three?
		if (scene.is3D())
			ortho(0f, scene.width(), scene.height(), 0.0f, 0.0f, -1.0f);
		else {
			// TODO implement 2D case
		}
		pushModelView();
		resetModelView();
	}
	// */
}
