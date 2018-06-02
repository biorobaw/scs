package edu.usf.ratsim.model.pathplanning.graphbased;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.SonarRobot;

public class SonarReadingsDrawer extends Drawer {

	private SonarRobot sRobot;
	private LocalizableRobot lRobot;
	
	Coordinate rPos = new Coordinate(-1000000,-1000000);
	float robotOrient = 0 ;
	float[] readings = {};
	float[] angles = {};

	public SonarReadingsDrawer(SonarRobot robot, LocalizableRobot lRobot) {
		this.sRobot = robot;
		this.lRobot = lRobot;
	}

	@Override
	public void draw(Graphics g, Scaler s) {

		

		g.setColor(Color.RED);
		for (int i = 0; i < readings.length; i++) {
			float reading = readings[i];
			float angle = angles[i];
			float absAngle = robotOrient + angle;

			Point readingStart = s.scale(new Coordinate(rPos.x + Math.cos(absAngle) * sRobot.getRadius(),
					rPos.y + Math.sin(absAngle) * sRobot.getRadius()));
			Point readingEnd = s.scale(new Coordinate(rPos.x + Math.cos(absAngle) * (sRobot.getRadius() + reading),
					rPos.y + Math.sin(absAngle) * (sRobot.getRadius() + reading)));

			g.drawLine(readingStart.x, readingStart.y, readingEnd.x, readingEnd.y);
		}
	}

	@Override
	public void clearState() {

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
