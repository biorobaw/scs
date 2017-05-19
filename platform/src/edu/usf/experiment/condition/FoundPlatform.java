package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.universe.PlatformUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundPlatform implements Condition {
	

	public FoundPlatform(ElementWrapper params){
	}

	@Override
	public boolean holds(Episode e) {
		Universe u = e.getUniverse();
		if (!(u instanceof PlatformUniverse))
			throw new IllegalArgumentException("");
		
		PlatformUniverse pu = (PlatformUniverse) u;
		
		return pu.hasRobotFoundPlatform();
	}

}
