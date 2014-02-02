/*******************************************************************************
 * TerseHandling (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.tersehandling.event.shortcut;

import remixlab.tersehandling.event.TerseEvent;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * This class represents mouse shortcuts.
 * <p>
 * Mouse shortcuts can be of one out of two forms: 1. Mouse buttons (e.g., 'LEFT');
 * 2. Mouse button + Key combinations (e.g., 'RIGHT' + CTRL key).
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
		if (obj == null) return false;
		if (obj == this) return true;		
		if (obj.getClass() != getClass()) return false;		
		
		ButtonShortcut other = (ButtonShortcut) obj;
	  return new EqualsBuilder()		
	  .appendSuper(super.equals(obj))
		.append(button, other.button)
		.isEquals();
	}
	
	public ButtonShortcut() {
		this(TH_NOMODIFIER_MASK, TH_NOBUTTON);
	}

	/**
	 * Defines a mouse shortcut from the given mouse button.
	 * 
	 * @param b mouse button
	 */
	public ButtonShortcut(Integer b) {
		this(TH_NOMODIFIER_MASK, b);
	}

	/**
	 * Defines a mouse shortcut from the given modifier mask and mouse button combination.
	 * 
	 * @param m the mask 
	 * @param b mouse button
	 */
	///**
	public ButtonShortcut(Integer m, Integer b) {
		super(m);
		this.button = b;
	}
	//*/
	
	/**
	public ButtonShortcut(Integer m, Integer b) {		
	  //TODO HACK see issue: https://github.com/processing/processing/issues/1693
		this.button = b;	  
		//ALT
		if(button == TH_CENTER) {
			mask = (TH_ALT | m);
		}
		//META
		else if(button == TH_RIGHT) {
    	mask = (TH_META | m);
		}
		else
			mask = m;
	}
	*/
	
	protected ButtonShortcut(ButtonShortcut other) {
		super(other);
		this.button = new Integer(other.button);
	}
	
	@Override
	public ButtonShortcut get() {
		return new ButtonShortcut(this);
	}
	
	/**
	 * Returns a textual description of this mouse shortcut.
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
		//TODO: NO_BUTTON should be defined -> e.g., mouse move
		String r = TerseEvent.modifiersText(mask);
		switch (b) {
		case TH_LEFT:
			r += (r.length() > 0) ? "+LEFT_BUTTON" : "LEFT_BUTTON";
			break;
		case TH_CENTER:
			r += (r.length() > 0) ? "+MIDDLE_BUTTON" : "MIDDLE_BUTTON";
			break;
		case TH_RIGHT:
			r += (r.length() > 0) ? "+RIGHT_BUTTON" : "RIGHT_BUTTON";
			break;			
		default:
			r += (r.length() > 0) ? "+NO_MOUSE_BUTTON" : "NO_MOUSE_BUTTON";
			break;
		}		
		return r;
	}
	
	/**
	 * Internal convenience function.
	 */
	/**
	protected String description(MouseEvent e) {
		return description(e.getButton());
	}	
	*/

	protected final Integer button;
}
