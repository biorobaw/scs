package com.github.biorobaw.scs.gui.displays.java_fx.drawer.universe;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.displays.java_fx.PanelFX;
import com.github.biorobaw.scs.gui.displays.java_fx.drawer.DrawerFX;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CycleDataDrawer extends DrawerFX {
	
	
	Experiment e;
	
	private double x, y; // screen coordinates of upper left corner of drawer 
	private String group;
	private String run_id;
	private String trial;
	private String episode;
	private int size;
	private String cycle;
	
	private Color color = Color.BLACK;
	
	// Utility to calculate frames per second
	static AnimationTimer fps_calculator = null;
	static double fps = 0;
	
	// Utility to calculte simulation cycles per second
	double cycles_per_second = 0;
	int oldest_id = 0;
	final int buffer_size = 400;
	long cycles[] = new long[buffer_size];
	long times[]  = new long[buffer_size];
	boolean buffer_full = false;

	public CycleDataDrawer() {
		this(30,20,16);
	}
	
	/**
	 * @param x The relative position of the text in the panel, 
	 * if x < 0 distance from left margin else distance from right
	 * @param y The relative position of the text in the panel,
	 * if y > 0 distance from the top margin else distance from the bottom
	 * @param size Size of the text in pixels
	 */
	public CycleDataDrawer(double x, double y,int size){
		this.x = x;
		this.y = y;
		this.size = size;
		e = Experiment.get();
		this.group   = e.getGlobal("group");
		this.run_id = e.getGlobal("run_id").toString();
		
		// init times:
		var now = System.nanoTime();
		for(int i=0; i <buffer_size; i++) times[i]=now;
		
	}
	
	@Override
	public void endEpisode() {
	}

	@Override
	public void updateData() {
		// get trial, episode and cycle
		trial   = e.getGlobal("trial");
		episode = e.getGlobal("episode").toString();
		cycle   = e.getGlobal("cycle").toString();
		
		// caclulate simulation cycles per second:
		var abs_cycle = (long)e.getGlobal("cycle_abs");
		var current_time = System.nanoTime();
				
		var id = buffer_full ? oldest_id : 0;
		long delta_cycle = abs_cycle - cycles[id];
		long delta_time  = current_time - times[id];
		
		cycles[oldest_id] = abs_cycle;
		times[oldest_id] = current_time;
		
		oldest_id = (oldest_id+1) % buffer_size;
		buffer_full |= oldest_id == 0; // once the oldest id becomes 0, the buffer is full
		
		cycles_per_second = delta_cycle * 1e9d / delta_time;
//		var s = String.format("delta: %d %d , %.2f, %.2f", abs_cycle, delta_cycle, delta_time/1e9d, cycles_per_second);
//		System.out.println(s);;
		
		
	}



	@Override
	protected DrawerScene createGraphics(PanelFX panel) {
		return new DrawerGraphics(panel);
	}
	
	class DrawerGraphics extends DrawerScene {
		Label label_group 	= new Label(group + " - " + run_id);
		Label label_trial 	= new Label();
		Label label_episode	= new Label();
		Label label_cycle	= new Label();
		Label label_fps	 	= new Label("0");
		Label label_sim_speed = new Label("0");
		Label[] all_labels = new Label[] {label_group, label_trial, label_episode, label_cycle, label_sim_speed, label_fps};
		
		GridPane pane = new GridPane();
		AnchorPane anchor_pane = new AnchorPane(pane);
		
		public DrawerGraphics(PanelFX panel) {
			super(panel);	
			root = anchor_pane; // replace default root pane
			pane.setAlignment(Pos.TOP_LEFT);			
			
			if(x<0) AnchorPane.setLeftAnchor(pane, -x); 
			else AnchorPane.setRightAnchor(pane, x);
			
			if(y<0) AnchorPane.setBottomAnchor(pane, -y); 
			else AnchorPane.setTopAnchor(pane, y);
			
			// Create label table:
			var titles = new String[] {"GroupId", "Trial", "Episode", "Cycle", "Sim Speed", "FPS"};
			for( int i=0; i < titles.length; i++ ) {
				var l = new Label(titles[i]);
				l.setTextFill(color);
				all_labels[i].setTextFill(color);
				l.setFont(new Font(size));
				all_labels[i].setFont(new Font(size));
				
				pane.add(l, 0, i);
				pane.add(all_labels[i], 1, i);
			}
			pane.setVgap(0);
			pane.setHgap(10);
			pane.setPrefWidth(150);
			
			if(fps_calculator == null) {
				fps_calculator = new AnimationTimer() {
					final int frames = 30;
					int oldest_id = 0;
					long times[] = new long[frames];
					
					@Override
					public void handle(long now) {
						// find delta time, then replace and advance olders
						long delta_nano = now - times[oldest_id];
						times[oldest_id] = now;
						oldest_id = (oldest_id + 1) % frames;
						
						fps = frames * 1e9d / delta_nano;
					}
				};
				
				fps_calculator.start();
			}
			
		}
		
		
		
		@Override
		public void update() {			
			
			label_trial.setText(trial);
			label_episode.setText(episode);
			label_cycle.setText(cycle);
			label_sim_speed.setText(String.format("%.2f", cycles_per_second));
			label_fps.setText(String.format("%.2f", fps));
			
		}
		
		public void updateWindow() {
			
		}
	}
	
}
