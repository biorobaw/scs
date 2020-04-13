package com.github.biorobaw.scs.tasks.trial.feeders;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.simulation.object.maze_elements.Feeder;
import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.files.XML;

public class AddFeeders implements Script {
	Maze m = Experiment.get().maze;
	float[] xs, ys;
	int[] ids;
	
	
	public AddFeeders(XML xml) {
		xs = xml.getFloatArrayAttribute("x");
		ys = xml.getFloatArrayAttribute("y");
		ids = xml.getIntArrayAttribute("id");
	}

	
	@Override
	public void newTrial() {
		for(int i=0; i < xs.length; i++)
			m.addFeeder(new Feeder(ids[i], xs[i], ys[i]));
	}
}
