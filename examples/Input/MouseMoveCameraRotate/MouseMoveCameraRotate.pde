/**
 * Mouse Move Came Rotate
 * by Jean Pierre Charalambos.
 *
 * Doc to come...
 */

import remixlab.dandelion.geom.*;
import remixlab.dandelion.agent.KeyboardAgent;
import remixlab.dandelion.agent.MouseAgent;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.proscene.*;
import remixlab.bias.event.*;

Scene scene;
MouseAgent prosceneMouse;
MouseMoveAgent agent;

public void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  prosceneMouse = scene.defaultMouseAgent();
  scene.enableBoundaryEquations();
  scene.setRadius(150);
  scene.showAll();
  agent = new MouseMoveAgent(scene, "MyMouseAgent");
  scene.setDefaultMouseAgent(agent);
}

public void draw() {	
  background(0);	
  noStroke();
  if ( scene.camera().ballIsVisible(new Vec(0, 0, 0), 40) == Camera.Visibility.SEMIVISIBLE )
    fill(255, 0, 0);
  else
    fill(0, 255, 0);
  sphere(40);
}

public void keyPressed() {
  // We switch between the default mouse agent and the one we created:
  if ( key != ' ') return;
  scene.setDefaultMouseAgent( scene.inputHandler().isAgentRegistered(prosceneMouse) ? agent : prosceneMouse );
}
