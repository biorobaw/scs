package edu.usf.experiment.universe;

import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.utils.ElementWrapper;

public class DummyUniverse extends Universe {

	public DummyUniverse(ElementWrapper params, String logPath){
		super(params, logPath);
		System.out.println("Dummy universe created");
	}

	@Override
	public java.awt.geom.Rectangle2D.Float getBoundingRectangle() {
		return new Rectangle2D.Float(0f,0f,0f,0f);
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
	public void setRobotPosition(Float float1, float w) {
		// TODO Auto-generated method stub
		
	}
}
