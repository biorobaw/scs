package edu.usf.ratsim.log;

import java.io.PrintWriter;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.DistributedLogger;
import edu.usf.experiment.model.ValueModel;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class ValueEntropyLogger extends DistributedLogger {

	private PrintWriter writer;

	public ValueEntropyLogger(ElementWrapper params, String logPath) {
		super(params, logPath);

		writer = getWriter();
	}


	public void log(Universe u, Subject sub) {
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episode = g.get("episode").toString();
		String cycle = g.get("cycle").toString();

//		System.out.println("Starting to log value");
		
		ValueModel vm = (ValueModel) sub.getModel();
		float valueEntropy = vm.getValueEntropy();

		writer.println(trialName + '\t' + groupName + '\t' + subName + '\t'
				+ episode + '\t' + cycle + "\t" + valueEntropy);

//		System.out.println("Finished loggin value");
	}

	private boolean inCircle(float x, float y, double width) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) <= width / 2;
	}

	@Override
	public void finalizeLog() {
		writer.close();
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tcycle\tentropy";
	}

	@Override
	public String getFileName() {
		return "valueEntropy.csv";
	}

}
