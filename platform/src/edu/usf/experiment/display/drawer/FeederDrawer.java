package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverse;

public class FeederDrawer extends Drawer {

	private static final float RADIUS = 0.02f;
	private FeederUniverse u;
	
	ArrayList<Coordinate> feederPos = new ArrayList<>();
	
	public FeederDrawer(FeederUniverse fu){
		u = fu;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		if(!doDraw) return;
		
		for (int i=0; i<feederPos.size();i++){
			Point pos = s.scale(feederPos.get(i));
			g.setColor(Color.GRAY);
			g.fillOval(pos.x - (int) (RADIUS * s.xscale), pos.y - (int) (RADIUS * s.yscale),
					(int) (RADIUS * s.xscale * 2), (int) (RADIUS * s.yscale * 2));
		}

		
	}

	@Override
	public void clearState() {
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		feederPos.clear();
		for (Feeder f : u.getFeeders())
			feederPos.add(f.getPosition());
	}



}
