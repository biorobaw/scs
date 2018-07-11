package edu.usf.ratsim.robot.robotito;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import edu.usf.experiment.universe.wall.Wall;

public class ROSWallDetector implements NodeMain {

	private static final String HOST = "cmac1";

	//protected static final double HALF_WALL = .35f;
	protected static final double HALF_WALL = 0.9144f; //size of the external walls
	protected static final double OBS_HALF_WALL = 0.1275f;
	protected static final double OBS_WIDTH = 0.039f;
	
	private static ROSWallDetector instance = null;

	private Map<Integer, Wall> walls;

	private ROSWallDetector() {

		walls = new HashMap<Integer, Wall>();

		NodeConfiguration conf = NodeConfiguration.newPublic(HOST);
		conf.setMasterUri(URI.create("http://" + HOST + ":11311"));
		ROSWallDetector node = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				DefaultNodeMainExecutor.newDefault().execute(node, conf);
			}
		}).start();

	}

	public static ROSWallDetector getInstance() {
		if (instance == null)
			instance = new ROSWallDetector();

		return instance;
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("WallDetector");
	}

	@Override
	public void onStart(ConnectedNode node) {
		Log log = node.getLog();
		Subscriber<geometry_msgs.PoseStamped> subscriber = node.newSubscriber("/walls",
				geometry_msgs.PoseStamped._TYPE);
		subscriber.addMessageListener(new MessageListener<geometry_msgs.PoseStamped>() {
			@Override
			public void onNewMessage(geometry_msgs.PoseStamped p) {
				synchronized (ROSWallDetector.this) {
					float x = (float) p.getPose().getPosition().getX();
					float y = (float) p.getPose().getPosition().getY();
					// Hack - the orientation is not really a quaternion, it encodes theta on the w component directly
					float t = (float) p.getPose().getOrientation().getW();
					
					int id = Integer.parseInt(p.getHeader().getFrameId());
					
					//TODO: Block below is only able to accept line segments. Representing obstacle walls
					//is done by creating two walls to allow depth to be considered. Note that this only works
					//when the width of the internal walls is smaller than the robot's radius.
					if(id <= 4) { //These are the "main walls" that enclose the arena; 1 wall is all that's needed
						Wall wall = new Wall((float) (-Math.cos(t) * HALF_WALL + x), (float) (-Math.sin(t) * HALF_WALL + y),
								(float) (Math.cos(t) * HALF_WALL + x), (float) (Math.sin(t) * HALF_WALL + y));
						walls.put(id, wall);
					} else { //these markers mark the obstacle walls, which have different dimensions
						id = id + (2 * id - 4); //each such id needs two walls to represent the front and back.
						
						Wall wall1 = new Wall((float) (-Math.cos(t) * OBS_HALF_WALL + x), (float) (-Math.sin(t) * OBS_HALF_WALL + y),
								(float) (Math.cos(t) * OBS_HALF_WALL + x), (float) (Math.sin(t) * OBS_HALF_WALL + y));
						Wall wall2 = new Wall((float) (-Math.cos(t) * OBS_HALF_WALL + (OBS_WIDTH * -Math.sin(t) + x)), 
											  (float) (-Math.sin(t) * OBS_HALF_WALL + (OBS_WIDTH * Math.cos(t) + y)),
											  (float) (Math.cos(t) * OBS_HALF_WALL + (OBS_WIDTH * -Math.sin(t) + x)), 
											  (float) (Math.sin(t) * OBS_HALF_WALL + (OBS_WIDTH * Math.cos(t) + y)));
						walls.put(id,  wall1);
						walls.put(id + 1, wall2);
						
					}
					
				}
			}
		});
	}

	@Override
	public void onShutdown(Node node) {
	}

	@Override
	public void onShutdownComplete(Node node) {
	}

	@Override
	public void onError(Node node, Throwable throwable) {
	}

	public static void main(String[] args) {
		new ROSWallDetector();
	}

	public synchronized Collection<Wall> getWalls(){
		return walls.values();
	}
}