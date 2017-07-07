package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.LocalizableRobot;

public class PathDrawer implements Drawer {

	private LocalizableRobot robot;
	private LinkedList<Point> poses;

	public PathDrawer(LocalizableRobot robot){
		this.robot = robot;
		
		poses = new LinkedList<Point>();
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		Coordinate pos = robot.getPosition();
		
		float centerx = (float)pos.x;
		float centery = (float)pos.y;
		Point scaledPos = s.scale(new Coordinate(centerx, centery));
		poses.add(scaledPos);
		
		g.setColor(Color.DARK_GRAY);
		Point start = poses.get(0);
		for (int i = 1; i < poses.size(); i++){
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
