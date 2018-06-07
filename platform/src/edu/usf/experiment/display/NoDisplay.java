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
	public void addPanel(DrawPanel panel,String id,int gridx, int gridy, int gridwidth, int gridheight) {
	}

	@Override
	public void setupUniversePanel(BoundedUniverse bu) {
	}

	@Override
	public void addDrawer(String panelID, String drawerID, Drawer d) {
	}

	@Override
	public void addDrawer(String panelID, String drawerID, Drawer d, int pos) {
		
	}

	@Override
	public void newEpisode() {
	}

	@Override
	public void addKeyAction(int key, Runnable action) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sync(long cycle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void waitUntilDoneRendering() {
		// TODO Auto-generated method stub
		
	}
	

}
