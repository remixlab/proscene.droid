/**
 * Touch 2 DOF
 * by Victor Manuel Forero and Jean Pierre Charalambos.
 *
 * This example illustrates how to control the Scene using touch events
 * which requires a customized Agent.
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import java.util.Vector;
import android.view.MotionEvent;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.Constants;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.proscene.Scene;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.agent.*;
import remixlab.proscene.*;

Scene scene;
TouchAgent agent;
Box [] boxes;

public void setup() {
  scene = new Scene(this);
  agent = new TouchAgent(scene, "MyTouchAgent");
  scene.setFrameVisualHint(true);
  //esta opcion tambien vale la pena probar:
  //scene.eye().frame().setDampingFriction(0);
  //como el frameRate es bajo, estos timers dan mejor resultado
  scene.setNonSeqTimers();
  boxes = new Box[10];

  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box(scene);
  
  frameRate(100);
}

public String sketchRenderer() {
  return P3D; 
}
  
public void draw() {  
  background(0);
  for (int i = 0; i < boxes.length; i++)      
    boxes[i].draw();
  scene.beginScreenDrawing();  
  text(frameRate, 5, 17);
  scene.endScreenDrawing();  
}

public boolean dispatchTouchEvent(MotionEvent event) {
     
  int action = event.getActionMasked();          // get code for action

  switch (action) {                              // let us know which action code shows up
  case MotionEvent.ACTION_DOWN:
    agent.addTouCursor(event);
    break;
  case MotionEvent.ACTION_UP:
    agent.removeTouCursor(event);
    break;
  case MotionEvent.ACTION_MOVE:
    agent.updateTouCursor(event);
    break;
  }

  return super.dispatchTouchEvent(event);        // pass data along when done!
}
