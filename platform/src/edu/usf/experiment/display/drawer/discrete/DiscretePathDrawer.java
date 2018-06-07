package edu.usf.experiment.display.drawer.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.UniverseLoader;

public class DiscretePathDrawer extends Drawer {

	private static final float HALF_SQUARE = 0.5f;
	private GlobalCameraUniverse u;
	private ArrayList<Coordinate> poses = new ArrayList<>();
	

	public DiscretePathDrawer(GlobalCameraUniverse gcu) {
		u = gcu;

	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		

		
		Point start = s.scale(poses.get(0));

		g.setColor(Color.DARK_GRAY);
		for (int i = 1; i < poses.size(); i++) {
			Point end = s.scale(poses.get(i));
			g.drawLine(start.x, start.y, end.x, end.y);
			start = end;
		}

	}

	@Override
	public void clearState() {
		poses.clear();
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		Coordinate pos = u.getRobotPosition();
		float centerx = (float) pos.x + HALF_SQUARE;
		float centery = (float) pos.y + HALF_SQUARE;
		pos = (new Coordinate(centerx, centery));
		poses.add(pos);
	}

}
