package remixlab.gesture;

//Android Multi-Touch event demo
//David Bouchard 
//http://www.deadpixel.ca
import java.util.ArrayList;
import java.util.Iterator;

import remixlab.util.*;
import remixlab.dandelion.core.Constants.Gestures;
import remixlab.dandelion.geom.Vec;

public class TouchProcessor {

	  // heuristic constants 
	  private final long  TAP_INTERVAL = 200;
	  private final long  TAP_TIMEOUT  = 200;
	  private final int   DOUBLE_TAP_DIST_THRESHOLD = 30;
	  private final int   FLICK_VELOCITY_THRESHOLD = 20;
	  private final float MAX_MULTI_DRAG_DISTANCE = 400; // from the centroid

	  private final float Turn_THRESHOLD = 0.01f;
	  private final float PINCH_THRESHOLD = 1.2f;
	  // A list of currently active touch points 
	  ArrayList <TouchPoint> touchPoints;

	  // Used for tap/doubletaps 
	  TouchPoint firstTap;
	  TouchPoint secondTap;
	  long tap;
	  int tapCount = 0;

	  // Events to be broadcast to the sketch 
	  ArrayList<TouchEvent> events;
	  
	  // centroid information
	  private float cx;
	  private float cy;
	  float old_cx, old_cy;
	  private float r;
	  private float z = 1;
	  
	  boolean pointsChanged = false;

	  //-------------------------------------------------------------------------------------
	  public TouchProcessor() {
	    touchPoints = new ArrayList<TouchPoint>();
	    events = new ArrayList<TouchEvent>();
	  }

	  //-------------------------------------------------------------------------------------
	  // Point Update functions 
	  public synchronized void pointDown(float x, float y, int id) {    
	    TouchPoint p = new TouchPoint(x, y, id);
	    touchPoints.add(p);  
	    setZ(1);
	    setR(0);
	    updateCentroid();
	    if ( touchPoints.size() >= 2) {
	      p.initGestureData(getCx(), getCy());
	      if (touchPoints.size() == 2) {
	        // if this is the second point, we now have a valid centroid to update the first point
	        TouchPoint frst = (TouchPoint)touchPoints.get(0);
	        frst.initGestureData(getCx(), getCy());
	      }
	    }

	    // tap detection 
	    if (tapCount == 0) {
	      firstTap = p;
	    }
	    if (tapCount == 1) {
	      secondTap = p;
	    }
	    tap = System.currentTimeMillis(); 
	    pointsChanged = true;
	  }

	  //-------------------------------------------------------------------------------------
	  public synchronized void pointUp(int id) {
	    TouchPoint p = getPoint(id);
	    touchPoints.remove(p);

	    // tap detection 
	    // TODO: handle a long press event here? 
	    if ( p == firstTap || p == secondTap ) {
	      // this could be either a Tap or a Flick gesture, based on movement 
	      float d = Util.distance(p.x, p.y, p.px, p.py);
	      if ( d > FLICK_VELOCITY_THRESHOLD ) {
	        FlickEvent event = new FlickEvent(p.px, p.py, new Vec(p.x-p.px, p.y-p.py));
	        events.add(event);
	      }
	      else {      
	        long interval = System.currentTimeMillis() - tap;
	        
	        if ( interval < TAP_INTERVAL ) {
	          tapCount++;
	        }
	      }
	    }
	    pointsChanged = true;
	  }

	  //-------------------------------------------------------------------------------------
	  public synchronized void pointMoved(float x, float y, int id) {
	    TouchPoint p = getPoint(id);
	    p.update(x, y);
	    // since the events will be in sync with draw(), we just wait until analyse() to
	    // look for gestures
	    pointsChanged = true;
	  }

	  //-------------------------------------------------------------------------------------
	  // Calculate the centroid of all active points 
	  void updateCentroid() {
	    old_cx = getCx();
	    old_cy = getCy();
	    setCx(0);
	    setCy(0);
	    for (int i=0; i < touchPoints.size(); i++) {
	      TouchPoint p = (TouchPoint)touchPoints.get(i);
	      setCx(getCx() + p.x);
	      setCy(getCy() + p.y);
	    }
	    setCx(getCx() / touchPoints.size());
	    setCy(getCy() / touchPoints.size()); 
	  }

