package edu.usf.ratsim.experiment.subject.morrisReplay;

import java.util.List;
import java.util.Map;

import edu.usf.micronsl.Model;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

public abstract class MorrisReplayModel extends Model {
	
	public abstract void newTrial();
	public abstract List<PlaceCell> getPlaceCells();
	public abstract Map<Integer, Float> getCellActivation();
	
	public abstract float getMaxActivation();

}
