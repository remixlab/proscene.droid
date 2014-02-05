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
import remixlab.util.Util;

public abstract class MatrixHelper implements MatrixHelpable, Constants {
	protected AbstractScene scene;
	
	protected Mat projectionViewMat, projectionViewInverseMat;
	protected boolean unprojectCacheIsOptimized, 	projectionViewMatHasInverse;	
	
	public MatrixHelper(AbstractScene scn) {
		scene = scn;
		projectionViewMat = new Mat();
		unprojectCacheIsOptimized = false;
	}
	
	@Override
	public AbstractScene scene() {
		return scene;
	}
	
	@Override
	public void bind() {
		loadProjection();
		loadModelView();//TODO test also: initModelView(false);
		cacheProjectionViewInverse();
	}
	
	@Override
	public void cacheProjectionViewInverse() {
		Mat.multiply(projection(), modelView(), projectionViewMat);
    if(unprojectCacheIsOptimized()) {
    	if(projectionViewInverseMat == null)
    		projectionViewInverseMat = new Mat();
    	projectionViewMatHasInverse = projectionViewMat.invert(projectionViewInverseMat);
    }
  }
	
	/**
   * Returns {@code true} if {@code P x M} and {@code inv (P x M)} are being cached,
   * and {@code false} otherwise.
   * 
   * @see #cacheProjectionViewInverse()
   * @see #optimizeUnprojectCache(boolean)
   */
	@Override
  public boolean unprojectCacheIsOptimized() {
  	return unprojectCacheIsOptimized;
  }
  
  /**
   * Cache {@code inv (P x M)} (and also {@code (P x M)} ) so that
   * {@code project(float, float, float, Matrx3D, Matrx3D, int[], float[])}
   * (and also {@code unproject(float, float, float, Matrx3D, Matrx3D, int[], float[])})
   * is optimised.
   * 
   * @see #unprojectCacheIsOptimized()
   * @see #cacheProjectionViewInverse()
   */
	@Override
  public void optimizeUnprojectCache(boolean optimise) {
  	unprojectCacheIsOptimized = optimise;
  }
  
  @Override
  public Mat projectionView() {
		return projectionViewMat;
	}
  
  @Override
  public Mat projectionViewInverse() {
  	if( !unprojectCacheIsOptimized() )
			throw new RuntimeException("optimizeUnprojectCache(true) should be called first");			
  	return projectionViewInverseMat;
  }
	
	@Override
	public void loadProjection() {
		setProjection(scene.eye().getProjection(true));
	}
	
	@Override
	public void loadModelView() {
		loadModelView(true);
	}

	public void loadModelView(boolean includeView) {
		scene.eye().computeView();
		if(includeView)
		  setModelView(scene.eye().getView(false));
		else
			resetModelView();//loads identity -> only model, (excludes view)
	}
	
	@Override
	public void beginScreenDrawing() {
		pushProjection();
    ortho2D();
    pushModelView();
    resetViewPoint();
	}
	
	@Override
	public void endScreenDrawing() {
		popProjection();  
		popModelView();
	}
	
	// see: http://www.opengl.org/archives/resources/faq/technical/transformations.htm
	// "9.030 How do I draw 2D controls over my 3D rendering?"
	protected void ortho2D() {
		float cameraZ = (scene.height()/2.0f) / (float) Math.tan(QUARTER_PI/2.0f);
    float cameraNear = cameraZ / 2.0f;
    float cameraFar = cameraZ * 2.0f;
    
		float left = -scene.width()/2;
		float right = scene.width()/2;
		float bottom = -scene.height()/2;
		float top = scene.height()/2;		
		float near = cameraNear;
		float far = cameraFar;
		
		float x = +2.0f / (right - left);
		float y = +2.0f / (top - bottom);
		float z = -2.0f / (far - near);
		
		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = -(far + near)   / (far - near);
		
		// The minus sign is needed to invert the Y axis.
		setProjection(new Mat(x,  0,  0,  0,
				                  0, -y,  0,  0,
				                  0,  0,  z,  0,
				                  tx, ty, tz, 1));
	}
	
	//as it's done in P5:
	protected void resetViewPoint() {		
		float eyeX = scene().width()/2f;
		float eyeY = scene.height()/2f;
		float eyeZ = (scene().height()/2f) / (float)Math.tan(PI*60 / 360);
    float centerX = scene().width()/2f;
    float centerY = scene.height()/2f;
    float centerZ = 0;
    float upX = 0;
    float upY = 1; 
    float upZ = 0;
				
		// Calculating Z vector
		float z0 = eyeX - centerX;
		float z1 = eyeY - centerY;
		float z2 = eyeZ - centerZ;
		float mag = (float) Math.sqrt(z0 * z0 + z1 * z1 + z2 * z2);
		if (Util.nonZero(mag)) {
			z0 /= mag;
			z1 /= mag;
			z2 /= mag;
		}
		
		// Calculating Y vector
		float y0 = upX;
		float y1 = upY;
		float y2 = upZ;
		
		// Computing X vector as Y cross Z
		float x0 =  y1 * z2 - y2 * z1;
		float x1 = -y0 * z2 + y2 * z0;
		float x2 =  y0 * z1 - y1 * z0;
		
		// Recompute Y = Z cross X
		y0 =  z1 * x2 - z2 * x1;
		y1 = -z0 * x2 + z2 * x0;
		y2 =  z0 * x1 - z1 * x0;
		
		// Cross product gives area of parallelogram, which is < 1.0 for
		// non-perpendicular unit-length vectors; so normalize x, y here:
		mag = (float) Math.sqrt(x0 * x0 + x1 * x1 + x2 * x2);
		if (Util.nonZero(mag)) {
			x0 /= mag;
			x1 /= mag;
			x2 /= mag;
		}
		
		mag = (float) Math.sqrt(y0 * y0 + y1 * y1 + y2 * y2);
		if (Util.nonZero(mag)) {
			y0 /= mag;
			y1 /= mag;
			y2 /= mag;
		}
		
		Mat mv = new Mat(x0, y0, z0, 0,
				             x1, y1, z1, 0,
				             x2, y2, z2, 0,
				             0,  0,  0,  1);
		
		float tx = -eyeX;
		float ty = -eyeY;
		float tz = -eyeZ;
		
		mv.translate(tx, ty, tz);
		
		setModelView(mv);
	}
}
