package edu.usf.experiment.model;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.usf.ratsim.nsl.modules.pathplanning.Edge;
import edu.usf.ratsim.nsl.modules.pathplanning.PointNode;

public interface GraphModel {

	UndirectedGraph<PointNode, Edge> getGraph();

}
