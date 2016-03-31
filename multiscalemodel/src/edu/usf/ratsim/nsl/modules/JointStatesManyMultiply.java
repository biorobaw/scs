package edu.usf.ratsim.nsl.modules;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.cartesian.Float1dPortCartesian;

public class JointStatesManyMultiply extends Module {

	private static final float EPS = 0.2f;

	public JointStatesManyMultiply(String name) {
		super(name);

	}

	public void run() {
		// All is done in the multiply port
		
		// Clear optimization cache
		((Float1dPortCartesian)getOutPort("jointState")).clearOptimizationCache();
	}

	@Override
	public void addInPorts(List<Port> ports) {
		super.addInPorts(ports);

		addOutPort("jointState", new Float1dPortCartesian(this,
				(List<Float1dPort>) (List<?>) ports, EPS));
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
