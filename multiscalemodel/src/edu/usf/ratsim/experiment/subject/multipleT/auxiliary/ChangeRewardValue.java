package edu.usf.ratsim.experiment.subject.multipleT.auxiliary;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.subject.multipleT.MultipleTSubject;

public class ChangeRewardValue extends Task {

	float newValue;
	
	public ChangeRewardValue(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		newValue = params.getChildFloat("reward");
	}

	@Override
	public void perform(Experiment experiment) {
		// TODO Auto-generated method stub
		perform(experiment.getSubject());

	}

	@Override
	public void perform(Trial trial) {
		// TODO Auto-generated method stub
		perform(trial.getSubject());

	}

	@Override
	public void perform(Episode episode) {
		// TODO Auto-generated method stub
		perform(episode.getSubject());

	}
	
	public void perform(Subject s){
		MultipleTSubject ts = (MultipleTSubject)s;
		
		ts.changeRewardValue(newValue);
		
	}

}
