package edu.usf.experiment.utils;

public class XMLUtils {

	public static RigidTransformation parsePoint(ElementWrapper child) {
		float x = child.getChildFloat("x");
		float y = child.getChildFloat("y");
		float theta = child.getChildFloat("theta");
		return new RigidTransformation(x, y, theta);
	}

}
