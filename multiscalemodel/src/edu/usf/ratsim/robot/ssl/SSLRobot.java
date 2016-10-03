package edu.usf.ratsim.robot.ssl;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.robot.ssl.sensefeeder.FeederSensor;

public class SSLRobot extends LocalizableRobot {

	IRReader irreader = null;
	SSLPilot pilot = null;
	FeederSensor fsensor = null;
	private Universe universe;
	private float closeThrs;

	public SSLRobot(ElementWrapper params, Universe u) {
		super(params);
		
		closeThrs = params.getChildFloat("closeThrs");
		
		this.universe = u;
	}

	@Override
	public void eat() {
	}

	@Override
	public boolean hasFoundFood() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startRobot() {
		if (irreader != null)
			irreader.terminate();

		irreader = new IRReader();
		irreader.start();

		if (pilot != null)
			pilot.close();

		pilot = new SSLPilot();

		if (fsensor != null)
			fsensor.close();
		fsensor = new FeederSensor();
	}

	@Override
	public void forward(float distance) {
		pilot.stepForward();
	}

	@Override
	public void rotate(float degrees) {
		if (degrees > 0)
			pilot.stepLeft();
		else
			pilot.stepRight();
	}

	@Override
	public Feeder getFlashingFeeder() {
		List<Feeder> visible = getVisibleFeeders(null);
		if (visible.isEmpty())
			return null;
		else if (universe.isFeederFlashing(visible.get(0).getId()))
			return visible.get(0);
		else 
			return null;
	}

	@Override
	public boolean seesFlashingFeeder() {
		return getFlashingFeeder() != null;
	}

	@Override
	public Feeder getClosestFeeder(int lastFeeder) {
		int[] except = {lastFeeder};
		List<Feeder> visible = getVisibleFeeders(except);
		if (visible.isEmpty())
			return null;
		else if (visible.get(0).getPosition().distance(new Point3f()) < closeThrs)
			return visible.get(0);
		else 
			return null;
	}

	@Override
	public boolean isFeederClose() {
		return getClosestFeeder(-1) != null;
	}

	@Override
	public List<Affordance> checkAffordances(List<Affordance> possibleAffordances) {
		for (Affordance af : possibleAffordances) {
			if (af instanceof TurnAffordance) {
				TurnAffordance tf = (TurnAffordance) af;
				if (((TurnAffordance) af).getAngle() > 0) // left
					tf.setRealizable(!irreader.somethingFront() && !irreader.somethingLeft());
				else
					tf.setRealizable(!irreader.somethingFront() && !irreader.somethingRight());
			} else if (af instanceof ForwardAffordance) {
				af.setRealizable(!irreader.somethingClose());
			} else if (af instanceof EatAffordance) {
				af.setRealizable(isFeederClose());
			}
		}

		return possibleAffordances;
	}

	@Override
	public void executeAffordance(Affordance af, Subject sub) {
		if (af instanceof TurnAffordance) {
			TurnAffordance tf = (TurnAffordance) af;
			if (((TurnAffordance) af).getAngle() > 0) // left
				rotate(30); // value is arbitrary - just positive for this robot
			else
				rotate(-30);
		} else if (af instanceof ForwardAffordance) {
			forward(30); // same, value is not necessary
		} else if (af instanceof EatAffordance) {
			sub.setTriedToEat();
			if (isFeederClose())
				sub.setHasEaten(true);
		}
	}

	@Override
	public List<Feeder> getVisibleFeeders(int[] is) {
		// There is only one feeeder.
		// Get location, if null, there is no feeder
		Point3f p = fsensor.getFeederLocation();
		List<Feeder> res = new LinkedList<Feeder>();

		if (p != null) {
			res.add(new Feeder(1, p));

			if (is != null)
				// Check that it is not excluded
				for (int i = 0; i < is.length; i++)
					if (is[i] == 1)
						res.clear();
		}
		
		return res;
	}

	@Override
	public List<Point3f> getVisibleWallEnds() {
		return new LinkedList<>();
	}

	@Override
	public int getLastAteFeeder() {
		return 1;
	}

	@Override
	public int getLastTriedToEatFeeder() {
		return 1;
	}

	@Override
	public Point3f getPosition() {
		return new Point3f();
	}

	@Override
	public float getOrientationAngle() {
		return 0;
	}

	@Override
	public Quat4f getOrientation() {
		return new Quat4f();
	}

	@Override
	public boolean seesFeeder() {
		return fsensor.getFeederLocation() != null;
	}

	@Override
	public List<Feeder> getAllFeeders() {
		return getVisibleFeeders(null);
	}

	@Override
	public float getDistanceToClosestWall() {
		return 30;
	}

	@Override
	public float getHalfFieldView() {
		return (float) (Math.PI/8);
	}

	@Override
	public int closeToNoseWalls(float distToConsider) {
		return 0;
	}

	@Override
	public Feeder getFeederInFront() {
		List<Feeder> feeders = getVisibleFeeders(null);
		if (feeders.isEmpty())
			return null;
		else
			return feeders.get(0);
	}

	public static void main(String[] args) throws InterruptedException{
		SSLRobot r = new SSLRobot(null, null);
		
		r.startRobot();
		
		r.forward(10);
		Thread.sleep(1000);
		
		r.rotate(10);
		Thread.sleep(1000);
		
		r.rotate(-10);
		Thread.sleep(1000);
		
	}
	
}


