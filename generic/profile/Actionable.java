/*******************************************************************************
 * TerseHandling (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.tersehandling.generic.profile;

/**
 * Generic interface used to implement action sub-sets.
 * 
 * The interface expects to be parametarized with the global enum action set,
 * and then defining a one-to-one mapping among the local subset and the global set.
 * 
 * @author pierre
 *
 * @param <E> Global enum action set.
 */
public interface Actionable<E extends Enum<E>> {
	/**
	 * Returns the global action this action is mapped to.
	 */
	E referenceAction();
	
	/**
	 * Returns a description of the action.
	 */
	String description();
	
	/**
	 * Returns the degrees-of-freedom needed to perform the action.
	 */
	public int dofs();
}
