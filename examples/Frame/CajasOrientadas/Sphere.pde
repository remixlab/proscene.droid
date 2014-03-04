/**
 * Esfera. 
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the Cajas Orientadas example.
 *
 * Any object that needs to be "pickable" (such as the Esfera), should be
 * attached to its own InteractiveFrame. That's all there is to it.
 *
 * The built-in picking proscene mechanism actually works as follows. At
 * instantiation time all InteractiveFrame objects are added to a mouse
 * grabber pool. Scene parses this pool every frame to check if the mouse
 * grabs a InteractiveFrame by projecting its origin onto the screen.
 * If the mouse position is close enough to that projection (default
 * implementation defines a 10x10 pixel square centered at it), the object
 * will be picked.
 *
 * Override InteractiveFrame.checkIfGrabsMouse if you need a more
 * sophisticated picking mechanism.
 */

public class Sphere {
  Scene scene;
  PApplet parent;
  InteractiveFrame iFrame;
  float r;
  int c;

  public Sphere(Scene scn, InteractiveFrame iF) {
    scene = scn;
    parent = scn.pApplet();
    iFrame = iF;
    setRadius(10);
  }

  public Sphere(Scene scn) {
    scene = scn;
    parent = scn.pApplet();
    iFrame = new InteractiveFrame(scn);
    setRadius(10);
  }

  public void draw() {
    draw(true);
  }

  public void draw(boolean drawAxis) {
    parent.pushMatrix();
    //iFrame.applyTransformation(parent);
    iFrame.applyTransformation(scene);

    if (drawAxis)
      //DrawingUtils.drawAxis(parent, radius()*1.3f);
      scene.drawAxis(radius()*1.3f);
    if (scene.grabsAnAgent(iFrame)) {
      parent.fill(255, 0, 0);
      parent.sphere(radius()*1.2f);
    }
    else {
      parent.fill(getColor());
      parent.sphere(radius());
    }    
    parent.popMatrix();
  }

  public float radius() {
    return r;
  }

  public void setRadius(float myR) {
    r = myR;
  }

  public int getColor() {
    return c;
  }

  public void setColor() {
    c = color(parent.random(0, 255), parent.random(0, 255), parent.random(0, 255));
  }

  public void setColor(int myC) {
    c = myC;
  }

  public void setPosition(Vec pos) {
    iFrame.setPosition(pos);
  }

  public Vec getPosition() {
    return iFrame.position();
  }
}
