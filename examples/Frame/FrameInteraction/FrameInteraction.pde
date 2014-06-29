/**
 * Frame Interaction.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates the three interactive frame mechanisms built-in proscene:
 * Camera, InteractiveFrame and MouseGrabber.
 * 
 * Press 'i' (which is a shortcut defined below) to switch the interaction between the
 * camera frame and the interactive frame. You can also manipulate the interactive
 * frame by picking the blue box passing the mouse next to its axis origin.
 * 
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscenedroid.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

Scene scene;
boolean focusIFrame;
InteractiveFrame iFrame;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

public void setup() {
  size(640, 360, renderer);		
  scene = new Scene(this);	
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(50, 50));
  scene.setNonSeqTimers();
}

public void draw() {
  background(0);
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid();	

  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(iFrame.matrix()) is possible but inefficient 
  iFrame.applyTransformation();//very efficient
  // Draw an axis using the Scene static function
  scene.drawAxes(20);

  // Draw a second box
  if (focusIFrame) {
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

public void keyPressed() {
  if ( key == 'i') {
    if ( focusIFrame ) {
      scene.motionAgent().setDefaultGrabber(scene.eye().frame());
      scene.motionAgent().enableTracking();
    } 
    else {
      scene.motionAgent().setDefaultGrabber(iFrame);
      scene.motionAgent().disableTracking();
    }
    focusIFrame = !focusIFrame;
  }
}
