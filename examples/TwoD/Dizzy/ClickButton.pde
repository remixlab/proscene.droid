import processing.core.*;
import remixlab.proscene.*;
import remixlab.tersehandling.event.ClickEvent;
import remixlab.tersehandling.event.TerseEvent;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

public class ClickButton extends Button2D {
  int path;

  public ClickButton(Scene scn, PVector p, int index) {
    this(scn, p, "", index);
  }

  public ClickButton(Scene scn, PVector p, String t, int index) {
    super(scn, p, t);
    path = index;
  }

  @Override
  public void performInteraction(TerseEvent event) {
    if (event instanceof ClickEvent)
      if (((ClickEvent) event).clickCount() == 1)
        if (path == 0)
          scene.togglePathsVisualHint();
        else
          scene.eye().playPath(path);
  }

  public void display() {
    String text = new String();
    if (path == 0)
      if (scene.pathsVisualHint())
        text = "don't edit camera paths";
      else
        text = "edit camera paths";
    else {
      if (grabsAgent(scene.defaultMouseAgent())) {
        if (scene.eye().keyFrameInterpolator(path)
          .numberOfKeyFrames() > 1)
          if (scene.eye().keyFrameInterpolator(path)
            .interpolationIsStarted())
            text = "stop path ";
          else
            text = "play path ";
        else
          text = "restore position ";
      } 
      else {
        if (scene.eye().keyFrameInterpolator(path)
          .numberOfKeyFrames() > 1)
          text = "path ";
        else
          text = "position ";
      }
      text += ((Integer) path).toString();
    }
    setText(text);
    super.display();
  }
}
