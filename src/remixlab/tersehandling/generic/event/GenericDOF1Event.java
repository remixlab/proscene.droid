/*******************************************************************************
 * TerseHandling (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.tersehandling.generic.event;

import remixlab.tersehandling.event.DOF1Event;
import remixlab.tersehandling.generic.profile.Actionable;
import remixlab.tersehandling.generic.profile.Duoable;

public class GenericDOF1Event<A extends Actionable<?>> extends DOF1Event implements Duoable<A> {
	Actionable<?> action;
	
	public GenericDOF1Event(float x, int modifiers, int button) {
		super(x,modifiers, button);
	}
	
	public GenericDOF1Event(GenericDOF1Event<A> prevEvent, float x, int modifiers, int button) {
		super(prevEvent, x, modifiers, button);
	}
	
	public GenericDOF1Event(float x) {
		super(x);
	}
	
	public GenericDOF1Event(GenericDOF1Event<A> prevEvent, float x) {
		super(prevEvent, x);
	}
	
	public GenericDOF1Event(float x, int modifiers, int button, Actionable<?> a) {
		super(x,modifiers, button);
		action = a;
	}
	
	public GenericDOF1Event(GenericDOF1Event<A> prevEvent, float x, int modifiers, int button, Actionable<?> a) {
		super(prevEvent, x, modifiers, button);
		action = a;
	}
	
	public GenericDOF1Event(float x, Actionable<?> a) {
		super(x);
		action = a;
	}
	
	public GenericDOF1Event(GenericDOF1Event<A> prevEvent, float x, Actionable<?> a) {
		super(prevEvent, x);
		action = a;
	}
	
	protected GenericDOF1Event(GenericDOF1Event<A> other) {
		super(other);
		action = other.action;
	}

	@Override
	public Actionable<?> action() {
		return action;
	}
	
	@Override
	public void setAction(Actionable<?> a) {
		if( a instanceof Actionable<?> ) action = a;
	}
	
	@Override
	public GenericDOF1Event<A> get() {
		return new GenericDOF1Event<A>(this);
	}
}
