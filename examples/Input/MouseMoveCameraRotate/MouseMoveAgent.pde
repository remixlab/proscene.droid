public class MouseMoveAgent extends MouseAgent {
  ActionDOF2Event<Constants.DOF2Action> event, prevEvent;
  public MouseMoveAgent(AbstractScene scn, String n) {
    super(scn, n);
    // agents creation registers it at the inputHandler.
    // we unregister it here, keeping the default mouse agent
    inputHandler().unregisterAgent(this);
    // while camera rotation requires no mouse button press:
    eyeProfile().setBinding(DOF2Action.ROTATE); // -> MouseEvent.MOVE
    // camera translation requires a mouse left button press:
    eyeProfile().setBinding(B_LEFT, DOF2Action.TRANSLATE); // -> MouseEvent.DRAG
    // Disable center and right button camera actions (inherited from MouseAgent):
    eyeProfile().setBinding(B_META, B_RIGHT, null);
    eyeProfile().setBinding(B_ALT, B_CENTER, null);
  }
  public void mouseEvent(processing.event.MouseEvent e) {
    //don't even necessary :P
    //if( e.getAction() == processing.event.MouseEvent.MOVE || e.getAction() == processing.event.MouseEvent.DRAG) {
    event = new ActionDOF2Event<Constants.DOF2Action>(prevEvent, e.getX() - scene.upperLeftCorner.x(), e.getY() - scene.upperLeftCorner.y(), e.getModifiers(), e.getButton());
    handle(event);
    prevEvent = event.get();
    //}
  }
}
