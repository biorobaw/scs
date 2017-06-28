package edu.usf.experiment.display.drawer.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.GlobalCameraUniverse;

public class DiscreteRobotDrawer implements Drawer {

	private static final float RADIUS = .4f;
	private static final float HALF_SQUARE = 0.5f;
	private GlobalCameraUniverse u;
	
	public DiscreteRobotDrawer(GlobalCameraUniverse gcu){
		u = gcu;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		Point3f pos = u.getRobotPosition();
		float angle = u.getRobotOrientationAngle();
		
		float centerx = pos.x + HALF_SQUARE;
		float centery = pos.y + HALF_SQUARE;
		Point center = s.scale(new Coordinate(centerx, centery));
		Point ul = s.scale(new Coordinate(centerx - RADIUS, centery + RADIUS));
		Point lr = s.scale(new Coordinate(centerx + RADIUS, centery - RADIUS));
		Point lineEnd = s.scale(new Coordinate(centerx + Math.cos(angle) * RADIUS, centery + Math.sin(angle) * RADIUS));
		
		
		g.setColor(Color.BLACK);
		g.drawOval(ul.x, ul.y, Math.abs(lr.x - ul.x), Math.abs(lr.y - ul.y));
		g.drawLine(center.x, center.y, lineEnd.x, lineEnd.y);
	}

}
