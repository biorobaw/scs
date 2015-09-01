package edu.usf.ratsim.nsl.modules;

import edu.usf.ratsim.micronsl.Float1dPortCopy;
import edu.usf.ratsim.micronsl.Float1dPort;
import edu.usf.ratsim.micronsl.Module;
import edu.usf.ratsim.micronsl.Port;

public class CopyStateModule extends Module {

	private Float1dPortCopy copyPort;

	public CopyStateModule(String name) {
		super(name);
	}

	@Override
	public void run() {
		copyPort.copy();
	}

	@Override
	public void addInPort(String name, Port port) {
		super.addInPort(name, port);
		copyPort = new Float1dPortCopy(this, (Float1dPort) port);
		addOutPort("copy", copyPort);
	}

	@Override
	public void addInPort(String name, Port port, boolean reverseDependency) {
		super.addInPort(name, port, reverseDependency);
		copyPort = new Float1dPortCopy(this, (Float1dPort) port);
		addOutPort("copy", copyPort);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
