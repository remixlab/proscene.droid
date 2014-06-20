/**
 * Basic Use.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates a direct approach to use proscene by Scene proper
 * instantiation.
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscenedroi.*;
import remixlab.proscene.*;
import android.view.MotionEvent;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

DroidScene scene;
//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

void setup() {
  //size(640, 360, renderer);
  //Scene instantiation
  scene = new DroidScene(this);
  // when damping friction = 0 -> spin
  scene.eye().frame().setDampingFriction(0);
  scene.TouchAgent().setCameraFirstPerson(true);
}

public String sketchRenderer() {
  return P3D; 
}

void draw() {
  background(0);
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid();
}

void keyPressed() {
  if(scene.eye().frame().dampingFriction() == 0)
    scene.eye().frame().setDampingFriction(0.5);
  else
    scene.eye().frame().setDampingFriction(0);
  println("Camera damping friction now is " + scene.eye().frame().dampingFriction());
 
  //press the space bar to switch between camera as first person 
  if (keyCode == 62) {
    if (scene.TouchAgent().isCameraFirstPerson())
      scene.TouchAgent().setCameraFirstPerson(false);
    else
      scene.TouchAgent().setCameraFirstPerson(true);
  }
  
  //Enable keyboard Android with Menu button 
  if (key == CODED) {
     if (keyCode == MENU) {
      InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(0, 0);
    }
  }
}

//Get the event directly
public boolean dispatchTouchEvent(MotionEvent event) {
  //Call the method to control the agent
  scene.surfaceTouchEvent(event);
  return super.dispatchTouchEvent(event);        // pass data along when done!
}
