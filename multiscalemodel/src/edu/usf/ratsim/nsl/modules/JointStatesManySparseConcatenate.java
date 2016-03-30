package edu.usf.ratsim.nsl.modules;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.Float1dPortSparseConcatenate;
import edu.usf.micronsl.Float1dSparsePortMap;
import edu.usf.micronsl.Module;
import edu.usf.micronsl.Port;

public class JointStatesManySparseConcatenate extends Module {

	public JointStatesManySparseConcatenate(String name) {
		super(name);
	}

	public void run() {
	}

	@Override
	public void addInPorts(List<Port> ports) {
		super.addInPorts(ports);
		addOutPort("jointState", new Float1dPortSparseConcatenate(this,
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
