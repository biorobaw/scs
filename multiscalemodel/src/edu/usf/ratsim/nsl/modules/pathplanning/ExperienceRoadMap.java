package edu.usf.ratsim.nsl.modules.pathplanning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.PointPort;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.nsl.modules.actionselection.apf.APFModule;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug0Module;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug1Module;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Bug2Module;
import edu.usf.ratsim.support.NotImplementedException;

public class ExperienceRoadMap extends Module {

	private static final float MIN_ACTIVATION = 8f;

	private static final float MAX_SINGLE_ACTIVATION = .98f;

	private static final float DIST_TO_GOAL_THRS = .1f;

	private static final float NEXT_NODE_DIST_THRS = 0.15f;

	private static final int WINDOW_SIZE = 800;

	private static final boolean PLOT = false;

	private static final float ACTIVATION_THRS = .0f;

	private static final float MIN_DISTANCE_TO_NEXT_NODE = 0.2f;

	private UndirectedGraph<PointNode, Edge> g;

	private BasicVisualizationServer<PointNode, Edge> vv;

	private JFrame frame;

	private Thread repainter;

	private boolean continueRepainting;

	private PointPort intermediateGoal;

	private Module bug;

	// private Bug0Module bug0;
	private APFModule bug0;

	private String algorithm;

	private Robot robot;

	private List<PointNode> prevActive;

	private PointNode prevMaxActive;

	private PointNode following;

	public ExperienceRoadMap(String name, String algorithm, Robot robot) {
		super(name);

		frame = null;
		repainter = null;

		intermediateGoal = new PointPort(this);
		addOutPort("intermediateGoal", intermediateGoal);

		this.algorithm = algorithm;
		this.robot = robot;

		prevMaxActive = null;
	}

