package edu.usf.experiment.display;

import javax.swing.JComponent;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.universe.BoundedUniverse;

/**
 * This interface represents a means of displaying information. 
 * @author martin
 *
 */
public interface Display {

	/**
	 * Add a component (e.g. a panel) to display information or include controls.
	 * @param panel The JPanel to display
	 * @param gridx The grid x coordinate, see GridBagConstraints
	 * @param gridy The grid y coordinate, see GridBagConstraints
	 * @param gridwidth The grid width, see GridBagConstraints
	 * @param gridheight The grid height, see GridBagConstraints
	 */
	public void addPanel(DrawPanel panel,String id,int gridx, int gridy, int gridwidth, int gridheight);
	
	/**
	 * Log a certain string using the display specific method (e.g. textbox or system.out)
	 * @param s
	 */
	public void log(String s);
	
	/**
	 * Update the display to reflect the most current data 
	 * ACTUALLY : depending on system, it invalidates frame 
	 * so that it will be updated in next rendering cycle
	 * 
	 * Depending on system, the function might or might not be synchronous
	 * If it is synchronous there are no issues
	 * If it is not synchronous, the display may show data of multiple cycles
	 * Programmer should (probably must) make sure all data drawn belongs to same cycle
	 * see function updateData
	 */
	public void repaint();
	
	/*
	 * The function should update all data to be drawn
	 * This function should execute atomically from the rendering cycle
	 * That is, as long as the function is running no rendering should be done
	 */
	public void updateData();
	
	/**
	 * Sets up the universe panel
	 * @param bu A bounded universe needed to compute the scaling factors
	 */
	public void setupUniversePanel(BoundedUniverse bu);
	
	/**
	 * Adds a drawer layer to the universe panel
	 * @param d
	 */
	public void addDrawer(String panelID, String drawerID, Drawer d);

	/**
	 * Adds a drawer layer at the specified position. Lower positions are painted first.
	 * @param d
	 * @param pos
	 */
	public void addDrawer(String panelID, String drawerID, Drawer d, int pos);
	
	/**
	 * Tells the display that a new episode began. Some drawers might have to clear stateful information due to this.
	 */
	public void newEpisode();
	
	/*
	 * Binds a runnable to a key, when the key is press the action is executed
	 */
	public void addKeyAction(int key,Runnable action);
	
	public void sync(int cycle);//function to be called by draw panels when they are done rendering for synchronization
	public void waitUntilDoneRendering();
}
