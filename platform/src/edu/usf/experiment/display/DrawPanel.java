package edu.usf.experiment.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.RandomSingleton;

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
	
	int renderCycle = -10;
	
	
	//by default world coordinates for a plot are between -1 and 1 for both x and y coordinates
	private Float coordinateFrame = new Float(-1, -1, 2, 2);
	
	
	public DrawPanel() {
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		
		
		super.paint(g);
		
		// Define scaling factors
		Float worldCoordinates = getCoordinateFrame();
		Dimension panelRect = getSize();
		Float panelCoordinates = new Float(XMARGIN,YMARGIN,panelRect.width-2*XMARGIN,panelRect.height-2*YMARGIN);

		Scaler s = new Scaler(worldCoordinates,panelCoordinates,true);		

		
		// Draw all layers
//		g.setColor(Color.RED);
//		g.drawRect(0, 0, panelRect.width-1, panelRect.height-1);
		for (Drawer d : drawers){
			//if (cBoxes.get(d).isSelected())
			if(executeDrawer(d)) {
				d.setCoordinateSystems(worldCoordinates,panelCoordinates);
				d.draw(g, s);
			}
		}
		
		if(parent!=null) parent.sync(renderCycle);
		
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
			d.clearState();
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
	public void setRenderCycle(int cycle) {
		renderCycle = cycle;
	}

	
}
