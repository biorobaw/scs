package edu.usf.ratsim.nsl.modules;

import edu.usf.micronsl.Float1dPortSparseCopy;
import edu.usf.micronsl.Float1dSparsePort;
import edu.usf.micronsl.Module;
import edu.usf.micronsl.Port;

public class CopyStateModuleSparse extends Module {

	private Float1dPortSparseCopy copyPort;

	public CopyStateModuleSparse(String name) {
		super(name);
	}

	@Override
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
