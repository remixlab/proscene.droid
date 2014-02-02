/**
 * Screen Drawing.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to combine 2D and 3D drawing.
 * 
 * All screen drawing should be enclosed between Scene.beginScreenDrawing() and
 * Scene.endScreenDrawing(). Then the x and y coordinates (e.g., as within vertex())
 * are expressed in pixels screen coordinates. In this mode, you should use z
 * values that are in the [0.0, 1.0] range (0.0 corresponding to the near
 * clipping plane and 1.0 being just beyond the far clipping plane).
 *
 * In addition, if you want your screen drawing to appear on top of your 3d scene
 * then draw first all your 3d before doing any call to a beginScreenDrawing()
 * and endScreenDrawing()} pair.
 * 
 * Press 'x' to toggle the screen drawing.
 * Press 'y' to clean your screen drawing.
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

Scene scene;
Box [] boxes;
ArrayList points;
PFont font;
boolean onScreen = false;
boolean additionalInstructions = false;
	
void setup() {
  size(640, 360, P3D);  
  font = loadFont("FreeSans-16.vlw");
  textFont(font);
  scene = new Scene(this);
  scene.setRadius(150);
  scene.showAll();
  
  boxes = new Box[50];
  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box(scene);
  
  points = new ArrayList();  // Create an empty ArrayList
}

void draw() {
  background(0);
  // A. 3D drawing
  for (int i = 0; i < boxes.length; i++)
    boxes[i].draw();
    
  // B. 2D drawing on top of the 3d scene 
  // All screen drawing should be enclosed between Scene.beginScreenDrawing() and
  // Scene.endScreenDrawing(). Then you can just begin drawing your screen shapes
  // (defined between beginShape() and endShape()).
  scene.beginScreenDrawing();
  pushStyle();
  strokeWeight(8);
  stroke(183,67,158,127);
  noFill();
  beginShape();
  for (int i = 0; i < points.size(); i++)    
    vertex((float) ((Point) points.get(i)).x(), (float) ((Point) points.get(i)).y(), 1);
  endShape();  
  popStyle();
  scene.endScreenDrawing();
  
  // C. Render text instructions.
  scene.beginScreenDrawing();
  if(onScreen)
    text("Press 'x' to handle 3d scene", 5, 17);
  else
    text("Press 'x' to begin screen drawing", 5, 17);
  if(additionalInstructions)
    text("Press 'y' to clear screen", 5, 35);
  scene.endScreenDrawing();  
}

void keyPressed() {
  if ((key == 'x') || (key == 'X')) {
    if(scene.isDefaultMouseAgentEnabled())
      scene.disableDefaultMouseAgent();
    else
      scene.enableDefaultMouseAgent();
    onScreen = !onScreen;
    if(!additionalInstructions)
      additionalInstructions = true;
  }
  if ((key == 'y') || (key == 'Y'))
    points.clear();
}

void mouseDragged() {
  if(!scene.isDefaultMouseAgentEnabled())
    points.add(new Point(mouseX, mouseY));
}
