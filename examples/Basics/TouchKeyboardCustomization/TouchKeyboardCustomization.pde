/**
 * Touch and Keyboard Customization.
 * by Victor Forero and Jean Pierre Charalambos.
 * 
 * This example shows proscene touch and keyboard customization.
 *
 * Press 'i' to switch the interaction between the camera frame and the interactive frame.
 * Press ' ' (the space bar) to randomly change the mouse bindings and keyboard shortcuts.
 * Press 'q' to display customization details.
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;
import remixlab.dandelion.agent.*;
import android.view.MotionEvent;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import remixlab.dandelion.agent.WheeledMultiTouchAgent.Gestures;

Scene scene;
DroidTouchAgent touch;
KeyboardAgent keyboard;
InteractiveFrame iFrame;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

void setup() {
  size(displayWidth, displayHeight, renderer);
  scene = new Scene(this);
  touch = scene.droidTouchAgent();
  keyboard = scene.keyboardAgent();
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30));
  setExoticCustomization();
}

void draw() {
  background(0);
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid();
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  scene.applyModelView(iFrame.matrix());  //Option 1. or,
  //iFrame.applyTransformation(); //Option 2.
  // Draw an axis using the Scene static function
  scene.drawAxes(20);
  // Draw a second torus attached to the interactive frame
  if (scene.motionAgent().defaultGrabber() == iFrame) {
    fill(0, 255, 255);
    scene.drawTorusSolenoid();
  }
  else if (iFrame.grabsInput(scene.motionAgent())) {
    fill(255, 0, 0);
    scene.drawTorusSolenoid();
  }
  else {
    fill(0, 0, 255, 150);
    scene.drawTorusSolenoid();
  }
  popMatrix();
}

// http://stackoverflow.com/questions/1972392/java-pick-a-random-value-from-an-enum/14257525#14257525
public <T extends Enum<?>> T randomAction(Class<T> actionClass) {
  int x = int(random(actionClass.getEnumConstants().length));
  return actionClass.getEnumConstants()[x];
}

public void setExoticCustomization() {
  // 1. Randomless:
  // 1a. touch
  touch.removeGestureBinding(Target.EYE, Gestures.DRAG_ONE);
  touch.setGestureBinding(Target.EYE, Gestures.DRAG_THREE, DOF6Action.ROTATE); 
  touch.setGestureBinding(Target.FRAME, Gestures.PINCH_THREE, DOF6Action.TRANSLATE_Z);
  touch.setTapBinding(Target.EYE, Gestures.TAP, ClickAction.ALIGN_FRAME);  
  // 1b. keyboard
  keyboard.setShortcut('b', KeyboardAction.TOGGLE_GRID_VISUAL_HINT);
  // 2. Random
  // 2a. touch
  touch.setGestureBinding(Target.FRAME, Gestures.DRAG_ONE, randomAction(DOF6Action.class));
  touch.setGestureBinding(Target.EYE, Gestures.TURN_THREE, randomAction(DOF6Action.class));
    
  // 2b. keyboard
  keyboard.setShortcut('a', randomAction(KeyboardAction.class));
}

public void keyPressed() {
  
  //Enable keyboard Android with Menu button 
  if (key == CODED) {
     if (keyCode == MENU) {
      InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(0, 0);
    }
  }
  if(key == ' ')
    setExoticCustomization();
  if(key == 'u') {
    //mouse.setAsArcball();
    keyboard.setDefaultShortcuts();
  }
  if(key == 'q') {
    String info;
    info = "TAP Gesture, ";
    info += touch.hasTapBinding(Target.EYE, Gestures.TAP) ? "define an EYE binding\n" : "isn't a binding\n";
    info += "ROTATE_X action ";
    info += touch.isGestureActionBound(Target.FRAME, DOF6Action.ROTATE_X) ? "bound to the frame\n" : "not bound\n";
    info += "DRAG Two Finger Gesture -> " + touch.gestureAction(Target.FRAME, Gestures.DRAG_TWO) + " frame\n";
    println(info);
  }
  if ( key == 'i')
    scene.motionAgent().setDefaultGrabber(scene.motionAgent().defaultGrabber() == iFrame ? scene.eye().frame() : iFrame);
}

public boolean dispatchTouchEvent(android.view.MotionEvent event) {
  //Llama el metodo para controlar el agente
  ((DroidTouchAgent)scene.motionAgent()).touchEvent(event);
  return super.dispatchTouchEvent(event);        // pass data along when done!
}
