public class TUIOAgent extends MouseAgent {
  Scene scene;
  DOF2Event event, prevEvent;

  public TUIOAgent(Scene scn, String n) {
    super(scn, n);
    this.enableTracking();
    scene = scn;
    eyeProfile().setBinding(DOF2Action.ROTATE);
    //eyeProfile().setBinding(DOF2Action.TRANSLATE);
    //frameProfile().setBinding(DOF2Action.ROTATE);
    frameProfile().setBinding(DOF2Action.TRANSLATE);
  }

  public void addTuioCursor(TuioCursor tcur) {
    event = new DOF2Event(prevEvent, 
    tcur.getScreenX(scene.width()), 
    tcur.getScreenY(scene.height()), 
    B_NOMODIFIER_MASK, 
    B_NOBUTTON);
    updateTrackedGrabber(event);
    prevEvent = event.get();
  }

  // called when a cursor is moved
  public void updateTuioCursor(TuioCursor tcur) {
    event = new DOF2Event(prevEvent, 
    tcur.getScreenX(scene.width()), 
    tcur.getScreenY(scene.height()), 
    B_NOMODIFIER_MASK, 
    B_NOBUTTON);
    handle(event);
    prevEvent = event.get();
  }

  // called when a cursor is removed from the scene
  public void removeTuioCursor(TuioCursor tcur) {
    event = new DOF2Event(prevEvent, 
    tcur.getScreenX(scene.width()), 
    tcur.getScreenY(scene.height()), 
    B_NOMODIFIER_MASK, 
    B_NOBUTTON);
    prevEvent = event.get();
    disableTracking();
    enableTracking();
  }
}
