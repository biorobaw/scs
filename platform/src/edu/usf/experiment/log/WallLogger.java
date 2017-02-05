package edu.usf.experiment.log;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.Wall;
import edu.usf.experiment.utils.ElementWrapper;

public class WallLogger extends Logger {

	private List<Wall> walls;

	public WallLogger(ElementWrapper params, String logPath) {
		super(params, logPath);

		walls = new LinkedList<Wall>();
	}

	public void log(Universe univ) {
		for (Wall w : univ.getWalls())
			walls.add(w);
	}
	
	@Override
	public void log(Episode episode) {
		log(episode.getUniverse());
	}

	@Override
	public void log(Trial trial) {
		log(trial.getUniverse());
	}
	
	

	public String getFileName() {
		return "walls.csv";
	}

	@Override
	public void finalizeLog() {
		synchronized (WallLogger.class) {
//			PropertyHolder props = PropertyHolder.getInstance();
//			String trialName = props.getProperty("trial");
//			String groupName = props.getProperty("group");
//			String subName = props.getProperty("subject");
//			String episode = props.getProperty("episode");
			
			Globals g = Globals.getInstance();
			String trialName = g.get("trial").toString();
			String groupName = g.get("group").toString();
			String subName = g.get("subName").toString();
			String episode = g.get("episode").toString();
			
			PrintWriter writer = getWriter();
			for (Wall w : walls)
				writer.println(trialName + '\t' + groupName + '\t' + subName
						+ '\t' + episode + '\t' + w.getX1() + "\t" + w.getY1()
						+ '\t' + w.getX2() + "\t" + w.getY2());

			walls.clear();
		}
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tx\ty\txend\tyend";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getUniverse());		
	}

	

}
