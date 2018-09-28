package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.UniverseLoader;

public class PathDrawer extends Drawer {

	private LocalizableRobot robot;
	private LinkedList<Coordinate> poses;
	private int currentDrawLength = 0;
	public LinkedList<LinkedList<Coordinate>> oldPaths = new LinkedList<>();
	
	Color pathColor = Color.DARK_GRAY;
	
	public boolean drawOldPaths = false;

	public PathDrawer(LocalizableRobot robot){
		this.robot = robot;
		
		poses = new LinkedList<>();
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		

		g.setColor(pathColor);
		if(drawOldPaths)
			for(LinkedList<Coordinate> l : oldPaths) {
				int[][] path = s.scale(l);
				if(l.size()==1){
					g.drawOval(path[0][0]-2, path[1][0]-2, 4, 4);
				}else g.drawPolyline(path[0], path[1], l.size());
			}
		else{
			int[][] path = s.scale(poses);
			if(poses.size()==1){
				g.drawOval(path[0][0]-2, path[1][0]-2, 4, 4);
			}else g.drawPolyline(path[0], path[1], poses.size());
		}
		
			
	}
	
	@Override
	public void endEpisode() {
		poses = new LinkedList<>();
		currentDrawLength = 0;
		if(drawOldPaths) oldPaths.add(poses);
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		currentDrawLength = poses.size();
		
	}
	
	@Override 
	public void appendData(){
		Coordinate pos = robot.getPosition();
		poses.add(pos);
	}
	
	public void setColor(Color c){
		pathColor = c;
	}


}
