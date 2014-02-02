/*******************************************************************************
 * TerseHandling (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.tersehandling.core;

/**
 * Default implementation of the Grabbable interface which eases
 * implementation by simply overriding {@link #grabsAgent(Agent)}.
 * 
 * @author pierre
 */
public abstract class AbstractGrabber implements Grabbable {
	public AbstractGrabber() {
	}
	
	public AbstractGrabber(Agent agent) {
		agent.addInPool(this);
	}
	
	public AbstractGrabber(TerseHandler handler) {
		for (Agent agent : handler.agents())
			agent.addInPool(this);
	}
	
	@Override
	public boolean grabsAgent(Agent agent) {
		return agent.grabber() == this;
	}
}
