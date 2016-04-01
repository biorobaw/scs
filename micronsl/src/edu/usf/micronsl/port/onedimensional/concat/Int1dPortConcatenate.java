package edu.usf.micronsl.port.onedimensional.concat;

import java.util.List;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Int1dPort;

/**
 * This port concatenates the data from a set of other 1d ports. This helps
 * prevent copying potentially unused data from the source ports into a
 * temporary array.
 * 
 * @author Martin Llofriu
 *
 */
public class Int1dPortConcatenate extends Int1dPort {
	/**
	 * The source ports
	 */
	private List<Int1dPort> sources;
	/**
	 * The total size of this port
	 */
	private int size;
	/**
	 * A temporary buffer
	 */
	private int[] tmpData;

	/**
	 * A constructor specifying the set of source ports
	 * 
	 * @param owner
	 *            The owner module
	 * @param states
	 *            The set of source ports
	 */
	public Int1dPortConcatenate(Module owner, List<Int1dPort> sources) {
		super(owner);

		this.sources = sources;

		// The total size of the port is the sum of the source ports
		size = 0;
		int maxSize = 0;
		for (Int1dPort source : sources) {
			size += source.getSize();
			if (source.getSize() > maxSize)
				maxSize = source.getSize();
		}
		tmpData = new int[maxSize];

	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int get(int index) {
		// Find the corresponding port in the index and fetch the value
		for (Int1dPort state : sources) {
			if (index - state.getSize() < 0)
				return state.get(index);
			else
				index -= state.getSize();
		}

		throw new RuntimeException("Index out of bounds");
	}

	@Override
	public void set(int index, int x) {
		// Find the corresponding port in the index and fetch the value
		for (Int1dPort state : sources) {
			if (index - state.getSize() < 0)
				state.set(index, x);
			else
				index -= state.getSize();
		}

		throw new RuntimeException("Index out of bounds");
	}

	@Override
	public void clear() {
		for (Int1dPort state : sources) {
			state.clear();
		}
	}

	@Override
	public synchronized int[] getData() {
		int[] res = new int[size];
		int i = 0;
		for (Int1dPort state : sources) {
			int stateSize = state.getSize();
			for (int j = 0; j < stateSize; j++)
				tmpData[j] = 0;
			// TODO: maybe just use state.getData() ??
			state.getData(tmpData);
			System.arraycopy(tmpData, 0, res, i, stateSize);
			i += stateSize;
		}

		return res;
	}

	@Override
	public synchronized void getData(int[] res) {
		int i = 0;
		for (Int1dPort state : sources) {
			int stateSize = state.getSize();
			for (int j = 0; j < stateSize; j++)
				tmpData[j] = 0;
			state.getData(tmpData);
			System.arraycopy(tmpData, 0, res, i, stateSize);
			i += stateSize;
		}
	}

}
