package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.List;
import java.util.Random;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.nsl.modules.cell.ConjCell;
import edu.usf.ratsim.nsl.modules.celllayer.RndHDPCellLayer;

public class ValueHillClimber extends Module implements Voter {

	public float[] actionVote;
	private int numActions;
	private boolean[] connected;
	private float foodReward;
	private List<RndHDPCellLayer> conjCellLayers;
	private LocalizableRobot lrobot;

	public ValueHillClimber(String name, List<RndHDPCellLayer> conjCellLayers, List<Float> connProbs,
			List<Integer> statesPerLayer, int numActions, float foodReward, LocalizableRobot lrobot) {
		super(name);

		actionVote = new float[numActions];
		addOutPort("votes", new Float1dPortArray(this, actionVote));

		int numStates = 0;
		for (Integer stateLen : statesPerLayer)
			numStates += stateLen;
		connected = new boolean[numStates];
		Random r = RandomSingleton.getInstance();
		int layer = 0;
		int stateIndex = 0;
		for (Integer layerNumStates : statesPerLayer) {
			float prob = connProbs.get(layer);
			for (int i = 0; i < layerNumStates; i++) {
				connected[stateIndex] = r.nextFloat() < prob;
				stateIndex++;
			}
			layer++;
		}

		this.numActions = numActions;
		this.conjCellLayers = conjCellLayers;

		this.foodReward = foodReward;
		this.lrobot = lrobot;
	}

	public void run() {
		Float1dSparsePort states = (Float1dSparsePort) getInPort("states");
		FloatMatrixPort value = (FloatMatrixPort) getInPort("value");

		for (int action = 0; action < numActions; action++)
			actionVote[action] = 0f;

		float maxVal = Float.MIN_VALUE;
		int maxCellIndex = 0;
		for (Integer state : states.getNonZero().keySet()) {
			if (connected[state]) {
				float stateVal = states.get(state);
				if (stateVal != 0) {
					if (value.get(state, numActions) > maxVal) {
						maxCellIndex = state;
						maxVal = value.get(state, numActions);
					}
				}
			}
		}

		int i = 0;
		
		while (maxCellIndex >= conjCellLayers.get(i).getCells().size()) {
			maxCellIndex -= conjCellLayers.get(i).getCells().size();
			i++;
		}
		ConjCell maxCell = conjCellLayers.get(i).getCells().get(maxCellIndex);
		Point3f dest = maxCell.getPreferredLocation();
		
		float rotToPoint = GeomUtils.angleToPointWithOrientation(GeomUtils.angleToRot(lrobot.getOrientationAngle()), lrobot.getPosition(), dest);
		if (Math.abs(rotToPoint)>.195)
			if (Math.signum(rotToPoint)>0)
				actionVote[0] = foodReward;
			else
				actionVote[2] = foodReward;
		else
			actionVote[1] = foodReward;
		
		
		System.out.println("Best value cell centered at " + dest);

	}

	public float[] getVotes() {
		return actionVote;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
