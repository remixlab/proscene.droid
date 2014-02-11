public class Box {
  InteractiveFrame iFrame;
  float w, h, d;
  int c;
  Scene scene;
  MouseGrabbers parent;

  Box(Scene scn) {
    scene = scn;
    parent = (MouseGrabbers) scene.pApplet();
    iFrame = new InteractiveFrame(scene);
    setSize();
    setColor();
    setPosition();
  }

  // don't draw local axis
  public void draw() {
    draw(false);
  }

  public void draw(boolean drawAxis) {
    parent.pushMatrix();
    parent.pushStyle();
    // Multiply matrix to get in the frame coordinate system.
    // scene.parent.applyMatrix(iFrame.matrix()) is handy but inefficient
    iFrame.applyTransformation(); // optimum
    if (drawAxis)
      scene.drawAxis(PApplet.max(w, h, d) * 1.3f);
    parent.noStroke();

    parent.fill(255, 0, 0);

    if (iFrame.grabsAgent(scene.defaultMouseAgent()) )
      parent.fill(255, 0, 0);
    else
      parent.fill(getColor());

    // Draw a box
    parent.box(w, h, d);
    parent.popStyle();
    parent.popMatrix();
  }

  // sets size randomly
  public void setSize() {
    w = parent.random(10, 40);
    h = parent.random(10, 40);
    d = parent.random(10, 40);
  }

  public void setSize(float myW, float myH, float myD) {
    w = myW;
    h = myH;
    d = myD;
  }

  public int getColor() {
    return c;
  }

  // sets color randomly
  public void setColor() {
    c = color(parent.random(0, 255), parent.random(0, 255), parent.random(0, 255));
  }

  public void setColor(int myC) {
    c = myC;
  }

  public Vec getPosition() {
    return iFrame.position();
  }

  // sets position randomly
  public void setPosition() {
    float low = -100;
    float high = 100;
    iFrame.setPosition(new Vec(parent.random(low, high), 
    parent.random(low, high), 
    parent.random(low, high)));
  }

  public void setPosition(Vec pos) {
    iFrame.setPosition(pos);
  }

  public Quat getOrientation() {
    return (Quat)iFrame.orientation();
  }

  public void setOrientation(Vec v) {
    Vec to = Vec.subtract(v, iFrame.position());
    iFrame.setOrientation(new Quat(new Vec(0, 1, 0), to));
  }
}
