package com.github.biorobaw.scs.gui.displays.java_fx.drawer.universe;

import java.util.ArrayList;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.displays.java_fx.PanelFX;
import com.github.biorobaw.scs.gui.displays.java_fx.drawer.DrawerFX;
import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.simulation.object.maze_elements.Feeder;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

public class FeederDrawer extends DrawerFX {

	ArrayList<DrawerGraphics> all_drawing_graphics = new ArrayList<>();
	
	ArrayList<FeederData> feederData = new ArrayList<>(); // Array to store gui feeder data
	boolean feeder_set_modified = true;					 // flag that signals feeder set has been modified
	boolean update_graphics = true;
	Maze maze;											 // Pointer to maze
	
	
	// DRAWING PARAMETERS
	float feeding_distance=0.1f;		 // distance from which the rat can eat from the feeder 
	final float CENTER_RADIUS = 0.02f;	 // Radius in meters of inner circle that represents a feeder
	Color hasFoodColor = Color.DARKGREEN; 	 // color when a feeder has food
	Color noFoodColor  = Color.GRAY;	 // color when a feeder has no food
	
	
	public FeederDrawer(float feeding_radius) {
		maze = Experiment.get().maze;
		this.feeding_distance = feeding_radius;
	}

	@Override
	public void newTrial() {
		super.newTrial();
		// TODO: ideally we would like to listen for a change signal in feeders
		// for the time being we assume feeder set only changes during new episode
		feeder_set_modified = true; 
	}

	
	@Override
	public void updateData() {
		if(feeder_set_modified) {
			// create new data wrappers, remove flag, then update all graphics 
			feederData = new ArrayList<>(maze.feeders.size());
			for (var f : maze.feeders.values() )
				feederData.add(new FeederData(f));
			feeder_set_modified = false;
			
			update_graphics = true;
			
		} else {
			for(var fd : feederData) fd.update();
		}
		
		// update which feeders have food
		
		
	}

	public void setHasFoodColor(Color c){
		hasFoodColor = c;
	}
	
	public void setNoFoodColor(Color c) {
		noFoodColor=c;
	}


	@Override
	protected DrawerScene createGraphics(PanelFX panel) {
		return new DrawerGraphics(panel);
	}
	
	
	class DrawerGraphics extends DrawerScene {

		ArrayList<FeederGraphics> feeder_graphics = new ArrayList<>();		
		
		FeederGraphics fg;
		public DrawerGraphics(PanelFX panel) {
			super(panel);		
			all_drawing_graphics.add(this);		
		}
		
		void update_feeder_set() {
			// remove previous graphics:
			root.getChildren().clear();
			
			// add new graphics:
			feeder_graphics.clear();
			for (var d : feederData) new FeederGraphics(d);
			
		}
		

		@Override
		public void update() {
			// if feeder set changed, need to update graphics of all drawer graphics
			// only the first graphics calls this function since otherwise we do not know
			// who is the last drawer that must erase flag
			if(update_graphics) {
				for (var g : all_drawing_graphics)
					g.update_feeder_set();	
					update_graphics = false;
			}
			// Only update colors (assumes feeders do not move):
			for(var g : feeder_graphics) g.setColor();
		}
		
		

		
		class FeederGraphics {
			Circle centerDot 	 = new Circle(0, 0, CENTER_RADIUS); 
			Circle feedingRadius = new Circle(0, 0, feeding_distance);
			Group  feeder = new Group(centerDot, feedingRadius);
			
			FeederData data;
			
			
			public FeederGraphics(FeederData data) {
				// set data, transform and add it feeder to children
				this.data = data;
				feeder.getTransforms().add(new Translate(data.pos[0], data.pos[1]));
				root.getChildren().add(feeder);

				// set color
				feedingRadius.setFill(Color.TRANSPARENT);
				centerDot.setStroke(Color.TRANSPARENT);
				feedingRadius.setStrokeWidth(0.005);
				
				setColor();
				
				
			}
			

			public void setColor() {
				var color = data.hasFood ? hasFoodColor : hasFoodColor;
				centerDot.setFill(color);
				feedingRadius.setStroke(color);
				
			}
			
			
		}

		
	}
	

	
	class FeederData {
		double pos[] = {0,0};
		boolean hasFood = false;
		public Feeder feeder;
		
		public FeederData(Feeder feeder) {
			this.feeder = feeder;
			var xy = feeder.getPosition();
			pos[0] = xy.getX();
			pos[1] = xy.getY();
			update();
		}
		
		public void update() {
			hasFood = feeder.hasFood;
		}
	}



	
}
