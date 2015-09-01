//package edu.usf.ratsim.robot.naorobot;
//
//import java.awt.geom.Point2D;
//
//import javax.vecmath.Point3f;
//import javax.vecmath.Quat4f;
//
//import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
//import edu.usf.ratsim.robot.naorobot.protobuf.VisionListener;
//
//public class GlobalCameraUniv extends VirtUniverse{
//
//	private VisionListener vision;
//
//	public GlobalCameraUniv(String mazeResource) {
//		super(mazeResource);
//		
//		vision = VisionListener.getVisionListener();
//	}
//
//	@Override
//	public Point3f getRobotPosition() {
//		Point3f p = vision.getRobotPoint();
////		System.out.println(p);
//		setRobotPosition(new Point2D.Float(p.x, p.z), vision.getRobotOrientation());
//		return vision.getRobotPoint();
//	}
//
//	@Override
//	public Quat4f getRobotOrientation() {
//		Point3f p = vision.getRobotPoint();
//		setRobotPosition(new Point2D.Float(p.x, p.z), vision.getRobotOrientation());
//		return new Quat4f(0, 1, 0, vision.getRobotOrientation());
//	}
//
//	@Override
//	public float getRobotOrientationAngle() {
//		Point3f p = vision.getRobotPoint();
//		setRobotPosition(new Point2D.Float(p.x, p.z), vision.getRobotOrientation());
//		return vision.getRobotOrientation();
//	}
//
//	
//}
