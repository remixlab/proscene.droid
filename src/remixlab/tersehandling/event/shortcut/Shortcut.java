/*********************************************************************************
 * TerseHandling
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.tersehandling.event.shortcut;

import remixlab.tersehandling.core.EventConstants;
import remixlab.tersehandling.event.*;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * Shortcuts are TerseEvent footprints (that may be 'empty') needed to bind user actions.
 * <p>
 * Shortcuts can represent, for instance, a button being dragged and the modifier key pressed at the very moment an user
 * interaction takes place, such as when she drags a giving mouse button while pressing the 'CTRL' modifier key.
 * 
 * @author pierre
 */
public class Shortcut implements EventConstants, Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
						append(mask).
						toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		Shortcut other = (Shortcut) obj;
		return new EqualsBuilder()
						.append(mask, other.mask)
						.isEquals();
	}

	protected final Integer mask;

	public Shortcut(Integer m) {
		mask = m;
	}

	/**
	 * Constructs an "empty" shortcut. Same as: {@link #Shortcut(Integer)} with the integer parameter being
	 * TH_NOMODIFIER_MASK.
	 */
	public Shortcut() {
		mask = TH_NOMODIFIER_MASK;
	}

	protected Shortcut(Shortcut other) {
		this.mask = new Integer(other.mask);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.util.Copyable#get()
	 */
	@Override
	public Shortcut get() {
		return new Shortcut(this);
	}

	/**
	 * Shortcut description.
	 * 
	 * @return description as a String
	 */
	public String description() {
		return TerseEvent.modifiersText(mask);
	}
}
