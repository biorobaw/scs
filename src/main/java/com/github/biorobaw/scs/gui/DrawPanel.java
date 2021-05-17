package com.github.biorobaw.scs.gui;

import java.util.ArrayList;

import com.github.biorobaw.scs.gui.utils.Window;

public class DrawPanel {
	
	static int next_id = 0; 			// id for next DrawPanel to be created
	final public int id; 				// draw panel id
	public String panelName = "";		// user friendly panel id designed by user
	protected Long renderCycle = -10L;	// cycle being rendered by the panel
	public Display parent   = null;  // pointer to parent to signal done rendering a given cycle
	
	public int min_size_x, min_size_y; 	// min size of panel in pixels
	public ArrayList<Drawer> drawers = new ArrayList<>(); // array list of drawers attached to the panel
	public GuiPanel gui_panel; 			// the gui panel correponding to this panel
	
	
	protected Window world_coordinates; // world coordinates
	protected Window local_coordinates = new Window(-1,-1,2,2); // local panel coordinates

	
	public DrawPanel(int min_size_x, int min_size_y) {
		this.min_size_x = min_size_x;
		this.min_size_y = min_size_y;
		this.id = next_id++;
		
	}
	
	public synchronized void addDrawer(Drawer d){
		drawers.add(drawers.size(), d); // add drawer to the panel's list of drawers
		gui_panel.addDrawer(d);			// add drawer to gui panel
	}
	
	public synchronized void setGuiPanel(GuiPanel gui_panel) {
		this.gui_panel = gui_panel;
	}
	
	public synchronized void removeDrawer(Drawer d) {
		drawers.remove(d);
	}
	
	/**
	 * Sets cycle being rendered for synchronization purposes
	 * @param cycle
	 */
	public void setRenderCycle(long cycle) {
		synchronized(renderCycle) {
			renderCycle = cycle;			
		}
	}



	
	public void setWorldCoordinates(float x, float y, float w, float h) {
		world_coordinates = new Window(x,y,w,h);
	}
	
	/**
	 * Sets the coordinate frame used by the panel
	 * @param frame
	 */
	public void setWorldCoordinates(Window frame) {
		world_coordinates = frame;
	}
	
	

	
	/**
	 * Gets the coordinate frame used by the panel
	 * @return
	 */
	public Window getWorldCoordinates() {
		return world_coordinates;
	}
	
	
	public Window getLocalCoordinates() {
		return local_coordinates;
	}
	
	/**
	 * Sets pointer to parent. Pointer used to signal done rendering cycle. 
	 * @param parent
	 */
	public void setParent(Display parent) {
		this.parent = parent;
	}
	
	public void setName(String name) {
		this.panelName = name;
	}
	
	/**
	 * Start a new render cycle and then paints the panel.
	 * When done, it signals its parent its done painting this cycle.
	 */
	public void paintPanel(Object... args) {
		
		long copyRenderCycle;
		synchronized(renderCycle) {
			copyRenderCycle = renderCycle;
		};
		
		synchronized(this) {
			// access to drawers needs to be synchronized since the list of drawers cannot be modified while traversed
			gui_panel.callDrawers(args);			
		}

		if(parent!=null) parent.signalPanelFinishedRendering(copyRenderCycle);
	}

	// Panel of the GUI framework must implement the following interface
	public interface GuiPanel {
		void addDrawer(Drawer drawer);
		void callDrawers(Object... args);
	}
	
	public void resetDefaultCoordinates() {
		this.world_coordinates = parent.defaultCoordinates.copy();
		this.local_coordinates = new Window(-1, -1, 2, 2);
	}
	
}
