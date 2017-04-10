package edu.usf.ratsim.nsl.modules.pathplanning;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;

class VertextPosLayout<E> extends AbstractLayout<PointNode, E> {

	private static final float DISPLAY_SCALE = 75;
	private static final float DISPLAY_OFFSET = 200f;

	protected VertextPosLayout(Graph<PointNode, E> graph) {
		super(graph);
	}

	@Override
	public void initialize() {

	}

	@Override
	public void reset() {

	}

	@Override
	public Point2D transform(PointNode pn) {
		return new Point2D.Double(pn.prefLoc.x * DISPLAY_SCALE + DISPLAY_OFFSET, -pn.prefLoc.y * DISPLAY_SCALE + DISPLAY_OFFSET);
	}

}