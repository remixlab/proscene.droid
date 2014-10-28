/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/, Victor Forero
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.agent;

import remixlab.bias.event.DOF6Event;
import remixlab.dandelion.core.*;

/**
 * An {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent} representing a Wheeled mouse and thus only holds 2
 * Degrees-Of-Freedom (e.g., two translations or two rotations), such as most mice.
 */
public class WheeledMultiTouchAgent extends HIDAgent {
	DOF6Event			event, pressEvent;
	InteractiveFrame	iFrame;
	protected int		drag= 1, pich = 2, rotate = 3;
	
	public WheeledMultiTouchAgent(AbstractScene scn, String n) {
		super(scn, n);
		// TODO Auto-generated constructor stub
	}
	
	
}