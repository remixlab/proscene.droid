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

import remixlab.tersehandling.event.ClickEvent;
import remixlab.tersehandling.generic.profile.Actionable;
import remixlab.tersehandling.generic.profile.Duoable;

public class GenericClickEvent <A extends Actionable<?>> extends ClickEvent implements Duoable<A> {
	Actionable<?> action;
	
	public GenericClickEvent(float x, float y, int b) {
		super(x, y, b);
	}
	
	public GenericClickEvent(float x, float y, int b, int clicks) {
		super(x, y, b, clicks);
	}
	
	public GenericClickEvent(float x, float y, Integer modifiers, int b) {
		super(x, y, modifiers, b);
	}
	
	public GenericClickEvent(float x, float y, Integer modifiers, int b, int clicks) {
		super(x, y, modifiers, b, clicks);
	}
	
	public GenericClickEvent(float x, float y, int b, Actionable<?> a) {
		super(x, y, b);
		action = a;
	}
	
	public GenericClickEvent(float x, float y, int b, int clicks, Actionable<?> a) {
		super(x, y, b, clicks);
		action = a;
	}
	
	public GenericClickEvent(float x, float y, Integer modifiers, int b, Actionable<?> a) {
		super(x, y, modifiers, b);
		action = a;
	}
	
	public GenericClickEvent(float x, float y, Integer modifiers, int b, int clicks, Actionable<?> a) {
		super(x, y, modifiers, b, clicks);
		action = a;
	}
	
	protected GenericClickEvent(GenericClickEvent<A> other) {
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
	public GenericClickEvent<A> get() {
		return new GenericClickEvent<A>(this);
	}
}
