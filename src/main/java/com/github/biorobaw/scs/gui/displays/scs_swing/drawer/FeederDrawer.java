package com.github.biorobaw.scs.gui.displays.scs_swing.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.displays.scs_swing.DrawerSwing;
import com.github.biorobaw.scs.gui.utils.Scaler;
import com.github.biorobaw.scs.gui.utils.Window;
import com.github.biorobaw.scs.maze.Maze;

public class FeederDrawer extends DrawerSwing {

	Maze maze;
	
	ArrayList<Boolean> hasFood = new ArrayList<>();
	ArrayList<float[]> feederPos = new ArrayList<>();
	float feeding_radius;
	
	static Color hasFoodColor = Color.GREEN;
	static Color noFoodColor  = Color.GRAY;
	static final int CENTER_RADIUS = 3;
	
	public FeederDrawer(float feeding_radius) {
		maze = Experiment.get().maze;
		this.feeding_radius = feeding_radius;
	}

	@Override
	public void draw(Graphics g, Window panelCoordinates) {
		if(!doDraw) return;
		
		Scaler s = new Scaler(worldCoordinates, panelCoordinates, true);
		int r = (int)s.scaleDistanceX(feeding_radius);
		
		for (int i=0; i<feederPos.size();i++){
			var pos = s.scale(feederPos.get(i));
			g.setColor(hasFood.get(i) ? hasFoodColor : noFoodColor );
			g.drawOval(pos[0] - r, pos[1] - r,2*r,2*r);
			g.fillOval(pos[0] - CENTER_RADIUS, pos[1] - CENTER_RADIUS,2*CENTER_RADIUS,2*CENTER_RADIUS);
		}

		
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
