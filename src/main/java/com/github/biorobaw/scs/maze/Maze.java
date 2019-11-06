package com.github.biorobaw.scs.maze;

import java.util.HashMap;
import java.util.HashSet;
//import com.github.biorobaw.scs.simulation.AbstractSimulator;
import com.github.biorobaw.scs.simulation.object.maze_elements.Feeder;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.AbstractWall;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.Wall;
import com.github.biorobaw.scs.utils.XML;

/**
 * Defines an empty maze that allows adding walls and feeders.
 * @author bucef
 *
 */
public class Maze {
	
	public HashSet<AbstractWall>   walls   = new HashSet<>();
	public HashMap<Integer,Feeder> feeders = new HashMap<>();
	
	/**
	 * Constructor from an xml node
	 * @param xml the xml node
	 */
	public Maze(XML xml) {
		
		if(xml.hasAttribute("file")) {
			var xml2 = new XML(xml.getAttribute("file"));
			xml = xml2.merge(xml);
		}
		
		for(var w : xml.getChildren("wall")) {
			if(w.hasAttribute("class")) walls.add((AbstractWall)w.loadObject());
			else walls.add(w.loadObject(Wall.class));
		};
		
		for(var f : xml.getChildren("feeder")) {
			Feeder fo;
			if( f.hasAttribute("class") ) fo = f.<Feeder>loadObject();
			else fo = f.loadObject(Feeder.class);
			feeders.put(fo.feeder_id, fo);
		}
		
		for(var g : xml.getChildren("generator")) {
			var maze = (Maze)g.loadObject();
			walls.addAll(maze.walls);
			feeders.putAll(maze.feeders);			
		}
	}
	
	public void addElementsToSimulator() {
		for(var w : walls) w.addToSimulation();
		for(var f : feeders.values()) f.addToSimulation();
	}
	
	
	public void addWall(AbstractWall wall) {
		walls.add(wall);
	}
	
	public void addFeeder(Feeder feeder) {
		feeders.put(feeder.feeder_id,feeder);
	}
	
	public void removeWall(AbstractWall wall) {
		walls.remove(wall);
		wall.removeFromSimulation();
	}
	
	public void removeFeeder(Feeder feeder) {
		feeders.remove(feeder.feeder_id);
		feeder.removeFromSimulation();
	}
	
}
