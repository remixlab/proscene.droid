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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import remixlab.tersehandling.core.EventConstants;
import remixlab.tersehandling.event.shortcut.*;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * A mapping between TerseEvent shortcuts and user-defined actions
 * implemented as a parameterized hash-map wrap.
 * <p>
 * Thanks to its Profiles, generic agents parse TerseEvents to determine
 * the user-defined action its input grabber should perform.
 *
 * @author pierre
 *
 * @param <K> Shortcut
 * @param <A> User defined action.
 */
public class GenericProfile<K extends Shortcut, A extends Actionable<?>> implements EventConstants, Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(map).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		GenericProfile<?, ?> other = (GenericProfile<?, ?>) obj;
		return new EqualsBuilder().append(map, other.map).isEquals();
	}

	protected HashMap<K, A> map;

	public GenericProfile() {
		map = new HashMap<K, A>();
	}

	/**
	 * Copy constructor. Use {@link #get()} to copy this profile.
	 * 
	 * @param other profile to be copied
	 */
	protected GenericProfile(GenericProfile<K, A> other) {
		map = new HashMap<K, A>();
		for (Map.Entry<K, A> entry : other.map().entrySet()) {
			K key = entry.getKey();
			A value = entry.getValue();
			setBinding(key, value);
		}
	}

	/**
	 * Returns a copy of this profile.
	 */
	@Override
	public GenericProfile<K, A> get() {
		return new GenericProfile<K, A>(this);
	}

	/**
	 * Main class method which attempts to define a user-defined action
	 * by parsing the event's shortcut.
	 * 
	 * @param event Generic event to be parsed by this profile.
	 * @return The user-defined action. May be null if no actions was found.
	 */
	public Actionable<?> handle(Duoable<?> event) {
		if (event != null)
			return binding(event.shortcut());
		return null;
	}


	/**
	 * Returns the {@code map} (which is simply an instance of {@code HashMap})
	 * encapsulated by this object.
	 */
	public HashMap<K, A> map() {
		return map;
	}

	public A binding(Shortcut key) {
		return map.get(key);
	}

	/**
	 * Defines the shortcut that triggers a given action.
	 * 
	 * @param key
	 *            shortcut.
	 * @param action
	 *            action.
	 */
	public void setBinding(K key, A action) {
		map.put(key, action);
	}

	/**
	 * Removes the shortcut binding.
	 * 
	 * @param key
	 *            shortcut
	 */
	public void removeBinding(K key) {
		map.remove(key);
	}

	/**
	 * Removes all the shortcuts from this object.
	 */
	public void removeAllBindings() {
		map.clear();
	}

	/**
	 * Returns true if this object contains a binding for the specified
	 * shortcut.
	 * 
	 * @param key
	 *            shortcut
	 * @return true if this object contains a binding for the specified
	 *         shortcut.
	 */
	public boolean isShortcutInUse(K key) {
		return map.containsKey(key);
	}

	/**
	 * Returns true if this object maps one or more shortcuts to the specified
	 * action.
	 * 
	 * @param action
	 *            action whose presence in this object is to be tested
	 * @return true if this object maps one or more shortcuts to the specified
	 *         action.
	 */
	public boolean isActionMapped(A action) {
		return map.containsValue(action);
	}

	/**
	 * Returns a description of all the bindings this profile holds.
	 */
	public String description() {
		String result = new String();
		for (Entry<K, A> entry : map.entrySet())
			if(entry.getKey() != null && entry.getValue() != null)
				result += entry.getKey().description() + " -> "	+ entry.getValue().description() + "\n";
		return result;
	}
}
