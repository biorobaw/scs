package edu.usf.experiment.display.drawer;

import java.awt.Point;
import java.awt.geom.Rectangle2D.Float;
import java.util.List;

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
	private Float worldCoordinates = new Float(-1, -1, 2, 2);
	
	/**
	 * Panel coordinates
	 */
	private Float panelCoordinates = null;
	
	/**
	 * Panel coordinates
	 */
	
	/**
	 * The x scaling factor
	 */
	public float xscale;
	/**
	 * The y scaling factor
	 */
	public float yscale;
	/**
	 * The offset for the x coordinate. Expressed in universe coordinates.
	 */
	private float xoffset;
	/**
	 * The offset for the y coordinate. Expressed in universe coordinates.
	 */
	private float yoffset;

	public Scaler(Float worldCoords, Float panelCoords,boolean keepAspectRatio) {
		this.worldCoordinates = worldCoords;
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
	public Point scale(Coordinate p) {
		Point pScaled = new Point();
		pScaled.x = (int)panelCoordinates.x+(int)Math.round( ((p.x + xoffset) * xscale));
		pScaled.y = (int)(panelCoordinates.y-Math.round( ((p.y + yoffset) * yscale)));
		return pScaled;
	}
	
	public int[][] scale(Coordinate p[]) {
		int results[][] = new int[2][p.length];
		for(int i=0;i<p.length;i++) {
			Point point = scale(p[i]);
			results[0][i] = point.x;
			results[1][i] = point.y;
		}
		return results;
	}
	
	public int[][][] scale(Coordinate p[][],int rows,int cols) {
		if(p.length == 0) return null;
		int results[][][] = new int[2][rows][cols];
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++){
				Point point = scale(p[i][j]);
				results[0][i][j] = point.x;
				results[1][i][j] = point.y;
			}
		return results;
	}
	
	public int[][] scale(List<Coordinate> positions){
		int results[][] = new int[2][positions.size()];
		int i=0;
		for(Coordinate c : positions) {
			Point point = scale(c);
			results[0][i] = point.x;
			results[1][i] = point.y;
			i++;
		}
		return results;
	}
	

	
	public int scaleDistanceX(float dx) {
		return Math.round(xscale*dx);
	}
	public float scaleDistanceY(float dy,boolean signed) {
		
		return signed? -yscale*dy : yscale*dy;
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
