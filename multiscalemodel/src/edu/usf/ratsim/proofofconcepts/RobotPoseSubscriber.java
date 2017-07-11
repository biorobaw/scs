package edu.usf.ratsim.proofofconcepts;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;

public class RobotPoseSubscriber implements NodeMain {

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
	        log.info("I heard: (" + p.getX() + "," + p.getY() + "," + p.getTheta() + ")");
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
  
  public static void main(String[] args){
	  NodeMainExecutor exec = DefaultNodeMainExecutor.newDefault();
	  NodeConfiguration conf = NodeConfiguration.newPublic("cmac1");
	  conf.setMasterUri(URI.create("http://cmac1:11311"));
	  exec.execute(new RobotPoseSubscriber(), conf);
  }

}