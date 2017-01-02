package edu.usf.ratsim.nsl.modules.pathplanning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.vecmath.Point3f;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.support.SonarUtils;

public class ExperienceRoadMap extends Module {

	private static final float MIN_ACTIVATION = 2f;

	private static final float MAX_SINGLE_ACTIVATION = .8f;

	private UndirectedGraph<PointNode, Edge> g;

	private BasicVisualizationServer<PointNode, Edge> vv;

	private JFrame frame;

	private Thread repainter;

	private boolean continueRepainting;

	public ExperienceRoadMap(String name) {
		super(name);

		frame = null;
		repainter = null;
	}

	@Override
	public void run() {
		Float1dPort sonarReadings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort sonarAngles = (Float1dPort) getInPort("sonarAngles");
		Point3fPort rPos = (Point3fPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");

		List<PointNode> active = new LinkedList<PointNode>();

		// Get the total activation of all nodes
		float totalActivation = 0;
		float maxActivation = Float.NEGATIVE_INFINITY;
		for (PointNode n : g.getVertices()) {
			n.updateActivation(rPos.get(), rOrient.get(), sonarReadings, sonarAngles);
			
			float activation = n.getActivation();
			totalActivation += activation;
			if (activation > 0)
				active.add(n);
			
			if (activation > maxActivation){
				maxActivation = activation;
			}
		}

		// If not enough activation
		if (totalActivation < MIN_ACTIVATION && maxActivation < MAX_SINGLE_ACTIVATION) {
			System.out.println("Creating a node");
			// Create new node
			PointNode nv = new PointNode(rPos.get());
			g.addVertex(nv);
			// Add to the active set
			nv.updateActivation(rPos.get(), rOrient.get(), sonarReadings, sonarAngles);
			active.add(nv);
		}

		// Connect all the active ones
		for (int i = 0; i < active.size() - 1; i++)
			for (int j = i + 1; j < active.size(); j++) {
				PointNode n1 = active.get(i);
				PointNode n2 = active.get(j);
				if (!g.isNeighbor(n1, n2))
					g.addEdge(new Edge(n1.prefLoc.distance(n2.prefLoc)), n1, n2);
			}

//		System.out.println(g.toString());
//		vv.repaint();
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void newTrial() {
		
		g = new UndirectedSparseGraph<PointNode, Edge>();

		Layout<PointNode, Edge> layout = new VertextPosLayout<Edge>(g);
		layout.setSize(new Dimension(600, 600));
		vv = new BasicVisualizationServer<PointNode, Edge>(layout);
		vv.setPreferredSize(new Dimension(650, 650));
		vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<PointNode, Paint>(){
			public Paint transform(PointNode pn) {
				return new Color(pn.activation,0, 1 - pn.activation, 1);
			}
			
		});

		frame = new JFrame("Topological map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
		
		if (repainter != null){
			continueRepainting = false;
			try {
				repainter.join(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		continueRepainting = true;
		repainter = new Thread(new Runnable() {
			@Override
			public void run() {
				while(continueRepainting) {
					vv.repaint();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		repainter.start();
	}

}

class PointNode {
	
	// TODO: make these parameters
	private static final float MAX_RADIUS = .5f;

	public Point3f prefLoc;
	public float activation;

	public PointNode(Point3f prefLoc) {
		this.prefLoc = prefLoc;
	}

	/**
	 * Updates the activation value of the node
	 * @param rPos the position of the robot 
	 * @param sonarReadings the sonar sensor readings
	 * @param sonarAngles the angles of the sonar sensors in the robot frame of reference
	 * @param orientation 
	 */
	public void updateActivation(Point3f rPos, float orientation, Float1dPort sonarReadings, Float1dPort sonarAngles) {
		float angle = -GeomUtils.angleToPointWithOrientation(orientation, rPos, prefLoc);
		
		// No good sensor for the angle, or obstacle closer than the unit's center
		float dist = prefLoc.distance(rPos);
		if (!SonarUtils.validSonar(angle, sonarReadings, sonarAngles)
				|| SonarUtils.getReading(angle, sonarReadings, sonarAngles) < dist)
			activation = 0;
		else {
			if (dist > MAX_RADIUS)
				activation = 0;
			else
				activation = (float) Math.exp(-Math.pow(dist, 2));
		}
		
		
	}

	public void updateActivation(Point3f loc, float distToObs){
		
	}
	
	public float getActivation() {
		return activation;
	}

	public String toString() {
		return "V" + prefLoc.toString();
	}

}

class Edge {
	public float weight;

	public Edge(float weight) {
		this.weight = weight;
	}

}

class VertextPosLayout<E> extends AbstractLayout<PointNode, E> {

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
		return new Point2D.Double(pn.prefLoc.x * 100 + 300, -pn.prefLoc.y * 100 + 300);
	}

}
