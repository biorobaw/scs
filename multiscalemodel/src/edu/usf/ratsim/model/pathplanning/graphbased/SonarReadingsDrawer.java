package edu.usf.ratsim.model.pathplanning.graphbased;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.Universe;

public class SonarReadingsDrawer extends Drawer {

	private Robot robot;
	private SonarRobot sRobot;
	private LocalizableRobot lRobot;
	
	Coordinate rPos = new Coordinate(-1000000,-1000000);
	float robotOrient = 0 ;
	float[] readings = {};
	float[] angles = {};

	public SonarReadingsDrawer(Robot robot) {
		this.robot = robot;
		this.sRobot = (SonarRobot)robot;
		this.lRobot = (LocalizableRobot)robot;
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)Universe.getUniverse();
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);

		

		g.setColor(Color.RED);
		for (int i = 0; i < readings.length; i++) {
			float reading = readings[i];
			float angle = angles[i];
			float absAngle = robotOrient + angle;

			Point readingStart = s.scale(new Coordinate(rPos.x + Math.cos(absAngle) * robot.getRadius(),
					rPos.y + Math.sin(absAngle) * robot.getRadius()));
			Point readingEnd = s.scale(new Coordinate(rPos.x + Math.cos(absAngle) * (robot.getRadius() + reading),
					rPos.y + Math.sin(absAngle) * (robot.getRadius() + reading)));

			g.drawLine(readingStart.x, readingStart.y, readingEnd.x, readingEnd.y);
		}
	}

	@Override
	public void endEpisode() {

	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		rPos = lRobot.getPosition();
		robotOrient = lRobot.getOrientationAngle();
		readings = sRobot.getSonarReadings();
		angles = sRobot.getSonarAngles();
	}

}