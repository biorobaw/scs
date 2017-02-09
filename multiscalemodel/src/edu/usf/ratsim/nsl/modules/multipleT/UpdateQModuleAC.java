package edu.usf.ratsim.nsl.modules.multipleT;

import java.util.HashMap;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be
 * performed It assumes actions are allocentric, distributed uniformly in the
 * range [0,2pi]
 * 
 * @author biorob
 * 
 */
public class UpdateQModuleAC extends Module {

	private float nu; // learning rate
	private int numActions;

	float[][] dotProducts;
	private HashMap<Integer, Float> oldPCs;

	public UpdateQModuleAC(String name, int numActions, float nu) {
		super(name);

		this.numActions = numActions;
		this.nu = nu;
		dotProducts = new float[numActions][numActions];

		
		// precalculate table:  if action i was taken,
		// all actions are rewarded according to their "distance" to action i.
		// right now r = 1 if d(ai,aj)==0,  0 otherwise
		double deltaAngle = 2 * Math.PI / numActions;
		float anglei = 0;
		for (int i = 0; i < numActions; i++) {
			float x = (float) Math.cos(anglei);
			float y = (float) Math.sin(anglei);

			float anglej = 0;
			for (int j = 0; j < numActions; j++) {
				dotProducts[i][j] = Math.max(x * (float) Math.cos(anglej) + y * (float) Math.sin(anglej), 0);
				// dotProducts[i][j] *=(dotProducts[i][j]*dotProducts[i][j]);
				dotProducts[i][j] = i == j ? 1 : 0;
				// System.out.print("\t"+dotProducts[i][j]);
				anglej += deltaAngle;
			}
			// System.out.println("");
			anglei += deltaAngle;
		}
	}

	public void run() {
		float nuDelta = nu * ((Float0dPort) getInPort("delta")).get();
		int action = ((Int0dPort) getInPort("action")).get();
		Float2dPort Q = (Float2dPort) getInPort("Q");

		if (nuDelta == 0)
			return;

		if (oldPCs != null) {
			for (int i : oldPCs.keySet()) {
				for (int j = 0; j < numActions; j++) {
					Q.set(i, j, Q.get(i, j) + nuDelta * oldPCs.get(i) * dotProducts[j][action]);
				}
				// Update value
				Q.set(i, numActions, Q.get(i, numActions) + nuDelta * oldPCs.get(i));
			}
		}

		Float1dSparsePort PCs = (Float1dSparsePort) getInPort("placeCells");
		oldPCs = new HashMap<Integer, Float>(PCs.getNonZero());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
	
	public void newEpisode() {
		oldPCs = null;
	}
}
