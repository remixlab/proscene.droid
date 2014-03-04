import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

Scene scene;
ArrayList toruses;
Button2D button1, button2;
int myColor;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

public void setup() {
  size(640, 360, renderer);
  scene = new Scene(this);

  // scene.setShortcut('f',
  // Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
  PFont buttonFont = loadFont("FreeSans-36.vlw");
  button1 = new ClickButton(scene, new PVector(10, 10), buttonFont, "+", true);
  button2 = new ClickButton(scene, new PVector(16, (2 + button1.myHeight)), buttonFont, "-", false);

  scene.setGridVisualHint(true);
  if(scene.is3D()) scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
  scene.setRadius(150);
  scene.showAll();

  myColor = 125;
  toruses = new ArrayList();
  addTorus();
}

public void draw() {
  background(0);
  for (int i = 0; i < toruses.size(); i++) {
    InteractiveTorus box = (InteractiveTorus) toruses.get(i);
    box.draw(true);
  }
  button1.display();
  button2.display();
}

public void addTorus() {
  InteractiveTorus iTorus = new InteractiveTorus(scene);
  iTorus.setColor(color(0, 0, 255));
  toruses.add(iTorus);
}

public void removeTorus() {
  if (toruses.size() > 0) {
    scene.inputHandler().removeFromAllAgentPools(((InteractiveTorus) toruses.get(0)).iFrame);
    toruses.remove(0);
  }
}
