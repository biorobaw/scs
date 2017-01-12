package edu.usf.ratsim.robot.ssl;

import java.awt.geom.Point2D;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

public class GlobalCameraUniv extends VirtUniverse{

	private VisionProxy vision;

	public GlobalCameraUniv(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		vision = VisionProxy.getVisionProxy();
	}

	@Override
	public Point3f getRobotPosition() {
		Point3f p = vision.getRobotPoint();
//		System.out.println(p);
		setRobotPosition(new Point2D.Float(p.x, p.z), vision.getRobotOrientation());
		return vision.getRobotPoint();
	}

	@Override
	public Quat4f getRobotOrientation() {
		
		Point3f p = vision.getRobotPoint();
		setRobotPosition(new Point2D.Float(p.x, p.z), vision.getRobotOrientation());
		return GeomUtils.angleToRot(vision.getRobotOrientation());
	}

	@Override
	public float getRobotOrientationAngle() {
		Point3f p = vision.getRobotPoint();
		setRobotPosition(new Point2D.Float(p.x, p.z), vision.getRobotOrientation());
		return vision.getRobotOrientation();
	}

	public boolean isCamInfoFresh() {
		return vision.isInfoFresh();
	}

	
}
