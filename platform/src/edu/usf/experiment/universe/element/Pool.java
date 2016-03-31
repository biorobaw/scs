package edu.usf.experiment.universe.element;

import edu.usf.experiment.universe.Wall;
import edu.usf.experiment.utils.ElementWrapper;

public class Pool extends MazeElement {
	
	
	private static final int WALLS_PER_POOL = 8;
	public float r,x,y;


	public Pool(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		
		if (params != null){
			r = params.getChildFloat("r");
			x = params.getChildFloat("x");
			y = params.getChildFloat("y");
			float currentAngle = (float) (Math.PI / WALLS_PER_POOL);
			for (int i = 0; i < WALLS_PER_POOL; i++) {
				float x1 = (float) (x + r * Math.sin(currentAngle));
				float y1 = (float) (y + r * Math.cos(currentAngle));
				float nextAngle = (float) (currentAngle + (2 * Math.PI / WALLS_PER_POOL) % (2*Math.PI));
				float x2 = (float) (r * Math.sin(nextAngle));
				float y2 = (float) (r * Math.cos(nextAngle));
				
				walls.add(new Wall(x1, y1, x2, y2));
				
				currentAngle = nextAngle;
			}
		}
	}

}
