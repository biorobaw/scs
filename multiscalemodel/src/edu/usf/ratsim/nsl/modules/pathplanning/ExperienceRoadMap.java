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
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.experiment.subject.NotImplementedException;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug0Module;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug1Module;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug2Module;
import edu.usf.ratsim.support.SonarUtils;

public class ExperienceRoadMap extends Module {

	private static final float MIN_ACTIVATION = 4f;

	private static final float MAX_SINGLE_ACTIVATION = .9f;

	private static final float DIST_TO_GOAL_THRS = .15f;

	private static final float NEXT_NODE_DIST_THRS = 0.5f;

	private static final int WINDOW_SIZE = 400;

	private UndirectedGraph<PointNode, Edge> g;

	private BasicVisualizationServer<PointNode, Edge> vv;

	private JFrame frame;

	private Thread repainter;

	private boolean continueRepainting;

	private Point3fPort intermediateGoal;

	private Subject subject;

	private Module bug;

	private Bug0Module bug0;

	private String algorithm;

	public ExperienceRoadMap(String name, Subject subject, String algorithm) {
		super(name);

		frame = null;
		repainter = null;
		
		intermediateGoal = new Point3fPort(this);
		addOutPort("intermediateGoal", intermediateGoal);
		
		this.subject = subject;
		this.algorithm = algorithm;
	}

	@Override
	public void run() {
		// Get info
		Float1dPort sonarReadings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort sonarAngles = (Float1dPort) getInPort("sonarAngles");
		Point3fPort rPos = (Point3fPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		Point3fPort platPos = (Point3fPort) getInPort("platformPosition");

		// Compute active nodes
		List<PointNode> active = new LinkedList<PointNode>();
		float totalActivation = 0;
		float maxActivation = Float.NEGATIVE_INFINITY;
		for (PointNode n : g.getVertices()) {
			n.updateActivation(rPos.get(), rOrient.get(), sonarReadings, sonarAngles);

			float activation = n.getActivation();
			totalActivation += activation;
			if (activation > 0)
				active.add(n);

			if (activation > maxActivation) {
				maxActivation = activation;
			}
		}

		// Creation of new nodes
		if ((totalActivation < MIN_ACTIVATION && maxActivation < MAX_SINGLE_ACTIVATION)
				|| (platPos.get().distance(rPos.get()) < DIST_TO_GOAL_THRS && totalActivation < 3 * MIN_ACTIVATION)) {
			System.out.println("Creating a node");
			// Create new node
			PointNode nv = new PointNode(rPos.get());
			g.addVertex(nv);
			// Add to the active set
			nv.updateActivation(rPos.get(), rOrient.get(), sonarReadings, sonarAngles);
			active.add(nv);
		}

		// Connectivity of all active nodes
		for (int i = 0; i < active.size() - 1; i++)
			for (int j = i + 1; j < active.size(); j++) {
				PointNode n1 = active.get(i);
				PointNode n2 = active.get(j);
				if (!g.isNeighbor(n1, n2))
					g.addEdge(new Edge(n1.prefLoc.distance(n2.prefLoc)), n1, n2);
			}

		// Compute dijsktra
		// Get the current node
		PointNode mostActive = active.get(0);
		for (PointNode pn : active)
			if (pn.activation > mostActive.activation)
				mostActive = pn;
		// Get the goal node
		PointNode goalNode = null;
		for (PointNode pn : g.getVertices())
			if (pn.prefLoc.distance(platPos.get()) < DIST_TO_GOAL_THRS)
				if (goalNode == null)
					goalNode = pn;
				else 
					if (goalNode.prefLoc.distance(platPos.get()) > pn.prefLoc.distance(platPos.get()))
						goalNode = pn;
		
		List<Edge> l = new LinkedList<Edge>();
		if (goalNode != null) {
			// Compute the shortest path					
			Transformer<Edge, Float> wtTransformer = new Transformer<Edge, Float>() {
				public Float transform(Edge link) {
					return link.weight;
				}
			};
			DijkstraShortestPath<PointNode, Edge> alg = new DijkstraShortestPath(g, wtTransformer);
			l = alg.getPath(mostActive, goalNode);
			Number dist = alg.getDistance(mostActive, goalNode);
			System.out.println("The shortest path from" + mostActive + " to " + goalNode + " is:");
			System.out.println(l.toString());
			System.out.println("and the length of the path is: " + dist);
		}
		
		// Publish a closer goal if there is a valid path
		if (l.isEmpty()){
			intermediateGoal.set(platPos.get());
			bug.run();
		} else {
			Point3f next = g.getEndpoints(l.get(0)).getSecond().prefLoc;
			if (next.distance(rPos.get()) > NEXT_NODE_DIST_THRS)
				intermediateGoal.set(next);
			else {
				if (l.size() > 1)
					intermediateGoal.set(g.getEndpoints(l.get(1)).getSecond().prefLoc);
				else 
					intermediateGoal.set(platPos.get());
			}
			bug0.run();
		}
			
			
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void newTrial() {

		g = new UndirectedSparseGraph<PointNode, Edge>();

		Layout<PointNode, Edge> layout = new VertextPosLayout<Edge>(g);
		layout.setSize(new Dimension(400, 400));
		vv = new BasicVisualizationServer<PointNode, Edge>(layout);
		vv.setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));
		vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<PointNode, Paint>() {
			public Paint transform(PointNode pn) {
				return new Color(pn.activation, 0, 1 - pn.activation, 1);
			}

		});

