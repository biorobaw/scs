package com.github.biorobaw.scs.gui.drawer;

import java.awt.Color;
import java.awt.Graphics;

import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.utils.Scaler;
import com.github.biorobaw.scs.gui.utils.Window;

public class GridDrawer extends Drawer {
	
	float grid_width, grid_height;

	public GridDrawer(float min_x, float min_y, float max_x, float max_y, float grid_width, float grid_height){
		worldCoordinates = new Window<Float>(min_x, min_y, max_x - min_x, max_y - min_y);
	}

	@Override
	public void draw(Graphics g, Window<Float> panelCoordinates) {
		if(!doDraw) return;
		
		Scaler s = new Scaler(worldCoordinates, panelCoordinates, true);
		
		g.setColor(Color.GRAY);
		for (float x = 0; x <= grid_width; x++){
			var p0 = s.scale(new float[] {x, 0});
			var p1 = s.scale(new float[] {x,grid_height});
			g.drawLine(p0[0], p0[1], p1[0], p1[1]);
		}
		for (float y = 0; y <= grid_height; y++){
			var p0 = s.scale(new float[] {0, y});
			var p1 = s.scale(new float[] {grid_width, y});
			g.drawLine(p0[0], p0[1], p1[0], p1[1]);
		}	
	}
	
	@Override
	public void endEpisode() {
		
	}

	@Override
	public void updateData() {
		
	}
}
