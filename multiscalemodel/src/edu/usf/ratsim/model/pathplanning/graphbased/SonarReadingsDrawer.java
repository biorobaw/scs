package edu.usf.ratsim.model.pathplanning.graphbased;

import java.awt.Graphics;
import java.awt.Point;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.utils.GeomUtils;

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
		Point3f rPos = lRobot.getPosition();
		float robotOrient = lRobot.getOrientationAngle();
		
		Point readingStart = s.scale(rPos);
		for (int i = 0; i < readings.length; i++){
			float reading = readings[i];
			float angle = angles[i];
			float absAngle = robotOrient + angle;
			
			Point readingEnd = s.scale(new Coordinate(rPos.getX() + Math.cos(absAngle) * reading, rPos.getY() + Math.sin(absAngle) * reading));
			
			g.drawLine(readingStart.x, readingStart.y, readingEnd.x, readingEnd.y);
		}
	}
	
	@Override
	public void clearState() {
		
	}

}
