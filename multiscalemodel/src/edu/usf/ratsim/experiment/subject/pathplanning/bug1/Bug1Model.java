package edu.usf.ratsim.experiment.subject.pathplanning.bug1;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug1Module;
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.PlatformPosition;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SonarReadings;
import edu.usf.ratsim.nsl.modules.pathplanning.ExperienceRoadMap;

public class Bug1Model extends Model {


	public Bug1Model() {
	}

	public Bug1Model(ElementWrapper params, 
			Robot robot) {
		
		SonarReadings sReadings = new SonarReadings("Sonar Readings", (SonarRobot) robot);
		addModule(sReadings);
		
		Position rPos = new Position("Position", (LocalizableRobot) robot);
		addModule(rPos);
		
		HeadDirection orientation = new HeadDirection("HeadDirection", (LocalizableRobot) robot);
		addModule(orientation);
		
		PlatformPosition platPos = new PlatformPosition("Plat Pos");
		addModule(platPos);
		
		Bug1Module bug1 = new Bug1Module("Bug1", robot);
		bug1.addInPort("sonarReadings", sReadings.getOutPort("sonarReadings"));
		bug1.addInPort("sonarAngles", sReadings.getOutPort("sonarAngles"));
		bug1.addInPort("position", rPos.getOutPort("position"));
		bug1.addInPort("orientation", orientation.getOutPort("orientation"));
		bug1.addInPort("platformPosition", platPos.getOutPort("platformPosition"));
		addModule(bug1);
	}

	public void newTrial() {
	}

	public void newEpisode() {
	}

}
