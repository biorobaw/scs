package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geomgraph.Position;

import edu.usf.experiment.universe.GlobalCameraUniverse;

public class BrokenDrawer extends Drawer {

	
	public BrokenDrawer(){
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		if(!doDraw) return;
		Point p = s.scale(new Coordinate(0.5,0.5));
		

		g.setColor(Color.BLACK);
		g.drawString("Broken",	p.x,p.y);

	}

	@Override
	public void clearState() {
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub

		
	}
}
