package edu.usf.ratsim.experiment.subject.replay.highlevel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.nsl.modules.cell.ConjCell;

public class MorrisHLReplaySubject extends SubjectOld {

	private float step;
	private float leftAngle;
	private float rightAngle;
	
	private MorrisHLReplayModel model;

	public MorrisHLReplaySubject(String name, String group,
			ElementWrapper params, RobotOld robot) {
		super(name, group, params, robot);
		
		step = params.getChildFloat("step");
		leftAngle = params.getChildFloat("leftAngle");
		rightAngle = params.getChildFloat("rightAngle");
		
		if (!(robot instanceof LocalizableRobot))
			throw new RuntimeException("MultiScaleArtificialPCSubject "
					+ "needs a Localizable Robot");
		LocalizableRobot lRobot = (LocalizableRobot) robot;

		model = new MorrisHLReplayModel(params, this, lRobot);
	}

	@Override
	public void stepCycle() {
		setHasEaten(false);
		clearTriedToEAt();
		
		model.simRun();
	}
	
	@Override
	public List<Affordance> getPossibleAffordances() {
		List<Affordance> res = new LinkedList<Affordance>();
		
		res.add(getLeftAffordance());
		res.add(getForwardAffordance());
		res.add(getRightAffordance());
		
		return res;
	}

	@Override
	public float getMinAngle() {
		return leftAngle;
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
//		return model.getHypotheticAction(pos, theta, getPossibleAffordances(), intention);
		return null;
	}

	@Override
	public void deactivateHPCLayersRadial(LinkedList<Integer> indexList, float constant) {
		model.deactivatePCLRadial(indexList, constant);
	}

	@Override
	public void setExplorationVal(float val) {
		model.setExplorationVal(val);
	}

	@Override
	public float getStepLenght() {
		return step;
	}

	@Override
	public Map<Float,Float> getValue(Point3f point, int intention, float angleInterval, float distToWall) {
		return model.getValue(point, intention, angleInterval, distToWall);
	}

	public List<ConjCell> getPlaceCells() {
		return model.getPlaceCells();
	}

	@Override
	public void deactivateHPCLayersProportion(LinkedList<Integer> indexList,
			float proportion) {
		model.deactivatePCLProportion(indexList, proportion);
	}

	@Override
	public void remapLayers(LinkedList<Integer> indexList) {
		model.remapLayers(indexList);
	}

	public Map<Integer, Float> getCellActivity() {
		return model.getCellActivation();
	}

	@Override
	public float getValueEntropy() {
		return model.getValueEntropy();
	}

	@Override
	public void reactivateHPCLayers(LinkedList<Integer> indexList) {
		model.reactivatePCL(indexList);
	}

	@Override
	public void endEpisode() {
//		for (int i = 0; i < 10; i++)
//			model.replay();
		model.clearPath();
	}

	@Override
	public Map<Point3f, Float> getValuePoints() {
		return model.getValuePoints();
	}

	@Override
	public Affordance getForwardAffordance() {
		return new ForwardAffordance(step);
	}

	@Override
	public Affordance getLeftAffordance() {
		return new TurnAffordance(leftAngle, step);
	}

	@Override
	public Affordance getRightAffordance() {
		return new TurnAffordance(rightAngle, step);
	}

}
