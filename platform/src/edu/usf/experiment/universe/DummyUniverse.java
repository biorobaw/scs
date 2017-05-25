package edu.usf.experiment.universe;

import java.awt.geom.Rectangle2D;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.ElementWrapper;

public class DummyUniverse implements GlobalCameraUniverse, BoundedUniverse {

	public DummyUniverse(ElementWrapper params, String logPath){
		System.out.println("Dummy universe created");
	}

	@Override
	public Point3f getRobotPosition() {
		return new Point3f();
	}

	@Override
	public Quat4f getRobotOrientation() {
		return new Quat4f();
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
}
