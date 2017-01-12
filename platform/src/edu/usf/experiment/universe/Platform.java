package edu.usf.experiment.universe;

import javax.vecmath.Point3f;

public class Platform {

	private Point3f position;
	private float radius;

	public Platform(Point3f position, float radius) {
		this.position = position;
		this.radius = radius;
	}
	
	public Platform(Platform platform) {
		this.position = platform.position;
		this.radius = platform.radius;
	}

	public Point3f getPosition() {
		return position;
	}

	public void setPosition(Point3f relFPos) {
		this.position = new Point3f(relFPos);
	}
}
