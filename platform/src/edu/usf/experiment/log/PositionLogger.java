package edu.usf.experiment.log;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class PositionLogger extends Logger {

	private List<Pose> poses;
	
	public PositionLogger(ElementWrapper params, String logPath){
		super(params, logPath);
		poses = new LinkedList<Pose>();
	}

	public void finalizeLog() {
//		PropertyHolder props = PropertyHolder.getInstance();
//		String trialName = props.getProperty("trial");
//		String groupName = props.getProperty("group");
//		String subName = props.getProperty("subject");
//		String episode = props.getProperty("episode");
		
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episode = g.get("episode").toString();
		
		System.out.println("Steps: " + poses.size());
		synchronized (PositionLogger.class) {
			PrintWriter writer = getWriter();
			for (Pose pose : poses)
				writer.println(trialName + '\t' + groupName + '\t' + subName
						+ '\t' + episode + '\t' + pose.x + "\t" + pose.y + "\t"
						+ pose.randomAction + '\t' + pose.triedToEat + '\t' + pose.ate);
			poses.clear();
		}
	}

	public void addPose(Pose p){
		poses.add(p);
	}
	

}

