package edu.usf.ratsim.micronsl;

import java.util.List;

public class Float1dPortSum extends Float1dPort {

	private List<Float1dPort> states;

	public Float1dPortSum(Module owner, List<Float1dPort> states) {
		super(owner);

		if (states.isEmpty())
			throw new IllegalArgumentException(
					"Cannot use an empty list of states");

		boolean allSameSize = true;
		int sizeFirst = states.get(0).getSize();
		for (Float1dPort state : states)
			allSameSize = sizeFirst == state.getSize();
		if (!allSameSize)
			throw new IllegalArgumentException(
					"All states should be the same size");

		this.states = states;
	}

	@Override
	public int getSize() {
		return states.get(0).getSize();
	}

	@Override
	public float get(int index) {
		float sum = 0;
		for (Float1dPort state : states)
			sum += state.get(index);
//		if (Math.abs(sum) > 1)
//			sum = Math.signum(sum);
		
		return sum;
	}

	@Override
	public float[] getData() {
		float [] data = new float[getSize()];
		for (int i = 0; i < getSize(); i++)
			data[i] = get(i);
		return data;
	}

	@Override
	public void getData(float[] data) {
		for (int i = 0; i < getSize(); i++)
			data[i] = get(i);
	}

}
