package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;

public class PlatformDrawer extends Drawer {
	
	private PlatformUniverse u;
	
	private ArrayList<Coordinate> coordinates = new ArrayList<>();
	private ArrayList<Float> radii = new ArrayList<>();
	private ArrayList<Color> colors = new ArrayList<>();

	public PlatformDrawer(PlatformUniverse pu){
		u = pu;
	}

	
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		if(bu==null) return;
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		for (int i=0;i<coordinates.size();i++){
			Point pos = s.scale(coordinates.get(i));
			float radius = radii.get(i);
//			g.drawOval(pos.x - (int) (radius * s.xscale), pos.y - (int) (radius * s.yscale),
//					(int) (radius * s.xscale * 2), (int) (radius * s.yscale * 2));
			g.setColor(colors.get(i));
			g.fillOval(pos.x - (int) (radius * s.xscale), pos.y - (int) (radius * s.yscale),
					(int) (radius * s.xscale * 2), (int) (radius * s.yscale * 2));
		}

		
	}

	@Override
	public void clearState() {
		
	}


	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		coordinates.clear();
		radii.clear();
		colors.clear();
		for (Platform p : u.getPlatforms()){
			coordinates.add(p.getPosition());
			radii.add(p.getRadius());
			colors.add(p.getColor());
		}
		
	}
}
