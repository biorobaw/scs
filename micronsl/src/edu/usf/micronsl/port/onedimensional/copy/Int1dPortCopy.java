package edu.usf.micronsl.port.onedimensional.copy;

import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.PortCopier;
import edu.usf.micronsl.port.onedimensional.Int1dPort;

/**
 * This port stores a copy of the source port. It is useful when the values at a
 * given point should be remembered after the port is updated.
 * 
 * @author Martin Llofriu
 *
 */
public class Int1dPortCopy extends Int1dPort implements PortCopier {

	/**
	 * The copy of the data
	 */
	private int[] data = null;
	/**
	 * The source port to be copied
	 */
	private Int1dPort toCopy;

	/**
	 * A constructor specifying the source port to be copied
	 * 
	 * @param owner
	 *            The owner module
	 * @param toCopy
	 *            The source port to be copied
	 */
	public Int1dPortCopy(Module owner, Int1dPort toCopy) {
		super(owner);

		data = new int[toCopy.getSize()];

		this.toCopy = toCopy;
	}

	@Override
	public int getSize() {
		return data.length;
	}

	@Override
	public int get(int index) {
		return data[index];
	}

	@Override
	public void set(int i, int x) {
		data[i] = x;
	}

	@Override
	public void clear() {
		for (int i = 0; i < data.length; i++)
			data[i] = 0;
	}

	@Override
	public void copy() {
		toCopy.getData(data);
	}

	@Override
	public int[] getData() {
		return data;
	}

	@Override
	public void getData(int[] buf) {
		System.arraycopy(data, 0, buf, 0, data.length);
	}

}
