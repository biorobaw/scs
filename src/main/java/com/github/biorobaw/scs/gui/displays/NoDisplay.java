package com.github.biorobaw.scs.gui.displays;

import com.github.biorobaw.scs.gui.Display;
import com.github.biorobaw.scs.gui.DrawPanel;
import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.utils.XML;

/**
 * A dummy display to be able to run headless.
 * @author martin
 *
 */
public class NoDisplay extends Display {

	public NoDisplay(XML xml) {
		super(xml);
	}
	
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
		
	}
	
	@Override
	public void updateData() {
		
	}

	@Override
	public void sync(long cycle) {
		
	}


	

}
