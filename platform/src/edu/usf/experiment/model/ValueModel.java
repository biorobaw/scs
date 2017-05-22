package edu.usf.experiment.model;

import java.util.Map;

import javax.vecmath.Point3f;

public interface ValueModel {

	Map<Point3f, Float> getValuePoints();

	float getValueEntropy();

}
