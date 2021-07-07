package com.github.biorobaw.scs.simulation.object.maze_elements.walls;

import java.text.DecimalFormat;

import com.github.biorobaw.scs.utils.files.XML;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

public class Wall extends AbstractWall{

	public float x1, x2, y1, y2, width, height; //note length = ||(x1,y1)-(x2,y2)||
	public float normal_x, normal_y, normal_tita;
	public float dir_x, dir_y, dir_normal;
	public float length;
	public float signed_distance;
	
	public final static DecimalFormat format = new DecimalFormat("0.###");
	
	public Wall(float x1, float y1, float x2, float y2,float width, float height) {
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;
		this.width = width;
		this.height = height;
		
		dir_x = x2-x1;
		dir_y = y2-y1;
		dir_normal =(float)Math.atan2(dir_y, dir_x);
		
		length = (float)Math.sqrt(dir_x*dir_x + dir_y*dir_y);
		dir_x/=length;
		dir_y/=length;
		
		normal_x = -dir_y;
		normal_y = dir_x;
		normal_tita = (float)Math.atan2(normal_y, normal_x);
		
		signed_distance = normal_x*x1  + normal_y*y1;
		
		
		
	}
	
	public Wall(XML xml) {
		this(xml.getFloatAttribute("x1"),
			xml.getFloatAttribute("y1"),
			xml.getFloatAttribute("x2"),
			xml.getFloatAttribute("y2"),
			xml.hasChild("w") ? xml.getFloatAttribute("w") : 0.1f,  //defaults to 10cm
			xml.hasChild("h") ? xml.getFloatAttribute("h") : 2f //defaults to 2m
		);
	}
	
	public Wall(float x1, float y1, float x2, float y2) {
		this(x1,y1,x2,y2,0.1f,2f);
	}
	
	public Wall(Wall w) {
		this(w.x1, w.y1, w.x2, w.y2, w.width, w.height);
	}
	
	@Override
	public String toString() {
		return "id: " + get_guid() + " - x1,x2,y1,y2: " 
				+ format.format(x1) + "," + format.format(y1) + ","
				+ format.format(x2) + "," + format.format(y2);
	}

	@Override
	public boolean intersectsSegment(Vector3D pos1, Vector3D pos2) {

		var factory = new GeometryFactory();
		var wall_line = factory.createLineString(new Coordinate[] {
				new Coordinate(x1, y1),
				new Coordinate(x2, y2)
		});

		var segment = factory.createLineString(new Coordinate[] {
				new Coordinate(pos1.getX(), pos1.getY()),
				new Coordinate(pos2.getX(), pos2.getY())
		});

		return segment.intersects(wall_line);

	}
}
