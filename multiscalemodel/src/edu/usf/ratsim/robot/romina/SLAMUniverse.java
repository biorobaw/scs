//package edu.usf.ratsim.robot.romina;
//
//import java.awt.geom.Point2D;
//import java.awt.geom.Point2D.Float;
//
//import javax.vecmath.Point3f;
//import javax.vecmath.Quat4f;
//
//import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
//
//public class SLAMUniverse extends VirtUniverse{
//
//	private Romina romina;
//	private boolean rominaAte;
//
//	public SLAMUniverse(String mazeResource) {
//		super(mazeResource);
//	}
//
//	@Override
//	public Point3f getRobotPosition() {
//		Point3f p = romina.getRobotPoint();
////		System.out.println(p);
//		super.setRobotPosition(new Point2D.Float(p.x, p.y), romina.getRobotOrientation());
//		return romina.getRobotPoint();
//	}
//
//	@Override
//	public Quat4f getRobotOrientation() {
//		Point3f p = romina.getRobotPoint();
//		super.setRobotPosition(new Point2D.Float(p.x, p.y), romina.getRobotOrientation());
//		return new Quat4f(0, 1, 0, romina.getRobotOrientation());
//	}
//
//	@Override
//	public float getRobotOrientationAngle() {
//		Point3f p = romina.getRobotPoint();
//		super.setRobotPosition(new Point2D.Float(p.x, p.y), romina.getRobotOrientation());
//		return romina.getRobotOrientation();
//	}
//
//	public void setRominaRobot(Romina romina) {
//		this.romina = romina;
//	}
//
//	@Override
//	public boolean hasRobotFoundFood() {
//		return romina.hasFoundFood();
//	}
//
//	@Override
//	public boolean hasRobotAte() {
//		return rominaAte;
//	}
//
//	@Override
//	public void robotEat() {
//		if (romina.hasFoundFood())
//			rominaAte = true;
//	}
//
//	@Override
//	public void clearRobotAte() {
//		rominaAte = false;
//		romina.invalidateResponse();
//	}
//
//	@Override
//	public boolean isRobotCloseToAFeeder() {
//		return romina.isCloseToAFeeder();
//	}
//
//	@Override
//	public void setRobotPosition(Float pos, float angle) {
//		super.setRobotPosition(pos, angle);
//		
//		romina.resetPosition(pos, angle);
//	}
//	
//	
//	
//}
