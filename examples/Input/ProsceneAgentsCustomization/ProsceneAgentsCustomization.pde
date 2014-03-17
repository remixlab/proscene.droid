/**
 * Camera Customization.
 * by Jean Pierre Charalambos.
 * 
 * This example shows all the different aspects of proscene that
 * can be customized and how to do it.
 * 
 * Read the commented lines of the sketch code for details.
 *
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.dandelion.core.Constants.KeyboardAction;

import remixlab.bias.event.*;

Scene scene;
MouseAgent prosceneMouseAgent;
KeyboardAgent prosceneKeyboardAgent;
CustomizedMouseAgent mouseAgent;
CustomizedKeyboardAgent keyboardAgent;
InteractiveFrame iFrame;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30, 0));

  prosceneMouseAgent = scene.defaultMouseAgent();
  prosceneKeyboardAgent = scene.defaultKeyboardAgent();

  mouseAgent = new CustomizedMouseAgent(scene, "MyMouseAgent");
  keyboardAgent = new CustomizedKeyboardAgent(scene, "MyKeyboardAgent");
  
  scene.setDefaultMouseAgent(mouseAgent);
  scene.setDefaultKeyboardAgent(keyboardAgent);
}

void draw() {
  background(0);
  fill(204, 102, 0);
  box(20, 20, 40);
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  scene.applyModelView(iFrame.matrix());  //Option 1. or,
  //iFrame.applyTransformation(); //Option 2.
  // Draw an axis using the Scene static function
  scene.drawAxis(20);
  // Draw a second box attached to the interactive frame
  if (iFrame.grabsInput(scene.defaultMouseAgent())) {
    fill(255, 0, 0);
    box(12, 17, 22);
  }
  else {
    fill(0, 0, 255);
    box(10, 15, 20);
  }  
  popMatrix();
}

public void keyPressed() {
  if ( key != ' ') return;
  if ( scene.inputHandler().isAgentRegistered(prosceneMouseAgent) ) {
    scene.setDefaultMouseAgent(mouseAgent);
    scene.setDefaultKeyboardAgent(keyboardAgent);
  }
  else { 
    scene.setDefaultMouseAgent(prosceneMouseAgent);
    scene.setDefaultKeyboardAgent(prosceneKeyboardAgent);
  }
}

public class CustomizedMouseAgent extends ProsceneMouse {
  public CustomizedMouseAgent(Scene scn, String n) {
    //inner class'ss weirdeness ...ss
    scn.super(scn, n);
    inputHandler().unregisterAgent(this);
    eyeProfile().setBinding(B_LEFT, DOF2Action.TRANSLATE);
    eyeProfile().setBinding(B_META, B_RIGHT, DOF2Action.ROTATE);
  }
}

public class CustomizedKeyboardAgent extends ProsceneKeyboard {
  public CustomizedKeyboardAgent(Scene scn, String n) {
    //ssame ...ss
    scn.super(scn, n);
    inputHandler().unregisterAgent(this);
    keyboardProfile().setShortcut('g', KeyboardAction.DRAW_AXIS);
    keyboardProfile().setShortcut('z', KeyboardAction.DRAW_FRAME_SELECTION_HINT);
    keyboardProfile().setShortcut('a', KeyboardAction.DRAW_GRID);
    //press "alt + shift" + 'l' -> moves camera to the left:
    //http://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html#VK_L
    keyboardProfile().setShortcut((B_ALT | B_SHIFT), java.awt.event.KeyEvent.VK_L, KeyboardAction.MOVE_EYE_LEFT);
  }
}
