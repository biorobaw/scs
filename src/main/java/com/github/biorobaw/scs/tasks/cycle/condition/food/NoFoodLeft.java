package com.github.biorobaw.scs.tasks.cycle.condition.food;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.tasks.cycle.condition.Condition;
import com.github.biorobaw.scs.utils.files.XML;

public class NoFoodLeft extends Condition {

	Maze maze;
	
	public NoFoodLeft(XML xml) {
		super(xml);
		maze = Experiment.get().maze;
	}

	@Override
	protected boolean condition() {
		for(var f : maze.feeders.values()) 
			if(f.hasFood) return false;
		return true;
	}

}
