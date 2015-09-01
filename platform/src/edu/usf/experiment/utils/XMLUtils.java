package edu.usf.experiment.utils;

import javax.vecmath.Point4f;

public class XMLUtils {

	public static Point4f parsePoint(ElementWrapper child) {
		float x = child.getChildFloat("x");
		float y = child.getChildFloat("y");
		float theta = child.getChildFloat("theta");
		return new Point4f(x, y, 0, theta);
	}

}
