package com.github.biorobaw.scs.maze.mazes;

import java.util.LinkedList;

import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.Wall;
import com.github.biorobaw.scs.utils.XML;

public class PolygonalMaze extends Maze {
	float apotema; // distance from center to any of the walls
	int numSides;
	float width;
	float height;
	
	float x,y;
	
	LinkedList<Wall> fixed_walls = new LinkedList<>();
	
	public PolygonalMaze(XML xml) {
		super(xml);
		// TODO Auto-generated constructor stub
		x 		 = xml.getFloatAttribute("x");
		y		 = xml.getFloatAttribute("y");
		apotema  = xml.getFloatAttribute("apotema");
		numSides = xml.getIntAttribute("numSides");
		width	 = xml.hasChild("width")  ? xml.getFloatAttribute("width") : 0.1f; //default 10cm
		height   = xml.hasChild("height") ? xml.getFloatAttribute("height") : 2f; // default 2m
		make();
	}
	
	private void make() {
		var apotema2 = apotema + width/2;
		float angle = 2*(float)Math.PI / numSides;
		float half = angle/2;
		
		for(int i=0;i<numSides;i++) {
			var x1 = x+apotema2*(float)Math.cos(angle*i - half);
			var x2 = x+apotema2*(float)Math.cos(angle*i + half);
			var y1 = y+apotema2*(float)Math.sin(angle*i - half);
			var y2 = y+apotema2*(float)Math.sin(angle*i + half);
			fixed_walls.add(new Wall(x1,y1,x2,y2,width,height));
		}
		
		for(var w : fixed_walls) addWall(w);
	}
}
