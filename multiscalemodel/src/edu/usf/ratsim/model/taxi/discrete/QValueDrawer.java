package edu.usf.ratsim.model.taxi.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;

public class QValueDrawer implements Drawer {

	private DiscreteTaxiModel model;

	public QValueDrawer(DiscreteTaxiModel model) {
		this.model = model;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		Map<Point3f, Float> vps = model.getValuePoints();
		
		
		for (Point3f p : vps.keySet())
		{
			Point ul = s.scale(new Coordinate(p.x, p.y + 1));
			Point lr = s.scale(new Coordinate(p.x +1, p.y));
			float val = vps.get(p);
			Color b = new Color(0,0,1,Math.min(1, Math.max(0, val)));
			g.setColor(b);
			
			g.fillRect(ul.x, ul.y, Math.abs(lr.x - ul.x),  Math.abs(ul.y - lr.y));
		}
	}

}
