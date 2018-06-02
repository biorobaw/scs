package edu.usf.experiment.display.drawer;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D.Float;

/**
 * This interface draws information on a graphics component. It can be used to paint layers of information over one panel.
 * @author martin
 *
 */
public abstract class Drawer {
	
	public boolean doDraw = true;
	public Float worldCoordinates;
	public Float panelCoordinates;

	/**
	 * Draw the relevant information in the specified graphics.
	 * @param g The graphics element to paint with
	 * @param s The scaling object. It maps the universe coordinates and dimensions to the container coordinate frame.
	 */
	abstract public void  draw(Graphics g, Scaler s);

	/**
	 * Tells the drawer to clear potential stateful information, such as the path of the robot so far.
	 */
	abstract public void clearState();
	
	/**
	 * Updates data to be drawn in next cycle
	 */
	abstract public void updateData();
	
	
	/**
	 * Specifies whether the drawer should draw
	 */
	final public void setDraw(boolean doDraw) {
		this.doDraw = doDraw;
	}
	
	/**
	 * set world and panel coordinates
	 */
	public void setCoordinateSystems(Float worldCoordinates, Float panelCoordinates) {
		this.worldCoordinates = worldCoordinates;
		this.panelCoordinates = panelCoordinates;
	}

}
