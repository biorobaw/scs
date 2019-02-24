package edu.usf.ratsim.log;

import java.io.PrintWriter;
import java.util.List;

import edu.usf.experiment.Globals;
import edu.usf.experiment.log.DistributedLogger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.model.PlaceCellModel;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

public class PCLogger extends DistributedLogger {

	private List<PlaceCell> cells;

	public PCLogger(ElementWrapper params) {
		super(params);
	}


	public void perform(Universe u, Subject sub) {
		if (sub.getModel() instanceof PlaceCellModel)
			cells = ((PlaceCellModel)sub.getModel()).getPlaceCells();
		else
			throw new IllegalArgumentException(
					"PC logger can only be used with PlaceCellModel");


		
	}

	
	public String getFileName() {
		return "placecells.csv";
	}

	@Override
	public void finalizeLog() {
		synchronized (PCLogger.class) {
			System.out.println("[+] Logging cells");
			Globals g = Globals.getInstance();
			String groupName = g.get("group").toString();
			String subName = g.get("subName").toString();

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


}