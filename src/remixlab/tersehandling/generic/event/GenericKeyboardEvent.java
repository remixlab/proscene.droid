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

import remixlab.tersehandling.event.KeyboardEvent;
import remixlab.tersehandling.generic.profile.Actionable;
import remixlab.tersehandling.generic.profile.KeyDuoable;

public class GenericKeyboardEvent<A extends Actionable<?>> extends KeyboardEvent implements KeyDuoable<A> {
	Actionable<?> action;
	
	public GenericKeyboardEvent() {
		super();
	}
	
	public GenericKeyboardEvent(Integer modifiers, Character c, Integer vk) {
		super(modifiers, c, vk);
	}
	
	public GenericKeyboardEvent(Integer modifiers, Character c) {
		super(modifiers, c);
	}
	
	public GenericKeyboardEvent(Integer modifiers, Integer vk) {
		super(modifiers, vk);
	}
	
	public GenericKeyboardEvent(Character c) {
		super(c);
	}
	
	public GenericKeyboardEvent(Actionable<?> a) {
		super();
		action = a;
	}
	
	public GenericKeyboardEvent(Integer modifiers, Character c, Integer vk, Actionable<?> a) {
		super(modifiers, c, vk);
		action = a;
	}
	
	public GenericKeyboardEvent(Integer modifiers, Character c, Actionable<?> a) {
		super(modifiers, c);
		action = a;
	}
	
	public GenericKeyboardEvent(Integer modifiers, Integer vk, Actionable<?> a) {
		super(modifiers, vk);
		action = a;
	}
	
	public GenericKeyboardEvent(Character c, Actionable<?> a) {
		super(c);
		action = a;
	}
	
	protected GenericKeyboardEvent(GenericKeyboardEvent<A> other) {
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
	public GenericKeyboardEvent<A> get() {
		return new GenericKeyboardEvent<A>(this);
	}
}
