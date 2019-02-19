package edu.usf.experiment.universe.element;

import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.utils.ElementWrapper;

public class Rectangle extends MazeElement {
	
	public float x1,y1,x2,y2;

	public Rectangle(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		
		if (params != null){
			x1 = params.getChildFloat("x1");
			y1 = params.getChildFloat("y1");
			x2 = params.getChildFloat("x2");
			y2 = params.getChildFloat("y2");
				
			walls.add(new Wall(x1,y1,x1,y2));
			walls.add(new Wall(x2,y1,x2,y2));
			walls.add(new Wall(x1,y1,x2,y1));
			walls.add(new Wall(x1,y2,x2,y2));
		}
	}

}
