/**
 * IFrame 2D
 * by Jean Pierre Charalambos.
 *
 * Doc to come...
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

Scene scene;
InteractiveFrame iFrame;

public void setup() {
  //size(640, 360, P2D);
  size(640, 360, JAVA2D);
  scene = new Scene(this);
  iFrame = new InteractiveFrame(scene);
  scene.setFrameVisualHint(true);
}

public void draw() {	
  background(0);
  fill(204, 102, 0);
  rect(0, 0, 55, 55);
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(iFrame.matrix()) is handy but
  // inefficient
  iFrame.applyTransformation();// optimum
  // Draw an axis using the Scene static function
  scene.drawAxis(40);
  // Draw a second box attached to the interactive frame
  if (iFrame.grabsAgent(scene.defaultMouseAgent())) {
    fill(255, 0, 0);
    rect(0, 0, 35, 35);
  }
  else {
    fill(0, 0, 255);
    rect(0, 0, 30, 30);
  }
  popMatrix();

  scene.beginScreenDrawing(); 
  text("Hello world", 5, 17);
  scene.endScreenDrawing(); //
}
