package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;

public class SaveSubject extends Task {

	//private ElementWrapper filename;

	public SaveSubject(ElementWrapper params) {
		super(params);

		//filename = params.getChild("filename");
	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getSubject());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getSubject());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getSubject());
	}

	public void perform(Subject sub) {
//		sub.save(PropertyHolder.getInstance().getProperty("log.directory") + filename);
		sub.save();
		
	}

}
