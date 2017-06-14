package edu.usf.ratsim.experiment.universe.virtual.drawingUtilities;

import java.awt.Color;
import java.awt.FontMetrics;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;



public class DrawPath extends DrawingFunction {
	
	
	public List<Point3f> path = new LinkedList<Point3f>();
	
	public float xMax,yMax,xMin,yMin;
	public float xWidth,yWidth;
	public Color pathColor = Color.red;
	
	
	public DrawPath(float xMin,float xMax, float yMin, float yMax){
		
		
		this.xMax = xMax;
		this.yMax = yMax;
		this.xMin = xMin;
		this.yMin = yMin;
		
		
		this.xWidth = xMax - xMin;
		this.yWidth = yMax - yMin;

		

	}
	
	public void setPath(List<Point3f> path){
		this.path = path;
	}
	
	public void setColor(Color c){
		pathColor =c;
	}

	@Override
	public void run() {
		if(!active) return;
		// TODO Auto-generated method stub
		
		
//		graphics.drawOval(boxStartX, boxStartY, boxSize, boxSize);
		
//		graphics.ca
		
		int pathSize = path.size();
		if(pathSize==0) return;
		int[] xPoints = new int[pathSize];
		int[] yPoints = new int[pathSize];
		
		
		
		for(int i=0;i<pathSize;i++){
			
			Point3f p = path.get(i);
			xPoints[i] = (int) ((p.x-xMin) / xWidth * canvas.getWidth());
			yPoints[i] = (int) ((yMax - p.y) / yWidth * canvas.getHeight());
			
		}
		
		
		
		graphics.setColor(pathColor);
		graphics.drawPolyline(xPoints, yPoints, pathSize);
		

		
		graphics.flush(false);
		
		
	}
	
	

}
