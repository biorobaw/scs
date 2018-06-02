package edu.usf.experiment.task.platform;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class PublishPlatformPosition extends Task {

	public PublishPlatformPosition(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Universe u, Subject s){
		if (!(u instanceof PlatformUniverse))
			throw new IllegalArgumentException("");
		
		PlatformUniverse pu = (PlatformUniverse) u;
		
		if (pu.getPlatforms().isEmpty())
			return;
		
		Coordinate pos = pu.getPlatforms().get(0).getPosition();
		
		Globals.getInstance().put("platformPosition", pos.x + "," + pos.y);
	}

	
}
