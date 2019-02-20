package edu.usf.experiment.log;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class WallLogger extends DistributedLogger {

	private List<Wall> walls;

	public WallLogger(ElementWrapper params, String logPath) {
		super(params, logPath);

		walls = new LinkedList<Wall>();
	}

	@Override
	public void log(Universe u, Subject sub) {
		if (!(u instanceof WallUniverse))
			throw new IllegalArgumentException("");
		
		WallUniverse wu = (WallUniverse) u;
		
		for (Wall w : wu.getWalls())
			walls.add(w);
	}
	
	
	

	public String getFileName() {
		return "walls.csv";
	}

	@Override
	public void finalizeLog() {
		synchronized (WallLogger.class) {
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


	

}
