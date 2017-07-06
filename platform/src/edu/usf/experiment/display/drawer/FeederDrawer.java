package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverse;

public class FeederDrawer implements Drawer {

	private static final float RADIUS = 0.02f;
	private FeederUniverse u;
	
	public FeederDrawer(FeederUniverse fu){
		u = fu;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		for (Feeder f : u.getFeeders()){
			Point pos = s.scale(f.getPosition());
			g.setColor(Color.GRAY);
			g.fillOval(pos.x - (int) (RADIUS * s.xscale), pos.y - (int) (RADIUS * s.yscale),
					(int) (RADIUS * s.xscale * 2), (int) (RADIUS * s.yscale * 2));
		}

		
	}

	@Override
	public void clearState() {
		
	}

}
