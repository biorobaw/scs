package edu.usf.experiment.display.drawer.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.GlobalCameraUniverse;

public class DiscreteRobotDrawer extends Drawer {

	private static final float RADIUS = .4f;
	private static final float HALF_SQUARE = 0.5f;
	private GlobalCameraUniverse u;
	
	float angle = 0;
	Coordinate pos = new Coordinate(-1000000, -1000000);
	
	public DiscreteRobotDrawer(GlobalCameraUniverse gcu) {
		u = gcu;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		

		float centerx = (float) pos.x + HALF_SQUARE;
		float centery = (float) pos.y + HALF_SQUARE;
		Point center = s.scale(new Coordinate(centerx, centery));
		Point ul = s.scale(new Coordinate(centerx - RADIUS, centery + RADIUS));
		Point lr = s.scale(new Coordinate(centerx + RADIUS, centery - RADIUS));
		Point lineEnd = s.scale(new Coordinate(centerx + Math.cos(angle) * RADIUS, centery + Math.sin(angle) * RADIUS));

		g.setColor(Color.BLACK);
		g.drawOval(ul.x, ul.y, Math.abs(lr.x - ul.x), Math.abs(lr.y - ul.y));
		g.drawLine(center.x, center.y, lineEnd.x, lineEnd.y);
	}

	@Override
	public void clearState() {
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		pos = u.getRobotPosition();
		angle = u.getRobotOrientationAngle();
		
	}

}
