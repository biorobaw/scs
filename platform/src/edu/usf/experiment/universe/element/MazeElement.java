package edu.usf.experiment.universe.element;

import java.util.LinkedList;

import edu.usf.experiment.universe.Wall;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class MazeElement {
	public LinkedList<Wall> walls = new LinkedList<Wall>();

	public MazeElement(ElementWrapper params){
		
	}
}
