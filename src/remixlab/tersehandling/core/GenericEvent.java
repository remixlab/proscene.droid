/*********************************************************************************
 * TerseHandling
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.tersehandling.core;

import remixlab.tersehandling.event.shortcut.Shortcut;

/**
 * A GenericEvent is {@link remixlab.tersehandling.event.TerseEvent} implementing this interface. The interface is used
 * to attach an action set (typically implemented as an enum) to a {@link remixlab.tersehandling.event.TerseEvent}.
 * <p>
 * <b>Note</b> that GenericEvents are implemented in their <b>remixlab.tersehandling.generic.event</b> package.
 * 
 * @param <A>
 *          Action set that may be attached to the event.
 */
public interface GenericEvent<A extends Action<?>> {
	/**
	 * Action attached to an event.
	 */
	public Action<?> action();

	/**
	 * Attaches the given action to the event.
	 */
	public void setAction(Action<?> a);

	/**
	 * Interface to event shortcut.
	 */
	public Shortcut shortcut();
}
