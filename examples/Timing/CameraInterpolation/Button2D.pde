/**
 * Button 2D.
 * by Jean Pierre Charalambos.
 * 
 * Base class of "2d buttons" that shows how simple is to implement
 * a MouseGrabber which can enable complex mouse interactions.
 */

public abstract class Button2D extends GrabberObject {
  public Scene scene;  
  public PApplet parent;
  String myText;
  PFont myFont;
  public int myWidth;
  public int myHeight;
  PVector position;

  public Button2D(Scene scn, PVector p, PFont font) {
    this(scn, p, font, "");
  }

  public Button2D(Scene scn, PVector p, PFont font, String t) {
    scene = scn;
    parent = scene.pApplet();
    position = p;
    myText = t;
    myFont = font;
    textFont(myFont);
    textAlign(LEFT);
    setText(t);
    scene.defaultMouseAgent().addInPool(this);
  }

  public void setText(String text) {
    myText = text;
    myWidth = (int) parent.textWidth(myText);
    myHeight = (int) (parent.textAscent() + parent.textDescent());
  }

  public void display() {
    parent.pushStyle();    
    parent.fill(255);
    if (grabsInput(scene.defaultMouseAgent()))
      parent.fill(255);
    else
      parent.fill(100);
    scene.beginScreenDrawing();
    parent.text(myText, position.x, position.y, myWidth, myHeight);
    scene.endScreenDrawing();
    parent.popStyle();
  }

  @Override
  public boolean checkIfGrabsInput(BogusEvent event) {
    if (event instanceof DOF2Event) {
      float x = ((DOF2Event)event).x();
      float y = ((DOF2Event)event).y();
      return ((position.x <= x) && (x <= position.x + myWidth) && (position.y <= y) && (y <= position.y + myHeight));
    }
    else
      return false;
  }
}