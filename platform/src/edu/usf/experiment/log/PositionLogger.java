package edu.usf.experiment.log;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class PositionLogger extends DistributedLogger {

	private List<Coordinate> poses;
	
	public PositionLogger(ElementWrapper params){
		super(params);
		poses = new LinkedList<Coordinate>();
	}

	public void finalizeLog() {
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episode = g.get("episode").toString();
		
		System.out.println("Steps: " + poses.size());
		synchronized (PositionLogger.class) {
			PrintWriter writer = getWriter();
			for (Coordinate pose : poses)
				writer.println(trialName + '\t' + groupName + '\t' + subName
						+ '\t' + episode + '\t' + pose.x + '\t' + pose.y);
			poses.clear();
		}
	}

	public void addPose(Coordinate c){
		poses.add(c);
	}
	

}

