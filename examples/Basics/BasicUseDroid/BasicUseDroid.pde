import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

import android.view.MotionEvent;

Scene scene;
float x,y,z;
Box [] boxes;


void setup() {
  boxes = new Box[10];
  //size(displayWidth, displayHeight, P3D);
  fullScreen(P3D, 1);
  scene = new Scene(this);
  scene.setDottedGrid(false);
  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box(scene);
    
  frameRate(100);
}

void draw() {
  background(0);
  lights();
  println(scene.platform());
  scene.beginScreenDrawing();  
  text(frameRate, 5, 17);
  scene.endScreenDrawing();
  for (int i = 0; i < boxes.length; i++)      
    boxes[i].draw();
    
}

public boolean surfaceTouchEvent(MotionEvent event)
{
  ((DroidTouchAgent)scene.motionAgent()).touchEvent(event);
  return true;
}