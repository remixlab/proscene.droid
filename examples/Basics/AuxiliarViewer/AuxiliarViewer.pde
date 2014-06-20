/**
 * Auxiliar Viewer
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to use proscene off-screen rendering to build
 * an second view on the main Scene. It also shows Frame linking among views. 
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscenedroi.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import android.view.MotionEvent;

DroidScene scene, auxScene;
PGraphics canvas, auxCanvas;	
InteractiveFrame frame1, auxFrame1, frame2, auxFrame2, frame3, auxFrame3;	
boolean drawHints = false;

//Choose one of P3D for a 3D scene, or P2D for a 2D scene
String renderer = P2D;

public void setup() {
  size(displayWidth, displayHeight, renderer);
  canvas = createGraphics(displayWidth, displayHeight/2, renderer);
  canvas.smooth();
  scene = new DroidScene(this, canvas);
  scene.addDrawHandler(this, "mainDrawing");
  frame1 = new InteractiveFrame(scene);
  frame1.translate(new Vec(30, 30));
  frame2 = new InteractiveFrame(scene);
  frame2.setReferenceFrame(frame1);
  frame2.translate(new Vec(40, 0, 0));
  frame3 = new InteractiveFrame(scene);
  frame3.setReferenceFrame(frame2);
  frame3.translate(new Vec(40, 0, 0));

  auxCanvas = createGraphics(displayWidth, displayHeight/2, renderer);
  auxCanvas.smooth();
  // Note that we pass the upper left corner coordinates where the scene
  // is to be drawn (see drawing code below) to its constructor.
  auxScene = new DroidScene(this, auxCanvas, 0, displayHeight/2);
  auxScene.addDrawHandler(this, "auxDrawing");
  auxScene.setRadius(200);
  auxScene.showAll();

  auxFrame1 = new InteractiveFrame(auxScene);
  auxFrame1.linkTo(frame1);

  auxFrame2 = new InteractiveFrame(auxScene);
  auxFrame2.linkTo(frame2);

  auxFrame3 = new InteractiveFrame(auxScene);
  auxFrame3.linkTo(frame3);

  handleMouse();
  smooth();
}

public void draw() {
  handleMouse();
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(0);
  scene.endDraw();
  canvas.endDraw();
  image(canvas, 0, 0);

  auxCanvas.beginDraw();
  auxScene.beginDraw();
  auxCanvas.background(0);		
  auxScene.endDraw();
  auxCanvas.endDraw();

  // We retrieve the scene upper left coordinates defined above.
  image(auxCanvas, auxScene.upperLeftCorner.x(), auxScene.upperLeftCorner.y());
}

public boolean dispatchTouchEvent(MotionEvent event) {
  //Llama el metodo para controlar el agente
  scene.surfaceTouchEvent(event);
  return super.dispatchTouchEvent(event);        // pass data along when done!
}

public void mainDrawing(Scene s) {				
  s.pg().pushStyle();
  s.pushModelView();
  if (s == scene)
    frame1.applyTransformation();
  else
    auxFrame1.applyTransformation();		
  if (drawHints)
    s.drawAxes(40);
  if (drawHints && frame1.grabsInput(scene.motionAgent())) {
    s.pg().fill(255, 0, 0);
    s.pg().rect(0, 0, 40, 10, 5);
  }
  else {
    s.pg().fill(0, 0, 255);
    s.pg().rect(0, 0, 40, 10, 5);
  }

  s.pushModelView();
  if (s == scene)
    frame2.applyTransformation();
  else
    auxFrame2.applyTransformation();
  if (drawHints)
    s.drawAxes(40);
  if (drawHints && frame2.grabsInput(scene.motionAgent())) {
    s.pg().fill(255, 0, 0);
    s.pg().rect(0, 0, 40, 10, 5);
  }
  else {
    s.pg().fill(255, 0, 255);
    s.pg().rect(0, 0, 40, 10, 5);
  }		

  s.pushModelView();
  if (s == scene)
    frame3.applyTransformation();
  else
    auxFrame3.applyTransformation();
  if (drawHints)
    s.drawAxes(40);
  if (drawHints && frame3.grabsInput(scene.motionAgent())) {
    s.pg().fill(255, 0, 0);
    s.pg().rect(0, 0, 40, 10, 5);
  }
  else {
    s.pg().fill(0, 255, 255);
    s.pg().rect(0, 0, 40, 10, 5);
  }		
  s.popModelView();

  s.popModelView();

  s.popModelView();
  s.pg().popStyle();
}

public void auxDrawing(Scene s) {
  mainDrawing(s);		

  s.pg().pushStyle();
  s.pg().stroke(255, 255, 0);
  s.pg().fill(255, 255, 0, 160);
  s.drawEye(scene.eye());
  s.pg().popStyle();
}

public void handleMouse() {
  if (mouseY < displayHeight/2) {
    scene.enableMotionAgent();
    scene.enableKeyboardAgent();
    auxScene.enableMotionAgent();
    auxScene.disableKeyboardAgent();
  } 
  else {
    scene.disableMotionAgent();
    scene.disableKeyboardAgent();
    auxScene.disableMotionAgent();
    auxScene.enableKeyboardAgent();
  }
}

public void printFrame(Frame frame) {
  println("Translation: " + frame.translation());
  println("Angle: " + frame.rotation().angle());
  println("Scaling: " + frame.scaling());
}

public void keyPressed() {
  if (key == 'u' || key == 'U') {
    drawHints = !drawHints;
  }
  if (key == 'v' || key == 'V') {
    scene.flip();
  }
}
