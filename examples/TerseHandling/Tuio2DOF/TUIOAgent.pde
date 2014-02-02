public class TUIOAgent extends MouseAgent {
  Scene scene;
  GenericDOF2Event<DOF2Action> event, prevEvent;

  public TUIOAgent(Scene scn, String n) {
    super(scn, n);
    this.enableTracking();
    scene = scn;
    cameraProfile().setBinding(DOF2Action.ROTATE);
    //cameraProfile().setBinding(DOF2Action.TRANSLATE);
    //frameProfile().setBinding(DOF2Action.ROTATE);
    frameProfile().setBinding(DOF2Action.TRANSLATE);
  }

  public void addTuioCursor(TuioCursor tcur) {
    event = new GenericDOF2Event<DOF2Action>(prevEvent, 
    tcur.getScreenX(scene.width()), 
    tcur.getScreenY(scene.height()), 
    TH_NOMODIFIER_MASK, 
    TH_NOBUTTON);
    updateGrabber(event);
    prevEvent = event.get();
  }

  // called when a cursor is moved
  public void updateTuioCursor(TuioCursor tcur) {
    event = new GenericDOF2Event<DOF2Action>(prevEvent, 
    tcur.getScreenX(scene.width()), 
    tcur.getScreenY(scene.height()), 
    TH_NOMODIFIER_MASK, 
    TH_NOBUTTON);
    handle(event);
    prevEvent = event.get();
  }

  // called when a cursor is removed from the scene
  public void removeTuioCursor(TuioCursor tcur) {
    event = new GenericDOF2Event<DOF2Action>(prevEvent, 
    tcur.getScreenX(scene.width()), 
    tcur.getScreenY(scene.height()), 
    TH_NOMODIFIER_MASK, 
    TH_NOBUTTON);
    prevEvent = event.get();
    disableTracking();
    enableTracking();
  }
}
