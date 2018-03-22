package edu.usf.ratsim.experiment.subject.morrisReplay.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.vecmath.Point3f;

import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

public class GuiUtils {
	
	public static float minX,minY,maxX,maxY;
	
	final public static int borderFix = 6;
	
	public static void setWorldCoordinates(float minX,float minY,float maxX,float maxY){
		GuiUtils.minX = minX;
		GuiUtils.minY = minY;
		GuiUtils.maxX = maxX;
		GuiUtils.maxY = maxY;
	}
	
	public static int[] worldToScreen(Point3f world){
		
		float width = VirtUniverse.getInstance().frame.topViewPanel.getWidth();
		float height = VirtUniverse.getInstance().frame.topViewPanel.getHeight();

		return worldToScreen(world,width,height);
		
	}
	
	
	public static int[] worldToScreen(Point3f world,float width,float height){
		
		int x = (int)((width-2*borderFix)*(world.x-minX)/(maxX-minX));
		int y = (int)((height-2*borderFix)*(maxY-world.y)/(maxY-minY));
		

		return new int[] {x,y};
		
	}
	
	
	public static void drawArrow(int x1, int y1, int x2, int y2,Color color,int arrowSize,int aThickness, Graphics2D graphics){
	      
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
            graphics.translate(x2-spaceX,y2-spaceY );
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

}
