package edu.usf.experiment.display.drawer.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.UniverseLoader;

import java.awt.geom.Rectangle2D.Float;

public class CycleDataDrawer extends Drawer {
	
	
	Float localCoordinates = new Float(0,0,1,1);
	
	final static int DEFAULT_WALL_THICKNESS = 2;
	int wallThickness;
	private Coordinate relativePosition;
	private String group;
	private String subName;
	private String trial;
	private String episode;
	private int size;
	private String cycle;
	
	private Color color = Color.BLACK;
	

	public CycleDataDrawer() {
		this(0.8f,1f,16);
	}
	
	
	public CycleDataDrawer(float x, float y,int size){
		relativePosition = new Coordinate(x, y);
		this.size = size;
		this.group   = Globals.getInstance().get("group").toString();
		this.subName = Globals.getInstance().get("subName").toString();
		
		
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		Scaler s = new Scaler(localCoordinates,panelCoordinates,false);
		
		Point upperLeftCorner = s.scale(relativePosition);
		int x = (int)upperLeftCorner.x;
		int y = (int)upperLeftCorner.y;
		
		
		g.setColor(color);
//		graphics.setFont(font);
		g.drawString("RatID:   " + group + " - " + subName , x, y);
		g.drawString("Trial:   " + trial, x , y+(size+2));
		g.drawString("Episode: " + episode, x, y+2*(size+2));
		g.drawString("Cycle:   " + cycle, x, y+3*(size+2));
//		graphics.drawString("Cycle: " + Globals.getInstance().get("cycle").toString(), x, y);
		
		
		
	}
	
	@Override
	public void clearState() {
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		
		trial   = Globals.getInstance().get("trial").toString();
		episode = Globals.getInstance().get("episode").toString();
		cycle   = Globals.getInstance().get("cycle").toString();
				

		
	}

}
