package edu.usf.experiment.model;

import java.util.List;

public interface DeactivableModel {

	void deactivateHPCLayersProportion(List<Integer> indexList, float proportion);

	void deactivateHPCLayersRadial(List<Integer> indexList, float constant);

	void reactivateHPCLayers(List<Integer> indexList);

	
}
