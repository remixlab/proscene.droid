/**
 * Basic 2D
 * by Jean Pierre Charalambos.
 *
 * Doc to come...
 */

import remixlab.proscene.*;
import remixlab.dandelion.geom.*;

Scene scene;
PFont font;
float angle;	

public void setup() {
  //size(640, 360, JAVA2D);
  size(640, 360, P2D);
  font = loadFont("FreeSans-16.vlw");
  textFont(font);
  scene = new Scene(this);
}	

public void draw() {
  background(150);
  ellipse(0, 0, 40, 40);		

  scene.beginScreenDrawing();
  text("Hello world", 5, 17);
  scene.endScreenDrawing();

  rect(50, 50, 30, 30);
}

public void keyPressed() {
  if (key == 'y' || key == 'Y') {
    scene.window().flip();
  }
}

