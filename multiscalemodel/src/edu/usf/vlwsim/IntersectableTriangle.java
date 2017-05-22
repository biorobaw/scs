package edu.usf.vlwsim;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Triangle;

public class IntersectableTriangle extends Triangle {

	private LineSegment side1;
	private LineSegment side2;
	private LineSegment side3;

	public IntersectableTriangle(Coordinate p0, Coordinate p1, Coordinate p2) {
		super(p0, p1, p2);
		
		side1 = new LineSegment(p0, p1);
		side2 = new LineSegment(p1, p2);
		side3 = new LineSegment(p2, p0);
	}

	/**
	 * Returns whether the segment s instersects or is inside the triangle 
	 * @param s the line segment to test for intersection
	 * @return Whether the segment s instersects or is inside the triangle 
	 */
	public boolean intersects(LineSegment s)
	{
		// Check for explicit intersection with the sides
		if (s.intersection(side1) != null)
			return true;
		if (s.intersection(side2) != null)
			return true;
		if (s.intersection(side3) != null)
			return true;
		
		// Check whether any endpoint is interior to the to the triangle
		return isInterior(s.p0) || isInterior(s.p1);
	}

	/**
	 * Checks whether the point p1 is interior to the triangle
	 * @param p1 The point to check for interior-ness
	 * @return Whether the point p1 is interior to the triangle
	 */
	public boolean isInterior(Coordinate p1) {
		// Check for the intersection of each side with segment formed by 
		// the remaining vertex and the point to check
		boolean intSide1 = side1.intersection(new LineSegment(side2.p1, p1)) != null;
		boolean intSide2 = side2.intersection(new LineSegment(side3.p1, p1)) != null;
		boolean intSide3 = side3.intersection(new LineSegment(side1.p1, p1)) != null;
		
		// If none of the segments intersect, then it is interior
		return !intSide1 && !intSide2 && !intSide3;
	}
}
