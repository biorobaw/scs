package edu.usf.micronsl.port.onedimensional.copy;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.PortCopier;
import edu.usf.micronsl.port.onedimensional.Bool1dPort;

/**
 * This port stores a copy of the source port. It is useful when the values at a
 * given point should be remembered after the port is updated.
 * 
 * @author Martin Llofriu
 *
 */
public class Bool1dPortCopy extends Bool1dPort implements PortCopier {

	/**
	 * The copy of the data
	 */
	private boolean[] data = null;
	/**
	 * The source port to be copied
	 */
	private Bool1dPort toCopy;

	/**
	 * A constructor specifying the source port to be copied
	 * 
	 * @param owner
	 *            The owner module
	 * @param toCopy
	 *            The source port to be copied
	 */
	public Bool1dPortCopy(Module owner, Bool1dPort toCopy) {
		super(owner);

		data = new boolean[toCopy.getSize()];

		this.toCopy = toCopy;
	}

	@Override
	public int getSize() {
		return data.length;
	}

	@Override
	public boolean get(int index) {
		return data[index];
	}

	@Override
	public void set(int i, boolean x) {
		data[i] = x;
	}

	@Override
	public void clear() {
		for (int i = 0; i < data.length; i++)
			data[i] = false;
	}

	@Override
	public void copy() {
		toCopy.getData(data);
	}

	@Override
	public boolean[] getData() {
		return data;
	}

	@Override
	public void getData(boolean[] buf) {
		System.arraycopy(data, 0, buf, 0, data.length);
	}

}
