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

//import remixlab.dandelion.core.Constants;
import remixlab.tersehandling.event.TerseEvent;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * This class represents agent click shortcuts.
 * <p>
 * Mouse click shortcuts are defined with a specific number of clicks
 * and can be of one out of two forms: 1. A mouse button; and, 2. A mouse
 * button plus a key-modifier (such as the CTRL key).
 */
public class ClickShortcut extends Shortcut implements Copyable {	
	@Override
	public int hashCode() {
    return new HashCodeBuilder(17, 37).
    appendSuper(super.hashCode()).
		append(numberOfClicks).
		append(button).
    toHashCode();		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;		
		if (obj.getClass() != getClass()) return false;		
		
		ClickShortcut other = (ClickShortcut) obj;
	  return new EqualsBuilder()
	  .appendSuper(super.equals(obj))
		.append(numberOfClicks, other.numberOfClicks)
		.append(button, other.button)
		.isEquals();
	}	
	
  /**
   * Defines a mouse single click shortcut from the given mouse button. 
   * 
   * @param b mouse button
   */
  public ClickShortcut(Integer b) {
  	this(TH_NOMODIFIER_MASK, b, 1);
  }
  
  /**
   * Defines a mouse single click shortcut from the given mouse button
   * and modifier mask.
   *      
   * @param m modifier mask
   * @param b mouse button
   */
  /**
  public ClickBinding(Integer m, Integer b) {
  	this(m, b, 1);
  }
  */
  
  /**
   * Defines a mouse click shortcut from the given mouse button and
   * number of clicks. 
   * 
   * @param b mouse button
   * @param c number of clicks
   */
  public ClickShortcut(Integer b, Integer c) {
  	this(TH_NOMODIFIER_MASK, b, c);
  }
	
		
	/**
	 * Defines a mouse click shortcut from the given mouse button,
	 * modifier mask, and number of clicks.
	 * 
	 * @param m modifier mask
	 * @param b mouse button
	 * @param c bumber of clicks
	 */
	public ClickShortcut(Integer m, Integer b, Integer c) {
		super(m);
		this.button = b;
		if(c <= 0)
			this.numberOfClicks = 1;
		else
			this.numberOfClicks = c;
	}
	
	protected ClickShortcut(ClickShortcut other) {
		super(other);
		this.numberOfClicks = new Integer(other.numberOfClicks);
		this.button = new Integer(other.button);
	}
	
	@Override
	public ClickShortcut get() {
		return new ClickShortcut(this);
	}
	
	/**
	 * Returns a textual description of this click shortcut.
	 *  
	 * @return description
	 */
	public String description() {
		String description = new String();
		if(mask != 0)
			description += TerseEvent.modifiersText(mask) + " + " + button.toString() + "_BUTTON";
		if(numberOfClicks==1)
		  description += " + " + numberOfClicks.toString() + " click";
		else
			description += " + " + numberOfClicks.toString() + " clicks";
		return description;
	}
	
	protected final Integer numberOfClicks;
	protected final Integer button;
}
