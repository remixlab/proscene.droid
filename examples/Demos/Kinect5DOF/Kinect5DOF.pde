/**
 * Low High
 * by Miguel Parra and Pierre Charalambos.
 *
 * Doc to come...
 */

import processing.opengl.*;

import SimpleOpenNI.*;

import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.bias.core.*;
import remixlab.bias.generic.event.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

Scene scene;
HIDAgent agent;
Kinect kinect;
PVector kinectPos, kinectRot;
Box [] boxes;
boolean cameraMode = true;

void setup() {
  size(800, 600, P3D);
  scene = new Scene(this);
  kinect=new Kinect(this);

  scene.setRadius(200);
  scene.showAll();

  agent = new HIDAgent(scene, "Kinect") {
    ActionDOF6Event<Constants.DOF6Action> event, prevEvent;
    @Override
    public ActionDOF6Event<Constants.DOF6Action> feed() {
      if(!kinect.initialDefined) return null;
      if (cameraMode) { //-> event is absolute
        setDefaultGrabber(scene.eye().frame()); //set it by default
        disableTracking();
        scene.setFrameVisualHint(false);
        event=new ActionDOF6Event<Constants.DOF6Action>(kinectPos.x, kinectPos.y, kinectPos.z, 0, kinectRot.y, kinectRot.z); 
      }
      else { //frame mode -> event is relative
        setDefaultGrabber(null);
        enableTracking();
        scene.setFrameVisualHint(true);
        event = new ActionDOF6Event<Constants.DOF6Action>(prevEvent, kinect.posit.x, kinect.posit.y, kinect.posit.z, 0, kinectRot.y, kinectRot.z);
        prevEvent = event.get();
        //debug:     
        //println("abs pos: " + event.getX() + ", " + event.getY() + ", " + event.getZ());
        //println("deltas : " + event.getDX() + ", " + event.getDY() + ", " + event.getDZ());
        if(trackedGrabber() == null)
          updateTrackedGrabber(event); 
      }
      return event;
    }
  };  
  agent.setSensitivities(0.03, 0.03, 0.03, 0.00005, 0.00005, 0.00005);
  agent.eyeProfile().setBinding(Constants.DOF6Action.TRANSLATE_ROTATE); //set by default anyway
  agent.frameProfile().setBinding(Constants.DOF6Action.TRANSLATE3);
  //needs fixing in dandelion:
  //agent.frameProfile().setBinding(Constants.DOF6Action.TRANSLATE_ROTATE); //set by default anyway

  boxes = new Box[30];
  for (int i = 0; i < boxes.length; i++) {
    boxes[i] = new Box(scene);
    agent.addInPool(boxes[i].iFrame);
  }
}

void draw() {
  background(0);

  for (int i = 0; i < boxes.length; i++)      
    boxes[i].draw();

  //Update the Kinect data
  kinect.update();

  kinect.draw();

  //Get the translation and rotation vectors from Kinect
  kinectPos=kinect.deltaPositionVector();
  kinectRot=kinect.rotationVector();
}

void keyPressed() {
  if(key == 'v' || key == 'V')  cameraMode = !cameraMode;
}

void onNewUser(SimpleOpenNI curContext, int userId) {
  kinect.onNewUser(curContext, userId);
}
