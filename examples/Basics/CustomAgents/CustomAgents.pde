import remixlab.tersehandling.core.*;
import remixlab.tersehandling.generic.agent.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;
import remixlab.tersehandling.event.*;
import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;

public class MouseAgent extends GenericMotionAgent<GenericMotionProfile<MotionAction>, GenericClickProfile<ClickAction>> implements EventConstants {
  GenericDOF2Event<MotionAction> event, prevEvent;
  public MouseAgent(TerseHandler scn, String n) {
    super(new GenericMotionProfile<MotionAction>(), 
    new GenericClickProfile<ClickAction>(), scn, n);
    //default bindings
    clickProfile().setClickBinding(TH_LEFT, 1, ClickAction.CHANGE_COLOR);
    clickProfile().setClickBinding(TH_META, TH_RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
    clickProfile().setClickBinding((TH_META | TH_SHIFT), TH_RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
    profile().setBinding(TH_LEFT, MotionAction.CHANGE_POSITION);
    profile().setBinding(TH_SHIFT, TH_LEFT, MotionAction.CHANGE_SHAPE);
    profile().setBinding(TH_META, TH_RIGHT, MotionAction.CHANGE_SHAPE);
  }

  public void mouseEvent(processing.event.MouseEvent e) {      
    if ( e.getAction() == processing.event.MouseEvent.MOVE ) {
      event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(), e.getY(),e.getModifiers(), e.getButton());
      updateGrabber(event);
      prevEvent = event.get();
    }
    if ( e.getAction() == processing.event.MouseEvent.DRAG ) {
      event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(), e.getY(), e.getModifiers(), e.getButton());
      handle(event);
      prevEvent = event.get();
    }
    if ( e.getAction() == processing.event.MouseEvent.CLICK ) {
      handle(new GenericClickEvent<ClickAction>(e.getX(), e.getY(), e.getModifiers(), e.getButton(), e.getCount()));
    }
  }
}

public class GrabbableCircle extends AbstractGrabber {
  public float radiusX, radiusY;
  public PVector center;
  public color colour;
  public int contourColour;
  public int sWeight;

  public GrabbableCircle(Agent agent) {
    agent.addInPool(this);
    setColor();
    setPosition();
    sWeight = 4;
    contourColour = color(0, 0, 0);
  }

  public GrabbableCircle(Agent agent, PVector c, float r) {
    agent.addInPool(this);
    radiusX = r;
    radiusY = r;
    center = c;    
    setColor();
    sWeight = 4;
  }

  public void setColor() {
    setColor(color(random(0, 255), random(0, 255), random(0, 255)));
  }

  public void setColor(color myC) {
    colour = myC;
  }

  public void setPosition(float x, float y) {
    setPositionAndRadii(new PVector(x, y), radiusX, radiusY);
  }

  public void setPositionAndRadii(PVector p, float rx, float ry) {
    center = p;
    radiusX = rx;
    radiusY = ry;
  }

  public void setPosition() {
    float maxRadius = 50;
    float low = maxRadius;
    float highX = w - maxRadius;
    float highY = h - maxRadius;
    float r = random(20, maxRadius);
    setPositionAndRadii(new PVector(random(low, highX), random(low, highY)), r, r);
  }

  public void draw() {
    draw(colour);
  }

  public void draw(int c) {
    pushStyle();
    stroke(contourColour);
    strokeWeight(sWeight);
    fill(c);
    ellipse(center.x, center.y, 2*radiusX, 2*radiusY);
    popStyle();
  }

  @Override
  public boolean checkIfGrabsInput(TerseEvent event) {
    if (event instanceof GenericDOF2Event) {
      float x = ((GenericDOF2Event<?>)event).x();
      float y = ((GenericDOF2Event<?>)event).y();
      return(pow((x - center.x), 2)/pow(radiusX, 2) + pow((y - center.y), 2)/pow(radiusY, 2) <= 1);
    }      
    return false;
  }

  @Override
  public void performInteraction(TerseEvent event) {
    if (event instanceof Duoable) {
      switch ((GlobalAction) ((Duoable<?>)event).action().referenceAction()) {
        case CHANGE_COLOR:
        contourColour = color(random(100, 255), random(100, 255), random(100, 255));
        break;
      case CHANGE_STROKE_WEIGHT:
        if (event.isShiftDown()) {					
          if (sWeight > 1)
            sWeight--;
        }
        else			
          sWeight++;		
        break;
      case CHANGE_POSITION:
        setPosition( ((GenericDOF2Event<?>)event).x(), ((GenericDOF2Event<?>)event).y() );
        break;
        case CHANGE_SHAPE:
        radiusX += ((GenericDOF2Event<?>)event).dx();
        radiusY += ((GenericDOF2Event<?>)event).dy();
        break;
      }
    }
  }
}

int w = 600;
int h = 600;
MouseAgent agent;
GrabbableCircle [] circles;
boolean drawSelectionHints = false;
Scene scene;
PFont font;

void setup() {
  size(w, h);
  scene = new Scene(this);
  scene.setAxisVisualHint(false);
  scene.setGridVisualHint(false);
  scene.setRadius(min(w,h)/2);
  scene.setCenter(new Vec(w/2,h/2));
  scene.showAll();
  agent = new MouseAgent(scene.terseHandler(), "my_mouse");
  circles = new GrabbableCircle[10];
  for (int i = 0; i < circles.length; i++)
    circles[i] = new GrabbableCircle(agent);
  scene.terseHandler().unregisterAgent(agent);
  font = loadFont("FreeSans-16.vlw");
  textFont(font);
}

void draw() {
  background(120);
  for (int i = 0; i < circles.length; i++) {
    if ( circles[i].grabsAgent(agent) )
      circles[i].draw(color(255, 0, 0));
    else
      circles[i].draw();
  }
  scene.beginScreenDrawing();
  if(scene.isDefaultMouseAgentEnabled()) {
    fill(255,0,0);
    text("Proscene's default mouse agent can handle your eye, but not your custom actions", 5, 17);
  }
  else {
    fill(0,255,0);
    text("Your agent can handle your custom actions, but not your aye", 5, 17);
    text("Press 'v' to toggle the display of the circle positions displacement due to the eye", 5, 37);
  }
  fill(0,0,255);
  text("Press 'u' to change the mouse agent", 5, 57);
  scene.endScreenDrawing();
  if(drawSelectionHints && !scene.isDefaultMouseAgentEnabled()) drawSelectionHints();
}

void keyPressed() {
  if(key == 'u') {
    if(scene.isDefaultMouseAgentEnabled()) {
      scene.disableDefaultMouseAgent();
      scene.terseHandler().registerAgent(agent);
      registerMethod("mouseEvent", agent);
    } else {
      scene.enableDefaultMouseAgent();
      scene.terseHandler().unregisterAgent(agent);
      unregisterMethod("mouseEvent", agent);
    }
  }
  if(key=='v')
    drawSelectionHints = !drawSelectionHints;
}

void drawSelectionHints() {
  scene.beginScreenDrawing();
  for (int i = 0; i < circles.length; i++) {
    color c = circles[i].colour;
    circles[i].draw(color(red(c), green(c), blue(c), 100));
  }
  scene.endScreenDrawing();
  pushMatrix();
  translate(w/2,h/2);
  scene.drawAxis();
  popMatrix();
}