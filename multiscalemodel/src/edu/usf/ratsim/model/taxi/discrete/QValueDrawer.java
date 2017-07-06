package edu.usf.ratsim.model.taxi.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.model.ValueModel;

public class QValueDrawer implements Drawer {

	private ValueModel model;

	public QValueDrawer(ValueModel model) {
		this.model = model;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		Map<Point3f, Float> vps = model.getValuePoints();
		
		float maxPos = -Float.MAX_VALUE;
		float maxNeg = Float.MAX_VALUE;
		for (Float val : vps.values())
			if (val < 0)
				maxNeg = val < maxNeg ? val : maxNeg;
			else 
				maxPos = val > maxPos ? val : maxPos;
		maxNeg = -maxNeg;
		
		maxNeg = maxNeg == 0 ? 1 : maxNeg;
		maxPos = maxPos == 0 ? 1 : maxPos;
		
		for (Point3f p : vps.keySet())
		{
			Point ul = s.scale(new Coordinate(p.x, p.y + 1));
			Point lr = s.scale(new Coordinate(p.x + 1, p.y));
			float val = vps.get(p);
		
			val = val < 0 ? val / maxNeg : val / maxPos;
			float red = val <= 0f ? 1f : 0f;
			float green = 0f;
			float blue = val > 0f ? 1f : 0f;
			float alpha = Math.abs(val);
			Color b = new Color(red, green, blue, alpha);
			g.setColor(b);
			g.fillRect(ul.x, ul.y, Math.abs(lr.x - ul.x),  Math.abs(ul.y - lr.y));
//			System.out.println(p.x + " " + p.y);
		}
	}

	@Override
	public void clearState() {
		
	}
}
