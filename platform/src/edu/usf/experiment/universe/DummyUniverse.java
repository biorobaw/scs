package edu.usf.experiment.universe;

import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.ElementWrapper;

public class DummyUniverse extends Universe implements GlobalCameraUniverse, BoundedUniverse {

	public DummyUniverse(ElementWrapper params, String logPath){
		System.out.println("Dummy universe created");
	}

	@Override
	public Coordinate getRobotPosition() {
		return new Coordinate();
	}

	@Override
	public float getRobotOrientationAngle() {
		return 0;
	}


	@Override
	public Rectangle2D.Float getBoundingRect() {
		return new Rectangle2D.Float(0, 0, 1, 1);
	}

	@Override
	public void setBoundingRect(java.awt.geom.Rectangle2D.Float boundingRect) {
		
	}

	@Override
	public void step() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRobot(Robot robot) {
		// TODO Auto-generated method stub
		
	}
	
	@Override 
	public void clearState() {
		
	}
}