	  //-------------------------------------------------------------------------------------
	  public synchronized void analyse() {
	    handleTaps();
	    // simple event priority rule: do not try to Turn or pinch while dragging
	    // this gets rid of a lot of jittery events 
	    if (pointsChanged) {
	      updateCentroid();
	      if (handleDrag() != null) {
	    	handlePinch();
	        handleturn();
	      }
	      pointsChanged = false;
	    }
	  }

	  public synchronized Gestures sendEvent(){
		  Gestures gesture = null;
		  DragEvent dragEvent = handleDrag();
		  PinchEvent pinchEvent = handlePinch();
		  TurnEvent TurnEvent = handleturn();
		  if(TurnEvent != null) setR(getR() + (TurnEvent.angle * -500));
		  if(pinchEvent != null)  setZ(getZ() + pinchEvent.amount);  
		  if(dragEvent != null){
			 if(dragEvent.numberOfPoints == 1) gesture = Gestures.DRAG_ONE;
			 else if(dragEvent.numberOfPoints == 2) gesture = Gestures.DRAG_TWO;
			 else if(dragEvent.numberOfPoints == 3) gesture = Gestures.DRAG_THREE;
	  
		  }else if(pinchEvent != null){
			  if(pinchEvent.numberOfPoints == 2) gesture = Gestures.PINCH_TWO;
			  else if(pinchEvent.numberOfPoints == 3) gesture = Gestures.PINCH_THREE;
		  }else if(TurnEvent != null){
			  if(TurnEvent.numberOfPoints == 2) gesture = Gestures.TURN_TWO;
			  else if(TurnEvent.numberOfPoints == 3) gesture = Gestures.TURN_THREE;
		  }
		  return gesture;
	  }

	  //-------------------------------------------------------------------------------------
	  // send events to the sketch
	  /*
	  void sendEvents() {
		    for (int i=0; i < events.size(); i++) {
		      TouchEvent e = (TouchEvent)events.get(i);
		      if      ( e instanceof TapEvent ) onTap( (TapEvent)e );
		      else if ( e instanceof FlickEvent ) onFlick( (FlickEvent)e );
		      else if ( e instanceof DragEvent ) onDrag( (DragEvent)e );
		      else if ( e instanceof PinchEvent ) onPinch( (PinchEvent)e );
		      else if ( e instanceof TurnEvent ) onTurn( (TurnEvent)e );
		    }
		    events.clear();
	  }*/

	  //-------------------------------------------------------------------------------------
	  void handleTaps() {
	    if (tapCount == 2) {
	      // check if the tap point has moved 
	      float d = Util.distance(firstTap.x, firstTap.y, secondTap.x, secondTap.y);
	      if ( d > DOUBLE_TAP_DIST_THRESHOLD ) {
	        // if the two taps are apart, count them as two single taps
	        //TapEvent event1 = new TapEvent(firstTap.x, firstTap.y, TapEvent.SINGLE);        
	        //onTap(event1);
	        //TapEvent event2 = new TapEvent(secondTap.x, secondTap.y, TapEvent.SINGLE);        
	        //onTap(event2);
	        events.add( new TapEvent(firstTap.x, firstTap.y, TapEvent.SINGLE) );
	      }
	      else {
	        events.add( new TapEvent(firstTap.x, firstTap.y, TapEvent.DOUBLE) );
	      }
	      tapCount = 0;
	    }
	    else if (tapCount == 1) { 
	      long interval = System.currentTimeMillis()  - tap;
	      if (interval > TAP_TIMEOUT) {
	        events.add( new TapEvent(firstTap.x, firstTap.y, TapEvent.SINGLE) );               
	        tapCount = 0;
	      }
	    }
	  }

	  //-------------------------------------------------------------------------------------
	  // turn is the average angle change between each point and the centroid 
	  TurnEvent handleturn() {
		TurnEvent TurnEvent = null;
	    if (touchPoints.size() >= 2){
		    // look for turn events
		    float turn = 0;
		    for (int i=0; i < touchPoints.size(); i++) {
		      TouchPoint p = (TouchPoint)touchPoints.get(i);
		      float angle = (float) Math.atan2( p.y-getCy(), p.x-getCx() );
		      p.setAngle(angle);
		      float delta = p.angle - p.oldAngle;
		      if ( delta > Math.PI ) delta -= 2 * Math.PI;
		      if ( delta < -Math.PI ) delta += 2 * Math.PI;
		      turn += delta;
		    } 
		    turn /= touchPoints.size() ;
		    if (Math.abs(turn) > Turn_THRESHOLD ){
		    	TurnEvent = new TurnEvent(getCx(), getCy(), turn, touchPoints.size());
		    	events.add( TurnEvent );
		    }
	    }
	    return TurnEvent;
	  }

