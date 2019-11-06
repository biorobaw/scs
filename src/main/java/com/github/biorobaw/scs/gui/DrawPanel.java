package com.github.biorobaw.scs.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.github.biorobaw.scs.gui.utils.Window;

public class DrawPanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Margins to pad the component and ensure good visualization of elements in
	 * the borders
	 */
	float XMARGIN = 0;
	float YMARGIN = 0;
	ArrayList<Drawer> drawers = new ArrayList<>();
	
	String panelName = "";
	Display parent = null;
	
	long renderCycle = -10;
	
	
	//by default world coordinates for a plot are between -1 and 1 for both x and y coordinates
	private Window<Float> coordinateFrame;
	
	
	public DrawPanel(int sizeX, int sizeY) {
		setMinimumSize(new Dimension(sizeX, sizeY));
	}
	
	@Override
	public synchronized void paint(Graphics g) {		
		super.paint(g);
		
		long copyRenderCycle = getRenderCycle();
		
		// Define scaling factors
		Dimension panelRect = getSize();
		Window<Float> panelCoordinates = new Window<>(XMARGIN,YMARGIN,panelRect.width-2*XMARGIN,panelRect.height-2*YMARGIN);


		
		// Draw all layers
		for (Drawer d : drawers){
			//if (cBoxes.get(d).isSelected())
			if(executeDrawer(d)) {
				d.draw(g, panelCoordinates);
			}
		}
		
		if(parent!=null) parent.sync(copyRenderCycle);
		
	}
	
	public boolean executeDrawer(Drawer d) {
		return true;
	}
	
	public synchronized void addDrawer(Drawer d, int pos) {

		drawers.add(pos, d);
	}
	
	public synchronized void addDrawer(Drawer d){
		addDrawer(d, drawers.size());
	}
	
	public void removeDrawer(Drawer d) {
		drawers.remove(d);
	}
	
	public void clearState() {
		for (Drawer d : drawers)
			d.endEpisode();
	}
	
	public void setCoordinateFrame(float x, float y, float w, float h) {
		coordinateFrame = new Window<Float>(x,y,w,h);
	}
	
	public void setCoordinateFrame(Window<Float> frame) {
		coordinateFrame = frame;
	}
	
	public Window<Float> getCoordinateFrame() {
		return coordinateFrame;
	}
	
	public void setParent(Display parent) {
		this.parent = parent;
	}
	public synchronized void setRenderCycle(long cycle) {
		renderCycle = cycle;
	}
	
	public synchronized long getRenderCycle(){
		return renderCycle;
	}
	
	public void setPanelName(String name) {
		this.panelName = name;
	}

	
}
