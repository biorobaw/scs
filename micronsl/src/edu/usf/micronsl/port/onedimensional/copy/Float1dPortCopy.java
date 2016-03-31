package edu.usf.micronsl.port.onedimensional.copy;

import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.PortCopier;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

/**
 * This port stores a copy of the source port. It is useful when the values at a
 * given point should be remembered after the port is updated.
 * 
 * @author Martin Llofriu
 *
 */
public class Float1dPortCopy extends Float1dPort implements PortCopier {

	/**
	 * The copy of the data
	 */
	private float[] data = null;
	/**
	 * The source port to be copied
	 */
	private Float1dPort toCopy;

	/**
	 * A constructor specifying the source port to be copied
	 * 
	 * @param owner
	 *            The owner module
	 * @param toCopy
	 *            The source port to be copied
	 */
	public Float1dPortCopy(Module owner, Float1dPort toCopy) {
		super(owner);

		data = new float[toCopy.getSize()];

		this.toCopy = toCopy;
	}

	@Override
	public int getSize() {
		return data.length;
	}

	@Override
	public float get(int index) {
		return data[index];
	}

	@Override
	public void set(int i, float x) {
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
	public float[] getData() {
		return data;
	}

	@Override
	public void getData(float[] buf) {
		System.arraycopy(data, 0, buf, 0, data.length);
	}

}
