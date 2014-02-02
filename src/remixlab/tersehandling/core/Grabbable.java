/*******************************************************************************
 * TerseHandling (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.tersehandling.core;

import remixlab.tersehandling.event.*;

/**
 * Grabbers are means to attach a set of actions to application objects.
 * Grabbers are attached to Agents through their API, and may be attached
 * to more than just a single Agent.
 * <p>
 * Each application object willing to subscribe a set of actions should
 * either implement the Grabbable interface or extend from the AbstractGrabber
 * class (which provides a default implementation of that interface easing the
 * implementation), and override the following two methods:
 * {@link #checkIfGrabsInput(TerseEvent)}, which defines the rules to set the
 * application object as an input grabber; and,
 * {@link #performInteraction(TerseEvent)}, which defines how the application
 * object should behave according to a given TerseEvent, which may be
 * parameterized to hold a user-defined action.
 * 
 * @author pierre
 */
public interface Grabbable {
	/**
	 * Defines the rules to set the application object as an input grabber.
	 */
	boolean checkIfGrabsInput(TerseEvent event);
	
	/**
	 * Defines how the application object should behave according to a given
	 * TerseEvent, which may be parameterized to hold a user-defined action.
	 */
	void performInteraction(TerseEvent event);
	
	/**
	 * Check if this object grabs agent. Returns {@code true} if this object
	 * grabs the agent and {@code false} otherwise.
	 */
	boolean grabsAgent(Agent agent);
}
