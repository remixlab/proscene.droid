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
import remixlab.bias.event.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

Scene scene;

boolean enforced = false;  
boolean grabsInput;

Constants.KeyboardAction keyAction;
Constants.DOF2Action mouseAction;
DOF2Event prevEvent, event;
DOF2Event gEvent, prevGenEvent;
KeyboardEvent kEvent;

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
  if (scene.isMouseAgentEnabled())
    return iFrame.grabsInput(scene.mouseAgent());
  else
    return grabsInput;
}

@Override
public void mouseMoved() {
  if (!scene.isMouseAgentEnabled()) {
    event = new DOF2Event(prevEvent, (float) mouseX, (float) mouseY);
    if (enforced)
      grabsInput = true;
    else
      grabsInput = iFrame.checkIfGrabsInput(event);    
    prevEvent = event.get();
  }
}

@Override
public void mouseDragged() {
  if (!scene.isMouseAgentEnabled()) {
    event = new DOF2Event(prevEvent, (float) mouseX, (float) mouseY);
    scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(event, mouseAction, grabsInput ? iFrame : scene.eye().frame()));
    prevEvent = event.get();
  }
}

@Override
public void keyPressed() {
  if (!scene.isKeyboardAgentEnabled()) {
    if (key == 'a' || key == 'g') {
      if (key == 'a')
        keyAction = Constants.KeyboardAction.DRAW_GRID;
      if (key == 'g')
        keyAction = Constants.KeyboardAction.DRAW_AXIS;
      kEvent = new KeyboardEvent(key);      
      scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(kEvent, keyAction, scene));
    }
  }
  if ( key == 'k' || key == 'K' ) {
    if (scene.isKeyboardAgentEnabled()) {
      scene.disableKeyboardAgent();
      println("low level key event handling");
    }
    else {
      scene.enableKeyboardAgent();
      println("high level key event handling");
    }
  }
  if (key == 'y') {
    enforced = !enforced;
    if(scene.isMouseAgentEnabled())
      if (enforced) {
        scene.mouseAgent().setDefaultGrabber(iFrame);
        scene.mouseAgent().disableTracking();
      }
      else {
        scene.mouseAgent().setDefaultGrabber(scene.eye().frame());
        scene.mouseAgent().enableTracking();
      }
    else
      if (enforced)
        grabsInput = true;
      else
        grabsInput = false;
  }
  if ( key == 'm' || key == 'M' ) {
    if (scene.isMouseAgentEnabled()) {
      scene.disableMouseAgent();
      println("Low level mouse event handling");
    }
    else {
      scene.enableMouseAgent();
      println("High level mouse event handling");
    }
  }    
  if (key == 'c')
    if (mouseAction == Constants.DOF2Action.ROTATE)
      mouseAction = Constants.DOF2Action.TRANSLATE;
    else
      mouseAction = Constants.DOF2Action.ROTATE;
}
