/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.event;

import remixlab.bias.core.EventConstants;
import remixlab.bias.event.shortcut.Shortcut;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * Base class of all events that are to be handled by an {@link remixlab.bias.core.Agent}. Every BogusEvent encapsulates
 * a {@link remixlab.bias.event.shortcut.Shortcut} which may be bound to an user-defined
 * {@link remixlab.bias.core.Action} (see {@link #shortcut()}).
 * <p>
 * There are non-generic and generic BogusEvents. While generic BogusEvents hold an {@link remixlab.bias.core.Action} to
 * be executed by objects implementing the {@link remixlab.bias.core.Grabbable} interface (see also the
 * {@link remixlab.bias.core.Agent} class documentation), non-generic BogusEvents don't. This class is the base class of
 * both, non-generic and generic BogusEvents. Note that ActionBogusEvents are defined in their own
 * remixlab.bias.generic.event package.
 * <p>
 * The following are the main BogusEvent specializations: {@link remixlab.bias.event.MotionEvent},
 * {@link remixlab.bias.event.ClickEvent}, and {@link remixlab.bias.event.KeyboardEvent}. Please refer to their
 * documentation for details.
 * <p>
 * <b>Note</b> that BogusEvent detection/reduction could happened in several different ways. For instance, in the
 * context of Java-based application, it typically takes place when implementing a mouse listener interface. In
 * Processing, it does when registering at the PApplet the so called mouseEvent method. Moreover, the
 * {@link remixlab.bias.core.Agent#feed()} provides a callback alternative when none of these mechanisms are available
 * (as it often happens when dealing with specialized, non-default input hardware).
 */
public class BogusEvent implements EventConstants, Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				append(modifiers).
				append(timestamp).
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

		BogusEvent other = (BogusEvent) obj;
		return new EqualsBuilder()
				.append(modifiers, other.modifiers)
				.append(timestamp, other.timestamp)
				.isEquals();
	}

	protected final int	modifiers;
	protected long			timestamp;

	/**
	 * Constructs an event with an "empty" {@link remixlab.bias.event.shortcut.Shortcut}.
	 */
	public BogusEvent() {
		this.modifiers = 0;
		timestamp = System.currentTimeMillis();
	}

	/**
	 * Constructs an event taking the given {@code modifiers} as a {@link remixlab.bias.event.shortcut.Shortcut}.
	 */
	public BogusEvent(Integer modifiers) {
		this.modifiers = modifiers;
		// this.action = null;
		timestamp = System.currentTimeMillis();
	}

	protected BogusEvent(BogusEvent other) {
		this.modifiers = new Integer(other.modifiers);
		this.timestamp = new Long(other.timestamp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.util.Copyable#get()
	 */
	@Override
	public BogusEvent get() {
		return new BogusEvent(this);
	}

	/**
	 * @return the shortcut encapsulated by this event.
	 */
	public Shortcut shortcut() {
		return new Shortcut(modifiers());
	}

	/**
	 * @return the modifiers defining the event {@link remixlab.bias.event.shortcut.ButtonShortcut}.
	 */
	public int modifiers() {
		return modifiers;
	}

	/**
	 * @return the time at which the event occurs
	 */
	public long timestamp() {
		return timestamp;
	}

	/**
	 * Only {@link remixlab.bias.event.MotionEvent}s may be null.
	 */
	public boolean isNull() {
		return false;
	}

	/**
	 * @return true if Shift was down when the event occurs
	 */
	public boolean isShiftDown() {
		return (modifiers & B_SHIFT) != 0;
	}

	/**
	 * @return true if Ctrl was down when the event occurs
	 */
	public boolean isControlDown() {
		return (modifiers & B_CTRL) != 0;
	}

	/**
	 * @return true if Meta was down when the event occurs
	 */
	public boolean isMetaDown() {
		return (modifiers & B_META) != 0;
	}

	/**
	 * @return true if Alt was down when the event occurs
	 */
	public boolean isAltDown() {
		return (modifiers & B_ALT) != 0;
	}

	/**
	 * @return true if AltGraph was down when the event occurs
	 */
	public boolean isAltGraph() {
		return (modifiers & B_ALT_GRAPH) != 0;
	}

	/**
	 * @param mask
	 *          of modifiers
	 * @return a String listing the event modifiers
	 */
	public static String modifiersText(int mask) {
		String r = new String();
		if ((B_ALT & mask) == B_ALT)
			r += "ALT";
		if ((B_SHIFT & mask) == B_SHIFT)
			r += (r.length() > 0) ? "+SHIFT" : "SHIFT";
		if ((B_CTRL & mask) == B_CTRL)
			r += (r.length() > 0) ? "+CTRL" : "CTRL";
		if ((B_META & mask) == B_META)
			r += (r.length() > 0) ? "+META" : "META";
		if ((B_ALT_GRAPH & mask) == B_ALT_GRAPH)
			r += (r.length() > 0) ? "+ALT_GRAPH" : "ALT_GRAPH";
		return r;
	}
}
