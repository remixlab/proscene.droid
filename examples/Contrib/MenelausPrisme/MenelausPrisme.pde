/**
 * Menelaus Prisme
 * by Jacques Maire (http://www.alcys.com/)
 * 
 * Part of proscene classroom: http://www.openprocessing.org/classroom/1158
 * Check also the collection: http://www.openprocessing.org/collection/1438
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscenedroid.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.constraint.*;
import android.view.MotionEvent;

DroidScene scene;
Prisme prisme;
PFont font;
float tempo;

void setup() {
  size(displayWidth, displayHeight, P3D);
  scene=new DroidScene(this);
  scene.setRadius(160);
  scene.showAll();
  font=loadFont("FreeSans-24.vlw");  
  textFont(font);

  prisme=new Prisme();
}

void draw() { 
  background(255, 150, 0);
  tempo=1.0/5000.0*(millis()%5000);
  prisme.draw();
}

public boolean dispatchTouchEvent(MotionEvent event) {
  //Llama el metodo para controlar el agente
  scene.touchEvent(event);
  return super.dispatchTouchEvent(event);        // pass data along when done!
}
