package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverse;

public class FeederDrawer extends Drawer {

	private static final int RADIUS = 4;
	private FeederUniverse u;
	
	ArrayList<Coordinate> feederPos = new ArrayList<>();
	
	Color color = GuiUtils.getHSBAColor(0.33f, 0.8f, 0.6f, 1f);
	
	
	public FeederDrawer(FeederUniverse fu){
		u = fu;
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		if(bu==null) return;
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		
		for (int i=0; i<feederPos.size();i++){
			Point pos = s.scale(feederPos.get(i));
			g.setColor(color);
			g.fillOval(pos.x - RADIUS, pos.y - RADIUS,2*RADIUS,2*RADIUS);
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

	public void setColor(Color c){
		color = c;
	}

}
