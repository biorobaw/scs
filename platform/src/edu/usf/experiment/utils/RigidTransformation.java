package edu.usf.experiment.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.util.AffineTransformation;

public class RigidTransformation extends AffineTransformation {
	
	public RigidTransformation(){
		super();
	}

	public RigidTransformation(Coordinate c, float angle){
		this((float)c.x, (float)c.y, angle);
	}
	
	public RigidTransformation(float x, float y, float angle){
		super();
		
		translate(x,y);
		composeBefore(AffineTransformation.rotationInstance(angle));
	}

	
	public RigidTransformation(RigidTransformation t){
		super(t);
	}
	
	public RigidTransformation(float angle) {
		this(0f,0f,angle);
	}
	
	public RigidTransformation(float x, float y) {
		this(x,y,0f);
	}

	public Coordinate getTranslation() {
		// Transform a point in 0 to get translation
		Coordinate p = new Coordinate(getMatrixEntries()[2], getMatrixEntries()[5]);
		return p;
	}

	public float getRotation() {
		return (float) Math.atan2(getMatrixEntries()[3], getMatrixEntries()[0]);
	}

}
