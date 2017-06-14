package edu.usf.experiment.log;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class PositionLogger extends DistributedLogger {

	private List<Coordinate> poses;
	
	public PositionLogger(ElementWrapper params, String logPath){
		super(params, logPath);
		poses = new LinkedList<Coordinate>();
	}

	public void finalizeLog() {
		PropertyHolder props = PropertyHolder.getInstance();
		String trialName = props.getProperty("trial");
		String groupName = props.getProperty("group");
		String subName = props.getProperty("subject");
		String episode = props.getProperty("episode");
		
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

