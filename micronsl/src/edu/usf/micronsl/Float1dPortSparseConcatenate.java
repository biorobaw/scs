package edu.usf.micronsl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Float1dPortSparseConcatenate extends Float1dSparsePort {

	private List<Float1dSparsePortMap> states;
	private int size;
	private HashMap<Integer, Float> nonZero;

	public Float1dPortSparseConcatenate(Module owner, List<Float1dSparsePortMap> states) {
		super(owner);

		this.states = states;

		size = 0;
		float sparseness = 0;
		for (Float1dSparsePortMap state : states) {
			size += state.getSize();
			sparseness += state.getSparseness();
		}
		sparseness = (sparseness / states.size());

		nonZero = new HashMap<Integer, Float>(Math.round(size / sparseness));
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public float get(int index) {
		for (Float1dPort state : states) {
			if (index - state.getSize() < 0)
				return state.get(index);
			else
				index -= state.getSize();
		}

		throw new RuntimeException("Index out of bounds");
	}

	@Override
	public float[] getData() {
		throw new NotImplementedException();
	}

	@Override
	public void getData(float[] data) {
		throw new NotImplementedException();
	}

	public Map<Integer, Float> getNonZero() {
		int previousSizes = 0;
		nonZero.clear();
		for (Float1dSparsePortMap state : states) {
			for (Entry<Integer, Float> entry : state.getNonZero().entrySet()) {
				nonZero.put(entry.getKey() + previousSizes, entry.getValue());
			}
			previousSizes += state.getSize();
		}
		return nonZero;
	}

}
