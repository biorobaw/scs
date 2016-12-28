package edu.usf.experiment.task;

import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class PublishPlatformPosition extends Task {

	public PublishPlatformPosition(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getUniverse());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse());
	}

	private void perform(Universe univ) {
		if (univ.getPlatforms().isEmpty())
			return;
		
		Point3f pos = univ.getPlatforms().get(0).getPosition();
		
		PropertyHolder.getInstance().setProperty("platformPosition", pos.x + "," + pos.y);
	}

	
}
