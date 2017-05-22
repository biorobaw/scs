package edu.usf.ratsim.experiment.subject.pathplanning.bug0;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug0Module;
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.PlatformPosition;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SonarReadings;

public class Bug0Model extends Model {


	public Bug0Model() {
	}

	public Bug0Model(ElementWrapper params,
			Robot robot) {
		
		SonarReadings sReadings = new SonarReadings("Sonar Readings", (SonarRobot) robot);
		addModule(sReadings);
		
		Position rPos = new Position("Position", (LocalizableRobot) robot);
		addModule(rPos);
		
		HeadDirection orientation = new HeadDirection("HeadDirection", (LocalizableRobot) robot);
		addModule(orientation);
		
		PlatformPosition platPos = new PlatformPosition("Plat Pos");
		addModule(platPos);
		
		Bug0Module bug0 = new Bug0Module("Bug0", robot);
		bug0.addInPort("sonarReadings", sReadings.getOutPort("sonarReadings"));
		bug0.addInPort("sonarAngles", sReadings.getOutPort("sonarAngles"));
		bug0.addInPort("position", rPos.getOutPort("position"));
		bug0.addInPort("orientation", orientation.getOutPort("orientation"));
		bug0.addInPort("platformPosition", platPos.getOutPort("platformPosition"));
		addModule(bug0);
	}

	public void newTrial() {
	}

	public void newEpisode() {
	}

}
