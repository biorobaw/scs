package edu.usf.micronsl;

import java.util.HashMap;
import java.util.Map;

public class Float1dSparsePortMap extends Float1dSparsePort {

	private int maxSize;
	private Map<Integer, Float> nonZero;
	private int sparseness;

	public Float1dSparsePortMap(Module owner, int maxSize, int sparseness) {
		super(owner);
		this.maxSize = maxSize;
		this.sparseness = sparseness;
		this.nonZero = new HashMap<Integer, Float>(maxSize / sparseness);
	}

	@Override
	public int getSize() {
		return maxSize;
	}

	@Override
	public float get(int index) {
		if (nonZero.containsKey(index))
			return nonZero.get(index);
		else
			throw new IllegalArgumentException();
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
		return nonZero;
	}

	public void clear() {
		nonZero.clear();
	}

	public int getSparseness() {
		return sparseness;
	}
}
