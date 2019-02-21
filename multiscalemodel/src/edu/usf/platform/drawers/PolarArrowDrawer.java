package edu.usf.platform.drawers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;

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
