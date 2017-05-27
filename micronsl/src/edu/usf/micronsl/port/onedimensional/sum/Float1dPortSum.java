package edu.usf.micronsl.port.onedimensional.sum;

import java.util.List;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

/**
 * A port that ouputs the sum of a set of source ports
 * 
 * @author Martin Llofriu
 *
 */
public class Float1dPortSum extends Float1dPort {

	/**
	 * The source ports to be added
	 */
	private List<Float1dPort> sources;

	/**
	 * Create the port
	 * 
	 * @param owner
	 *            The owner module
	 * @param sources
	 *            The set of source ports. This set should be non-empty and
	 *            contain ports with the same size
	 */
	public Float1dPortSum(Module owner, List<Float1dPort> sources) {
		super(owner);

		if (sources.isEmpty())
			throw new IllegalArgumentException("Cannot use an empty list of sources");

		boolean allSameSize = true;
		int sizeFirst = sources.get(0).getSize();
		for (Float1dPort source : sources)
			allSameSize = sizeFirst == source.getSize();
		if (!allSameSize)
			throw new IllegalArgumentException("All sources should be the same size");
		
		this.sources = sources;
	}

	@Override
	public int getSize() {
		return sources.get(0).getSize();
	}

	@Override
	public float get(int index) {
		float sum = 0;
		for (Float1dPort source : sources)
			sum += source.get(index);

		return sum;
	}

	@Override
	public void set(int i, float x) {
		throw new RuntimeException("Cannot set the value of a sum port");
	}

	@Override
	public void clear() {
		for (Float1dPort source : sources)
			source.clear();
	}

	@Override
	public float[] getData() {
		float[] data = new float[getSize()];
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
