/**
 * Basic Use.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates a direct approach to using proscene by Scene proper
 * instantiation.
 * 
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;

Scene scene;

void setup() {
  size(640, 360, P3D);
  //Scene instantiation
  scene = new Scene(this);
  // when damping friction = 0 -> spin
  scene.camera().frame().setDampingFriction(0);
}

void draw() {
  background(0);
  fill(204, 102, 0);
  box(20, 30, 50);
}

void keyPressed() {
  if(scene.camera().frame().dampingFriction() == 0)
    scene.camera().frame().setDampingFriction(0.5);
  else
    scene.camera().frame().setDampingFriction(0);
  println("Camera damping friction now is " + scene.camera().frame().dampingFriction());
}