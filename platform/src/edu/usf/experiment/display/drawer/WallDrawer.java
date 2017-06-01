package edu.usf.experiment.display.drawer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.wall.WallUniverse;

public class WallDrawer implements Drawer {
	
	private WallUniverse u;

	public WallDrawer(WallUniverse wu){
		u = wu;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		g.setColor(Color.BLACK);
		Graphics2D g2d = (Graphics2D)g;
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(2));
		for (Wall w : u.getWalls()){
			Point p0 = s.scale(w.s.p0);
			Point p1 = s.scale(w.s.p1);
			g.drawLine(p0.x, p0.y, p1.x, p1.y);
		}
		g2d.setStroke(oldStroke);
	}

}
