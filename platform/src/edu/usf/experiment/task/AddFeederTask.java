package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class AddFeederTask extends Task {

	private int id;
	private float x;
	private float y;

	public AddFeederTask(ElementWrapper params) {
		super(params);

		id = params.getChildInt("id");
		x = params.getChildFloat("x");
		y = params.getChildFloat("y");
		System.out.println("" + id + " " + x + " " + y );
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
		univ.addFeeder(id, x, y);
	}

	
}
