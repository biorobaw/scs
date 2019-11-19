package com.github.biorobaw.scs.tasks.episode.feeders;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.files.XML;


public class SetFoodAllFeeders implements Script {

	Maze m = Experiment.get().maze;
	
	public SetFoodAllFeeders(XML xml) {

	}

	
	@Override
	public void newEpisode() {
		for(var f : m.feeders.values())
			f.hasFood = true;
	}
	

	

}
