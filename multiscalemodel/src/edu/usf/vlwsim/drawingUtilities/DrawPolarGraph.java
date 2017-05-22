package edu.usf.vlwsim.drawingUtilities;

import java.awt.Color;
import java.awt.FontMetrics;



public class DrawPolarGraph extends DrawingFunction {
	
	
	float radius;
	float x;
	float y;
	
	int boxStartX;
	int boxStartY;
	int boxSize;
	
	float[] data;
	
	float[][] fixedVectors;
	
	int[][] function;
	
	String title;
	
	boolean normalize;
	boolean active = true; //indicates if its activated, if not, do not draw
	
	
	/**
	 * Draws a polar graph
	 * @param x		 Center of circle x coord	
	 * @param y		 Center of circle y coord
	 * @param radius 
	 * @param data
	 */
	
	public DrawPolarGraph(String title,float x,float y,float radius,float[] data,boolean normalize){
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.data = data;
		this.title = title;
		this.normalize = normalize;
		
		boxSize = (int)(2*radius);
		boxStartX = (int)(x - radius);
		boxStartY = (int)(y - radius);
		
		
		float deltaAngle = 2*(float)Math.PI/data.length;
		fixedVectors = new float[2][data.length];
		function = new int[2][data.length];
		
		float angle = 0;
		for (int i=0;i<data.length;i++){
			
			fixedVectors[0][i] = radius*(float)Math.cos(angle);
			fixedVectors[1][i] = -radius*(float)Math.sin(angle); //y pixel coords are inverted
			
			angle+=deltaAngle;
		}
		

	}

	@Override
	public void run() {
		if(!active) return;
		// TODO Auto-generated method stub
		
		graphics.setColor(Color.white);
		graphics.drawOval(boxStartX, boxStartY, boxSize, boxSize);
		
		FontMetrics metrics =  graphics.getFontMetrics();
		
		
		
		float max = Float.NEGATIVE_INFINITY;
		if (normalize){
			for (float f : data) max = f > max ? f : max;
			if (max==0) max=1;

			for (int i=0;i<data.length;i++){
				function[0][i]= (int)(x + (data[i]/max)*fixedVectors[0][i]);
				function[1][i]= (int)(y + (data[i]/max)*fixedVectors[1][i]);
			}
			
		}else{
			for (int i=0;i<data.length;i++){
				function[0][i]= (int)(x + data[i]*fixedVectors[0][i]);
				function[1][i]= (int)(y + data[i]*fixedVectors[1][i]);
			}
		}
		graphics.drawString(title+ "-"+max, (int)(x-metrics.stringWidth(title)/2), (int)(y+radius+metrics.getHeight()));
		graphics.setColor(Color.orange);
		
		graphics.drawPolygon(function[0], function[1], data.length);
		
		graphics.setColor(Color.red);
		graphics.drawLine((int)x, (int)y, (int)x, (int)y);
		
		graphics.flush(false);
		
		
	}
	
	public void setActive(boolean active){
		this.active = active;
	}

}
