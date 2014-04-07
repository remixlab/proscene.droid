public class TouchAgent extends MouseAgent {
    Scene scene;
    DOF2Event event, prevEvent;

    public TouchAgent(Scene scn, String n) {
      super(scn, n);
      this.enableTracking();
      scene = scn;
      eyeProfile().setBinding(DOF2Action.ROTATE);
      frameProfile().setBinding(DOF2Action.TRANSLATE);
    }

    public void addTouCursor(MotionEvent tcur) {
      event = new DOF2Event(prevEvent, 
      tcur.getX(), 
      tcur.getY(), 
      B_NOMODIFIER_MASK, 
      B_NOBUTTON);
      updateTrackedGrabber(event);
      prevEvent = event.get();
    }

    // called when a cursor is moved
    public void updateTouCursor(MotionEvent tcur) {
      event = new DOF2Event(prevEvent, 
      tcur.getX(), 
      tcur.getY(), 
      B_NOMODIFIER_MASK, 
      B_NOBUTTON);
      handle(event);
      prevEvent = event.get();
    }

    // called when a cursor is removed from the scene
    public void removeTouCursor(MotionEvent tcur) {
      event = new DOF2Event(prevEvent, 
      tcur.getX(), 
      tcur.getY(),  
      B_NOMODIFIER_MASK, 
      B_NOBUTTON);
      prevEvent = event.get();
      disableTracking();
      enableTracking();
    }
}
