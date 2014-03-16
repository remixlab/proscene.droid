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
import remixlab.dandelion.core.Constants.ClickAction;
import remixlab.dandelion.core.Constants.KeyboardAction;

import remixlab.bias.generic.event.ActionDOF2Event;
import remixlab.bias.generic.event.ActionKeyboardEvent;

Scene scene;
InteractiveFrame iFrame;
boolean exotic = true;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

void setup() {
  size(640, 360, renderer);
  scene = new Scene(this);
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30));
  setExoticCustomization();
}

void draw() {
  background(0);
  fill(204, 102, 0);
  scene.drawTorusSolenoid();
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
    scene.drawTorusSolenoid();
  }
  else {
    fill(0, 0, 255);
    scene.drawTorusSolenoid();
  }  
  popMatrix();
}

public void setExoticCustomization() {
  //eye
  scene.setMouseButtonBinding(true, LEFT, DOF2Action.TRANSLATE);
  scene.setMouseButtonBinding(true, RIGHT, DOF2Action.CAD_ROTATE);
  //scene.setMouseClickBinding(true, EventConstants.B_SHIFT, EventConstants.B_RIGHT, 2, ClickAction.DRAW_AXIS);
  scene.setMouseClickBinding(true, Event.SHIFT, LEFT, 2, ClickAction.DRAW_AXIS);
  //frame
  scene.setMouseButtonBinding(false, LEFT, DOF2Action.TRANSLATE);
  //keyboard
  scene.setKeyboardShortcut('g',KeyboardAction.DRAW_AXIS);
  scene.setKeyboardShortcut(Event.CTRL,java.awt.event.KeyEvent.VK_G,KeyboardAction.DRAW_GRID);
}

public void keyPressed() {
  if ( key != ' ')
    return;
  if(exotic) {
    scene.setMouseAsArcball();
    scene.setDefaultKeyboardShortcuts();
    exotic = false;
  }
  else {
    setExoticCustomization();
    exotic = true;
  }
}