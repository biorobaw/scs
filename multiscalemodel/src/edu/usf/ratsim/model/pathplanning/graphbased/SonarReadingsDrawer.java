package edu.usf.ratsim.model.pathplanning.graphbased;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.SonarRobot;

public class SonarReadingsDrawer implements Drawer {

	private SonarRobot sRobot;
	private LocalizableRobot lRobot;

	public SonarReadingsDrawer(SonarRobot robot, LocalizableRobot lRobot) {
		this.sRobot = robot;
		this.lRobot = lRobot;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		float[] readings = sRobot.getSonarReadings();
		float[] angles = sRobot.getSonarAngles();
		Coordinate rPos = lRobot.getPosition();
		float robotOrient = lRobot.getOrientationAngle();

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

}
