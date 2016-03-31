package edu.usf.micronsl.port.onedimensional.sparse;

import java.util.HashMap;
import java.util.Map;

import edu.usf.micronsl.Module;

/**
 * A sparse 1d float port implemented by a Map from integer index to non-zero
 * values
 * 
 * @author Martin Llofriu
 *
 */
public class Float1dSparsePortMap extends Float1dSparsePort {

	/**
	 * The maximum number of elements
	 */
	private int maxSize;
	/**
	 * The map used to hold the data
	 */
	private Map<Integer, Float> nonZero;
	/**
	 * The expected sparseness of the data (proportion of non-zero elements)
	 */
	private float sparseness;

	/**
	 * Create the port
	 * 
	 * @param owner
	 *            The owner module
	 * @param maxSize
	 *            The maximum number of elements
	 * @param sparseness
	 *            The expected number of non-zero elements
	 */
	public Float1dSparsePortMap(Module owner, int maxSize, float sparseness) {
		super(owner);
		this.maxSize = maxSize;
		this.sparseness = sparseness;
		this.nonZero = new HashMap<Integer, Float>(Math.round(maxSize * sparseness));
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
	public void set(int i, float x) {
		if (x != 0)
			nonZero.put(i, x);
	}

	@Override
	public float[] getData() {
		float data[] = new float[getSize()];
		getData(data);
		return data;
	}

	@Override
	public void getData(float[] data) {
		for (int i = 0; i < getSize(); i++)
			data[i] = 0f;
		for (Integer i : nonZero.keySet())
			data[i] = nonZero.get(i);
	}

	@Override
	public Map<Integer, Float> getNonZero() {
		return nonZero;
	}

	@Override
	public void clear() {
		nonZero.clear();
	}

	@Override
	public float getSparseness() {
		return sparseness;
	}

}
