/**
 * Alternative Use.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to use proscene through inheritance.
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscenedroi.*;
import remixlab.proscene.*;
import android.view.MotionEvent;

MyScene scene;
//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P2D;

void setup() {
  //size(640, 360, renderer);
  // We instantiate our MyScene class defined below
  scene = new MyScene(this);
}

public String sketchRenderer() {
  return P3D; 
}

// Make sure to define the draw() method, even if it's empty.
void draw() {
}

public boolean dispatchTouchEvent(MotionEvent event) {
  //Llama el metodo para controlar el agente
  scene.surfaceTouchEvent(event);
  return super.dispatchTouchEvent(event);        // pass data along when done!
}

class MyScene extends DroidScene {
  // We need to call super(p) to instantiate the base class
  public MyScene(PApplet p) {
    super(p);
  }

  // Initialization stuff could have also been performed at
  // setup(), once after the Scene object have been instantiated 
  public void init() {
    setGridVisualHint(false);
  }

  //Define here what is actually going to be drawn.
  public void proscenium() {
    background(0);
    fill(204, 102, 0, 150);
    drawTorusSolenoid();
  }
}
