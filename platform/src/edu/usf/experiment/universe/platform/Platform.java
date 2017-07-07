package edu.usf.experiment.universe.platform;

import java.awt.Color;

import com.vividsolutions.jts.geom.Coordinate;

public class Platform {

	private Coordinate position;
	private float radius;
	private Color color;

	public Platform(Coordinate position, float radius, Color color) {
		this.position = position;
		this.radius = radius;
		this.color = color;
	}
	
	public Platform(Platform platform) {
		this.position = platform.position;
		this.radius = platform.radius;
	}

	public Coordinate getPosition() {
		return position;
	}

	public void setPosition(Coordinate relFPos) {
		this.position = new Coordinate(relFPos);
	}
	
	public float getRadius(){
		return radius;
	}

	public Color getColor() {
		return color;
	}
}
