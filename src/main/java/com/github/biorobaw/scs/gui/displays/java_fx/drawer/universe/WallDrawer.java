package com.github.biorobaw.scs.gui.displays.java_fx.drawer.universe;

import java.util.LinkedList;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.displays.java_fx.PanelFX;
import com.github.biorobaw.scs.gui.displays.java_fx.drawer.DrawerFX;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.CylindricalWall;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.Wall;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;


public class WallDrawer extends DrawerFX {
	
	
	LinkedList<DrawerGraphics> all_drawing_graphics = new LinkedList<>();
	private LinkedList<Wall> walls = new LinkedList<>();
	private LinkedList<CylindricalWall> cwalls = new LinkedList<>();
	Color color = Color.BLACK;
	
	float wall_thickness = 0.01f;// wall thickness in meters
	
	public boolean update_each_trial = true;
	public boolean update_each_episode = false;
	public boolean update_graphics = false;

	public WallDrawer(float wallThickness) {
		this.wall_thickness = wallThickness;
	}
	
	@Override
	public void newEpisode() {
		super.newEpisode();
		if(update_each_episode) updateWallSet();
		
		
	}
	
	@Override
	public void newTrial() {
		if(update_each_trial) updateWallSet();
	}
	
	void updateWallSet() {
		
		// update pointers to walls
		var m = Experiment.get().maze;
		for (var w : m.walls){
			if(w instanceof Wall) 
				walls.add(new Wall((Wall)w));
			else if(w instanceof CylindricalWall) 
				cwalls.add(new CylindricalWall((CylindricalWall)w));
		}
		
		// update drawers
		update_graphics = true;
	}
	


	@Override
	public synchronized void updateData() {		
		// walls are not updated during an episode, thus do nothing
	}
	
	public void setColor(Color c){
		color = c;
	}
	
	
	class DrawerGraphics extends DrawerScene {
		
		
		public DrawerGraphics(PanelFX panel) {
			super(panel);
			all_drawing_graphics.add(this);
						
		}
		

		public void update_wall_set() {
			root.getChildren().clear();
			
			var m = Experiment.get().maze;
			for (var w : m.walls){
				if(w instanceof Wall) new WallGraphics((Wall)w);
				else if(w instanceof CylindricalWall) new CircularWallGraphics((CylindricalWall)w);
			}
			
		}
		
		@Override
		public void update() {
			if(update_graphics) {
				for(var g: all_drawing_graphics) 
					g.update_wall_set();
				update_graphics = false;
			}
		}
		
		

		
		class WallGraphics extends Line {
			WallGraphics(Wall w){
				super(w.x1, w.y1, w.x2, w.y2);
				setStroke(color);
				setStrokeWidth(wall_thickness);
				root.getChildren().add(this);
			}
		}
		
		
		class CircularWallGraphics extends Circle{
			CircularWallGraphics(CylindricalWall wall){
				super(wall.x, wall.y, wall.r,color);
				setStrokeWidth(wall_thickness);
				root.getChildren().add(this);
			}
		}
		
	}
	


	@Override
	protected DrawerScene createGraphics(PanelFX panel) {
		return new DrawerGraphics(panel);
	}

}
