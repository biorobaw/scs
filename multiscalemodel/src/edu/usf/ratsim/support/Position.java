package edu.usf.ratsim.support;

import javax.vecmath.Point3f;

public class Position {

	float x;
	float y;
	float orient;

	public Position(float x, float y, float orient) {
		super();
		this.x = x;
		this.y = y;
		this.orient = orient;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getOrient() {
		return orient;
	}

	public void setOrient(float orient) {
		this.orient = orient;
	}

	public Point3f getPoint3f() {
		return new Point3f(x, y, 0);
	}

}
