package edu.usf.ratsim.experiment.subject.pathplanning.graphbased;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.subject.NotImplementedException;


public class BugAndGraphSubject extends Subject {

	private BugAndGraphModel model;

	public BugAndGraphSubject(String name, String group,
			ElementWrapper params, Robot robot) {
		super(name, group, params, robot);
		
		if (!(robot instanceof SonarRobot))
			throw new RuntimeException("SonarTest "
					+ "needs a Sonar Robot");

		model = new BugAndGraphModel(params, this, robot);
	}

	@Override
	public void stepCycle() {
		setHasEaten(false);
		clearTriedToEAt();
		
		model.simRun();
	}
	
	@Override
	public List<Affordance> getPossibleAffordances() {
		return new LinkedList<Affordance>();
	}

	@Override
	public float getMinAngle() {
		return 0;
	}

	@Override
	public void newEpisode() {
		model.newEpisode();
	}

	@Override
	public void newTrial() {
		model.newTrial();
	}

	@Override
	public Affordance getHypotheticAction(Point3f pos, float theta,
			int intention) {
		return null;
	}

	@Override
	public void deactivateHPCLayersRadial(LinkedList<Integer> indexList, float constant) {
		throw new NotImplementedException();
	}

	@Override
	public void setExplorationVal(float val) {
		throw new NotImplementedException();
	}

	@Override
	public float getStepLenght() {
		return 0;
	}

	@Override
	public Map<Float,Float> getValue(Point3f point, int intention, float angleInterval, float distToWall) {
		throw new NotImplementedException();
	}

	
	@Override
	public void deactivateHPCLayersProportion(LinkedList<Integer> indexList,
			float proportion) {
		throw new NotImplementedException();
	}

	@Override
	public void remapLayers(LinkedList<Integer> indexList) {
		throw new NotImplementedException();
	}

	

	@Override
	public float getValueEntropy() {
		throw new NotImplementedException();
	}

	@Override
	public void reactivateHPCLayers(LinkedList<Integer> indexList) {
		throw new NotImplementedException();
	}

	@Override
	public Affordance getForwardAffordance() {
		return new ForwardAffordance(0);
	}

	@Override
	public Affordance getLeftAffordance() {
		return new TurnAffordance(0, 0);
	}

	@Override
	public Affordance getRightAffordance() {
		return new TurnAffordance(0, 0);

	}
	

}
