/*******************************************************************************
 * dandelion (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.dandelion.core;

import remixlab.dandelion.geom.*;

public interface VisualHintable {
  //drawing

  // 2D-3D
  
  /**
	 * Draws an axis of length {@code length} which origin correspond to the
	 * world coordinate system origin.
	 * 
	 * @see #drawGrid(float, int)
	 */
	public abstract void drawAxis(float length);
	
	/**
	 * Draws a grid in the XY plane, centered on (0,0,0) (defined in the current
	 * coordinate system).
	 * <p>
	 * {@code size} and {@code nbSubdivisions} define its geometry.
	 * 
	 * @see #drawAxis(float)
	 */
	public abstract void drawGrid(float size, int nbSubdivisions);
	
	public abstract void drawDottedGrid(float size, int nbSubdivisions);
	
	/**
	 * Draws a rectangle on the screen showing the region where a zoom operation
	 * is taking place.
	 */	
	public abstract void drawZoomWindowHint();
	
	/**
	 * Draws visual hint (a line on the screen) when a screen rotation is taking
	 * place.
	 */
	public abstract void drawScreenRotateHint();
	
	public abstract void drawFrameSelectionHints();
	
	public abstract void drawEyePathsSelectionHints();
	
	/**
	 * Draws visual hint (a cross on the screen) when the
	 * @link remixlab.dandelion.core.Eye#arcballReferencePoint()} is being set.
	 * <p>
	 * Simply calls {@link #drawCross(float, float, float)} on
	 * {@link remixlab.dandelion.core.Eye#projectedCoordinatesOf()} from
	 * {@link remixlab.dandelion.core.Eye#arcballReferencePoint()}.
	 * 
	 * @see #drawCross(float, float, float)
	 */	
	public abstract void drawArcballReferencePointHint();
	
	public abstract void drawPointUnderPixelHint();
	
	/**
	 * Draws a cross on the screen centered under pixel {@code (px, py)}, and edge
	 * of size {@code size}.
	 * 
	 * @see #drawArcballReferencePointHint()
	 */
	public abstract void drawCross(float px, float py, float size);
	
	/**
	 * Draws a filled circle using screen coordinates.
	 * 
	 * @param subdivisions
	 *          Number of triangles approximating the circle. 
	 * @param center
	 *          Circle screen center.
	 * @param radius
	 *          Circle screen radius.
	 */	
	public abstract void drawFilledCircle(int subdivisions, Vec center, float radius);
	
	/**
	 * Draws a filled square using screen coordinates.
	 * 
	 * @param center
	 *          Square screen center.
	 * @param edge
	 *          Square edge length.
	 */
	public abstract void drawFilledSquare(Vec center, float edge);
	
	/**
	 * Draws the classical shooter target on the screen.
	 * 
	 * @param center
	 *          Center of the target on the screen
	 * @param length
	 *          Length of the target in pixels
	 */
	public abstract void drawShooterTarget(Vec center, float length);
		
	public abstract void drawPath(KeyFrameInterpolator kfi, int mask, int nbFrames, float scale);
	
	/**
	 * Draws a representation of the {@code camera} in the 3D virtual world.
	 * <p>
	 * The near and far planes are drawn as quads, the frustum is drawn using
	 * lines and the camera up vector is represented by an arrow to disambiguate
	 * the drawing.
	 * <p>
	 * When {@code drawFarPlane} is {@code false}, only the near plane is drawn.
	 * {@code scale} can be used to scale the drawing: a value of 1.0 (default)
	 * will draw the Camera's frustum at its actual size.
	 * <p>
	 * <b>Note:</b> The drawing of a Scene's own Scene.camera() should not be
	 * visible, but may create artifacts due to numerical imprecisions.
	 */
 public abstract void drawEye(Eye eye, float scale);
 
 //public void drawWindow(Window window, float scale);	

 public abstract void drawEye(float scale);
 
 public abstract void drawMoebius();
 
 public abstract void drawMoebius(int noFaces);
 
 public abstract void drawMoebius(int noFaces, float torusRadius, float circleRadius);
	
	// Only 3D
	/**
	 * Draws a cylinder of width {@code w} and height {@code h}, along the 
	 * positive {@code z} axis. 
	 */
 public void drawCylinder(float w, float h);
 
 /**
	 * Draws a cylinder whose bases are formed by two cutting planes ({@code m}
	 * and {@code n}), along the Camera positive {@code z} axis.
	 * 
	 * @param detail
	 * @param w radius of the cylinder and h is its height
	 * @param h height of the cylinder
	 * @param m normal of the plane that intersects the cylinder at z=0
	 * @param n normal of the plane that intersects the cylinder at z=h
	 * 
	 * @see #drawCylinder(float, float)
	 */
	public void drawHollowCylinder(int detail, float w, float h, Vec m, Vec n);
 
 /**
	 * Draws a cone along the positive {@code z} axis, with its base centered
	 * at {@code (x,y)}, height {@code h}, and radius {@code r}. 
	 * 
	 * @see #drawCone(int, float, float, float, float, float)
	 */
 public void drawCone(int detail, float x, float y, float r, float h);
 
 /**
	 * Draws a truncated cone along the positive {@code z} axis,
	 * with its base centered at {@code (x,y)}, height {@code h}, and radii
	 * {@code r1} and {@code r2} (basis and height respectively).
	 * 
	 * @see #drawCone(int, float, float, float, float)
	 */
 public void drawCone(int detail, float x, float y, float r1, float r2, float h);
}
