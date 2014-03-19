import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;

Scene scene;
boolean focusIFrame;
boolean firstPerson;
InteractiveFrame iFrame;

void setup() {
  size(640, 360, P3D);		
  scene = new Scene(this);	
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30, 0));
  scene.mouseAgent().setAsFirstPerson();
  firstPerson = true;
}

void draw() {
  background(0);
  fill(204, 102, 0);
  box(20, 30, 40);		

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
    box(12, 17, 22);
  }
  else if (iFrame.grabsInput(scene.mouseAgent())) {
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
  if ( key == 'i') {
    if ( focusIFrame ) {
      scene.mouseAgent().setDefaultGrabber(scene.eye().frame());
      scene.mouseAgent().enableTracking();
    } 
    else {
      scene.mouseAgent().setDefaultGrabber(iFrame);
      scene.mouseAgent().disableTracking();
    }
    focusIFrame = !focusIFrame;
  }
  if ( key == ' ') {
    firstPerson = !firstPerson;
    if ( firstPerson ) {
      scene.mouseAgent().setAsFirstPerson();
    }
    else {
      scene.mouseAgent().setAsArcball();
    }
  }
}
