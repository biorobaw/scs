package edu.usf.ratsim.experiment.log;

import java.util.List;
import java.util.Map;

import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

public interface PlaceCellModel {

	Map<Integer, Float> getPCActivity();

	List<PlaceCell> getPlaceCells();


}
