package edu.usf.experiment.display.drawer;

import java.awt.Point;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A class to transform universe coordinates to component coordinates.
 * 
 * @author martin
 *
 */
public class Scaler {

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

	public Scaler(float xscale, float yscale, float xoffset, float yoffset) {
		this.xscale = xscale;
		this.yscale = yscale;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
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
		pScaled.x = (int) ((p.x + xoffset) * xscale);
		pScaled.y = -(int) ((p.y + yoffset) * yscale);
		return pScaled;
	}

	public Point scale(Point3f pos) {
		return scale(new Coordinate(pos.x, pos.y));
	}

}
