package edu.usf.experiment.universe.platform;

import java.awt.Color;

import javax.vecmath.Point3f;

public class Platform {

	private Point3f position;
	private float radius;
	private Color color;

	public Platform(Point3f position, float radius, Color color) {
		this.position = position;
		this.radius = radius;
		this.color = color;
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
	
	public float getRadius(){
		return radius;
	}

	public Color getColor() {
		return color;
	}
}
