package com.github.biorobaw.scs.tasks.cycle.condition.food;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.robot.modules.FeederModule;
import com.github.biorobaw.scs.tasks.cycle.condition.Condition;
import com.github.biorobaw.scs.utils.files.XML;

public class SubjectAte extends Condition {

	boolean subject_ate = false;
	
	public SubjectAte(XML xml) {
		super(xml);
		var subject = xml.getAttribute("subject");
		Experiment.get()
				  .subjects
				  .get(subject)
				  .getRobot()
				  .<FeederModule>getModule("FeederModule")
				  .addEatEventListener(()->subject_ate=true);
	}

	@Override
	protected boolean condition() {
		return subject_ate;
	}
	
	@Override
	public void newEpisode() {
		super.newEpisode();
		subject_ate = false;
	}

}
