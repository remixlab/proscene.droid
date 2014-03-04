/**
 * Low High
 * by Jean Pierre Charalambos.
 *
 * Doc to come...
 */

import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.bias.core.*;
import remixlab.bias.generic.event.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

Scene scene;

boolean enforced = false;  
boolean grabsInput;

Constants.KeyboardAction keyAction;
Constants.DOF2Action mouseAction;
ActionDOF2Event<Constants.DOF2Action> prevEvent, event;
ActionDOF2Event<Constants.DOF2Action> gEvent, prevGenEvent;
ActionKeyboardEvent<Constants.KeyboardAction> kEvent;

int count = 4;

InteractiveFrame iFrame;

@Override
public void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30, 0));

  mouseAction = Constants.DOF2Action.ROTATE;
}

@Override
public void draw() {
  background(0);

  fill(204, 102, 0);
  box(20, 30, 40);

  pushMatrix();
  iFrame.applyTransformation();
  scene.drawAxis(20);

  // Draw a second box    
  if (iFrameGrabsInput()) {
    fill(255, 0, 0);
    box(12, 17, 22);
  } 
  else {
    fill(0, 0, 255);
    box(10, 15, 20);
  }

  popMatrix();
}

public boolean iFrameGrabsInput() {
  if (scene.isDefaultMouseAgentEnabled())
    return iFrame.grabsAgent(scene.defaultMouseAgent());
  else
    return grabsInput;
}

@Override
public void mouseMoved() {
  if (!scene.isDefaultMouseAgentEnabled()) {
    event = new ActionDOF2Event<Constants.DOF2Action>(prevEvent, (float) mouseX, (float) mouseY);
    if (enforced)
      grabsInput = true;
    else
      grabsInput = iFrame.checkIfGrabsInput(event);    
    prevEvent = event.get();
  }
}

@Override
public void mouseDragged() {
  if (!scene.isDefaultMouseAgentEnabled()) {
    event = new ActionDOF2Event<Constants.DOF2Action>(prevEvent, (float) mouseX, (float) mouseY, mouseAction);
    if (grabsInput)
      scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(event, iFrame));
    else
      scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(event, scene.eye().frame()));
    prevEvent = event.get();
  }
}

@Override
public void keyPressed() {
  if (!scene.isDefaultKeyboardAgentEnabled()) {
    if (key == 'a' || key == 'g') {
      if (key == 'a')
        keyAction = Constants.KeyboardAction.DRAW_GRID;
      if (key == 'g')
        keyAction = Constants.KeyboardAction.DRAW_AXIS;
      kEvent = new ActionKeyboardEvent<Constants.KeyboardAction>(key, keyAction);      
      scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(kEvent, scene));
    }
  }
  if ( key == 'k' || key == 'K' ) {
    if (scene.isDefaultKeyboardAgentEnabled()) {
      scene.disableDefaultKeyboardAgent();
      println("low level key event handling");
    }
    else {
      scene.enableDefaultKeyboardAgent();
      println("high level key event handling");
    }
  }
  if (key == 'y') {
    enforced = !enforced;
    if(scene.isDefaultMouseAgentEnabled())
      if (enforced) {
        scene.defaultMouseAgent().setDefaultGrabber(iFrame);
        scene.defaultMouseAgent().disableTracking();
      }
      else {
        scene.defaultMouseAgent().setDefaultGrabber(scene.eye().frame());
        scene.defaultMouseAgent().enableTracking();
      }
    else
      if (enforced)
        grabsInput = true;
      else
        grabsInput = false;
  }
  if ( key == 'm' || key == 'M' ) {
    if (scene.isDefaultMouseAgentEnabled()) {
      scene.disableDefaultMouseAgent();
      println("Low level mouse event handling");
    }
    else {
      scene.enableDefaultMouseAgent();
      println("High level mouse event handling");
    }
  }    
  if (key == 'c')
    if (mouseAction == Constants.DOF2Action.ROTATE)
      mouseAction = Constants.DOF2Action.TRANSLATE;
    else
      mouseAction = Constants.DOF2Action.ROTATE;
}
