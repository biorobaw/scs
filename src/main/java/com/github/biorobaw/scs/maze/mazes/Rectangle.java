package com.github.biorobaw.scs.maze.mazes;

import java.util.LinkedList;

import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.Wall;
import com.github.biorobaw.scs.utils.XML;


public class Rectangle extends Maze {
	
	public float x1,y1,x2,y2,width,height;
	LinkedList<Wall> fixed_walls = new LinkedList<>();
	
	public Rectangle(XML params) {	
			super(params);
			x1 = params.getFloatAttribute("x1");
			y1 = params.getFloatAttribute("y1");
			x2 = params.getFloatAttribute("x2");
			y2 = params.getFloatAttribute("y2");
			width = params.hasAttribute("width") ? params.getFloatAttribute("width") : 0.1f;
			height = params.hasAttribute("height") ? params.getFloatAttribute("height") : 2f;
				
			fixed_walls.add(new Wall(x1,y1,x1,y2,width,height));
			fixed_walls.add(new Wall(x2,y1,x2,y2,width,height));
			fixed_walls.add(new Wall(x1,y1,x2,y1,width,height));
			fixed_walls.add(new Wall(x1,y2,x2,y2,width,height));
			
			for(var w : fixed_walls) addWall(w);
	}
	
	
}
