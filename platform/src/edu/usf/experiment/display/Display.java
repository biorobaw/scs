package edu.usf.experiment.display;

import javax.swing.JPanel;

/**
 * This interface represents a means of displaying information. 
 * @author martin
 *
 */
public interface Display {

	/**
	 * Add a panel to display information or include controls.
	 * @param panel The JPanel to display
	 * @param gridx The grid x coordinate, see GridBagConstraints
	 * @param gridy The grid y coordinate, see GridBagConstraints
	 * @param gridwidth The grid width, see GridBagConstraints
	 * @param gridheight The grid height, see GridBagConstraints
	 */
	public void addPanel(JPanel panel, int gridx, int gridy, int gridwidth, int gridheight);
	
	/**
	 * Log a certain string using the display specific method (e.g. textbox or system.out)
	 * @param s
	 */
	public void log(String s);
	
	/**
	 * Update the display to reflect the most current data
	 */
	public void repaint();
	
}
