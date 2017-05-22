package edu.usf.ratsim.experiment.subject.pathplanning.bug2;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug2Module;
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.PlatformPosition;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SonarReadings;

public class Bug2Model extends Model {


	public Bug2Model() {
	}

	public Bug2Model(ElementWrapper params,
			Robot robot) {
		
		SonarReadings sReadings = new SonarReadings("Sonar Readings", (SonarRobot) robot);
		addModule(sReadings);
		
		Position rPos = new Position("Position", (LocalizableRobot) robot);
		addModule(rPos);
		
		HeadDirection orientation = new HeadDirection("HeadDirection", (LocalizableRobot) robot);
		addModule(orientation);
		
		PlatformPosition platPos = new PlatformPosition("Plat Pos");
		addModule(platPos);
		
		Bug2Module bug2 = new Bug2Module("Bug1", robot);
		bug2.addInPort("sonarReadings", sReadings.getOutPort("sonarReadings"));
		bug2.addInPort("sonarAngles", sReadings.getOutPort("sonarAngles"));
		bug2.addInPort("position", rPos.getOutPort("position"));
		bug2.addInPort("orientation", orientation.getOutPort("orientation"));
		bug2.addInPort("platformPosition", platPos.getOutPort("platformPosition"));
		addModule(bug2);
	}

	public void newTrial() {
	}

	public void newEpisode() {
	}

}
