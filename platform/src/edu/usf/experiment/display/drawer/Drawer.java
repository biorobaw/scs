package edu.usf.experiment.display.drawer;

import java.awt.Graphics;

/**
 * This interface draws information on a graphics component. It can be used to paint layers of information over one panel.
 * @author martin
 *
 */
public interface Drawer {

	/**
	 * Draw the relevant information in the specified graphics.
	 * @param g The graphics element to paint with
	 * @param s The scaling object. It maps the universe coordinates and dimensions to the container coordinate frame.
	 */
	void draw(Graphics g, Scaler s);

	/**
	 * Tells the drawer to clear potential stateful information, such as the path of the robot so far.
	 */
	void clearState();

}
