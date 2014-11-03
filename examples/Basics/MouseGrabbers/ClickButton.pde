public class ClickButton extends Button2D {
  boolean addBox;

  public ClickButton(Scene scn, PVector p, PFont font, String t, boolean addB) {
    super(scn, p, font, t);
    addBox = addB;
  }

  public void performInteraction(BogusEvent event) {
    println("PERFORM");
    if (event instanceof ClickEvent){
      if (((ClickEvent) event).clickCount() == 1) {
        if (addBox)
          ((MouseGrabbers)parent).addTorus();
        else
          ((MouseGrabbers)parent).removeTorus();
      }
    }
  }
}
