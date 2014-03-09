/**
 * Low
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
boolean iFrameGrabsInput;

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

  // Low-level handling (mouse and keyboard in the case)
  // requires disabling high level handling ;)
  scene.disableDefaultKeyboardAgent();
  scene.disableDefaultMouseAgent();

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
  if (iFrameGrabsInput) {
    fill(255, 0, 0);
    box(12, 17, 22);
  } 
  else {
    fill(0, 0, 255);
    box(10, 15, 20);
  }

  popMatrix();
}

@Override
public void mouseMoved() {
  // mouseX and mouseY are reduced into a ActionDOF2Event
  event = new ActionDOF2Event<Constants.DOF2Action>(prevEvent, (float) mouseX, (float) mouseY);
  // iFrame may be grabbing the mouse input in two cases:
  // Enforced by the 'y' key
  if (enforced)
    iFrameGrabsInput = true;
  // or if the mouse position is close enough the the iFrame position:
  else
    iFrameGrabsInput = iFrame.checkIfGrabsInput(event);		
  prevEvent = event.get();
}

@Override
public void mouseDragged() {
  // a mouse drag will cause action execution without involving any agent:
  event = new ActionDOF2Event<Constants.DOF2Action>(prevEvent, (float) mouseX, (float) mouseY, mouseAction);
  // the action will be executed by the iFrame or the camera:
  if (iFrameGrabsInput)
    scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(event, iFrame));
  else
    scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(event, scene.eye().frame()));
  prevEvent = event.get();
}

@Override
public void keyPressed() {
  // All keyboard action in proscene are performed by the Scene.
  // Here we define two keyboard actions
  if (key == 'a' || key == 'g') {
    if (key == 'a')
      keyAction = Constants.KeyboardAction.DRAW_GRID;
    if (key == 'g')
      keyAction = Constants.KeyboardAction.DRAW_AXIS;
    kEvent = new ActionKeyboardEvent<Constants.KeyboardAction>(key, keyAction);      
    scene.inputHandler().enqueueEventTuple(new EventGrabberTuple(kEvent, scene));
  }
  // Grabbing the iFrame may be done with the keyboard:
  if (key == 'y') {
    enforced = !enforced;
    if (enforced)
      iFrameGrabsInput = true;
    else
      iFrameGrabsInput = false;
  }	
  // The default mouse action (to be performed when dragging it) may be change here:	
  if (key == 'c')
    if (mouseAction == Constants.DOF2Action.ROTATE)
      mouseAction = Constants.DOF2Action.TRANSLATE;
    else
      mouseAction = Constants.DOF2Action.ROTATE;
}
