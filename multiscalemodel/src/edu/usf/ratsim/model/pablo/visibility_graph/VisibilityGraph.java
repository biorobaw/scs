package edu.usf.ratsim.model.pablo.visibility_graph;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.vividsolutions.jts.algorithm.RobustLineIntersector;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.universe.wall.Wall;


/**
 * class for computing a visibility graph (assumes 2d) and the min distance
 * path between an origin and destiny (using euclidean distance)
 * @author bucef
 *
 */
public class VisibilityGraph {
	
	List<Wall> walls;
	Float[] origin = null;
	Float[] destiny = null;
	Graph<Float[],DefaultEdge> graph = new SimpleWeightedGraph<>(DefaultEdge.class);
	
	float precision;
	
	
	public VisibilityGraph(LinkedList<Wall> walls) {
		// TODO Auto-generated constructor stub
		this(walls,0.01f);
	}
	
	public VisibilityGraph(LinkedList<Wall> walls, float precision) {
		// TODO Auto-generated constructor stub

		//set precision:
		this.precision = precision;
		
		//need to split walls first
		Wall[] auxWalls = new Wall[walls.size()];
		for(int i=0; i<walls.size();i++) auxWalls[i] = walls.get(i);
		this.walls = splitWalls(auxWalls);
		
		
		//Add vertexes
		for(var w : walls) {
			addVertex(new Float[] {w.getX1(),w.getY1()});
			addVertex(new Float[] {w.getX2(),w.getY2()});
		}
		
		//Add wall edges
		
		
	}
	
	/**
	 * Split a set of walls into non intersecting segments (except for the vertexes)
	 * @param walls
	 * @return
	 */
	private LinkedList<Wall> splitWalls(Wall[] walls) {
		
		// result list
		var result = new LinkedList<Wall>();
		
		// priority queue of each segment 
		// each queue hold the distances from the respective start point at which intersections where found
		var intersections = new PriorityQueue[walls.length];	
		for(int i=0; i < walls.length; i++) 
			intersections[i] = new PriorityQueue<Float>();
		
		
		for(int i=0; i < walls.length;i++) {
			
			//find all intersections of segment i
			for(int j=i+1 ; j < walls.length;j++) {
				var inter = walls[i].s.intersection(walls[j].s);
				if(inter!= null) {
					intersections[i].add((float)(new LineSegment(walls[i].s.p0, inter).getLength()));
					intersections[j].add((float)(new LineSegment(walls[j].s.p0, inter).getLength()));
				}
			}
			
			//Split segment i 
			float segmentSize = (float)walls[i].s.getLength();
			intersections[i].add(segmentSize);
			
			//coordinates of the first vertex
			float originX = (float)walls[i].s.p0.x;
			float originY = (float)walls[i].s.p0.y;
			//dx and dy of the wall
			float dx	  = (float)walls[i].s.p1.x - originX;
			float dy	  = (float)walls[i].s.p1.y - originY;
			//length and coordinates of initial point
			float start = 0;
			float startX  = originX;
			float startY  = originY;
			
			//split the segment
			while(intersections[i].size()!=0) {
				float next = (float)intersections[i].poll();
				if(next-start > precision) {
					float alpha = next/segmentSize;
					float endX = originX + alpha*dx;
					float endY = originY + alpha*dy;
					result.add(new Wall(startX, startY, endX, endY));
					start = next;
					startX = endX;
					startY = endY;
				}
				
			}
			
		}
		
		return result;
	}
	
	/**
	 * Add the vertex if its not an alias of another vertex
	 * on either case, return the vertex of the graph.
	 * @param vertex
	 * @return
	 */
	private Float[] addVertex(Float[] vertex) {
		//check if vertex has already been added
		for(var w : graph.vertexSet()) {
			if (Math.abs(vertex[0]-w[0]) < precision && Math.abs(vertex[1]-w[1])<precision)
				return w;
			
		}
		//add the vertex and the respective edges
		addVertexAndEdges(vertex);
		return vertex;
	}
	
	private void addVertexAndEdges(Float[] vertex) {
		graph.addVertex(vertex);
		for(var v : graph.vertexSet()) {
			if( v == vertex) continue;
			LineSegment segment = new LineSegment(vertex[0], vertex[1], v[0], v[1]);
			if(!segmentIntersectsWall(segment)) {
				var e = graph.addEdge(vertex, v);
				graph.setEdgeWeight(e, segment.getLength());
			}
		}
	}
	
	private boolean segmentIntersectsWall(LineSegment s) {
		for(var w: walls) {
			
			//check intersection
			var intersector = new RobustLineIntersector();
			intersector.computeIntersection(w.s.p0, w.s.p1, s.p0, s.p1);
			if(intersector.isInteriorIntersection()) return true;
			
		}
		return false;
	}
	
	public void addOrigin(Float[] newOrigin) {
		graph.removeVertex(origin);
		origin = newOrigin;
		addVertexAndEdges(newOrigin);
	}
	
	public void addDestiny(Float[] newDestiny) {
		graph.removeVertex(destiny);
		destiny=newDestiny;
		addVertexAndEdges(newDestiny);
	}
	
	public GraphPath<Float[], DefaultEdge> getShortestPath() {
		if(origin == null || destiny == null) {
			System.err.println("ERROR: origin or destiny was not set in visibility graph");
			System.exit(-1);
		}
		AStarShortestPath<Float[], DefaultEdge> astar = 
				new AStarShortestPath<Float[], DefaultEdge>(graph, 
						(v1,v2) -> (new LineSegment(v1[0], v1[1], v2[0], v2[1])).getLength());
		return astar.getPath(origin, destiny);
	}
	
	public static void main(String[] args) {
		
		var walls = new LinkedList<Wall>();
		
		walls.add(new Wall(-1, -1, 1, -1));
		walls.add(new Wall(1, -1, 1, 1));
		walls.add(new Wall(1, 1, -1, 1));
		walls.add(new Wall(-1, 1, -1, -1));
		walls.add(new Wall(-1, 0, 0.5f, 0));
		walls.add(new Wall(0.3f, 0.5f, 1, 0.75f));
		
		Float origin[]  = new Float[] {-0.5f,-0.5f};
		Float destiny[] = new Float[] { 0.4f, 0.9f};
		
		var graph = new VisibilityGraph(walls);
		graph.addOrigin(origin);
		graph.addDestiny(destiny);
		
		var shortest = graph.getShortestPath();
		System.out.println("shortest: " + shortest.getWeight());
		System.out.println("edges: ");
		shortest.getVertexList().forEach(v->System.out.println("\t("+v[0] +","+v[1]+")"));
		
		
		graph.addOrigin(new Float[] {-0.6f,0.9f});
		System.out.println("new number of vertexes and edges: "+ 
							graph.graph.vertexSet().size() + " " +
							graph.graph.edgeSet().size());
		shortest = graph.getShortestPath();
		System.out.println("shortest: " + shortest.getWeight());
		System.out.println("edges: ");
		shortest.getVertexList().forEach(v->System.out.println("\t("+v[0] +","+v[1]+")"));
		
		
	}
	
}
