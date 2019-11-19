package com.github.biorobaw.scs.gui;

import java.util.HashMap;

import com.github.biorobaw.scs.gui.utils.Window;
import com.github.biorobaw.scs.tasks.cycle.CycleTask;
import com.github.biorobaw.scs.utils.files.XML;

/**
 * This interface represents a means of displaying information. 
 * @author martin, bucef
 *
 */
public abstract class Display extends CycleTask {
	
	protected Window<Float> defaultCoordinates = new Window<Float>(-1f, -1f, 2f, 2f);
	public HashMap<String,Drawer> drawers = new HashMap<>();
	protected boolean syncDisplay = false; // whether sync the display with the simulation
	
	
	public Display(XML xml) {
		super(xml);
		if(xml.hasAttribute("window")) {
			var w = xml.getFloatArrayAttribute("window");
			defaultCoordinates = new Window<Float>(w[0],w[1],w[2],w[3]);
		}
		
		if(xml.hasAttribute("syncDisplay")) {
			syncDisplay = xml.getBooleanAttribute("syncDisplay");
		}
	}

	
	/**
	 * Add a component (e.g. a panel) to display information or include controls.
	 * @param panel The JPanel to display
	 * @param gridx The grid x coordinate, see GridBagConstraints
	 * @param gridy The grid y coordinate, see GridBagConstraints
	 * @param gridwidth The grid width, see GridBagConstraints
	 * @param gridheight The grid height, see GridBagConstraints
	 */
	public void addPanel(DrawPanel panel,String id,int gridx, int gridy, int gridwidth, int gridheight) {
		if(panel.getCoordinateFrame()==null) 
			panel.setCoordinateFrame(defaultCoordinates);
	};
	
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
	@Override
	synchronized public void newEpisode() {
		for(Drawer d : drawers.values()) d.newEpisode();
	};
	
	@Override
	synchronized public void endEpisode() {
		for(Drawer d : drawers.values()) d.endEpisode();
	}
	
	@Override
	public void newTrial() {
		for(Drawer d : drawers.values()) d.newTrial();
	}
	
	@Override
	public void endTrial() {
		for(Drawer d : drawers.values()) d.endTrial();
	}
	
	/*
	 * Binds a runnable to a key, when the key is press the action is executed
	 */
	public abstract void addKeyAction(int key,Runnable action);
	
	public abstract void sync(long cycle);//function to be called by draw panels when they are done rendering for synchronization
	
	/**
	 * Since display is a cycle script, the run function is an alias of updateData
	 */
	public long perform() {
		updateData();
		return 0;
	}
	
	/**
	 * Displays have the least priority so that they always run last
	 */
	@Override
	final public int getPriority() {
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Methods to close the gui
	 */
	public void close() {}
}
