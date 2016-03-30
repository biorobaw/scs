package edu.usf.ratsim.nsl.modules;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.Float1dPort;
import edu.usf.micronsl.Float1dPortConcatenate;
import edu.usf.micronsl.Module;
import edu.usf.micronsl.Port;

public class JointStatesManyConcatenate extends Module {

	public JointStatesManyConcatenate(String name) {
		super(name);
	}

	public void run() {
	}

	@Override
	public void addInPorts(List<Port> ports) {
		super.addInPorts(ports);
		addOutPort("jointState", new Float1dPortConcatenate(this,
				(List<Float1dPort>) (List<?>) ports));
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
