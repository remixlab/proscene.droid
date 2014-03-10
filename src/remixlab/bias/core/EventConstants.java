/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.core;

/**
 * Interface defining various {@link remixlab.bias.event.shortcut.Shortcut} constants.
 */
public interface EventConstants {
	// modifier keys
	static public final int	B_NOMODIFIER_MASK	= 0;
	static public final int	B_SHIFT						= 1 << 0;
	static public final int	B_CTRL						= 1 << 1;
	static public final int	B_META						= 1 << 2;
	static public final int	B_ALT							= 1 << 3;
	static public final int	B_ALT_GRAPH				= 1 << 4;

	static public final int	B_SHIFT_DOWN			= 64;
	static public final int	B_CTRL_DOWN				= 128;
	static public final int	B_META_DOWN				= 256;
	static public final int	B_ALT_DOWN				= 512;
	static public final int	B_ALT_GRAPH_DOWN	= 8192;

	static final int				B_NOBUTTON				= 0;

	static final int				B_CENTER					= 3;

	// Arrows
	static final int				B_LEFT						= 37;
	static final int				B_UP							= 38;
	static final int				B_RIGHT						= 39;
	static final int				B_DOWN						= 40;
}
