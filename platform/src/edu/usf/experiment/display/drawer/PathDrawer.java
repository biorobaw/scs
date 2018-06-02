package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;

public class PathDrawer extends Drawer {

	private LocalizableRobot robot;
	private LinkedList<Coordinate> poses;

	public PathDrawer(LocalizableRobot robot){
		this.robot = robot;
		
		poses = new LinkedList<Coordinate>();
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		if(!doDraw) return;
		

		g.setColor(Color.DARK_GRAY);
		Point start = s.scale(poses.get(0));
		for (int i = 1; i < poses.size(); i++){
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
		Coordinate pos = robot.getPosition();
		poses.add(pos);
		
	}

}
