package com.github.biorobaw.scs.gui;


import com.github.biorobaw.scs.gui.DrawPanel.GuiPanel;
import com.github.biorobaw.scs.gui.displays.scs_swing.DrawerSwing;
import com.github.biorobaw.scs.gui.utils.Window;

/**
 * This interface draws information on a graphics component. It can be used to paint layers of information over one panel.
 * @author martin,bucef
 *
 */
public abstract class Drawer {
	
	public boolean doDraw = true;
	public String drawerName;
	protected Window worldCoordinates;

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
	 * Updates data to be drawn in next render cycle.
	 * Note that multiple simulation cycles can be executed before next render cycle.
	 * If drawer needs data from all simulation cycles, use function {@link Drawer#appendData()} to append the data 
	 * to a temporal buffer and use this function to synchronize it with the data being drawn.
	 */
	abstract  public  void updateData();
	
	/**
	 * Appends data to be drawn
	 * Add's data to the list of data that that will be drawn.
	 * Ueful for drawers that require full history of a variable (example ratpath)
	 * @see {@link Drawer#updateData()} for more info
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
	
	public void setWorldCoordinates(Window w) {
		this.worldCoordinates = w;
	}

}
