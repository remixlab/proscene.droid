import remixlab.proscene.Scene;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.event.DOF2Event;
import remixlab.tersehandling.event.TerseEvent;

public abstract class Button2D extends AbstractGrabber {
  public Scene scene;  
  public PApplet parent;
  String myText;
  PFont myFont;
  public int myWidth;
  public int myHeight;
  PVector position;

  public Button2D(Scene scn, PVector p, int fSize) {
    this(scn, p, "", fSize);
  }

  public Button2D(Scene scn, PVector p, String t, int fSize) {
    scene = scn;
    parent = scene.pApplet();
    position = p;
    myText = t;
    myFont = parent.createFont("FFScala", fSize);
    parent.textFont(myFont);
    parent.textAlign(PApplet.LEFT);
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
    if (grabsAgent(scene.defaultMouseAgent()))
      parent.fill(255);
    else
      parent.fill(100);
    scene.beginScreenDrawing();
    parent.text(myText, position.x, position.y, myWidth, myHeight);
    scene.endScreenDrawing();
    parent.popStyle();
  }

  @Override
    public boolean checkIfGrabsInput(TerseEvent event) {
    if (event instanceof DOF2Event) {
      float x = ((DOF2Event)event).x();
      float y = ((DOF2Event)event).y();
      return ((position.x <= x) && (x <= position.x + myWidth) && (position.y <= y) && (y <= position.y + myHeight));
    }
    else
      return false;
  }
}
