package com.github.biorobaw.scs.gui.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.utils.Scaler;
import com.github.biorobaw.scs.gui.utils.Window;
import com.github.biorobaw.scs.maze.Maze;

public class FeederDrawer extends Drawer {

	Maze maze;
	
	private static int RADIUS = 6;
	ArrayList<Boolean> hasFood = new ArrayList<>();
	ArrayList<float[]> feederPos = new ArrayList<>();

	static Color hasFoodColor = Color.red;
	static Color noFoodColor  = Color.GRAY;
	
	public FeederDrawer(float xmin) {
		maze = Experiment.get().maze;
	}

	@Override
	public void draw(Graphics g, Window<Float> panelCoordinates) {
		if(!doDraw) return;
		
		Scaler s = new Scaler(worldCoordinates, panelCoordinates, true);
		
		for (int i=0; i<feederPos.size();i++){
			var pos = s.scale(feederPos.get(i));
			g.setColor(hasFood.get(i) ? hasFoodColor : noFoodColor );
			g.fillOval(pos[0] - RADIUS, pos[1] - RADIUS,2*RADIUS,2*RADIUS);
		}

		
	}

	@Override
	public void endEpisode() {
		
	}

	@Override
	public void updateData() {
		feederPos.clear();
		hasFood.clear();
		
		for (var f : maze.feeders.values() ) {
			feederPos.add(new float[] {(float)f.pos.getX(),(float)f.pos.getY()});
			hasFood.add(f.hasFood);
		}
	}

	public void setHasFoodColor(Color c){
		hasFoodColor = c;
	}
	
	public void setNoFoodColor(Color c) {
		noFoodColor=c;
	}

}
