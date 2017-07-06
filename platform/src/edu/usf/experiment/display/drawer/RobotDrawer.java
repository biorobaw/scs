package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.vecmath.Point3f;

import edu.usf.experiment.universe.GlobalCameraUniverse;

public class RobotDrawer implements Drawer {

	private static final float RADIUS = .05f;
	private GlobalCameraUniverse u;
	
	public RobotDrawer(GlobalCameraUniverse gcu){
		u = gcu;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		Point3f pos = u.getRobotPosition();
		Point p = s.scale(pos);
		float angle = u.getRobotOrientationAngle();

		g.setColor(Color.BLACK);
		g.drawOval(p.x - (int) (RADIUS * s.xscale), p.y - (int) (RADIUS * s.yscale),
				(int) (RADIUS * s.xscale * 2), (int) (RADIUS * s.yscale * 2));
		g.drawLine(p.x, p.y, p.x + (int) (RADIUS * Math.cos(angle) * s.xscale),
				p.y - (int) (RADIUS * Math.sin(angle) * s.yscale));
	}

	@Override
	public void clearState() {
		
	}
}
