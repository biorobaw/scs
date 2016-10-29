package edu.usf.ratsim.nsl.modules.actionselection;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

/**
 * Class to set the value for a single state AC.
 * 
 * @author ludo
 *
 */
public class SingleStateValue extends Module  {

	public float[] valueEst;
	private int numActions;

	public SingleStateValue(String name, int numActions) {
		super(name);
		valueEst = new float[1];
		this.numActions = numActions;
		addOutPort("valueEst", new Float1dPortArray(this, valueEst));
	}

	public void run() {
		Int0dPort state = (Int0dPort) getInPort("state");
		FloatMatrixPort value = (FloatMatrixPort) getInPort("value");
		
		valueEst[0] =  value.get(state.get(),numActions);
		
		if (Debug.printValues) {
			System.out.println("RL value");
			System.out.print(valueEst[0] + " ");
			System.out.println();
		}

	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
