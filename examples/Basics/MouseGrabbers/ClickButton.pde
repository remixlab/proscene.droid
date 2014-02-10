import processing.core.PVector;
import remixlab.proscene.Scene;
import remixlab.tersehandling.event.*;
import remixlab.tersehandling.generic.event.GenericClickEvent;

public class ClickButton extends Button2D {
  boolean addBox;

  public ClickButton(Scene scn, PVector p, String t, boolean addB) {
    super(scn, p, t);
    addBox = addB;
  }

  @Override
  public void performInteraction(TerseEvent event) {
    if (event instanceof ClickEvent)
      if (((ClickEvent) event).clickCount() == 1) {
        if (addBox)
          ((MouseGrabbers)parent).addBox();
        else
          ((MouseGrabbers)parent).removeBox();
      }
  }
}
