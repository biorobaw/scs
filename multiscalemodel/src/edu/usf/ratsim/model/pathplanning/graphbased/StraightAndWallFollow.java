package edu.usf.ratsim.model.pathplanning.graphbased;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.usf.experiment.display.Display;
import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.ratsim.model.GraphModel;
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
		
		StraightAndWF sawf = new StraightAndWF("Straight and WF", robot);
		sawf.addInPort("sonarReadings", sReadings.getOutPort("sonarReadings"));
		sawf.addInPort("sonarAngles", sReadings.getOutPort("sonarAngles"));
		addModule(sawf);
		
		Display.getDisplay().addDrawer("universe","sonar",new SonarReadingsDrawer( robot), 0);
	}

	public UndirectedGraph<PointNode, Edge> getGraph() {
		return erm.getGraph();
	}

}
