package edu.usf.ratsim.experiment.subject.sonartest;

import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.ratsim.nsl.modules.input.SonarReadings;

public class SonarTestModel extends Model {


	public SonarTestModel() {
	}

	public SonarTestModel(ElementWrapper params, Subject subject,
			SonarRobot robot) {
		
		SonarReadings sReadings = new SonarReadings("Sonar Readings", robot);
		addModule(sReadings);
	}

	public void newTrial() {
	}

	public void newEpisode() {
	}

}
