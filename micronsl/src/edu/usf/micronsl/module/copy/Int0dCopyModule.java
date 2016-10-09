package edu.usf.micronsl.module.copy;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.copy.Float1dPortCopy;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPortCopy;

/**
 * This module copies the input port upon call to the run method and includes an
 * output port with the copied data.
 * 
 * @author Martin Llofriu
 * 
 */
public class Int0dCopyModule extends Module {

	/**
	 * The copy output port
	 */
	private Int0dPortCopy copyPort;

	/**
	 * Creates the module
	 * 
	 * @param name
	 *            The module's name. Should be unique systemwide.
	 */
	public Int0dCopyModule(String name) {
		super(name);
	}

	@Override
	/**
	 * Invoques the copy port's copy operation.
	 */
	public void run() {
		copyPort.copy();
	}

	@Override
	public void addInPort(String name, Port port) {
		super.addInPort(name, port);
		copyPort = new Int0dPortCopy(this, (Int0dPort) port);
		addOutPort("copy", copyPort);
	}

	@Override
	public void addInPort(String name, Port port, boolean reverseDependency) {
		super.addInPort(name, port, reverseDependency);
		copyPort = new Int0dPortCopy(this, (Int0dPort) port);
		addOutPort("copy", copyPort);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
