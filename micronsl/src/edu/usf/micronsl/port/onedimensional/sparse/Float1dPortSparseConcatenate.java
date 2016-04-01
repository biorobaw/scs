package edu.usf.micronsl.port.onedimensional.sparse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

/**
 * Concatenates a set of sparse ports.
 * 
 * @author Martin Llofriu
 *
 */
public class Float1dPortSparseConcatenate extends Float1dSparsePort {

	/**
	 * The set of sparse source states
	 */
	private List<Float1dSparsePortMap> sources;
	/**
	 * The total size of the port
	 */
	private int size;
	/**
	 * The map of non-zero values
	 */
	private HashMap<Integer, Float> nonZero;
	/**
	 * The average sparseness of the source ports
	 */
	private float sparseness;

	/**
	 * Constructs a sparse concatenate port
	 * 
	 * @param owner
	 *            The owner module
	 * @param sources
	 *            The source ports
	 */
	public Float1dPortSparseConcatenate(Module owner, List<Float1dSparsePortMap> sources) {
		super(owner);

		this.sources = sources;

		size = 0;
		sparseness = 0;
		for (Float1dSparsePortMap state : sources) {
			size += state.getSize();
			sparseness += state.getSparseness();
		}
		sparseness = (sparseness / sources.size());

		nonZero = new HashMap<Integer, Float>(Math.round(size * sparseness));
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public float get(int index) {
		for (Float1dPort source : sources) {
			if (index - source.getSize() < 0)
				return source.get(index);
			else
				index -= source.getSize();
		}

		throw new RuntimeException("Index out of bounds");
	}

	@Override
	public void set(int index, float x) {
		for (Float1dPort state : sources) {
			if (index - state.getSize() < 0)
				state.set(index, x);
			else
				index -= state.getSize();
		}

		throw new RuntimeException("Index out of bounds");
	}

	@Override
	public void clear() {
		for (Float1dPort state : sources)
			state.clear();
	}

	@Override
	public float[] getData() {
		float data[] = new float[size];
		getData(data);
		return data;
	}

	@Override
	public void getData(float[] data) {
		for (int i = 0; i < size; i++)
			data[i] = get(i);
	}

	@Override
	public Map<Integer, Float> getNonZero() {
		int previousSizes = 0;
		nonZero.clear();
		for (Float1dSparsePortMap source : sources) {
			for (Entry<Integer, Float> entry : source.getNonZero().entrySet()) {
				nonZero.put(entry.getKey() + previousSizes, entry.getValue());
			}
			previousSizes += source.getSize();
		}
		return nonZero;
	}

	@Override
	public float getSparseness() {
		return sparseness;
	}

}
