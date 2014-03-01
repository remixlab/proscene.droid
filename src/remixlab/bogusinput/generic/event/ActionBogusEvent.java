/*********************************************************************************
 * bogusinput_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.bogusinput.generic.event;

import remixlab.bogusinput.core.Action;
import remixlab.bogusinput.event.shortcut.Shortcut;

/**
 * ActionBogusEvent Interface used to attach a user-defined {@link remixlab.bogusinput.core.Action} (typically
 * implemented as an enum) to a (raw) {@link remixlab.bogusinput.event.BogusEvent}.
 * <p>
 * <b>Note</b> that while BogusEvents are implemented in the <b>remixlab.bogusinput.event</b> package, ActionBogusEvents
 * are implemented in their own <b>remixlab.bogusinput.generic.event</b> package.
 * 
 * @param <A>
 *          Action set that may be attached to the event.
 */
public interface ActionBogusEvent<A extends Action<?>> {
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
