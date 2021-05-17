package com.github.biorobaw.scs.gui.displays.java_fx.drawer.plot;


import java.util.LinkedList;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.displays.java_fx.PanelFX;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;


public class RuntimesDrawer extends Plot {
	
	public double radius = 7;
	public Color color = Color.hsb(0, 0, 0, 0.05);
	
	LinkedList<DrawerGraphics> all_graphics = new LinkedList<>(); // array with all JFX graphics 
	LinkedList<Long> new_data = new LinkedList<>();			  // list of data to be added on next graphics update
	int current_episode = 0;									  // current episode being executed
	long current_cycle = 0;										  // current cycle being executed
	boolean clear_data = false;									  // flag indicating to clear plot data 
	
	LinkedList<Long> buffer = new LinkedList<>();				  // buffer to hold data during updates
	boolean clear_data_in_next_update = false;					  // Indicates whether to set the clear_data flag in the following update		 
	
	

	public RuntimesDrawer(double minx, double miny, double maxx, double maxy) {
		super(minx, miny, maxx, maxy);
		title_x = "Episode";
		title_y = "Cycle";
	}

	@Override
	public void newTrial() {
		clear_data_in_next_update = true;
		buffer = new LinkedList<>();
	}
	
	@Override
	public void endEpisode() {
		buffer.add((long)Experiment.get().getGlobal("cycle"));
	}
	
	@Override
	public void updateData() {
		current_episode = (int)Experiment.get().getGlobal("episode");
		current_cycle = Experiment.get().getGlobal("cycle");
		
		clear_data |= clear_data_in_next_update;
		clear_data_in_next_update = false;
		
		new_data = buffer;
		buffer = new LinkedList<>();
	}
	
	@Override
	protected DrawerScene createGraphics(PanelFX panel) {
		return new DrawerGraphics(panel);
	}

	class DrawerGraphics extends PlotScene {

		int next_episode = 0;

		double circle_radius_x = radius*pixel_x; // note: x and y scale factors are different
		double circle_radius_y = radius*pixel_y; // note: x and y scale factors are different

		Ellipse last_episode_circle = new Ellipse(0,0,circle_radius_x, circle_radius_y);
		
		public DrawerGraphics(PanelFX panel) {
			super(panel);
			all_graphics.add(this);
			data.getChildren().add(last_episode_circle);
			last_episode_circle.setFill(Color.RED);
			
		}
		
		@Override
		public void update() {
			if(clear_data ) clearData();
			if(new_data.size() > 0) addNewData();
			
			last_episode_circle.setCenterX(current_episode);
			last_episode_circle.setCenterY(current_cycle);
			
		}
		
		void clearData() {
			for(var g : all_graphics) {
				g.data.getChildren().clear();
				g.data.getChildren().add(last_episode_circle);
				g.next_episode = 0;
			}
			clear_data = false;
		}
		
		void addNewData() {
			for(var g : all_graphics) {
				var children = g.data.getChildren();
				for(var d : new_data) {
					var c = new Ellipse(g.next_episode++, d, circle_radius_x, circle_radius_y);
					c.setFill(color);
					children.add (0,c);
				}
			}
			new_data.clear();
		}
		
	}
	
	
	
}
