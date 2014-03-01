import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.bogusinput.core.*;
import remixlab.bogusinput.event.*;

Scene scene;
ArrayList boxes;
Button2D button1, button2;
int myColor;

public void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);

  // scene.setShortcut('f',
  // Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
  PFont buttonFont = loadFont("FreeSans-36.vlw");
  button1 = new ClickButton(scene, new PVector(10, 10), buttonFont, "+", true);
  button2 = new ClickButton(scene, new PVector(16, (2 + button1.myHeight)), buttonFont, "-", false);

  scene.setGridVisualHint(true);
  scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
  scene.setRadius(150);
  scene.showAll();

  myColor = 125;
  boxes = new ArrayList();
  addBox();
}

public void draw() {
  background(0);
  for (int i = 0; i < boxes.size(); i++) {
    Box box = (Box) boxes.get(i);
    box.draw(true);
  }
  button1.display();
  button2.display();
}

public void addBox() {
  Box box = new Box(scene);
  box.setSize(20, 20, 20);
  box.setColor(color(0, 0, 255));
  boxes.add(box);
}

public void removeBox() {
  if (boxes.size() > 0) {
    scene.inputHandler().removeFromAllAgentPools(((Box) boxes.get(0)).iFrame);
    boxes.remove(0);
  }
}