		frame = new JFrame("Topological map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);

		if (repainter != null) {
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
				while (continueRepainting) {
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
		
		
		// Create the delegate bug algorithms
		Float1dPort sonarReadings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort sonarAngles = (Float1dPort) getInPort("sonarAngles");
		Point3fPort rPos = (Point3fPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		Point3fPort platPos = (Point3fPort) getInPort("platformPosition");
		
		// algorithm comes from group parameters
		bug = null;
		if (algorithm.equals("bug0"))
			bug = new Bug0Module("Bug0", subject);
		else if (algorithm.equals("bug1"))
			bug = new Bug1Module("Bug1", subject);
		else if (algorithm.equals("bug2"))
			bug = new Bug2Module("Bug2", subject);
		else
			throw new NotImplementedException();
		
		bug.addInPort("sonarReadings", sonarReadings);
		bug.addInPort("sonarAngles", sonarAngles);
		bug.addInPort("position", rPos);
		bug.addInPort("orientation", rOrient);
		bug.addInPort("platformPosition", intermediateGoal);
		
		bug0 = new Bug0Module("ERMBug0", subject);
		bug0.addInPort("sonarReadings", sonarReadings);
		bug0.addInPort("sonarAngles", sonarAngles);
		bug0.addInPort("position", rPos);
		bug0.addInPort("orientation", rOrient);
		bug0.addInPort("platformPosition", intermediateGoal); 
		
		bug.newTrial();
		bug0.newTrial();
	}

	@Override
	public void newEpisode() {
		super.newEpisode();
		
		bug.newEpisode();
		bug0.newEpisode();
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
	 * 
	 * @param rPos
	 *            the position of the robot
	 * @param sonarReadings
	 *            the sonar sensor readings
	 * @param sonarAngles
	 *            the angles of the sonar sensors in the robot frame of
	 *            reference
	 * @param orientation
	 */
	public void updateActivation(Point3f rPos, float orientation, Float1dPort sonarReadings, Float1dPort sonarAngles) {
		float angle = -GeomUtils.angleToPointWithOrientation(orientation, rPos, prefLoc);

		// No good sensor for the angle, or obstacle closer than the unit's
		// center
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

	public void updateActivation(Point3f loc, float distToObs) {

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
