package edu.usf.platform.drawers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.wall.WallUniverse;

public class PolarArrowDrawer extends Drawer {
	

	java.awt.geom.Rectangle2D.Float localCoordinates = new java.awt.geom.Rectangle2D.Float(-1,-1,2,2);
	float relativeLength;
	private Coordinate relativeCenter;
	
	
	Float angle = null;	
	Float nextAngle = null;
	boolean normalize;
	
	Color color = Color.red;
	int arrowHeadSize = 5;
	int lineWidth =2;

	
	
	public boolean debug = false;
	

	/**
	 * Draws a polar graph
	 * @param x		 Center of circle x coord	
	 * @param y		 Center of circle y coord
	 * @param relativeLength 
	 */
	
	public PolarArrowDrawer(float x,float y,float relativeLength) {

		relativeCenter = new Coordinate(x,y);
		this.relativeLength = relativeLength;
		

	}
	
	public PolarArrowDrawer() {
		this(0f,0f,0.95f);
		

	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw || angle == null) return;
		
		Scaler s = new Scaler(localCoordinates,panelCoordinates,true);

		
		Point start = s.scale(relativeCenter);
		float length = s.scaleDistanceX(relativeLength);
		
		int endX = start.x+Math.round(length*(float)Math.cos(angle));
		int endY = start.y+Math.round(-length*(float)Math.sin(angle));
		
		GuiUtils.drawArrow(start.x, start.y, endX, endY, color, arrowHeadSize, lineWidth, g);

		
	}
	
	@Override
	public void endEpisode() {
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		angle = nextAngle;
				

		
	}
	
	public void setParams(Color c,int lineWidth,int arrowHeadSize){
		this.color = c;
		this.lineWidth = lineWidth;
		this.arrowHeadSize = arrowHeadSize;
	}
	
	public void setAngle(float a){
		nextAngle = a;
	}

}
