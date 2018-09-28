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
import java.awt.geom.Rectangle2D.Float;

public class PolarDataDrawer extends Drawer {
	

	Float localCoordinates = new Float(-1,-1,2,2);
	
	float relativeRadius;
	float x;
	float y;
	
	float[] data;
	float[] dataCopy;
	
	Coordinate[] relativeVectors;
	
	int[][] function;
	
	String title;
	
	boolean normalize;

	private Coordinate relativeCenter;
	private Coordinate relativeUpperLeft;
	
	public boolean debug = false;
	
	
	
	//Data for highlighting a directions:
	int direc = -1;	
	int nextDirec = -1;
	
	Color arrowColor = Color.red;
	int arrowHeadSize = 8;
	int arrowLineWidth =2;
	
	
	
	
	
	

	/**
	 * Draws a polar graph
	 * @param x		 Center of circle x coord	
	 * @param y		 Center of circle y coord
	 * @param radius 
	 * @param data
	 */
	
	public PolarDataDrawer(String title,float x,float y,float radius,float[] data) {
		this(title,x,y,radius,data,true);
	}
	
	public PolarDataDrawer(String title,float[] data) {
		this(title,0f,0f,0.95f,data,true);
	}
	
	public PolarDataDrawer(String title,float x,float y,float radius,float[] data,boolean normalize){

		relativeCenter = new Coordinate(x,y);
		this.relativeRadius = radius;
		
		this.data = data;
		this.dataCopy = new float[data.length];
		for(int i =0;i <dataCopy.length;i++) dataCopy[i]=1;
		this.title = title;
		this.normalize = normalize;
		
		relativeUpperLeft = new Coordinate(x-radius,y+radius);
		
		
		float deltaAngle = 2*(float)Math.PI/data.length;
		relativeVectors = new Coordinate[data.length];
		function = new int[2][data.length];
		
		float angle = 0;
		for (int i=0;i<data.length;i++){
			
			relativeVectors[i] = new Coordinate(radius*(float)Math.cos(angle),radius*(float)Math.sin(angle));			
			angle+=deltaAngle;
		}
		

	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();		
		Scaler s = new Scaler(localCoordinates,panelCoordinates,true);
		Point center = s.scale(relativeCenter);
		
		Point upperLeft = s.scale(relativeUpperLeft);
		float radius = s.scaleDistanceX(relativeRadius);
		Point vectors[] = s.scaleDistance(relativeVectors,true);
		
		g.setColor(Color.BLACK);
		g.drawOval(upperLeft.x, upperLeft.y, (int)(2*radius), (int)(2*radius));
		
		FontMetrics metrics =  g.getFontMetrics();
		
		
		
		float max = java.lang.Float.NEGATIVE_INFINITY;
		if (normalize){
			for (float f : dataCopy) max = (float)Math.max(max, f);
			if (max==0) max=1;

			for (int i=0;i<data.length;i++){
				function[0][i] = (int)(center.x + (dataCopy[i]/max)*vectors[i].x);
				function[1][i] = (int)(center.y + (dataCopy[i]/max)*vectors[i].y);
			}
			
		}else{
			for (int i=0;i<data.length;i++){
				function[0][i] = (int)(center.x + (dataCopy[i])*vectors[i].x);
				function[1][i] = (int)(center.y + (dataCopy[i])*vectors[i].y);
			}
		}
		g.drawString(title+ "-"+max, (int)(center.x-metrics.stringWidth(title)/2), (int)(Math.min(center.y+radius+metrics.getHeight(),panelCoordinates.height)));
		g.setColor(Color.orange);
		
		g.drawPolygon(function[0], function[1], data.length);
		
//		g.setColor(Color.red);
//		g.drawLine((int)x, (int)y, (int)x, (int)y);
		
		
		
		if(direc!=-1){
			
			GuiUtils.drawArrow(center.x, center.y, function[0][direc], function[1][direc], arrowColor, arrowHeadSize, arrowLineWidth, g);
		}
		
		
		
	}
	
	@Override
	public void endEpisode() {
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		for(int i=0;i<dataCopy.length;i++) dataCopy[i] = data[i];
		if(debug) System.out.println("Updating data...");
		direc = nextDirec;
				

		
	}
	
	
	public void setArrowParams(Color c,int lineWidth,int arrowHeadSize){
		this.arrowColor = c;
		this.arrowLineWidth = lineWidth;
		this.arrowHeadSize = arrowHeadSize;
	}
	
	public void setArrowDirection(int direc){
		nextDirec = direc;
	}

}
