package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;

public class BrokenDrawer extends Drawer {

	
	public BrokenDrawer(){
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		Scaler s = new Scaler(new java.awt.geom.Rectangle2D.Float(-1,-1,2,2), panelCoordinates, true);
		Point p = s.scale(new Coordinate(0,0));
		

		g.setColor(Color.BLACK);
		g.drawString("Broken",	p.x,p.y);

	}

	@Override
	public void endEpisode() {
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub

		
	}
}
