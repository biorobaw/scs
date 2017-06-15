package edu.usf.ratsim.log;

import java.io.PrintWriter;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.DistributedLogger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.model.PlaceCellModel;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

public class PCLogger extends DistributedLogger {

	private List<PlaceCell> cells;

	public PCLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(Subject sub) {
		if (sub.getModel() instanceof PlaceCellModel)
			cells = ((PlaceCellModel)sub.getModel()).getPlaceCells();
		else
			throw new IllegalArgumentException(
					"PC logger can only be used with PlaceCellModel");


		
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
			for (PlaceCell cell : cells) {
				writer.println(groupName + '\t' + subName + '\t'
						+ cellNum + '\t'
						+ cell.getPreferredLocation().x  + '\t'
						+ cell.getPreferredLocation().y + '\t'
						+ cell.getPlaceRadius());
				cellNum++;
			}

			//cells.clear();
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
