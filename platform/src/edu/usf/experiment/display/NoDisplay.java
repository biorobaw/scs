package edu.usf.experiment.display;

import javax.swing.JPanel;

/**
 * A dummy display to be able to run headless.
 * @author martin
 *
 */
public class NoDisplay implements Display {

	@Override
	public void addPanel(JPanel panel, int gridx, int gridy, int gridwidth, int gridheight) {
		// Do nothing
	}

	@Override
	public void log(String s) {
		System.out.println(s);		
	}

	@Override
	public void repaint() {
		// Do nothing
	}

}
