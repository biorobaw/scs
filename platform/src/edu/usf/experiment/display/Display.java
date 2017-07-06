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
	public void addPlot(JComponent component, int gridx, int gridy, int gridwidth, int gridheight);
	
	/**
	 * Log a certain string using the display specific method (e.g. textbox or system.out)
	 * @param s
	 */
	public void log(String s);
	
	/**
	 * Update the display to reflect the most current data
	 */
	public void repaint();
	
	/**
	 * Sets up the universe panel
	 * @param bu A bounded universe needed to compute the scaling factors
	 */
	public void setupUniversePanel(BoundedUniverse bu);
	
	/**
	 * Adds a drawer layer to the universe panel
	 * @param d
	 */
	public void addUniverseDrawer(Drawer d);

	/**
	 * Adds a drawer layer at the specified position. Lower positions are painted first.
	 * @param d
	 * @param pos
	 */
	public void addUniverseDrawer(Drawer d, int pos);
	
	/**
	 * Tells the display that a new episode began. Some drawers might have to clear stateful information due to this.
	 */
	public void newEpisode();
}
