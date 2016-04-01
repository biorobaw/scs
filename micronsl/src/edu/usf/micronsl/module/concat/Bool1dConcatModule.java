package edu.usf.micronsl.module.concat;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Bool1dPort;
import edu.usf.micronsl.port.onedimensional.concat.Bool1dPortConcatenate;

/**
 * This module concatenates the output of a set of 1d ports using the
 * concatenate port
 * 
 * @author odroid
 * 
 */
public class Bool1dConcatModule extends Module {

	/**
	 * Builds the module
	 * 
	 * @param name
	 *            The modules name. Should be unique systemwide.
	 */
	public Bool1dConcatModule(String name) {
		super(name);
	}

	/**
	 * Run does not perform any operation, as the operations are done on the fly
	 * by the underlying port
	 */
	@Override
	public void run() {
	}

	@Override
	public void addInPorts(List<Port> ports) {
		super.addInPorts(ports);
		addOutPort("jointState", new Bool1dPortConcatenate(this,
				(List<Bool1dPort>) (List<?>) ports));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	/**
	 * The influencing modules are the join of all the input modules influencing modules.
	 */
	public Set<Module> getValueInfluencingModules() {
		Set<Module> res = new LinkedHashSet<Module>();
		for (Port p : getInPorts())
			res.addAll(p.getOwner().getValueInfluencingModules());
		return res;
	}

}
