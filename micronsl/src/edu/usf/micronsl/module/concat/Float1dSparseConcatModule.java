package edu.usf.micronsl.module.concat;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dPortSparseConcatenate;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;

public class Float1dSparseConcatModule extends Module {

	/**
	 * Creates the module
	 * 
	 * @param name
	 *            The module's name. Should be unique systemwide.
	 */
	public Float1dSparseConcatModule(String name) {
		super(name);
	}

	@Override
	/**
	 * All computations are done on the fly by the port.
	 */
	public void run() {
	}

	@Override
	public void addInPorts(List<Port> ports) {
		super.addInPorts(ports);
		addOutPort("output", new Float1dPortSparseConcatenate(this,
				(List<Float1dSparsePortMap>) (List<?>) ports));
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
