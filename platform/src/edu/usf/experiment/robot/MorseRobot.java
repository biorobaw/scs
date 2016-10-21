package edu.usf.experiment.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.morse.IRSensorProxy;
import edu.usf.experiment.universe.morse.MorseUtils;
import edu.usf.experiment.universe.morse.PosSensorProxy;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;

public class MorseRobot extends LocalizableRobot {

	private static final float FORWARD_THRS = .3f;
	private static final float TURN_THRS = .2f;
	private PosSensorProxy posSensor;
	private MorseControllerProxy robCtrl;
	private IRSensorProxy leftIR;
	private IRSensorProxy rightIR;
	private IRSensorProxy frontIR;

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
		
		if (streamPorts.containsKey("robot.vw")) {
			System.out.println("[+] Starting waipoint controller proxy");
			robCtrl = new MorseControllerProxy(posSensor);
		} else {
			throw new RuntimeException("No robot waypoint controller available");
		}
		
		if (streamPorts.containsKey("robot.leftir")) {
			System.out.println("[+] Starting left IR sensor proxy");
			leftIR = new IRSensorProxy(streamPorts.get("robot.leftir"));
			leftIR.start();
		} else {
			throw new RuntimeException("No left IR available");
		}
		
		if (streamPorts.containsKey("robot.rightir")) {
			System.out.println("[+] Starting right IR sensor proxy");
			rightIR = new IRSensorProxy(streamPorts.get("robot.rightir"));
			rightIR.start();
		} else {
			throw new RuntimeException("No right IR available");
		}
		
		if (streamPorts.containsKey("robot.frontir")) {
			System.out.println("[+] Starting front IR sensor proxy");
			frontIR = new IRSensorProxy(streamPorts.get("robot.frontir"));
			frontIR.start();
		} else {
			throw new RuntimeException("No front IR available");
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
		return 30;
	}

	@Override
	public float getHalfFieldView() {
		return 30;
	}

	@Override
	public int closeToNoseWalls(float distToConsider) {
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
		robCtrl.stepForward();
	}

	@Override
	public void rotate(float degrees) {
		robCtrl.turnStep(degrees);
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
			if (a instanceof ForwardAffordance){
				
				a.setRealizable(frontIR.getDistance() > FORWARD_THRS);
			} else if (a instanceof TurnAffordance) {
				TurnAffordance ta = (TurnAffordance) a;
				boolean canForward = frontIR.getDistance() > FORWARD_THRS;
				if (ta.getAngle() > 0){
					a.setRealizable(leftIR.getDistance() > TURN_THRS | !canForward);
				} else
					a.setRealizable(rightIR.getDistance() > TURN_THRS | !canForward);
			}
		return possibleAffordances;
	}

	@Override
	public void executeAffordance(Affordance selectedAction, Subject sub) {
		if (selectedAction instanceof ForwardAffordance){
			forward(((ForwardAffordance)selectedAction).getDistance());
		} else if (selectedAction instanceof TurnAffordance){
			rotate(((TurnAffordance)selectedAction).getAngle());
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

	@Override
	public boolean hasFoundPlatform() {
		// TODO Auto-generated method stub
		return false;
	}

}
