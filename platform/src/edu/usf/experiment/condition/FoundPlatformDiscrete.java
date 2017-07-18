package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.universe.platform.PlatformUniverseUtilities;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundPlatformDiscrete implements Condition {
	

	public FoundPlatformDiscrete(ElementWrapper params){
	}

	@Override
	public boolean holds(Episode e) {
		Universe u = e.getUniverse();
		if (!(u instanceof PlatformUniverse))
			throw new IllegalArgumentException("");
		
		PlatformUniverse pu = (PlatformUniverse) u;
		GlobalCameraUniverse gcu = (GlobalCameraUniverse) u;
		
		return PlatformUniverseUtilities.hasRobotFoundPlatformDiscrete(pu.getPlatforms(), gcu.getRobotPosition());
	}

}