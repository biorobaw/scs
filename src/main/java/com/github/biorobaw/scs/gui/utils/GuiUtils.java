package com.github.biorobaw.scs.gui.utils;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Map;



public class GuiUtils {
	
	
	
	
	public static void drawArrow(int x1, int y1, int x2, int y2,Color color,int arrowSize,int aThickness, Graphics g){
		Graphics2D graphics = (Graphics2D)g;
		
	      
        graphics.setColor(color);
        
//        int aThickness =  arrowThickness;
        
        int dx = x2-x1;
        int dy = y2 -y1;
        
        double angle = Math.atan2(dy,dx);
        
        double norm = Math.sqrt(dx*dx + dy*dy);
        
        
        if(norm!=0){
            
            int spaceX = (int)Math.round((arrowSize)*dx/norm);
            int spaceY = (int)Math.round((arrowSize)*dy/norm);
//            double finalX = x2-spaceX;
//            double finalY = y2-spaceY;
            
            graphics.setStroke(new BasicStroke(aThickness));
            //if(norm> 2*arrowSize) // draw line if there is space -- I assume there's always space
            graphics.drawLine(x1,y1,x2-spaceX,y2-spaceY);
            
            
            
            graphics.setStroke(new BasicStroke(1));
            

        
            AffineTransform previous = graphics.getTransform();
            graphics.translate(x2,y2 );
            graphics.rotate(angle);
            
            graphics.translate(-2.0*arrowSize/3,0);
            graphics.translate(2.0*arrowSize/3,0 );

            //int aSize = norm> 2*arrowSize ? arrowSize : (int)norm/2 - pointRadius;
            graphics.fillPolygon(new int[] {0,-arrowSize,-arrowSize}, new int[] {0,-arrowSize/2,arrowSize/2}, 3);


            graphics.setTransform(previous);
        }
        
      
    }
	
	public static Color getHSBAColor(float h, float s, float b,float a){
		
		int rgb = Color.HSBtoRGB(h,s,b);
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		Color color = new Color(red, green, blue, (int)(a*255) & 0xFF);
		return color;
		
	}
	
	public static float findMaxInMap(Map<?, Float> map){
		float max = Float.NEGATIVE_INFINITY;
		for(Float f : map.values())  max = Math.max(max, Math.abs(f));
		return max;
	}
	


}