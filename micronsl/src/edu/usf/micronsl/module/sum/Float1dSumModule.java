package edu.usf.micronsl.module.sum;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sum.Float1dPortSum;

public class Float1dSumModule extends Module {

	/**
	 * Creates the module
	 * 
	 * @param name
	 *            The module's name. Should be unique systemwide.
	 */
	public Float1dSumModule(String name) {
		super(name);
	}

	@Override
	public void addInPorts(List<Port> states) {
		super.addInPorts(states);
		addOutPort("jointState", new Float1dPortSum(this, (List<Float1dPort>)(List<?>)states));
	}
	
	@Override
	public void addInPort(Port port) {
		super.addInPort(port);
		addOutPort("jointState", new Float1dPortSum(this, (List<Float1dPort>)(List<?>)getInPorts()));
	}

	/**
	 * All computations are done on the fly by the port.
	 */
	public void run() {
		// Do nothing, the port does it all
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public Set<Module> getValueInfluencingModules() {
		Set<Module> res = new LinkedHashSet<Module>();
		for (Port p : getInPorts())
			res.addAll(p.getOwner().getValueInfluencingModules());
		return res;
	}
}
