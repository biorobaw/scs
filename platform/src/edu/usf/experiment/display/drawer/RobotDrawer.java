package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geomgraph.Position;

import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.UniverseLoader;

public class RobotDrawer extends Drawer {

	private static final float RADIUS = .075f;
	private GlobalCameraUniverse u;
	
	Coordinate position = new Coordinate(-1000000,-1000000);
	Float angle = 0f;
	
	public RobotDrawer(GlobalCameraUniverse gcu){
		u = gcu;
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		if(bu==null) return;
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		Point p = s.scale(position);
		

		g.setColor(Color.BLACK);
		g.drawOval(p.x - (int) (RADIUS * s.xscale), p.y - (int) (RADIUS * s.yscale),
				(int) (RADIUS * s.xscale * 2), (int) (RADIUS * s.yscale * 2));
		g.drawLine(p.x, p.y, p.x + (int) (RADIUS * Math.cos(angle) * s.xscale),
				p.y - (int) (RADIUS * Math.sin(angle) * s.yscale));
	}

	@Override
	public void endEpisode() {
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		position = u.getRobotPosition();
		angle = u.getRobotOrientationAngle();
		
	}
}
