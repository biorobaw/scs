package edu.usf.ratsim.experiment.subject.pathplanning.graphbased;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.ratsim.experiment.subject.NotImplementedException;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug0Module;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug1Module;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug2Module;
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.PlatformPosition;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SonarReadings;
import edu.usf.ratsim.nsl.modules.pathplanning.ExperienceRoadMap;

/**
 * This model allows to switch between bug algorithms depending on the 'algorithm' parameter
 * @author ludo
 *
 */
public class BugAndGraphModel extends Model {


	public BugAndGraphModel() {
	}

	public BugAndGraphModel(ElementWrapper params, Subject subject,
			Robot robot) {
		
		String algorithm = params.getChildText("algorithm");
		
		SonarReadings sReadings = new SonarReadings("Sonar Readings", (SonarRobot) robot);
		addModule(sReadings);
		
		Position rPos = new Position("Position", (LocalizableRobot) robot);
		addModule(rPos);
		
		HeadDirection orientation = new HeadDirection("HeadDirection", (LocalizableRobot) robot);
		addModule(orientation);
		
		PlatformPosition platPos = new PlatformPosition("Plat Pos");
		addModule(platPos);
		
		ExperienceRoadMap erm = new ExperienceRoadMap("Experience road map", subject, algorithm);
		erm.addInPort("sonarReadings", sReadings.getOutPort("sonarReadings"));
		erm.addInPort("sonarAngles", sReadings.getOutPort("sonarAngles"));
		erm.addInPort("position", rPos.getOutPort("position"));
		erm.addInPort("orientation", orientation.getOutPort("orientation"));
		erm.addInPort("platformPosition", platPos.getOutPort("platformPosition"));
		addModule(erm);		
	}

}
