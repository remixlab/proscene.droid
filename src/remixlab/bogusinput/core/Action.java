/*********************************************************************************
 * bogusinput_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.bogusinput.core;

/**
 * Generic interface defining user action (sub)groups.
 * <p>
 * (User-defined) global Actions in BogusInput should be defined by a third-party simply using an Enum. This interface
 * allows grouping items of that global action Enum together, thus possibly forming action sub-groups. Each item in the
 * action sub-group should be mapped back to an item in the global Enum set (see {@link #referenceAction()}).
 * <p>
 * <b>Note:</b> User-defined actions subgroups implementing this Interface are used to parameterize both, BogusEvents (
 * {@link remixlab.bogusinput.generic.event.ActionBogusEvent}), and Agents (
 * {@link remixlab.bogusinput.generic.agent.ActionAgent}). The idea being that user-defined actions may be grouped
 * together according to the BogusEvent type needed to implement them (see
 * {@link remixlab.bogusinput.core.Grabbable#performInteraction(remixlab.bogusinput.event.BogusEvent)}). Parsing the
 * BogusEvent thus requires the "same type" of {@link remixlab.bogusinput.core.Agent}.
 * <p>
 * <b>Observation</b> Enums provide an easy (typical) implementation of this Interface, e.g.,
 * {@code public enum ActionGroup implements Action<GlobalAction>}.
 * 
 * @param <E>
 *          Global enum action set.
 */
public interface Action<E extends Enum<E>> {
	/**
	 * Returns the global action item this action mapped to.
	 */
	E referenceAction();

	/**
	 * Returns a description of the action item.
	 */
	String description();

	/**
	 * Returns the degrees-of-freedom needed to perform the action.
	 */
	public int dofs();
}
