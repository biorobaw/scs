package edu.usf.experiment.log;

import java.io.PrintWriter;
import java.util.LinkedList;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class FeedingLogger extends DistributedLogger {

	private LinkedList<FeedingLog> feederLogs;

	public FeedingLogger(ElementWrapper params, String logPath) {
		super(params);

		feederLogs = new LinkedList<FeedingLog>();
	}

	


	// TODO: decide how to solve this issue - Global camera + feeding universe? Maybe redefine concept of just ate
	@Override
	public void perform(Universe u, Subject sub) {
//		if (!(univ instanceof FeederUniverse))
//			throw new IllegalArgumentException("");
//		
//		FeederUniverse fu = (FeederUniverse) univ;
//		
//		if (subject.hasTriedToEat()) {
//			PropertyHolder props = PropertyHolder.getInstance();
//			String cycle = props.getProperty("cycle");
//	//		int feeder = universe.getFoundFeeder();
//			if(subject.getRobot().isFeederClose()){
//	            int feeder = subject.getRobot().getClosestFeeder().getId();
//				FeedingLog fl = new FeedingLog(feeder, cycle, subject.hasEaten(),
//						fu.isFeederFlashing(feeder), fu.isFeederEnabled(feeder));
//				feederLogs.add(fl);
//			}
//		}

	}

	

	@Override
	public void finalizeLog() {
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episode = g.get("episode").toString();


		synchronized (PositionLogger.class) {
			PrintWriter writer = getWriter();
			int prevFeeder = -1;
			for (FeedingLog fl : feederLogs){
				// Error when eating from a non enabled feeder for the first time
				// If eating from an enabled, does not count as error
				// If eating from the same feeder twice, the second does not count as error	
				boolean error = !fl.wasEnabled && fl.feederId != prevFeeder;
				// Correct feeder when is enabled and different from previous
				boolean correct = fl.wasEnabled && fl.feederId != prevFeeder;
				prevFeeder = fl.feederId;
				writer.println(trialName + '\t' + groupName + '\t' + subName
						+ '\t' + episode + '\t' + fl.cycle + '\t' + fl.feederId + "\t" + fl.ate
						+ "\t" + correct + '\t' + fl.wasFlashing + '\t' + fl.wasEnabled + '\t' + error);
			}
			feederLogs.clear();
		}
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tcycle\tfeeder\tate\tcorrect\tflashing\tenabled\terror";
	}

	@Override
	public String getFileName() {
		return "atefeeders.csv";
	}
	
	
	class FeedingLog {

		public int feederId;
		public boolean ate;
		public boolean wasFlashing;
		public boolean wasEnabled;
		public String cycle;
		
		public FeedingLog(int feederId, String cycle, boolean ate, boolean wasFlashing, boolean enabled) {
			super();
			this.feederId = feederId;
			this.cycle = cycle;
			this.ate = ate;
			this.wasFlashing = wasFlashing;
			this.wasEnabled = enabled;
		}
		
	}

}
