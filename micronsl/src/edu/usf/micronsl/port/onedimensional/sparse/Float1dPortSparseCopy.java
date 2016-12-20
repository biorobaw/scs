package edu.usf.micronsl.port.onedimensional.sparse;

import java.util.HashMap;
import java.util.Map;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.PortCopier;

/**
 * A port to copy the non-zero values of a sparse port
 * 
 * @author Martin Llofriu
 *
 */
public class Float1dPortSparseCopy extends Float1dSparsePort implements PortCopier {

	/**
	 * The sparse port to be copied
	 */
	private Float1dSparsePort toCopy;
	/**
	 * The map of non-zero values
	 */
	private HashMap<Integer, Float> nonZero;

	/**
	 * Creates a sparse copy port
	 * 
	 * @param owner
	 *            The owner module
	 * @param toCopy
	 *            The sparse port to be copied
	 */
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
		Float ans = nonZero.get(index);
		if(ans == null)
			return 0;
		else return ans;
		
	}

	@Override
	public void set(int i, float x) {
		if (x != 0)
			nonZero.put(i, x);
		else nonZero.remove(i);
	}

	@Override
	public void clear() {
		nonZero.clear();
	}

	@Override
	public void copy() {
		nonZero.clear();
		nonZero.putAll(toCopy.getNonZero());
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
	public float getSparseness() {
		return toCopy.getSparseness();
	}

}
