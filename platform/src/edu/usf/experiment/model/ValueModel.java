package edu.usf.experiment.model;

import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

public interface ValueModel {

	Map<Coordinate, Float> getValuePoints();

	float getValueEntropy();

}
