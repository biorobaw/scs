package edu.usf.experiment.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.LocalActionAffordanceRobot;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.morse.IRSensorProxy;
import edu.usf.experiment.universe.morse.MorseUtils;
import edu.usf.experiment.universe.morse.PosSensorProxy;
import edu.usf.experiment.utils.ElementWrapper;

//TODO: Implement the functionality of all these interfaces
public class MorseRobot
		implements DifferentialRobot, LocalizableRobot, StepRobot, PlatformRobot, LocalActionAffordanceRobot, SonarRobot {

	private static final float FORWARD_THRS = .3f;
	private static final float TURN_THRS = .2f;
	private PosSensorProxy posSensor;
	private MorseControllerProxy robCtrl;
	private IRSensorProxy leftIR;
	private IRSensorProxy rightIR;
	private IRSensorProxy frontIR;
	private Affordance lastAction;

	public MorseRobot(ElementWrapper params, Universe univ) {
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

		lastAction = null;
	}

	@Override
	public Coordinate getPosition() {
		return posSensor.getPosition();
	}

	@Override
	public float getOrientationAngle() {
		return posSensor.getOrientation();
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
	public List<Affordance> checkAffordances(List<Affordance> possibleAffordances) {
		// TODO Auto-generated method stub
		float front = frontIR.getDistance();
		float left = leftIR.getDistance();
		float right = rightIR.getDistance();
		// System.out.println(front + " " + left + " " + right);
		boolean canForward = front > FORWARD_THRS;
		for (Affordance a : possibleAffordances)
			if (a instanceof ForwardAffordance) {

				a.setRealizable(canForward);
			} else if (a instanceof TurnAffordance) {
				TurnAffordance ta = (TurnAffordance) a;
				// if (lastAction != null && lastAction instanceof
				// TurnAffordance
				// && ((TurnAffordance) lastAction).getAngle() != ta.getAngle())
				// a.setRealizable(false);
				// else
				if (ta.getAngle() > 0) {
					a.setRealizable(left > TURN_THRS || (!canForward && right <= TURN_THRS));
				} else
					a.setRealizable(right > TURN_THRS || (!canForward && left <= TURN_THRS));
			}
		return possibleAffordances;
	}

	@Override
	public void executeAffordance(Affordance selectedAction) {
		if (selectedAction instanceof ForwardAffordance) {
			forward(((ForwardAffordance) selectedAction).getDistance());
		} else if (selectedAction instanceof TurnAffordance) {
			rotate(((TurnAffordance) selectedAction).getAngle());
			// if (frontIR.getDistance() > FORWARD_THRS)
			// forward(10);
		}

		lastAction = selectedAction;
	}

	@Override
	public boolean hasFoundPlatform() {
		// TODO: make it generic
		return posSensor.getPosition().distance(new Coordinate(.3f, .3f, 0)) < .15f;
	}

	@Override
	public void moveContinous(float lVel, float angVel) {

	}

	@Override
	public float checkAffordance(Affordance af) {
		List<Affordance> l = new LinkedList<Affordance>();
		l.add(af);
		checkAffordances(l);
		return l.get(0).isRealizable() ? 1 : 0;
	}

	@Override
	public void setLinearVel(float linearVel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAngularVel(float angularVel) {
		// TODO Auto-generated method stub

	}

	@Override
	public float[] getSonarReadings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getSonarAngles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getSonarAperture() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Affordance> getPossibleAffordances() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getMinAngle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getStepLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Affordance getForwardAffordance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Affordance getLeftAffordance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Affordance getRightAffordance() {
		// TODO Auto-generated method stub
		return null;
	}

}
