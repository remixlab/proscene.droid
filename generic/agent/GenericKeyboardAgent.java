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

import remixlab.tersehandling.core.EventGrabberTuple;
import remixlab.tersehandling.core.TerseHandler;
import remixlab.tersehandling.event.TerseEvent;
import remixlab.tersehandling.generic.profile.Duoable;
import remixlab.tersehandling.generic.profile.GenericKeyboardProfile;
import remixlab.tersehandling.generic.profile.KeyDuoable;

public class GenericKeyboardAgent<K extends GenericKeyboardProfile<?>> extends GenericActionableAgent<K> {	
	public GenericKeyboardAgent(K k, TerseHandler scn, String n) {
		super(k, scn, n);
	}
	
	public K keyboardProfile() {
		return profile();
	}

	public void setKeyboardProfile(K kprofile) {
		setProfile(profile);
	}
	
	@Override
	public void handle(TerseEvent event) {
		if(event == null || !handler.isAgentRegistered(this) || grabber() == null) return;
		if(event instanceof Duoable<?>)
			if( foreignGrabber() )
				enqueueEventTuple(new EventGrabberTuple(event, grabber()));
			else
				enqueueEventTuple(new EventGrabberDuobleTuple(event, keyboardProfile().handle((Duoable<?>)event), grabber()));
	}
	
	public void handleKey(TerseEvent event) {
		if(event == null || !handler.isAgentRegistered(this) || grabber() == null) return;	
		if(event instanceof KeyDuoable<?>)
			if( foreignGrabber() )
				enqueueEventTuple(new EventGrabberTuple(event, grabber()));
			else
				enqueueEventTuple(new EventGrabberDuobleTuple(event, keyboardProfile().handleKey((KeyDuoable<?>)event), grabber()));
	}
}
