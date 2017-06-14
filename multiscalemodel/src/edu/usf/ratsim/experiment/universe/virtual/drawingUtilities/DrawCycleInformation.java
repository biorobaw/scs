package edu.usf.ratsim.experiment.universe.virtual.drawingUtilities;

import java.awt.Color;
import java.awt.FontMetrics;

import edu.usf.experiment.Globals;



public class DrawCycleInformation extends DrawingFunction {
	
	int x;
	int y;
	int size;
	
	/**
	 * Draws a polar graph
	 * @param x		 top left x coord
	 * @param y		 top left y coord
	 */
	
	public DrawCycleInformation(int _x,int _y, int _size){
		
		x = _x;
		y = _y;
		size = _size;
		

	}

	@Override
	public void run() {
		if(!active) return;
		// TODO Auto-generated method stub
		
		graphics.setColor(Color.white);
//		graphics.setFont(font);
		graphics.drawString("RatID:   " + Globals.getInstance().get("group").toString() + " - " +  Globals.getInstance().get("subName").toString(), x, y);
		graphics.drawString("Trial:   " + Globals.getInstance().get("trial").toString(), x , y+(size+2));
		graphics.drawString("Episode: " + Globals.getInstance().get("episode").toString(), x, y+2*(size+2));
		graphics.drawString("Cycle:   " + Globals.getInstance().get("cycle").toString(), x, y+3*(size+2));
//		graphics.drawString("Cycle: " + Globals.getInstance().get("cycle").toString(), x, y);
		
		
		graphics.flush(false);
		
		
	}
	
	public void setActive(boolean active){
		this.active = active;
	}

}
