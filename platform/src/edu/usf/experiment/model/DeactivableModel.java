package edu.usf.experiment.model;

import java.util.LinkedList;

public interface DeactivableModel {

	void deactivateHPCLayersProportion(LinkedList<Integer> indexList, float proportion);

	void deactivateHPCLayersRadial(LinkedList<Integer> indexList, float constant);

	void reactivateHPCLayers(LinkedList<Integer> indexList);

	
}
