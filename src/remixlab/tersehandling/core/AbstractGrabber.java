/*********************************************************************************
 * TerseHandling
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.tersehandling.core;

/**
 * Default implementation of the Grabber interface which eases implementation by simply overriding
 * {@link #grabsAgent(Agent)}.
 * 
 * @author pierre
 */
public abstract class AbstractGrabber implements Grabbable {
	/**
	 * Empty constructor.
	 */
	public AbstractGrabber() {
	}

	/**
	 * Constructs and adds this grabber to the agent pool.
	 * 
	 * @see remixlab.tersehandling.core.Agent#pool()
	 */
	public AbstractGrabber(Agent agent) {
		agent.addInPool(this);
	}

	/**
	 * Constructs and adds this grabber to all agents belonging to the tersehandler.
	 * 
	 * @see remixlab.tersehandling.core.TerseHandler#agents()
	 */
	public AbstractGrabber(TerseHandler tersehandler) {
		for (Agent agent : tersehandler.agents())
			agent.addInPool(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see remixlab.tersehandling.core.Grabber#grabsAgent(remixlab.tersehandling.core.Agent)
	 */
	@Override
	public boolean grabsAgent(Agent agent) {
		return agent.grabber() == this;
	}
}
