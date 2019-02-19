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
	public String drawerName;
//	public Float defaultWorldCoordinates;

	/**
	 * Draw the relevant information in the specified graphics.
	 * @param g The graphics element to paint with
	 * @param s The scaling object. It maps the universe coordinates and dimensions to the container coordinate frame.
	 */
	abstract public void  draw(Graphics g, Float panelCoordinates);

	/**
	 * Signals end of episode
	 */
	public void endEpisode() {};
	
	/**
	 * Signals start of episode
	 */
	public void newEpisode() {};
	
	/**
	 * Signals end of trial
	 */
	public void endTrial() {};
	
	/**
	 * Signals start of trial
	 */
	public void newTrial() {};
	
	
	
	/**
	 * Updates data to be drawn in next cycle
	 * Drawers which draw information of only one cycle should use this function
	 * Drawers which draw info of multiple cycles should use appendData instead
	 * Update data only gets called when last cycle has been rendered, if not synchronizing it may skip
	 */
	abstract public void updateData();
	
	/**
	 * Appends data to be drawn
	 * Add's data to the list of data that that will be drawn (Example ratpath)
	 * useful for drawers that require full history of a variable (example ratpath)
	 * 
	 */
	public void appendData(){
		
	}
	
	
	/**
	 * Specifies whether the drawer should draw
	 */
	final public void setDraw(boolean doDraw) {
		this.doDraw = doDraw;
	}
	
	/**
	 * set world and panel coordinates
	 */
//	public void setDefaultCoordinates(Float defaultCoordinates) {
//		this.defaultWorldCoordinates = defaultCoordinates;
//	}
	
	public void setName(String name) {
		this.drawerName = name;
	}
	

}
