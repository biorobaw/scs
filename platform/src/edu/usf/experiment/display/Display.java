package edu.usf.experiment.display;

import java.util.HashMap;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.universe.BoundedUniverse;

/**
 * This interface represents a means of displaying information. 
 * @author martin
 *
 */
public abstract class Display {

	/**
	 * A global instance for the display object
	 */
	private static Display display;

	public static void setDisplay(Display display) {
		Display.display = display;
	}

	public static Display getDisplay(){
		return display;
	}
	
	
	public HashMap<String,Drawer> drawers = new HashMap<>();
	
	/**
	 * specifies whether the display needs to be synchronized with simulation
	 */
//	public final static AtomicBoolean synchronizeDisplay = new AtomicBoolean(false);
	
	/**
	 * Add a component (e.g. a panel) to display information or include controls.
	 * @param panel The JPanel to display
	 * @param gridx The grid x coordinate, see GridBagConstraints
	 * @param gridy The grid y coordinate, see GridBagConstraints
	 * @param gridwidth The grid width, see GridBagConstraints
	 * @param gridheight The grid height, see GridBagConstraints
	 */
	public abstract void addPanel(DrawPanel panel,String id,int gridx, int gridy, int gridwidth, int gridheight);
	
	/**
	 * Log a certain string using the display specific method (e.g. textbox or system.out)
	 * @param s
	 */
	public abstract void log(String s);
	
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
	public abstract void repaint();
	
	/*
	 * The function should update all data to be drawn
	 * This function should execute atomically from the rendering cycle
	 * That is, as long as the function is running no rendering should be done
	 */
	public abstract void updateData();
	
	/**
	 * Sets up the universe panel
	 * @param bu A bounded universe needed to compute the scaling factors
	 */
	public abstract void setupUniversePanel(BoundedUniverse bu);
	
	/**
	 * Adds a drawer layer to the universe panel
	 * @param d
	 */
	public abstract void addDrawer(String panelID, String drawerID, Drawer d);

	/**
	 * Adds a drawer layer at the specified position. Lower positions are painted first.
	 * @param d
	 * @param pos
	 */
	public abstract void addDrawer(String panelID, String drawerID, Drawer d, int pos);
	
	/**
	 * Tells the display that a new episode began. Some drawers might have to clear stateful information due to this.
	 */
	synchronized public void newEpisode() {
		for(Drawer d : drawers.values()) d.newEpisode();
	};
	
	synchronized public void endEpisode() {
		for(Drawer d : drawers.values()) d.endEpisode();
	}
	
	public void newTrial() {
		for(Drawer d : drawers.values()) d.newTrial();
	}
	
	public void endTrial() {
		for(Drawer d : drawers.values()) d.endTrial();
	}
	
	/*
	 * Binds a runnable to a key, when the key is press the action is executed
	 */
	public abstract void addKeyAction(int key,Runnable action);
	
	public abstract void sync(long cycle);//function to be called by draw panels when they are done rendering for synchronization
	

}
