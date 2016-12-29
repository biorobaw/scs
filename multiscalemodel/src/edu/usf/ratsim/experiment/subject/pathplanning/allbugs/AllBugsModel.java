package edu.usf.ratsim.experiment.subject.pathplanning.allbugs;

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

/**
 * This model allows to switch between bug algorithms depending on the 'algorithm' parameter
 * @author ludo
 *
 */
public class AllBugsModel extends Model {


	public AllBugsModel() {
	}

	public AllBugsModel(ElementWrapper params, Subject subject,
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
		
		
		Module bug = null;
		if (algorithm.equals("bug0"))
			bug = new Bug0Module("Bug0", subject);
		else if (algorithm.equals("bug1"))
			bug = new Bug1Module("Bug1", subject);
		else if (algorithm.equals("bug2"))
			bug = new Bug2Module("Bug2", subject);
		else
			throw new NotImplementedException();
		
		bug.addInPort("sonarReadings", sReadings.getOutPort("sonarReadings"));
		bug.addInPort("sonarAngles", sReadings.getOutPort("sonarAngles"));
		bug.addInPort("position", rPos.getOutPort("position"));
		bug.addInPort("orientation", orientation.getOutPort("orientation"));
		bug.addInPort("platformPosition", platPos.getOutPort("platformPosition"));
		addModule(bug);
	}

}
