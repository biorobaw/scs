package com.github.biorobaw.scs.simulation.object.maze_elements.walls;

import com.github.biorobaw.scs.utils.XML;

public class Wall extends AbstractWall{

	public float x1, x2, y1, y2, width, height; //note length = ||(x1,y1)-(x2,y2)||
	
	public Wall(Wall w) {
		x1 = w.x1;
		x2 = w.x2;
		y1 = w.y1;
		y2 = w.y2;
		width = w.width;
		height = w.height;
	}
	
	public Wall(XML xml) {
		x1 = xml.getFloatAttribute("x1");
		y1 = xml.getFloatAttribute("y1");
		x2 = xml.getFloatAttribute("x2");
		y2 = xml.getFloatAttribute("y2");
		width = xml.hasChild("w") ? xml.getFloatAttribute("w") : 0.1f;  //defaults to 10cm
		height = xml.hasChild("h") ? xml.getFloatAttribute("h") : 2f; //defaults to 2m 
	}

	public Wall(float x1, float y1, float x2, float y2,float width, float height) {
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;
		this.width = width;
		this.height = height;
	}
	
	public Wall(float x1, float y1, float x2, float y2) {
		this(x1,y1,x2,y2,0.1f,2f);
	}
	
}
