package edu.usf.ratsim.experiment.subject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.nsl.modules.cell.ConjCell;

public class MultiScaleArtificialPCSubject extends Subject {

	private float step;
	private float leftAngle;
	private float rightAngle;
	
	private MultiScaleArtificialPCModel model;

	public MultiScaleArtificialPCSubject(String name, String group,
			ElementWrapper params, Robot robot) {
		super(name, group, params, robot);
		
		step = params.getChildFloat("step");
		leftAngle = params.getChildFloat("leftAngle");
		rightAngle = params.getChildFloat("rightAngle");
		
		if (!(robot instanceof LocalizableRobot))
			throw new RuntimeException("MultiScaleArtificialPCSubject "
					+ "needs a Localizable Robot");
		LocalizableRobot lRobot = (LocalizableRobot) robot;

		model = new MultiScaleArtificialPCModel(params, this, lRobot);
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
		
		res.add(new TurnAffordance(leftAngle, step));
		res.add(new ForwardAffordance(step));
		res.add(new TurnAffordance(rightAngle, step));
		res.add(new EatAffordance());
		
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
	

}
