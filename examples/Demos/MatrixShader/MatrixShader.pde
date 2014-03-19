/**
 * Matrix Shader
 * by Jean Pierre Charalambos.
 *
 * Doc to come...
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;
import remixlab.dandelion.geom.*;

Scene scene;
boolean focusIFrame;
InteractiveFrame iFrame;
PShader prosceneShader;
Mat pmv;
PMatrix3D pmatrix = new PMatrix3D( );

void setup() {
  size(640, 360, P3D);
  prosceneShader = loadShader("FrameFrag.glsl", "FrameVert_pmv.glsl");
  scene = new Scene(this);
  scene.setMatrixHelper(new MatrixStackHelper(scene));
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30, 0));
}

void draw() {
  background(0);
  //discard Processing matrices at all
  resetMatrix();
  //set initial model-view and projection proscene matrices
  setUniforms();
  fill(204, 102, 0);
  box(20, 30, 40);
  scene.pushModelView();
  scene.applyModelView(iFrame.matrix());
  //iFrame.applyTransformation();//also possible here
  //model-view changed:
  setUniforms();
  if (focusIFrame) {
    fill(0, 255, 255);
    box(12, 17, 22);
  }
  else if (iFrame.grabsInput(scene.mouseAgent())) {
    fill(255, 0, 0);
    box(12, 17, 22);
  }
  else {
    fill(0, 0, 255);
    box(10, 15, 20);
  } 
  scene.popModelView();
}

//Whenever the model-view (or projection) matrices changes
// we need to update the shader:
void setUniforms() {
  shader(prosceneShader);
  pmv = Mat.multiply(scene.projection(), scene.modelView());
  pmatrix.set(pmv.get(new float[16]));
  prosceneShader.set("proscene_transform", pmatrix);
}
