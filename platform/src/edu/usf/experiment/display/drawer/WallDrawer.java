package edu.usf.experiment.display.drawer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.LinkedList;

import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.wall.WallUniverse;

public class WallDrawer extends Drawer {
	
	private WallUniverse u;
	private LinkedList<Wall> wallCopies = new LinkedList<>();
	
	final static int DEFAULT_WALL_THICKNESS = 2;
	int wallThickness;

	public WallDrawer(WallUniverse wu){
		this(wu, DEFAULT_WALL_THICKNESS);
	}

	public WallDrawer(WallUniverse u, int wallThickness) {
		this.u = u;
		this.wallThickness = wallThickness;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		if(!doDraw) return;
		g.setColor(Color.BLACK);
		Graphics2D g2d = (Graphics2D)g;
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(wallThickness));
		
		for (Wall w : wallCopies){
			Point p0 = s.scale(w.s.p0);
			Point p1 = s.scale(w.s.p1);
			g.drawLine(p0.x, p0.y, p1.x, p1.y);
		}
		//System.out.println("Walls y : " + my + " " + My + " " + (My-my));
		g2d.setStroke(oldStroke);
	}
	
	@Override
	public void clearState() {
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		wallCopies.clear();
		for (Wall w : u.getWalls()){
			wallCopies.add(new Wall((float)w.s.p0.x,
									(float)w.s.p0.y,
									(float)w.s.p1.x,
									(float)w.s.p1.y));
		}
		
	}

}
