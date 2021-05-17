package com.github.biorobaw.scs.gui.displays.java_fx.drawer.universe;

//import java.awt.Color;
//import java.awt.Graphics;
import java.util.LinkedList;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.displays.java_fx.PanelFX;
import com.github.biorobaw.scs.gui.displays.java_fx.drawer.DrawerFX;
import com.github.biorobaw.scs.simulation.object.RobotProxy;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class PathDrawer extends DrawerFX {

	// DATA 
	private LinkedList<float[]> new_poses = new LinkedList<>();
	private LinkedList<float[]> buffer = new LinkedList<>();
	private RobotProxy robot; // proxy to get data
	
	// STAMP OF LAST TIME NEW_POSES WAS UPDATED:
	String last_trial = "";
	int last_episode = -1;
	
	// Drawing parameters
	Color pathColor = Color.RED;
	double stroke_width = 0.005; // stroke width in milli meters
	

	public PathDrawer(RobotProxy robot){
		this.robot = robot;
	}
	
	@Override
	public void newEpisode() {
		super.newEpisode();
		buffer.clear();
	}
	

	
	@Override 
	public void appendData(){
		var p = robot.getPosition();
		var pos = new float[] {(float)p.getX(),(float)p.getY()};
		buffer.add(pos);
	}
	
	@Override
	public void updateData() {
		new_poses.addAll(buffer);
		buffer.clear();
		
		last_trial = (String)Experiment.get().getGlobal("trial");
		last_episode = (int)Experiment.get().getGlobal("episode");
		
	}

	public void setColor(Color c){
		pathColor = c;
	}
	

	
	class DrawerGraphics extends DrawerScene {


		// scale and translation operations
		Path path = new Path(); // path graphics
		String trial;			// trial corresponding to the path
		int episode;			// episode corresponding to the path
		
		public DrawerGraphics(PanelFX panel) {
			super(panel);
			root.getChildren().add(path);
			path.setStroke(pathColor);
			path.setStrokeWidth(stroke_width);
			
		}

		@Override
		public void update() {
			var elements = path.getElements();
			
			// if new episode, clear previous path
			if(last_trial != trial || last_episode != episode) {
				elements.clear();
				trial = last_trial;
				episode = last_episode;				
			}
			
			// if path is empty, initialize it
			if(elements.size() == 0)
				if(new_poses.size() > 0) {
					var pose = new_poses.pop();
					elements.add(new MoveTo(pose[0], pose[1]));
				}
			
			// add all other position
			for(var pose : new_poses) {
				var p = new LineTo(pose[0], pose[1]);
				elements.add(p);
			}
			
			// clear array of new positions
			new_poses.clear();

		}
		

		

		
	}
	
	@Override
	protected DrawerScene createGraphics(PanelFX panel) {
		return new DrawerGraphics(panel);
	}

}
