/*******************************************************************************
 * TerseHandling (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.tersehandling.generic.agent;

import remixlab.tersehandling.core.*;
import remixlab.tersehandling.event.*;
import remixlab.tersehandling.generic.profile.*;

public class GenericMotionAgent<M extends GenericMotionProfile<?>, C extends GenericClickProfile<?>> extends GenericActionableAgent<M> {
	protected C clickProfile;
	protected float[] sens;
	
	public GenericMotionAgent(M p, C c, TerseHandler tHandler, String n) {
		super(p, tHandler, n);
		clickProfile = c;
		sens = new float[] {1f, 1f, 1f, 1f, 1f, 1f};
	}
	
	public M motionProfile() {
		return profile();
	}
	
	public void setMotionProfile(M profile) {
		setProfile(profile);
	}
	
	public C clickProfile() {
		return clickProfile;
	}
	
	public void setClickProfile(C profile) {
		clickProfile = profile;
	}
	
	public void setSensitivities(float x) {
		setSensitivities(x,0,0,0,0,0);
	}
	
	public void setSensitivities(float x, float y) {
		setSensitivities(x,y,0,0,0,0);
	}
	
	public void setSensitivities(float x, float y, float z) {
		setSensitivities(x,y,z,0,0,0);
	}
	
	public void setSensitivities(float x, float y, float z, float rx, float ry,	float rz) {
		sens[0] = x;
		sens[1] = y;
		sens[2] = z;
		sens[3] = rx;
		sens[4] = ry;
		sens[5] = rz;
	}
	
	public float [] sensitivities() {
		return sens;
	}
	
	@Override
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if( clickProfile().description().length() != 0 ) {
			description += "Click shortcuts\n";
			description += clickProfile().description();
		}
		if( motionProfile().description().length() != 0 ) {
			description += "Motion shortcuts\n";
			description += motionProfile().description();
		}
		return description;
	}
	
	@Override
	public void handle(TerseEvent event) {
		//overkill but feels safer ;)
		if(event == null || !handler.isAgentRegistered(this) || grabber() == null) return;		
		if(event instanceof Duoable<?>) {
			if(event instanceof ClickEvent)
				if( foreignGrabber() )
					enqueueEventTuple(new EventGrabberTuple(event, grabber()));
				else
					enqueueEventTuple(new EventGrabberDuobleTuple(event, clickProfile().handle((Duoable<?>)event), grabber()));
			else
				if(event instanceof MotionEvent) {
					((MotionEvent)event).modulate(sens);
					if( foreignGrabber() )
						enqueueEventTuple(new EventGrabberTuple(event, grabber()));
					else
						enqueueEventTuple(new EventGrabberDuobleTuple(event, motionProfile().handle((Duoable<?>)event), grabber()));			
			}
		}
	}	
}
