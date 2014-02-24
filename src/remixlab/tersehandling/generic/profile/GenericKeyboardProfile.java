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

import remixlab.tersehandling.event.shortcut.KeyboardShortcut;

/**
 * A specialized profile to deal with keyboard events.
 *
 * @param <A> User defined action
 */
public class GenericKeyboardProfile<A extends Actionable<?>> extends GenericProfile<KeyboardShortcut, A> {
	/**
	 * Defines a keyboard shortcut to bind the given action.
	 * 
	 * @param key
	 *            shortcut
	 * @param action
	 *            action to be bound
	 */
	public void setShortcut(Character key, A action) {
		if (isKeyInUse(key)) {
			Actionable<?> a = shortcut(key);
			System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
		}
		setBinding(new KeyboardShortcut(key), action);
	}

	/**
	 * Defines a keyboard shortcut to bind the given action.
	 * 
	 * @param mask
	 *            modifier mask defining the shortcut
	 * @param vKey
	 *            coded key defining the shortcut
	 * @param action
	 *            action to be bound
	 */
	public void setShortcut(Integer mask, Integer vKey, A action) {
		if (isKeyInUse(mask, vKey)) {
			Actionable<?> a = shortcut(mask, vKey);
			System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
		}
		setBinding(new KeyboardShortcut(mask, vKey), action);
	}

	/**
	 * Removes the keyboard shortcut.
	 * 
	 * @param key
	 *            shortcut
	 */
	public void removeShortcut(Character key) {
		removeBinding(new KeyboardShortcut(key));
	}

	/**
	 * Removes the keyboard shortcut.
	 * 
	 * @param mask
	 *            modifier mask that defining the shortcut
	 * @param vKey
	 *            coded key defining the shortcut
	 */
	public void removeShortcut(Integer mask, Integer vKey) {
		removeBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns the action that is bound to the given keyboard shortcut.
	 * 
	 * @param key
	 *            shortcut
	 * @return action
	 */
	public Actionable<?> shortcut(Character key) {
		return binding(new KeyboardShortcut(key));
	}

	/**
	 * Returns the action that is bound to the given keyboard shortcut.
	 * 
	 * @param mask
	 *            modifier mask defining the shortcut
	 * @param vKey
	 *            coded key defining the shortcut
	 * @return action
	 */
	public Actionable<?> shortcut(Integer mask, Integer vKey) {
		return binding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns true if the given keyboard shortcut binds an action.
	 * 
	 * @param key
	 *            shortcut
	 */
	public boolean isKeyInUse(Character key) {
		return isShortcutInUse(new KeyboardShortcut(key));
	}

	/**
	 * Returns true if the given keyboard shortcut binds an action.
	 * 
	 * @param mask
	 *            modifier mask defining the shortcut
	 * @param vKey
	 *            coded key defining the shortcut
	 */
	public boolean isKeyInUse(Integer mask, Integer vKey) {
		return isShortcutInUse(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns true if there is a keyboard shortcut for the given action.
	 */
	public boolean isKeyboardActionBound(A action) {
		return isActionMapped(action);
	}
}
