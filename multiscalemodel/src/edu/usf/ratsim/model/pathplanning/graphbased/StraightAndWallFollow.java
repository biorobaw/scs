package edu.usf.ratsim.model.pathplanning.graphbased;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.usf.experiment.condition.FoundPlatform;
import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.drawer.PathDrawer;
import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.robot.HolonomicRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.ratsim.model.GraphModel;
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.PlatformPosition;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SonarReadings;
import edu.usf.ratsim.nsl.modules.input.SubFoundPlatform;
import edu.usf.ratsim.nsl.modules.pathplanning.Edge;
import edu.usf.ratsim.nsl.modules.pathplanning.ExperienceRoadMap;
import edu.usf.ratsim.nsl.modules.pathplanning.PointNode;

/**
 * This model allows to switch between bug algorithms depending on the 'algorithm' parameter
 * @author ludo
 *
 */
public class StraightAndWallFollow extends Model implements GraphModel {


	private ExperienceRoadMap erm;

	public StraightAndWallFollow() {
	}

	public StraightAndWallFollow(ElementWrapper params,
			Robot robot) {
		
		SonarReadings sReadings = new SonarReadings("Sonar Readings", (SonarRobot) robot);
		addModule(sReadings);
		
		SubFoundPlatform foundPlat = new SubFoundPlatform("Found Plat", (PlatformRobot) robot);
		addModule(foundPlat);
		
		StraightAndWF sawf = new StraightAndWF("Straight and WF", (HolonomicRobot) robot);
		sawf.addInPort("sonarReadings", sReadings.getOutPort("sonarReadings"));
		sawf.addInPort("sonarAngles", sReadings.getOutPort("sonarAngles"));
		addModule(sawf);
		
		DisplaySingleton.getDisplay().addUniverseDrawer(new SonarReadingsDrawer((SonarRobot) robot, (LocalizableRobot) robot), 0);
	}

	public UndirectedGraph<PointNode, Edge> getGraph() {
		return erm.getGraph();
	}

}
