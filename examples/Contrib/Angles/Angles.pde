/**
 * Angles
 * by Jacques Maire (http://www.alcys.com/)
 * 
 * Part of proscene classroom: http://www.openprocessing.org/classroom/1158
 * Check also the collection: http://www.openprocessing.org/collection/1438
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.constraint.*;
import android.view.MotionEvent;

DroidScene scene;
LocalConstraint planaire;
WorldConstraint drag;
float lon;
Arcad arc1;
PVector or;
PFont font;

void setup() {
  size(displayWidth, displayHeight, P3D);
  font=loadFont("FreeSans-24.vlw");
  textFont(font);
  scene =new DroidScene(this);
  scene.setRadius(700);
  scene.setAxesVisualHint(false);
  scene.setGridVisualHint(false);
  lon=200;
  or=new PVector(0, 0, 0);

  planaire=new LocalConstraint();
  planaire.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new Vec(0, 0, 1));
  planaire.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));
  drag=new WorldConstraint();
  drag.setTranslationConstraint(AxisPlaneConstraint.Type.FREE, new Vec(0, 0, 0));
  drag.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));

  scene.camera().setPosition(new Vec(0, 0, 900));

  arc1=new Arcad(new PVector(-50, -200, 200));
  or=new PVector(0, 0, 0);  
}

void draw() {
  background(255, 155, 0);
  arc1.draw();
}

public boolean dispatchTouchEvent(MotionEvent event) {
  //Llama el metodo para controlar el agente
  scene.surfaceTouchEvent(event);
  return super.dispatchTouchEvent(event);        // pass data along when done!
}
