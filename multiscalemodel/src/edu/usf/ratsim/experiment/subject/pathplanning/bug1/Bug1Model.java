package edu.usf.ratsim.experiment.subject.pathplanning.bug1;

import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Model;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug1Module;
import edu.usf.ratsim.nsl.modules.input.SonarReadings;

public class Bug1Model extends Model {


	public Bug1Model() {
	}

	public Bug1Model(ElementWrapper params, Subject subject,
			SonarRobot robot) {
		
		SonarReadings sReadings = new SonarReadings("Sonar Readings", robot);
		addModule(sReadings);
		
		Bug1Module bug1 = new Bug1Module("Bug1", subject);
		bug1.addInPort("sonarReadings", sReadings.getOutPort("sonarReadings"));
		bug1.addInPort("sonarAngles", sReadings.getOutPort("sonarAngles"));
		addModule(bug1);
	}

	public void newTrial() {
	}

	public void newEpisode() {
	}

}
