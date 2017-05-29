package edu.usf.experiment.display.drawer.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;

public class DiscretePlatformDrawer implements Drawer {
	
	private PlatformUniverse u;

	public DiscretePlatformDrawer(PlatformUniverse pu){
		u = pu;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		for (Platform p : u.getPlatforms()){
			Point3f pos = p.getPosition();
			g.setColor(Color.ORANGE);
			Point ul = s.scale(new Coordinate(pos.x, pos.y + 1));
			Point lr = s.scale(new Coordinate(pos.x +1, pos.y));
			
			g.fillRect(ul.x, ul.y, Math.abs(lr.x - ul.x),  Math.abs(ul.y - lr.y));
		}

		
	}

}
