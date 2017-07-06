package edu.usf.experiment.display.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;

public class PlatformDrawer implements Drawer {
	
	private PlatformUniverse u;

	public PlatformDrawer(PlatformUniverse pu){
		u = pu;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		for (Platform p : u.getPlatforms()){
			Point pos = s.scale(p.getPosition());
			float radius = p.getRadius();
//			g.drawOval(pos.x - (int) (radius * s.xscale), pos.y - (int) (radius * s.yscale),
//					(int) (radius * s.xscale * 2), (int) (radius * s.yscale * 2));
			g.setColor(p.getColor());
			g.fillOval(pos.x - (int) (radius * s.xscale), pos.y - (int) (radius * s.yscale),
					(int) (radius * s.xscale * 2), (int) (radius * s.yscale * 2));
		}

		
	}

	@Override
	public void clearState() {
		
	}
}
