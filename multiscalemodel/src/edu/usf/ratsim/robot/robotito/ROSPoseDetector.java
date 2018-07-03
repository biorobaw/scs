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
	
	private Coordinate[] lastReadings = {null, null, null};
	
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
				synchronized(ROSPoseDetector.this) {
					x = (float) p.getX();
					y = (float) p.getY();
					//theta = (float) p.getTheta() + 1.57f; //TODO:Marker is not oriented with robot. Confirm adjustment accuracy
					theta = (float) p.getTheta();
					lastPoseReceived = System.currentTimeMillis();
					//System.out.println("Received Pose: " + x + " " + y);
					
					//updates the Reading filter
					lastReadings[0] = lastReadings[1];
					lastReadings[1] = lastReadings[2];
					lastReadings[2] = new Coordinate(x, y);
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
		new ROSPoseDetector();
	}

	public synchronized Coordinate getPosition() {
		return new Coordinate(x,y);
	}
	
	/*Simple filter that returns the most recent coordinate in any set of two recent readings
	 *that are within a close threshold value. This prevents movement readings and any 
	 *noise spikes from being returned, if the current position must be accurate.
	 *
	 *If no reading is returned by the fifth attempt, the threshold will be multiplied 
	 *by 1.5 after each new attempt. This ensures the method returns within a few seconds
	 *at most.
	 */
	public Coordinate getFilteredPosition() {
		float errorT = 0.010f;
		int numAttempts = 0;
		while(true) {
			//check each pair - if their differences are within the threshold, return most recent
			if(lastReadings[2] != null && Math.abs(lastReadings[2].x - lastReadings[1].x) < errorT && Math.abs(lastReadings[2].y - lastReadings[1].y) < errorT) 
				return lastReadings[2];
			if(lastReadings[1] != null && Math.abs(lastReadings[1].x - lastReadings[0].x) < errorT && Math.abs(lastReadings[1].y - lastReadings[0].y) < errorT) 
				return lastReadings[1];
			if(lastReadings[2] != null && Math.abs(lastReadings[2].x - lastReadings[0].x) < errorT && Math.abs(lastReadings[2].y - lastReadings[0].y) < errorT) 
				return lastReadings[2];
			
			//no pairs match; wait and try again.
			try { 
				Thread.sleep(150); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//threshold backoff - after three attempts, if it cannot find readings,
			//increase threshold until method can exit.
			numAttempts++;
			if(numAttempts > 4)
				errorT *= 1.5f;
			if(numAttempts > 10) {
				System.out.println("WARNING: Current pose filter threshold is now: " + errorT
						+ "\n\tConfirm that the robot isn't moving and that the pose topic is producing expected results.");
			}
		}
		
	}

	public synchronized float getAngle() {
		return theta;
	}

}