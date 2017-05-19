package edu.usf.experiment.task;

import javax.vecmath.Point3f;

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.PlatformUniverse;
import edu.usf.experiment.universe.Universe;
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
		
		Point3f pos = pu.getPlatforms().get(0).getPosition();
		
		PropertyHolder.getInstance().setProperty("platformPosition", pos.x + "," + pos.y);
	}

	
}
