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

import remixlab.tersehandling.event.DOF2Event;
import remixlab.tersehandling.generic.profile.Actionable;
import remixlab.tersehandling.generic.profile.Duoable;

public class GenericDOF2Event<A extends Actionable<?>> extends DOF2Event implements Duoable<A> {
	Actionable<?> action;
	
	public GenericDOF2Event(float x, float y) {
		super(x, y);
	}
	
	public GenericDOF2Event(GenericDOF2Event<A> prevEvent, float x, float y) {
		super(prevEvent, x, y);
	}
	
	public GenericDOF2Event(GenericDOF2Event<A> prevEvent, float x, float y, int modifiers, int button) {
		super(prevEvent, x, y, modifiers, button);
	}
	
	public GenericDOF2Event(float x, float y, int modifiers, int button) {
		super(x, y, modifiers, button);
	}
	
	public GenericDOF2Event(float x, float y, Actionable<?> a) {
		super(x, y);
		action = a;
	}
	
	public GenericDOF2Event(GenericDOF2Event<A> prevEvent, float x, float y, Actionable<?> a) {
		super(prevEvent, x, y);
		action = a;
	}
	
	public GenericDOF2Event(GenericDOF2Event<A> prevEvent, float x, float y, int modifiers, int button, Actionable<?> a) {
		super(prevEvent, x, y, modifiers, button);
		action = a;
	}
	
	public GenericDOF2Event(float x, float y, int modifiers, int button, Actionable<?> a) {
		super(x, y, modifiers, button);
		action = a;
	}
	
	protected GenericDOF2Event(GenericDOF2Event<A> other) {
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
	public GenericDOF2Event<A> get() {
		return new GenericDOF2Event<A>(this);
	}
}
