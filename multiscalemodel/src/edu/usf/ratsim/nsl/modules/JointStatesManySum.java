package edu.usf.ratsim.nsl.modules;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.experiment.utils.Debug;
import edu.usf.ratsim.micronsl.Float1dPort;
import edu.usf.ratsim.micronsl.Float1dPortSum;
import edu.usf.ratsim.micronsl.Module;
import edu.usf.ratsim.micronsl.Port;

public class JointStatesManySum extends Module {

	public JointStatesManySum(String name) {
		super(name);
	}

	@Override
	public void addInPorts(List<Port> states) {
		super.addInPorts(states);
		addOutPort("jointState", new Float1dPortSum(this, (List<Float1dPort>)(List<?>)states));
	}

	public void run() {
		// Do nothing, the port does it all
		if (Debug.printValues && (getName().equals("Votes") || getName().equals("Joint value estimation"))){
			System.out.println(getName());
			for (Port p : getInPorts()){
				Float1dPort fp = ((Float1dPort)p);
				for (int i =0; i < fp.getSize(); i++)
					System.out.print(fp.get(i) + "\t");
				if (getName().equals("Votes"))
				System.out.println();
			}
			System.out.println();
				
		}
			
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
