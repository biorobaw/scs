package edu.usf.experiment.task;

import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class PublishPlatformPosition extends Task {

	public PublishPlatformPosition(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Universe u, Subject s){
		if (u.getPlatforms().isEmpty())
			return;
		
		Point3f pos = u.getPlatforms().get(0).getPosition();
		
		PropertyHolder.getInstance().setProperty("platformPosition", pos.x + "," + pos.y);
	}

	
}
