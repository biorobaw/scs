 package edu.usf.experiment.robot;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.ElementWrapper;

public class DummyLocalizableRobot extends LocalizableRobot {

	public DummyLocalizableRobot(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Point3f getPosition() {
		return new Point3f();
	}

	@Override
	public void eat() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasFoundFood() {
		return false;
	}

	@Override
	public void startRobot() {
	}

	@Override
	public void rotate(float degrees) {
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
	public boolean isFeederClose() {
		return false;
	}

	@Override
	public float getOrientationAngle() {
		return 0;
	}

	@Override
	public Quat4f getOrientation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forward(float distance) {
		// TODO Auto-generated method stub

	}

	@Override
	public Feeder getClosestFeeder(int lastFeeder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean seesFeeder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Affordance> checkAffordances(
			List<Affordance> possibleAffordances) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeAffordance(Affordance selectedAction, Subject sub) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Feeder> getVisibleFeeders(int[] i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feeder> getAllFeeders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getDistanceToClosestWall() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Point3f> getVisibleWallEnds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getHalfFieldView() {
		// TODO Auto-generated method stub
		return 0;
	}

}
