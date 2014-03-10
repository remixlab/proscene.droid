/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.event.shortcut;

import remixlab.bias.event.BogusEvent;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * This class represents button shortcuts.
 * <p>
 * Button shortcuts can be of one of two forms: 1. Buttons (e.g., 'LEFT' , or even 'B_NOBUTTON'); 2. Button + modifier
 * key combinations (e.g., 'RIGHT' + 'CTRL').
 * <p>
 * Note that the shortcut may be empty: the no-button (B_NOBUTTON) and no-modifier-mask (B_NOMODIFIER_MASK) combo may
 * also defined a shortcut. Empty shortcuts may bind (mouse) move interactions.
 */
public final class ButtonShortcut extends Shortcut implements Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(button).
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

		ButtonShortcut other = (ButtonShortcut) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(button, other.button)
				.isEquals();
	}

	protected final Integer	button;

	/**
	 * Constructs an "empty" shortcut by conveniently calling {@code this(B_NOMODIFIER_MASK, B_NOBUTTON);}
	 */
	public ButtonShortcut() {
		this(B_NOMODIFIER_MASK, B_NOBUTTON);
	}

	/**
	 * Defines a shortcut from the given button.
	 * 
	 * @param b
	 *          button
	 */
	public ButtonShortcut(Integer b) {
		this(B_NOMODIFIER_MASK, b);
	}

	/**
	 * Defines a shortcut from the given modifier mask and button combination.
	 * 
	 * @param m
	 *          the mask
	 * @param b
	 *          button
	 */
	public ButtonShortcut(Integer m, Integer b) {
		super(m);
		this.button = b;
	}

	protected ButtonShortcut(ButtonShortcut other) {
		super(other);
		this.button = new Integer(other.button);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.bias.event.shortcut.Shortcut#get()
	 */
	@Override
	public ButtonShortcut get() {
		return new ButtonShortcut(this);
	}

	/**
	 * Returns a textual description of this shortcut.
	 * 
	 * @return description
	 */
	public String description() {
		return description(button);
	}

	/**
	 * Internal. Low-level description() function.
	 */
	protected String description(Integer b) {
		// TODO: NO_BUTTON should be defined -> e.g., mouse move
		String r = BogusEvent.modifiersText(mask);
		switch (b) {
		case B_LEFT:
			r += (r.length() > 0) ? "+LEFT_BUTTON" : "LEFT_BUTTON";
			break;
		case B_CENTER:
			r += (r.length() > 0) ? "+MIDDLE_BUTTON" : "MIDDLE_BUTTON";
			break;
		case B_RIGHT:
			r += (r.length() > 0) ? "+RIGHT_BUTTON" : "RIGHT_BUTTON";
			break;
		default:
			r += (r.length() > 0) ? "+NO_BUTTON" : "NO_BUTTON";
			break;
		}
		return r;
	}
}
