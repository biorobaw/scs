package edu.usf.ratsim.log;

import java.io.PrintWriter;
import java.util.Map;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.DistributedLogger;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.model.PlaceCellModel;

public class PCActivityLogger extends DistributedLogger {

	PrintWriter writer = null;

	public PCActivityLogger(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Universe u, Subject sub) {
		if (writer == null)
			writer = getWriter();

		PlaceCellModel pcm = (PlaceCellModel) sub.getModel();
		Map<Integer, Float> activation = pcm.getPCActivity();
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episode = g.get("episode").toString();
		String cycle = g.get("cycle").toString();

		for (Integer cell : activation.keySet())
			writer.println(trialName + '\t' + groupName + '\t' + subName + '\t'
					+ episode + '\t' + cycle + '\t' + cell + '\t'
					+ activation.get(cell));
	}


	@Override
	public String getFileName() {
		return "cellactivity.csv";
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tcycle\tcellNum\tactivation";
	}


	@Override
	public void finalizeLog() {
		if (writer != null)
			writer.close();
	}

}
