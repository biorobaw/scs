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

	private static final int RADIUS = 6;
	private FeederUniverse u;
	
	ArrayList<Boolean> active = new ArrayList<>();
	ArrayList<Boolean> flashing = new ArrayList<>(); 
	ArrayList<Boolean> enabled  = new ArrayList<>();
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
			g.setColor(active.get(i) ? Color.RED : enabled.get(i) ? Color.BLUE : color);
			int radius = flashing.get(i) ? 2*RADIUS : RADIUS;
			g.fillOval(pos.x - radius, pos.y - radius,2*radius,2*radius);
		}

		
	}

	@Override
	public void endEpisode() {
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		feederPos.clear();
		active.clear();
		flashing.clear();
		for (Feeder f : u.getFeeders()) {
			feederPos.add(f.getPosition());
			active.add(f.isActive());
			flashing.add(f.isFlashing());
			enabled.add(f.isEnabled());
		}
	}

	public void setColor(Color c){
		color = c;
	}

}
