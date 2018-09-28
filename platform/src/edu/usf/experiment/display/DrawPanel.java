package edu.usf.experiment.display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;

import javax.swing.JPanel;

import edu.usf.experiment.display.drawer.Drawer;

public class DrawPanel extends JPanel{
	
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
	private Float coordinateFrame = new Float(-1, -1, 2, 2);
	
	
	public DrawPanel(int sizeX, int sizeY) {
		setMinimumSize(new Dimension(sizeX, sizeY));
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		
		
		super.paint(g);
		
		long copyRenderCycle = getRenderCycle();
		
		// Define scaling factors
		Dimension panelRect = getSize();
		Float panelCoordinates = new Float(XMARGIN,YMARGIN,panelRect.width-2*XMARGIN,panelRect.height-2*YMARGIN);


		
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
	
	public void addDrawer(Drawer d, int pos) {

		drawers.add(pos, d);
	}
	
	public void addDrawer(Drawer d){
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
		coordinateFrame = new Float(x,y,w,h);
	}
	
	public void setCoordinateFrame(Float frame) {
		coordinateFrame = frame;
	}
	
	public Float getCoordinateFrame() {
		return coordinateFrame;
	}
	
	public void setParent(Display parent) {
		this.parent = parent;
	}
	public synchronized void setRenderCycle(int cycle) {
		renderCycle = cycle;
	}
	
	public synchronized long getRenderCycle(){
		return renderCycle;
	}

	
}
