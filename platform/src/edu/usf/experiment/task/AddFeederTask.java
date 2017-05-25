package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
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


	public void perform(Universe u, Subject s) {
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		fu.addFeeder(id, x, y);
	}

	
}
