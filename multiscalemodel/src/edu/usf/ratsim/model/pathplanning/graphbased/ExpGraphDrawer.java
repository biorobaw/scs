package edu.usf.ratsim.model.pathplanning.graphbased;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	private float maxDistToPaint;

	public ExpGraphDrawer(ExperienceRoadMap erm, float maxDistToPaint) {
		this.erm = erm;
		this.maxDistToPaint = maxDistToPaint;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		UndirectedGraph<PointNode, Edge> graph = erm.getGraph();
		
		if (graph != null){
			synchronized (graph) {
				for (Edge e : graph.getEdges()){
					g.setColor(Color.GRAY);
					Point dst = s.scale(graph.getEndpoints(e).getFirst().prefLoc);
					Point src = s.scale(graph.getEndpoints(e).getSecond().prefLoc);
					
					g.drawLine(src.x, src.y, dst.x, dst.y);
				}
				PointNode followingNode = erm.getFollowingNode();
				Collection<PointNode> vertices = new LinkedList<PointNode>(graph.getVertices());
				for (PointNode pn : vertices){
					Point pos = s.scale(new Coordinate(pn.prefLoc.x, pn.prefLoc.y));
					if (pn == followingNode)
						g.setColor(Color.GREEN);
					else if (pn.distToRobot > maxDistToPaint)
						g.setColor(Color.BLUE);
					else
						g.setColor(Color.RED);
					g.fillOval(pos.x - (int) (NODE_R * s.xscale), pos.y - (int) (NODE_R * s.yscale),
							(int) (NODE_R * s.xscale * 2), (int) (NODE_R * s.yscale * 2));
				}
			}
			
		}
			
	}
	
	@Override
	public void clearState() {
		
	}

}
