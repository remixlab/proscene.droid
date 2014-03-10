/*********************************************************************************
 * bias_tree 
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.generic.profile;

import remixlab.bias.core.Action;
import remixlab.bias.event.shortcut.*;

/**
 * A {@link remixlab.bias.generic.profile.Profile} defining a mapping between
 * {@link remixlab.bias.event.shortcut.ButtonShortcut}s and user-defined {@link remixlab.bias.core.Action}s.
 * 
 * @param <A>
 *          {@link remixlab.bias.core.Action} : User-defined action.
 */
public class MotionProfile<A extends Action<?>> extends Profile<ButtonShortcut, A> {
	public boolean isBindingInUse() {
		return isBindingInUse(B_NOMODIFIER_MASK, B_NOBUTTON);
	}

	/**
	 * Returns true if the given binding binds an action.
	 * 
	 * @param button
	 */
	public boolean isBindingInUse(Integer button) {
		return isBindingInUse(B_NOMODIFIER_MASK, button);
	}

	/**
	 * Returns true if the given binding binds an action.
	 * 
	 * @param mask
	 * @param button
	 */
	public boolean isBindingInUse(Integer mask, Integer button) {
		return isBindingInUse(new ButtonShortcut(mask, button));
	}

	/**
	 * Returns true if the given action is bound.
	 */
	public boolean isActionBound(A action) {
		return isActionMapped(action);
	}

	/**
	 * Convenience function that simply calls {@code setWheelShortcut(0, action)}.
	 */
	public void setBinding(A action) {
		setBinding(B_NOBUTTON, action);
	}

	/**
	 * Binds the action to the given binding
	 * 
	 * @param button
	 * @param action
	 */
	public void setBinding(Integer button, A action) {
		setBinding(B_NOMODIFIER_MASK, button, action);
	}

	/**
	 * Binds the action to the given binding
	 * 
	 * @param mask
	 * @param button
	 * @param action
	 */
	public void setBinding(Integer mask, Integer button, A action) {
		if (isBindingInUse(mask, button)) {
			Action<?> a = binding(mask, button);
			System.out.println("Warning: overwritting binding which was previously associated to " + a);
		}
		setBinding(new ButtonShortcut(mask, button), action);
	}

	/**
	 * Convenience function that simply calls {@code removeWheelShortcut(0)}.
	 */
	public void removeBinding() {
		removeBinding(B_NOMODIFIER_MASK, B_NOBUTTON);
	}

	/**
	 * Removes the action binding.
	 * 
	 * @param button
	 */
	public void removeBinding(Integer button) {
		removeBinding(B_NOMODIFIER_MASK, button);
	}

	/**
	 * Removes the action binding.
	 * 
	 * @param mask
	 * @param button
	 */
	public void removeBinding(Integer mask, Integer button) {
		removeBinding(new ButtonShortcut(mask, button));
	}

	public Action<?> binding() {
		return binding(B_NOMODIFIER_MASK, B_NOBUTTON);
	}

	/**
	 * Returns the action associated to the given binding.
	 * 
	 * @param button
	 */
	public Action<?> binding(Integer button) {
		return binding(B_NOMODIFIER_MASK, button);
	}

	/**
	 * Returns the action associated to the given binding.
	 * 
	 * @param mask
	 * @param button
	 */
	public Action<?> binding(Integer mask, Integer button) {
		return binding(new ButtonShortcut(mask, button));
	}
}
