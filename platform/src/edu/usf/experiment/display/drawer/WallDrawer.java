package edu.usf.experiment.display.drawer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.LinkedList;

import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.wall.WallUniverse;

public class WallDrawer extends Drawer {
	
	private WallUniverse u;
	private LinkedList<Wall> wallCopies = new LinkedList<>();
	Color color = Color.BLACK;
	
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
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		if(bu==null) return;
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		
		g.setColor(color);
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
	public void endEpisode() {
		
	}

	@Override
	public synchronized void updateData() {
		// TODO Auto-generated method stub
		wallCopies.clear();
		for (Wall w : u.getWalls()){
			wallCopies.add(new Wall((float)w.s.p0.x,
									(float)w.s.p0.y,
									(float)w.s.p1.x,
									(float)w.s.p1.y));
		}
		
	}
	
	public void setColor(Color c){
		color = c;
	}

}
