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

	protected static final double HALF_WALL = .35f;

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
				float x = (float) p.getPose().getPosition().getX();
				float y = (float) p.getPose().getPosition().getY();
				float t = (float) (2 * Math.acos(p.getPose().getOrientation().getW() / 2));
				Wall wall = new Wall((float) (-Math.cos(t) * HALF_WALL + x), (float) (-Math.sin(t) * HALF_WALL + y),
						(float) (Math.cos(t) * HALF_WALL + x), (float) (Math.sin(t) * HALF_WALL + y));
				int id = Integer.parseInt(p.getHeader().getFrameId());
				walls.put(id, wall);
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

	public Collection<Wall> getWalls(){
		return walls.values();
	}
}