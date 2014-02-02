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

import remixlab.tersehandling.event.DOF6Event;
import remixlab.tersehandling.generic.profile.Actionable;
import remixlab.tersehandling.generic.profile.Duoable;

public class GenericDOF6Event<A extends Actionable<?>> extends DOF6Event implements Duoable<A> {
	Actionable<?> action;
	
	public GenericDOF6Event(float x, float y, float z, float rx, float ry, float rz, int modifiers, int button) {
		super(x, y, z, rx, ry, rz, modifiers, button);
	}
	
	public GenericDOF6Event(GenericDOF6Event<A> prevEvent, float x, float y, float z, float rx, float ry, float rz, int modifiers, int button) {
		super(prevEvent, x, y, z, rx, ry, rz, modifiers, button);
	}
	
	public GenericDOF6Event(float x, float y, float z, float rx, float ry, float rz) {
		super(x, y, z, rx, ry, rz);
	}
	
	public GenericDOF6Event(GenericDOF6Event<A> prevEvent, float x, float y, float z, float rx, float ry, float rz) {
		super(prevEvent, x, y, z, rx, ry, rz);
	}
	
	public GenericDOF6Event(float x, float y, float z, float rx, float ry, float rz, int modifiers, int button, Actionable<?> a) {
		super(x, y, z, rx, ry, rz, modifiers, button);
		action = a;
	}
	
	public GenericDOF6Event(GenericDOF6Event<A> prevEvent, float x, float y, float z, float rx, float ry, float rz, int modifiers, int button, Actionable<?> a) {
		super(prevEvent, x, y, z, rx, ry, rz, modifiers, button);
		action = a;
	}
	
	public GenericDOF6Event(float x, float y, float z, float rx, float ry, float rz, Actionable<?> a) {
		super(x, y, z, rx, ry, rz);
		action = a;
	}
	
	public GenericDOF6Event(GenericDOF6Event<A> prevEvent, float x, float y, float z, float rx, float ry, float rz, Actionable<?> a) {
		super(prevEvent, x, y, z, rx, ry, rz);
		action = a;
	}
	
	protected GenericDOF6Event(GenericDOF6Event<A> other) {
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
	public GenericDOF6Event<A> get() {
		return new GenericDOF6Event<A>(this);
	}
}
