package com.github.biorobaw.scs.gui.drawer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.LinkedList;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.utils.Scaler;
import com.github.biorobaw.scs.gui.utils.Window;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.CylindricalWall;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.Wall;


public class WallDrawer extends Drawer {
	
	private LinkedList<Wall> walls = new LinkedList<>();
	private LinkedList<CylindricalWall> cwalls = new LinkedList<>();
	Color color = Color.BLACK;
	
	final static int DEFAULT_WALL_THICKNESS = 2;
	int wallThickness;

	public WallDrawer(int wallThickness) {
		this.wallThickness = wallThickness;
	}

	@Override
	public void draw(Graphics g, Window<Float> panelCoordinates) {
		if(!doDraw) return;
		
		Scaler s = new Scaler(worldCoordinates, panelCoordinates, true);
		
		g.setColor(color);
		Graphics2D g2d = (Graphics2D)g;
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(wallThickness));
		
		for (var w : walls){
			var p0 = s.scale(new float[] {w.x1, w.y1});
			var p1 = s.scale(new float[] {w.x2, w.y2});
			g.drawLine(p0[0], p0[1], p1[0], p1[1]);
		}
		
		for (var w : cwalls){
			var p = s.scale(new float[] {w.x, w.y});
			var r  = s.scaleDistanceX(w.r);
			g.drawOval(p[0]-r, p[1]-r, 2*r, 2*r);
		}
		
		//System.out.println("Walls y : " + my + " " + My + " " + (My-my));
		g2d.setStroke(oldStroke);
	}
	
	@Override
	public void endEpisode() {
		
	}
	
	@Override
	public void newEpisode() {
		super.newEpisode();
		
		walls.clear();
		var m = Experiment.instance.maze;
		for (var w : m.walls){
			if(w instanceof Wall) walls.add(new Wall((Wall)w));
			else if(w instanceof CylindricalWall) 
				cwalls.add(new CylindricalWall((CylindricalWall)w));
		}
		
	}

	@Override
	public synchronized void updateData() {		
		
	}
	
	public void setColor(Color c){
		color = c;
	}

}