	@Override
	public void run() {
		// Get info
		Float1dPort sonarReadings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort sonarAngles = (Float1dPort) getInPort("sonarAngles");
		PointPort rPos = (PointPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		PointPort platPos = (PointPort) getInPort("platformPosition");
		Bool0dPort foundPlat = (Bool0dPort) getInPort("foundPlatform");

		List<Edge> bestPath = null;
		PointNode start = null;
		List<PointNode> active = new LinkedList<PointNode>();
		synchronized (g) {
			// Compute active nodes
			float totalActivation = 0;
			float maxActivation = Float.NEGATIVE_INFINITY;
			PointNode maxActive = null;
			for (PointNode n : g.getVertices()) {
				n.updateActivation(rPos.get(), rOrient.get(), sonarReadings, sonarAngles);

				float activation = n.getActivation();
				totalActivation += activation;
				if (activation > ACTIVATION_THRS)
					active.add(n);

				if (activation > maxActivation) {
					maxActivation = activation;
					maxActive = n;
				}
			}

			// Creation of new nodes
			if ((totalActivation < MIN_ACTIVATION && maxActivation < MAX_SINGLE_ACTIVATION) || foundPlat.get()) {
				// System.out.println("Creating a node");
				// Create new node
				PointNode nv = new PointNode(rPos.get());
				g.addVertex(nv);
				// Add to the active set
				nv.updateActivation(rPos.get(), rOrient.get(), sonarReadings, sonarAngles);
				active.add(nv);
				maxActive = nv;
				System.out.println(foundPlat.get() + " " + platPos.get().distance(rPos.get()));
			}

			// Connectivity of all active nodes
			if (active.size() == 1 && prevActive != null) { // Newly created node
				PointNode node = active.get(0);
				for (PointNode pn : prevActive) {
					g.addEdge(new Edge((float) node.prefLoc.distance(pn.prefLoc)), node, pn);
				}
			} else {
				for (int i = 0; i < active.size(); i++) {
					PointNode n1 = active.get(i);
					for (int j = i + 1; j < active.size(); j++) {
						PointNode n2 = active.get(j);
						if (!g.isNeighbor(n1, n2)) {
							if (shouldConnect(n1, n2, rPos.get(), rOrient.get(), sonarReadings, sonarAngles)) {
								g.addEdge(new Edge((float) n1.prefLoc.distance(n2.prefLoc)), n1, n2);
							}
						}
					}
				}
			}

			prevActive = active;

			// Compute dijsktra
			// Get the current node
			PointNode mostActive = active.get(0);
			for (PointNode pn : active)
				if (pn.activation > mostActive.activation)
					mostActive = pn;
			// Get the goal node
			PointNode goalNode = null;
			float minDistToGoal = Float.MAX_VALUE;
			for (PointNode pn : g.getVertices()) {
				double dist = pn.prefLoc.distance(platPos.get());
				if (dist <= DIST_TO_GOAL_THRS && dist < minDistToGoal) {
					goalNode = pn;
					minDistToGoal = (float) dist;
				}
			}

			start = null;
			if (goalNode != null) {
				float minDist = Float.MAX_VALUE;
				for (PointNode n : active) {
					// Compute the shortest path
					Transformer<Edge, Float> wtTransformer = new Transformer<Edge, Float>() {
						public Float transform(Edge link) {
							return link.weight;
						}
					};
					DijkstraShortestPath<PointNode, Edge> alg = new DijkstraShortestPath(g, wtTransformer);
					List<Edge> l = alg.getPath(n, goalNode);
					Number dist = alg.getDistance(n, goalNode);
					if (dist != null && dist.floatValue() < minDist) {
						minDist = dist.floatValue();
						bestPath = l;
						start = n;
					}
				}

				// System.out.println("The shortest path from" + mostActive + "
				// to "
				// + goalNode + " is:");
				// System.out.println(l.toString());
				// System.out.println("and the length of the path is: " + dist);
			}
		}

		//

		// Publish a closer goal if there is a valid path
		if (bestPath == null || bestPath.isEmpty()) {
			intermediateGoal.set(platPos.get());
			bug.run();

		} else {
			// Point3f next = g.getEndpoints(l.get(0)).getSecond().prefLoc;
			// if (next.distance(rPos.get()) > NEXT_NODE_DIST_THRS)
			// intermediateGoal.set(next);
			// else {
			// if (l.size() > 1)
			// intermediateGoal.set(g.getEndpoints(l.get(1)).getSecond().prefLoc);
			// else
			// intermediateGoal.set(platPos.get());
			// }0
			// Get the node further into the path that is not active
			int i = 0;
			PointNode nextNode = start;
			PointNode prevNode = null;
			while (i < bestPath.size() && (active.contains(nextNode))) {
				prevNode = nextNode;

				Pair<PointNode> edge = g.getEndpoints(bestPath.get(i));
				if (edge.getFirst() == nextNode)
					nextNode = edge.getSecond();
				else
					nextNode = edge.getFirst();
				i++;
			}

			// if (prevNode.prefLoc.distance(rPos.get()) >
			// MIN_DISTANCE_TO_NEXT_NODE)
			// following = prevNode;
			// else
			following = prevNode;

			for (PointNode n : g.getVertices())
				n.following = false;

			if (i >= bestPath.size() || following == null)
				intermediateGoal.set(platPos.get());
			else {
				intermediateGoal.set(following.prefLoc);
				following.following = true;
			}

			bug0.run();
			// try {
			// Thread.sleep(10);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

	}

	/**
	 * Returns whether two segments nodes should be connected.
	 * 
	 * They should be connected if the segment connecting them is clear
	 * according to the current readings.
	 * 
	 * @param pn1
	 *            The first node
	 * @param pn2
	 *            The second node
	 * @param rPos
	 *            The robot position
	 * @param rOrient
	 *            The robot orientation
	 * @param sonarReadings
	 *            The set of sonar readings
	 * @param sonarAngles
	 *            The set of sonar angles
	 * @return
	 */
	private boolean shouldConnect(PointNode pn1, PointNode pn2, Coordinate rPos, float rOrient,
			Float1dPort sonarReadings, Float1dPort sonarAngles) {
		// If the robot is at one of the nodes, connect them (assuming the caller has both active)
		if (rPos.equals(pn1.prefLoc) || rPos.equals(pn2.prefLoc))
			return true;
		// Work in robot coordinates
		Coordinate p1 = GeomUtils.relativeCoords(pn1.prefLoc, rPos, rOrient);
		Coordinate p2 = GeomUtils.relativeCoords(pn2.prefLoc, rPos, rOrient);
		// The segment connecting both nodes
		LineSegment p1p2 = new LineSegment(p1, p2);
		// The angles to both nodes
		float angleToP1 = GeomUtils.angleToPoint(p1);
		float angleToP2 = GeomUtils.angleToPoint(p2);

		// Iterate over readings
		for (int a = 0; a < sonarAngles.getSize(); a++) {
			float angle = sonarAngles.get(a);
			// If the angle falls between the angles to both nodes
			// TODO: minor fix, take angle +/- aperture
			if (GeomUtils.angleBetweenAngles(angle, angleToP1, angleToP2)) {
				float reading = sonarReadings.get(a);
				// Get the segment representing the reading (loose estimate)
				Coordinate rayP = new Coordinate(Math.cos(angle) * reading, Math.sin(angle) * reading);
				LineSegment ray = new LineSegment(new Coordinate(), rayP);
				// If the segment doesnt intersect, it means the reading was
				// shorter than needed -> don't connect
				Coordinate intersection = ray.intersection(p1p2);
				if (intersection == null)
					return false;
			}
		}

		return true;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void newTrial() {

		g = new UndirectedSparseGraph<PointNode, Edge>();

		Layout<PointNode, Edge> layout = new VertextPosLayout<Edge>(g);
		layout.setSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));
		vv = new BasicVisualizationServer<PointNode, Edge>(layout);
		vv.setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));
		vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<PointNode, Paint>() {
			public Paint transform(PointNode pn) {
				if (pn.following)
					return new Color(0f, 1f, 0f, 1f);
				else
					return new Color(pn.activation, 0f, 1 - pn.activation, 1f);
			}

		});

		// Create the delegate bug algorithms
		Float1dPort sonarReadings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort sonarAngles = (Float1dPort) getInPort("sonarAngles");
		PointPort rPos = (PointPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		PointPort platPos = (PointPort) getInPort("platformPosition");

		// algorithm comes from group parameters
		bug = null;
		if (algorithm.equals("bug0"))
			bug = new Bug0Module("Bug0", robot);
		else if (algorithm.equals("bug1"))
			bug = new Bug1Module("Bug1", robot);
		else if (algorithm.equals("bug2"))
			bug = new Bug2Module("Bug2", robot);
		else
			throw new NotImplementedException();

		bug.addInPort("sonarReadings", sonarReadings);
		bug.addInPort("sonarAngles", sonarAngles);
		bug.addInPort("position", rPos);
		bug.addInPort("orientation", rOrient);
		bug.addInPort("platformPosition", intermediateGoal);

		// bug0 = new Bug0Module("ERMBug0", robot);
		bug0 = new APFModule("ERMBug0", robot);
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

		prevActive = null;
		prevMaxActive = null;
	}

	public UndirectedGraph<PointNode, Edge> getGraph() {
		return g;
	}

	public PointNode getFollowingNode() {
		return following;
	}

}
