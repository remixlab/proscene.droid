import remixlab.proscene.Scene;
Scene scene;
float x,y,z;
Box [] boxes;

void setup() {
  boxes = new Box[10];
  //size(640, 640, P3D);
  scene = new Scene(this);
  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box(scene);
    
  frameRate(100);
}

public String sketchRenderer() {
  return P3D; 
}

void draw() {
  background(0);
  lights();
  
  scene.beginScreenDrawing();  
  text(frameRate, 5, 17);
  scene.endScreenDrawing();
  for (int i = 0; i < boxes.length; i++)      
    boxes[i].draw();
 
}

public boolean dispatchTouchEvent(android.view.MotionEvent event) {
  //Llama el metodo para controlar el agente
  ((DroidTouchAgent)scene.motionAgent()).touchEvent(event);
  return super.dispatchTouchEvent(event);        // pass data along when done!
}
