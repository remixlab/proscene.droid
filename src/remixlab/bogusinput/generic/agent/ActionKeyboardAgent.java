/*********************************************************************************
 * bogusinput_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/
package remixlab.bogusinput.generic.agent;

import remixlab.bogusinput.core.InputHandler;
import remixlab.bogusinput.generic.profile.KeyboardProfile;

/**
 * This class is provided purely for symmetry and style reasons against the events and shortcuts API. Only needed if you
 * plan to implement your own KeyboardAgent.
 * 
 * @see remixlab.dandelion.agent.KeyboardAgent
 * 
 * @param <K>
 *          The {@link remixlab.bogusinput.generic.profile.KeyboardProfile} to parameterize this Agent with.
 */
public class ActionKeyboardAgent<K extends KeyboardProfile<?>> extends ActionAgent<K> {
	/**
	 * Simply calls
	 * {@link remixlab.bogusinput.generic.agent.ActionAgent#ActionAgent(remixlab.bogusinput.generic.profile.Profile, InputHandler, String)}
	 * on the given parameters.
	 */
	public ActionKeyboardAgent(K k, InputHandler scn, String n) {
		super(k, scn, n);
	}

	/**
	 * @return The {@link remixlab.bogusinput.generic.profile.KeyboardProfile}
	 */
	public K keyboardProfile() {
		return profile();
	}

	/**
	 * Sets the The {@link remixlab.bogusinput.generic.profile.KeyboardProfile}.
	 */
	public void setKeyboardProfile(K kprofile) {
		setProfile(profile);
	}
}
