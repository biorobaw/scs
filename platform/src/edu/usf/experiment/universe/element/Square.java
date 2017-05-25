package edu.usf.experiment.universe.element;

import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.utils.ElementWrapper;

public class Square extends MazeElement {
	
	public float l,x,y;

	public Square(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		
		if (params != null){
			l = params.getChildFloat("l");
			x = params.getChildFloat("x");
			y = params.getChildFloat("y");
				
			walls.add(new Wall(x-l/2, y-l/2, x-l/2, y+l/2));
			walls.add(new Wall(x-l/2, y-l/2, x+l/2, y-l/2));
			walls.add(new Wall(x+l/2, y+l/2, x-l/2, y+l/2));
			walls.add(new Wall(x+l/2, y+l/2, x+l/2, y-l/2));
		}
	}

}
