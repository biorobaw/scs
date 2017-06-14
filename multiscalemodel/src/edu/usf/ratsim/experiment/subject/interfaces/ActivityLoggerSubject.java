package edu.usf.ratsim.experiment.subject.interfaces;

import java.util.List;
import java.util.Map;

import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

public interface ActivityLoggerSubject {

	public Map<Integer, Float> getPCActivity();
	public List<PlaceCell> getPlaceCells();
}
