package edu.usf.experiment.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.morse.MorseUtils;
import edu.usf.experiment.universe.morse.PosSensorProxy;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;

public class MorseRobot extends LocalizableRobot {

	private PosSensorProxy posSensor;
	private MorseControllerProxy wayptsCtrl;

	public MorseRobot(ElementWrapper params) {
		super(params);
		
		Map<String, Integer> streamPorts = MorseUtils.getStreamPorts();
		System.out.println(streamPorts.toString());
		if (streamPorts.containsKey("robot.pose")) {
			System.out.println("[+] Starting pose sensor proxy");
			posSensor = new PosSensorProxy(streamPorts.get("robot.pose"));
			posSensor.start();
		} else {
			throw new RuntimeException("No robot pose sensor available");
		}
		
		if (streamPorts.containsKey("robot.wpy")) {
			System.out.println("[+] Starting waipoint controller proxy");
			wayptsCtrl = new MorseControllerProxy(posSensor, streamPorts.get("robot.wpy"));
		} else {
			throw new RuntimeException("No robot waypoint controller available");
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
		wayptsCtrl.stepForward();
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
		for(Affordance a : possibleAffordances)
			a.setRealizable(true);
		return possibleAffordances;
	}

	@Override
	public void executeAffordance(Affordance selectedAction, Subject sub) {
		if (selectedAction instanceof ForwardAffordance){
			System.out.println("Executing forward affordance");
			forward(((ForwardAffordance)selectedAction).getDistance());
		} else {
			System.out.println(selectedAction);
			forward(0);
		}
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