	  //-------------------------------------------------------------------------------------
	  // pinch is simply the average distance change from each points to the centroid
	  PinchEvent handlePinch() {
		PinchEvent pinchEvent = null;
	    if (touchPoints.size() >= 2){
	    	// look for pinch events 
	    	float pinch = 0;
	    	for (int i=0; i < touchPoints.size(); i++) {
	    		TouchPoint p = (TouchPoint)touchPoints.get(i);
	    		float distance = Util.distance(p.x, p.y, getCx(), getCy());
	    		p.setPinch(distance);
	    		float delta = p.pinch - p.oldPinch;
	    		pinch += delta;
	    	}
	    	pinch /= touchPoints.size();
	    	if (Math.abs(pinch) > PINCH_THRESHOLD){
	    		pinchEvent = new PinchEvent(getCx(), getCy(), pinch, touchPoints.size());
	    		events.add(pinchEvent);
	    	}
	    }
	    return pinchEvent;
	  }

	  //-------------------------------------------------------------------------------------
	  DragEvent handleDrag() {
	    // look for multi-finger drag events
	    // multi-drag is defined as all the fingers moving close-ish together in the same direction
	    boolean x_drag = true;
	    boolean y_drag = true;
	    boolean clustered = false;
	    int first_x_dir = 0;
	    int first_y_dir = 0;
	    DragEvent dragEvent = null;
	    
	    for (int i=0; i < touchPoints.size(); i++) {
	      TouchPoint p = (TouchPoint)touchPoints.get(i);
	      int x_dir = 0;
	      int y_dir = 0;
	      if (p.dx() > 0) x_dir = 1;
	      if (p.dx() < 0) x_dir = -1;
	      if (p.dy() > 0) y_dir = 1;
	      if (p.dy() < 0) y_dir = -1;

	      if (i==0) {
	        first_x_dir = x_dir;
	        first_y_dir = y_dir;
	      }
	      else {
	        if (first_x_dir != x_dir) x_drag = false;
	        if (first_y_dir != y_dir) y_drag = false;
	      }
	      
	      // if the point is stationary 
	      if (x_dir == 0) x_drag = false;
	      if (y_dir == 0) y_drag = false;
	      
	      if (touchPoints.size() == 1) clustered = true;
	      else {
	        float distance = Util.distance(p.x, p.y, getCx(), getCy());
	        if ( distance < MAX_MULTI_DRAG_DISTANCE ) {
	          clustered = true;
	        }
	      }
	    }

	    if ((x_drag || y_drag) && clustered) {
	      if (touchPoints.size() == 1) {
	        TouchPoint p = (TouchPoint)touchPoints.get(0);
	        // use the centroid to calculate the position and delta of this drag event
	        dragEvent = new DragEvent(p.x, p.y, p.dx(), p.dy(), 1);
	        events.add(dragEvent);
	      }
	      else {  
	        // use the centroid to calculate the position and delta of this drag event
	    	  dragEvent = new DragEvent(getCx(), getCy(), getCx()-old_cx, getCy()-old_cy, touchPoints.size());
	        events.add(dragEvent);
	      }
	    }
	    return dragEvent;
	  }

	  //-------------------------------------------------------------------------------------
	  @SuppressWarnings("unchecked")
	synchronized ArrayList<TouchPoint> getPoints() {
	    return (ArrayList<TouchPoint>) touchPoints.clone();
	  }

	  //-------------------------------------------------------------------------------------
	  synchronized TouchPoint getPoint(int pid) {
	    Iterator<TouchPoint> i = touchPoints.iterator();
	    while (i.hasNext ()) {
	      TouchPoint tp = (TouchPoint)i.next();
	      if (tp.id == pid) return tp;
	    }
	    return null;
	  }

	public float getCx() {
		return cx;
	}

	public void setCx(float cx) {
		this.cx = cx;
	}

	public float getCy() {
		return cy;
	}

	public void setCy(float cy) {
		this.cy = cy;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}
	 
}
