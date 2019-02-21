package edu.usf.experiment.log;

import java.io.PrintWriter;

import edu.usf.experiment.Globals;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class PlatformLogger extends DistributedLogger {

	public PlatformLogger(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Universe u, Subject sub) {
		PlatformUniverse fu = (PlatformUniverse) u;
		
		synchronized (PlatformLogger.class) {
			Globals g = Globals.getInstance();
			String groupName = g.get("group").toString();
			String subName = g.get("subName").toString();

			PrintWriter writer = getWriter();
			for (Platform p : fu.getPlatforms())
				writer.println(groupName + '\t' + subName + '\t' 
						+ p.getPosition().x + '\t' + p.getPosition().y + '\t');
		}
	}


	public String getFileName() {
		return "feeders.csv";
	}

	@Override
	public void finalizeLog() {
	}

	@Override
	public String getHeader() {
		return "group\tsubject\tx\ty";
	}


}
