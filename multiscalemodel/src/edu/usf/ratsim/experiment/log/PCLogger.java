package edu.usf.ratsim.experiment.log;

import java.io.PrintWriter;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.subject.TSPSubject;
import edu.usf.ratsim.experiment.subject.TSPFrance.TSPSubjectFrance;
import edu.usf.ratsim.experiment.subject.morrisReplay.MorrisReplaySubject;
import edu.usf.ratsim.experiment.subject.multipleT.MultipleTSubject;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

public class PCLogger extends Logger {

	private List<PlaceCell> cells;

	public PCLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(Subject sub) {
		if (sub instanceof TSPSubject)
			cells = ((TSPSubject) sub).getPlaceCells();
		else if (sub instanceof MultipleTSubject)
			cells = ((MultipleTSubject) sub).getPlaceCells();
		else if (sub instanceof TSPSubjectFrance)
			cells = ((TSPSubjectFrance) sub).getPlaceCells();
		else if (sub instanceof MorrisReplaySubject)
			cells = ((MorrisReplaySubject) sub).getPlaceCells();
		else
			throw new IllegalArgumentException(
					"PC logger can only be used with TSPModel or MultipleTModel");


		
	}

	@Override
	public void log(Episode episode) {
		log((Subject)episode.getSubject());
	}

	@Override
	public void log(Trial trial) {
		log((Subject)trial.getSubject());
	}

	public String getFileName() {
		return "placecells.csv";
	}

	@Override
	public void finalizeLog() {
		synchronized (PCLogger.class) {
			System.out.println("[+] Logging cells");
//			PropertyHolder props = PropertyHolder.getInstance();
//			String trialName = props.getProperty("trial");
//			String groupName = props.getProperty("group");
//			String subName = props.getProperty("subject");
//			String episode = props.getProperty("episode");
			
			Globals g = Globals.getInstance();
			String groupName = g.get("group").toString();
			String subName = g.get("subName").toString();
			
//			setLogPath(getLogPath() + groupName + "/" + subName + "/");
//			System.out.println("logpath:       " +getLogPath());
//			String trialName = g.get("trial").toString();
//			String episode = g.get("episode").toString();

			PrintWriter writer = getWriter();
			int cellNum = 0;
			for (PlaceCell cell : cells) {
				writer.println(groupName + '\t' 
										 + subName + '\t'
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
		log((Subject)experiment.getSubject());
	}

}
