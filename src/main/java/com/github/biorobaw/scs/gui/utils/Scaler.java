package com.github.biorobaw.scs.gui.utils;

import java.awt.Point;
import java.util.List;

import com.github.biorobaw.scs.utils.math.Floats;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * A class to transform universe coordinates to component coordinates.
 * 
 * @author martin
 *
 */
public class Scaler {

	
	/**
	 * World cooridnates
	 */
//	private Window<Float> worldCoordinates = new Window<>(-1f, -1f, 2f, 2f);
	
	/**
	 * Panel coordinates
	 */
	private Window<Float> panelCoordinates = null;
	
	/**
	 * Panel coordinates
	 */
	
	/**
	 * The x and y scaling factor
	 */
	public float xscale, yscale;

	/**
	 * The offset for the x and y coordinate. Expressed in universe coordinates.
	 */
	private float xoffset,  yoffset;
	
	/**
	 * The offset for the x and y coordinates expressed in panel coordinates
	 */

	public Scaler(Window<Float> worldCoords, Window<Float> panelCoords,boolean keepAspectRatio) {
//		this.worldCoordinates = worldCoords;
		this.panelCoordinates = panelCoords;
		
		// The scaling factors are the relation between effective draw space and
		// the universe bounding box (taken from the xml file for the maze)
		xscale =  (float)panelCoords.width  / worldCoords.width;
		yscale =  (float)panelCoords.height / worldCoords.height;
		
		if(keepAspectRatio) {
			// Take the minimum of both scales to keep aspect ratio
			xscale = yscale = Math.min(xscale, yscale);
		}
		
		
		// The x and y offset centers the image by following the next equations:
		// scale * (world_Xmidpoint + offset) = panel_Xmidpoint
		//-scale * (world_Ymidpoint + offset) = panel_Ymidpoint
		xoffset = 0.5f*( (float)panelCoords.width /xscale - 2*worldCoords.x - worldCoords.width  );
		yoffset = -0.5f*( (float)panelCoords.height /yscale + 2*worldCoords.y + worldCoords.height  );
				
		xoffset = panelCoordinates.x + xoffset*xscale;
		yoffset = panelCoordinates.y - yoffset*yscale;

	}
	
	public Scaler(float[] worldCoords, Window<Float> panelCoords,boolean keepAspectRatio) {
		this(new Window<Float>(worldCoords[0],worldCoords[1],worldCoords[2],worldCoords[3])
				,panelCoords,keepAspectRatio);
	}

	/**
	 * Transforms the universe coordinates in p to component (e.g. JPanel)
	 * coordinates. The coordinates are offset to account for universe with
	 * lower coordinates different that (0,0) (e.g. centered at 0, or far from
	 * the origin). Then, they are scaled by a scaling factor. Most cases leave
	 * the x and y scaling factor the same to keep the aspect ratio.
	 * 
	 * @param p The point in universe coordinates
	 * @return The point in component (pixel) coordinates.
	 */
	public int[] scale(float[] p) {
		var res = new int[2];
		res[0] = Math.round( xoffset + p[0]* xscale );
		res[1] = Math.round( yoffset - p[1]* yscale );
		return res;
	}
	
	public float[] scaleX(float[] x) {
		var res = Floats.mul(x, xscale);
		return Floats.add(res,xoffset,res);
	}
	
	public float[] scaleY(float[] y) {
		var res = Floats.mul(y, -yscale);
		return Floats.add(res,yoffset,res);
	}
	
	
	public float[] scaleDistanceX(float[] d) {
		return Floats.mul(d, xscale);
	}
	
	public float[] scaleDistanceY(float[] d) {
		return Floats.mul(d, yscale);
	}
	
	public float scaleDistanceX(float dx) {
		return xscale*dx;
	}
	public float scaleDistanceY(float dy) {
		
		return yscale*dy;
	}
	
	public int[][] scale(float p[][]) {
		int results[][] = new int[2][p.length];
		for(int i=0;i<p.length;i++) {
			var point = scale(p[i]);
			results[0][i] = point[0];
			results[1][i] = point[1];
		}
		return results;
	}
	
	public int[][][] scale(float p[][][],int rows,int cols) {
		if(p.length == 0) return null;
		int results[][][] = new int[2][rows][cols];
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++){
				var point = scale(p[i][j]);
				results[0][i][j] = point[0];
				results[1][i][j] = point[1];
			}
		return results;
	}
	
	public int[][] scale(List<float[]> positions){
		int results[][] = new int[2][positions.size()];
		int i=0;
		for(var c : positions) {
			var point = scale(c);
			results[0][i] = point[0];
			results[1][i] = point[1];
			i++;
		}
		return results;
	}
	

	
	
	public Point scaleDistance(Coordinate p,boolean signed) {
		return new Point((int)(p.x*xscale),(int)(signed ?  -p.y*yscale : p.y*yscale));
	}
	public Point[] scaleDistance(Coordinate p[],boolean signed) {
		Point results[] = new Point[p.length];
		for(int i=0;i<p.length;i++) results[i] = scaleDistance(p[i],signed);
		return results;
	}

}
