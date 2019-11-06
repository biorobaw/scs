package com.github.biorobaw.scs.gui.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.utils.Scaler;
import com.github.biorobaw.scs.gui.utils.Window;
import com.github.biorobaw.scs.simulation.object.RobotProxy;

public class PathDrawer extends Drawer {

	private LinkedList<float[]> poses;
	private LinkedList<float[]> newPoses = new LinkedList<>();
//	private int currentDrawLength = 0;
	public LinkedList<LinkedList<float[]>> oldPaths = new LinkedList<>();
	
	Color pathColor = Color.DARK_GRAY;
	
	public boolean drawOldPaths = false;
	private RobotProxy robot;

	public PathDrawer(RobotProxy robot){
		poses = new LinkedList<>();
		this.robot = robot;
	}

	@Override
	public void draw(Graphics g, Window<Float> panelCoordinates) {
		if(!doDraw) return;

				Scaler s = new Scaler(worldCoordinates, panelCoordinates, true);

		g.setColor(pathColor);
		if(drawOldPaths)
			for( var l : oldPaths) {
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
		newPoses = new LinkedList<>();
//		currentDrawLength = 0;
		if(drawOldPaths) oldPaths.add(poses);
		
	}

	@Override
	public void updateData() {
		poses.addAll(newPoses);
		newPoses.clear();
//		currentDrawLength = poses.size();
		
	}
	
	@Override 
	public void appendData(){
		var p = robot.getPosition();
		var pos = new float[] {(float)p.getX(),(float)p.getY()};
		newPoses.add(pos);
	}
	
	public void setColor(Color c){
		pathColor = c;
	}


}
