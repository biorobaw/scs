package edu.usf.experiment.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.morse.MorseUtils;
import edu.usf.experiment.universe.morse.PosSensorProxy;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;

public class MorseRobot extends LocalizableRobot {

	private PosSensorProxy posSensor;
	private WaypointsControllerProxy wayptsCtrl;

	public MorseRobot(ElementWrapper params) {
		super(params);
		
		Map<String, Integer> streamPorts = MorseUtils.getStreamPorts();
		
		if (streamPorts.containsKey("robot.pose_001")) {
			System.out.println("[+] Starting pose sensor proxy");
			posSensor = new PosSensorProxy(streamPorts.get("robot.pose_001"));
			posSensor.start();
		} else {
			throw new RuntimeException("No robot pose sensor available");
		}
		
		if (streamPorts.containsKey("robot.waypoint")) {
			System.out.println("[+] Starting waipoint controller proxy");
			wayptsCtrl = new WaypointsControllerProxy(posSensor, streamPorts.get("robot.waypoint"));
		} else {
			throw new RuntimeException("No robot pose sensor available");
		}
	}

	@Override
	public Point3f getPosition() {
		return posSensor.getPosition();
	}

	@Override
	public float getOrientationAngle() {
		return posSensor.getOrientation();
	}

	@Override
	public Quat4f getOrientation() {
		return GeomUtils.angleToRot(getOrientationAngle());
	}

	@Override
	public boolean seesFeeder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Feeder> getAllFeeders() {
		return new LinkedList<Feeder>();
	}

	@Override
	public float getDistanceToClosestWall() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getHalfFieldView() {
		return 30;
	}

	@Override
	public int closeToNoseWalls(float distToConsider) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Feeder getFeederInFront() {
		return null;
	}

	@Override
	public void eat() {

	}

	@Override
	public boolean hasFoundFood() {
		return false;
	}

	@Override
	public void startRobot() {
		// TODO Auto-generated method stub

	}

	@Override
	public void forward(float distance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rotate(float degrees) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Landmark> getLandmarks() {
		return new LinkedList<Landmark>();
	}

	@Override
	public Feeder getFlashingFeeder() {
		return null;
	}

	@Override
	public boolean seesFlashingFeeder() {
		return false;
	}

	@Override
	public Feeder getClosestFeeder(int lastFeeder) {
		return null;
	}

	@Override
	public boolean isFeederClose() {
		return false;
	}

	@Override
	public List<Affordance> checkAffordances(List<Affordance> possibleAffordances) {
		// TODO Auto-generated method stub
		return possibleAffordances;
	}

	@Override
	public void executeAffordance(Affordance selectedAction, Subject sub) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Feeder> getVisibleFeeders(int[] is) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Point3f> getVisibleWallEnds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLastAteFeeder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLastTriedToEatFeeder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
