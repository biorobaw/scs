package edu.usf.micronsl.module.copy;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dPortSparseCopy;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;

/**
 * This module copies the sparse input port upon call to the run method and includes an
 * output port with the copied data.
 * 
 * @author Martin Llofriu
 * 
 */
public class Float1dSparseCopyModule extends Module {

	/**
	 * The sparse copy port
	 */
	private Float1dPortSparseCopy copyPort;

	/**
	 * Creates the module. 
	 * @param name The module's name. Should be unique systemwide.
	 */
	public Float1dSparseCopyModule(String name) {
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
		copyPort = new Float1dPortSparseCopy(this, (Float1dSparsePort) port);
		addOutPort("copy", copyPort);
	}

	@Override
	public void addInPort(String name, Port port, boolean reverseDependency) {
		super.addInPort(name, port, reverseDependency);
		copyPort = new Float1dPortSparseCopy(this, (Float1dSparsePort) port);
		addOutPort("copy", copyPort);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
