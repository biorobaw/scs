package edu.usf.ratsim.model.pathplanning.apf;

import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.drawer.PathDrawer;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.ratsim.model.pathplanning.graphbased.ExpGraphDrawer;
import edu.usf.ratsim.model.pathplanning.graphbased.SonarReadingsDrawer;
import edu.usf.ratsim.nsl.modules.actionselection.apf.APFModule;
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.PlatformPosition;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SonarReadings;

public class APFModel extends Model {


	public APFModel() {
	}

	public APFModel(ElementWrapper params,
			Robot robot) {
		
		SonarReadings sReadings = new SonarReadings("Sonar Readings", (SonarRobot) robot);
		addModule(sReadings);
		
		Position rPos = new Position("Position", (LocalizableRobot) robot);
		addModule(rPos);
		
		HeadDirection orientation = new HeadDirection("HeadDirection", (LocalizableRobot) robot);
		addModule(orientation);
		
		PlatformPosition platPos = new PlatformPosition("Plat Pos");
		addModule(platPos);
		
		APFModule apf = new APFModule("APF", robot);
		apf.addInPort("sonarReadings", sReadings.getOutPort("sonarReadings"));
		apf.addInPort("sonarAngles", sReadings.getOutPort("sonarAngles"));
		apf.addInPort("position", rPos.getOutPort("position"));
		apf.addInPort("orientation", orientation.getOutPort("orientation"));
		apf.addInPort("platformPosition", platPos.getOutPort("platformPosition"));
		addModule(apf);
		
		DisplaySingleton.getDisplay().addDrawer("universe","sonar",new SonarReadingsDrawer((SonarRobot) robot, (LocalizableRobot) robot), 0);
		DisplaySingleton.getDisplay().addDrawer("universe","path",new PathDrawer((LocalizableRobot) robot));
	}

	public void newTrial() {
	}

	public void newEpisode() {
	}

}
