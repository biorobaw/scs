package com.github.biorobaw.scs.simulation.object.maze_elements.walls;

import com.github.biorobaw.scs.utils.files.XML;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class CylindricalWall extends AbstractWall {

	public float x,y,r; // wall center and radius of inner circle
	public float w,h;   // width and height, note: outter radius = r+w

	public CylindricalWall(float x, float y,float r, float w, float h) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.w = w;
		this.h = h;
	}
	
	public CylindricalWall(float x, float y,float r) {
		this(x,y,r,0.1f,2f);
	}
	
	public CylindricalWall(XML xml) {
		x = xml.getFloatAttribute("x");
		y = xml.getFloatAttribute("y");
		r = xml.getFloatAttribute("r");
		w = xml.hasChild("w") ?  xml.getFloatAttribute("w") : 0.1f;
		h = xml.hasChild("h") ?  xml.getFloatAttribute("h") : 2f;
	}
	
	public CylindricalWall(CylindricalWall cw) {
		x = cw.x;
		y = cw.y;
		r = cw.r;
		w = cw.w;
		h = cw.h;
	}

	@Override
	public boolean intersectsSegment(Vector3D pos1, Vector3D pos2) {
		System.err.println("Method not yet implemented: CyclindricalWall#intersectSegment");
		System.exit(-1);
		return false;
	}
}
