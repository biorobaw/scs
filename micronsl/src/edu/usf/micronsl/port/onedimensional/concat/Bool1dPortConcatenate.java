package edu.usf.micronsl.port.onedimensional.concat;

import java.util.List;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Bool1dPort;

/**
 * This port concatenates the data from a set of other 1d ports. This helps
 * prevent copying potentially unused data from the source ports into a
 * temporary array.
 * 
 * @author Martin Llofriu
 *
 */
public class Bool1dPortConcatenate extends Bool1dPort {
	/**
	 * The source ports
	 */
	private List<Bool1dPort> sources;
	/**
	 * The total size of this port
	 */
	private int size;
	/**
	 * A temporary buffer
	 */
	private boolean[] tmpData;

	/**
	 * A constructor specifying the set of source ports
	 * 
	 * @param owner
	 *            The owner module
	 * @param states
	 *            The set of source ports
	 */
	public Bool1dPortConcatenate(Module owner, List<Bool1dPort> sources) {
		super(owner);

		this.sources = sources;

		// The total size of the port is the sum of the source ports
		size = 0;
		int maxSize = 0;
		for (Bool1dPort source : sources) {
			size += source.getSize();
			if (source.getSize() > maxSize)
				maxSize = source.getSize();
		}
		tmpData = new boolean[maxSize];

	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public boolean get(int index) {
		// Find the corresponding port in the index and fetch the value
		for (Bool1dPort state : sources) {
			if (index - state.getSize() < 0)
				return state.get(index);
			else
				index -= state.getSize();
		}

		throw new RuntimeException("Index out of bounds");
	}

	@Override
	public void set(int index, boolean x) {
		// Find the corresponding port in the index and fetch the value
		for (Bool1dPort state : sources) {
			if (index - state.getSize() < 0)
				state.set(index, x);
			else
				index -= state.getSize();
		}

		throw new RuntimeException("Index out of bounds");
	}

	@Override
	public void clear() {
		for (Bool1dPort state : sources) {
			state.clear();
		}
	}

	@Override
	public synchronized boolean[] getData() {
		boolean[] res = new boolean[size];
		int i = 0;
		for (Bool1dPort state : sources) {
			int stateSize = state.getSize();
			for (int j = 0; j < stateSize; j++)
				tmpData[j] = false;
			// TODO: maybe just use state.getData() ??
			state.getData(tmpData);
			System.arraycopy(tmpData, 0, res, i, stateSize);
			i += stateSize;
		}

		return res;
	}

	@Override
	public synchronized void getData(boolean[] res) {
		int i = 0;
		for (Bool1dPort state : sources) {
			int stateSize = state.getSize();
			for (int j = 0; j < stateSize; j++)
				tmpData[j] = false;
			state.getData(tmpData);
			System.arraycopy(tmpData, 0, res, i, stateSize);
			i += stateSize;
		}
	}

}
