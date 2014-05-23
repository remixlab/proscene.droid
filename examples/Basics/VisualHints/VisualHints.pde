/**
 * Visual Hints.
 * by Jean Pierre Charalambos.
 * 
 * This illustrates how to customize the looking of proscene visual hints.
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.Constants.*;
import android.view.MotionEvent;

DroidScene scene;
boolean focusIFrame;
InteractiveAvatarFrame iFrame;
boolean displayPaths = true;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

public void setup() {
  size(displayWidth, displayHeight, renderer);
  scene = new CustomizedScene(this);
  iFrame = new InteractiveAvatarFrame(scene);
  iFrame.translate(new Vec(30, -30, 0));
  scene.keyboardAgent().profile().setShortcut('r', null);
  scene.setNonSeqTimers();
  scene.setVisualHints(Scene.AXES | Scene.GRID | Scene.PICKING );
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
  background(40);
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid(2);

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
    scene.drawTorusSolenoid(6, 10);
  }
  else if (iFrame.grabsInput(scene.motionAgent())) {
    fill(255, 0, 0);
    scene.drawTorusSolenoid(8, 10);
  }
  else {
    fill(0, 0, 255, 150);
    scene.drawTorusSolenoid(6, 10);
  }
  popMatrix();
  drawPaths();
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
  if(key == 'u')
    displayPaths = !displayPaths;
}

public void drawPaths() {
  if(displayPaths) {
    pushStyle();
    colorMode(PApplet.RGB, 255);
    strokeWeight(3);
    stroke(220,0,220);
    scene.drawEyePaths();
    popStyle();
  }
  else
    scene.hideEyePaths();
}

public boolean dispatchTouchEvent(MotionEvent event) {
  //Llama el metodo para controlar el agente
  scene.surfaceTouchEvent(event);
  return super.dispatchTouchEvent(event);        // pass data along when done!
}

public class CustomizedScene extends DroidScene {
  // We need to call super(p) to instantiate the base class
  public CustomizedScene(PApplet p) {
    super(p);
  }

  @Override
  protected void drawPickingHint() {
    pg().pushStyle();
    pg().colorMode(PApplet.RGB, 255);
    pg().strokeWeight(1);
    pg().stroke(0,220,0);
    drawPickingTargets();
    pg().popStyle();
  }
}

