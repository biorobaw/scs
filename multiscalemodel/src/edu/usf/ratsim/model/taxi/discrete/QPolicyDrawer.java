package edu.usf.ratsim.model.taxi.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.model.PolicyModel;
import edu.usf.experiment.robot.affordance.AbsoluteAngleDiscreteAffordance;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.AffordanceRobot;

public class QPolicyDrawer implements Drawer {

	private static final float TRIANGLE_HEIGHT = .4f;
	private static final float GRID_HALF = 0.5f;
	private PolicyModel model;
	private ArrayList<Float> angles;

	public QPolicyDrawer(PolicyModel model, AffordanceRobot robot) {
		this.model = model;

		angles = new ArrayList<Float>();
		for (Affordance a : robot.getPossibleAffordances())
			if (a instanceof AbsoluteAngleDiscreteAffordance) {
				AbsoluteAngleDiscreteAffordance aada = (AbsoluteAngleDiscreteAffordance) a;
				angles.add((float) Math.atan2(aada.dy, aada.dx));
				// angles.add()
			}
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		Map<Point3f, Integer> vps = model.getPolicyPoints();

		for (Point3f p : vps.keySet()) {
			// Get the angle for the max action
			float angle = angles.get(vps.get(p));
			// Set up the triangle
			Point p1 = s.scale(
					rotateTranslatate(new Coordinate(TRIANGLE_HEIGHT / 2, 0), angle, p.x + GRID_HALF, p.y + GRID_HALF));
			Point p2 = s.scale(rotateTranslatate(new Coordinate(-TRIANGLE_HEIGHT / 2, TRIANGLE_HEIGHT / 2), angle,
					p.x + GRID_HALF, p.y + GRID_HALF));
			Point p3 = s.scale(rotateTranslatate(new Coordinate(-TRIANGLE_HEIGHT / 2, -TRIANGLE_HEIGHT / 2), angle,
					p.x + GRID_HALF, p.y + GRID_HALF));

			int[] xPoints = new int[3];
			int[] yPoints = new int[3];
			xPoints[0] = p1.x;
			xPoints[1] = p2.x;
			xPoints[2] = p3.x;
			yPoints[0] = p1.y;
			yPoints[1] = p2.y;
			yPoints[2] = p3.y;
			g.setColor(Color.BLACK);
			g.fillPolygon(xPoints, yPoints, 3);
		}
	}

	private Coordinate rotateTranslatate(Coordinate c, float a, float x, float y) {
		return new Coordinate(Math.cos(a) * c.x - Math.sin(a) * c.y + x, Math.sin(a) * c.x + Math.cos(a) * c.y + y);
	}

}
