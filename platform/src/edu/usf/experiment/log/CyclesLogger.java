package edu.usf.experiment.log;

import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.utils.ElementWrapper;

public class CyclesLogger extends SingleFileLogger {

	private List<CompletionTime> times;

	public CyclesLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		times = new LinkedList<CompletionTime>();
	}

	@Override
	public void log(Episode episode) {
		log();
	}

	@Override
	public void finalizeLog() {
		PropertyHolder props = PropertyHolder.getInstance();
		String groupName = props.getProperty("group");
		String subName = props.getProperty("subject");
		String trial = props.getProperty("trial");
		
		for (CompletionTime ct : times)
			append(trial + '\t' + groupName + '\t' + subName + '\t' + ct.episode + '\t' 
					+ ct.cycles);
		
		super.finalizeLog();
	}

	private void log() {
		PropertyHolder props = PropertyHolder.getInstance();
		int episode = Integer.parseInt(props.getProperty("episode"));
		int cycle = Integer.parseInt(props.getProperty("cycle"));
		times.add(new CompletionTime(episode, cycle));
	}

	@Override
	public void log(Trial trial) {

	}

	@Override
	public void log(Experiment experiment) {
	}

	@Override
	public String getFileName() {
		return "runtimes.csv";
	}

	@Override
	public String getName() {
		return "Runtimes";
	}

}

class CompletionTime {
	public int episode;
	public int cycles;
	public CompletionTime(int episode, int cycles) {
		this.episode = episode;
		this.cycles = cycles;
	}
}
