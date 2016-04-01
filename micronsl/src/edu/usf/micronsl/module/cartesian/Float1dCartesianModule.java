package edu.usf.micronsl.module.cartesian;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.cartesian.Float1dPortCartesian;

public class Float1dCartesianModule extends Module {

	/**
	 * The minimum value to consider the output 0 for optimization purposes
	 */
	private float eps;

	/**
	 * Creates the module
	 * 
	 * @param name
	 *            The module's name. Should be unique systemwide.
	 * @param eps
	 *            The minimum value to consider the output 0 for optimization
	 *            purposes
	 */
	public Float1dCartesianModule(String name, float eps) {
		super(name);
		this.eps = eps;
	}

	@Override
	/**
	 * This method only clears the optimization variables from the module to avoid inconsistent results.
	 * All computations are done on the fly by the port.
	 */
	public void run() {
		// All is done in the multiply port
		// Only clears optimization cache
		((Float1dPortCartesian) getOutPort("jointState"))
				.clearOptimizationCache();
	}

	@Override
	public void addInPorts(List<Port> ports) {
		super.addInPorts(ports);

		addOutPort("jointState", new Float1dPortCartesian(this,
				(List<Float1dPort>) (List<?>) ports, eps));
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
