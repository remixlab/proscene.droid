/**************************************************************************************
 * ProScene (version 2.1.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import processing.core.PGraphics;
import processing.core.PMatrix2D;
import remixlab.dandelion.core.MatrixHelper;
import remixlab.dandelion.geom.Mat;
import remixlab.dandelion.geom.Rotation;
import remixlab.dandelion.geom.Vec;

/**
 * Internal {@link remixlab.dandelion.core.MatrixHelper} based on PGraphicsJava2D graphics transformations.
 */
class Java2DMatrixHelper extends MatrixHelper {
	protected PGraphics	pg;

	public Java2DMatrixHelper(Scene scn, PGraphics renderer) {
		super(scn);
		pg = renderer;
	}

	public PGraphics pg() {
		return pg;
	}

	// Comment the above line and uncomment this one to develop the driver:
	// public PGraphicsJava2D pg() { return (PGraphicsJava2D) pg; }

	@Override
	public void bind() {
		scene.eye().computeProjection();
		scene.eye().computeView();
		cacheProjectionView();

		Vec pos = scene.eye().position();
		Rotation o = scene.eye().frame().orientation();
		translate(scene.width() / 2, scene.height() / 2);

		if (scene.isRightHanded())
			scale(1, -1);
		scale(1 / scene.eye().frame().magnitude(), 1 / scene.eye().frame().magnitude());
		rotate(-o.angle());
		translate(-pos.x(), -pos.y());
	}

	@Override
	protected void cacheProjectionView() {
		Mat.multiply(scene.eye().getProjection(), scene.eye().getView(), projectionViewMat);
		if (isProjectionViewInverseCached()) {
			if (projectionViewInverseMat == null)
				projectionViewInverseMat = new Mat();
			projectionViewMatHasInv = projectionViewMat.invert(projectionViewInverseMat);
		}
	}

	@Override
	public void beginScreenDrawing() {
		Vec pos = scene.eye().position();
		Rotation o = scene.eye().frame().orientation();

		pushModelView();
		translate(pos.x(), pos.y());
		rotate(o.angle());
		scale(scene.window().frame().magnitude(), scene.window().frame().magnitude());
		if (scene.isRightHanded())
			scale(1, -1);
		translate(-scene.width() / 2, -scene.height() / 2);
	}

	@Override
	public void endScreenDrawing() {
		popModelView();
	}

	@Override
	public void pushModelView() {
		pg().pushMatrix();
	}

	@Override
	public void popModelView() {
		pg().popMatrix();
	}

	@Override
	public void resetModelView() {
		pg().resetMatrix();
	}

	@Override
	public Mat modelView() {
		return Scene.toMat(new PMatrix2D(pg().getMatrix()));
	}

	@Override
	public Mat getModelView(Mat target) {
		if (target == null)
			target = Scene.toMat((PMatrix2D) pg().getMatrix()).get();
		else
			target.set(Scene.toMat((PMatrix2D) pg().getMatrix()));
		return target;
	}

	@Override
	public void setModelView(Mat source) {
		pg().setMatrix(Scene.toPMatrix2D(source));
	}

	@Override
	public void printModelView() {
		pg().printMatrix();
	}

	@Override
	public void printProjection() {
		pg().printProjection();
	}

	@Override
	public void applyModelView(Mat source) {
		pg().applyMatrix(Scene.toPMatrix2D(source));
	}

	@Override
	public void translate(float tx, float ty) {
		pg().translate(tx, ty);
	}

	@Override
	public void translate(float tx, float ty, float tz) {
		pg().translate(tx, ty, tz);
	}

	@Override
	public void rotate(float angle) {
		pg().rotate(angle);
	}

	@Override
	public void rotateX(float angle) {
		pg().rotateX(angle);
	}

	@Override
	public void rotateY(float angle) {
		pg().rotateY(angle);
	}

	@Override
	public void rotateZ(float angle) {
		pg().rotateZ(angle);
	}

	@Override
	public void rotate(float angle, float vx, float vy, float vz) {
		pg().rotate(angle, vx, vy, vz);
	}

	@Override
	public void scale(float s) {
		pg().scale(s);
	}

	@Override
	public void scale(float sx, float sy) {
		pg().scale(sx, sy);
	}

	@Override
	public void scale(float x, float y, float z) {
		pg().scale(x, y, z);
	}
}