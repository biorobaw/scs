package edu.usf.experiment.universe.element;

import edu.usf.experiment.universe.Wall;
import edu.usf.experiment.utils.ElementWrapper;

public class MultipleT extends MazeElement{
	
	private float width;
	private float length;
	private float x;
	private float y;

	private float[] generateWalls(float startX,float startY,ElementWrapper e){
		
		float minx = startX - length/2;
		float maxx = startX + length/2 + width;
		float miny = startY;
		float maxy = startY + length + width;
		
		walls.add(new Wall(startX,  startY,  startX					, startY+=length));
		walls.add(new Wall(startX,  startY,  startX -= (length/2)	, startY));
		walls.add(new Wall(startX,  startY,  startX 			 	, startY+=width));
		
		ElementWrapper left = e.getChild("left");
		if (left!=null) {
			float[] boundbox = generateWalls(startX, startY, left);
			minx = boundbox[0] < minx ? boundbox[0] : minx;
			maxx = boundbox[1] > maxx ? boundbox[1] : maxx;
			miny = boundbox[2] < miny ? boundbox[2] : miny;
			maxy = boundbox[3] > maxy ? boundbox[3] : maxy;
			startX+=width;
		}
		else{
			walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}
		
		walls.add(new Wall(startX,  startY,  startX += (length-width)		, startY));
		
		ElementWrapper right = e.getChild("right");
		if (right!=null) {
			float[] boundbox = generateWalls(startX, startY, right);
			minx = boundbox[0] < minx ? boundbox[0] : minx;
			maxx = boundbox[1] > maxx ? boundbox[1] : maxx;
			miny = boundbox[2] < miny ? boundbox[2] : miny;
			maxy = boundbox[3] > maxy ? boundbox[3] : maxy;
			startX+=width;
		}
		else{
			walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}
		
		walls.add(new Wall(startX,  startY,  startX 			 	, startY-=width));
		walls.add(new Wall(startX,  startY,  startX -= (length/2)	, startY));
		walls.add(new Wall(startX,  startY,  startX					, startY-=length));
		
		
				
		return new float[] {minx,maxx,miny,maxy};
		
	}

	public MultipleT(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub

		width = params.getChildFloat("width");
		length = params.getChildFloat("length");
		
		x = params.getChildFloat("x");
		y = params.getChildFloat("y");
		
		float[] boundbox = generateWalls(x, y, params);
		walls.add(new Wall(x+width,  y,  x  , y));
		
	}

}
