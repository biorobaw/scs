package edu.usf.ratsim.robot.robotito;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import com.vividsolutions.jts.geom.Coordinate;

public class ROSPoseDetector implements NodeMain {
	
	private static final String HOST = "cmac1";

	private static ROSPoseDetector instance = null;
	
	public float x, y, theta;
	public long lastPoseReceived;
	
	private ROSPoseDetector() {
		x = 0;
		y = 0;
		theta = 0;
		lastPoseReceived = System.currentTimeMillis();
		
		NodeConfiguration conf = NodeConfiguration.newPublic(HOST);
		conf.setMasterUri(URI.create("http://"+HOST+":11311"));
		ROSPoseDetector node = this;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				DefaultNodeMainExecutor.newDefault().execute(node, conf);				
			}
		}).start();
	
	}
	
	public static ROSPoseDetector getInstance(){
		if (instance == null)
			instance = new ROSPoseDetector();
		
		return instance;
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("NodoJava");
	}

	@Override
	public void onStart(ConnectedNode node) {
		Log log = node.getLog();
		Subscriber<geometry_msgs.Pose2D> subscriber = node.newSubscriber("/robotito/pose", geometry_msgs.Pose2D._TYPE);
		subscriber.addMessageListener(new MessageListener<geometry_msgs.Pose2D>() {
			@Override
			public void onNewMessage(geometry_msgs.Pose2D p) {
				x = (float) p.getX();
				y = (float) p.getY();
				theta = (float) p.getTheta();
				lastPoseReceived = System.currentTimeMillis();
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
		new ROSPoseDetector();
	}

	public Coordinate getPosition() {
		return new Coordinate(x,y);
	}

	public float getAngle() {
		return theta;
	}

}