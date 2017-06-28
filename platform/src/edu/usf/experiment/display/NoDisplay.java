package edu.usf.experiment.display;

import javax.swing.JComponent;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.universe.BoundedUniverse;

/**
 * A dummy display to be able to run headless.
 * @author martin
 *
 */
public class NoDisplay implements Display {

	@Override
	public void log(String s) {
		System.out.println(s);		
	}

	@Override
	public void repaint() {
	}

	@Override
	public void addPlot(JComponent component, int gridx, int gridy, int gridwidth, int gridheight) {
	}

	@Override
	public void setupUniversePanel(BoundedUniverse bu) {
	}

	@Override
	public void addUniverseDrawer(Drawer d) {
	}

	@Override
	public void addUniverseDrawer(Drawer d, int pos) {
		
	}

}
