package edu.usf.ratsim.experiment.log;

import java.io.PrintWriter;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.subject.MultiScaleArtificialPCSubject;
import edu.usf.ratsim.nsl.modules.ExponentialConjCell;

public class PCLogger extends Logger {

	private List<ExponentialConjCell> cells;

	public PCLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(Subject sub) {
		if (!(sub instanceof MultiScaleArtificialPCSubject))
			throw new IllegalArgumentException(
					"PC logger can only be used with TSPModel");

		MultiScaleArtificialPCSubject msSub = (MultiScaleArtificialPCSubject) sub;

		cells = msSub.getPlaceCells();
	}

	@Override
	public void log(Episode episode) {
		log(episode.getSubject());
	}

	@Override
	public void log(Trial trial) {
		log(trial.getSubject());
	}

	public String getFileName() {
		return "placecells.csv";
	}

	@Override
	public void finalizeLog() {
		synchronized (PCLogger.class) {
			System.out.println("[+] Logging cells");
			PropertyHolder props = PropertyHolder.getInstance();
			String trialName = props.getProperty("trial");
			String groupName = props.getProperty("group");
			String subName = props.getProperty("subject");
			String episode = props.getProperty("episode");

			PrintWriter writer = getWriter();
			int cellNum = 0;
			for (ExponentialConjCell cell : cells) {
				writer.println(groupName + '\t' + subName + '\t'
						+ cellNum + '\t'
						+ cell.getPreferredLocation().x  + '\t'
						+ cell.getPreferredLocation().y + '\t'
						+ cell.getPlaceRadius());
				cellNum++;
			}

			cells.clear();
		}
	}

	@Override
	public String getHeader() {
		return "tgroup\tsubject\tcellNum\tx\ty\tplaceradius";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getSubject());
	}

}
