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
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;

public class GenericWheeledMotionAgent<W extends GenericMotionProfile<?>,
									   M extends GenericMotionProfile<?>,
									   C extends GenericClickProfile<?>> extends GenericMotionAgent<M,C> {
	
	protected W wheelProfile;
	
	public GenericWheeledMotionAgent(W w, M p, C c, TerseHandler tHandler, String n) {
		super(p, c, tHandler, n);
		wheelProfile = w;
	}
	
	public W wheelProfile() {
		return wheelProfile;
	}
	
	public void setWheelProfile(W profile) {
		wheelProfile = profile;
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
		if( wheelProfile().description().length() != 0 ) {
			description += "Wheel shortcuts\n";
			description += wheelProfile().description();
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
					handler.enqueueEventTuple(new EventGrabberTuple(event, grabber()));
				else
					handler.enqueueEventTuple(new EventGrabberDuobleTuple(event, clickProfile().handle((Duoable<?>)event), grabber()));
			else
				if(event instanceof MotionEvent) {
					((MotionEvent)event).modulate(sens);
					if( foreignGrabber() )
						handler.enqueueEventTuple(new EventGrabberTuple(event, grabber()));
					else
						if( event instanceof GenericDOF1Event )
							handler.enqueueEventTuple(new EventGrabberDuobleTuple(event, wheelProfile().handle((Duoable<?>)event), grabber()));
						else
							handler.enqueueEventTuple(new EventGrabberDuobleTuple(event, motionProfile().handle((Duoable<?>)event), grabber()));			
			}
		}
	}
}
