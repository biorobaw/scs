package edu.usf.experiment.display.drawer.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;

public class DiscretePlatformDrawer extends Drawer {

	private PlatformUniverse u;
	
	private ArrayList<Coordinate> coordinates = new ArrayList<>();
	private ArrayList<Color> colors = new ArrayList<>();

	public DiscretePlatformDrawer(PlatformUniverse pu) {
		u = pu;
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		
		for (int i=0;i<coordinates.size();i++) {
			Coordinate pos = coordinates.get(i);
			g.setColor(colors.get(i));
			Point ul = s.scale(new Coordinate(pos.x, pos.y + 1));
			Point lr = s.scale(new Coordinate(pos.x + 1, pos.y));

			g.fillRect(ul.x, ul.y, Math.abs(lr.x - ul.x), Math.abs(ul.y - lr.y));
		}

	}

	@Override
	public void clearState() {

	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		coordinates.clear();
		colors.clear();
		for (Platform p : u.getPlatforms()){
			coordinates.add(p.getPosition());
			colors.add(p.getColor());
		}
		
	}

}
