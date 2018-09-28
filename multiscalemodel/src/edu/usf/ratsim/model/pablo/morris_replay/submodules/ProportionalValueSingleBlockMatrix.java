package edu.usf.ratsim.model.pablo.morris_replay.submodules;

import java.util.Map;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;
import edu.usf.micronsl.port.twodimensional.Float2dSingleBlockMatrixPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

/**
 * Class to set the votes for actions depending both in the state activation and
 * a value function.
 * 
 *
 */
public class ProportionalValueSingleBlockMatrix extends Module {

	public Float0dPort valuePort;

	public ProportionalValueSingleBlockMatrix(String name) {
		super(name);
		valuePort = new Float0dPort(this);
		addOutPort("value",valuePort);
		
	}

	public void run() {
		Float2dSingleBlockMatrixPort states = (Float2dSingleBlockMatrixPort) getInPort("states");
		Float2dPort value = (Float2dPort) getInPort("value");
		
		run(states, value);
	}
	

	public void run(Float2dSingleBlockMatrixPort states, Float2dPort value) {
		float valueEst = 0f;

		float sum = 0;
		
		for(int i=0; i<states.getBlockRows();i++)
			for(int j=0;j<states.getBlockCols();j++){
				sum+=states.getBlock(i,j);
			}
		
		for(int i=0; i<states.getBlockRows();i++)
			for(int j=0;j<states.getBlockCols();j++){
				float stateVal = states.getBlock(i,j)/sum;
				if (stateVal != 0) {
					float val = value.get(states.getBlockIndex(i, j), 0);
					if (val != 0)
						valueEst += stateVal * val;
				}
			}
		

		// Max value is food reward
		
		valuePort.set(valueEst);
		
		if (Debug.printValues) {
			System.out.println("RL value");
			System.out.print(valueEst + " ");
			System.out.println();
		}

	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
