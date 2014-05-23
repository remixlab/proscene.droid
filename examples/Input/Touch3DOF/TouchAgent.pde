public class TouchAgent extends JoystickAgent {
    Scene scene;
    DOF3Event event, prevEvent;
    float histDistance;
    public TouchAgent(Scene scn, String n) {
      super(scn, n);
      this.enableTracking();
      scene = scn;
      eyeProfile().setBinding(DOF3Action.ROTATE);
      frameProfile().setBinding(DOF3Action.ROTATE);
      eyeProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, DOF3Action.TRANSLATE_XYZ);
      frameProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, DOF3Action.TRANSLATE_XYZ);
      //eyeProfile().setBinding(B_NOMODIFIER_MASK, B_CENTER, scene.is3D() ? DOF3Action.ZOOM : DOF3Action.SCALE);
      
      
    }

    public void addTouCursor(MotionEvent tcur) {
      
      event = new DOF3Event(prevEvent, 
      tcur.getX(), 
      tcur.getY(),
      0,
      B_NOMODIFIER_MASK, 
      B_NOBUTTON);
      updateTrackedGrabber(event);
      prevEvent = event.get();
      
    }

    // called when a cursor is moved
    public void updateTouCursor(MotionEvent tcur) {
      event = new DOF3Event(prevEvent, 
      tcur.getX(), 
      tcur.getY(),
      0, 
      B_NOMODIFIER_MASK, 
      B_NOBUTTON);
      handle(event);
      prevEvent = event.get();
    }

    // called when a cursor is removed from the scene
    public void removeTouCursor(MotionEvent tcur) {
      event = new DOF3Event(prevEvent, 
      tcur.getX(), 
      tcur.getY(),
      0,  
      B_NOMODIFIER_MASK, 
      B_NOBUTTON);
      prevEvent = event.get();
      disableTracking();
      enableTracking();
      histDistance = 0f;
    }
    
    //tcur.getX(0),tcur.getY(0)
    public void transalateTouCursor(MotionEvent tcur) {
      float distance;
      if (histDistance == 0){
        distance = 0;
      }else{
        distance = histDistance - sqrt((tcur.getX(0) - tcur.getX(1))*(tcur.getX(0) - tcur.getX(1)) + (tcur.getY(0) - tcur.getY(1))*(tcur.getY(0) - tcur.getY(1)));
      }
      
      event = new DOF3Event(prevEvent, 
      tcur.getX(0),
      tcur.getY(0),      
      distance, 
      B_NOMODIFIER_MASK, 
      B_CENTER);
      histDistance = sqrt((tcur.getX(0) - tcur.getX(1))*(tcur.getX(0) - tcur.getX(1)) + (tcur.getY(0) - tcur.getY(1))*(tcur.getY(0) - tcur.getY(1)));
      handle(event);
      prevEvent = event.get();
    }
    
}

