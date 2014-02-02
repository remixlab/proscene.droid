/*******************************************************************************
 * TerseHandling (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.tersehandling.event;

import remixlab.tersehandling.core.EventConstants;
import remixlab.tersehandling.event.shortcut.Shortcut;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

public class TerseEvent implements EventConstants, Copyable {
	@Override
	public int hashCode() {
    return new HashCodeBuilder(17, 37).		
		//append(action).
		append(modifiers).
		append(timestamp).
    toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;		
		if (obj.getClass() != getClass()) return false;		
		
		TerseEvent other = (TerseEvent) obj;
	  return new EqualsBuilder()		
		//.append(action, other.action)
		.append(modifiers, other.modifiers)
		.append(timestamp, other.timestamp)
		.isEquals();
	}
	
  protected final Integer modifiers;
  protected Long timestamp;
  
  public TerseEvent() {
    this.modifiers = 0;
    timestamp = System.currentTimeMillis();
  }
 
  public TerseEvent(Integer modifiers) {
    this.modifiers = modifiers;
    //this.action = null;
    timestamp = System.currentTimeMillis();
  }  
  
  protected TerseEvent(TerseEvent other) {
		this.modifiers = new Integer(other.modifiers);
		this.timestamp = new Long(other.timestamp);
	}  
  
  @Override
	public TerseEvent get() {
		return new TerseEvent(this);
	}
  
  public Shortcut shortcut() {
  	return new Shortcut(modifiers());
  }
  
  public Integer modifiers() {
    return modifiers;
  }
  
  public long timestamp() {
  	return timestamp;
  }
  
  public boolean isNull() {
  	return false;
  }

  public boolean isShiftDown() {
    return (modifiers & TH_SHIFT) != 0;
  }

  public boolean isControlDown() {
    return (modifiers & TH_CTRL) != 0;
  }

  public boolean isMetaDown() {
    return (modifiers & TH_META) != 0;
  }

  public boolean isAltDown() {
    return (modifiers & TH_ALT) != 0;
  }
  
  public boolean isAltGraph() {
    return (modifiers & TH_ALT_GRAPH) != 0;
  }
  
	public static String modifiersText(int mask) {
		String r = new String();
		if((TH_ALT & mask)       == TH_ALT) r += "ALT";						
		if((TH_SHIFT & mask)     == TH_SHIFT) r += (r.length() > 0) ? "+SHIFT" : "SHIFT";
		if((TH_CTRL & mask)      == TH_CTRL) r += (r.length() > 0) ? "+CTRL" : "CTRL";
		if((TH_META & mask)      == TH_META) r += (r.length() > 0) ? "+META" : "META";
		if((TH_ALT_GRAPH & mask) == TH_ALT_GRAPH) r += (r.length() > 0) ? "+ALT_GRAPH" : "ALT_GRAPH";
		return r;
	}
}
