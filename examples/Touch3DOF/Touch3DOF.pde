/**
 * Touch 2 DOF
 * by Victor Manuel Forero and Jean Pierre Charalambos.
 *
 * This example illustrates how to control the Scene using touch events
 * which requires a customized Agent.
 * 
 */

import java.util.Vector;
import android.view.MotionEvent;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.Constants;
import remixlab.dandelion.core.Constants.DOF3Action;
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
  
  agent = new TouchAgent(scene, "OtherTouchAgent");
  
  scene.setNonSeqTimers();
  boxes = new Box[10];
  scene.setDottedGrid(false);

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
      
  switch (action) {        // let us know which action code shows up
  case MotionEvent.ACTION_DOWN:
    agent.addTouCursor(event);
    if (event.getX()< 10){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);  
    }
    break;
  case MotionEvent.ACTION_UP:
    agent.removeTouCursor(event);
    break;
  case MotionEvent.ACTION_MOVE:
   if (event.getPointerCount() == 1){
      agent.updateTouCursor(event);
    }else{
      agent.transalateTouCursor(event);
    }
    break;
  }

  return super.dispatchTouchEvent(event);        // pass data along when done!
}
