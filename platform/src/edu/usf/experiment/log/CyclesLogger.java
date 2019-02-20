package edu.usf.experiment.log;

import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class CyclesLogger extends SingleFileLogger {

	private List<CompletionTime> times;

	public CyclesLogger(ElementWrapper params) {
		super(params);
		
		times = new LinkedList<CompletionTime>();
	}

	@Override
	public void finalizeLog() {
//		PropertyHolder props = PropertyHolder.getInstance();
		Globals g = Globals.getInstance();
		String groupName = (String)g.get("group");
		String subName = (String) g.get("subName");
		String trial = (String)g.get("trial");
		
		for (CompletionTime ct : times)
			append(trial + '\t' + groupName + '\t' + subName + '\t' + ct.episode + '\t' 
					+ ct.cycles);
		
		super.finalizeLog();
	}


	public void perform(Universe u, Subject sub) {
		Globals g = Globals.getInstance();
		int episode = (Integer)g.get("episode");
		int cycle = (Integer)g.get("cycle");
		times.add(new CompletionTime(episode, cycle));
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
