/**
 * Combo 2D
 * by Jean Pierre Charalambos.
 *
 * Doc to come...
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

Scene scene, auxScene;
PGraphics canvas, auxCanvas;	
InteractiveFrame frame1, auxFrame1, frame2, auxFrame2, frame3, auxFrame3;	
boolean drawHints = false;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = JAVA2D;

public void setup() {
  size(640, 720, renderer);
  canvas = createGraphics(640, 360, renderer);
  canvas.smooth();
  scene = new Scene(this, canvas);
  scene.addDrawHandler(this, "mainDrawing");
  frame1 = new InteractiveFrame(scene);
  frame1.translate(new Vec(30, 30));
  frame2 = new InteractiveFrame(scene);
  frame2.setReferenceFrame(frame1);
  frame2.translate(new Vec(40, 0, 0));
  frame3 = new InteractiveFrame(scene);
  frame3.setReferenceFrame(frame2);
  frame3.translate(new Vec(40, 0, 0));

  auxCanvas = createGraphics(640, 360, renderer);
  auxCanvas.smooth();
  // Note that we pass the upper left corner coordinates where the scene
  // is to be drawn (see drawing code below) to its constructor.
  auxScene = new Scene(this, auxCanvas, 0, 360);
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

public void mainDrawing(Scene s) {				
  s.pg().pushStyle();
  s.pushModelView();
  if (s == scene)
    frame1.applyTransformation();
  else
    auxFrame1.applyTransformation();		
  if (drawHints)
    s.drawAxis(40);
  if (drawHints && frame1.grabsAgent(scene.defaultMouseAgent())) {
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
    s.drawAxis(40);
  if (drawHints && frame2.grabsAgent(scene.defaultMouseAgent())) {
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
    s.drawAxis(40);
  if (drawHints && frame3.grabsAgent(scene.defaultMouseAgent())) {
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
  if (mouseY < 360) {
    scene.enableDefaultMouseAgent();
    scene.enableDefaultKeyboardAgent();
    auxScene.disableDefaultMouseAgent();
    auxScene.disableDefaultKeyboardAgent();
  } 
  else {
    scene.disableDefaultMouseAgent();
    scene.disableDefaultKeyboardAgent();
    auxScene.enableDefaultMouseAgent();
    auxScene.enableDefaultKeyboardAgent();
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
    scene.eye().flip();
  }
}
