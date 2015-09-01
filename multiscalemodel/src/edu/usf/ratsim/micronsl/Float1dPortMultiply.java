package edu.usf.ratsim.micronsl;

import java.util.List;
import java.util.Stack;

import edu.usf.experiment.utils.Debug;

public class Float1dPortMultiply extends Float1dPort {

	private int size;
	private float eps;
	private List<Float1dPort> states;
	private int minZeroVal;
	private int maxZeroVal;
	private boolean validOptimization;

	public Float1dPortMultiply(Module owner, List<Float1dPort> states, float eps) {
		super(owner);

		this.eps = eps;

		this.states = states;

		if (states.isEmpty()) {
			size = 0;
		} else {
			size = 1;
			for (Float1dPort state : states) {
				size *= state.getSize();
			}
		}

		if (size == 0)
			throw new RuntimeException("Empty input to Multiply port");

		validOptimization = false;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public float get(int index) {
		if (validOptimization && index >= minZeroVal && index <= maxZeroVal)
			return 0;

		int remainingSize = size;
		float jointActivation = 1;
		// Track the current scope of the state to optimize
		int currentState = 0;

		for (Float1dPort state : states) {
			remainingSize /= state.getSize();
			int stateIndex = (index / remainingSize) % state.getSize();
			currentState += remainingSize * stateIndex;
			jointActivation *= state.get(stateIndex);
			if (jointActivation < eps) {
				jointActivation = 0;
				// TODO: check optimize to set zero from then on
				minZeroVal = currentState;
				maxZeroVal = currentState + remainingSize - 1;
				validOptimization = true;
				break;
			}
		}

		// try {
		// Thread.sleep(1);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		if (Float.isNaN(jointActivation))
			System.out.println("Numeric Error");
		
		return jointActivation;
	}

	public void clearOptimizationCache() {
		validOptimization = false;
	}

	@Override
	public float[] getData() {
		float[] data = new float[size];
		getData(data);
		return data;
	}

	@Override
	public void getData(float[] data) {
		Stack<Integer> listPointer = new Stack<Integer>();
		Stack<Integer> indexStack = new Stack<Integer>();
		Stack<Float> currValStack = new Stack<Float>();

		for (int i = states.get(0).getSize() - 1; i >= 0; i--) {
			listPointer.push(0);
			indexStack.push(i);
			currValStack.push(1f);
		}

		int element = 0;
		do {
			int listNum = listPointer.pop();
			int index = indexStack.pop();
			float currVal = currValStack.pop();

			// System.out.println(listNum + " " + index + " " + currVal);
			// If not the last list
//			System.out.println(currVal);
//			System.out.println(states.get(listNum).get(index));
			currVal *= states.get(listNum).get(index);
			if (Float.isNaN(currVal)){
				System.out.println("Numeric Error");
				System.exit(1);
			}
			if (currVal < eps) {
				int elemsToSkip = 1;
				for (int s = listNum + 1; s < states.size(); s++)
					elemsToSkip *= states.get(s).getSize();
				element += elemsToSkip;
				// System.out.println("Skipping elements");
			} else {
				if (listNum < states.size() - 1) {
					for (int i = states.get(listNum + 1).getSize() - 1; i >= 0; i--) {
						listPointer.push(listNum + 1);
						indexStack.push(i);
						currValStack.push(currVal);
					}
				} else {
					data[element] = currVal;
					
					element++;
				}
			}
		} while (!listPointer.isEmpty());

		// float[] data2 = new float[size];
		// for (int i = 0; i < size; i++)
		// data2[i] = get(i);
		//
		// for (int i = 0; i < size; i++)
		// if (data2[i] != data[i])
		// System.err.println("Incorrect optimization: " + data2[i] + " "
		// + data[i]);

		if (Debug.printConjCells)
			if (states.get(0).get(0) == 1) {
				System.out.println("Conjuntive cells output " + data.length);
				for (int i = 0; i < data.length; i++)
					if (data[i] != 0)
						System.out.print(data[i] + " ");
				System.out.println();
			}

	}

}
