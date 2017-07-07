package edu.usf.experiment.display.drawer.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.GlobalCameraUniverse;

public class DiscretePathDrawer implements Drawer {

	private static final float HALF_SQUARE = 0.5f;
	private GlobalCameraUniverse u;
	private LinkedList<Point> poses;

	public DiscretePathDrawer(GlobalCameraUniverse gcu) {
		u = gcu;

		poses = new LinkedList<Point>();
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		Coordinate pos = u.getRobotPosition();

		float centerx = (float) pos.x + HALF_SQUARE;
		float centery = (float) pos.y + HALF_SQUARE;
		Point scaledPos = s.scale(new Coordinate(centerx, centery));
		poses.add(scaledPos);

		g.setColor(Color.DARK_GRAY);
		Point start = poses.get(0);
		for (int i = 1; i < poses.size(); i++) {
			Point end = poses.get(i);
			g.drawLine(start.x, start.y, end.x, end.y);
			start = end;
		}

	}

	@Override
	public void clearState() {
		poses.clear();
	}

}
