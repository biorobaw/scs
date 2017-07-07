package edu.usf.experiment.universe.wall;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

public class Wall {

	public LineSegment s;
	
	public Wall(float x1, float y1, float x2, float y2){
		s = new LineSegment(new Coordinate(x1, y1), new Coordinate(x2, y2));
	}
	
	public Wall(LineSegment segment) {
		s = segment;
	}

	public float getX1(){
		return (float) s.p0.x;
	}
	
	public float getY1(){
		return (float) s.p0.y;
	}
	
	public float getX2(){
		return (float) s.p1.x;
	}
	
	public float getY2(){
		return (float) s.p1.y;
	}

	public float distanceTo(LineSegment wall) {
		return (float) s.distance(wall);
	}

	public float distanceTo(Coordinate c) {
		return (float) s.distance(c);
	}
	
	public boolean intersects(Polygon c) {
		GeometryFactory gf = new GeometryFactory();
		Coordinate cs[] = new Coordinate[2];
		cs[0] = s.p0;
		cs[1] = s.p1;
		LineString ls = gf.createLineString(cs);
		return ls.crosses(c) || c.contains(ls);
	}
	
	public boolean intersects(LineSegment wall) {
		return s.intersection(wall) != null;
	}

}
