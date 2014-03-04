/**
 * Frame Interaction.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates the three interactive frame mechanisms built-in proscene:
 * Camera, InteractiveFrame and MouseGrabber.
 * 
 * Press 'i' (which is a shortcut defined below) to switch the interaction between the
 * eye frame and the interactive frame. You can also manipulate the interactive
 * frame by picking the blue box passing the mouse next to its axis origin.
 * 
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current eye profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.Constants.KeyboardAction;

Scene scene;
boolean focusIFrame;
InteractiveAvatarFrame iFrame;
boolean displayPaths = true;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

public void setup() {
  size(640, 360, renderer);    
  scene = new CustomizedScene(this);  
  iFrame = new InteractiveAvatarFrame(scene);
  iFrame.translate(new Vec(30, -30, 0));
  scene.defaultKeyboardAgent().profile().setShortcut('r', null);
  scene.setJavaTimers();
  scene.setVisualHints(Constants.AXIS | Constants.GRID | Constants.FRAME );
  //create a eye path and add some key frames:
  //key frames can be added at runtime with keys [j..n]
  scene.eye().setPosition(new Vec(80,0,0));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.eye().addKeyFrameToPath(1);

  scene.eye().setPosition(new Vec(30,30,-80));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.eye().addKeyFrameToPath(1);

  scene.eye().setPosition(new Vec(-30,-30,-80));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.eye().addKeyFrameToPath(1);

  scene.eye().setPosition(new Vec(-80,0,0));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.eye().addKeyFrameToPath(1);

  //re-position the eye:
  scene.eye().setPosition(new Vec(0,0,1));
  if(scene.is3D()) scene.eye().lookAt( scene.eye().sceneCenter() );
  scene.showAll();
}

public void draw() {
  background(70);
  fill(204, 102, 0);
  scene.drawTorusSolenoid(2);

  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(iFrame.matrix()) is possible but inefficient 
  iFrame.applyTransformation();//very efficient
  // Draw an axis using the Scene static function
  scene.drawAxis(20);

  // Draw a second box
  if (focusIFrame) {
    fill(0, 255, 255);
    scene.drawTorusSolenoid(6, 10);
  }
  else if (iFrame.grabsAgent(scene.defaultMouseAgent())) {
    fill(255, 0, 0);
    scene.drawTorusSolenoid(8, 10);
  }
  else {
    fill(0, 0, 255);
    scene.drawTorusSolenoid(6, 10);
  }
  popMatrix();
  drawPaths();
}

public void keyPressed() {
  if ( key == 'i') {
    if ( focusIFrame ) {
      scene.defaultMouseAgent().setDefaultGrabber(scene.eye().frame());
      scene.defaultMouseAgent().enableTracking();
    } 
    else {
      scene.defaultMouseAgent().setDefaultGrabber(iFrame);
      scene.defaultMouseAgent().disableTracking();
    }
    focusIFrame = !focusIFrame;
  }
  if(key == 'u')
    displayPaths = !displayPaths;
}

public void drawPaths() {
  if(displayPaths) {
    pushStyle();
    colorMode(PApplet.RGB, 255);
    strokeWeight(3);
    stroke(220,0,220);
    scene.drawAllEyePaths();
    popStyle();
  }
  else
    scene.hideAllEyePaths();
}

public class CustomizedScene extends Scene {
  // We need to call super(p) to instantiate the base class
  public CustomizedScene(PApplet p) {
    super(p);
  }

  @Override
  protected void drawFramesHint() {
    pg().pushStyle();
    pg().colorMode(PApplet.RGB, 255);
    pg().strokeWeight(1);
    pg().stroke(0,220,0);
    drawFrameSelectionTargets();
    pg().popStyle();
  }
}
