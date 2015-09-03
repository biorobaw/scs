package edu.usf.ratsim.micronsl;

import java.util.HashMap;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Float1dPortSparseCopy extends Float1dSparsePort {

	private Float1dSparsePort toCopy;
	private HashMap<Integer, Float> nonZero;

	public Float1dPortSparseCopy(Module owner, Float1dSparsePort toCopy) {
		super(owner);

		this.toCopy = toCopy;
		this.nonZero = new HashMap<Integer, Float>();
	}

	@Override
	public int getSize() {
		return toCopy.getSize();
	}

	@Override
	public float get(int index) {
		return nonZero.get(index);
	}

	public void copy() {
		nonZero.clear();
		nonZero.putAll(toCopy.getNonZero());
	}

	@Override
	public float[] getData() {
		throw new NotImplementedException();
	}

	@Override
	public void getData(float[] buf) {
		throw new NotImplementedException();
	}

	@Override
	public Map<Integer, Float> getNonZero() {
		return nonZero;
	}

}
