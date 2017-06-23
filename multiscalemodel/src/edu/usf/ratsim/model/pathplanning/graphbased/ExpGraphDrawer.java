package edu.usf.ratsim.model.pathplanning.graphbased;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.ratsim.nsl.modules.pathplanning.Edge;
import edu.usf.ratsim.nsl.modules.pathplanning.ExperienceRoadMap;
import edu.usf.ratsim.nsl.modules.pathplanning.PointNode;

public class ExpGraphDrawer implements Drawer {

	private static final float NODE_R = .05f;
	private ExperienceRoadMap erm;

	public ExpGraphDrawer(ExperienceRoadMap erm) {
		this.erm = erm;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		UndirectedGraph<PointNode, Edge> graph = erm.getGraph();
		
		if (graph != null){
			LinkedList<Edge> edges = new LinkedList<Edge>(graph.getEdges());
			for (Edge e : edges){
				g.setColor(Color.GRAY);
				Point dst = s.scale(graph.getEndpoints(e).getFirst().prefLoc);
				Point src = s.scale(graph.getEndpoints(e).getSecond().prefLoc);
				
				g.drawLine(src.x, src.y, dst.x, dst.y);
			}
			PointNode nextNode = erm.getNextNode();
			for (PointNode pn : graph.getVertices()){
				Point pos = s.scale(new Coordinate(pn.prefLoc.x, pn.prefLoc.y));
				if (pn == nextNode)
					g.setColor(Color.GREEN);
				else if (pn.getActivation() == 0)
					g.setColor(Color.BLUE);
				else
					g.setColor(new Color(1, 1- pn.getActivation(), 1 - pn.getActivation()));
				g.fillOval(pos.x - (int) (NODE_R * s.xscale), pos.y - (int) (NODE_R * s.yscale),
						(int) (NODE_R * s.xscale * 2), (int) (NODE_R * s.yscale * 2));
			}
		}
			
	}

}
